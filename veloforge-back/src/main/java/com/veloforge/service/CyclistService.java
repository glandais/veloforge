package com.veloforge.service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;

import com.veloforge.entity.CyclistEntity;
import com.veloforge.model.CreateCyclistRequest;
import com.veloforge.model.Cyclist;
import com.veloforge.model.CyclistCapabilities;
import com.veloforge.model.CyclistState;

@ApplicationScoped
public class CyclistService {

  public List<Cyclist> getAllCyclists() {
    return CyclistEntity.<CyclistEntity>listAll().stream().map(this::toModel).toList();
  }

  public Optional<Cyclist> getCyclistById(String id) {
    CyclistEntity entity = CyclistEntity.findById(id);
    return entity != null ? Optional.of(toModel(entity)) : Optional.empty();
  }

  public Cyclist createCyclist(CreateCyclistRequest request) {
    CyclistEntity entity = new CyclistEntity();
    entity.name = request.getName();
    entity.capabilities = toEntityCapabilities(request.getCapabilities());
    entity.persist();

    return toModel(entity);
  }

  public Optional<Cyclist> updateCyclistState(String id, CyclistState newState) {
    CyclistEntity entity = CyclistEntity.findById(id);
    if (entity == null) {
      return Optional.empty();
    }

    entity.state = toEntityState(newState);
    entity.persist();

    return Optional.of(toModel(entity));
  }

  private Cyclist toModel(CyclistEntity entity) {
    Cyclist cyclist = new Cyclist();
    cyclist.setId(entity.id.toString());
    cyclist.setName(entity.name);
    cyclist.setCapabilities(toModelCapabilities(entity.capabilities));
    cyclist.setState(toModelState(entity.state));
    cyclist.setCreatedAt(OffsetDateTime.of(entity.createdAt, ZoneOffset.UTC));
    return cyclist;
  }

  private CyclistCapabilities toModelCapabilities(CyclistEntity.CyclistCapabilities entityCaps) {
    CyclistCapabilities capabilities = new CyclistCapabilities();
    capabilities.setPma(BigDecimal.valueOf(entityCaps.pma));
    capabilities.setPhysicalEndurance(BigDecimal.valueOf(entityCaps.physicalEndurance));
    capabilities.setSleepResistance(BigDecimal.valueOf(entityCaps.sleepResistance));
    capabilities.setMentalResilience(BigDecimal.valueOf(entityCaps.mentalResilience));
    return capabilities;
  }

  private CyclistState toModelState(CyclistEntity.CyclistState entityState) {
    CyclistState state = new CyclistState();
    state.setEnergyReserve(BigDecimal.valueOf(entityState.energyReserve));
    state.setMuscularFatigue(BigDecimal.valueOf(entityState.muscularFatigue));
    state.setCerebralFatigue(BigDecimal.valueOf(entityState.cerebralFatigue));
    state.setHydration(BigDecimal.valueOf(entityState.hydration));
    return state;
  }

  private CyclistEntity.CyclistCapabilities toEntityCapabilities(CyclistCapabilities modelCaps) {
    return new CyclistEntity.CyclistCapabilities(
        modelCaps.getPma().doubleValue(),
        modelCaps.getPhysicalEndurance().doubleValue(),
        modelCaps.getSleepResistance().doubleValue(),
        modelCaps.getMentalResilience().doubleValue());
  }

  private CyclistEntity.CyclistState toEntityState(CyclistState modelState) {
    return new CyclistEntity.CyclistState(
        modelState.getEnergyReserve().doubleValue(),
        modelState.getMuscularFatigue().doubleValue(),
        modelState.getCerebralFatigue().doubleValue(),
        modelState.getHydration().doubleValue());
  }
}
