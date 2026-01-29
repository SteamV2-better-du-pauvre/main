#!/bin/bash
# run_all.sh
# Script to build schemas and run all 3 apps.

set -e # Exit immediately if a command exits with a non-zero status.

# Check if we are in the root directory
if [ ! -f "settings.gradle.kts" ]; then
    echo "Error: Please run this script from the project root directory."
    exit 1
fi

echo "=========================================="
echo "    SteamV2 Better du Pauvre - Run All    "
echo "=========================================="

echo "Step 1: Compiling Avro Schemas and Building schema-lib..."
# Run the build task for schema-lib. This will generate avro sources and create the JAR.
# We use the root wrapper.
./gradlew :schema-lib:build

if [ $? -eq 0 ]; then
   echo "✅ schema-lib built successfully."
else
   echo "❌ Failed to build schema-lib."
   exit 1
fi

echo "Step 2: Starting Applications..."

# Array to store background process IDs
PIDS=()

# Function to run an app
run_app() {
    local app_dir=$1
    echo "🚀 Starting $app_dir..."
    # Running in a subshell so cd doesn't affect main script
    # We use the gradle wrapper inside the app directory as requested/observed
    (cd "$app_dir" && ./gradlew run) &
    # Store the PID
    PIDS+=($!)
}

# Start the 3 apps
run_app "platform"
run_app "game-editor"
run_app "player"

echo "All applications are starting in the background."
echo "Press Ctrl+C to stop all applications."

# Handler to kill all background processes on exit
cleanup() {
    echo ""
    echo "Stopping all applications..."
    if [ ${#PIDS[@]} -gt 0 ]; then
        kill ${PIDS[*]} 2>/dev/null
    fi
    exit
}

trap cleanup SIGINT SIGTERM EXIT

# Wait for all background processes
wait
