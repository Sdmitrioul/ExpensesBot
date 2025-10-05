#!/bin/bash

# Check if directory argument is provided
if [ -z "$1" ]; then
    echo "Error: No directory provided"
    echo "Usage: $0 <directory_path>"
    echo "Example: $0 /Users/dskroba/Local/personal/programming/ExpensesBot/data/db"
    exit 1
fi

VOLUME_NAME="postgres-data-expense-tracker-db"
MOUNT_DIR="$1"

# Convert to absolute path if relative path provided
if [[ "$MOUNT_DIR" != /* ]]; then
    MOUNT_DIR="$(cd "$(dirname "$MOUNT_DIR")" && pwd)/$(basename "$MOUNT_DIR")"
fi

echo "Volume name: $VOLUME_NAME"
echo "Mount directory: $MOUNT_DIR"

# Create directory if it doesn't exist
if [ ! -d "$MOUNT_DIR" ]; then
    echo "Creating directory: $MOUNT_DIR"
    mkdir -p "$MOUNT_DIR"
fi

# Set permissions
echo "Setting permissions on directory..."
chmod 755 "$MOUNT_DIR"

# Check if volume exists
if docker volume inspect "$VOLUME_NAME" &> /dev/null; then
    echo "Volume $VOLUME_NAME exists. Checking if it's in use..."

    # Check if volume is in use
    CONTAINERS_USING=$(docker ps -a --filter volume="$VOLUME_NAME" --format "{{.ID}}")

    if [ ! -z "$CONTAINERS_USING" ]; then
        echo "Stopping and removing containers using this volume..."
        echo "$CONTAINERS_USING" | xargs docker rm -f
    fi

    echo "Removing existing volume..."
    docker volume rm "$VOLUME_NAME"

    if [ $? -eq 0 ]; then
        echo "Volume removed successfully"
    else
        echo "Error: Failed to remove volume"
        exit 1
    fi
else
    echo "Volume does not exist, creating new one..."
fi

# Create the volume
echo "Creating volume with local directory binding..."
docker volume create \
    --driver local \
    --opt type=none \
    --opt device="$MOUNT_DIR" \
    --opt o=bind \
    "$VOLUME_NAME"

if [ $? -eq 0 ]; then
    echo "✓ Volume created successfully!"
    echo ""
    echo "Volume details:"
    docker volume inspect "$VOLUME_NAME"
else
    echo "✗ Failed to create volume"
    exit 1
fi