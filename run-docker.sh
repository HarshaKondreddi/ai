#!/bin/bash

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}🚀 Building and running AI application with Docker...${NC}"

# Check if Docker is running
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}❌ Docker is not running. Please start Docker and try again.${NC}"
    exit 1
fi

# Build and run with docker-compose
echo -e "${YELLOW}📦 Building and starting services...${NC}"
docker-compose up --build -d

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✅ Services started successfully!${NC}"
    echo -e "${YELLOW}📊 Application will be available at: http://localhost:8080${NC}"
    echo -e "${YELLOW}🏥 Health check: http://localhost:8080/actuator/health${NC}"
    echo -e "${YELLOW}🗄️  Cassandra is available at: localhost:9043${NC}"
    echo -e "${YELLOW}📋 To view logs: docker-compose logs -f${NC}"
    echo -e "${YELLOW}📋 To view app logs: docker-compose logs -f ai-app${NC}"
    echo -e "${YELLOW}🛑 To stop: docker-compose down${NC}"
    echo -e "${YELLOW}⏳ Waiting for services to be ready...${NC}"
    echo -e "${YELLOW}   This may take a few minutes for Cassandra to fully start...${NC}"
else
    echo -e "${RED}❌ Failed to start application${NC}"
    exit 1
fi 