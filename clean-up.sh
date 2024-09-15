#!/bin/bash

# Check if the required argument is provided
if [ "$#" -ne 1 ]; then
    echo "Usage: $0 <directory>"
    exit 1
fi

DIR="$1"

# Find directories matching the pattern and write to a temporary file
find "$DIR" -type d -name '*@tmp' -print > dirs_to_check.txt

# Loop through the found directories and remove if they exist
while IFS= read -r dir; do
    if [ -d "$dir" ]; then
        echo "Removing directory: $dir"
        rm -rf "$dir"
    else
        echo "Directory does not exist: $dir"
    fi
done < dirs_to_check.txt

# Clean up temporary file
rm -f dirs_to_check.txt