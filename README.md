# 🚴 VeloForge - Virtual Ultra Cycling Competition Platform

**MVP Status: ✅ COMPLETE**

A sophisticated virtual ultra-cycling competition platform with real-time simulation, live tracking, and interactive race visualization.

## Overview

VeloForge simulates realistic long-distance cycling events with detailed physiological modeling, real-world conditions, and strategic gameplay elements. This MVP demonstrates the core functionality with 2-3 cyclists racing on a 100km route.

## Features

### ✅ MVP Features Implemented
- **Full-Stack Application**: Complete frontend and backend integration
- **Interactive Map**: Leaflet-based real-time race visualization
- **Live WebSocket Updates**: Real-time position tracking
- **Form Validation**: Input validation with user feedback
- **Notifications System**: Success/error notifications
- **Cyclist Management**: Create and manage cyclists with unique capabilities
- **Event System**: Create events, add participants, start races
- **Physics Simulation**: Realistic speed calculations with fatigue modeling
- **Dynamic Leaderboards**: Auto-updating rankings
- **Responsive UI**: Modern Vue 3 interface with Pinia state management

### 🎯 Core Simulation
- **Physiological Model**: Energy, fatigue, hydration depletion over time
- **Power-to-Speed Physics**: Aerodynamics and rolling resistance
- **Route Interpolation**: Linear progression between start/finish points
- **Auto-progression**: Cyclists advance automatically based on calculated speeds

## Quick Start

### Prerequisites
- Java 21+
- Maven 3.8+
- Docker & Docker Compose
- Node.js 18+ (for frontend)

### 1. Quick Start - All Services
```bash
# Start everything with one command
./startup.sh
```

Or manually:

### Start Infrastructure
```bash
docker-compose up -d
```

This will start:
- MongoDB (data storage)
- Redis (real-time cache)
- Kafka (event streaming)
- Management UIs for all services

### 2. Start Backend API
```bash
cd veloforge-back
./mvnw quarkus:dev
```

Backend will be available at http://localhost:8080

### 3. Start Frontend
```bash
cd veloforge-front
npm install  # First time only
npm run dev
```

Frontend will be available at http://localhost:5173

### 4. Access Points
- **🌐 Frontend**: http://localhost:5173
- **🔧 Backend API**: http://localhost:8080
- **📊 Swagger UI**: http://localhost:8080/swagger-ui
- **🗄️ MongoDB UI**: http://localhost:8081
- **🔴 Redis Commander**: http://localhost:8082
- **📨 Kafka UI**: http://localhost:8083

## Architecture

```
veloforge/
├── contract.yaml              # OpenAPI 3.1 specification
├── docker-compose.yml         # Infrastructure services  
├── veloforge-back/           # Quarkus REST API
│   ├── src/main/java/com/veloforge/
│   │   ├── entity/           # MongoDB entities
│   │   ├── service/          # Business logic
│   │   ├── resource/         # REST controllers
│   │   └── websocket/        # Real-time updates
│   └── pom.xml
├── veloforge-front/          # Vue 3 Frontend
│   ├── src/
│   │   ├── api/              # Generated TypeScript client
│   │   ├── components/       # Vue components
│   │   ├── stores/           # Pinia state management
│   │   ├── views/            # Page components
│   │   └── composables/      # WebSocket & utilities
│   └── package.json
├── scripts/
│   └── init-mongo.js         # Sample data
└── start-dev.sh              # Development setup
```

## API Endpoints

### Cyclists
- `GET /cyclists` - List all cyclists
- `POST /cyclists` - Create new cyclist
- `GET /cyclists/{id}` - Get cyclist details

### Events  
- `GET /events` - List all events
- `POST /events` - Create new event
- `GET /events/{id}` - Get event details
- `POST /events/{id}/start` - Start event simulation
- `POST /events/{id}/participants` - Add cyclist to event
- `GET /events/{id}/leaderboard` - Get current rankings

### Real-time
- `GET /events/{id}/positions` - Get current positions
- `WS /ws/events/{id}/positions` - Live position updates

## Sample Data

The MongoDB initialization includes:
- **3 Cyclists**: Alice Champion, Bob Endurance, Charlie Speed
- **1 Event**: Paris-Chartres Classic (100km route)

## 🎮 Using the MVP

### Via Frontend UI (Recommended)
1. **Open http://localhost:5173** after starting services
2. **Navigate to Cyclists** - View sample cyclists or create new ones
3. **Navigate to Events** - View the sample event or create new ones
4. **Click "View Details"** on an event to access the event dashboard
5. **Add participants** to the event using the "Add Participant" button
6. **Start the event** - Real-time simulation begins automatically
7. **Watch live updates** - Leaderboard and positions update every 30 seconds
8. **Monitor progress** - Visual route progress and cyclist statistics

### Via API (Advanced)
Use the curl examples below or explore the API documentation.

### Example Workflow
```bash
# Get sample cyclists
curl http://localhost:8080/cyclists

# Get sample event  
curl http://localhost:8080/events

# Add cyclist to event
curl -X POST http://localhost:8080/events/{eventId}/participants \
  -H "Content-Type: application/json" \
  -d '{"cyclistId": "{cyclistId}"}'

# Start the event
curl -X POST http://localhost:8080/events/{eventId}/start

# Watch positions update
curl http://localhost:8080/events/{eventId}/positions

# Check leaderboard
curl http://localhost:8080/events/{eventId}/leaderboard
```

## Development Services

Access management interfaces:
- **MongoDB UI**: http://localhost:8081
- **Redis Commander**: http://localhost:8082  
- **Kafka UI**: http://localhost:8083

## Configuration

Key settings in `application.properties`:
- Simulation updates every 30 seconds
- Physics constants (air density, rolling resistance)
- Database connections

## 🚀 MVP Components Implemented

### Frontend (Vue 3)
- ✅ **EventMap.vue** - Leaflet map with live cyclist positions
- ✅ **NotificationContainer.vue** - Toast notifications
- ✅ **useWebSocket.ts** - WebSocket composable with auto-reconnect
- ✅ **useNotification.ts** - Notification system
- ✅ **Form Validation** - Input validation with error messages
- ✅ **Pinia Stores** - State management for cyclists and events
- ✅ **Vue Router** - Navigation between views

### Backend (Quarkus)
- ✅ **REST API** - Full CRUD operations
- ✅ **WebSocket Server** - Real-time position broadcasting
- ✅ **Simulation Engine** - Physics-based cyclist movement
- ✅ **MongoDB Integration** - Data persistence
- ✅ **Redis Caching** - Performance optimization
- ✅ **Kafka Messaging** - Event streaming

## 🔮 Future Enhancements

- [ ] Enhanced route system with elevation profiles
- [ ] Weather integration affecting performance
- [ ] Advanced fatigue and recovery models
- [ ] Team management features
- [ ] Historical race analytics
- [ ] Mobile companion app
- [ ] Machine learning for strategy optimization

## Architecture Notes

- **Contract-First**: OpenAPI drives both backend and frontend generation
- **Event-Driven**: Kafka enables scalable real-time features
- **Simulation Engine**: Scheduled updates with physics calculations
- **Entity Separation**: Clean boundaries between API models and database entities
