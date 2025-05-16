// src/pages/NotificationsPage.js
import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { Link } from 'react-router-dom';
import notificationPageStyles from './NotificationsPage.module.css';

const NotificationsPage = () => {
    const [connectionRequests, setConnectionRequests] = useState([]);
    const [userInteractions, setUserInteractions] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem('authToken');
        axios.get('https://localhost:8087/api/network/requested', {
            headers: { Authorization: `Bearer ${token}` },
        }).then(res => setConnectionRequests(res.data));
        axios.get('https://localhost:8087/api/notifications/user-interactions', {
            headers: { Authorization: `Bearer ${token}` },
        }).then(res => setUserInteractions(res.data));
    }, []);

    const handleAccept = (requestId) => {
        const token = localStorage.getItem('authToken');
        axios.post(`https://localhost:8087/api/network/connection-requests/${requestId}/ACCEPTED`, {}, {
            headers: { Authorization: `Bearer ${token}` },
        });
    };
    const handleDecline = (requestId) => {
        const token = localStorage.getItem('authToken');

        axios.post(`https://localhost:8087/api/network/connection-requests/${requestId}/REJECTED`,{}, {
            headers: { Authorization: `Bearer ${token}` },
        });
    };

    return (
        <div>
            <h1>Notifications</h1>
            <div>
                <h2>Connection Requests</h2>
                {Array.isArray(connectionRequests) && connectionRequests.length > 0 ? (
                    connectionRequests.map(request => (
                        <div key={request.id}>
                            <p>{request.name} wants to connect with you.</p>
                            <Link to={`/main/profile/${request.id}`}>View Profile</Link>
                            <button onClick={() => handleAccept(request.id)}>Accept</button>
                            <button onClick={() => handleDecline(request.id)}>Decline</button>
                        </div>
                    ))
                ) : (
                    <p>Currently there are no new Connection Requests </p>
                )}
            </div>

            <div className={notificationPageStyles.ntfUserInteractions}>
                <h2>User Interactions</h2>
                {Array.isArray(userInteractions) && userInteractions.length > 0 ? (
                    userInteractions.map(interaction => (
                        <div key={interaction.id} className={notificationPageStyles.ntfUserInteractionsItem}>
                            {/*<p>{interaction.userName}</p>*/}
                            {interaction.reaction?.reactionType && (
                                <p>{`${interaction.userName} reacted to your post with: ${interaction.reaction.reactionType}`}</p>
                            )}
                            {interaction.comment && (
                                <p>{`${interaction.userName} commented on your post: "${interaction.comment.content}"`}</p>
                            )}
                            <Link to={`/post/${interaction.postId}`} className={notificationPageStyles.viewPostButton}>View Post</Link>
                        </div>
                    ))
                ) : (
                    <p>Currently there are no new Notifications</p>
                )}
            </div>
        </div>
    );
};

export default NotificationsPage;