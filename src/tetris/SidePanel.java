package tetris;

import javax.swing.*;
import java.awt.*;

/**
 * Panel boczny interfejsu gry.
 */
public class SidePanel extends JPanel {

    /**
     * Przycisk nowej gry.
     */
    JButton newGameButton = new JButton("New Game");

    /**
     * Przycisk pauzy.
     */
    JButton pauseButton = new JButton("Pause");

    /**
     * Etykieta do wyświetlania wyniku/stanu gry.
     */
    JLabel statusbar = new JLabel("0");

    /**
     * Przycisk restart.
     */
    JButton restartButton = new JButton("Restart");

    /**
     * Konstruktor tworzacy panel.
     */
    public SidePanel() {
        setLayout(new GridLayout(5, 1));
        add(newGameButton);
        add(pauseButton);
        add(restartButton);
        add(statusbar);

        setSize(100, 400);
    }


    /**
     * @return Metoda zwracająca etykietę z wynikiem.
     */
    public JLabel getStatusBar() {
        return statusbar;
    }

}
