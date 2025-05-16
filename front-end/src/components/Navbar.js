import React from 'react';
import {Link, useLocation, useNavigate} from 'react-router-dom';
import navbarStyles from './Navbar.module.css';
import ConnectX_icon from '../assets/ConnectX_icon.png';

const Navbar = ({ userName }) => {
    const navigate = useNavigate();
    const location = useLocation();
    const isActive = (path) => {
        // Exact match for non-nested routes
        if (['/main/jobs', '/main/chats', '/main/settings'].includes(path)) {
            return location.pathname === path;
        }
        // Contains match for nested routes or special cases
        return location.pathname.includes(path);
    };
    const handleLogout = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('userName');
        navigate('/');
    };

    const redirectToHome = () => {
        navigate('/main'); // Redirect to welcome page
    };

    return (
        <nav className={navbarStyles.navbar}>
            <div className={navbarStyles.navbarLeft}>
                <img src={ConnectX_icon} alt="Home" onClick={redirectToHome} style={{cursor: 'pointer', width: '70px', height: '50px'}}/>
                <Link to="/main" className={location.pathname === '/main' ? navbarStyles.active : ''}>Home</Link>
                <Link to="/main/network" className={isActive('/main/network') ? navbarStyles.active : ''}>Network</Link>
                <Link to="/main/jobs" className={isActive('/main/jobs') ? navbarStyles.active : ''}>Ads</Link>
                <Link to="/main/chats" className={isActive('/main/chats') ? navbarStyles.active : ''}>Chats</Link>
                <Link to="/main/notifications"
                      className={isActive('/main/notifications') ? navbarStyles.active : ''}>Notifications</Link>
                <Link to="/main/personal-info" className={isActive('/main/personal-info') ? navbarStyles.active : ''}>Personal
                    Information</Link>
                <Link to="/main/settings"
                      className={isActive('/main/settings') ? navbarStyles.active : ''}>Settings</Link>
            </div>
            <div className={navbarStyles.navbarRight}>
                <button onClick={handleLogout} className={navbarStyles.navbarLogout}>Logout</button>
            </div>
        </nav>
    );
};

export default Navbar;
