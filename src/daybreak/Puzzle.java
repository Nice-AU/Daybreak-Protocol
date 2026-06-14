package daybreak;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

final class Puzzle {
    private final String chapter;
    private final String title;
    private final String story;
    private final String transmission;
    private final String prompt;
    private final List<String> options;
    private final int answerIndex;
    private final String hint;
    private final String explanation;
    private final int reward;

    Puzzle(
            String chapter,
            String title,
            String story,
            String transmission,
            String prompt,
            int answerIndex,
            String hint,
            String explanation,
            int reward,
            String... options) {
        this.chapter = chapter;
        this.title = title;
        this.story = story;
        this.transmission = transmission;
        this.prompt = prompt;
        this.options = Collections.unmodifiableList(Arrays.asList(options));
        this.answerIndex = answerIndex;
        this.hint = hint;
        this.explanation = explanation;
        this.reward = reward;
    }

    String getChapter() {
        return chapter;
    }

    String getTitle() {
        return title;
    }

    String getStory() {
        return story;
    }

    String getTransmission() {
        return transmission;
    }

    String getPrompt() {
        return prompt;
    }

    List<String> getOptions() {
        return options;
    }

    int getAnswerIndex() {
        return answerIndex;
    }

    String getHint() {
        return hint;
    }

    String getExplanation() {
        return explanation;
    }

    int getReward() {
        return reward;
    }
}
