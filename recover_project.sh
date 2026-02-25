#!/bin/bash
# Urmi Shield Recovery Script
# Run this to recreate the project structure if the download fails.

echo "Recreating Urmi Shield Project Structure..."

mkdir -p app/src/main/java/com/urmilabs/shield/service
mkdir -p app/src/main/java/com/urmilabs/shield/ai
mkdir -p app/src/main/java/com/urmilabs/shield/db
mkdir -p app/src/main/java/com/urmilabs/shield/ui/screens
mkdir -p app/src/main/java/com/urmilabs/shield/ui/theme
mkdir -p app/src/main/java/com/urmilabs/shield/worker
mkdir -p app/src/main/res/values
mkdir -p app/src/main/res/xml
mkdir -p app/src/main/res/drawable
mkdir -p app/src/main/res/layout
mkdir -p app/src/main/assets

echo "Project structure created."
echo "Please use the provided source code to populate the files."
