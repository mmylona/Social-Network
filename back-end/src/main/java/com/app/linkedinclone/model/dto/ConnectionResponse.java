package com.app.linkedinclone.model.dto;


import com.app.linkedinclone.model.dao.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionResponse {
    private Long id;
    private String name;
    private String company;
    private String title;
    private String profilePic;

    public static ConnectionResponse mapConnectionResponse(User user,String imageUrl) {
        return new ConnectionResponse(user.getId(),user.getFirstName().concat(" ").concat(user.getLastName()), user.getCurrentCompany(), user.getTitle(), imageUrl);
    }

}
