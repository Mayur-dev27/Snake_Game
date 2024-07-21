package SnakeGames;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.*;

public class Board extends JPanel implements ActionListener {

    private Image apple;
    private Image dot;
    private Image head;
    private Image bonusApple;

    private final int ALL_DOTS = 900;
    private final int DOT_SIZE = 10;
    private final int RANDOM_POSITION = 29;

    private int apple_x;
    private int apple_y;

    private final int x[] = new int[ALL_DOTS];
    private final int y[] = new int[ALL_DOTS];

    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;

    private boolean inGame = true;

    private int dots;
    private Timer timer;
    private Timer bonusTimer;

    private int score;
    private int applesEaten;
    private boolean isBonusApple;
    private int highScore;

    private final String HIGH_SCORE_FILE = "highscore.dat";

    public Board() {
        addKeyListener(new TAdapter());

        setBackground(Color.BLACK);
        setPreferredSize(new Dimension(300, 300));
        setFocusable(true);

        loadImages();
        initGame();
        loadHighScore();
    }

    public void loadImages() {
        ImageIcon i1 = new ImageIcon(ClassLoader.getSystemResource("Snakegames/icons/apple.png"));
        apple = i1.getImage();

        ImageIcon i2 = new ImageIcon(ClassLoader.getSystemResource("Snakegames/icons/dot.png"));
        dot = i2.getImage();

        ImageIcon i3 = new ImageIcon(ClassLoader.getSystemResource("Snakegames/icons/head.png"));
        head = i3.getImage();

        ImageIcon i4 = new ImageIcon(ClassLoader.getSystemResource("Snakegames/icons/bonus_apple.png"));
        bonusApple = i4.getImage();
    }

    public void initGame() {
        dots = 3;
        score = 0;
        applesEaten = 0;
        isBonusApple = false;

        for (int i = 0; i < dots; i++) {
            y[i] = 50;
            x[i] = 50 - i * DOT_SIZE;
        }

        locateApple();

        timer = new Timer(140, this);
        timer.start();
    }

    public void locateApple() {
        int r = (int) (Math.random() * RANDOM_POSITION);
        apple_x = r * DOT_SIZE;

        r = (int) (Math.random() * RANDOM_POSITION);
        apple_y = r * DOT_SIZE;

        // If the next apple is a bonus apple, start the bonus timer
        if (applesEaten > 0 && applesEaten % 4 == 0) {
            isBonusApple = true;
            startBonusTimer();
        } else {
            isBonusApple = false;
        }
    }

    public void startBonusTimer() {
        if (bonusTimer != null && bonusTimer.isRunning()) {
            bonusTimer.stop();
        }
        bonusTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // If bonus apple not eaten in 5 seconds, it reverts to normal apple
                isBonusApple = false;
                locateApple();
            }
        });
        bonusTimer.setRepeats(false);
        bonusTimer.start();
    }

    public void loadHighScore() {
        try (BufferedReader br = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            highScore = Integer.parseInt(br.readLine());
        } catch (IOException e) {
            highScore = 0;
        }
    }

    public void saveHighScore() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            bw.write(String.valueOf(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        draw(g);
    }

    public void draw(Graphics g) {
        if (inGame) {
            if (isBonusApple) {
                g.drawImage(bonusApple, apple_x, apple_y, this);
            } else {
                g.drawImage(apple, apple_x, apple_y, this);
            }

            for (int i = 0; i < dots; i++) {
                if (i == 0) {
                    g.drawImage(head, x[i], y[i], this);
                } else {
                    g.drawImage(dot, x[i], y[i], this);
                }
            }

            drawScore(g);

            Toolkit.getDefaultToolkit().sync();
        } else {
            gameOver(g);
        }
    }

    public void drawScore(Graphics g) {
        String scoreMsg = "Score: " + score;
        Font small = new Font("Helvetica", Font.BOLD, 14);
        g.setColor(Color.WHITE);
        g.setFont(small);
        g.drawString(scoreMsg, 10, 20);

        String highScoreMsg = "High Score: " + highScore;
        g.drawString(highScoreMsg, 10, 40);
    }

    public void gameOver(Graphics g) {
        String msg = "Game Over!";
        Font font = new Font("SAN_SERIF", Font.BOLD, 14);
        FontMetrics metrices = getFontMetrics(font);

        g.setColor(Color.WHITE);
        g.setFont(font);
        g.drawString(msg, (300 - metrices.stringWidth(msg)) / 2, 300 / 2);

        String scoreMsg = "Score: " + score;
        g.drawString(scoreMsg, (300 - metrices.stringWidth(scoreMsg)) / 2, 300 / 2 + 20);

        String highScoreMsg = "High Score: " + highScore;
        g.drawString(highScoreMsg, (300 - metrices.stringWidth(highScoreMsg)) / 2, 300 / 2 + 40);
    }

    public void move() {
        for (int i = dots; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        if (leftDirection) {
            x[0] = x[0] - DOT_SIZE;
        }
        if (rightDirection) {
            x[0] = x[0] + DOT_SIZE;
        }
        if (upDirection) {
            y[0] = y[0] - DOT_SIZE;
        }
        if (downDirection) {
            y[0] = y[0] + DOT_SIZE;
        }
    }

    public void checkApple() {
        if ((x[0] == apple_x) && (y[0] == apple_y)) {
            dots++;
            applesEaten++;

            if (isBonusApple) {
                score += 5;
                bonusTimer.stop();
            } else {
                score += 1;
            }

            locateApple();
        }
    }

    public void checkCollision() {
        for (int i = dots; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
                inGame = false;
            }
        }

        if (y[0] >= 300) {
            inGame = false;
        }
        if (x[0] >= 300) {
            inGame = false;
        }
        if (y[0] < 0) {
            inGame = false;
        }
        if (x[0] < 0) {
            inGame = false;
        }

        if (!inGame) {
            timer.stop();
            if (bonusTimer != null) {
                bonusTimer.stop();
            }

            if (score > highScore) {
                highScore = score;
                saveHighScore();
            }
        }
    }

    public void actionPerformed(ActionEvent ae) {
        if (inGame) {
            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    public class TAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (key == KeyEvent.VK_LEFT && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if (key == KeyEvent.VK_RIGHT && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if (key == KeyEvent.VK_UP && (!downDirection)) {
                upDirection = true;
                leftDirection = false;
                rightDirection = false;
            }

            if (key == KeyEvent.VK_DOWN && (!upDirection)) {
                downDirection = true;
                leftDirection = false;
                rightDirection = false;
            }
        }
    }

}
