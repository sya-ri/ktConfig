# Changelog

## v2.1.0 (SNAPSHOT)

### Added
- Added support for serialization of sealed classes and interfaces.
    - Added `discriminator` property to the `@KtConfig` annotation for handling sealed hierarchies.
- Added `FormattedColorSerializer#isSupportedAlpha` property to detect Minecraft version support for an alpha channel in colors.

### Changed
- Deprecated `@PathName` and replaced it with `@SerialName` for better consistency.
    - `@PathName` is scheduled to be removed in v2.2.0.
- Fixed `FormattedColorSerializer` to ignore alpha values of 255 (fully opaque) when encoding colors, treating them as if no alpha channel is specified.

### Fixed
- Fixed `FormattedColorSerializer` to ignore an alpha channel when encoding colors on Minecraft versions that don't support alpha transparency.

## v2.0.0

Initial release.
