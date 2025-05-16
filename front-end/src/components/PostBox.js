import React, {useState} from 'react';
import axios from 'axios';
import {TextField, Button, Box} from '@mui/material';
import postBoxStyles from './PostBox.module.css';

const PostBox = () => {
    const [content, setContent] = useState('');
    const [error, setError] = useState('');
    const [file, setFile] = useState(null);
    const [fileName, setFileName] = useState('');

    const handleFileChange = (event) => {
        const selectedFile = event.target.files[0];
        setFile(selectedFile);
        setFileName(selectedFile.name); // Update the file name state
    };

    const triggerFileInputClick = () => {
        document.getElementById('fileInput').click();
    };

    const handleSubmit = async () => {
        const token = localStorage.getItem('token');
        if (!token) {
            setError('You must be logged in to post.');
            return;
        }

        const formData = new FormData();
        formData.append('content', content);
        if (file) {
            const fileName = file.name;
            const fileExtension = fileName.slice(((fileName.lastIndexOf(".") - 1) >>> 0) + 2);
            if (fileExtension === 'mp3') {
                formData.append('voiceNote', file);
            } else if (['jpg', 'jpeg', 'png', 'gif'].includes(fileExtension)) {
                formData.append('image', file);
            } else if (fileExtension === 'mp4') {
                formData.append('video', file);
            }
        }

        try {
            await axios.post(
                'https://localhost:8087/api/posts',
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'multipart/form-data',
                    },
                }
            );
            setContent('');
            setFile(null); // Reset file
            setFileName(''); // Reset file name
            setError('');
        } catch (error) {
            console.error('Post error:', error.response.data);
            setError('An error occurred while posting.');
        }
    };

    return (
        <Box className={postBoxStyles.postBox}>
            <TextField
                fullWidth
                label="What's on your mind?"
                multiline
                rows={4}
                value={content}
                onChange={(e) => setContent(e.target.value)}
                margin="normal"
                className={postBoxStyles.postBoxInput}
            />
            <input
                id="fileInput"
                type="file"
                onChange={handleFileChange}
                style={{display: 'none'}} // Hide the file input
            />
            <Button variant="contained" color="primary" onClick={triggerFileInputClick}>
                Upload File
            </Button>
            {fileName && <p>File selected: {fileName}</p>} {/* Display selected file name */}
            <Button variant="contained" color="primary" onClick={handleSubmit}>
                Post
            </Button>
            {error && <p className="error">{error}</p>}
        </Box>
    );
};

export default PostBox;