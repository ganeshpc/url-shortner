#!/bin/bash

# Production deployment script
set -e

echo "🚀 Starting production deployment..."

# Check if .env file exists
if [ ! -f .env ]; then
    echo "❌ .env file not found. Please copy .env.example to .env and configure."
    echo "💡 You can also use .env.prod as a template for production."
    exit 1
fi

# Load environment variables
source .env

# Validate required environment variables
required_vars=("DB_PASSWORD" "BASE_URL")
for var in "${required_vars[@]}"; do
    if [ -z "${!var}" ]; then
        echo "❌ Required environment variable $var is not set."
        exit 1
    fi
done

# Backup existing data (if any)
echo "💾 Creating backup..."
mkdir -p backups
if docker volume inspect urlshortener_postgres_data > /dev/null 2>&1; then
    docker run --rm -v urlshortener_postgres_data:/data -v $(pwd)/backups:/backup alpine tar czf /backup/postgres-backup-$(date +%Y%m%d_%H%M%S).tar.gz -C /data .
    echo "✅ Database backup created"
fi

# Build and start services
echo "📦 Building and starting services..."
docker-compose -f docker-compose.yml down
docker-compose -f docker-compose.yml build --no-cache
docker-compose -f docker-compose.yml up -d

# Wait for services to be healthy
echo "⏳ Waiting for services to be healthy..."
timeout=180
elapsed=0
while [ $elapsed -lt $timeout ]; do
    if docker-compose -f docker-compose.yml ps | grep -q "healthy"; then
        if [ $(docker-compose -f docker-compose.yml ps | grep "healthy" | wc -l) -eq 3 ]; then
            echo "✅ All services are healthy!"
            break
        fi
    fi
    sleep 5
    elapsed=$((elapsed + 5))
    echo "⏳ Waiting... ($elapsed/${timeout}s)"
done

if [ $elapsed -ge $timeout ]; then
    echo "❌ Services failed to become healthy within $timeout seconds"
    echo "📋 Service status:"
    docker-compose -f docker-compose.yml ps
    echo "📋 Recent logs:"
    docker-compose -f docker-compose.yml logs --tail=20
    exit 1
fi

# Run health checks
echo "🔍 Running health checks..."
backend_health=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health || echo "000")
frontend_health=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:3000/ || echo "000")

if [ "$backend_health" = "200" ] && [ "$frontend_health" = "200" ]; then
    echo "✅ Deployment successful!"
    echo "🌐 Backend: http://localhost:8080"
    echo "🌐 Frontend: http://localhost:3000"
    echo "📊 Health: http://localhost:8080/actuator/health"
else
    echo "❌ Health checks failed:"
    echo "   Backend: $backend_health (expected: 200)"
    echo "   Frontend: $frontend_health (expected: 200)"
    exit 1
fi

echo "🎉 Production deployment completed successfully!"
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
