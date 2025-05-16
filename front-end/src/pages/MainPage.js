// src/pages/MainPage.js
import React from 'react';
import Sidebar from '../components/Sidebar';
import Timeline from '../components/Timeline';
import PostBox from '../components/PostBox';

const MainPage = () => {
    return (
        <div className="main-container">
            <div className="top-bar">
                <div className="tab">Home</div>
                <div className="tab">Network</div>
                <div className="tab">Ads</div>
                <div className="tab">Discussions</div>
                <div className="tab">Notifications</div>
                <div className="tab">Personal Information</div>
                <div className="tab">Settings</div>
            </div>
            <div className="sidebar-and-content">
                <Sidebar />
                <div className="content">
                    <Timeline />
                    <PostBox />
                </div>
            </div>
        </div>
    );
};

export default MainPage;
