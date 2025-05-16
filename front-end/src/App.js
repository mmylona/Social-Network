import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import LoginForm from './components/LoginForm';
import RegistrationForm from './components/RegistrationForm';
import MainPage from './pages/MainPage';
import AuthenticatedMainPage from './pages/AuthenticatedMainPage';
import WelcomePage from './pages/WelcomePage';
import AdminPage from './pages/AdminPage';
import './App.css';
import PersonalInformationPage from "./pages/PersonalInformationPage";
import PostPage from "./pages/PostPage";

const theme = createTheme();

const App = () => {
    const [authToken, setAuthToken] = useState(null);
    const [userName, setUserName] = useState('');
    const [userRole, setUserRole] = useState('');

    useEffect(() => {
        const token = localStorage.getItem('authToken');
        const user = localStorage.getItem('userName');
        const role = localStorage.getItem('userRole');

        if (token) {
            setAuthToken(token);
            setUserName(user);
            setUserRole(role);
        }
    }, []);

    const PrivateRoute = ({ children }) => {
        return authToken ? children : <Navigate to="/login" />;
    };

    const AdminRoute = ({ children, ...props }) => {
        return authToken && userRole.toLowerCase() === 'admin' ? React.cloneElement(children, props) : <Navigate to="/main" />;
    };

    return (
        <ThemeProvider theme={theme}>
            <Router>
                <Routes>
                    <Route path="/" element={<WelcomePage />} />
                    <Route path="/login" element={<LoginForm setAuthToken={setAuthToken} setUserName={setUserName} setUserRole={setUserRole} />} />
                    <Route path="/register" element={<RegistrationForm />} />
                    <Route path="/post/:postId" element={<PostPage />} />


                    <Route
                        path="/main/*"
                        element={
                            <PrivateRoute>
                                <AuthenticatedMainPage userName={userName} />
                            </PrivateRoute>

                        }
                    />
                    <Route path="/public-main" element={<MainPage />} />
                    <Route
                        path="/admin"
                        element={
                            <AdminRoute>
                                <AdminPage />
                            </AdminRoute>
                        }
                    />
                    <Route
                        path="/admin/users/:id"
                        element={
                            <AdminRoute>
                                <PersonalInformationPage />
                            </AdminRoute>
                        }
                    />
                </Routes>
            </Router>
        </ThemeProvider>
    );
};

export default App;
