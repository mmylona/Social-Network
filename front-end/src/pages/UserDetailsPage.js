import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import userDetailStyles from './UserDetailsPage.module.css';

const UserDetailsPage = () => {
    const { id } = useParams();
    const [user, setUser] = useState(null);

    useEffect(() => {
        axios.get(`/api/admin/users/${id}`)
            .then(response => {
                setUser(response.data);
            })
            .catch(error => {
                console.error('Error fetching user details:', error);
            });
    }, [id]);

    if (!user) {
        return <p>Loading...</p>;
    }

    return (
        <div className={userDetailStyles.uDetUserDetailsPage}>
            <h1>User Details</h1>
            <p><strong>ID:</strong> {user.id}</p>
            <p><strong>First Name:</strong> {user.firstName}</p>
            <p><strong>Last Name:</strong> {user.lastName}</p>
            <p><strong>Email:</strong> {user.email}</p>

        </div>
    );
};

export default UserDetailsPage;