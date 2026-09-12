# Architecture

## Stack

- Kotlin + Jetpack Compose + Material 3
- MVVM + StateFlow + Room
- Gradle 9.3.1, JDK 17, KSP
- Package: com.aistudio.manufacturing.vrtqxk

## Key files

- data/model/Entities.kt — all entities
- data/database/AppDatabase.kt — DB + migrations
- data/dao/AppDao.kt — all DAOs
- data/repository/ManufacturingRepository.kt — business logic
- viewmodel/ManufacturingViewModel.kt — UI state
- ui/screens/MoreHubScreen.kt — settings hub
- ui/screens/InventoryScreen.kt — inventory with tabs
- ui/screens/ReadyGoodsScreen.kt — cut/sewing/ready
- ui/dialogs/QuickActionSheets.kt — quick actions
- ui/components/ManagementButtons.kt — shared buttons

## Database

- Version: 12
- Migrations inline in AppDatabase.kt
- Pattern: object : Migration(from, to) { override fun migrate(db) { db.execSQL(...) } }

## UI

- Persian RTL
- LocalCustomColors.current for theming
- Colors: AccentCyan, AccentIndigo, AccentBlue, AccentAmber, Status*
- Buttons: Management Add/Edit/Delete/Save/Cancel

## State

- collectAsStateWithLifecycle() everywhere
- items(list, key = { it.id }) for LazyColumn
