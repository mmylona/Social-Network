import React, { useEffect, useState } from 'react';
import authMainStyle from './AuthenticatedMainPage.module.css';

import Navbar from '../components/Navbar';
import { Route, Routes } from "react-router-dom";
import PersonalInformationPage from "./PersonalInformationPage";
import ChatPage from "./ChatPage";
import NetworkPage from "./NetworkPage";
import ProfilePage from "./ProfilePage";
import NotificationsPage from "./NotificationsPage";
import PostService from '../service/PostService';
import { FaThumbsUp, FaHeart, FaStar } from 'react-icons/fa';
import SettingsPage from "./SettingsPage";
import AdvertisementsPage from "./AdvertisementsPage";
import Post from "../components/Post";

const AuthenticatedMainPage = ({ userName }) => {
    const [posts, setPosts] = useState([]);
    const [newPost, setNewPost] = useState("");
    const [key, setKey] = useState(Math.random());
    const [file, setFile] = useState(null);
    const [fileName, setFileName] = useState("");
    const [uploadProgress, setUploadProgress] = useState(0);
    const [showTooltip, setShowTooltip] = useState(false);

    const isDisabled = !newPost.trim() && !file;
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(12);
    const [totalPosts, setTotalPosts] = useState(0);
    const [totalPages, setTotalPages] = useState(0);

    useEffect(() => {
        PostService.getPostsByUserAndNetwork(currentPage, pageSize).then(response => {
            if (response.data && response.data.postDto) {
                setPosts(response.data.postDto);
                setTotalPosts(response.data.totalPosts);
                setTotalPages(response.data.totalPages);
            }
        }).catch(error => {
            console.error("Error fetching posts:", error);
        });
    }, [userName, currentPage, pageSize]);

    const handleMouseEnter = () => {
        if (isDisabled) {
            setShowTooltip(true);
        }
    };

    const handleMouseLeave = () => {
        setShowTooltip(false);
    };

    const handleNextPage = () => {
        setCurrentPage(currentPage + 1);
    };

    const handlePreviousPage = () => {
        setCurrentPage(currentPage - 1);
    };

    const handleNewPost = () => {
        if (!newPost.trim() && !file) {
            console.error("Cannot create post: No content or file provided.");
            return;
        }

        const postDto = {
            authorName: userName,
            content: newPost
        };

        PostService.createPost(postDto, file, {
            onUploadProgress: progressEvent => {
                const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
                setUploadProgress(percentCompleted);
            }
        }).then(response => {
            if (response.data) {
                setPosts([response.data, ...posts]);
                setNewPost("");
                setFile(null);
                setFileName("");
                setUploadProgress(0);
                PostService.getPostsByUserAndNetwork(currentPage, pageSize).then(response => {
                    if (response.data && response.data.postDto) {
                        setPosts(response.data.postDto);
                    }
                }).catch(error => {
                    console.error("Error fetching posts:", error);
                });
            }
        }).catch(error => {
            console.error("Error creating post:", error);
        });
    };

    const handleComment = (postId, comment) => {
        PostService.commentOnPost(postId, { authorName: userName, content: comment }).then(response => {
            if (response.data) {
                PostService.getPostsByUserAndNetwork(currentPage, pageSize).then(response => {
                    if (response.data && response.data.postDto) {
                        setPosts(response.data.postDto);
                    }
                }).catch(error => {
                    console.error("Error fetching posts:", error);
                });
            }
        }).catch(error => {
            console.error("Error adding comment:", error);
        });
    };

    const handleReaction = (postId, reaction) => {
        PostService.handleReaction(postId, reaction, currentPage, pageSize, setPosts);
    };

    const countReactions = (reactions = [], type) => {
        return reactions.filter(reaction => reaction.reactionType === type).length;
    };

    const triggerFileSelectPopup = () => document.getElementById('fileInput').click();

    const handleFileChange = (event) => {
        const selectedFile = event.target.files[0];

        if (selectedFile) {
            setFile(selectedFile);
            setFileName(selectedFile.name);
        }
    };

    const handleFileRemove = () => {
        setFile(null);
        setFileName("");
        setUploadProgress(0);
    };

    return (
        <div className={authMainStyle.authenticatedMainPage}>
            <Navbar className={authMainStyle.authMainnavbar} userName={userName} />
            <div className={authMainStyle.authMaincontent}>
                <Routes>
                    <Route path="/" element={
                        <>
                            <h1 className={authMainStyle.authMainWelcomeMessage}>Welcome, {userName}!</h1>
                            {posts.length === 0 && (
                                <p>This is the main page of the networking app, where you can connect with others and share your professional experiences.</p>
                            )}
                            <div className={authMainStyle.authMainPostInput}>
                                <textarea
                                    value={newPost}
                                    onChange={(e) => setNewPost(e.target.value)}
                                    placeholder="What's on your mind?"
                                />
                                <input
                                    id="fileInput"
                                    type="file"
                                    style={{ display: 'none' }}
                                    onChange={handleFileChange}
                                />
                                <button onClick={triggerFileSelectPopup}>Upload File</button>
                                {fileName && (
                                    <div>
                                        <p>Attached files:</p>
                                        <p>{fileName} <button onClick={handleFileRemove} className={authMainStyle.removeFileButton}>X</button></p>
                                        <div className={authMainStyle.progressBar}>
                                            <div
                                                className={`${authMainStyle.progressBarInner} ${uploadProgress === 100 ? authMainStyle.complete : ''}`}
                                                style={{ width: `${uploadProgress}%` }}
                                            ></div>
                                        </div>
                                    </div>
                                )}
                                <button
                                    onClick={handleNewPost}
                                    disabled={isDisabled}
                                    className={isDisabled ? authMainStyle.authMainButtonDisabled : ''}
                                    onMouseEnter={handleMouseEnter}
                                    onMouseLeave={handleMouseLeave}
                                >
                                    Post
                                </button>
                                {showTooltip && isDisabled &&
                                    <div className="tooltip">You need to add text or a file to post.</div>}
                            </div>
                            <div className={authMainStyle.authMainPosts} key={key}>
                                {posts.map(post => (
                                    <Post
                                        key={post.id}
                                        post={post}
                                        handleReaction={handleReaction}
                                        handleComment={handleComment}
                                        countReactions={countReactions}
                                    />
                                ))}
                                {totalPages > 1 && (
                                    <div className="pagination-controls">
                                        <button onClick={() => setCurrentPage(0)} disabled={currentPage === 0}>First</button>
                                        <button onClick={handlePreviousPage} disabled={currentPage === 0}>Previous</button>
                                        <span>Page {currentPage + 1} of {totalPages}</span>
                                        <button onClick={handleNextPage} disabled={currentPage >= totalPages - 1}>Next</button>
                                        <button onClick={() => setCurrentPage(totalPages - 1)} disabled={currentPage >= totalPages - 1}>Last</button>
                                    </div>
                                )}
                            </div>
                        </>
                    } />
                    <Route path="personal-info" element={<PersonalInformationPage />} />
                    <Route path="/chats" element={<ChatPage />} />
                    <Route path="/chats/:id" element={<ChatPage />} />
                    <Route path="network" element={<NetworkPage />} />
                    <Route path="/profile/:userId" element={<ProfilePage />} />
                    <Route path="/main/profile/:userId" element={<ProfilePage />} />
                    <Route path="/notifications" element={<NotificationsPage />} />
                    <Route path="*" element={<h1>Not Found</h1>} />
                    <Route path="/settings" element={<SettingsPage />} />
                    <Route path="/jobs" element={<AdvertisementsPage />} />
                </Routes>
            </div>
        </div>
    );
};

export default AuthenticatedMainPage;