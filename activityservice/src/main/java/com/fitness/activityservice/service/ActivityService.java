package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private static final Logger log = LoggerFactory.getLogger(ActivityService.class);
    // Constructor injection for ActivityRepository
    private final ActivityRepository activityRepository;
   private final UserValidationService userValidationService;
   private final RabbitTemplate rabbitTemplate;



  /*
    * This method tracks a user's activity.
    * It validates the user ID, converts the request to an Activity entity,
    * and saves it to the database.
    *
    * @param request The ActivityRequest object containing activity details.
    * @return The saved ActivityResponse object.
  * */
    public ActivityResponse trackActivity(ActivityRequest request) {
        // Validate the user ID
        boolean isValidUser = userValidationService.validateUser(request.getUserId());
        if (!isValidUser) {
            throw new RuntimeException("Invalid User ID: " + request.getUserId());
        }

        // Convert ActivityRequest to Activity entity
        Activity activity = Activity.builder()
                .userId(request.getUserId())
                .type(request.getType())
                .duration(request.getDuration())
                .caloriesBurned(request.getCaloriesBurned())
                .startTime(request.getStartTime())
                .additionalMetrics(request.getAdditionalMetrics())
                .build();
        // Save the activity to the database
        Activity savedActivity = activityRepository.save(activity);

        //publish to RabbitMQ for AI processing
            try {
                rabbitTemplate.convertAndSend(exchange, routingKey, savedActivity);
                log.info("Activity published to RabbitMQ: {}", savedActivity);
            }catch (Exception e){
                log.error("Failed to publish activity to RabbitMQ: {}", e);
            }

        return mapToResponse(savedActivity);
    }
        // Convert saved Activity entity to ActivityResponse
        private ActivityResponse mapToResponse(Activity activity) {
            ActivityResponse response =new ActivityResponse();
            response.setId(activity.getId());
            response.setUserId(activity.getUserId());
            response.setType(activity.getType());
            response.setDuration(activity.getDuration());
            response.setCaloriesBurned(activity.getCaloriesBurned());
            response.setStartTime(activity.getStartTime());
            response.setAdditionalMetrics(activity.getAdditionalMetrics());
            response.setCreatedAt(activity.getCreatedAt());
            response.setUpdatedAt(activity.getUpdatedAt());
            return response;
        }


    public List<ActivityResponse> getUserActivities(String userId) {
        // Fetch activities for the user from the database
        List<Activity> activities = activityRepository.findByUserId(userId);
        // Convert each Activity entity to ActivityResponse
        log.info("Fetching activities for user with ID: {}", userId);
        return activities.stream()
                .map(this::mapToResponse)
                .toList();
    }

    public ActivityResponse getActivityById(String activityId) {
        // Fetch activity by ID from the database
        log.info("Fetching activity with ID: {}", activityId);
       return  activityRepository.findById(activityId)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Activity not found with ID: " + activityId));
        // Convert Activity entity to ActivityResponse

    }
}
