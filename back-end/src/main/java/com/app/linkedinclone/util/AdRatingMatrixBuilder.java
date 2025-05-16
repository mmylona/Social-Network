package com.app.linkedinclone.util;

import com.app.linkedinclone.model.dao.JobAdvertisement;
import com.app.linkedinclone.model.dao.User;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class AdRatingMatrixBuilder {
    private double[][] ratings;
    double numUsers;
    double numAds;
    private static final double APPLICATION_WEIGHT = 2.0;
    private static final double VIEW_WEIGHT = 1.0;

    public AdRatingMatrixBuilder(List<User> users, List<JobAdvertisement> ads){
        this.numUsers = users.size();
        this.numAds = ads.size();
        this.ratings = new double[(int) numUsers][(int) numAds];

        for (int i = 0; i < numUsers; i++) {
            User user = users.get(i);
            for (int j = 0; j < numAds; j++) {
                JobAdvertisement ad = ads.get(j);

                double apply = 0.0;
                double view = 0.0;

                if (ad.getApplicants().contains(user)) {
                    apply = 1.0;
                }

                if(ad.getViewers().contains(user)) {
                    view = 1.0;
                }

                ratings[i][j] = (apply * APPLICATION_WEIGHT) + (view * VIEW_WEIGHT);


            }
        }
//        printRatings();
    }

    private void printRatings() {
        for (int i = 0; i < numUsers; i++) {
            for (int j = 0; j < numAds; j++) {
                System.out.print(ratings[i][j] + " ");
            }
            System.out.println();
        }
    }

    public double[][] getMatrix(){
        return this.ratings;
    }

}
