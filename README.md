# CORS Policy Companion

Warning icon on any Java/Kotlin Spring CORS configuration that combines
a wildcard origin with credentials — this exact combination is
forbidden by the CORS spec itself: browsers reject a wildcard origin
combined with credentials, so requests silently fail (or, worse, the
intent behind the configuration is misunderstood entirely). Catches
both forms Spring offers for the same footgun:

- The declarative `@CrossOrigin(origins = "*", allowCredentials =
  "true")` annotation (class- or method-level).
- The programmatic `WebMvcConfigurer` registration —
  `registry.addMapping(...).allowedOrigins("*").allowCredentials(true)`
  — regardless of the order the fluent calls appear in.

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

## Scope — stated honestly, not exhaustively

Covers `@CrossOrigin` and the single-statement fluent
`registry.addMapping(...)` chain. Does not cover a
`CorsConfigurationSource`/`UrlBasedCorsConfigurationSource` setup where
the wildcard origin and the credentials flag are set on separate
statements against a `CorsConfiguration` object built up over multiple
lines.

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
