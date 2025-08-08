#!/bin/sh
if command -v gradle >/dev/null 2>&1; then
  gradle "$@"
else
  echo "Gradle not found. Trying ./gradlew (wrapper) instead."
  ./gradlew "$@"
fi
