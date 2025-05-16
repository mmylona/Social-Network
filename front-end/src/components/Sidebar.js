// src/components/Sidebar.js
import React from 'react';
import  sideStyles from './Sidebar.module.css';

const Sidebar = () => {
    return (
        <div className={sideStyles.sidebar}>
            <h2>Profile</h2>
            <ul>
                <li>Connections</li>
                <li>Messages</li>
                <li>Groups</li>
                <li>Events</li>
                <li>Saved Posts</li>
            </ul>
        </div>
    );
};

export default Sidebar;
