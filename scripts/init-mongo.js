// MongoDB initialization script for VeloForge
print('Starting VeloForge database initialization...');

// Switch to veloforge database
db = db.getSiblingDB('veloforge');

// Create collections with validation
db.createCollection('cyclists', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['name', 'capabilities', 'state', 'createdAt'],
      properties: {
        name: {
          bsonType: 'string',
          minLength: 2,
          maxLength: 50
        },
        capabilities: {
          bsonType: 'object',
          required: ['pma', 'physicalEndurance', 'sleepResistance', 'mentalResilience'],
          properties: {
            pma: { bsonType: 'number', minimum: 200, maximum: 500 },
            physicalEndurance: { bsonType: 'number', minimum: 0, maximum: 100 },
            sleepResistance: { bsonType: 'number', minimum: 0, maximum: 100 },
            mentalResilience: { bsonType: 'number', minimum: 0, maximum: 100 }
          }
        },
        state: {
          bsonType: 'object',
          required: ['energyReserve', 'muscularFatigue', 'cerebralFatigue', 'hydration'],
          properties: {
            energyReserve: { bsonType: 'number', minimum: 0, maximum: 100 },
            muscularFatigue: { bsonType: 'number', minimum: 0, maximum: 100 },
            cerebralFatigue: { bsonType: 'number', minimum: 0, maximum: 100 },
            hydration: { bsonType: 'number', minimum: 0, maximum: 100 }
          }
        },
        createdAt: { bsonType: 'date' }
      }
    }
  }
});

db.createCollection('events', {
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['name', 'type', 'status', 'route', 'createdAt'],
      properties: {
        name: { bsonType: 'string', minLength: 3, maxLength: 100 },
        type: { enum: ['point-to-point', 'loop', 'brevet'] },
        status: { enum: ['planned', 'started', 'finished'] },
        route: {
          bsonType: 'object',
          required: ['name', 'distance', 'startPoint', 'endPoint'],
          properties: {
            name: { bsonType: 'string' },
            distance: { bsonType: 'number', minimum: 1 },
            startPoint: {
              bsonType: 'object',
              required: ['latitude', 'longitude'],
              properties: {
                latitude: { bsonType: 'number', minimum: -90, maximum: 90 },
                longitude: { bsonType: 'number', minimum: -180, maximum: 180 }
              }
            },
            endPoint: {
              bsonType: 'object',
              required: ['latitude', 'longitude'],
              properties: {
                latitude: { bsonType: 'number', minimum: -90, maximum: 90 },
                longitude: { bsonType: 'number', minimum: -180, maximum: 180 }
              }
            }
          }
        },
        createdAt: { bsonType: 'date' }
      }
    }
  }
});

db.createCollection('positions');
db.createCollection('participants');

// Create indexes for performance
print('Creating indexes...');

// Cyclists indexes
db.cyclists.createIndex({ "name": 1 });
db.cyclists.createIndex({ "createdAt": -1 });

// Events indexes
db.events.createIndex({ "status": 1 });
db.events.createIndex({ "startTime": 1 });
db.events.createIndex({ "createdAt": -1 });

// Positions indexes (for real-time queries)
db.positions.createIndex({ "cyclistId": 1, "timestamp": -1 });
db.positions.createIndex({ "eventId": 1, "timestamp": -1 });
db.positions.createIndex({ "eventId": 1, "cyclistId": 1, "timestamp": -1 });

// Participants indexes
db.participants.createIndex({ "eventId": 1 });
db.participants.createIndex({ "cyclistId": 1 });
db.participants.createIndex({ "eventId": 1, "status": 1 });

// Insert sample data for development
print('Inserting sample data...');

// Sample cyclists
const sampleCyclists = [
  {
    name: "Alice Champion",
    capabilities: {
      pma: 350,
      physicalEndurance: 85,
      sleepResistance: 70,
      mentalResilience: 90
    },
    state: {
      energyReserve: 100,
      muscularFatigue: 0,
      cerebralFatigue: 0,
      hydration: 100
    },
    createdAt: new Date()
  },
  {
    name: "Bob Endurance",
    capabilities: {
      pma: 320,
      physicalEndurance: 95,
      sleepResistance: 85,
      mentalResilience: 75
    },
    state: {
      energyReserve: 100,
      muscularFatigue: 0,
      cerebralFatigue: 0,
      hydration: 100
    },
    createdAt: new Date()
  },
  {
    name: "Charlie Speed",
    capabilities: {
      pma: 380,
      physicalEndurance: 70,
      sleepResistance: 60,
      mentalResilience: 80
    },
    state: {
      energyReserve: 100,
      muscularFatigue: 0,
      cerebralFatigue: 0,
      hydration: 100
    },
    createdAt: new Date()
  }
];

db.cyclists.insertMany(sampleCyclists);

// Sample event - 100km route from Paris to Chartres
const sampleEvent = {
  name: "Paris-Chartres Classic",
  type: "point-to-point",
  status: "planned",
  route: {
    name: "Paris to Chartres",
    distance: 100,
    startPoint: {
      latitude: 48.8566,
      longitude: 2.3522
    },
    endPoint: {
      latitude: 48.4469,
      longitude: 1.4869
    },
    waypoints: [
      {
        position: { latitude: 48.8566, longitude: 2.3522 },
        distance: 0,
        elevation: 35
      },
      {
        position: { latitude: 48.7500, longitude: 2.1500 },
        distance: 25,
        elevation: 150
      },
      {
        position: { latitude: 48.6500, longitude: 1.9000 },
        distance: 50,
        elevation: 120
      },
      {
        position: { latitude: 48.5500, longitude: 1.7000 },
        distance: 75,
        elevation: 180
      },
      {
        position: { latitude: 48.4469, longitude: 1.4869 },
        distance: 100,
        elevation: 142
      }
    ]
  },
  participants: [],
  createdAt: new Date()
};

db.events.insertOne(sampleEvent);

print('VeloForge database initialization completed successfully!');
print('Sample data created:');
print('- 3 cyclists: Alice Champion, Bob Endurance, Charlie Speed');
print('- 1 event: Paris-Chartres Classic (100km)');