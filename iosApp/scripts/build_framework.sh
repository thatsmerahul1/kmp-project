#!/bin/bash

set -e

# Build the shared framework using Gradle
cd "${SRCROOT}/.."
./gradlew :shared:embedAndSignAppleFrameworkForXcode

# Copy the framework to the expected location
FRAMEWORK_SOURCE="${SRCROOT}/../shared/build/bin/ios*/debugFramework/shared.framework"
FRAMEWORK_DEST="${BUILT_PRODUCTS_DIR}/shared.framework"

if [ -d "$FRAMEWORK_DEST" ]; then
    rm -rf "$FRAMEWORK_DEST"
fi

# Find the correct framework based on architecture
if [[ "$PLATFORM_NAME" == "iphonesimulator" ]]; then
    if [[ "$ARCHS" == *"arm64"* ]]; then
        FRAMEWORK_PATH="${SRCROOT}/../shared/build/bin/iosSimulatorArm64/debugFramework/shared.framework"
    else
        FRAMEWORK_PATH="${SRCROOT}/../shared/build/bin/iosX64/debugFramework/shared.framework"
    fi
else
    FRAMEWORK_PATH="${SRCROOT}/../shared/build/bin/iosArm64/debugFramework/shared.framework"
fi

cp -R "$FRAMEWORK_PATH" "$FRAMEWORK_DEST"