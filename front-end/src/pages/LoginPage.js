// src/pages/LoginPage.js
import React from 'react';
import LoginForm from '../components/LoginForm';
import logPageStyles from './LoginPage.module.css';

const LoginPage = ({ onLoginSuccess }) => (
    <div className={logPageStyles.loginPage}>
        <LoginForm onLoginSuccess={onLoginSuccess} />
    </div>
);

export default LoginPage;