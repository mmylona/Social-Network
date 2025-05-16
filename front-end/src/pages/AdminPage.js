import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import styles from './AdminPage.module.css';
const AdminPage = () => {
    const [users, setUsers] = useState([]);
    const [selectedUsers, setSelectedUsers] = useState([]);
    const [selectedFields, setSelectedFields] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const token = localStorage.getItem('authToken');
                const response = await axios.get('https://localhost:8087/api/admin/users', {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setUsers(response.data);
            } catch (error) {
                console.error('Error fetching users:', error);
            }
        };

        fetchUsers();
    }, []);

    const handleViewDetails = (userId) => {
        navigate(`/admin/users/${userId}`);
    };

    const handleSelectUser = (userId) => {
        setSelectedUsers((prevSelected) =>
            prevSelected.includes(userId)
                ? prevSelected.filter((id) => id !== userId)
                : [...prevSelected, userId]
        );
    };

    const handleSelectField = (field) => {
        setSelectedFields((prevSelected) =>
            prevSelected.includes(field)
                ? prevSelected.filter((f) => f !== field)
                : [...prevSelected, field]
        );
    };

    const exportData = async (format) => {
        try {
            const token = localStorage.getItem('authToken');
            const fieldsToExport = selectedFields.length > 0 ? selectedFields : fields;
            const response = await axios.post(`https://localhost:8087/api/admin/export/${format}`, {
                users: selectedUsers,
                fields: fieldsToExport
            }, {
                headers: { Authorization: `Bearer ${token}` },
                responseType: 'blob',
            });
            const blob = new Blob([response.data], { type: `application/${format}` });
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `users.${format}`;
            a.click();
            window.URL.revokeObjectURL(url);
        } catch (error) {
            console.error(`Error exporting data to ${format}:`, error);
        }
    };

    const fields = ['firstName','lastName','email','title','bio','cv', 'posts', 'ads', 'experience', 'interests', 'comments', 'network'];

    return (
        <div className={styles.adminContainer}>
            <h2>Admin Page</h2>
            <div className={styles.selectionContainer}>
                <div className={styles.usersSection}>
                    <h3>Select Users</h3>
                    <ul>
                        {Array.isArray(users) && users.map((user) => (
                            <li key={user.id} className="list-item">
                                <input
                                    type="checkbox"
                                    checked={selectedUsers.includes(user.id)}
                                    onChange={() => handleSelectUser(user.id)}
                                />
                                {user.firstName} {user.lastName} - {user.email}
                                <button onClick={() => handleViewDetails(user.id)}>View Details</button>
                            </li>
                        ))}
                    </ul>
                </div>
                <div className={styles.fieldsSection}>
                    <h3>Select Fields</h3>
                    <ul>
                        {fields.map((field) => (
                            <li key={field} className={styles.listItem}>
                                <input
                                    type="checkbox"
                                    checked={selectedFields.includes(field)}
                                    onChange={() => handleSelectField(field)}
                                />
                                {field.toUpperCase()}
                            </li>
                        ))}
                    </ul>
                </div>
            </div>
            <div className={styles.exportButtons}>
                <button onClick={() => exportData('xml')}>Export to XML</button>
                <button onClick={() => exportData('json')}>Export to JSON</button>
                <button onClick={() => {
                    localStorage.removeItem('authToken');
                    navigate('/login');
                }}>Log out</button>
            </div>
        </div>
    );
};

export default AdminPage;
