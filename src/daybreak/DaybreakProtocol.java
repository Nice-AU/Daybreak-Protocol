package daybreak;

import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Separator;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.util.Duration;

public final class DaybreakProtocol extends Application {
    private final GameSession session = new GameSession();
    private final List<Puzzle> puzzles = PuzzleLibrary.create();
    private final List<Button> answerButtons = new ArrayList<Button>();
    private final List<Circle> rotorLights = new ArrayList<Circle>();

    private Stage stage;
    private StackPane root;
    private BorderPane gameView;
    private Timeline clock;
    private Timeline demoTimeline;
    private Timeline captureTimeline;
    private ExecutorService captureWriter;
    private int captureFrame;
    private SunDial sunDial;
    private Label chapterLabel;
    private Label titleLabel;
    private Label storyLabel;
    private Label transmissionLabel;
    private Label promptLabel;
    private Label feedbackLabel;
    private Label timerLabel;
    private Label scoreLabel;
    private Label streakLabel;
    private Label progressLabel;
    private ProgressBar lightBar;
    private ImageView reactionView;
    private Button hintButton;
    private Button nextButton;
    private int puzzleIndex;
    private boolean puzzleSolved;
    private boolean gameEnded;
    private boolean demoMode;

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;
        root = new StackPane();
        root.getStyleClass().add("app-root");

        Scene scene = new Scene(root, 1180, 760);
        String css = getClass().getResource("/styles/daybreak.css").toExternalForm();
        scene.getStylesheets().add(css);
        scene.setOnKeyPressed(event -> handleKey(event.getCode()));

        stage.setTitle("Daybreak Protocol");
        stage.getIcons().add(image("/assets/app-icon.png"));
        stage.setMinWidth(980);
        stage.setMinHeight(680);
        stage.setScene(scene);
        demoMode = getParameters().getRaw().contains("--demo");
        showTitleScreen();
        stage.show();
        if (demoMode) {
            startDemo();
        }
        for (String argument : getParameters().getRaw()) {
            if (argument.startsWith("--capture-dir=")) {
                startCapture(new File(argument.substring("--capture-dir=".length())));
            }
        }
    }

    private void showTitleScreen() {
        stopClock();
        gameEnded = true;
        root.getChildren().clear();

        SunDial titleSun = new SunDial();
        titleSun.setLightFraction(0.82);
        titleSun.setPrefHeight(260);
        titleSun.setMaxHeight(260);

        Label eyebrow = label("A SOLSTICE CODE-BREAKING GAME", "eyebrow");
        Label title = label("DAYBREAK\nPROTOCOL", "hero-title");
        Label subtitle = label(
                "Six transmissions. One turning point.\nRepair the Daybreak Engine before the longest day becomes night.",
                "hero-subtitle");
        subtitle.setTextAlignment(TextAlignment.CENTER);

        Label category = label("AN ODE TO ALAN TURING  /  JUNE SOLSTICE GAME JAM", "category-chip");
        Button start = button("BEGIN DECODING  [ENTER]", "primary-button");
        start.setOnAction(event -> startGame());

        ImageView landmark = imageView("/assets/archive-landmark.jpg", 210, 126);
        landmark.getStyleClass().add("archive-image");
        ImageView homeSignal = imageView("/assets/anomaly-home.gif", 210, 126);
        homeSignal.getStyleClass().add("archive-image");
        HBox artStrip = new HBox(14, landmark, homeSignal);
        artStrip.setAlignment(Pos.CENTER);

        Label rules = label(
                "Correct answers restore daylight. Mistakes cost 16 seconds. Hints cost 10.\n"
                        + "Use algorithmic thinking, not speed alone. Keys: 1-4, H, Enter.",
                "small-copy");
        rules.setTextAlignment(TextAlignment.CENTER);

        VBox card = new VBox(15, eyebrow, title, subtitle, artStrip, category, start, rules);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(32, 70, 40, 70));
        card.getStyleClass().add("hero-card");
        card.setMaxWidth(720);

        VBox layout = new VBox(0, titleSun, card);
        layout.setAlignment(Pos.CENTER);
        layout.setMaxWidth(960);
        root.getChildren().add(layout);
    }

    private void startGame() {
        session.reset();
        puzzleIndex = 0;
        puzzleSolved = false;
        gameEnded = false;
        buildGameView();
        root.getChildren().setAll(gameView);
        renderPuzzle();
        startClock();
    }

    private void buildGameView() {
        gameView = new BorderPane();
        gameView.getStyleClass().add("game-view");
        gameView.setTop(buildHeader());
        gameView.setLeft(buildLeftRail());
        gameView.setCenter(buildPuzzlePanel());
        gameView.setRight(buildRightRail());
        gameView.setBottom(buildFooter());
        BorderPane.setMargin(gameView.getCenter(), new Insets(18));
    }

    private Node buildHeader() {
        Label brand = label("DAYBREAK PROTOCOL", "brand");
        progressLabel = label("", "status-text");
        scoreLabel = label("", "status-text");
        streakLabel = label("", "status-text");

        HBox spacer = new HBox();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox header = new HBox(24, brand, spacer, progressLabel, scoreLabel, streakLabel);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 22, 15, 22));
        header.getStyleClass().add("top-bar");
        return header;
    }

    private Node buildLeftRail() {
        sunDial = new SunDial();
        sunDial.setPrefWidth(285);
        sunDial.setMaxHeight(185);

        timerLabel = label("02:30.0", "timer-label");
        lightBar = new ProgressBar(1.0);
        lightBar.setMaxWidth(Double.MAX_VALUE);
        lightBar.getStyleClass().add("light-bar");

        ImageView guide = imageView("/assets/lumen-guide.gif", 116, 116);
        guide.getStyleClass().add("portrait");
        Label guideName = label("LUMEN / ARCHIVE GUIDE", "eyebrow");
        Label guideText = label(
                "\"A contradiction is useful. It tells us where truth cannot be.\"",
                "guide-copy");
        guideText.setWrapText(true);

        VBox rail = new VBox(12,
                label("DAYLIGHT REMAINING", "eyebrow"),
                timerLabel,
                lightBar,
                sunDial,
                new Separator(),
                guide,
                guideName,
                guideText);
        rail.setAlignment(Pos.TOP_CENTER);
        rail.setPadding(new Insets(20));
        rail.setPrefWidth(305);
        rail.getStyleClass().add("side-rail");
        return rail;
    }

    private Node buildPuzzlePanel() {
        chapterLabel = label("", "eyebrow");
        titleLabel = label("", "puzzle-title");
        titleLabel.setWrapText(true);
        storyLabel = label("", "story-copy");
        storyLabel.setWrapText(true);
        transmissionLabel = label("", "transmission");
        transmissionLabel.setWrapText(true);
        transmissionLabel.setTextAlignment(TextAlignment.CENTER);
        transmissionLabel.setMaxWidth(Double.MAX_VALUE);
        promptLabel = label("", "prompt");
        promptLabel.setWrapText(true);

        VBox answers = new VBox(10);
        for (int i = 0; i < 4; i++) {
            final int answerIndex = i;
            Button answer = button("", "answer-button");
            answer.setMaxWidth(Double.MAX_VALUE);
            answer.setWrapText(true);
            answer.setOnAction(event -> answer(answerIndex));
            answerButtons.add(answer);
            answers.getChildren().add(answer);
        }

        reactionView = imageView("/assets/lumen-portrait.gif", 118, 68);
        reactionView.getStyleClass().add("reaction-image");
        feedbackLabel = label("Choose a decoding lead.", "feedback");
        feedbackLabel.setWrapText(true);
        feedbackLabel.setMinHeight(56);
        HBox feedbackBox = new HBox(14, reactionView, feedbackLabel);
        feedbackBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(feedbackLabel, Priority.ALWAYS);

        VBox panel = new VBox(12,
                chapterLabel,
                titleLabel,
                storyLabel,
                transmissionLabel,
                promptLabel,
                answers,
                feedbackBox);
        panel.setPadding(new Insets(28));
        panel.getStyleClass().add("puzzle-panel");
        return panel;
    }

    private Node buildRightRail() {
        VBox rotors = new VBox(14);
        rotors.setAlignment(Pos.TOP_CENTER);
        for (int i = 0; i < puzzles.size(); i++) {
            Circle light = new Circle(15, Color.web("#17223d"));
            light.setStroke(Color.web("#65708f"));
            light.setStrokeWidth(2);
            rotorLights.add(light);
            Label number = label(String.format("%02d", i + 1), "rotor-number");
            StackPane rotor = new StackPane(light, number);
            rotors.getChildren().add(rotor);
        }

        Label objective = label(
                "Restore every relay before the sun crosses the horizon. Each solved transmission illuminates one rotor.",
                "guide-copy");
        objective.setWrapText(true);
        objective.setTextAlignment(TextAlignment.CENTER);
        ImageView eventSignal = imageView("/assets/anomaly-event.png", 160, 105);
        eventSignal.getStyleClass().add("mission-image");

        VBox rail = new VBox(16,
                label("BOMBE RELAYS", "eyebrow"),
                rotors,
                new Separator(),
                label("MISSION", "eyebrow"),
                eventSignal,
                objective);
        rail.setAlignment(Pos.TOP_CENTER);
        rail.setPadding(new Insets(24, 18, 18, 18));
        rail.setPrefWidth(190);
        rail.getStyleClass().add("side-rail");
        return rail;
    }

    private Node buildFooter() {
        hintButton = button("REQUEST HINT  -10s  [H]", "secondary-button");
        hintButton.setOnAction(event -> showHint());
        nextButton = button("NEXT TRANSMISSION  [ENTER]", "primary-button");
        nextButton.setDisable(true);
        nextButton.setOnAction(event -> nextPuzzle());
        Button restart = button("RESTART", "ghost-button");
        restart.setOnAction(event -> startGame());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        HBox footer = new HBox(12, restart, spacer, hintButton, nextButton);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(14, 22, 14, 22));
        footer.getStyleClass().add("bottom-bar");
        return footer;
    }

    private void renderPuzzle() {
        Puzzle puzzle = puzzles.get(puzzleIndex);
        puzzleSolved = false;
        chapterLabel.setText(puzzle.getChapter());
        titleLabel.setText(puzzle.getTitle());
        storyLabel.setText(puzzle.getStory());
        transmissionLabel.setText(puzzle.getTransmission());
        promptLabel.setText(puzzle.getPrompt());
        feedbackLabel.setText("Choose a decoding lead. A wrong lead burns daylight.");
        reactionView.setImage(image("/assets/lumen-portrait.gif"));
        feedbackLabel.getStyleClass().removeAll("feedback-good", "feedback-bad", "feedback-hint");
        hintButton.setDisable(false);
        nextButton.setDisable(true);

        for (int i = 0; i < answerButtons.size(); i++) {
            Button answer = answerButtons.get(i);
            answer.setText((i + 1) + "  /  " + puzzle.getOptions().get(i));
            answer.setDisable(false);
            answer.getStyleClass().removeAll("answer-correct", "answer-wrong");
        }
        updateStatus();
    }

    private void answer(int answerIndex) {
        if (gameEnded || puzzleSolved) {
            return;
        }
        Puzzle puzzle = puzzles.get(puzzleIndex);
        Button selected = answerButtons.get(answerIndex);
        if (answerIndex == puzzle.getAnswerIndex()) {
            puzzleSolved = true;
            int earned = session.solve(puzzle);
            selected.getStyleClass().add("answer-correct");
            answerButtons.forEach(button -> button.setDisable(true));
            hintButton.setDisable(true);
            nextButton.setDisable(false);
            feedbackLabel.getStyleClass().removeAll("feedback-bad", "feedback-hint");
            feedbackLabel.getStyleClass().add("feedback-good");
            feedbackLabel.setText("RELAY RESTORED  +" + earned + "\n" + puzzle.getExplanation());
            reactionView.setImage(image("/assets/anomaly-success.gif"));
            illuminateRotor(puzzleIndex);
        } else {
            session.miss();
            selected.setDisable(true);
            selected.getStyleClass().add("answer-wrong");
            feedbackLabel.getStyleClass().removeAll("feedback-good", "feedback-hint");
            feedbackLabel.getStyleClass().add("feedback-bad");
            feedbackLabel.setText("CONTRADICTION FOUND  -16s\nThat lead collapses. Try another.");
            reactionView.setImage(image("/assets/anomaly-error.gif"));
            if (session.getLight() <= 0.0) {
                finishGame(false);
            }
        }
        updateStatus();
    }

    private void showHint() {
        if (gameEnded || puzzleSolved || hintButton.isDisabled()) {
            return;
        }
        session.useHint();
        hintButton.setDisable(true);
        feedbackLabel.getStyleClass().removeAll("feedback-good", "feedback-bad");
        feedbackLabel.getStyleClass().add("feedback-hint");
        feedbackLabel.setText("LUMEN'S HINT  -10s\n" + puzzles.get(puzzleIndex).getHint());
        reactionView.setImage(image("/assets/lumen-guide.gif"));
        updateStatus();
        if (session.getLight() <= 0.0) {
            finishGame(false);
        }
    }

    private void nextPuzzle() {
        if (gameEnded || !puzzleSolved) {
            return;
        }
        if (puzzleIndex == puzzles.size() - 1) {
            finishGame(true);
        } else {
            puzzleIndex++;
            renderPuzzle();
        }
    }

    private void illuminateRotor(int index) {
        Circle rotor = rotorLights.get(index);
        rotor.setFill(Color.web("#79f2c0"));
        rotor.setStroke(Color.web("#e6fff6"));
        rotor.setEffect(new DropShadow(18, Color.web("#79f2c0")));
    }

    private void startClock() {
        stopClock();
        clock = new Timeline(new KeyFrame(Duration.millis(100), event -> {
            if (!gameEnded) {
                session.tick(0.1);
                updateStatus();
                if (session.getLight() <= 0.0) {
                    finishGame(false);
                }
            }
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    private void startDemo() {
        demoTimeline = new Timeline(
                new KeyFrame(Duration.seconds(1.5), event -> startGame()),
                new KeyFrame(Duration.seconds(3.5), event -> answer(1)),
                new KeyFrame(Duration.seconds(4.8), event -> answer(0)),
                new KeyFrame(Duration.seconds(6.5), event -> nextPuzzle()),
                new KeyFrame(Duration.seconds(8.0), event -> showHint()),
                new KeyFrame(Duration.seconds(9.5), event -> answer(1)),
                new KeyFrame(Duration.seconds(11.0), event -> nextPuzzle()),
                new KeyFrame(Duration.seconds(12.5), event -> answer(2)),
                new KeyFrame(Duration.seconds(14.0), event -> nextPuzzle()),
                new KeyFrame(Duration.seconds(15.5), event -> answer(0)),
                new KeyFrame(Duration.seconds(17.0), event -> nextPuzzle()),
                new KeyFrame(Duration.seconds(18.5), event -> answer(2)),
                new KeyFrame(Duration.seconds(20.0), event -> nextPuzzle()),
                new KeyFrame(Duration.seconds(21.5), event -> answer(3)),
                new KeyFrame(Duration.seconds(23.5), event -> nextPuzzle()));
        demoTimeline.play();
    }

    private void startCapture(File directory) {
        directory.mkdirs();
        captureFrame = 0;
        captureWriter = Executors.newSingleThreadExecutor();
        captureTimeline = new Timeline(new KeyFrame(Duration.millis(333), event -> {
            WritableImage frame = new WritableImage(
                    Math.max(1, (int) Math.round(stage.getScene().getWidth())),
                    Math.max(1, (int) Math.round(stage.getScene().getHeight())));
            stage.getScene().snapshot(frame);
            File output = new File(directory, String.format("frame-%04d.png", captureFrame++));
            BufferedImage bufferedFrame = SwingFXUtils.fromFXImage(frame, null);
            captureWriter.submit(() -> writeFrame(bufferedFrame, output));
        }));
        captureTimeline.setCycleCount(81);
        captureTimeline.setOnFinished(event -> {
            captureWriter.shutdown();
            Thread waiter = new Thread(() -> {
                try {
                    captureWriter.awaitTermination(5, TimeUnit.MINUTES);
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                }
                Platform.runLater(Platform::exit);
            }, "daybreak-capture-waiter");
            waiter.setDaemon(true);
            waiter.start();
        });
        captureTimeline.play();
    }

    private void writeFrame(BufferedImage frame, File output) {
        try {
            ImageIO.write(frame, "png", output);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to write demo frame " + output, exception);
        }
    }

    private void stopClock() {
        if (clock != null) {
            clock.stop();
        }
    }

    private void updateStatus() {
        double light = session.getLight();
        int totalTenths = (int) Math.ceil(light * 10.0);
        int minutes = totalTenths / 600;
        int seconds = (totalTenths / 10) % 60;
        int tenths = totalTenths % 10;
        timerLabel.setText(String.format("%02d:%02d.%d", minutes, seconds, tenths));
        lightBar.setProgress(light / GameSession.STARTING_LIGHT);
        sunDial.setLightFraction(light / GameSession.STARTING_LIGHT);
        progressLabel.setText("RELAYS " + session.getSolved() + "/" + puzzles.size());
        scoreLabel.setText("SCORE " + session.getScore());
        streakLabel.setText("STREAK x" + session.getStreak());
    }

    private void finishGame(boolean victory) {
        if (gameEnded) {
            return;
        }
        gameEnded = true;
        stopClock();
        boolean highScore = victory && !demoMode && session.saveHighScore();

        String endingTitle;
        String endingCopy;
        if (!victory) {
            endingTitle = "THE HORIZON WENT DARK";
            endingCopy = "The archive is quiet, but no failed test is wasted. The machine remembers every contradiction.";
        } else if (session.getLight() >= 100) {
            endingTitle = "THE LONGEST DAY";
            endingCopy = "Every relay shines. You did not merely race the sunset; you changed what the machine could understand.";
        } else if (session.getLight() >= 55) {
            endingTitle = "AT THE TURNING POINT";
            endingCopy = "The final signal crosses the horizon exactly when it needs to. Darkness becomes a beginning.";
        } else {
            endingTitle = "A SIGNAL AFTER SUNSET";
            endingCopy = "One narrow beam survives the night. It is enough. Tomorrow's machine will begin from here.";
        }

        Label eyebrow = label(victory ? "PROTOCOL COMPLETE" : "PROTOCOL INTERRUPTED", "eyebrow");
        Label title = label(endingTitle, "end-title");
        title.setWrapText(true);
        title.setTextAlignment(TextAlignment.CENTER);
        Label copy = label(endingCopy, "hero-subtitle");
        copy.setWrapText(true);
        copy.setTextAlignment(TextAlignment.CENTER);
        Label stats = label(
                "SCORE " + session.getScore()
                        + "   /   RELAYS " + session.getSolved() + "/" + puzzles.size()
                        + "   /   MISTAKES " + session.getMistakes()
                        + "   /   HINTS " + session.getHints()
                        + "\nHIGH SCORE " + session.getHighScore()
                        + (highScore ? "  /  NEW RECORD" : ""),
                "category-chip");
        stats.setTextAlignment(TextAlignment.CENTER);

        VBox content = new VBox(17, eyebrow, title, copy, stats);
        content.setAlignment(Pos.CENTER);

        if (victory && session.getMistakes() == 0 && session.getHints() == 0) {
            ImageView anomaly = imageView("/assets/anomaly-success.gif", 290, 120);
            Label anomalyText = label("PERFECT DECODE: CHRONOLOGICAL ANOMALY UNLOCKED", "eyebrow");
            content.getChildren().addAll(anomaly, anomalyText);
        }

        Button again = button(victory ? "DECODE AGAIN" : "TRY AGAIN", "primary-button");
        again.setOnAction(event -> startGame());
        Button titleButton = button("TITLE SCREEN", "secondary-button");
        titleButton.setOnAction(event -> showTitleScreen());
        HBox controls = new HBox(12, again, titleButton);
        controls.setAlignment(Pos.CENTER);
        content.getChildren().add(controls);

        StackPane overlay = new StackPane(content);
        overlay.setPadding(new Insets(45));
        overlay.getStyleClass().add("end-overlay");
        root.getChildren().add(overlay);
    }

    private void handleKey(KeyCode code) {
        if (code == KeyCode.ENTER) {
            if (gameEnded && root.getChildren().size() == 1) {
                startGame();
            } else if (!gameEnded && puzzleSolved) {
                nextPuzzle();
            }
        } else if (!gameEnded && code == KeyCode.H) {
            showHint();
        } else if (!gameEnded && code.isDigitKey()) {
            int index = Integer.parseInt(code.getName()) - 1;
            if (index >= 0 && index < answerButtons.size() && !answerButtons.get(index).isDisabled()) {
                answer(index);
            }
        }
    }

    private Label label(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private Button button(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        return button;
    }

    private ImageView imageView(String resource, double width, double height) {
        ImageView view = new ImageView(image(resource));
        view.setFitWidth(width);
        view.setFitHeight(height);
        view.setPreserveRatio(true);
        view.setSmooth(true);
        return view;
    }

    private Image image(String resource) {
        return new Image(getClass().getResource(resource).toExternalForm(), false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
