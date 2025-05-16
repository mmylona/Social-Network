import React, { useEffect, useState, useCallback, useRef} from 'react';
import axios from 'axios';
import styles from './ChatPage.module.css';

import { useNavigate } from 'react-router-dom';
import { useParams } from 'react-router-dom';

const debounce = (func, delay) => {
    let debounceTimer;
    return function(...args) {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => func.apply(this, args), delay);
    };
};

const ChatPage = () => {

    const [conversations, setConversations] = useState([]);
    const [messages, setMessages] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const [searchResults, setSearchResults] = useState([]);
    const [selectedChat, setSelectedChat] = useState(null);
    const [newMessage, setNewMessage] = useState('');
    const [userEmail, setUserEmail] = useState('');
    const [profilePicUrl, setProfilePicUrl] = useState(''); // Add this line
    const navigate = useNavigate();
    const { id } = useParams();
    const searchBarRef = useRef(null);
    const searchResultsRef = useRef(null);

    useEffect(() => {
        fetchUserEmail();
        fetchConversations();
    }, []);
    useEffect(() => {
        const fetchProfileInfoByChatId = async (chatId) => {
            try {
                const token = localStorage.getItem('authToken');
                const response = await axios.get(`https://localhost:8087/api/network/profile-chat/${chatId}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                return response.data;
            } catch (error) {
                console.error('Error fetching profile information:', error);
                return null;
            }
        }

        const handleChatSelection = async () => {
            if (id) {
                const chat = conversations.find(convo => convo.chatId === parseInt(id));
                if (chat) {
                    handleConversationClick(chat);  // Auto-select existing chat
                } else {
                    const profile = await fetchProfileInfoByChatId(id);
                    if (profile) {
                        setSelectedChat({
                            recipientId: profile.id,
                            email: profile.email,
                            firstName: profile.firstName,
                            lastName: profile.lastName
                        });
                    }
                }
            }
        };

        handleChatSelection();
    }, [id, conversations]);

    useEffect(() => {
        const handleClickOutside = (event) => {
            if (searchBarRef.current && !searchBarRef.current.contains(event.target) &&
                searchResultsRef.current && !searchResultsRef.current.contains(event.target)) {
                setSearchQuery(''); // Clear the search query
                setSearchResults([]); // Hide the search results
            }
        };

        document.addEventListener('mousedown', handleClickOutside);
        return () => {
            document.removeEventListener('mousedown', handleClickOutside);
        };
    }, []);

    const fetchUserEmail = async () => {
        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.get('https://localhost:8087/api/user/personal-info', {
                headers: { Authorization: `Bearer ${token}` },
            });
            setUserEmail(response.data.email);
        } catch (error) {
            console.error('Failed to fetch user email:', error);
        }
    };

    const fetchConversations = async () => {
        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.get('https://localhost:8087/api/message/retrieve?latest=true', {
                headers: { Authorization: `Bearer ${token}` },
            });
            setConversations(response.data);
        } catch (error) {
            console.error('Failed to fetch conversations:', error);
        }
    };

    const fetchMessages = async (recipientEmail) => {
        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.get(`https://localhost:8087/api/message/retrieve?latest=false&recipientEmail=${recipientEmail}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setMessages(response.data);
        } catch (error) {
            console.error('Failed to fetch messages:', error);
        }
    };

    const fetchSearchResults = async (query) => {
        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.get(`https://localhost:8087/api/user/search?name=${query}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setSearchResults(response.data);
        } catch (error) {
            console.error('Failed to fetch search results:', error);
        }
    };

    const debouncedFetchSearchResults = useCallback(debounce(fetchSearchResults, 300), []);

    useEffect(() => {
        if (searchQuery) {
            debouncedFetchSearchResults(searchQuery);
        } else {
            setSearchResults([]);
        }
    }, [searchQuery, debouncedFetchSearchResults]);

    useEffect(() => {
    }, [profilePicUrl]);

    const [senderProfilePicUrl, setSenderProfilePicUrl] = useState('');
    const [recipientProfilePicUrl, setRecipientProfilePicUrl] = useState('');

    const handleConversationClick = async (conversation) => {
        const email = conversation.recipientEmail === userEmail ? conversation.senderEmail : conversation.recipientEmail;
        const firstName = conversation.recipientEmail === userEmail ? conversation.senderFirstName : conversation.recipientFirstName;
        const lastName = conversation.recipientEmail === userEmail ? conversation.senderLastName : conversation.recipientLastName;
        const newProfilePicUrl = conversation.recipientEmail === userEmail ? conversation.senderProfilePicUrl : conversation.recipientProfilePicUrl;
        const newSenderProfilePicUrl = conversation.senderProfilePicUrl;
        const newRecipientProfilePicUrl = conversation.recipientProfilePicUrl;

        setProfilePicUrl(newProfilePicUrl);
        setSenderProfilePicUrl(newSenderProfilePicUrl); // Set the sender's profile picture URL
        setRecipientProfilePicUrl(newRecipientProfilePicUrl); // Set the recipient's profile picture URL

        setProfilePicUrl(newProfilePicUrl);
        setSelectedChat({
            email,
            firstName,
            lastName,
            recipientId: conversation.recipientId
        });
        fetchMessages(email);
        navigate(`/main/chats/${conversation.chatId}`);
    };

    const handleSearchResultClick = async (user) => {
        setSelectedChat({
            email: user.email,
            firstName: user.firstName,
            lastName: user.lastName,
        });
        fetchMessages(user.email);
        setSearchQuery(''); // Clear the search query
        setSearchResults([]); // Hide the search results
        document.querySelector(`.${styles.searchBar}`).classList.add(styles.top);
    };

    const isSender = (message) => {
        return message.senderEmail === userEmail;
    };

    const groupedMessages = messages.reduce((groups, message) => {
        const date = new Date(message.timestamp).toLocaleDateString();
        const sender = isSender(message) ? 'You' : message.senderName;
        const lastGroup = groups[groups.length - 1];

        if (!lastGroup || lastGroup.date !== date || lastGroup.sender !== sender) {
            groups.push({ date, sender, messages: [message] });
        } else {
            lastGroup.messages.push(message);
        }

        return groups;
    }, []);

    const handleSendMessage = async () => {

        if (!newMessage.trim() || !selectedChat || !selectedChat.email || !selectedChat.firstName || !selectedChat.lastName) {
            console.error('Missing recipient information:', selectedChat);
            return;
        }

        try {
            const token = localStorage.getItem('authToken');

            // Fetch sender info from authentication context
            const senderInfoResponse = await axios.get('https://localhost:8087/api/user/personal-info', {
                headers: { Authorization: `Bearer ${token}` },
            });

            const senderInfo = senderInfoResponse.data;

            // Construct the message object in the required format
            const message = {
                recipientId: selectedChat.recipientId,
                senderEmail: senderInfo.email,
                senderName: `${senderInfo.firstName} ${senderInfo.lastName}`,
                recipientEmail: selectedChat.email,
                recipientName: `${selectedChat.firstName} ${selectedChat.lastName}`,
                content: newMessage,
                timestamp: new Date().toISOString()
            };


            // Send the message to the backend
            await axios.post(
                'https://localhost:8087/api/message/send',
                message,
                {
                    headers: { Authorization: `Bearer ${token}` },
                }
            );

            // Update state to reflect the sent message
            setMessages([...messages, message]);
            setNewMessage('');
            fetchConversations();
        } catch (error) {
            console.error('Failed to send message:', error);
        }
    };


    return (
        <div className={styles.chatContainer}>
            <div className={styles.sidebar}>
                <h2>Chats</h2>

                {conversations.map((conversation, index) => (
                    <div
                        key={index}
                        className={styles.conversationItem}
                        onClick={() => handleConversationClick(conversation)}
                    >
                        <img src={conversation.recipientEmail === userEmail ? conversation.senderProfilePicUrl : conversation.recipientProfilePicUrl} alt={`${conversation.senderName} ${conversation.recipientName}`}/>
                        <div>
                            <p>{conversation.recipientEmail === userEmail ? conversation.senderName : conversation.recipientName}</p>
                            <p>{conversation.content}</p>
                        </div>
                    </div>
                ))}
            </div>
            <div className={styles.mainChat}>
                <div className={styles.searchBar} ref={searchBarRef}>
                    <input
                        type="text"
                        placeholder="Search..."
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        onKeyDown={(e) => {
                            if (e.key === 'Enter' && searchResults.length > 0) handleSearchResultClick(searchResults[0]);
                        }}
                    />
                </div>
                {searchQuery && (
                    <div className={styles.searchResults} ref={searchResultsRef}>
                        {searchResults.map((result, index) => (
                            <div
                                key={index}
                                className={styles.searchResultItem}
                                onClick={() => handleSearchResultClick(result)}
                            >
                                <img src={result.profilePicUrl} alt={`${result.firstName} ${result.lastName}`}/>
                                <div>
                                    <p>{result.firstName} {result.lastName}</p>
                                    <p>{result.title}</p>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
                <div className={styles.messages}>
                    {selectedChat ? (
                        <div>
                            <h2>Chat with {selectedChat.firstName} {selectedChat.lastName}</h2>
                            {groupedMessages.length > 0 ? (
                                groupedMessages.map((group, index) => (
                                    <div key={index}>
                                        <h4 className={isSender(group.messages[0]) ? styles.senderName : styles.otherSenderName}>{group.sender}</h4>
                                        <h3 className={styles.date}>{group.date}</h3>
                                        {group.messages.map((message, index) => (
                                            <div key={index} className={`${styles.messageGroup} ${isSender(message) ? styles.messageGroupRight : styles.messageGroupLeft}`}>
                                                <div className={styles.message}>
                                                    <div className={styles.messageContent}>
                                                        <img
                                                            src={isSender(message) ? senderProfilePicUrl : recipientProfilePicUrl}
                                                            alt={isSender(message) ? 'Sender' : 'Recipient'}
                                                            style={{
                                                                width: '30px',
                                                                height: '30px',
                                                                borderRadius: '50%',
                                                                marginRight: '10px',
                                                                objectFit: 'cover'
                                                            }}
                                                        />
                                                        <p>{message.content}</p>
                                                    </div>
                                                    <span>{new Date(message.timestamp).toLocaleTimeString()}</span>
                                                </div>
                                            </div>
                                        ))}
                                    </div>
                                ))
                            ) : (
                                <p>Send your first message</p>
                            )}
                            <div className={styles.sendMessageContainer}>
                                <input
                                    type="text"
                                    placeholder="Type your message..."
                                    value={newMessage}
                                    onChange={(e) => setNewMessage(e.target.value)}
                                />
                                <button onClick={handleSendMessage}>Send</button>
                            </div>
                        </div>
                    ) : (
                        <p>Wow, such an empty page. Currently there is no conversation.</p>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ChatPage;
