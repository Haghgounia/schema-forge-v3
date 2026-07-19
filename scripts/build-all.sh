#!/usr/bin/env sh
set -eu
./mvnw clean verify
cd frontend
npm ci
npm run build
