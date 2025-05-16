import React, {useState, useEffect} from 'react';
import axios from 'axios';
import {useParams} from 'react-router-dom';
import  perPageStyles from './PersonalInformationPage.module.css';
import {
    Container,
    Typography,
    TextField,
    Button,
    Box,
    Checkbox,
    FormControlLabel,
    Paper,
    Divider,
    FormControl,
    InputLabel,
    Select,
    MenuItem, DialogContentText, DialogContent, DialogTitle, Dialog, DialogActions
} from '@mui/material';
import {
    Card,
    CardContent,
    CardMedia
} from '@mui/material';
import {SoftSkills, CommunicationSkills, TechnicalSkills, ProgrammingLanguage} from '../components/SkillsEnum';

const PersonalInformationPage = () => {
    const [personalInfo, setPersonalInfo] = useState({
        firstName: '',
        lastName: '',
        bio: '',
        title: '',
        location: '',
        connection: '',
        backgroundPicUrl: '',
        workExperiences: [],
        skills: [],
        educations: [],
        isWorkExperiencePublic: false,
        isSkillPublic: false,
        isEducationPublic: false,
        connections: []
    });
    const [showConnections, setShowConnections] = useState(false);

    const [softSkills, setSoftSkills] = useState([]);
    const [communicationSkills, setCommunicationSkills] = useState([]);
    const [technicalSkills, setTechnicalSkills] = useState([]);
    const [programmingLanguages, setProgrammingLanguages] = useState([]);
    const [isEditing, setIsEditing] = useState(false);
    const [selectedFile, setSelectedFile] = useState(null);
    const [initialPersonalInfo, setInitialPersonalInfo] = useState({});
    const {id} = useParams();
    const [openDialog, setOpenDialog] = React.useState(false);
    const [selectedConnectionId, setSelectedConnectionId] = React.useState(null);
    const [cvFile, setCvFile] = useState(null);
    const [cvUrl, setCvUrl] = useState(null);

    const handleClickOpen = (connectionId) => {
        setSelectedConnectionId(connectionId);
        setOpenDialog(true);
    };

    const handleClose = () => {
        setOpenDialog(false);
    };
    const confirmRemoveConnection = async () => {
        if (selectedConnectionId) {
            await handleRemoveConnection(selectedConnectionId);
            handleClose();
        } else {
            console.error('No connection ID selected');
        }
    };
    useEffect(() => {
        let isMounted = true;

        const fetchPersonalInfo = async () => {
            try {
                const token = localStorage.getItem('authToken');
                const url = id
                    ? `https://localhost:8087/api/admin/users/${id}`
                    : 'https://localhost:8087/api/user/personal-info';

                const response = await axios.get(url, {
                    headers: {Authorization: `Bearer ${token}`},
                });
                if (isMounted) {
                    const skills = response.data.skills || {};
                    console.log("skills:", skills);
                    setPersonalInfo(prevInfo => ({
                        ...prevInfo,
                        ...response.data,
                        isWorkExperiencePublic: response.data.isWorkExperiencePublic,
                        isSkillPublic: response.data.isSkillPublic,
                        isEducationPublic: response.data.isEducationPublic
                    }));
                    setInitialPersonalInfo({
                        workExperiences: response.data.workExperiences || [],
                        educations: response.data.educations || [],
                    });
                    // Update skills state
                    setSoftSkills(skills.softSkills || []);
                    setCommunicationSkills(skills.communicationSkills || []);
                    setTechnicalSkills(skills.technicalSkills || []);
                    setProgrammingLanguages(skills.programmingLanguages || []);
                }
            } catch (error) {
                console.error('Error fetching personal information:', error);
            }
        };

        fetchPersonalInfo().catch(error => console.error('Error in fetchPersonalInfo:', error));
        fetchCvUrl();
        return () => {
            isMounted = false;
        };
    }, [id]);
    const fetchCvUrl = async () => {
        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.get(`https://localhost:8087/api/user/cv`, {
                headers: { Authorization: `Bearer ${token}` },
                responseType: 'blob'
            });
            if (response.data.type === "application/pdf" || response.data.type.startsWith('image/')) {
                const blob = new Blob([response.data], { type: response.data.type });
                const url = window.URL.createObjectURL(blob);
                setCvUrl(url);
            } else {
                console.error('CV format not supported for inline display:', response.data.type);
            }
        } catch (error) {
            console.error('Error fetching CV:', error);
        }
    };
    const handleChange = (e) => {
        const {name, type, checked} = e.target;
        const value = type === 'checkbox' ? checked : e.target.value;
        setPersonalInfo((prevInfo) => ({
            ...prevInfo,
            [name]: value,
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('authToken');
            const formData = new FormData();

            // Ensure work experiences and educations are arrays before filtering
            const workExperiences = Array.isArray(personalInfo.workExperiences) ? personalInfo.workExperiences : [];
            const initialWorkExperiences = Array.isArray(initialPersonalInfo.workExperiences) ? initialPersonalInfo.workExperiences : [];
            const educations = Array.isArray(personalInfo.educations) ? personalInfo.educations : [];
            const initialEducations = Array.isArray(initialPersonalInfo.educations) ? initialPersonalInfo.educations : [];

            // Determine changes in work experiences and educations
            const updatedWorkExperiences = workExperiences.filter((exp, index) => {
                return JSON.stringify(exp) !== JSON.stringify(initialWorkExperiences[index]);
            });

            const updatedEducations = educations.filter((edu, index) => {
                return JSON.stringify(edu) !== JSON.stringify(initialEducations[index]);
            });

            // Prepare skills and other data as before
            const skillsDto = {
                softSkills: [...softSkills],
                communicationSkills: [...communicationSkills],
                technicalSkills: [...technicalSkills],
                programmingLanguages: [...programmingLanguages]
            };

            formData.append('skills', JSON.stringify(skillsDto));

            // Append only updated work experiences and educations
            const personalInfoToUpdate = {
                ...personalInfo,
                skills: skillsDto,
                workExperiences: updatedWorkExperiences,
                educations: updatedEducations
            };

            const personalInfoBlob = new Blob([JSON.stringify(personalInfoToUpdate)], {type: 'application/json'});
            formData.append('personalInfo', personalInfoBlob);

            if (selectedFile) {
                formData.append('image', selectedFile);
            }

            if(cvFile) {
                formData.append('cv', cvFile);
            }

            await axios.put(
                'https://localhost:8087/api/user/personal-info',
                formData,
                {
                    headers: {
                        Authorization: `Bearer ${token}`,
                        'Content-Type': 'multipart/form-data'
                    }
                }
            );

            alert('Personal information updated successfully.');
        } catch (error) {
            console.error('Error updating personal information:', error);
        }
    };

    const handleModify = () => {
        setIsEditing(!isEditing);
    };

    const handleAddWorkExperience = () => {
        setPersonalInfo(prevInfo => ({
            ...prevInfo,
            workExperiences: [
                ...(prevInfo.workExperiences || []), // Ensure it's always an array
                {
                    title: '',
                    description: '',
                    company: '',
                    location: '',
                    startDate: '',
                    endDate: '',
                    years: ''
                }
            ]
        }));
    };
    const handleRemoveConnection = async (userId) => {

        try {
            const token = localStorage.getItem('authToken');
            await axios.delete(`https://localhost:8087/api/network/remove-connection/${userId}`, {
                headers: { Authorization: `Bearer ${token}` }
            })

            setPersonalInfo(prevInfo => ({
                ...prevInfo,
                connections: prevInfo.connections.filter(conn => conn.id !== userId)
            }));
        } catch (error) {
            console.error('Error removing connection:', error);
        }
    };
    const handleAddEducation = () => {
        // Ensure educations is an array
        const educationsArray = Array.isArray(personalInfo.educations) ? personalInfo.educations : [];
        setPersonalInfo(prevInfo => ({
            ...prevInfo,
            educations: [...educationsArray, {
                schoolName: '',
                degree: '',
                fieldOfStudy: '',
                startDate: '',
                endDate: '',
                grade: '',
                activitiesAndSocieties: '',
                description: ''
            }]
        }));
    };

    const handleWorkExperienceChange = (e, index) => {
        const {name, value} = e.target;
        const updatedWorkExperiences = [...personalInfo.workExperiences];
        updatedWorkExperiences[index] = {...updatedWorkExperiences[index], [name]: value};
        setPersonalInfo(prevInfo => ({
            ...prevInfo,
            workExperiences: updatedWorkExperiences
        }));
    };

    const handleFileChange = (e) => {
        setSelectedFile(e.target.files[0]);
    };

    const handleSkillChange = (skillType, event) => {
        const value = event.target.value; // Directly use the value for Material-UI Select with multiple
        switch (skillType) {
            case 'softSkills':
                setSoftSkills(value);
                break;
            case 'communicationSkills':
                setCommunicationSkills(value);
                break;
            case 'technicalSkills':
                setTechnicalSkills(value);
                break;
            case 'programmingLanguages':
                setProgrammingLanguages(value);
                break;
            default:
                console.error('Invalid skill type');
        }
    };

    const handleEducationChange = (e, index) => {
        const {name, value} = e.target;
        const updatedEducations = [...personalInfo.educations];
        updatedEducations[index] = {...updatedEducations[index], [name]: value};
        setPersonalInfo(prevInfo => ({
            ...prevInfo,
            educations: updatedEducations
        }));
    };
    const toggleConnections = () => setShowConnections(!showConnections);

    return (
        <Container className={perPageStyles.personalInformationPage}>
            <Typography variant="h4" gutterBottom>
                Personal Information
            </Typography>
            <Dialog open={openDialog} onClose={handleClose}>
                <DialogTitle>Confirm Remove Connection</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Are you sure you want to remove this connection?
                    </DialogContentText>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={confirmRemoveConnection} color="primary">
                        Confirm
                    </Button>
                </DialogActions>
            </Dialog>
            <Paper elevation={3} sx={{padding: 3}}>
                <form onSubmit={handleSubmit}>
                    <TextField

                        fullWidth
                        label="First Name"
                        name="firstName"
                        value={personalInfo.firstName}
                        onChange={handleChange}
                        margin="normal"
                        disabled={!isEditing}
                    />
                    <TextField
                        fullWidth
                        label="Last Name"
                        name="lastName"
                        value={personalInfo.lastName}
                        onChange={handleChange}
                        margin="normal"
                        disabled={!isEditing}
                    />
                    <TextField
                        fullWidth
                        label="Bio"
                        name="bio"
                        value={personalInfo.bio}
                        onChange={handleChange}
                        multiline
                        rows={4}
                        margin="normal"
                        disabled={!isEditing}
                    />
                    <TextField
                        fullWidth
                        label="Title"
                        name="title"
                        value={personalInfo.title}
                        onChange={handleChange}
                        margin="normal"
                        disabled={!isEditing}
                    />
                    <TextField
                        fullWidth
                        label="Location"
                        name="location"
                        value={personalInfo.location}
                        onChange={handleChange}
                        margin="normal"
                        disabled={!isEditing}
                    />
                    <div style={{display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '20px'}}>

                        <div>
                            <h2>Connections</h2>
                            {personalInfo && (
                                <>
                                    <p>You
                                        have {personalInfo.connections ? personalInfo.connections.length : 0} connections</p>
                                    <button type="button" onClick={toggleConnections}>
                                        {showConnections ? 'Hide Connections' : 'View Connections'}
                                    </button>
                                    {showConnections && (
                                        <div style={{
                                            display: 'flex',
                                            flexWrap: 'wrap',
                                            gap: '20px',
                                            justifyContent: 'center'
                                        }}>
                                            {personalInfo.connections.map((connection) => (
                                                <Card key={connection.id} sx={{maxWidth: 345}}>
                                                    <CardMedia
                                                        component="img"
                                                        height="140"
                                                        image={connection.profilePic}
                                                        alt={connection.name}
                                                    />
                                                    <CardContent>
                                                        <Typography gutterBottom variant="h5" component="div">
                                                            {connection.name}
                                                        </Typography>
                                                        <Typography variant="body2" color="text.secondary">
                                                            {connection.title} at {connection.company}
                                                        </Typography>
                                                        <Button size="small" color="primary"
                                                                onClick={() => handleClickOpen(connection.id)}>
                                                            Remove Connection
                                                        </Button>
                                                    </CardContent>
                                                </Card>
                                            ))}
                                        </div>
                                    )}
                                </>
                            )}
                        </div>
                        <div className={perPageStyles.imageContainer}>
                            {personalInfo.backgroundPicUrl && (
                                <img
                                    src={personalInfo.backgroundPicUrl}
                                    alt="Background"
                                    className={perPageStyles.backgroundImage}
                                />
                            )}
                            {isEditing && (
                                <label className={perPageStyles.changeIcon}>
                                    <input type="file" onChange={handleFileChange} style={{display: 'none'}}/>
                                    <span className="material-icons">edit</span>
                                </label>
                            )}
                        </div>
                        <div style={{display: 'flex', flexDirection: 'column', alignItems: 'center', gap: '20px'}}>
                            {cvUrl && (
                                <a
                                    href={cvUrl}
                                    target="_blank"
                                    rel="noopener noreferrer"
                                    className={perPageStyles.cvLink} // Assuming your CSS module is imported as `styles`
                                >
                                    Open CV
                                </a>
                            )}
                            <input
                                type="file"
                                id="cvFileInput"
                                style={{display: 'none'}}
                                onChange={(e) => setCvFile(e.target.files[0])}
                            />
                            <Button
                                variant="contained"
                                color="primary"
                                onClick={() => document.getElementById('cvFileInput').click()}
                                disabled={!isEditing}
                            >
                                Upload a New CV
                            </Button>
                        </div>
                    </div>

                    <Divider sx={{my: 2}}/>
                    {/* Work Experiences */}
                    {personalInfo.workExperiences && personalInfo.workExperiences.length > 0 ? (
                        personalInfo.workExperiences.map((experience, index) => (
                            <Box key={index} mb={2}>
                                <TextField
                                    fullWidth
                                    label="Title"
                                    name="title"
                                    value={experience.title}
                                    onChange={(e) => handleWorkExperienceChange(e, index)}
                                    margin="normal"
                                    disabled={!isEditing}
                                />
                                <TextField
                                    fullWidth
                                    label="Description"
                                    name="description"
                                    value={experience.description}
                                    onChange={(e) => handleWorkExperienceChange(e, index)}
                                    margin="normal"
                                    disabled={!isEditing}
                                />
                                <TextField
                                    fullWidth
                                    label="Company"
                                    name="company"
                                    value={experience.company}
                                    onChange={(e) => handleWorkExperienceChange(e, index)}
                                    margin="normal"
                                    disabled={!isEditing}
                                />
                                <TextField
                                    fullWidth
                                    label="Location"
                                    name="location"
                                    value={experience.location}
                                    onChange={(e) => handleWorkExperienceChange(e, index)}
                                    margin="normal"
                                    disabled={!isEditing}
                                />
                                <TextField
                                    fullWidth
                                    label="Start Date"
                                    name="startDate"
                                    value={experience.startDate}
                                    onChange={(e) => handleWorkExperienceChange(e, index)}
                                    margin="normal"
                                    disabled={!isEditing}
                                />
                                <TextField
                                    fullWidth
                                    label="End Date"
                                    name="endDate"
                                    value={experience.endDate}
                                    onChange={(e) => handleWorkExperienceChange(e, index)}
                                    margin="normal"
                                    disabled={!isEditing}
                                />
                            </Box>
                        ))
                    ) : (
                        <Typography variant="body2">No work experiences added.</Typography>
                    )}
                    {isEditing && (
                        <Button variant="contained" color="primary" onClick={handleAddWorkExperience}>
                            Add Work Experience
                        </Button>
                    )}
                    <Divider sx={{my: 2}}/>
                    {/* Skills */}
                    <Typography variant="h6">Skills</Typography>
                    <FormControl fullWidth margin="normal">
                        <InputLabel>Soft Skills</InputLabel>
                        <Select
                            multiple
                            value={softSkills}
                            onChange={(e) => handleSkillChange('softSkills', e)}
                            renderValue={(selected) => selected.join(', ')}
                        >
                            {Object.keys(SoftSkills).map((key) => (
                                <MenuItem key={key} value={SoftSkills[key]}>
                                    {SoftSkills[key]}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <FormControl fullWidth margin="normal">
                        <InputLabel>Communication Skills</InputLabel>
                        <Select
                            multiple
                            value={communicationSkills}
                            onChange={(e) => handleSkillChange('communicationSkills', e)}
                            renderValue={(selected) => selected.join(', ')}
                        >
                            {Object.keys(CommunicationSkills).map((key) => (
                                <MenuItem key={key} value={CommunicationSkills[key]}>
                                    {CommunicationSkills[key]}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <FormControl fullWidth margin="normal">
                        <InputLabel>Technical Skills</InputLabel>
                        <Select
                            multiple
                            value={technicalSkills}
                            onChange={(e) => handleSkillChange('technicalSkills', e)}
                            renderValue={(selected) => selected.join(', ')}
                        >
                            {Object.keys(TechnicalSkills).map((key) => (
                                <MenuItem key={key} value={TechnicalSkills[key]}>
                                    {TechnicalSkills[key]}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <FormControl fullWidth margin="normal">
                        <InputLabel>Programming Languages</InputLabel>
                        <Select
                            multiple
                            value={programmingLanguages}
                            onChange={(e) => handleSkillChange('programmingLanguages', e)}
                            renderValue={(selected) => selected.join(', ')}
                        >
                            {Object.keys(ProgrammingLanguage).map((key) => (
                                <MenuItem key={key} value={ProgrammingLanguage[key]}>
                                    {ProgrammingLanguage[key]}
                                </MenuItem>
                            ))}
                        </Select>
                    </FormControl>
                    <Divider sx={{my: 2}}/>
                    {/* Educations */}
                    {personalInfo.educations && personalInfo.educations.length > 0 ? (
                        personalInfo.educations.map((education, index) => (
                            <Box key={index} mb={2}>
                                <TextField
                                    fullWidth
                                    label="School Name"
                                    name="schoolName"
                                    value={education.schoolName}
                                    onChange={(e) => handleEducationChange(e, index)}
                                    margin="normal"
                                    disabled={!isEditing}
                                />
                                <TextField
                                    fullWidth
                                    label="Degree"
                                    name="degree"
                                    value={education.degree}
                                    onChange={(e) => handleEducationChange(e, index)}
                                    margin="normal"
                                    disabled={!isEditing}
                                />
                                <TextField
                                    fullWidth
                                    label="Field of Study"
                                    name="fieldOfStudy"
                                    value={education.fieldOfStudy}
                                    onChange={(e) => handleEducationChange(e, index)}
                                    margin="normal"
                                    disabled={!isEditing}
                                />
                                <TextField
                                    fullWidth
                                    label="Start Date"
                                    name="startDate"
                                    value={education.startDate}
                                    onChange={(e) => handleEducationChange(e, index)}
                                    margin="normal"
                                    disabled={!isEditing}
                                />
                                <TextField
                                    fullWidth
                                    label="End Date"
                                    name="endDate"
                                    value={education.endDate}
                                    onChange={(e) => handleEducationChange(e, index)}
                                    margin="normal"
                                    disabled={!isEditing}
                                />
                            </Box>
                        ))
                    ) : (
                        <Typography variant="body2">No educations added.</Typography>
                    )}
                    {isEditing && (
                        <Button variant="contained" color="primary" onClick={handleAddEducation}>
                            Add Education
                        </Button>
                    )}
                    <Divider sx={{my: 2}}/>
                    {/* Public Visibility Options */}
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={personalInfo.isWorkExperiencePublic}
                                onChange={handleChange}
                                name="isWorkExperiencePublic"
                                disabled={!isEditing}
                            />
                        }
                        label="Work Experience Public"
                    />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={personalInfo.isSkillPublic}
                                onChange={handleChange}
                                name="isSkillPublic"
                                disabled={!isEditing}
                            />
                        }
                        label="Skill Public"
                    />
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={personalInfo.isEducationPublic}
                                onChange={handleChange}
                                name="isEducationPublic"
                                disabled={!isEditing}
                            />
                        }
                        label="Education Public"
                    />
                    {isEditing ? (
                        <Box>
                            <Button variant="contained" color="primary" type="submit">
                                Save
                            </Button>
                            <Button variant="contained" color="secondary" onClick={() => setIsEditing(false)}>
                                Cancel
                            </Button>
                        </Box>
                    ) : (
                        <Button variant="contained" color="primary" onClick={handleModify} type="button">
                            Edit
                        </Button>
                    )}
                </form>
            </Paper>
        </Container>
    );
};

export default PersonalInformationPage;