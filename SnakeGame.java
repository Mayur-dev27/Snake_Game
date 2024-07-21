package SnakeGames;

import javax.swing.*;

//public class SnakeGame extends JFrame {          // for full screen
//    SnakeGame(){
//        super("Snake Game ");
//        add(new Board());
//        pack();
//
//        setSize(300,300);
//        setLocationRelativeTo(null);
//        setVisible(true);
//    }
//
//    public static void main(String[] args) {
//        new SnakeGame();
//    }
//}

public class SnakeGame extends JFrame {

    SnakeGame() {
        super("Snake Game");
        add(new Board());
        pack();

        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    public static void main(String[] args) {
        new SnakeGame().setVisible(true);
    }
}