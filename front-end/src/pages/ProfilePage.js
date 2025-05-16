import React, {useState, useEffect} from 'react';
import axios from 'axios';
import {useParams, useNavigate} from 'react-router-dom';
import profPageStyles from './ProfilePage.module.css';
import IconButton from '@material-ui/core/IconButton';
import ChatBubbleOutlineIcon from '@material-ui/icons/ChatBubbleOutline';


const ProfilePage = () => {
    const {userId} = useParams();
    const [chatId, setChatId] = useState(null)
    const [profile, setProfile] = useState(null);
    const [connectionStatus, setConnectionStatus] = useState('');
    const navigate = useNavigate();
    const [showNetwork, setShowNetwork] = useState(false);

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const token = localStorage.getItem('authToken');
                const response = await axios.get(`https://localhost:8087/api/network/profile/${userId}`, {
                    headers: {Authorization: `Bearer ${token}`},
                });
                setProfile(response.data);
                const responseChat = await axios.post(`https://localhost:8087/api/message/createChat/${userId}`, {}, {
                    headers: {Authorization: `Bearer ${token}`},
                });

                setChatId(responseChat.data);


                setConnectionStatus(response.data.connectionStatus);
            } catch (error) {
                console.error('Error fetching profile:', error);
            }
        };

        fetchProfile();
    }, [chatId]);
    const handleChatClick = () => {
        navigate(`/main/chats/${chatId}`);
    };
    const toggleNetworkVisibility = () => {
        setShowNetwork(!showNetwork);
    };
    const handleConnect = async () => {
        try {
            const token = localStorage.getItem('authToken');
            await axios.post(`https://localhost:8087/api/network/connect/${userId}`, {}, {
                headers: {Authorization: `Bearer ${token}`}
            });
            setConnectionStatus('PENDING');
        } catch (error) {
            console.error('Error sending connection request:', error);
        }
    };

    if (!profile) {
        return <div>Loading...</div>;
    }
    const handleViewProfile = (userId) => {
        if (userId) {
            navigate(`/main/profile/${userId}`);
        }
    };

    return (
        <div className={profPageStyles.proPageProfileContainer}>
            <div className={profPageStyles.proPageprofileHeader}>
                <img className={profPageStyles.proPageprofilePic} src={profile.profilePic}
                     alt={`${profile.name}'s profile`}/>
                <div className={profPageStyles.proPageProfileInfo}>
                    <h2>{profile.name}</h2>
                    <p>{profile.title} at {profile.company}</p>
                    {localStorage.getItem("email") !== profile.email && connectionStatus !== 'CONNECTED' && (
                        <button onClick={handleConnect} disabled={connectionStatus === 'PENDING'}>
                            {connectionStatus === 'PENDING' ? 'Request Sent' : 'Connect'}
                        </button>
                    )}
                    <IconButton color="primary" onClick={handleChatClick}>
                        <ChatBubbleOutlineIcon/>
                    </IconButton>
                </div>
            </div>
            {(!profile.workExperiences && !profile.skills && !profile.educations) ? (
                <p>Sorry all the information about that user is private</p>
            ) : (
                <div className={profPageStyles.proPageProfileDetails}>
                    {profile.workExperiences && profile.workExperiences.map((experience, index) => (
                        <div key={index}>
                            <h3>{experience.title}</h3>
                            <p>{experience.description}</p>
                            <p>{experience.company}</p>
                            <p>{experience.location}</p>
                            <p>From: {new Date(experience.startDate).toLocaleDateString()} To: {new Date(experience.endDate).toLocaleDateString()}</p>
                            <p>Years: {experience.years}</p>
                        </div>
                    ))}
                    {profile.skills && (
                        <>
                            {profile.skills.softSkills && profile.skills.softSkills.length > 0 && (
                                <div>
                                    <p>Soft Skills: {profile.skills.softSkills.join(', ')}</p>
                                </div>
                            )}
                            {profile.skills.communicationSkills && profile.skills.communicationSkills.length > 0 && (
                                <div>
                                    <p>Communications Skill: {profile.skills.communicationSkills.join(', ')}</p>
                                </div>
                            )}
                            {profile.skills.technicalSkills && profile.skills.technicalSkills.length > 0 && (
                                <div>
                                    <p>Technical Skill: {profile.skills.technicalSkills.join(', ')}</p>
                                </div>
                            )}
                            {profile.skills.programmingLanguages && profile.skills.programmingLanguages.length > 0 && (
                                <div>
                                    <p>Programming Languages: {profile.skills.programmingLanguages.join(', ')}</p>
                                </div>
                            )}
                        </>
                    )}
                    {profile.educations && profile.educations.map((education, index) => (
                        <div key={index}>
                            <h3>{education.schoolName}</h3>
                            <p>Degree: {education.degree}</p>
                            <p>Field Of Study: {education.fieldOfStudy}</p>
                            <p>From: {education.startDate} To: {education.endDate}</p>
                            <p>Grade: {education.grade}</p>
                            <p>Activities And Societies: {education.activitiesAndSocieties}</p>
                            <p>Description: {education.description}</p>
                        </div>
                    ))}

                    <button onClick={toggleNetworkVisibility} className={profPageStyles.proPageProfileInfoButton}>View
                        Network
                    </button>
                    {showNetwork && profile?.network && (
                        <div>
                            <h3>Network</h3>
                            <ul>
                                {Array.from(profile.network).map((prNet, index) => (
                                    <li key={index}>
                                        {prNet.userName}
                                        <button onClick={() => handleViewProfile(prNet.id)}>View Profile</button>
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}
                </div>
            )}
        </div>
    );
};

export default ProfilePage;