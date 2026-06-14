package daybreak;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class PuzzleLibrary {
    private PuzzleLibrary() {
    }

    static List<Puzzle> create() {
        return Collections.unmodifiableList(Arrays.asList(
                new Puzzle(
                        "TRANSMISSION 01 / DAWN",
                        "The Longest Shift",
                        "At sunrise, the Daybreak Engine catches a message displaced from its proper hour.",
                        "OLJKW",
                        "The dial reads SHIFT -3. Which plaintext restores the first ray?",
                        0,
                        "Move every letter three places backward through the alphabet.",
                        "A Caesar shift rotates each symbol by a fixed offset. OLJKW shifted back three places becomes LIGHT.",
                        500,
                        "LIGHT", "NIGHT", "DAWNS", "SOLAR"),
                new Puzzle(
                        "TRANSMISSION 02 / TURING",
                        "A Machine That Cancels",
                        "A relay marked A.M.T. hums awake. Its switches obey XOR: matching bits become 0; different bits become 1.",
                        "CIPHER 1111   XOR   KEY 1010",
                        "What four-bit plaintext emerges from the relay?",
                        1,
                        "Compare one position at a time. 1 XOR 1 is 0; 1 XOR 0 is 1.",
                        "XOR is reversible: ciphertext XOR key reveals plaintext. 1111 XOR 1010 equals 0101.",
                        650,
                        "1010", "0101", "1111", "0000"),
                new Puzzle(
                        "TRANSMISSION 03 / PRIDE",
                        "Every Color, Kept",
                        "The archive contains a spectrum signal. Compress it without erasing a single color or changing its order.",
                        "RRRRROOYYYGG",
                        "Which run-length encoding is lossless and shortest?",
                        2,
                        "Count each uninterrupted run, from left to right.",
                        "Run-length encoding records repeated runs as count plus symbol: five R, two O, three Y, two G.",
                        700,
                        "5R5O3Y2G", "RR5OO2YY3GG2", "5R2O3Y2G", "R5O2Y3G2"),
                new Puzzle(
                        "TRANSMISSION 04 / FREEDOM",
                        "The Unbroken Word",
                        "A June 19 archive beacon repeats one word. A single-bit error threatens to silence it; parity can expose the damage.",
                        "DATA 1011001   +   EVEN PARITY BIT ?",
                        "Which parity bit makes the total number of 1s even?",
                        0,
                        "Count the 1s already present. The parity bit must make that count even.",
                        "The data contains four 1s, already an even total, so the even-parity bit is 0.",
                        800,
                        "0", "1", "1010", "Cannot know"),
                new Puzzle(
                        "TRANSMISSION 05 / BOMBE",
                        "Contradiction Is a Compass",
                        "Thousands of rotor settings remain. Lumen proposes testing a crib: the plaintext word SUN can never encrypt to itself.",
                        "CANDIDATE A: SUN -> RAY\nCANDIDATE B: SUN -> SKY\nCANDIDATE C: SUN -> SUN\nCANDIDATE D: SUN -> LIT",
                        "Which candidate can the Bombe reject immediately?",
                        2,
                        "A key property of Enigma was that a letter could never encrypt to itself.",
                        "A contradiction eliminates an entire candidate setting. Since Enigma never mapped a letter to itself, SUN -> SUN is impossible.",
                        950,
                        "Candidate A", "Candidate B", "Candidate C", "Candidate D"),
                new Puzzle(
                        "TRANSMISSION 06 / SOLSTICE",
                        "The Turning Point",
                        "At sunset, the final search space stretches from 1 to 1,024. There is time for only ten yes-or-no tests.",
                        "FIND ONE HIDDEN SETTING AMONG 1,024",
                        "Which strategy guarantees finding it in at most ten tests?",
                        3,
                        "Each yes-or-no test can halve the remaining possibilities.",
                        "Binary search halves the space every test. Since 1,024 is 2^10, ten tests are sufficient. The turning point is the algorithm.",
                        1200,
                        "Check randomly", "Start at 1 and count", "Test only even settings", "Binary search")));
    }
}
