<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# CORS Policy Companion Changelog

## [Unreleased]

## [0.1.1]

### Added

- Review/star CTA: after 10 distinct real findings, a one-time
  notification asks whether to rate the plugin on Marketplace, with a
  permanent "Don't ask again" option. Standard mechanism used
  catalog-wide since 2026-08-24 (`CONSTITUTION.md` §7.2), rolled out
  to this plugin now.

## [0.1.0]

### Added

- Warning icon on any Java/Kotlin Spring `@CrossOrigin(origins = "*",
  allowCredentials = "true")` annotation -- forbidden by the CORS spec
  itself.
- 100% static PSI analysis, Java and Kotlin, no network calls, no
  telemetry. Free.

[Unreleased]: https://github.com/GapHunterLabs/cors-policy-companion/compare/0.1.1...HEAD
[0.1.1]: https://github.com/GapHunterLabs/cors-policy-companion/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/GapHunterLabs/cors-policy-companion/commits/0.1.0
