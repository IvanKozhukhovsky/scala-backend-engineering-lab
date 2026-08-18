#!/usr/bin/env python3
from pathlib import Path
import shutil
import subprocess
import sys

ROOT = Path(__file__).resolve().parents[1]


def run(command: list[str]) -> None:
    print("+", " ".join(command), flush=True)
    subprocess.run(command, cwd=ROOT, check=True)


def main() -> int:
    run([sys.executable, "scripts/progress.py", "check"])
    scala_cli = shutil.which("scala-cli") or shutil.which("scala")
    if scala_cli is None:
        print("Scala CLI is not installed or not available on PATH.", file=sys.stderr)
        return 2
    run([scala_cli, "fmt", "--check", "."])
    run([scala_cli, "test", "."])
    print("All local quality gates passed.")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except subprocess.CalledProcessError as exc:
        raise SystemExit(exc.returncode)
