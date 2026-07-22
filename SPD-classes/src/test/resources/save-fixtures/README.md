# Save fixtures for BundleBridge tests

Fixtures pinned here reproduce specific CPD / SPD save formats that the bridge must handle.

## Currently pinned

- (Slice 0 ships without live fixtures; Task 9 adds `cpd-v2.1.0-1.0-sample.dat` from a playtest save.)

## Adding a new fixture

1. Play the game at the target version, save at a well-defined state.
2. Copy the save file into this directory with a descriptive name.
3. Add an `.expected.json` file describing the fields the upcast bridge should produce.
4. Add a test in `BundleBridgeTest.java` that loads the fixture and asserts the upcast result matches expectations.
