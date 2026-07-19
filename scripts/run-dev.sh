#!/usr/bin/env sh
set -eu
./mvnw spring-boot:run &
BACKEND_PID=$!
trap 'kill "$BACKEND_PID" 2>/dev/null || true' EXIT
cd frontend
npm run dev
