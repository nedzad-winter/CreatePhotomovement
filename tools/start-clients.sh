#!/bin/bash

# Start all Minecraft mod clients for testing
# This script launches all available mod versions in separate terminals

# Get script directory and project root
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"

# Parse arguments
FABRIC=false
NEOFORGE=false
ALL=false

for arg in "$@"; do
    case $arg in
        -fabric|--fabric)
            FABRIC=true
            ;;
        -neoforge|--neoforge)
            NEOFORGE=true
            ;;
        -all|--all)
            ALL=true
            ;;
        -h|--help)
            echo "Usage: $0 [options]"
            echo ""
            echo "Options:"
            echo "  -all, --all           Start all clients (default)"
            echo "  -fabric, --fabric     Start only Fabric clients"
            echo "  -neoforge, --neoforge Start only NeoForge clients"
            echo "  -h, --help            Show this help message"
            exit 0
            ;;
    esac
done

# If no flags specified, default to All
if ! $FABRIC && ! $NEOFORGE && ! $ALL; then
    ALL=true
fi

echo "========================================"
echo "  Create Photomovement - Client Launcher"
echo "========================================"
echo ""

LAUNCHED_COUNT=0

# Function to launch a client
launch_client() {
    local name="$1"
    local path="$2"
    local flag="$3"
    
    local should_launch=false
    
    if $ALL; then
        should_launch=true
    elif $FABRIC && [ "$flag" = "Fabric" ]; then
        should_launch=true
    elif $NEOFORGE && [ "$flag" = "NeoForge" ]; then
        should_launch=true
    fi
    
    if $should_launch; then
        if [ -d "$path" ]; then
            echo "Starting $name..."
            
            # Detect terminal emulator and launch accordingly
            if command -v gnome-terminal &> /dev/null; then
                gnome-terminal --title="$name" -- bash -c "cd '$path' && echo 'Launching $name...' && ./gradlew runClient; exec bash"
            elif command -v konsole &> /dev/null; then
                konsole --new-tab -e bash -c "cd '$path' && echo 'Launching $name...' && ./gradlew runClient; exec bash" &
            elif command -v xterm &> /dev/null; then
                xterm -title "$name" -e "cd '$path' && echo 'Launching $name...' && ./gradlew runClient; exec bash" &
            elif command -v Terminal &> /dev/null; then
                # macOS Terminal
                osascript -e "tell application \"Terminal\" to do script \"cd '$path' && echo 'Launching $name...' && ./gradlew runClient\""
            elif [[ "$OSTYPE" == "msys" ]] || [[ "$OSTYPE" == "cygwin" ]]; then
                # Git Bash on Windows
                start bash -c "cd '$path' && echo 'Launching $name...' && ./gradlew runClient; exec bash"
            else
                echo "Warning: Could not detect terminal emulator. Running in background..."
                (cd "$path" && ./gradlew runClient) &
            fi
            
            ((LAUNCHED_COUNT++))
            
            # Small delay between launches
            sleep 2
        else
            echo "Skipping $name - Path not found: $path"
        fi
    fi
}

# Launch all clients
launch_client "Fabric 1.20.1" "$PROJECT_ROOT/fabric/1201" "Fabric"
launch_client "NeoForge 1.20.1" "$PROJECT_ROOT/neoforge/1201" "NeoForge"
launch_client "NeoForge 1.21.1" "$PROJECT_ROOT/neoforge/1211" "NeoForge"

echo ""
echo "========================================"
echo "  Launched $LAUNCHED_COUNT client(s)"
echo "========================================"
echo ""
echo "Usage:"
echo "  ./start-clients.sh             # Start all clients"
echo "  ./start-clients.sh -all        # Start all clients"
echo "  ./start-clients.sh -fabric     # Start only Fabric clients"
echo "  ./start-clients.sh -neoforge   # Start only NeoForge clients"
echo ""
