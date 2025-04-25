import java.io.*;
import java.util.*;

public class MazeGame {

    static int SIZE = 11; // Default size of the maze
    static char[][] maze;
    static int playerX = 1, playerY = 1;
    static int endX, endY;
    static int moveCount = 0;
    static long startTime;
    static int playerHealth = 100; // Health system
    static int difficulty = 1; // 1 = Easy, 2 = Medium, 3 = Hard

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Difficulty selection
        System.out.println("Choose Difficulty: 1 = Easy, 2 = Medium, 3 = Hard");
        difficulty = scanner.nextInt();
        setMazeSize(difficulty);

        generateMaze();
        scanner.nextLine(); // Consume the leftover newline

        System.out.println("==== MAZE GAME ====");
        System.out.println("Reach 'E' to win. Use U D L R to move (Up, Down, Left, Right).");
        System.out.println("Good luck!");

        startTime = System.currentTimeMillis(); // Start the timer

        boolean running = true;
        while (running) {
            long timeElapsed = (System.currentTimeMillis() - startTime) / 1000; // Elapsed time in seconds

            printMaze();
            System.out.println("Time Elapsed: " + timeElapsed + " seconds");
            System.out.print("Move (U/D/L/R): ");
            String input = scanner.nextLine().toUpperCase();

            if (input.length() == 0) continue;
            char move = input.charAt(0);

            switch (move) {
                case 'U' -> movePlayer(-1, 0); // Up
                case 'D' -> movePlayer(1, 0);  // Down
                case 'L' -> movePlayer(0, -1); // Left
                case 'R' -> movePlayer(0, 1);  // Right
                default -> System.out.println("Invalid key! Use U D L R");
            }

            moveCount++;

            // Check if the player has reached the end
            if (playerX == endX && playerY == endY) {
                printMaze();
                long timeTaken = (System.currentTimeMillis() - startTime) / 1000; // Time taken in seconds
                System.out.println("\nYou won in " + moveCount + " moves and " + timeTaken + " seconds!");
                running = false;
                saveLeaderboard(timeTaken, moveCount);
            }

            // Health system (player loses health when hitting walls)
            if (playerHealth <= 0) {
                System.out.println("Game Over! You ran out of health.");
                break;
            }
        }

        scanner.close();
    }

    static void printMaze() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (i == playerX && j == playerY) {
                    System.out.print("P "); // Player's position
                } else {
                    System.out.print(maze[i][j] + " ");
                }
            }
            System.out.println();
        }

        System.out.println("Health: " + playerHealth + "%");
    }

    static void movePlayer(int dx, int dy) {
        int newX = playerX + dx;
        int newY = playerY + dy;

        if (newX >= 0 && newX < SIZE && newY >= 0 && newY < SIZE && maze[newX][newY] != '#') {
            playerX = newX;
            playerY = newY;
        } else {
            System.out.println("Wall! Try again.");
            playerHealth -= 5;  // Deduct health if the player hits a wall
        }

        // Health deduction if hitting obstacles (e.g., spikes)
        if (maze[playerX][playerY] == 'O') { // 'O' represents obstacles
            playerHealth -= 10;
            System.out.println("You hit an obstacle! Health reduced by 10%");
        }
    }

    static void generateMaze() {
        maze = new char[SIZE][SIZE];
        // Initialize the maze with walls
        for (char[] row : maze) Arrays.fill(row, '#');
        dfsMaze(1, 1, new boolean[SIZE][SIZE]);

        maze[1][1] = 'S'; // Start point
        endX = SIZE - 2;
        endY = SIZE - 2;
        maze[endX][endY] = 'E'; // End point

        // Add some obstacles randomly in the maze
        Random rand = new Random();
        for (int i = 0; i < SIZE / 3; i++) {
            int x = rand.nextInt(SIZE - 2) + 1;
            int y = rand.nextInt(SIZE - 2) + 1;
            maze[x][y] = 'O';  // 'O' represents an obstacle (spike/pit)
        }
    }

    static void dfsMaze(int x, int y, boolean[][] visited) {
        visited[x][y] = true;
        maze[x][y] = ' ';

        int[] dx = {-2, 2, 0, 0};
        int[] dy = {0, 0, -2, 2};
        Integer[] dirs = {0, 1, 2, 3};
        Collections.shuffle(Arrays.asList(dirs));

        for (int dir : dirs) {
            int nx = x + dx[dir];
            int ny = y + dy[dir];

            if (inBounds(nx, ny) && !visited[nx][ny]) {
                maze[x + dx[dir] / 2][y + dy[dir] / 2] = ' ';
                dfsMaze(nx, ny, visited);
            }
        }
    }

    static boolean inBounds(int x, int y) {
        return x > 0 && x < SIZE - 1 && y > 0 && y < SIZE - 1;
    }

    static void setMazeSize(int difficulty) {
        switch (difficulty) {
            case 1 -> {
                SIZE = 9; // Easy
            }
            case 2 -> {
                SIZE = 15; // Medium
            }
            case 3 -> {
                SIZE = 21; // Hard
            }
        }
    }

    // Save the score to leaderboard
    static void saveLeaderboard(long timeTaken, int moveCount) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("leaderboard.txt", true))) {
            writer.write("Time: " + timeTaken + " seconds, Moves: " + moveCount + "\n");
            System.out.println("Score saved to leaderboard.");
        } catch (IOException e) {
            System.out.println("Error saving leaderboard." );}
        }
    }