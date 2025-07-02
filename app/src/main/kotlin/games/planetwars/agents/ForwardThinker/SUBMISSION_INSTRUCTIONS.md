Thank you for providing your agent's path:  
F:\PlanetWarAIProject\app\src\main\kotlin\games\planetwars\agents\random\SmarterAgent.kt

---

## 1. Project Structure

Assuming your repo structure is similar to:

```
planet-wars-rts/
├── app/
│   └── src/
│       └── main/
│           └── kotlin/
│               └── games/
│                   └── planetwars/
│                       └── agents/
│                           └── random/
│                               └── SmarterAgent.kt
├── build.gradle.kts (or build.gradle)
├── settings.gradle.kts
└── Dockerfile      (to be created)
```

---

## 2. Dockerfile for Kotlin/JVM Agent

This Dockerfile will:
- Build the project using Gradle.
- Copy the resulting JAR.
- Run your agent using the JVM.

**Create a `Dockerfile` in the root of your repo:**

```dockerfile
# Stage 1: Build the project using Gradle
FROM gradle:8.5-jdk17 AS builder

WORKDIR /app

# Copy only necessary files for caching dependencies first
COPY build.gradle* settings.gradle* ./
COPY app/src ./app/src

# Build the agent JAR (adjust :app: for your actual Gradle project name if needed)
RUN gradle clean :app:build --no-daemon

# Stage 2: Minimal runtime image
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/app/build/libs/app.jar .

# Set the default command to run your agent
# If your main class is not set in the JAR manifest, specify it with -cp and -main
CMD ["java", "-jar", "app.jar"]
```

---

## 3. Ensure Your Gradle Build Produces a JAR

Your Gradle config (build.gradle.kts or build.gradle) should have something like:

```kotlin
application {
    mainClass.set("games.planetwars.agents.random.SmarterAgentKt") // Adjust if needed
}
```
Or, if using the standard `jar` task, ensure your manifest defines the main class.

---

## 4. Building and Running the Docker Image

From your project root (where the Dockerfile is):

```bash
docker build -t smarteragent .
docker run --rm smarteragent
```

You can pass arguments to your agent if required:

```bash
docker run --rm smarteragent --help
```

---

## 5. Example `submit_entry.md` Section

Add or update a section like this:

```markdown
## SmarterAgent Docker Instructions

### Build the image
docker build -t smarteragent .

### Run the agent
docker run --rm smarteragent
```

---

## 6. Notes

- If your agent is part of a multi-module Gradle project or not in `:app:`, adjust the build and JAR paths accordingly.
- Ensure your `build.gradle` produces a “fat” JAR (with dependencies), or use the Shadow plugin if needed.
- If your main class is different, update the `mainClass.set(...)` value.

---






------
# Planet Wars RTS Competition Submission Instructions

To submit your agent to the competition, follow these steps:

1. Go to the [Issues page of the competition repository](https://github.com/SimonLucas/planet-wars-rts-submissions/issues).
2. Click on “New issue”.
3. Title your issue clearly, e.g., `Submission: Smarteragent`.
4. In the issue body, include a YAML or JSON description like:

   ```yaml
   id: smarteragent
   repo_url: https://github.com/YOUR_GITHUB_USERNAME/YOUR_AGENT_REPO
   port: 9001
   commit: <commit_hash>  # optional
   ```

5. If your agent repo is private, add `@SimonLucas` as a collaborator with at least read access.