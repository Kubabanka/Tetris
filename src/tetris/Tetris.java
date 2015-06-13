package tetris;

import java.awt.BorderLayout;

import javax.swing.*;

/**
 * Główna klasa programu. Jest to okno w którym wyświetla się nasza gra.
 */
public class Tetris extends JFrame {
    /**
     * Pasek boczny GUI.
     */
    private SidePanel aSidePanel;
    /**
     * Plansza z grą.
     */
    private Board board;

    /**
     * @return Zwraca panel boczny.
     */
    public SidePanel getSidePanel() {
        return aSidePanel;
    }

    /**
     * @return Zwraca plansze.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Konstruktor tworzący naszą grę i GUI.
     */
    public Tetris() {

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        aSidePanel = new SidePanel();
        board = new Board(this);

        aSidePanel.setBoard(board);

        add(aSidePanel, BorderLayout.EAST);
        add(board);
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