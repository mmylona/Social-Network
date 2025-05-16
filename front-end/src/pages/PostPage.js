import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import Post from "../components/Post";
import PostService from "../service/PostService";
import Navbar from '../components/Navbar';
import styles from './PostPage.module.css';

const PostPage = ({ userName }) => {
    const { postId } = useParams();
    const [post, setPost] = useState(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);

    useEffect(() => {
        const fetchPost = async () => {
            try {
                const response = await PostService.fetchPost(postId);
                if (response.data && response.data.postDto) {
                    setPost(response.data.postDto[0]);
                }
            } catch (error) {
                console.error('Failed to fetch post:', error);
                setError('Failed to fetch post. Please try again later.');
            } finally {
                setLoading(false);
            }
        };

        fetchPost();
    }, [postId]);

    const countReactions = (reactions = [], type) => {
        return reactions.filter(reaction => reaction.reactionType === type).length;
    };

    const handleComment = (postId, comment) => {
        PostService.commentOnPost(postId, { authorName: userName, content: comment }).then(response => {
            if (response.data) {
                PostService.fetchPost(postId).then(response => {
                    if (response.data && response.data.postDto) {
                        setPost(response.data.postDto[0]);
                    }
                }).catch(error => {
                    console.error("Error fetching post:", error);
                });
            }
        }).catch(error => {
            console.error("Error adding comment:", error);
        });
    };

    const handleReaction = (postId, reaction) => {
        PostService.handleReaction(postId, reaction, setPost);
    };

    if (loading) {
        return <div>Loading...</div>;
    }

    if (error) {
        return <div>{error}</div>;
    }

    if (!post) {
        return <div>No post found.</div>;
    }


    return (
        <div className={styles.postPageContainer}>
            <Navbar userName={userName} />
            <div className={styles.postPageContent}>
                <Post
                    post={post}
                    handleReaction={handleReaction}
                    handleComment={handleComment}
                    countReactions={countReactions}
                />
            </div>
        </div>
    );
};

export default PostPage;