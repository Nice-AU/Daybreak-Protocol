package daybreak;

import java.util.prefs.Preferences;

final class GameSession {
    static final double STARTING_LIGHT = 150.0;
    private static final Preferences PREFS = Preferences.userNodeForPackage(GameSession.class);
    private static final String HIGH_SCORE_KEY = "highScore";

    private double light;
    private int score;
    private int streak;
    private int solved;
    private int mistakes;
    private int hints;

    GameSession() {
        reset();
    }

    void reset() {
        light = STARTING_LIGHT;
        score = 0;
        streak = 0;
        solved = 0;
        mistakes = 0;
        hints = 0;
    }

    void tick(double seconds) {
        light = Math.max(0.0, light - seconds);
    }

    int solve(Puzzle puzzle) {
        solved++;
        streak++;
        int earned = puzzle.getReward() + (streak - 1) * 75 + (int) Math.round(light * 2.0);
        score += earned;
        light = Math.min(STARTING_LIGHT, light + 8.0);
        return earned;
    }

    void miss() {
        mistakes++;
        streak = 0;
        light = Math.max(0.0, light - 16.0);
    }

    void useHint() {
        hints++;
        streak = 0;
        light = Math.max(0.0, light - 10.0);
    }

    boolean saveHighScore() {
        if (score > getHighScore()) {
            PREFS.putInt(HIGH_SCORE_KEY, score);
            return true;
        }
        return false;
    }

    int getHighScore() {
        return PREFS.getInt(HIGH_SCORE_KEY, 0);
    }

    double getLight() {
        return light;
    }

    int getScore() {
        return score;
    }

    int getStreak() {
        return streak;
    }

    int getSolved() {
        return solved;
    }

    int getMistakes() {
        return mistakes;
    }

    int getHints() {
        return hints;
    }
}
