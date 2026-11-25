#!/usr/bin/env bash
set -euo pipefail
root=$(cd -- "$(dirname -- "$0")" && pwd)
if [[ -n "${JAVA_HOME:-}" ]]; then export PATH="$JAVA_HOME/bin:$PATH"; fi
mode=${1:-test}
if (( $# )); then shift; fi
mkdir -p "$root/build/main" "$root/build/test"
mapfile -d '' sources < <(find "$root/src/main/java" -name '*.java' -print0)
javac --release 17 -encoding UTF-8 -Xlint:all -Werror -d "$root/build/main" "${sources[@]}"
case "$mode" in
    build) ;;
    test)
        mapfile -d '' tests < <(find "$root/src/test/java" -name '*.java' -print0)
        javac --release 17 -encoding UTF-8 -Xlint:all -Werror -cp "$root/build/main" -d "$root/build/test" "${tests[@]}"
        java -cp "$root/build/main:$root/build/test" by.numbers.NumberFileTest
        ;;
    run) java -cp "$root/build/main" by.numbers.Main "$@" ;;
    *) printf 'Режимы сборки: build, test, run\n' >&2; exit 2 ;;
esac
