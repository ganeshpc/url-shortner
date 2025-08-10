#!/bin/bash

# Development environment setup script
set -e

echo "🛠️ Setting up development environment..."

# Copy environment file if it doesn't exist
if [ ! -f .env ]; then
    echo "📋 Creating .env file from development template..."
    cp .env.dev .env
    echo "✅ .env file created. You can customize it if needed."
fi

# Build and start development services
echo "📦 Building and starting development services..."
docker-compose -f docker-compose.dev.yml down
docker-compose -f docker-compose.dev.yml build
docker-compose -f docker-compose.dev.yml up -d

# Wait for services to be ready
echo "⏳ Waiting for services to be ready..."
timeout=120
elapsed=0
while [ $elapsed -lt $timeout ]; do
    backend_ready=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health 2>/dev/null || echo "000")
    if [ "$backend_ready" = "200" ]; then
        echo "✅ Backend is ready!"
        break
    fi
    sleep 5
    elapsed=$((elapsed + 5))
    echo "⏳ Waiting for backend... ($elapsed/${timeout}s)"
done

if [ $elapsed -ge $timeout ]; then
    echo "❌ Backend failed to start within $timeout seconds"
    echo "📋 Service status:"
    docker-compose -f docker-compose.dev.yml ps
    echo "📋 Backend logs:"
    docker-compose -f docker-compose.dev.yml logs backend
    exit 1
fi

echo "✅ Development environment is ready!"
echo "🌐 Backend: http://localhost:8080"
echo "🌐 Frontend: http://localhost:3000"
echo "🔧 H2 Console: http://localhost:8080/h2-console"
echo "📊 Health: http://localhost:8080/actuator/health"
echo ""
echo "💡 To see logs: docker-compose -f docker-compose.dev.yml logs -f"
echo "💡 To stop: docker-compose -f docker-compose.dev.yml down"
