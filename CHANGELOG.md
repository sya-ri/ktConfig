# Changelog

## v2.1.0 (SNAPSHOT)

### Added
- Added support for serialization of sealed classes and interfaces.
    - Added `discriminator` property to the `@KtConfig` annotation for handling sealed hierarchies.

### Changed
- Deprecated `@PathName` and replaced it with `@SerialName` for better consistency.
    - `@PathName` is scheduled to be removed in v2.2.0.

## v2.0.0

Initial release.
