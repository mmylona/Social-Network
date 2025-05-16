package com.app.linkedinclone.service;

import com.app.linkedinclone.config.ArtificialDataGenerator;
import com.app.linkedinclone.model.dao.JobAdvertisement;
import com.app.linkedinclone.model.dao.Post;
import com.app.linkedinclone.model.dao.Skill;
import com.app.linkedinclone.model.dao.User;
import com.app.linkedinclone.repository.JobAdRepository;
import com.app.linkedinclone.repository.PostRepository;
import com.app.linkedinclone.repository.UserRepository;
import com.app.linkedinclone.util.AdRatingMatrixBuilder;
import com.app.linkedinclone.util.MatrixFactorization;
import com.app.linkedinclone.util.PostRatingMatrixBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final JobAdRepository jobAdRepository;
    private MatrixFactorization postModel;
    private MatrixFactorization adModel;


    public List<Post> recommendPostForUser(User user, int numRecommendations) {
        List<Post> posts = postRepository.findAll();
        List<User> users = userRepository.findAll();
        if(users.size() < 100) {
            //add 50 more users
            users.addAll(ArtificialDataGenerator.generateUsers(50));
            userRepository.saveAll(users);

            //add 25 more posts
            posts.addAll(ArtificialDataGenerator.generatePosts(25, users));
            ArtificialDataGenerator.generateConnections(users);
            ArtificialDataGenerator.generateReactions(users, posts);
//            ArtificialDataGenerator.generateComments(users, posts);
            postRepository.saveAll(posts);
        }

        Map<Long, Integer> userIndexMap = new HashMap<>();
        Map<Long, Integer> postIndexMap = new HashMap<>();

        // Populate the maps with 0-based indices
        for (int i = 0; i < users.size(); i++) {
            userIndexMap.put(users.get(i).getId(), i);
        }

        for (int i = 0; i < posts.size(); i++) {
            postIndexMap.put(posts.get(i).getId(), i);
        }

        PostRatingMatrixBuilder matrixBuilder = new PostRatingMatrixBuilder(users, posts);
        this.postModel = new MatrixFactorization(matrixBuilder.getMatrix(), 3, 0.01, 0.02, 100);
        this.postModel.train();




        List<RecommendedPost> recommendations = new ArrayList<>();

        // Use the matrix factorization model to predict ratings for each post
        for (Post post : posts) {
            long userId = userIndexMap.get(user.getId());
            long postId = postIndexMap.get(post.getId());

            // Use matrix factorization to predict rating
            double predictedRating = this.postModel.predict((int)userId, (int)postId);

            // Add the recommendation to the list
            recommendations.add(new RecommendedPost(post, predictedRating));
        }

        // Sort recommendations by predicted rating (descending)
        recommendations.sort(Collections.reverseOrder(
                (RecommendedPost p1, RecommendedPost p2) -> Double.compare(p1.getPredictedRating(), p2.getPredictedRating()))
        );

        // Prioritize articles by network and user
        List<RecommendedPost> prioritized = new ArrayList<>();

        // Fetch the user's posts
        List<Post> userPosts = postRepository.findAllByAuthor(user);

        // Convert user's posts to RecommendedPost objects with a default predicted rating (e.g., 0.0)
        List<RecommendedPost> userRecommendedPosts = userPosts.stream()
                .map(post -> new RecommendedPost(post, 0.0))
                .collect(Collectors.toList());

        // Add user's posts to the prioritized list
        prioritized.addAll(userRecommendedPosts);

        // Log user's own posts
        for (RecommendedPost recommendedPost : userRecommendedPosts) {
            Post post = recommendedPost.getPost();
            System.out.println("User's own post ID: " + post.getId());
        }

        // Prioritize posts from the user's network
        Set<User> network = user.getNetwork();
        for (RecommendedPost recommendation : recommendations) {
            Post post = recommendation.getPost();
            if (network.contains(post.getAuthor())) {
                log.info("Network post ID: {}", post.getId());
                prioritized.add(recommendation);
            }
        }

        // Sort user's posts and network posts by creation date (descending)
        prioritized.sort((RecommendedPost p1, RecommendedPost p2) -> p2.getPost().getCreatedDate().compareTo(p1.getPost().getCreatedDate()));

        // If network prioritization yields enough results, return those
        if (prioritized.size() >= numRecommendations) {
            log.info("That's enough");
            return prioritized.stream()
                    .limit(numRecommendations)
                    .map(RecommendedPost::getPost)
                    .collect(Collectors.toList());
        }

        // Otherwise, fill the remaining slots with non-network recommendations
        List<RecommendedPost> remaining = recommendations.stream()
                .filter(p -> !prioritized.contains(p) && !p.getPost().getAuthor().equals(user))
                .limit(numRecommendations - prioritized.size())
                .collect(Collectors.toList());

        // Log non-network recommendations
        for (RecommendedPost recommendedPost : remaining) {
            Post post = recommendedPost.getPost();
            System.out.println("Non-network post ID: " + post.getId() + ", Rating: " + recommendedPost.predictedRating);
        }

        // Return the prioritized and remaining recommendations together
        return Stream.concat(prioritized.stream(), remaining.stream())
                .limit(numRecommendations)
                .map(RecommendedPost::getPost)
                .collect(Collectors.toList());
    }

    private static class RecommendedPost {
        private final Post post;
        private final double predictedRating;

        public RecommendedPost(Post post, double predictedRating) {
            this.post = post;
            this.predictedRating = predictedRating;
        }

        public Post getPost() {
            return post;
        }

        public double getPredictedRating() {
            return predictedRating;
        }
    }

    public List<JobAdvertisement> recommendAdForUser(User user, int numRecommendations){
        List<JobAdvertisement> ads = jobAdRepository.findAll();
        List<User> users = userRepository.findAll();

        if(ads.size() < 10){
            ads.addAll(ArtificialDataGenerator.generateAds(users));
            jobAdRepository.saveAll(ads);
        }

        Map<Long, Integer> userIndexMap = new HashMap<>();
        Map<Long, Integer> adIndexMap = new HashMap<>();

        // Populate the maps with 0-based indices
        for (int i = 0; i < users.size(); i++) {
            userIndexMap.put(users.get(i).getId(), i);
        }

        for (int i = 0; i < ads.size(); i++) {
            adIndexMap.put(ads.get(i).getId(), i);
        }

        AdRatingMatrixBuilder matrixBuilder = new AdRatingMatrixBuilder(users, ads);
        this.adModel = new MatrixFactorization(matrixBuilder.getMatrix(), 3, 0.01, 0.02, 100);
        this.adModel.train();

        List<RecommendedAds> recommendations = new ArrayList<>();

        // Use the matrix factorization model to predict ratings for each ad
        for (JobAdvertisement ad : ads) {
            long userId = userIndexMap.get(user.getId());
            long adId = adIndexMap.get(ad.getId());

            // Use matrix factorization to predict rating
            double predictedRating = this.adModel.predict((int)userId, (int)adId);

            // Add the recommendation to the list
            recommendations.add(new RecommendedAds(ad, predictedRating));
        }

        // Sort recommendations by predicted rating (descending)
        recommendations.sort(Collections.reverseOrder(
                (RecommendedAds p1, RecommendedAds p2) -> Double.compare(p1.getPredictedRating(), p2.getPredictedRating()))
        );

        // Prioritize ad by user and skills
        List<RecommendedAds> prioritized = new ArrayList<>();

        // Fetch the user's ads
        List<JobAdvertisement> userAds = jobAdRepository.findAllByCreatorOf(user);

        // Convert user's ads to RecommendedAds objects with a default predicted rating
        List<RecommendedAds> userRecommendedAds = userAds.stream()
                .map(ad -> new RecommendedAds(ad, 0.0))
                .collect(Collectors.toList());

        // Add user's ads to the prioritized list
        prioritized.addAll(userRecommendedAds);

        // Log user's own ads
        for (RecommendedAds recommendedAd : userRecommendedAds) {
            JobAdvertisement ad = recommendedAd.getAd();
            System.out.println("User's own ad ID: " + ad.getId());
        }

        // Add these recommendations to the prioritized list
        List<RecommendedAds> skillMatchedAds = getSkillMatchedAds(user,ads,numRecommendations);
        for (RecommendedAds ad : skillMatchedAds) {
            if (!prioritized.contains(ad)) {
                prioritized.add(ad);
            }
        }
        // If skill matching and user's ads yield enough results, return those
        if (prioritized.size() >= numRecommendations) {
            log.info("That's enough");
            return prioritized.stream()
                    .limit(numRecommendations)
                    .map(RecommendedAds::getAd)
                    .collect(Collectors.toList());
        }

        // Otherwise, fill the remaining slots with non-network recommendations
        List<RecommendedAds> remaining = recommendations.stream()
                .filter(p -> !prioritized.contains(p))
                .limit(numRecommendations - prioritized.size())
                .collect(Collectors.toList());

        // Log recommendations
        remaining.forEach(recommendedAd -> {
            JobAdvertisement ad = recommendedAd.getAd();
            double rating = recommendedAd.getPredictedRating();
            log.info("Results from matrix factorization ad ID: {}, Rating: {}", ad.getId(), rating);
        });

        // Add remaining recommendations to the prioritized list
        for (RecommendedAds ad : remaining) {
            if (!prioritized.contains(ad)) {
                prioritized.add(ad);
            }
        }
        // Return the prioritized list
        return prioritized.stream()
                .map(RecommendedAds::getAd)
                .collect(Collectors.toList());
    }

    private static class RecommendedAds {
        private final JobAdvertisement ad;
        private final double predictedRating;

        public RecommendedAds(JobAdvertisement ad, double predictedRating) {
            this.ad = ad;
            this.predictedRating = predictedRating;
        }

        public JobAdvertisement getAd() {
            return ad;
        }

        public double getPredictedRating() {
            return predictedRating;
        }
    }

    private List<RecommendedAds> getSkillMatchedAds(User user, List<JobAdvertisement> ads, int numRecommendations) {
        // Create a map to store the number of matching skills for each ad
        Map<JobAdvertisement, Integer> matchingSkillsMap = new HashMap<>();
        for (JobAdvertisement ad : ads) {
            int matchingSkills = matchSkills(user, ad);
            matchingSkillsMap.put(ad, matchingSkills);

            // If the ad matches the user's skills, log it
            if (matchingSkills > 0) {
                log.info("Job ID: {}, Matching Skills: {}", ad.getId(), matchingSkills);
            }
        }

        // Filter the ads based on the user's skills
        List<JobAdvertisement> skillMatched = ads.stream()
                .filter(ad -> matchingSkillsMap.get(ad) > 0)
                .collect(Collectors.toList());

        // Sort the ads by the number of matching skills in descending order
        skillMatched.sort((JobAdvertisement ad1, JobAdvertisement ad2) ->
                Integer.compare(matchingSkillsMap.get(ad2), matchingSkillsMap.get(ad1))
        );

        // Convert JobAdvertisement objects to RecommendedAds objects
        List<RecommendedAds> skillMatchedAds = skillMatched.stream()
                .map(ad -> new RecommendedAds(ad, matchingSkillsMap.get(ad)))
                .collect(Collectors.toList());

        return skillMatchedAds;
    }

    private int matchSkills(User user, JobAdvertisement ad) {
        Skill userSkills = user.getSkills();
        Skill adSkills = ad.getSkills();

        if (userSkills == null || adSkills == null) {
            log.warn("User or Ad skills are null. User ID: {}, Ad ID: {}", user.getId(), ad.getId());
            return 0;
        }

        int matchingSkills = 0;
        matchingSkills += userSkills.getSoftSkills().stream().filter(adSkills.getSoftSkills()::contains).count();
        matchingSkills += userSkills.getCommunicationSkills().stream().filter(adSkills.getCommunicationSkills()::contains).count();
        matchingSkills += userSkills.getTechnicalSkills().stream().filter(adSkills.getTechnicalSkills()::contains).count();
        matchingSkills += userSkills.getProgrammingLanguage().stream().filter(adSkills.getProgrammingLanguage()::contains).count();

        return matchingSkills;
    }

}
