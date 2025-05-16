// src/pages/WelcomePage.js
import React from 'react';
import { Button } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import welPageStyles from './WelcomePage.module.css';
import ConnectX_icon from '../assets/ConnectX_icon.png';

const WelcomePage = () => {
    const navigate = useNavigate();

    const redirectToWelcome = () => {
        navigate('/'); // Redirect to welcome page
    };

    return (
        <div className={welPageStyles.welPagWelcomeContainer}>
            <img src={ConnectX_icon} alt="Home" onClick={redirectToWelcome} className="cornerIcon" style={{width: '150px', height: '135px'}}/>
            <h1 className={welPageStyles.welPagWelcomeTitle}>Welcome to ConnectX</h1>
            <p className={welPageStyles.welPagWelcomeSubtitle}>Your professional network</p>
            <Button
                variant="contained"
                className={welPageStyles.welPagRegisterButton}
                onClick={() => navigate('/register')}
            >
                Register Now
            </Button>
            <Button
                variant="contained"
                className={welPageStyles.welPagLogInButton}
                onClick={() => navigate('/login')}
            >
                Log In
            </Button>
        </div>
    );
};

export default WelcomePage;
