package com.app.linkedinclone.model.dto;

import com.app.linkedinclone.model.dao.Skill;
import com.app.linkedinclone.model.enums.CommunicationSkills;
import com.app.linkedinclone.model.enums.ProgrammingLanguage;
import com.app.linkedinclone.model.enums.SoftSkills;
import com.app.linkedinclone.model.enums.TechnicalSkills;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;

@Getter
@Setter
@ToString
@Slf4j
public class SkillDto {
    private List<SoftSkills> softSkills;

    private List<CommunicationSkills> communicationSkills;

    private List<TechnicalSkills> technicalSkills;

    private List<ProgrammingLanguage> programmingLanguages;

    public static SkillDto mapToSkillDto(Skill skill) {
        SkillDto skillDto = new SkillDto();
        if(isNull(skill )) {
            log.error("Skill is null");
            return null;
        }
        skillDto.setSoftSkills(Optional.of(skill.getSoftSkills()).orElse(new ArrayList<>()));
        skillDto.setCommunicationSkills(Optional.of(skill.getCommunicationSkills()).orElse(new ArrayList<>()));
        skillDto.setTechnicalSkills(Optional.of((skill.getTechnicalSkills())).orElse(new ArrayList<>()));
        skillDto.setProgrammingLanguages(Optional.of(skill.getProgrammingLanguage()).orElse(new ArrayList<>()));
        return skillDto;
    }

    public static List<SkillDto> fromSkill(Skill skill) {
        if (skill == null) {
            throw new IllegalArgumentException("Skill cannot be null");
        }
        SkillDto skillDto = new SkillDto();
        skillDto.setSoftSkills(skill.getSoftSkills());
        skillDto.setCommunicationSkills(skill.getCommunicationSkills());
        skillDto.setTechnicalSkills(skill.getTechnicalSkills());
        skillDto.setProgrammingLanguages(skill.getProgrammingLanguage());

        List<SkillDto> skillDtoList = new ArrayList<>();
        skillDtoList.add(skillDto);
        return skillDtoList;
    }
}
