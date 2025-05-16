import axios from 'axios';

const API_BASE_URL = 'https://localhost:8087/api/post';

const PostService = {
    getPostsByUserAndNetwork: (page, size) => {
        const token = localStorage.getItem('authToken');
        return axios.get(`${API_BASE_URL}/posts?page=${page}&size=${size}`, {
            headers: {Authorization: `Bearer ${token}`}
        });
    },

    createPost: (postDto, file) => {
        const token = localStorage.getItem('authToken');
        const formData = new FormData();

        // Append the file to formData. The key should match the backend's expected key.
        if (file) {
            formData.append(file.type.startsWith('video/') ? 'video' : file.type.startsWith('image/') ? 'image' : 'voiceNote', file);
        }

        // Append the postDto as a JSON string
        formData.append('formData', new Blob([JSON.stringify(postDto)], {type: "application/json"}));

        return axios.post(`${API_BASE_URL}/create`, formData, {
            headers: {
                Authorization: `Bearer ${token}`,
            }
        });
    },


    updatePost: (postDto) => {
        const token = localStorage.getItem('authToken');
        return axios.post(`${API_BASE_URL}/update`, postDto, {
            headers: {Authorization: `Bearer ${token}`}
        });
    },

    deletePost: (postId) => {
        const token = localStorage.getItem('authToken');
        return axios.delete(`${API_BASE_URL}/delete/${postId}`, {
            headers: {Authorization: `Bearer ${token}`}
        });
    },

    commentOnPost: (postId, commentDto) => {
        const token = localStorage.getItem('authToken');
        return axios.post(`${API_BASE_URL}/comment/${postId}`, commentDto, {
            headers: {Authorization: `Bearer ${token}`}
        });
    },
    fetchPost: (postId) => {
        const token = localStorage.getItem('authToken');
        return axios.get(`${API_BASE_URL}/${postId}`, {
            headers: {Authorization: `Bearer ${token}`},
        });
    },
    handleReaction: async (postId, reaction, currentPage, pageSize, setPosts) => {
        // Optimistically update the state first
        setPosts(prevPosts => prevPosts.map(post =>
            post.id === postId
                ? {...post, reactions: [...post.reactions, reaction]}
                : post
        ));

        try {
            // Send the request to add the reaction and wait for the response
            const response = await PostService.addReaction(postId, reaction);


            // Once the response is received, update the state with the actual data from the server
            if (response.data) {
                PostService.getPostsByUserAndNetwork(currentPage, pageSize).then(response => {
                    if (response.data && response.data.postDto) {
                        setPosts(response.data.postDto);
                    }
                }).catch(error => {
                    console.error("Error fetching posts:", error);
                });
            }
        } catch (error) {
            console.error("Error adding reaction:", error);
            // Revert the state back to its previous value if an error occurs
            setPosts(prevPosts => prevPosts);
        }
    },
    addReaction: (postId, reaction) => {
        const token = localStorage.getItem('authToken');
        return axios.post(`${API_BASE_URL}/reaction/${postId}`, reaction, {
            headers: {
                Authorization: `Bearer ${token}`,
                'Content-Type': 'application/json'
            }
        });
    }
};

export default PostService;
