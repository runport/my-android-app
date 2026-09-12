# Conventions

## Patch workflow

1. Create Python script with --dry-run
2. Run dry-run first, review output
3. Run real script
4. git --no-pager diff
5. rm script.py
6. git add, commit, push
7. Wait for GitHub Actions GREEN

## Build

- No local Gradle — GitHub Actions only
- URL: https://github.com/runport/my-android-app/actions

## Pitfalls

- Nested LazyColumn inside items() -> crash. Extract screen.
- IconButton with commas in onClick -> use balanced-paren parser
- Room schema change -> bump version + add migration
- collectAsState -> use collectAsStateWithLifecycle
- items without key -> add key = { it.id }

## Commit format

type(scope): short description

Types: feat, fix, refactor, perf, chore, docs

## Colors

AccentCyan, AccentIndigo, AccentBlue, AccentAmber
StatusSuccess, StatusWarning, StatusDanger
