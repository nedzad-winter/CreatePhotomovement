#!/bin/bash

# Exit on any error
set -e

if [ -z "$1" ]; then
    echo "Usage: ./build_mod.sh <version>"
    exit 1
fi

VERSION=$1
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )"
ROOT_DIR="$(dirname "$SCRIPT_DIR")"
BUILDS_DIR="$SCRIPT_DIR/builds"

# Ensure builds directory exists
mkdir -p "$BUILDS_DIR"

echo "Starting build for version: $VERSION"

# Function to run gradle build and copy artifacts
build_project() {
    local PROJECT_NAME=$1
    local PROJECT_PATH=$2
    local GRADLE_TASK=$3

    echo "Building $PROJECT_NAME..."
    
    cd "$ROOT_DIR"
    
    # Use ./gradlew if executable, otherwise sh gradlew
    if [ -x "./gradlew" ]; then
        ./gradlew "$GRADLE_TASK" -Pmod_version="$VERSION"
    else
        sh ./gradlew "$GRADLE_TASK" -Pmod_version="$VERSION"
    fi

    echo "Copying artifacts for $PROJECT_NAME..."
    cp "$ROOT_DIR/$PROJECT_PATH/build/libs/"*"$VERSION"*.jar "$BUILDS_DIR/" 2>/dev/null || echo "No jars found for $PROJECT_NAME"
}

# Build Forge 1.20.1
build_project "Forge 1.20.1" "forge/1201" ":forge:1201:build"

# Build NeoForge 1.20.1
build_project "NeoForge 1.20.1" "neoforge/1201" ":neoforge:1201:build"

# Build NeoForge 1.21.1
build_project "NeoForge 1.21.1" "neoforge/1211" ":neoforge:1211:build"

echo "All builds completed! Artifacts are in: $BUILDS_DIR"
