import React, { useState } from 'react';
import { useFormik } from 'formik';
import * as yup from 'yup';
import axios from 'axios';
import { TextField, Button, Alert, Grid, Fade } from '@mui/material';
import regStyles from './RegistrationForm.module.css';
import { useNavigate } from 'react-router-dom';
import ConnectX_icon from '../assets/ConnectX_icon.png';


const validationSchema = yup.object({
    firstName: yup.string('Enter your first name').required('First name is required'),
    lastName: yup.string('Enter your last name').required('Last name is required'),
    email: yup.string('Enter your email').email('Enter a valid email').required('Email is required'),
    password: yup.string('Enter your password').required('Password is required'),
    confirmPassword: yup.string('Confirm your password')
        .oneOf([yup.ref('password'), null], 'Passwords must match')
        .required('Password confirmation is required'),
    phoneNumber: yup.string()
        .matches(/^[0-9]+$/, "Must be only digits")
        .min(10, 'Must be exactly 10 digits')
        .max(10, 'Must be exactly 10 digits'),
});

const RegistrationForm = () => {
    const [error, setError] = useState(null);
    const [profilePicture, setProfilePicture] = useState(null);
    const navigate = useNavigate();

    const formik = useFormik({
        initialValues: {
            firstName: '',
            lastName: '',
            email: '',
            password: '',
            confirmPassword: '',
            phoneNumber: '',
        },
        validationSchema: validationSchema,
        onSubmit: async (values, { setSubmitting, setErrors }) => {
            setError(null); // Reset error state
            const formData = new FormData();
            formData.append('firstName', values.firstName);
            formData.append('lastName', values.lastName);
            formData.append('email', values.email);
            formData.append('password', values.password);
            formData.append('phoneNumber', values.phoneNumber);
            if (profilePicture) {
                formData.append('profilePicture', profilePicture);
            }

            try {
                const response = await axios.post('https://localhost:8087/api/user/register', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });
                localStorage.setItem('token', response.data.token);
                alert('Registration successful');
            } catch (error) {
                console.error('Registration error:', error.response ? error.response.data : error);
                if (error.response && error.response.status === 303) {
                    setErrors({ email: 'Email Already in Use' });
                    setError('Email Already in Use');
                } else {
                    setErrors({ email: 'An error occurred during registration' });
                    setError('An error occurred during registration');
                }
            } finally {
                setSubmitting(false);
            }
        },
    });

    const redirectToWelcome = () => {
        navigate('/'); // Redirect to welcome page
    };

    return (
        <Fade in={true} timeout={500} className={regStyles.registrationForm}>
            <div>
                <img src={ConnectX_icon} alt="Home" onClick={redirectToWelcome} style={{ cursor: 'pointer', width: '120px', height: '90px'}} />
                <form onSubmit={formik.handleSubmit} className={regStyles.registrationForm} encType="multipart/form-data">
                    {error && <Alert severity="error">{error}</Alert>}
                    <Grid container spacing={2}>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                id="firstName"
                                name="firstName"
                                label="First Name"
                                value={formik.values.firstName}
                                onChange={formik.handleChange}
                                error={formik.touched.firstName && Boolean(formik.errors.firstName)}
                                helperText={formik.touched.firstName && formik.errors.firstName}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                fullWidth
                                id="lastName"
                                name="lastName"
                                label="Last Name"
                                value={formik.values.lastName}
                                onChange={formik.handleChange}
                                error={formik.touched.lastName && Boolean(formik.errors.lastName)}
                                helperText={formik.touched.lastName && formik.errors.lastName}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                id="email"
                                name="email"
                                label="Email"
                                value={formik.values.email}
                                onChange={formik.handleChange}
                                error={formik.touched.email && Boolean(formik.errors.email)}
                                helperText={formik.touched.email && formik.errors.email}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                id="password"
                                name="password"
                                label="Password"
                                type="password"
                                value={formik.values.password}
                                onChange={formik.handleChange}
                                error={formik.touched.password && Boolean(formik.errors.password)}
                                helperText={formik.touched.password && formik.errors.password}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                id="confirmPassword"
                                name="confirmPassword"
                                label="Confirm Password"
                                type="password"
                                value={formik.values.confirmPassword}
                                onChange={formik.handleChange}
                                error={formik.touched.confirmPassword && Boolean(formik.errors.confirmPassword)}
                                helperText={formik.touched.confirmPassword && formik.errors.confirmPassword}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                fullWidth
                                id="phoneNumber"
                                name="phoneNumber"
                                label="Phone Number"
                                value={formik.values.phoneNumber}
                                onChange={formik.handleChange}
                                error={formik.touched.phoneNumber && Boolean(formik.errors.phoneNumber)}
                                helperText={formik.touched.phoneNumber && formik.errors.phoneNumber}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <input
                                accept="image/*"
                                id="profilePicture"
                                name="profilePicture"
                                type="file"
                                onChange={(event) => {
                                    setProfilePicture(event.currentTarget.files[0]);
                                }}
                            />
                            <label htmlFor="profilePicture">
                                <h5>Upload Profile Image</h5>
                            </label>
                        </Grid>
                    </Grid>
                    <Button color="primary" variant="contained" fullWidth type="submit" disabled={formik.isSubmitting} className={regStyles.regButton}>
                        Register
                    </Button>
                </form>
            </div>
        </Fade>
    );
};

export default RegistrationForm;