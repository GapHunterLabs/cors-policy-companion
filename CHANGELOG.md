<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# CORS Policy Companion Changelog

## [Unreleased]

### Changed

- The rating prompt's local counter keeps one-way fingerprints of findings
  instead of their file paths, and deletes the list that earlier versions
  kept.
- `PRIVACY.md` describes the values the plugin keeps in the IDE's local
  settings.

## [0.2.3]

### Fixed

- A wildcard origin **pattern** with credentials
  (`allowedOriginPatterns("*")`) was explained as "invalid per the CORS
  spec, browsers reject it" -- false: Spring allows it, the requests
  work, and every origin is echoed back, so any website can make
  credentialed requests and read the responses. It now has its own
  warning saying exactly that; `@CrossOrigin(originPatterns = "*")` is
  covered too.
- A `*` inside an origin (`https://*.example.com`) was taken for the
  wildcard; only a literal `"*"` is reported now.
- `@CrossOrigin(value = "*", ...)` (the alias of `origins`) and Kotlin's
  positional `@CrossOrigin("*", ...)` were not checked.

## [0.2.2]

### Fixed

- Review/star CTA now links to this plugin's own Marketplace
  reviews page instead of the vendor's generic plugin list.

## [0.2.1]

### Fixed

- Marketplace listing (`plugin.xml`) still said "v0.1 only covers the
  declarative `@CrossOrigin` annotation, not a programmatic
  `CorsConfigurationSource`/`CorsRegistry` setup" -- stale since 0.2.0
  added exactly that (the `WebMvcConfigurer` fluent-chain detector).
  README already had the correct scope text; `plugin.xml` now matches.

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

[Unreleased]: https://github.com/GapHunterLabs/cors-policy-companion/compare/0.2.3...HEAD
[0.2.3]: https://github.com/GapHunterLabs/cors-policy-companion/compare/0.2.2...0.2.3
[0.2.2]: https://github.com/GapHunterLabs/cors-policy-companion/compare/0.2.1...0.2.2
[0.2.1]: https://github.com/GapHunterLabs/cors-policy-companion/compare/0.2.0...0.2.1
[0.2.0]: https://github.com/GapHunterLabs/cors-policy-companion/compare/0.1.1...0.2.0
[0.1.1]: https://github.com/GapHunterLabs/cors-policy-companion/compare/0.1.0...0.1.1
[0.1.0]: https://github.com/GapHunterLabs/cors-policy-companion/commits/0.1.0
