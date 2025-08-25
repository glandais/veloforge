#!/bin/bash

echo "🚴 Starting VeloForge MVP..."

# Start infrastructure services
echo "Starting Docker services (MongoDB, Redis, Redpanda)..."
docker compose up -d

# Wait for services to be ready
echo "Waiting for services to be ready..."
sleep 2

# Check MongoDB is ready
until docker compose exec mongodb mongosh --eval "db.adminCommand('ping')" > /dev/null 2>&1; do
    echo "Waiting for MongoDB..."
    sleep 2
done
echo "✅ MongoDB is ready"

# Check Redis is ready
until docker compose exec redis redis-cli ping > /dev/null 2>&1; do
    echo "Waiting for Redis..."
    sleep 2
done
echo "✅ Redis is ready"

# Check Redpanda is ready
until docker compose exec redpanda rpk cluster health > /dev/null 2>&1; do
    echo "Waiting for Redpanda..."
    sleep 2
done
echo "✅ Redpanda is ready"

# Start backend
echo "Starting backend service..."
cd veloforge-back
./mvnw quarkus:dev &
BACKEND_PID=$!

# Wait for backend to be ready
echo "Waiting for backend to be ready..."
until curl -s http://localhost:8080/health > /dev/null 2>&1; do
    echo "Waiting for backend..."
    sleep 2
done
echo "✅ Backend is ready at http://localhost:8080"

# Start frontend
echo "Starting frontend..."
cd ../veloforge-front
npm run dev &
FRONTEND_PID=$!

echo ""
echo "✅ VeloForge MVP is running!"
echo ""
echo "🌐 Frontend: http://localhost:5173"
echo "🔧 Backend API: http://localhost:8080"
echo "📊 Swagger UI: http://localhost:8080/swagger-ui"
echo "🗄️ MongoDB UI: http://localhost:8081"
echo "🔴 Redis Commander: http://localhost:8082"
echo "📨 Redpanda Console: http://localhost:8083"
echo ""
echo "Press Ctrl+C to stop all services"

# Handle shutdown
trap 'echo "Shutting down..."; kill $BACKEND_PID $FRONTEND_PID; docker compose down; exit' INT

# Keep script running
wait