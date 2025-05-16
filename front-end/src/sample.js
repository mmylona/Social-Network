import React, { useEffect, useState, useCallback } from 'react';
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
    const [profilePicUrl, setProfilePicUrl] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        fetchUserEmail();
        fetchConversations();
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

    const sendMessage = async () => {
        if (!selectedChat || !newMessage) return;

        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.post('https://localhost:8087/api/message/send', {
                recipientEmail: selectedChat.email,
                content: newMessage,
            }, {
                headers: { Authorization: `Bearer ${token}` },
            });

            setMessages([...messages, response.data]);
            setNewMessage('');
        } catch (error) {
            console.error('Failed to send message:', error);
        }
    };

    const handleSearchChange = (e) => {
        setSearchQuery(e.target.value);
        debounceSearch(e.target.value);
    };

    const debounceSearch = useCallback(debounce((query) => {
        if (query) {
            searchUsers(query);
        } else {
            setSearchResults([]);
        }
    }, 300), []);

    const searchUsers = async (query) => {
        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.get(`https://localhost:8087/api/user/search?query=${query}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setSearchResults(response.data);
        } catch (error) {
            console.error('Failed to search users:', error);
        }
    };

    const handleSearchResultClick = (result) => {
        setSelectedChat(result);
        setMessages([]);
        fetchMessages(result.email);
        setSearchQuery('');
        setSearchResults([]);
    };

    const handleConversationClick = (conversation) => {
        setSelectedChat(conversation);
        setMessages([]);
        fetchMessages(conversation.email);
    };

    const handleLogout = () => {
        localStorage.removeItem('authToken');
        navigate('/login');
    };

    return (
        <div className={styles.chatContainer}>
            <div className={styles.sidebar}>
                <h2>Conversations</h2>
                {conversations.map((conversation) => (
                    <div key={conversation.email} className={styles.conversationItem} onClick={() => handleConversationClick(conversation)}>
                        <img src={conversation.profilePicUrl || 'default-profile.png'} alt="Profile" />
                        <div className={styles.info}>
                            <p>{conversation.name}</p>
                            <p>{conversation.latestMessage}</p>
                        </div>
                    </div>
                ))}
            </div>

            <div className={styles.mainChat}>
                {selectedChat ? (
                    <>
                        <div className={styles.messages}>
                            {messages.map((message, index) => (
                                <div
                                    key={index}
                                    className={`${styles.message} ${message.senderEmail === userEmail ? styles.messageRight : styles.messageLeft}`}
                                >
                                    <h4>{message.senderEmail === userEmail ? 'You' : selectedChat.name}</h4>
                                    <p>{message.content}</p>
                                    <span>{new Date(message.timestamp).toLocaleString()}</span>
                                </div>
                            ))}
                        </div>
                        <div className={styles.sendMessageContainer}>
                            <input
                                type="text"
                                value={newMessage}
                                onChange={(e) => setNewMessage(e.target.value)}
                                placeholder="Type a message"
                            />
                            <button onClick={sendMessage} className={styles.sendButton}>Send</button>
                        </div>
                    </>
                ) : (
                    <div className={styles.placeholder}>
                        <p>Select a conversation to start chatting</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default ChatPage;
