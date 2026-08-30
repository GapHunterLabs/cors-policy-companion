<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# CORS Policy Companion Changelog

## [Unreleased]

## [0.2.0]

### Added

- New detector: flags the programmatic `WebMvcConfigurer` CORS
  registration —
  `registry.addMapping(...).allowedOrigins("*").allowCredentials(true)`
  — the same spec-invalid wildcard-origin-plus-credentials combination
  as `@CrossOrigin`, just expressed as a fluent builder chain instead
  of an annotation. Order of the fluent calls doesn't matter, and
  `allowedOriginPatterns("*")` is covered too. Java and Kotlin.

## [0.1.1]

### Added

- Review/star CTA: after 10 distinct real findings, a one-time
  notification asks whether to rate the plugin on Marketplace, with a
  permanent "Don't ask again" option. Standard mechanism used
  catalog-wide since 2026-08-24, rolled out
  to this plugin now.

## [0.1.0]

### Added

- Warning icon on any Java/Kotlin Spring `@CrossOrigin(origins = "*",
  allowCredentials = "true")` annotation -- forbidden by the CORS spec
  itself.
- 100% static PSI analysis, Java and Kotlin, no network calls, no
  telemetry. Free.

[Unreleased]: https://github.com/GapHunterLabs/cors-policy-companion/compare/0.2.0...HEAD
[0.2.0]: https://github.com/GapHunterLabs/cors-policy-companion/compare/0.1.1...0.2.0
[0.1.1]: https://github.com/GapHunterLabs/cors-policy-companion/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/GapHunterLabs/cors-policy-companion/commits/0.1.0
