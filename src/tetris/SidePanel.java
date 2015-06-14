package tetris;


import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.event.EventListenerList;
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
     * Etykieta do wyświetlania wyniku/stanu gry.
     */
    JLabel statusbar = new JLabel("");

    /**
     * Plansza do gry.
     */
    Board board;

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
        newGameButton = new JButton(newGameAction);
        restartButton = new JButton(restartAction);
        pauseButton = new JButton(pauseAction);
        add(newGameButton);
        add(pauseButton);
        add(restartButton);
        add(new ImagePanel());
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

        }

        /**
         * Metoda decydująca o działaniu po przycisnięciu przycisku.
         *
         * @param e uuu
         */
        public void actionPerformed(ActionEvent e) {

            String command = e.getActionCommand();
            if (command.equals("New Game"))
                board.start();
            if (command.equals("Restart")) {
                board.restart();
                System.out.println("restart");
            }
            if (command.equals("Pause"))
                board.pause();

            if (!board.isFocusOwner())
                board.grabFocus();


        }
    }

    private class ImagePanel extends JPanel implements  PieceChangedListener {


        private Image [] pieces=new Image[10];

        private void LoadImages(){
            ImageIcon [] icons = new ImageIcon[10];
            for (int i =1;i<10;i++)
            {
                icons[i] = new ImageIcon("images/"+i+".png");
                pieces[i] = icons[i].getImage();
            }
        }

        public void paintComponent(Graphics g) {

            g.drawImage(pieces[board.nextPiece.getShape().ordinal()], 0, 0, null);
        }

        ImagePanel (){
            LoadImages();
            int w=pieces[4].getWidth(null);
            int h=pieces[4].getHeight(null);
            setPreferredSize(new Dimension(w,h));
        }



        public void PieceChanged(Event e) {
            repaint();
        }
    }

}
