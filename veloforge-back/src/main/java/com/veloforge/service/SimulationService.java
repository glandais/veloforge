package com.veloforge.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veloforge.entity.CyclistEntity;
import com.veloforge.entity.EventEntity;
import com.veloforge.entity.PositionEntity;
import com.veloforge.websocket.LiveTrackingWebSocket;
import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class SimulationService {
    
    @Inject
    EventService eventService;
    
    @Inject
    ObjectMapper objectMapper;
    
    @ConfigProperty(name = "veloforge.simulation.physics.gravity", defaultValue = "9.81")
    double gravity;
    
    @ConfigProperty(name = "veloforge.simulation.physics.air-density", defaultValue = "1.225")
    double airDensity;
    
    @ConfigProperty(name = "veloforge.simulation.physics.rolling-coefficient", defaultValue = "0.005")
    double rollingCoefficient;
    
    // Active simulations
    private final Set<String> activeEventIds = ConcurrentHashMap.newKeySet();
    
    public void startEventSimulation(String eventId) {
        activeEventIds.add(eventId);
    }
    
    public void stopEventSimulation(String eventId) {
        activeEventIds.remove(eventId);
    }
    
    @Scheduled(every = "30s")
    public void updateSimulations() {
        for (String eventId : activeEventIds) {
            updateEventSimulation(eventId);
        }
    }
    
    private void updateEventSimulation(String eventId) {
        EventEntity event = EventEntity.findById(eventId);
        if (event == null || event.status != EventEntity.EventStatus.STARTED) {
            stopEventSimulation(eventId);
            return;
        }
        
        for (EventEntity.Participant participant : event.participants) {
            if (participant.status == EventEntity.Participant.ParticipantStatus.STARTED ||
                participant.status == EventEntity.Participant.ParticipantStatus.RIDING) {
                
                updateCyclistPosition(eventId, participant, event);
            }
        }
        
        // Broadcast updated positions via WebSocket
        try {
            var positions = eventService.getCurrentPositions(eventId);
            LiveTrackingWebSocket.broadcastPositions(eventId, positions, objectMapper);
        } catch (Exception e) {
            // Log error
        }
    }
    
    private void updateCyclistPosition(String eventId, EventEntity.Participant participant, EventEntity event) {
        CyclistEntity cyclist = CyclistEntity.findById(participant.cyclistId);
        if (cyclist == null) {
            return;
        }
        
        // Get last position or create initial position
        PositionEntity lastPosition = PositionEntity.find(
            "cyclistId = ?1 AND eventId = ?2 ORDER BY timestamp DESC LIMIT 1",
            participant.cyclistId, eventId
        ).firstResult();
        
        PositionEntity newPosition;
        if (lastPosition == null) {
            // First position - start at route start
            newPosition = createInitialPosition(eventId, participant.cyclistId, event.route);
        } else {
            // Calculate next position based on physics
            newPosition = calculateNextPosition(lastPosition, cyclist, event.route);
        }
        
        // Update participant status
        if (participant.status == EventEntity.Participant.ParticipantStatus.STARTED) {
            participant.status = EventEntity.Participant.ParticipantStatus.RIDING;
            event.persist();
        }
        
        // Check if finished
        if (newPosition.distance >= event.route.distance) {
            participant.status = EventEntity.Participant.ParticipantStatus.FINISHED;
            participant.finishTime = LocalDateTime.now();
            newPosition.distance = event.route.distance;
            event.persist();
        }
        
        newPosition.persist();
        
        // Update cyclist fatigue (simplified)
        updateCyclistFatigue(cyclist, newPosition.speed);
    }
    
    private PositionEntity createInitialPosition(String eventId, String cyclistId, EventEntity.Route route) {
        PositionEntity position = new PositionEntity();
        position.eventId = eventId;
        position.cyclistId = cyclistId;
        position.location = new PositionEntity.GeoPoint(
            route.startPoint.latitude, 
            route.startPoint.longitude
        );
        position.distance = 0.0;
        position.speed = 25.0; // Start at 25 km/h
        position.power = 250.0; // Base power
        return position;
    }
    
    private PositionEntity calculateNextPosition(PositionEntity lastPosition, CyclistEntity cyclist, EventEntity.Route route) {
        // Simple physics simulation for MVP
        double deltaTimeHours = 30.0 / 3600.0; // 30 seconds in hours
        
        // Calculate effective power based on cyclist state
        double effectivePower = calculateEffectivePower(cyclist, lastPosition.power != null ? lastPosition.power : 250.0);
        
        // Simple speed calculation (ignoring wind, gradient for MVP)
        // Power = 0.5 * airDensity * CdA * v^3 + Crr * mass * g * v
        // Simplified: v = cbrt(Power / k) where k is a constant
        double speedKmh = Math.cbrt(effectivePower / 0.3) * 3.6; // Convert m/s to km/h
        speedKmh = Math.max(10.0, Math.min(speedKmh, 50.0)); // Clamp between 10-50 km/h
        
        // Calculate distance traveled
        double distanceTraveled = speedKmh * deltaTimeHours;
        double newDistance = lastPosition.distance + distanceTraveled;
        
        // Calculate new position along route (simplified linear interpolation)
        PositionEntity.GeoPoint newLocation = interpolatePosition(route, newDistance);
        
        PositionEntity newPosition = new PositionEntity();
        newPosition.eventId = lastPosition.eventId;
        newPosition.cyclistId = lastPosition.cyclistId;
        newPosition.location = newLocation;
        newPosition.distance = newDistance;
        newPosition.speed = speedKmh;
        newPosition.power = effectivePower;
        
        return newPosition;
    }
    
    private double calculateEffectivePower(CyclistEntity cyclist, double basePower) {
        // Factor in fatigue and hydration
        double fatigueMultiplier = (100.0 - cyclist.state.muscularFatigue) / 100.0;
        double hydrationMultiplier = cyclist.state.hydration / 100.0;
        double energyMultiplier = cyclist.state.energyReserve / 100.0;
        
        return basePower * fatigueMultiplier * hydrationMultiplier * energyMultiplier;
    }
    
    private PositionEntity.GeoPoint interpolatePosition(EventEntity.Route route, double distance) {
        // Simple linear interpolation between start and end for MVP
        double progress = Math.min(distance / route.distance, 1.0);
        
        double lat = route.startPoint.latitude + 
                     (route.endPoint.latitude - route.startPoint.latitude) * progress;
        double lng = route.startPoint.longitude + 
                     (route.endPoint.longitude - route.startPoint.longitude) * progress;
        
        return new PositionEntity.GeoPoint(lat, lng);
    }
    
    private void updateCyclistFatigue(CyclistEntity cyclist, double speed) {
        // Simple fatigue model for MVP
        double fatigueIncrease = speed > 30 ? 0.5 : 0.2; // More fatigue at higher speeds
        double hydrationDecrease = 0.1;
        double energyDecrease = 0.3;
        
        cyclist.state.muscularFatigue = Math.min(100.0, cyclist.state.muscularFatigue + fatigueIncrease);
        cyclist.state.hydration = Math.max(0.0, cyclist.state.hydration - hydrationDecrease);
        cyclist.state.energyReserve = Math.max(0.0, cyclist.state.energyReserve - energyDecrease);
        
        cyclist.persist();
    }
}