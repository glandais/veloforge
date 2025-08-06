#!/bin/bash

echo "🚴 Starting VeloForge MVP Development Environment"
echo "=============================================="

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker is not running. Please start Docker first."
    exit 1
fi

echo "📦 Starting infrastructure services..."
docker-compose up -d

echo "⏳ Waiting for services to be ready..."
sleep 10

echo "🏗️  Building backend..."
cd veloforge-back
mvn clean compile

echo "📦 Installing frontend dependencies..."
cd ../veloforge-front
npm install

echo "🔧 Generating API client..."
npm run generate:api

cd ..

echo ""
echo "✅ Setup complete! Services are running:"
echo "  - MongoDB: http://localhost:27017"
echo "  - Redis: localhost:6379"  
echo "  - Kafka: localhost:9092"
echo "  - MongoDB UI: http://localhost:8081"
echo "  - Redis UI: http://localhost:8082"
echo "  - Kafka UI: http://localhost:8083"
echo ""
echo "🚀 To start the full stack:"
echo ""
echo "Terminal 1 - Backend API:"
echo "  cd veloforge-back && mvn quarkus:dev"
echo ""
echo "Terminal 2 - Frontend (after backend is running):" 
echo "  cd veloforge-front && npm run dev"
echo ""
echo "📚 Once started, access:"
echo "  - Frontend: http://localhost:3000"
echo "  - API Documentation: http://localhost:8080/swagger-ui"
echo "  - API Endpoints: http://localhost:8080"