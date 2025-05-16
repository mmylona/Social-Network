import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import netWPageStyles from'./NetworkPage.module.css';

const NetworkPage = () => {
    const [connections, setConnections] = useState([]);
    const [searchResults, setSearchResults] = useState([]);
    const [searchQuery, setSearchQuery] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        const fetchConnections = async () => {
            try {
                const token = localStorage.getItem('authToken');
                const response = await axios.get('https://localhost:8087/api/network/connections', {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setConnections(response.data);
            } catch (error) {
                console.error('Error fetching connections:', error);
            }
        };

        fetchConnections();
    }, []);

    const handleViewProfile = (userId) => {
        if (userId) {
            navigate(`/main/profile/${userId}`);
        }
    };

    const handleSearch = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.get(`https://localhost:8087/api/network/search?query=${searchQuery}`, {
                headers: { Authorization: `Bearer ${token}` },
            });
            setSearchResults(response.data);
        } catch (error) {
            console.error('Error searching professionals:', error);
        }
    };
    useEffect(() => {
        const fetchSearchResults = async () => {
            try {
                const token = localStorage.getItem('authToken');
                const response = await axios.get(`https://localhost:8087/api/network/search?query=${searchQuery}`, {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setSearchResults(response.data);
            } catch (error) {
                console.error('Error searching professionals:', error);
            }
        };

        if (searchQuery) {
            fetchSearchResults();
        } else {
            setSearchResults([]);
        }
    }, [searchQuery]);
    const handleStartConversation = (userId) => {
        navigate(`/messages/${userId}`);
    };

    return (
        <div className={netWPageStyles.networkContainer}>
            <h2>My Network</h2>
            <div className={netWPageStyles.netSearchBox}>
                <input
                    type="text"
                    placeholder="Search professionals..."
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                />
                <button onClick={handleSearch}>Search</button>
            </div>

            <ul className={netWPageStyles.netSearchResults}>
                {searchResults.map((result) => (
                    <li key={result.id} onClick={() => handleViewProfile(result.id)}>
                        {result.name} - {result.title} at {result.company}
                    </li>
                ))}
            </ul>
            <div className={netWPageStyles.netConnectionsGrid}>
                {connections.map((connection) => (
                    <div key={connection.id} className={netWPageStyles.netConnectionCard} onClick={() => handleViewProfile(connection.id)}>
                        <img src={connection.profilePic} alt={`${connection.name}'s profile`} />
                        <div className={netWPageStyles.netConnectionInfo}>
                            <h3>{connection.name}</h3>
                            <p>{connection.title}</p>
                            <p>{connection.company}</p>
                            {connection.isConnected && <button onClick={(e) => { e.stopPropagation(); handleStartConversation(connection.id); }}>Start Conversation</button>}
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default NetworkPage;
