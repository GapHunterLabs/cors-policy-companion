# CORS Policy Companion

Warning icon on any Java/Kotlin Spring `@CrossOrigin(origins = "*",
allowCredentials = "true")` annotation (class- or method-level) — this
exact combination is forbidden by the CORS spec itself: browsers reject
a wildcard origin combined with credentials, so requests silently fail
(or, worse, the intent behind the annotation is misunderstood
entirely).

## Why it exists

This specific combination is a well-known, spec-documented CORS
footgun — easy to write by copy-pasting `origins = "*"` from one
controller and `allowCredentials = "true"` from another without
realizing the combination is invalid, and the failure mode (a
mysterious browser-side rejection, not a clear server error) makes it
hard to diagnose after the fact.

## Why built this way

- **100% static PSI analysis** — matches the annotation by simple name
  only, works whether the real Spring jar is on the classpath or not.
  Java and Kotlin.
- **Always a real misconfiguration, never a judgment call.** Unlike
  many "security smell" checks, this one has zero false-positive risk
  by design — the CORS spec itself forbids the combination, so there's
  no legitimate reason it would ever be intentional.

## v0.1 scope — stated honestly, not exhaustively

Only covers the declarative `@CrossOrigin` annotation, not a
programmatic `CorsConfigurationSource`/`CorsRegistry` setup built
across multiple statements.

## Usage

Open any Java/Kotlin Spring controller. The invalid combination shows a
warning icon on the annotation.

## Enterprise / Team Licensing

Need enterprise features, custom rules, or team licensing? Contact us at
**gaphunterlabs@gmail.com**.

## Development

```
./gradlew test           # unit tests
./gradlew buildPlugin    # generates build/distributions/*.zip
./gradlew verifyPlugin   # checks compatibility against real IDEs
```

## License

Apache-2.0. See `LICENSE`.
