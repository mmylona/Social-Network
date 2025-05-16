package com.app.linkedinclone.util;

import com.app.linkedinclone.model.dao.Post;
import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.model.dao.UserItemInteraction;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
@Slf4j
@Getter
@Setter
public class PostRatingMatrixBuilder {
    private double[][] ratings;
    double numUsers;
    double numPosts;
    private static final double LIKE_WEIGHT = 1.0;
    private static final double LOVE_WEIGHT = 1.5;
    private static final double CARE_WEIGHT = 2.0;
    private static final double COMMENT_WEIGHT = 3.0;

    public PostRatingMatrixBuilder(List<User> users, List<Post> posts) {
        this.numUsers = users.size();
        this.numPosts = posts.size();
        this.ratings = new double[(int) numUsers][(int) numPosts];

        for (int i = 0; i < numUsers; i++) {
            User user = users.get(i);
            for (int j = 0; j < numPosts; j++) {
                Post post = posts.get(j);

                double like = 0.0;
                double love = 0.0;
                double care = 0.0;
                double comment = 0.0;

                for (UserItemInteraction interaction : user.getUserItemInteractions()) {
//                    log.info("interaction: {}", interaction);
                    if (interaction.getPost().equals(post)) {
                        switch (interaction.getInteractionType()) {
                            case LIKE:
//                                System.out.println("like");
                                like = 1.0;
                                break;
                            case LOVE:
//                                System.out.println("love");
                                love = 1.0;
                                break;
                            case CARE:
//                                System.out.println("care");
                                care = 1.0;
                                break;
                            case COMMENT:
//                                System.out.println("comment");
                                comment += 1.0;
                                break;
                        }
                    }
                }

                ratings[i][j] = (like * LIKE_WEIGHT) +
                        (love * LOVE_WEIGHT) +
                        (care * CARE_WEIGHT) +
                        (comment * COMMENT_WEIGHT);
            }

        }
//    printRatings();
    }


    public void printRatings() {
        for (int i = 0; i < numUsers; i++) {
            for (int j = 0; j < numPosts; j++) {
                System.out.print(ratings[i][j] + " ");
            }
            System.out.println();
        }
    }

    public double[][] getMatrix(){
        return this.ratings;
    }

    public void setMatrix(double[][] matrix) { this.ratings = matrix;}
}
