# Contributing to Urmi Shield 🛡️

First off, thank you for considering contributing to Urmi Shield! It's people like you that make this zero-trust, privacy-first call protection engine possible for vulnerable users.

We want to make contributing to this project as easy and transparent as possible, whether it's:
- Reporting a bug
- Discussing the current state of the code
- Submitting a fix
- Proposing new features
- Becoming a maintainer

## Code of Conduct

We expect all participants to adhere to our [Code of Conduct](CODE_OF_CONDUCT.md). Please read it before interacting with this repository.

## Our Development Process

Urmi Shield uses a strict GitHub Flow. We use GitHub Actions for our CI pipeline to ensure that all commits pass our rigorous testing suites.

1.  **Fork** the repo on GitHub.
2.  **Clone** the project to your own machine.
3.  **Commit** changes to your own branch (`git checkout -b feature/amazing-feature`).
4.  **Push** your work back up to your fork.
5.  Submit a **Pull Request** so that we can review your changes.

> **Note:** All PRs must target the `master` branch.

## Pull Request Guidelines

Before you submit a Pull Request, please ensure the following:

- **Run Tests:** Ensure you run `./gradlew test` and that all unit tests pass. New features must include accompanying tests (minimum 80% coverage).
- **Format Code:** We follow the standard [ktlint](https://ktlint.github.io/) formatting. Ensure your code conforms to these Kotlin style guidelines.
- **Commit Messages:** Follow the [Conventional Commits](https://www.conventionalcommits.org/en/v1.0.0/) specification (e.g., `feat:`, `fix:`, `docs:`, `test:`).
- **Documentation:** If you add a new API or complex logic, you **must** document it using standard KDoc comments.

## Reporting Bugs

Bugs are tracked as GitHub issues. When creating an issue, please include:
- A quick summary and/or background
- Steps to reproduce
- What you expected would happen
- What actually happened
- Logs and/or traces if applicable
- Android OS Version & Device Manufacturer

## Security Vulnerabilities

**Do not open public issues for security exploits.**
If you discover a security vulnerability, please refer to our [Security Policy](SECURITY.md) for encrypted reporting instructions. Urmi Shield maintains a strict Zero-Network policy—any code proposing to add remote API or cloud database calls will be rejected.

## License

By contributing to Urmi Shield, you agree that your contributions will be licensed under its [Apache License 2.0](LICENSE).
