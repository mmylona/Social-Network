import React, { useState } from 'react';
import axios from 'axios';
import { TextField, Button, Switch, FormControlLabel, FormGroup, Container, Typography, Paper } from '@mui/material';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import setPageStyles from './SettingsPage.module.css';

const SettingsPage = () => {
    const [email, setEmail] = useState('');
    const [oldPassword, setOldPassword] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const [oldPasswordError, setOldPasswordError] = useState('');
    const [darkMode, setDarkMode] = useState(false);

    const theme = createTheme({
        palette: {
            mode: darkMode ? 'dark' : 'light',
        },
    });

    const handleEmailChange = (e) => setEmail(e.target.value);
    const handleOldPasswordChange = (e) => setOldPassword(e.target.value);
    const handleNewPasswordChange = (e) => setNewPassword(e.target.value);
    const handleConfirmPasswordChange = (e) => setConfirmPassword(e.target.value);

    const handleDarkModeChange = (event) => {
        setDarkMode(event.target.checked);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (newPassword !== confirmPassword) {
            setError('Passwords do not match.');
            setOldPasswordError('');
            return;
        }

        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.post('https://localhost:8087/api/user/update-credentials',
                { email, oldPassword, newPassword }, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            if (response.data.status === 'OK') {
                setMessage('Credentials updated successfully.');
                setError('');
                setOldPasswordError('');
            } else {
                setMessage('');
                setError(response.data.message);
                if (response.data.message === 'Old password is incorrect') {
                    setOldPasswordError(response.data.message);
                    setError('');
                } else {
                    setOldPasswordError('');
                }
            }
        } catch (error) {
            setError('Failed to update credentials.');
            setMessage('');
            setOldPasswordError('');
        }
    };

    return (
        <ThemeProvider theme={theme}>
            <Container component="main" maxWidth="sm">
                <Paper elevation={3} className={setPageStyles.setPageBody}>
                    <Typography component="h1" variant="h5">
                        Settings
                    </Typography>
                    <form onSubmit={handleSubmit}>
                        <TextField
                            variant="outlined"
                            margin="normal"
                            required
                            fullWidth
                            label="Email"
                            name="email"
                            autoComplete="email"
                            autoFocus
                            value={email}
                            onChange={handleEmailChange}
                        />
                        <TextField
                            variant="outlined"
                            margin="normal"
                            required
                            fullWidth
                            label="Old Password"
                            name="oldPassword"
                            type="password"
                            value={oldPassword}
                            onChange={handleOldPasswordChange}
                            error={!!oldPasswordError}
                            helperText={oldPasswordError}
                        />
                        <TextField
                            variant="outlined"
                            margin="normal"
                            required
                            fullWidth
                            label="New Password"
                            name="newPassword"
                            type="password"
                            value={newPassword}
                            onChange={handleNewPasswordChange}
                        />
                        <TextField
                            variant="outlined"
                            margin="normal"
                            required
                            fullWidth
                            label="Confirm New Password"
                            name="confirmPassword"
                            type="password"
                            value={confirmPassword}
                            onChange={handleConfirmPasswordChange}
                        />
                        {error && <Typography color="error">{error}</Typography>}
                        {message && <Typography color="primary">{message}</Typography>}
                        <FormGroup>
                            <FormControlLabel
                                control={<Switch checked={darkMode} onChange={handleDarkModeChange} />}
                                label="Dark Mode"
                            />
                        </FormGroup>
                        <Button
                            type="submit"
                            fullWidth
                            variant="contained"
                            color="primary"
                            className={setPageStyles.setPageButton}
                        >
                            Update Credentials
                        </Button>
                    </form>
                </Paper>
            </Container>
        </ThemeProvider>
    );
};

export default SettingsPage;