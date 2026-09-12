#!/bin/bash
cd "$(git rev-parse --show-toplevel)"
OUT="docs/PROJECT_STATE.md"
BRANCH=$(git rev-parse --abbrev-ref HEAD)
COMMIT=$(git rev-parse --short HEAD)
MSG=$(git log -1 --pretty=%s)
NOW=$(date '+%Y-%m-%d %H:%M:%S')

cat > "$OUT" << EOF
# Project State

Generated: $NOW
Branch: $BRANCH
HEAD: $COMMIT
Last commit: $MSG

## Recent commits
$(git log --oneline -20)

## Build
https://github.com/runport/my-android-app/actions

## Database
$(grep -m1 "version = " app/src/main/java/com/example/data/database/AppDatabase.kt)

## Key files
- Entities.kt
- AppDatabase.kt
- AppDao.kt
- ManufacturingRepository.kt
- ManufacturingViewModel.kt
- MoreHubScreen.kt
- InventoryScreen.kt
- ManagementButtons.kt
EOF

echo "Updated: $OUT"
