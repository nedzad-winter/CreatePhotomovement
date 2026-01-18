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

# Game profiles directory (optional - adjust path as needed)
GAME_PROFILES_DIR="$HOME/.minecraft/profiles"

# Mapping of project path to game profile folder name
declare -A GAME_PROFILE_MAPPING=(
    ["forge/1201"]="Dev-CreatePM-Forge-1201"
    ["neoforge/1201"]="Dev-CreatePM-NeoForge-1201"
    ["neoforge/1211"]="Dev-CreatePM-NeoForge-1211"
    ["fabric/1201"]="Dev-CreatePM-Fabric-1201"
)

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
    
    # Copy main JAR (not sources) to builds directory
    for jar in "$ROOT_DIR/$PROJECT_PATH/build/libs/"*"$VERSION"*.jar; do
        if [[ -f "$jar" && ! "$jar" == *"-sources"* ]]; then
            cp "$jar" "$BUILDS_DIR/"
            echo "  Copied to builds: $(basename "$jar")"
            
            # Copy to game profile if exists
            PROFILE_NAME="${GAME_PROFILE_MAPPING[$PROJECT_PATH]}"
            if [ -n "$PROFILE_NAME" ]; then
                GAME_MODS_DIR="$GAME_PROFILES_DIR/$PROFILE_NAME/mods"
                if [ -d "$GAME_MODS_DIR" ]; then
                    # Remove old versions of this mod
                    rm -f "$GAME_MODS_DIR/createphotomovement-"*.jar 2>/dev/null || true
                    # Copy new version
                    cp "$jar" "$GAME_MODS_DIR/"
                    echo "  Copied to game: $GAME_MODS_DIR"
                else
                    echo "  Game folder not found: $GAME_MODS_DIR (skipped)"
                fi
            fi
        fi
    done
}

# Build Forge 1.20.1
build_project "Forge 1.20.1" "forge/1201" ":forge:1201:build"

# Build NeoForge 1.20.1
build_project "NeoForge 1.20.1" "neoforge/1201" ":neoforge:1201:build"

# Build NeoForge 1.21.1
build_project "NeoForge 1.21.1" "neoforge/1211" ":neoforge:1211:build"

# Build Fabric 1.20.1
build_project "Fabric 1.20.1" "fabric/1201" ":fabric:1201:build"

echo ""
echo "All builds completed!"
echo "Artifacts are in: $BUILDS_DIR"

# List built files
echo ""
echo "Built JARs:"
for jar in "$BUILDS_DIR/"*"$VERSION"*.jar; do
    if [[ -f "$jar" && ! "$jar" == *"-sources"* ]]; then
        echo "  - $(basename "$jar")"
    fi
done
