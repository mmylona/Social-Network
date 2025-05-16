import React, {useEffect, useRef, useState} from 'react';
import axios from 'axios';
import adStyles from './AdvertisementsPage.module.css';
import ApplicantCard from "../components/ApplicantCard";
import { CommunicationSkills, TechnicalSkills, SoftSkills, ProgrammingLanguage } from '../components/SkillsEnum'

const AdvertisementsPage = () => {
    const [showForm, setShowForm] = useState(false);
    const [jobAds, setJobAds] = useState([]);
    const [applicationsStatus, setApplicationsStatus] = useState({});
    const [isCreator, setIsCreator] = useState({});
    const [applicants, setApplicants] = useState({});
    const [showApplicantsModal, setShowApplicantsModal] = useState(false);
    const [currentJobAdId, setCurrentJobAdId] = useState(null);
    const [title, setTitle] = useState('');
    const [description, setDescription] = useState('');
    const [company, setCompany] = useState('');
    const [location, setLocation] = useState('');
    const [type, setType] = useState('');
    const [level, setLevel] = useState('');
    const [isRemote, setIsRemote] = useState(false);
    const [communicationSkills, setCommunicationSkills] = useState([]);
    const [technicalSkills, setTechnicalSkills] = useState([]);
    const [softSkills, setSoftSkills] = useState([]);
    const [programmingLanguages, setProgrammingLanguages] = useState([]);
    const [createdTime, setCreatedTime] = useState(null);
    const viewedAds = useRef(new Set());

    useEffect(() => {
        fetchJobAds();
    }, []);

    const handleSkillChange = (setter) => (event) => {
        const { options } = event.target;
        const value = [];
        for (let i = 0, l = options.length; i < l; i++) {
            if (options[i].selected) {
                value.push(options[i].value);
            }
        }
        setter(value);
    };
    const handleApply = async (jobAdId) => {
        try {
            const token = localStorage.getItem('authToken');
            // Replace the URL with your actual endpoint for applying to a job
            const response = await axios.post(`https://localhost:8087/api/job/apply/${jobAdId}`, {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            });
            // Handle response or update UI accordingly
            alert('Application successful!');
            // Refresh or update application status
            checkApplicationStatus(jobAdId);
        } catch (error) {
            console.error('Error applying to job:', error);
            alert('Application failed.');
        }
    };
    const fetchJobAds = async () => {
        try {
            const response = await axios.get('https://localhost:8087/api/job/all', {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('authToken')}`,
                }
            });

            // Ensure uniqueness based on the job ad's `id` in the response data
            const uniqueAds = response.data.filter((ad, index, self) =>
                index === self.findIndex((t) => t.id === ad.id)
            );

            // Set the job ads to state after ensuring they're unique
            setJobAds(uniqueAds);

            // Check application status and creator status for each ad
            uniqueAds.forEach(ad => {
                checkApplicationStatus(ad.id);
                checkIsCreator(ad.id);
            });
        } catch (error) {
            console.error('Error fetching job ads:', error);
        }
    };


    const checkApplicationStatus = async (jobAdId) => {
        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.get(`https://localhost:8087/api/job/has-applied/${jobAdId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            });
            setApplicationsStatus(prevStatus => ({...prevStatus, [jobAdId]: response.data}));
        } catch (error) {
            console.error('Error checking application status:', error);
        }
    };

    const checkIsCreator = async (jobAdId) => {
        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.get(`https://localhost:8087/api/job/is-creator/${jobAdId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            });
            setIsCreator(prevState => ({ ...prevState, [jobAdId]: response.data }));
        } catch (error) {
            console.error('Error checking creator status:', error);
        }
    };

    const fetchApplicants = async (jobAdId) => {
        try {
            const token = localStorage.getItem('authToken');
            const response = await axios.get(`https://localhost:8087/api/job/applicants/${jobAdId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            });
            setApplicants({ ...applicants, [jobAdId]: response.data });
            setShowApplicantsModal(true);
            setCurrentJobAdId(jobAdId);
        } catch (error) {
            console.error('Error fetching applicants:', error);
        }
    };

    const handleInputChange = (setter) => (event) => {
        setter(event.target.value);
    };

    const handleCheckboxChange = (event) => {
        setIsRemote(event.target.checked);
        console.log('Remote:', event.target.checked);
    };

    const handleSubmit = async (event) => {
        event.preventDefault();

        // Adjusted to directly use skill values
        const skillDto = {
            softSkills: softSkills,
            communicationSkills: communicationSkills,
            technicalSkills: technicalSkills,
            programmingLanguages: programmingLanguages
        };

        const newAd = {
            title,
            description,
            company,
            location,
            type,
            level,
            isRemote,
            skills: skillDto
        };
        console.log('Job ad created, isRemote:', isRemote);


        try {
            await axios.post('https://localhost:8087/api/job/create', newAd, {
                headers: {
                    Authorization: `Bearer ${localStorage.getItem('authToken')}`,
                    'Content-Type': 'application/json',
                }
            });
            // Reset form fields and hide form
            setTitle('');
            setDescription('');
            setCompany('');
            setLocation('');
            setType('');
            setLevel('');
            setIsRemote(false);
            setCommunicationSkills([]);
            setTechnicalSkills([]);
            setSoftSkills([]);
            setProgrammingLanguages([]);
            setShowForm(false); // Hide form after submission
            setCreatedTime(new Date());
            // Refresh job ads to include the newly created ad
            fetchJobAds();
        } catch (error) {
            console.error("Error posting ad:", error);
        }
    };

    const trackAdView = async (adId) => {
        console.log(`Tracking view for ad ${adId}`);

        if (viewedAds.current.has(adId)) {
            console.log(`Ad ${adId} has already been viewed.`);
            return;  // Prevent multiple views of the same ad from being tracked
        }

        try {
            const token = localStorage.getItem('authToken');
            await axios.post(`https://localhost:8087/api/job/track-view/${adId}`, {}, {
                headers: {
                    Authorization: `Bearer ${token}`,
                    'Content-Type': 'application/json',
                }
            });
            console.log(`Ad ${adId} view tracked successfully.`);
            viewedAds.current.add(adId);  // Mark this ad as viewed
        } catch (error) {
            console.error('Error tracking ad view:', error);
        }
    };

    const handleDelete = async (jobAdId) => {
        try {
            const token = localStorage.getItem('authToken');
            await axios.delete(`https://localhost:8087/api/job/delete/${jobAdId}`, {
                headers: {
                    Authorization: `Bearer ${token}`,
                }
            });
            // Remove the deleted ad from the jobAds state
            setJobAds(prevAds => prevAds.filter(ad => ad.id !== jobAdId));
            console.log(`Job ad ${jobAdId} deleted successfully.`);
        } catch (error) {
            console.error('Error deleting job ad:', error);
        }
    };
    return (
        <div className={adStyles.adsPage}>
            {!showForm && (
                <button className={adStyles.createJobBtn} onClick={() => setShowForm(true)}>Create Job</button>
            )}
            <div className="job-ads-container">
                {jobAds.map((ad, index) => (
                    <div
                        key={ad.id}
                        className={adStyles.jobAdCard}
                        data-ad-id={ad.id}
                        onMouseEnter={() => trackAdView(ad.id)} // Track view on mouse hover
                    >
                        <h3>{ad.title}</h3>
                        <p>{ad.description}</p>
                        <p><strong>Company:</strong> {ad.company}</p>
                        <p><strong>Location:</strong> {ad.location}</p>
                        <p><strong>Type:</strong> {ad.type}</p>
                        <p><strong>Level:</strong> {ad.level}</p>
                        <p><strong>Remote:</strong> {ad.isRemote ? 'Yes' : 'No'}</p>
                        {!isCreator[ad.id] && !applicationsStatus[ad.id] && (
                            <button onClick={() => handleApply(ad.id)}>Apply</button>
                        )}

                        {applicationsStatus[ad.id] && (
                            <p>Already Applied</p>
                        )}
                        {isCreator[ad.id] && (
                            <>
                                <button onClick={() => fetchApplicants(ad.id)}>View Applicants</button>
                                <button
                                    className={adStyles.deleteJobBtn}
                                    onClick={() => handleDelete(ad.id)}
                                >
                                    Delete
                                </button>
                            </>
                        )}
                    </div>
                ))}
            </div>
            {showForm && (
                    <div className={adStyles.advModal}>
                    <div className={adStyles.advModalContent}>
                        <span className={adStyles.advClose} onClick={() => setShowForm(false)}>&times;</span>
                        <form onSubmit={handleSubmit} className={adStyles.adForm}>
                            <input type="text" placeholder="Title" value={title} onChange={handleInputChange(setTitle)} />
                            <textarea placeholder="Description" value={description} onChange={handleInputChange(setDescription)} />
                            <input type="text" placeholder="Company" value={company} onChange={handleInputChange(setCompany)} />
                            <input type="text" placeholder="Location" value={location} onChange={handleInputChange(setLocation)} />
                            <select value={type} onChange={handleInputChange(setType)}>
                                <option value="">Select Type</option>
                                <option value="FULL_TIME">Full-time</option>
                                <option value="PART_TIME">Part-time</option>
                                <option value="CONTRACT">Contract</option>
                                <option value="TEMPORARY">Temporary</option>
                                <option value="INTERNSHIP">Internship</option>
                                <option value="REMOTE">Remote</option>
                            </select>
                            <select value={level} onChange={handleInputChange(setLevel)}>
                                <option value="">Select Level</option>
                                <option value="JUNIOR">Junior</option>
                                <option value="MID_LEVEL">Mid-level</option>
                                <option value="SENIOR">Senior</option>
                                <option value="DIRECTOR">Director</option>
                                <option value="EXECUTIVE">Executive</option>
                            </select>
                            <label>
                                Remote:
                                <input type="checkbox" checked={isRemote} onChange={handleCheckboxChange} />
                            </label>
                            {/* Skill selection sections */}
                            <div>
                                <h4>Communication Skills</h4>
                                <select multiple value={communicationSkills} onChange={handleSkillChange(setCommunicationSkills)}>
                                    {Object.values(CommunicationSkills).map(skill => (
                                        <option key={skill} value={skill}>{skill}</option>
                                    ))}
                                </select>
                            </div>
                            <div>
                                <h4>Technical Skills</h4>
                                <select multiple value={technicalSkills} onChange={handleSkillChange(setTechnicalSkills)}>
                                    {Object.values(TechnicalSkills).map(skill => (
                                        <option key={skill} value={skill}>{skill}</option>
                                    ))}
                                </select>
                            </div>
                            <div>
                                <h4>Soft Skills</h4>
                                <select multiple value={softSkills} onChange={handleSkillChange(setSoftSkills)}>
                                    {Object.values(SoftSkills).map(skill => (
                                        <option key={skill} value={skill}>{skill}</option>
                                    ))}
                                </select>
                            </div>
                            <div>
                                <h4>Programming Languages</h4>
                                <select multiple value={programmingLanguages} onChange={handleSkillChange(setProgrammingLanguages)}>
                                    {Object.values(ProgrammingLanguage).map(language => (
                                        <option key={language} value={language}>{language}</option>
                                    ))}
                                </select>
                            </div>
                            <button type="submit" className={adStyles.postJobBtn}>Post Job Ad</button>
                        </form>
                    </div>
                </div>
            )}
            {showApplicantsModal && (
                <div className={adStyles.advModal}>
                    <div className={adStyles.advModalContent}>
                        <span className={adStyles.advClose} onClick={() => setShowApplicantsModal(false)}>&times;</span>
                        <h2>Applicants for Job Ad {currentJobAdId}</h2>
                        <div className="applicants-container">
                            {applicants[currentJobAdId] && applicants[currentJobAdId].map((applicant, index) => (
                                <ApplicantCard key={index} applicant={applicant} />
                            ))}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AdvertisementsPage;