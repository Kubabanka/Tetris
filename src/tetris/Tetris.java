package tetris;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 * Główna klasa programu. Jest to okno w którym wyświetla się nasza gra.
 */
public class Tetris extends JFrame {

    /**
     * Etykieta do wyświetlania wyniku/stanu gry.
     */
    JLabel statusbar;

    /**
     * Konstruktor tworzący naszą grę i GUI.
     */
    public Tetris() {

        statusbar = new JLabel(" 0");
        add(statusbar, BorderLayout.SOUTH);
        Board board = new Board(this);
        add(board);
        board.start();

        setSize(200, 400);
        setTitle("Tetris");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    /**
     *
     * @return Metoda zwracająca etykietę z wynikiem.
     */
    public JLabel getStatusBar() {
        return statusbar;
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