#!/bin/bash

# Production deployment script
set -e

echo "🚀 Starting production deployment..."

# Check if .env file exists
if [ ! -f .env ]; then
    echo "❌ .env file not found. Please copy .env.example to .env and configure."
    exit 1
fi

# Build and start services
echo "📦 Building and starting services..."
docker-compose -f docker-compose.yml down
docker-compose -f docker-compose.yml build --no-cache
docker-compose -f docker-compose.yml up -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be healthy..."
sleep 30

# Check health
echo "🔍 Checking service health..."
docker-compose ps

# Run health checks
echo "🩺 Running health checks..."
curl -f http://localhost:8080/actuator/health || echo "❌ Backend health check failed"
curl -f http://localhost:3000/health || echo "❌ Frontend health check failed"

echo "✅ Deployment complete!"
echo "🌐 Frontend: http://localhost:3000"
echo "🔗 Backend: http://localhost:8080"
echo "📊 Backend Health: http://localhost:8080/actuator/health"
