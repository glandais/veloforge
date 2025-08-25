# VeloForge - Virtual Ultra Cycling Competition Platform

## Project Overview
VeloForge is a sophisticated virtual ultra-cycling competition platform that simulates realistic long-distance cycling events with detailed physiological modeling, real-world conditions, and strategic gameplay elements.

## Architecture

### Technology Stack
- **Backend**: Quarkus (Java 21+) - Reactive, cloud-native
- **Frontend**: Vue 3 + TypeScript + Pinia + Vue Router
- **Database**: MongoDB (cyclists, events, results)
- **Cache**: Redis (real-time positions, leaderboards)
- **Messaging**: Redpanda (Kafka-compatible event streaming, real-time updates)
- **Routing**: GraphHopper (self-hosted) or external API
- **Contract**: OpenAPI 3.1 specification-driven development

### Repository Structure
```
veloforge/
├── contract.yaml                 # OpenAPI 3.1 contract (source of truth)
├── veloforge-back/          # Main REST/GraphQL API (Quarkus)
├── veloforge-simulation/    # Physics/physiology engine (Python/NumPy)
├── veloforge-weather/       # Weather integration service
├── veloforge-social/        # Social features microservice
├── veloforge-analytics/     # Metrics and dashboards
├── veloforge-front/
│   ├── src/
│   │   ├── api/                 # Generated TypeScript client from contract.yaml
│   │   ├── stores/              # Pinia stores
│   │   ├── views/               # Vue components
│   │   └── composables/         # Shared logic
│   └── package.json
└── docker-compose.yml            # Local development environment
```

## Core Domain Model

### Cyclist Entity
```typescript
interface Cyclist {
  // Capabilities (point-based system for balance)
  capabilities: {
    pma: number;                    // Power at VO2max
    physicalEndurance: number;       // Cardiac capacity for long efforts
    sleepResistance: number;         // Sleep deprivation tolerance
    sleepQuality: number;            // Recovery efficiency
    nutritionEfficiency: number;     // Caloric needs and carb processing
    hydrationEfficiency: number;     // Water needs and retention
    mentalResilience: number;        // Pressure resistance
    motivation: number;              // Drive to continue
  };
  
  // Real-time gauges
  state: {
    energyReserve: number;          // 0-100%
    muscularFatigue: number;        // 0-100%
    cerebralFatigue: number;        // 0-100%
    psychologicalState: number;     // Multi-factorial score
    coreTemperature: number;        // Thermoregulation
    hydration: number;              // 0-100%
    glycogenStores: number;         // 0-100%
  };
  
  equipment: {
    bike: BikeSpec;                 // Weight, aerodynamics, reliability
    clothing: ClothingSet;          // Weather adaptation
    navigation: NavigationType;     // GPS quality, failure risk
  };
}
```

### Event Management
```typescript
interface Event {
  id: string;
  name: string;
  type: 'point-to-point' | 'loop' | 'brevet';
  startTime: Date;
  checkpoints: Checkpoint[];
  weatherForecast: WeatherData[];
  participants: Participant[];
  rules: EventRules;
}
```

## Key Algorithms & Services

### Simulation Engine
The simulation engine calculates cyclist performance every minute based on:
- **Power output**: W/kg adjusted for gradient, wind, fatigue
- **Energy expenditure**: Calories/hour based on effort zones
- **Fatigue accumulation**: Exponential model with recovery curves
- **Speed calculation**: Physics model including rolling resistance, air resistance, gradient
- **Decision making**: AI evaluates manager orders vs cyclist state

### Critical Calculations
```java
// Example: Effective power calculation
double effectivePower = basePower 
  * fatigueMultiplier(muscularFatigue, cerebralFatigue)
  * weatherImpact(headwind, temperature)
  * nutritionMultiplier(glycogenStores, hydration)
  * psychologicalMultiplier(motivation, pressure);
```

### Real-time Features
- **Redpanda Topics** (Kafka-compatible):
  - `cyclist.position.updated`: GPS coordinates every 30s
  - `cyclist.state.changed`: Status updates (riding, resting, eating)
  - `event.leaderboard.updated`: Rankings
  - `social.activity`: Posts, photos, encouragements

### External Integrations
- **Elevation**: OpenTopoData API or SRTM data
- **Weather**: OpenWeatherMap API (real-time + forecast)
- **POIs**: Overpass API for OSM data (shops, water, accommodation)
- **Maps**: MapLibre GL for rendering

## Development Workflow

### Contract-First Development
1. Modify `contract.yaml` with new endpoints/models
2. Backend: Generate Quarkus interfaces via `openapi-generator-maven-plugin`
3. Frontend: Generate TypeScript client via `openapi-typescript-codegen`
4. Implement business logic following generated contracts

### Key Commands
```bash
# Backend development
cd backend/veloforge-api
./mvnw quarkus:dev                    # Hot reload development

# Frontend development  
cd frontend
npm run dev                            # Vite dev server
npm run generate:api                   # Regenerate API client

# Full stack
docker compose up                      # MongoDB, Redis, Redpanda
```

### Testing Strategy
- **Unit tests**: Service layer logic, simulation algorithms
- **Integration tests**: @QuarkusTest for API endpoints
- **E2E tests**: Cypress for critical user journeys
- **Load tests**: K6 for real-time features (1000+ concurrent cyclists)

## Performance Considerations

### Optimization Points
1. **Position updates**: Batch writes to MongoDB, use Redis for real-time
2. **Leaderboard**: Materialized views in Redis, recalculate every 30s
3. **Weather cache**: 1-hour TTL for weather data
4. **Route calculations**: Pre-compute popular segments, cache in Redis
5. **Social feed**: Pagination with cursor-based navigation

### Scalability Targets
- Support 10,000 concurrent cyclists per event
- Process 100,000 position updates/minute
- Serve dot-watching map with <100ms latency
- Handle 1M social interactions/day

## Security & Anti-Cheat

### Measures
- **Performance validation**: Statistical anomaly detection
- **Rate limiting**: API throttling per user
- **Input validation**: Strict bounds on cyclist capabilities
- **Audit logging**: All manager commands timestamped
- **JWT authentication**: Quarkus OIDC with refresh tokens

## Business Logic Examples

### Fatigue Model
```python
def calculate_fatigue_accumulation(power_output, duration, cyclist_endurance):
    """
    Fatigue accumulates exponentially above threshold power
    Recovery happens during rest periods
    """
    threshold = cyclist_endurance * 0.75
    if power_output > threshold:
        accumulation_rate = math.exp((power_output - threshold) / 50)
    else:
        accumulation_rate = -0.1  # Recovery
    return min(100, max(0, current_fatigue + accumulation_rate * duration))
```

### Manager AI Compliance
```java
public CyclistResponse processManagerOrder(ManagerOrder order, CyclistState state) {
    // Cyclist may refuse orders if too exhausted
    double compliance = calculateCompliance(state.cerebralFatigue, 
                                           state.psychologicalState,
                                           order.intensity);
    
    if (compliance < 0.3) {
        return CyclistResponse.FORCED_REST; // 12h mandatory stop
    } else if (compliance < 0.7) {
        return CyclistResponse.REDUCED_EFFORT; // Partial compliance
    }
    return CyclistResponse.FULL_COMPLIANCE;
}
```

## Configuration Management

### Environment Variables
```yaml
MONGO_CONNECTION_STRING: mongodb://localhost:27017/veloforge
REDIS_URL: redis://localhost:6379
KAFKA_BOOTSTRAP_SERVERS: localhost:19092  # Redpanda external port
GRAPHHOPPER_API_KEY: ${GRAPHHOPPER_API_KEY}
OPENWEATHERMAP_API_KEY: ${OPENWEATHERMAP_API_KEY}
JWT_SECRET: ${JWT_SECRET}
```

## Monitoring & Observability

### Key Metrics
- **Business**: Active cyclists, events completed, user retention
- **Technical**: API latency (p50, p95, p99), error rates
- **Simulation**: Computation time per cyclist, accuracy scores
- **Infrastructure**: CPU, memory, MongoDB operations/sec

### Logging Strategy
- Structured logging (JSON) with correlation IDs
- Error tracking with Sentry
- Distributed tracing with OpenTelemetry

## Future Enhancements
1. **Machine Learning**: Cyclist behavior prediction, optimal strategy suggestions
2. **Mobile App**: React Native companion app
3. **Live Streaming**: Integration with Twitch/YouTube APIs
4. **Sponsorship System**: Virtual team sponsors, prize pools
5. **Training Mode**: Practice events, strategy testing

## Important Notes for Development

### Code Generation
- Always regenerate TypeScript client after contract.yaml changes
- Use `@Generated` annotation for generated code (exclude from coverage)
- Never manually edit generated files

### Database Indexes
```javascript
// MongoDB indexes for performance
db.cyclists.createIndex({ "eventId": 1, "position.timestamp": -1 })
db.events.createIndex({ "startTime": 1, "status": 1 })
db.positions.createIndex({ "cyclistId": 1, "timestamp": -1 })
```

### Redis Keys Pattern
```
cyclist:{id}:position        # Current position (TTL: 1min)
event:{id}:leaderboard       # Sorted set of cyclists
cyclist:{id}:state           # Current state cache (TTL: 30s)
event:{id}:weather:{lat}:{lon} # Weather cache (TTL: 1h)
```

### Redpanda Message Format
All messages use Avro schema with Redpanda's built-in schema registry for evolution.

## Contributing Guidelines
1. Feature branches from `develop`
2. Commits follow conventional commits format
3. PR requires 2 approvals + passing CI
4. Performance impact assessment for simulation changes
5. Update contract.yaml for any API changes