#!/bin/bash
# commit_cleanup.sh — commit deletion of phase11.py and push
set -e

cd "$(git rev-parse --show-toplevel)"

GREEN='\033[0;32m'; YELLOW='\033[1;33m'; RED='\033[0;31m'; BLUE='\033[0;34m'; NC='\033[0m'
log()  { echo -e "${GREEN}✓${NC} $1"; }
warn() { echo -e "${YELLOW}⚠${NC} $1"; }
err()  { echo -e "${RED}✗${NC} $1"; }
info() { echo -e "${BLUE}ℹ${NC} $1"; }

echo "=== 1. Current state ==="
git status --short

echo
echo "=== 2. Stage deletion of phase11.py ==="
if [ -f phase11.py ]; then
    warn "phase11.py still exists on disk. Removing..."
    rm -f phase11.py
fi

git add -u phase11.py 2>/dev/null || true
git rm --cached phase11.py 2>/dev/null || true

# stage any other deletions
git add -u

echo
echo "=== 3. Staged changes ==="
git diff --cached --stat

echo
echo "=== 4. Commit ==="
if git diff --cached --quiet; then
    warn "Nothing staged — already clean."
else
    git commit -m "chore: remove phase scripts from repository"
    log "Committed."
fi

echo
echo "=== 5. Push ==="
git push origin feature/cutting-parts-workflow
log "Pushed."

echo
echo "=== 6. Final status ==="
git status
