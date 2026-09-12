#!/bin/bash
cd "$(git rev-parse --show-toplevel)"
OUT="docs/PROJECT_STATE.md"
NOW=$(date '+%Y-%m-%d %H:%M:%S')
BRANCH=$(git rev-parse --abbrev-ref HEAD)
COMMIT=$(git rev-parse --short HEAD)
MSG=$(git log -1 --pretty=%s)

cat > "$OUT" << 'STATE_EOF'
# Project State

Generated: '$NOW'
Branch: '$BRANCH'
HEAD: '$COMMIT'
Last commit: '$MSG'

## Recent commits
$(git log --oneline -20)

## Build
https://github.com/runport/my-android-app/actions

## Database
$(grep -m1 'version = ' app/src/main/java/com/example/data/database/AppDatabase.kt)
STATE_EOF

echo "Updated: $OUT"
