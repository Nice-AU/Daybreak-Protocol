# Daybreak Protocol

**Daybreak Protocol** is a timed JavaFX code-breaking game made for the DEV June Solstice Game Jam. Repair six symbolic Bombe relays before the solstice sun sets. Every puzzle teaches or applies an algorithmic idea connected to Alan Turing's legacy.

## Play

On this computer, double-click `run.bat`.

Controls:

- `1` to `4`: choose an answer
- `H`: request a hint at the cost of ten seconds
- `Enter`: begin or continue
- Mouse controls work throughout

Wrong answers cost sixteen seconds. Correct answers restore eight seconds and build a score streak. The remaining daylight changes the ending. A flawless run unlocks an animated chronological anomaly.

## Build And Test

The scripts use the installed Azul Zulu Java 11 JDK with JavaFX:

```bat
build.bat
test.bat
```

The runnable JAR is written to `build\DaybreakProtocol.jar`. Because JavaFX is bundled with the local JDK rather than the JAR, use `run.bat` for the simplest launch.

## Why It Fits The Jam

- **June solstice:** daylight is both the timer and the central visual language.
- **Alan Turing:** the player reasons through ciphers, XOR, contradiction-based elimination, compression, parity, and binary search.
- **June celebrations:** Pride and Juneteenth appear as archive transmissions about preserving identity and freedom. They are treated as signals worth protecting, not as obstacles or trivia.
- **Original game mechanics:** correct reasoning restores daylight; mistakes visibly push the sun toward the horizon; algorithmic explanations make every solved relay part of the narrative.

## Project Structure

```text
src/daybreak/           JavaFX application and game logic
resources/styles/       Visual theme
resources/assets/       Supplied animated art
tests/daybreak/         Dependency-free logic test
docs/                   Submission, recording, and design notes
```

## Submission Deadline

The official deadline is **June 21, 2026 at 11:59 PM PDT**. In Sydney, Australia, that is **June 22, 2026 at 4:59 PM AEST**.

## Credits And Transparency

Game concept, writing, puzzle design, UI implementation, and procedural solstice artwork were created for this jam entry on June 15, 2026 with assistance from OpenAI Codex.

The animated character and dinosaur assets in `resources/assets` were supplied by the project owner. See `docs/ASSET-CREDITS.md`. The entry should not claim the optional Google AI prize category unless a genuine Google AI integration is added and documented.
