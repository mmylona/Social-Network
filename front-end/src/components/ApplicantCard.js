// src/components/ApplicantCard.js
import React from 'react';
import applicantStyle from './ApplicantCard.module.css';

const ApplicantCard = ({ applicant }) => {
    const { skills, educations, workExperiences, cvFile } = applicant;

    const handleDownloadCV = () => {
        const blob = new Blob([cvFile], { type: 'application/pdf' });
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `${applicant.firstName}_${applicant.lastName}_CV.pdf`;
        a.click();
        window.URL.revokeObjectURL(url);
    };

    return (
        <div className={applicantStyle.applicantCard}>
            <img src={applicant.profilePicUrl} alt={`${applicant.firstName} ${applicant.lastName}`} className={applicantStyle.applicantAvatar} />
            <div className={applicantStyle.applicantInfo}>
                <h3>{applicant.firstName} {applicant.lastName}</h3>
                <p className={applicantStyle.applicantTitle}>{applicant.title} at {applicant.currentCompany}</p>
                <p className={applicantStyle.applicantLocation}>{applicant.location}</p>
                <div className={applicantStyle.applicantDetails}>
                    <div>
                        <h4>Skills:</h4>
                        {skills ? (
                            <ul>
                                {skills.softSkills && (
                                    <>
                                        <strong>Soft Skills:</strong>
                                        {skills.softSkills.map((skill, index) => <li key={index}>{skill}</li>)}
                                    </>
                                )}
                                {skills.communicationSkills && (
                                    <>
                                        <strong>Communication Skills:</strong>
                                        {skills.communicationSkills.map((skill, index) => <li key={index}>{skill}</li>)}
                                    </>
                                )}
                                {skills.technicalSkills && (
                                    <>
                                        <strong>Technical Skills:</strong>
                                        {skills.technicalSkills.map((skill, index) => <li key={index}>{skill}</li>)}
                                    </>
                                )}
                                {skills.programmingLanguages && (
                                    <>
                                        <strong>Programming Languages:</strong>
                                        {skills.programmingLanguages.map((skill, index) => <li key={index}>{skill}</li>)}
                                    </>
                                )}
                            </ul>
                        ) : (
                            <p>No skills listed</p>
                        )}
                    </div>
                    <div className={applicantStyle.applWorkExperience}>
                        <strong>Work Experience:</strong>
                        {workExperiences ? (
                            workExperiences.map((exp, index) => (
                                <p key={index}>{exp.position} at {exp.company}</p>
                            ))
                        ) : (
                            <p>No work experience listed</p>
                        )}
                    </div>
                    <div className={applicantStyle.applEducation}>
                        <strong>Education:</strong>
                        {educations ? (
                            educations.map((edu, index) => (
                                <p key={index}>{edu.degree} from {edu.schoolName}</p>
                            ))
                        ) : (
                            <p>No education listed</p>
                        )}
                    </div>
                    {cvFile && (
                        <button onClick={handleDownloadCV} className={applicantStyle.downloadCvBtn}>Download CV</button>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ApplicantCard;