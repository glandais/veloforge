package com.veloforge.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

import java.time.LocalDateTime;

@MongoEntity(collection = "cyclists")
public class CyclistEntity extends PanacheMongoEntity {
    
    public String name;
    public CyclistCapabilities capabilities;
    public CyclistState state;
    public LocalDateTime createdAt;
    
    public CyclistEntity() {
        this.createdAt = LocalDateTime.now();
        this.state = new CyclistState();
    }
    
    public static class CyclistCapabilities {
        public double pma;
        public double physicalEndurance;
        public double sleepResistance;
        public double mentalResilience;
        
        public CyclistCapabilities() {}
        
        public CyclistCapabilities(double pma, double physicalEndurance, 
                                 double sleepResistance, double mentalResilience) {
            this.pma = pma;
            this.physicalEndurance = physicalEndurance;
            this.sleepResistance = sleepResistance;
            this.mentalResilience = mentalResilience;
        }
    }
    
    public static class CyclistState {
        public double energyReserve = 100.0;
        public double muscularFatigue = 0.0;
        public double cerebralFatigue = 0.0;
        public double hydration = 100.0;
        
        public CyclistState() {}
        
        public CyclistState(double energyReserve, double muscularFatigue, 
                           double cerebralFatigue, double hydration) {
            this.energyReserve = energyReserve;
            this.muscularFatigue = muscularFatigue;
            this.cerebralFatigue = cerebralFatigue;
            this.hydration = hydration;
        }
    }
}