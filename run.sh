#!/usr/bin/env bash
# Build and run the MotorPH Employee Application.
# Run from the motorph-app folder so the CSV files are found.
set -e

cd "$(dirname "$0")"

echo "Compiling sources..."
javac -d out $(find src -name "*.java")

echo "Starting MotorPH Employee Application..."
java -cp out app.MotorPHApp
