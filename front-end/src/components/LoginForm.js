import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';
import {TextField, Button, Container, Typography, Box} from '@mui/material';
import logInStyles from './LoginForm.module.css';
import ConnectX_icon from '../assets/ConnectX_icon.png'; // Import your icon

const LoginForm = ({ setAuthToken, setUserName,setUserRole  }) => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await axios.post('https://localhost:8087/api/user/authenticate', { email, password });
            const token = response.data.token;
            const user = response.data.userName;
            localStorage.setItem('authToken', token);
            localStorage.setItem('userName', user);
            localStorage.setItem('email',  response.data.email);
            localStorage.setItem("userRole", response.data.userRole.roleName);
            setAuthToken(token);
            setUserName(user);
            setUserRole(response.data.userRole.roleName);
            if (response.data.userRole.roleName === 'ADMIN') {
                navigate('/admin');
            } else {
                navigate('/main');
            }
        } catch (error) {
            setError('Login failed. Please check your credentials and try again.');
        }
    };

    const redirectToWelcome = () => {
        navigate('/');
    };

    return (
        <Container className={logInStyles.logInformContainer}>
            <img src={ConnectX_icon} alt="Home" onClick={redirectToWelcome}
                 style={{cursor: 'pointer', width: '100px', height: '75px', marginBottom: '0.00001px'}}/>
            <Typography variant="h4" align="center" gutterBottom>Login</Typography>
            <form onSubmit={handleSubmit}>
                <TextField
                    label="Email"
                    type="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    fullWidth
                    margin="normal"
                />
                <TextField
                    label="Password"
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    fullWidth
                    margin="normal"
                />
                <Box display="flex" justifyContent="center" mt={2}>
                    <Button type="submit" variant="contained" color="primary" className={logInStyles.logInButton}>
                        Login
                    </Button>
                </Box>
                {error && <Typography color="error">{error}</Typography>}
            </form>
        </Container>
    );
};

export default LoginForm;
