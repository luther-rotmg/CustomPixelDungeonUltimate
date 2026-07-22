# Slice N Implementation Plan Template

Copy this file when starting a new Sub-B slice and rename to
`docs/superpowers/plans/YYYY-MM-DD-cpdu-sub-b-slice-N-<name>.md`.

## Acceptance block (mandatory on every Sub-B slice)

Every slice PR must show all of the following green before merge:

1. **Build passes:**
   `./gradlew android:assembleDebug && ./gradlew desktop:release` both exit 0
2. **Headless Android smoke-boot:**
   `pwsh -NoProfile -File services/tools/smoke-boot/smoke-boot.ps1` exits 0
3. **Save-roundtrip** (if the slice touches any bundle-serialized state):
   Load the pinned save fixture from `SPD-classes/src/test/resources/save-fixtures/`,
   play 10 turns via a headless run, save, reload, assert state matches.
   Bead template: `test(bridge): slice-N save roundtrip regression`.
4. **Marketplace-pack boot smoke:**
   `./gradlew packSmoke` exits 0.
   In Slices 1-6, "green" means no pack regressed from the prior slice.
   In Slice 7, "green" means all 30 packs pass.
5. **API-diff report:**
   `./gradlew apiDiff --args="--base <previous-slice-tag> --head HEAD"` exits 0.
   Any flagged removal must be documented in the slice PR description.
6. **CHANGELOG + PROJECT-STATUS updated** in the same commit as the slice's headline work.
7. **Enum-serialization audit** (Slice 2, 3a, 6c only):
   grep for `\.ordinal\(\)` in save-write sites confirms zero uses on serialized-to-Bundle enums.

Any slice PR missing proof of these gates does not merge.
