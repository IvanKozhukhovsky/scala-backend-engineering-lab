# ENV is the process environment, not a CMD argument

The learner named exec-form `CMD ["java", "-jar", "desk.jar"]` as why `java` is PID 1, and that this image does not start Postgres. They first treated `DESK_CODE=GBP` as a program argument, then described `docker run --env` as an override. Ciris reads process env: the Dockerfile `ENV` is the default; `--env DESK_CODE=EUR` is what that container sees. `CMD`’s array is only the executable and its argv (`java`, `-jar`, `desk.jar`).

**Evidence**: retrieval after `ExercisesDay25Suite`; `packaging/desk/Dockerfile` `ENV` + exec-form `CMD`.

**Implications**: Do not re-teach image vs Postgres PID 1 unless they `FROM postgres`. If they put the code in `CMD` or `sys.argv`, ask whether Ciris `env("DESK_CODE")` reads argv. Compose and CI image builds wait.
