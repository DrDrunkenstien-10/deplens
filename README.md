# Deplens

Deplens is a command-line tool to help developers, security teams, and organizations quickly analyze project dependencies. It detects potential issues such as:

- Vulnerable dependencies
- License violations
- Outdated packages

Deplens is simple to use and easy to integrate into CI/CD pipelines. The current version supports Maven projects, with additional ecosystems planned.

## Key features

- Analyze Maven project dependencies
- Human-readable dependency analysis reports (useful for audits and compliance)
- Detect outdated dependencies and suggest latest versions
- Check for vulnerable dependencies
- Check for disallowed licenses
- Lightweight and fast; works on Ubuntu and macOS
- No heavy scanner installations required
- Open and extensible architecture (add new project types, rules, and output formats)

## Installation

Install on Ubuntu or macOS with a single command:

```bash
curl -L https://raw.githubusercontent.com/DrDrunkenstien-10/deplens/main/scripts/install.sh | bash
```

Verify installation:

```bash
deplens --help
```

## Usage

Note: Run Deplens from the Maven project root (where `pom.xml` is located).

Analyze a Maven project:

```bash
deplens --type maven
```

Fail analysis if specific licenses are found:

```bash
deplens --type maven --fail-on-license GPL-2.0,LGPL-3.0
```

## Exit codes

| Code | Meaning                         |
|-----:|---------------------------------|
| 0    | Analysis completed successfully |
| 2    | Unsupported project type        |
| 3    | I/O error                       |
| 4    | Interrupted                     |
| 5    | License violation detected      |
| 99   | Unexpected error                |

## Building from source

Requirements:

- Java 17 or newer
- Maven 3.8+

Build:

```bash
mvn clean package
```

Packaged CLI artifact:

```
target/deplens-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## Roadmap

- Add support for additional project types:
    - npm / Node.js (`package.json`)
    - Go modules (`go.mod`)
    - Python (`requirements.txt`, `pyproject.toml`)
    - Gradle
- Auto-detect ecosystem type without flags
- Enhanced reporting: JSON and Markdown output formats
- Custom vulnerability severity thresholds
- Open to recommendations and integrations

## Contributing

Contributions welcome — report issues, submit enhancements, add ecosystem support, or improve documentation. Please open an issue or submit a PR with clear descriptions. Follow the standard GitHub fork → branch → pull request workflow.
