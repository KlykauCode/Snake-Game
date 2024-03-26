package cz.cvut.fel.klykadan;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.skin.TextInputControlSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class App extends Application {
    private Config config;
    private GraphicsContext graphicsContext;
    private VBox root;
    private Canvas canvas;
    private Pane gameOverPane;

    @Override
    public void start(Stage stage) {
        config = new Config();
        root = new VBox();
        canvas = new Canvas(config.getWidth() * config.getCornerSize(), config.getHeight() * config.getCornerSize());
        graphicsContext = canvas.getGraphicsContext2D();
        root.getChildren().add(canvas);

        root.setStyle("-fx-background-color: black;");

        gameOverPane = new Pane();
        gameOverPane.setVisible(false);

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.setFont(new Font("", 50));
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setLayoutX(config.getWidth() * config.getCornerSize() / 4 - 20);
        gameOverLabel.setLayoutY(config.getHeight() * config.getCornerSize() / 4);

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(e -> restartGame());
        restartButton.setLayoutX((double) (config.getWidth() * config.getCornerSize()) / 2 - 30);
        restartButton.setLayoutY((double) (config.getHeight() * config.getCornerSize()) / 2);

        gameOverPane.getChildren().addAll(gameOverLabel, restartButton);

        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(canvas, gameOverPane);
        root.getChildren().add(stackPane);

        Scene scene = new Scene(root, config.getWidth() *
                config.getCornerSize(), config.getHeight() * config.getCornerSize());
        scene.addEventFilter(KeyEvent.KEY_PRESSED, this::handleKeyPress);

        stage.setTitle("Snake Game");
        stage.setScene(scene);
        stage.show();

        startAnimation();
        initGame();
    }

    private void handleKeyPress(KeyEvent key) {
        if (config.isGameOver()) {
            return;
        }

        Dir newDirection = config.getDirection();
        switch (key.getCode()) {
            case W: newDirection = Dir.up; break;
            case A: newDirection = Dir.left; break;
            case S: newDirection = Dir.down; break;
            case D: newDirection = Dir.right; break;
        }

        if (!isOppositeDirection(newDirection, config.getDirection())) {
            config.setDirection(newDirection);
        }
    }

    private boolean isOppositeDirection(Dir newDir, Dir currentDir) {
        return (newDir == Dir.up && currentDir == Dir.down) ||
                (newDir == Dir.down && currentDir == Dir.up) ||
                (newDir == Dir.left && currentDir == Dir.right) ||
                (newDir == Dir.right && currentDir == Dir.left);
    }

    private void restartGame() {
        config = new Config();
        gameOverPane.setVisible(false);
        initGame();
        startAnimation();
    }

    private void initGame() {

        config.getSnake().clear();
        config.getSnake().add(new Corner(config.getWidth()/2, config.getHeight()/2));
        config.getSnake().add(new Corner(config.getWidth()/2, config.getHeight()/2));
        config.getSnake().add(new Corner(config.getWidth()/2, config.getHeight()/2));
        newFood(config);
    }

    private void startAnimation() {
        new AnimationTimer() {
            long lastTick = 0;

            @Override
            public void handle(long now) {
                if (config.isGameOver()) {
                    gameOverPane.setVisible(true);
                    stop();
                    return;
                }
                if (lastTick == 0 || now - lastTick > 1000000000 / config.getSpeed()) {
                    lastTick = now;
                    tick(graphicsContext, config);
                }
            }
        }.start();
    }

    public static void tick(GraphicsContext graphicsContext, Config config) {
        graphicsContext.clearRect(0, 0, config.getWidth() *
                config.getCornerSize(), config.getHeight() * config.getCornerSize());

        if (config.isGameOver()) {
            graphicsContext.setFill(Color.RED);
            graphicsContext.setFont(new Font("", 50));
            graphicsContext.fillText("GAME OVER", (double) (config.getWidth() *
                    config.getCornerSize()) / 4, (double) (config.getHeight() * config.getCornerSize()) / 2);
            return;
        }

        updateSnake(config);
        checkCollisions(config);
        drawGame(graphicsContext, config);
    }

    private static void updateSnake(Config config) {
        List<Corner> snake = config.getSnake();
        for (int i = snake.size() - 1; i >= 1; i--) {
            snake.get(i).setX(snake.get(i - 1).getX());
            snake.get(i).setY(snake.get(i - 1).getY());
        }

        Corner head = snake.get(0);
        switch (config.getDirection()) {
            case up:
                head.setY(head.getY() - 1);
                break;
            case down:
                head.setY(head.getY() + 1);
                break;
            case left:
                head.setX(head.getX() - 1);
                break;
            case right:
                head.setX(head.getX() + 1);
                break;
        }
    }

    private static void checkCollisions(Config config) {
        Corner head = config.getSnake().get(0);

        if (head.getX() < 0 || head.getX() >= config.getWidth() ||
                head.getY() < 0 || head.getY() >= config.getHeight()) {
            config.setGameOver(true);
        }


        for (int i = 1; i < config.getSnake().size(); i++) {
            if (head.getX() == config.getSnake().get(i).getX() &&
                    head.getY() == config.getSnake().get(i).getY()) {
                config.setGameOver(true);
            }
        }


        if (head.getX() == config.getFoodX() && head.getY() == config.getFoodY()) {
            snakeGrow(config);
            config.increaseSpeed();
            newFood(config);
        }
    }

    private static void snakeGrow(Config config) {
        List<Corner> snake = config.getSnake();
        Corner tail = new Corner(snake.get(snake.size() - 1).getX(), snake.get(snake.size() - 1).getY());
        snake.add(tail);
    }

    private static void drawGame(GraphicsContext graphicsContext, Config config) {
        drawFood(graphicsContext, config);
        drawSnake(graphicsContext, config);

        graphicsContext.setFill(Color.WHITE);
        graphicsContext.setFont(new Font("", 30));
        graphicsContext.fillText("Score: " + (config.getSnake().size() - 3), 10, 30);
    }

    private static void drawFood(GraphicsContext graphicsContext, Config config) {
        graphicsContext.setFill(getFoodColor(config.getFoodColor()));
        graphicsContext.fillOval(config.getFoodX() * config.getCornerSize(), config.getFoodY()
                * config.getCornerSize(), config.getCornerSize(), config.getCornerSize());
    }

    private static Color getFoodColor(int foodColorIndex) {
        switch (foodColorIndex) {
            case 0: return Color.PURPLE;
            case 1: return Color.LIGHTBLUE;
            case 2: return Color.YELLOW;
            case 3: return Color.RED;
            case 4: return Color.ORANGE;
            default: return Color.WHITE;
        }
    }

    private static void newFood(Config config) {
        start:
        while (true) {
            int nextFoodX = config.getRand().nextInt(config.getWidth());
            int nextFoodY = config.getRand().nextInt(config.getHeight());

            for (Corner corner: config.getSnake()) {
                if (corner.getX() == nextFoodX && corner.getY() == nextFoodY) {
                    continue start;
                }
            }
            config.setFoodX(nextFoodX);
            config.setFoodY(nextFoodY);
            config.setFoodColor(config.getRand().nextInt(5));
            break;
        }
    }

    private static void drawSnake(GraphicsContext graphicsContext, Config config) {
        for (Corner corner : config.getSnake()) {
            graphicsContext.setFill(Color.GREEN);
            graphicsContext.fillRect(corner.getX() * config.getCornerSize(), corner.getY() *
                    config.getCornerSize(), config.getCornerSize(), config.getCornerSize());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}