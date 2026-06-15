package daybreak;

import java.util.List;

public final class LogicTest {
    public static void main(String[] args) {
        List<Puzzle> puzzles = PuzzleLibrary.create();
        require(puzzles.size() == 6, "Expected six puzzles");
        for (Puzzle puzzle : puzzles) {
            require(puzzle.getOptions().size() == 4, puzzle.getTitle() + " must have four options");
            require(puzzle.getAnswerIndex() >= 0 && puzzle.getAnswerIndex() < 4,
                    puzzle.getTitle() + " has an invalid answer");
            require(!puzzle.getHint().isEmpty(), puzzle.getTitle() + " needs a hint");
            require(!puzzle.getExplanation().isEmpty(), puzzle.getTitle() + " needs an explanation");
        }

        GameSession session = new GameSession();
        double start = session.getLight();
        session.tick(1.5);
        require(close(session.getLight(), start - 1.5), "Clock should consume light");
        session.miss();
        require(close(session.getLight(), start - 17.5), "Mistake should consume sixteen seconds");
        session.useHint();
        require(close(session.getLight(), start - 27.5), "Hint should consume ten seconds");
        int earned = session.solve(puzzles.get(0));
        require(earned > 0 && session.getSolved() == 1, "Solving should award score and progress");
        require(session.getLight() <= GameSession.STARTING_LIGHT, "Light cannot exceed starting maximum");

        System.out.println("All Daybreak Protocol logic checks passed.");
    }

    private static boolean close(double left, double right) {
        return Math.abs(left - right) < 0.0001;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
