package com.app.linkedinclone.repository;

import com.app.linkedinclone.model.dao.Role;
import com.app.linkedinclone.model.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role,Integer> {

    Role findByRoleName(RoleName roleName);
}
