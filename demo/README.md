# Demo data for screenshots

`ApiController.java` — `getAccount` has the invalid wildcard+credentials
combo (should show a warning), `getOrder` has a specific origin
(should not).

## How to get the screenshot

1. `./gradlew runIde` from `cors-policy-companion`, open this `demo/`
   folder as the project.
2. Full Screen, open `ApiController.java` — a warning icon should
   appear on `getAccount` but not on `getOrder`.
3. Screenshot with both methods and the icon contrast visible, save
   into `cors-policy-companion/docs/screenshots/`. Close the sandbox.
