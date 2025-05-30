package com.app.linkedinclone.model.dao;

import com.app.linkedinclone.model.enums.RoleName;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Role implements Serializable  {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id ;
    @Enumerated(EnumType.STRING)
    RoleName roleName ;

    public Role(RoleName roleName) {this.roleName = roleName;}
    public String getRoleName() {
        return roleName.toString();
    }
}
