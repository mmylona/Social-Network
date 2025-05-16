package com.app.linkedinclone.model.dao;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode
public abstract class Job implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    protected Long id;
    @NotNull(message = "Job title is required")
    @Column(name = "job_title")
    protected String title;
    @NotNull(message = "Job description is required")
    @Column(name = "job_description")
    protected String description;
    @NotNull(message = "Company name is required")
    @Column(name = "company_name")
    protected String company;
    @NotNull(message = "Job location is required")
    @Column(name = "job_location")
    protected String location;

}