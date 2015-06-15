package tetris;

/**
 * Created by Jakub Bańka & Tomasz Duda
 * EiTI
 * 2015
 *
 * @author Jakub Bańka
 * @author Tomasz Duda
 */

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
     * Metoda zwracąjąca panel boczny w oknie gry.
     * @return Zwraca panel boczny.
     */
    public SidePanel getSidePanel() {
        return aSidePanel;
    }

    /**
     * Metoda zwracająca planszę gry.
     * @return Zwraca plansze.
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Konstruktor tworzący grę i GUI.
     */
    public Tetris() {

        try {
            /**
             * Wygląd buttonów będzie dostosowywał się do systemu operacyjnego.
             */
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
        /**
         * Domyślny rozmiar okna (w pixelach)
         */
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