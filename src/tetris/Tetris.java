package tetris;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Główna klasa programu. Jest to okno w którym wyświetla się nasza gra.
 */
public class Tetris extends JFrame {

    SidePanel sidePanel = new SidePanel();

    /**
     * Konstruktor tworzący naszą grę i GUI.
     */
    public Tetris() {

        Board board = new Board(sidePanel);
        add(board);
        add(sidePanel, BorderLayout.EAST);
        board.start();

        setSize(300, 400);
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    /**
     * Główna funkcja programu.
     * @param args parametry podawane z konsoli
     */
    public static void main(String[] args) {

        Tetris game = new Tetris();
        game.setLocationRelativeTo(null);
        game.setVisible(true);

    }
}