package com.fitness.aiservice.repository;

import com.fitness.aiservice.model.Recommendation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecommendationRepository extends MongoRepository<Recommendation, String> {
    List<Recommendation> findByUserId(String userId);

    Optional<Recommendation> findByActivityId(String activityId);
    // Custom query methods can be defined here if needed
    // For example, find by userId or activityId
    // List<Recommendation> findByUserId(String userId);
    // List<Recommendation> findByActivityId(String activityId);
}
