package day25

// Independent exercise for scala-025.
// Fill deskRuntime yourself. Then write packaging/desk/Dockerfile so the file
// equals dockerfileOf(deskRuntime) exactly (trailing newline included).
// repository: desk
// tag: 0.1.0
// base: eclipse-temurin:21-jre-alpine-3.24
// workdir: /app
// jar: desk.jar
// env: DESK_CODE=GBP
// cmd: java -jar desk.jar
// Do not alias quotesRuntime. Do not FROM postgres. Do not COPY source or .env.
// Do not install scala-cli. Do not bake a token or password into ENV.

/** Runtime recipe for the desk process. Building this does not talk to Docker. */
def deskRuntime: RuntimeImage =
  RuntimeImage(
    repository = "desk",
    tag = "0.1.0",
    base = "eclipse-temurin:21-jre-alpine-3.24",
    workdir = "/app",
    jar = "desk.jar",
    env = List("DESK_CODE" -> "GBP"),
    cmd = List("java", "-jar", "desk.jar")
  )
