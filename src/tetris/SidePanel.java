package tetris;


import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Panel boczny interfejsu gry.
 */
public class SidePanel extends JPanel {

    /**
     * Przycisk nowej gry.
     */
    JButton newGameButton;// = new JButton("New Game");

    /**
     * Przycisk pauzy.
     */
    JButton pauseButton;// = new JButton("Pause");

    /**
     * Przycisk obsługi sieciowej.
     */
    JButton webButton;

    /**
     * Etykieta do wyświetlania wyniku/stanu gry.
     */
    JLabel statusbar = new JLabel("");

    /**
     * Plansza do gry.
     */
    private Board board;

    /**
     * Przycisk restart.
     */
    JButton restartButton;// = new JButton("Restart");

    /**
     * Etykieta do wyświetlania wyniku.
     */
    JLabel pointsbar = new JLabel("");

    /**
     * Metoda zwracająca etykietę wyniku.
     * @return Zwraca etykietę wyniku.
     */
    public JLabel getPointsbar() {
        return pointsbar;
    }

    /**
     * Konstruktor tworzacy panel.
     */
    public SidePanel() {
        setLayout(new GridLayout(6, 1));
        ButtonAction newGameAction = new ButtonAction("New Game", "Starts a new game");
        ButtonAction restartAction = new ButtonAction("Restart", "Restarts game");
        ButtonAction pauseAction = new ButtonAction("Pause", "Pauses game");
        ButtonAction webAction = new ButtonAction("Web service", "Downloads files from server");
        newGameButton = new JButton(newGameAction);
        restartButton = new JButton(restartAction);
        pauseButton = new JButton(pauseAction);
        webButton = new JButton(webAction);
        add(newGameButton);
        add(pauseButton);
        add(restartButton);
        add(webButton);
        add(pointsbar);
        add(statusbar);
        setBorder(new EtchedBorder());
        setSize(100, 400);
    }

    /**
     * Ustawia plansze na ta z parametru
     *
     * @param board plansza do ustawienia
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Metoda zwracająca etykietę ze statusem gry (np. Pause).
     * @return Zwraca etykietę ze statusem gry.
     */
    public JLabel getStatusBar() {
        return statusbar;
    }

    /**
     * Klasa pomagająca w tworzeniu przycisków.
     */
    private class ButtonAction extends AbstractAction {

        public ButtonAction(String name, String des) {
            putValue(Action.NAME, name);
            putValue(Action.SHORT_DESCRIPTION, des);
            //putValue(Action.);
        }

        /**
         * Metoda decydująca o działaniu po przycisnięciu przycisku.
         *
         * @param e
         */
        public void actionPerformed(ActionEvent e) {

            String command = e.getActionCommand();
            if (command.equals("New Game"))
                board.start();
            if (command.equals("Restart")) {
                board.restart();
            }
            if (command.equals("Pause"))
                board.pause();
            if (command.equals("Web service")) {
                Network n = new Network(board.hostIP);
                board.isOnline=true;
            }

            if (!board.isFocusOwner())
                board.grabFocus();


        }
    }

}
