package com.app.linkedinclone.model.dao;

import com.app.linkedinclone.model.dto.SkillDto;
import com.app.linkedinclone.model.enums.CommunicationSkills;
import com.app.linkedinclone.model.enums.ProgrammingLanguage;
import com.app.linkedinclone.model.enums.SoftSkills;
import com.app.linkedinclone.model.enums.TechnicalSkills;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "skill_table")
@Getter
@Setter
@AllArgsConstructor
@ToString(exclude = "user")
public class Skill implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection(targetClass = SoftSkills.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "soft_skills", joinColumns = @JoinColumn(name = "skill_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "soft_skill")
    private List<SoftSkills> softSkills;

    @ElementCollection(targetClass = CommunicationSkills.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "communication_skills", joinColumns = @JoinColumn(name = "skill_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "communication_skill")
    private List<CommunicationSkills> communicationSkills;

    @ElementCollection(targetClass = TechnicalSkills.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "technical_skills", joinColumns = @JoinColumn(name = "skill_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "technical_skill")
    private List<TechnicalSkills> technicalSkills;

    @ElementCollection(targetClass = ProgrammingLanguage.class, fetch = FetchType.LAZY)
    @CollectionTable(name = "programming_languages", joinColumns = @JoinColumn(name = "skill_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "programming_language")
    private List<ProgrammingLanguage> programmingLanguage;

    @OneToOne(mappedBy = "skills")
    private User user;

    public static Skill mapToSkill(SkillDto skillDto) {
        Skill skill = new Skill();
        skill.setSoftSkills(skillDto.getSoftSkills());
        skill.setCommunicationSkills(skillDto.getCommunicationSkills());
        skill.setTechnicalSkills(skillDto.getTechnicalSkills());
        skill.setProgrammingLanguage(skillDto.getProgrammingLanguages());
        return skill;
    }
    public Skill(){
        this.softSkills=new ArrayList<>();
        this.technicalSkills=new ArrayList<>();
        this.communicationSkills=new ArrayList<>();
        this.programmingLanguage=new ArrayList<>();
    }

}