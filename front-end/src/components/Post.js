import React from 'react';
import { FaThumbsUp, FaHeart, FaStar } from 'react-icons/fa';
import { Link } from 'react-router-dom';
import authMainStyle from '../pages/AuthenticatedMainPage.module.css';

const Post = ({ post, handleReaction, handleComment, countReactions, userReaction }) => {
    console.log("Post content:", post.content);
    console.log("Post id:", post.authorId);

    return (
        <div className={authMainStyle.authMainPost}>
            {/* Use Link to navigate to the profile page when author name is clicked */}
            <Link to={`/main/profile/${post.authorId}`}>
                <h2>{post.authorName}</h2>
            </Link>
            <p className={authMainStyle.dateStyle}>Post created on: {new Date(post.createdDate).toLocaleDateString('it-IT')}</p>
            <p>{post.content}</p>
            {post.imageUrl && (
                <img src={post.imageUrl} alt="Post image" style={{maxWidth: '100%', height: 'auto'}}/>
            )}

            {post.videoUrl && (
                <video controls style={{maxWidth: '100%', height: 'auto'}}>
                    <source src={post.videoUrl} type="video/mp4"/>
                    Your browser does not support the video tag.
                </video>
            )}

            {post.voiceNoteUrl && (
                <audio controls style={{width: '100%'}}>
                    <source src={post.voiceNoteUrl} type="audio/mpeg"/>
                    Your browser does not support the audio element.
                </audio>
            )}

            <div className={authMainStyle.authMainPostActions}>
                <button
                    onClick={() => handleReaction(post.id, "LIKE")}
                    className={userReaction === "LIKE" ? authMainStyle.activeReaction : ""}
                >
                    <FaThumbsUp /> Like ({countReactions(post.reactions, 'LIKE')})
                </button>
                <button
                    onClick={() => handleReaction(post.id, "LOVE")}
                    className={userReaction === "LOVE" ? authMainStyle.activeReaction : ""}
                >
                    <FaHeart /> Love ({countReactions(post.reactions, 'LOVE')})
                </button>
                <button
                    onClick={() => handleReaction(post.id, "CARE")}
                    className={userReaction === "CARE" ? authMainStyle.activeReaction : ""}
                >
                    <FaStar /> Care ({countReactions(post.reactions, 'CARE')})
                </button>
            </div>

            <div className={authMainStyle.authMainComments}>
                {post.comments && post.comments.map(comment => (
                    <div key={comment.id} className={authMainStyle.authMainComment}>
                        <strong>{comment.authorName}</strong>
                        <p>{comment.content}</p>
                        <small>{new Date(comment.createdDate).toLocaleDateString()}</small>
                    </div>
                ))}

                <textarea
                    placeholder="Add a comment..."
                    onKeyDown={(e) => {
                        if (e.key === 'Enter') {
                            handleComment(post.id, e.target.value);
                            e.target.value = '';
                        }
                    }}
                />
            </div>
        </div>
    );
};

export default Post;
