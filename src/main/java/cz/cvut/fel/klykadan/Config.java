package cz.cvut.fel.klykadan;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Config {
    private int speed = 5;
    private int foodColor = 0;
    private int height = 20;
    private int width = 20;
    private int foodX = 0;
    private int foodY = 0;
    private int cornerSize = 25;
    private List<Corner> snake = new ArrayList<>();
    private Dir direction = Dir.left;
    private boolean gameOver = false;
    private Random rand = new Random();

    public void increaseSpeed() {
        if (speed < 10) {
            speed++;
        }
    }

    public int getFoodColor() { return foodColor;}
    public int getSpeed(){
        return speed;
    }
    public void setSpeed(int speed){ this.speed = speed;}
    public int getHeight() {
        return height;
    }

    public void setFoodColor(int foodColor) {this.foodColor = foodColor; }
    public void setHeight(int height) { this.height = height; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getFoodX() { return foodX; }
    public void setFoodX(int foodX) { this.foodX = foodX; }

    public int getFoodY() { return foodY; }
    public void setFoodY(int foodY) { this.foodY = foodY; }

    public int getCornerSize() { return cornerSize; }
    public void setCornerSize(int cornerSize) { this.cornerSize = cornerSize; }

    public List<Corner> getSnake() { return snake; }
    public void setSnake(List<Corner> snake) { this.snake = snake; }

    public Dir getDirection() { return direction; }
    public void setDirection(Dir direction) { this.direction = direction; }

    public boolean isGameOver() { return gameOver; }
    public void setGameOver(boolean gameOver) { this.gameOver = gameOver; }


    public Random getRand() { return rand; }

}