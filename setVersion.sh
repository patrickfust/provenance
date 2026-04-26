#!/bin/bash
set -euo pipefail

# Helper script to set project version in both pom.xml and jreleaser.yml

if [ "$#" -ne 1 ]; then
    echo "Usage: ./set-version.sh <new-version>"
    echo "Example: ./set-version.sh 1.0.0"
    exit 1
fi

NEW_VERSION="$1"

echo "[INFO] Setting version to $NEW_VERSION..."

echo "[INFO] Updating pom.xml..."
./mvnw -N -Pset-version versions:set -DnewVersion="$NEW_VERSION"
echo "[INFO] Successfully updated pom.xml files"

echo "[INFO] Updating jreleaser.yml..."
sed -i.bak "s/^  version: .*$/  version: $NEW_VERSION/" jreleaser.yml

if grep -q "^  version: $NEW_VERSION$" jreleaser.yml; then
    rm -f jreleaser.yml.bak
    echo "[INFO] Successfully updated jreleaser.yml"
    echo "[SUCCESS] Version set to $NEW_VERSION"
else
    echo "[ERROR] Failed to verify jreleaser.yml update"
    exit 1
fi
