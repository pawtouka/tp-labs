#!/usr/bin/env bash
set -euo pipefail
cd -- "$(dirname -- "$0")"
if [[ -n "${JAVA_HOME:-}" ]]; then
    export PATH="$JAVA_HOME/bin:$PATH"
fi
command -v javac >/dev/null || { echo 'JDK 17+ is required (set JAVA_HOME).' >&2; exit 1; }
mkdir -p build/main build/test
find src/main/java -name '*.java' | sort > build/main-sources.txt
javac --release 17 -encoding UTF-8 -Xlint:all -Werror -d build/main @build/main-sources.txt
case "${1:-test}" in
    test)
        find src/test/java -name '*.java' | sort > build/test-sources.txt
        javac --release 17 -encoding UTF-8 -Xlint:all -Werror -cp build/main -d build/test @build/test-sources.txt
        java -cp build/main:build/test by.publishing.PublicationTest
        ;;
    run) java -cp build/main by.publishing.Main ;;
    build) ;;
    *) echo 'Usage: ./build.sh [build|test|run]' >&2; exit 2 ;;
esac
