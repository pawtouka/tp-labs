#!/usr/bin/env bash
set -euo pipefail
cd -- "$(dirname -- "$0")"
if [[ -n "${JAVA_HOME:-}" ]]; then export PATH="$JAVA_HOME/bin:$PATH"; fi
mkdir -p build/main build/test
find src/main/java -name '*.java' | sort > build/main-sources.txt
javac --release 17 -encoding UTF-8 -Xlint:all -Werror -d build/main @build/main-sources.txt
mode=${1:-test}
if (( $# )); then shift; fi
case "$mode" in
build) ;;
test)
find src/test/java -name '*.java' | sort > build/test-sources.txt
javac --release 17 -encoding UTF-8 -Xlint:all -Werror -cp build/main -d build/test @build/test-sources.txt
java -cp build/main:build/test by.text.TasksTest ;;
run) java -cp build/main by.text.Main "$@" ;;
*) exit 2 ;;
esac
