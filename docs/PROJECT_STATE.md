# Project State

Generated: 2026-09-12 22:36:01
Branch: feature/cutting-parts-workflow
HEAD: 778fdac
Last commit: perf: cache enum.values() with remember in list-rendering hot paths

## Recent commits
778fdac perf: cache enum.values() with remember in list-rendering hot paths
11f6967 perf(morehub): add stable keys to items() for LazyColumn diffing
66ed5a1 feat(ready): add MoreSubSection switcher to ReadyGoods screen
c9537ec fix(morehub): prevent vertical button text breaking
2c302fd fix(inventory): unify tab borders for دسته‌های پارچه and ملزومات
a096b62 refactor(dialogs): complete ManagementButton migration
32ecc42 refactor(qas): use shared ManagementButtons for edit/delete
34b2d4c fix(morehub): correct ManagementButton migration (onClick extraction)
1bdbbad refactor(morehub): replace remaining IconButtons with shared components
e28ebd9 refactor(morehub): use shared ManagementButtons for edit/delete icons
a5f6d04 refactor(dialogs): use shared ManagementButtons components
095c91f refactor(inventory): use shared ManagementButtons components
3466821 fix: add missing Build icon import for inventory tabs
3d44e5b feat: redesign inventory category tabs (safe line-based)
fae8651 revert: restore InventoryScreen.kt (broken by tab redesign)
ae8b3e2 feat: redesign inventory category tabs + unified management buttons
05a93e7 chore: remove temp debug files and update .gitignore
54c7e23 fix: BackHandler for sub-sections, remove (فاز ۲), perf via collectAsStateWithLifecycle
4c2c449 fix: resolve nested-LazyColumn crash in ReadyGoods screen
661304a chore: remove phase scripts from repository

## Build
https://github.com/runport/my-android-app/actions

## Database
  version = 12,

## Key files
- Entities.kt
- AppDatabase.kt
- AppDao.kt
- ManufacturingRepository.kt
- ManufacturingViewModel.kt
- MoreHubScreen.kt
- InventoryScreen.kt
- ManagementButtons.kt
