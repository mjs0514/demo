#!/bin/bash

echo "start"

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LIB_DIR="$SCRIPT_DIR/../libs"

jars=( "$LIB_DIR"/demo-apps-*.jar )

if [ ${#jars[@]} -eq 0 ]; then
    echo "No apps jar found in $LIB_DIR"
    exit 1
fi

if [ ${#jars[@]} -gt 1 ]; then
    echo "Multiple apps jars found:"
    printf '%s\n' "${jars[@]}"
    exit 1
fi

java -jar "${jars[0]}"