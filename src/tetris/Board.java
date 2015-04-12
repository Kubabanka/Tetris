package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import tetris.Shape.Tetrominoes;
/**
 * halo halo Bańka słychać mnie? heloł?
 * kolejna zmiana:(
 *
 * Tu banka
 */

/**
 * Klasa odwzorowywująca planszę do gry.
 * Jest to panel implementujący zdarzenia.
 */
public class Board extends JPanel implements ActionListener {


    /* TODO dodać restart, wczytywanie z pliku, GUI!!, power-upy. */

    /**
     * Stała mówiąca ile bloków szerokości ma plansza.
     */
    final int BoardWidth = 10;

    /**
     * Stała mówiąca ile bloków wysokości ma plansza.
     */
    final int BoardHeight = 22;

    /**
     * Timer służący do tworzenia zdarzeń.
     */
    Timer timer;

    /**
     * Zmienna logiczna mówiąca czy klocek skończył opadać.
     */
    boolean isFallingFinished = false;

    /**
     * Zmienna logiczna mówiąca czy gra się zaczęła.
     */
    boolean isStarted = false;

    /**
     * Zmienna logiczna mówiąca czy zatrzymano grę.
     */
    boolean isPaused = false;

    /**
     * Zmienna przechowująca ilość wykasowanych linii.
     */
    int numLinesRemoved = 0;

    /**
     * Aktualna współrzędna x na planszy
     */
    int curX = 0;

    /**
     * Aktualna współrzędna y na planszy
     */
    int curY = 0;

    /**
     * Etykieta do wyświetlania wyniku/stanu gry.
     */
    JLabel statusbar;

    /**
     * Aktualny spadający klocek
     */
    Shape curPiece;

    /**
     * Tablica klocków które skończyły opadanie.
     */
    Tetrominoes[] board;


    /**
     * Konstruktor. Tworzy planszę do gry.
     * @param parent kontener zawierający planszę.
     */
    public Board(Tetris parent) {

        setFocusable(true);
        curPiece = new Shape();
        timer = new Timer(400, this);
        timer.start();

        statusbar =  parent.getStatusBar();
        board = new Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        clearBoard();
    }

    /**
     * Metoda sprawdzająca czy klocek skończył już opadać.
     * Jeżeli tak tworzy nowy klocek w przeciwnym wypadku klocek kontynuuje opadanie.
     * @param e zdarzenie wygenerowane przez zegar.
     */
    public void actionPerformed(ActionEvent e) {
        if (isFallingFinished) {
            isFallingFinished = false;
            newPiece();
        } else {
            oneLineDown();
        }
    }

    /**
     *
     * @return Zwraca dynamicznie obliczaną szerokość bloku klocka
     */
    int squareWidth() { return (int) getSize().getWidth() / BoardWidth; }

    /**
     *
     * @return Zwraca dynamicznie obliczaną wysokość bloku klocka
     */
    int squareHeight() { return (int) getSize().getHeight() / BoardHeight; }

    /**
     *  @param x współrzędna x planszy
     *  @param y  współrzędna y planszy
     * @return Kształt z jakiego pochodzi blok klocka na współrzędnych (x,y)
     */
    Tetrominoes shapeAt(int x, int y) { return board[(y * BoardWidth) + x]; }

    /**
     * Metoda rozpoczynająca grę.
     */
    public void start()
    {
        if (isPaused)
            return;

        isStarted = true;
        isFallingFinished = false;
        numLinesRemoved = 0;
        clearBoard();

        newPiece();
        timer.start();
    }

    /**
     * Metoda zatrzymująca grę.
     */
    private void pause()
    {
        if (!isStarted)
            return;

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            statusbar.setText("paused");
        } else {
            timer.start();
            statusbar.setText(String.valueOf(numLinesRemoved));
        }
        repaint();
    }

    /**
     * Metoda rysująca planszę go gry.
     * @param g  obiekt potrzebny do rysowania
     */
    public void paint(Graphics g)
    {
        super.paint(g);

        Dimension size = getSize();
        int boardTop = (int) size.getHeight() - BoardHeight * squareHeight();


        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Tetrominoes shape = shapeAt(j, BoardHeight - i - 1);
                if (shape != Tetrominoes.NoShape)
                    drawSquare(g, 0 + j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
            }
        }

        if (curPiece.getShape() != Tetrominoes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = curX + curPiece.x(i);
                int y = curY - curPiece.y(i);
                drawSquare(g, 0 + x * squareWidth(),
                        boardTop + (BoardHeight - y - 1) * squareHeight(),
                        curPiece.getShape());
            }
        }
    }

    /**
     * Metoda powodująca natychmiastowe opadniecie klocka.
     */
    private void dropDown()
    {
        int newY = curY;
        while (newY > 0) {
            if (!tryMove(curPiece, curX, newY - 1))
                break;
            --newY;
        }
        pieceDropped();
    }

    /**
     * Metoda powodująca opadnięcie klocka o jego wysokość.
     */
    private void oneLineDown()
    {
        if (!tryMove(curPiece, curX, curY - 1))
            pieceDropped();
    }

    /**
     * Metoda czyszcząca planszę z klocków które już upadły.
     */
    private void clearBoard()
    {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Tetrominoes.NoShape;
    }

    /**
     * Metoda wywoływana po skończonym opadaniu klocka.
     * Aktualizuje ona tablicę <code>board</code>.
     */
    private void pieceDropped()
    {
        for (int i = 0; i < 4; ++i) {
            int x = curX + curPiece.x(i);
            int y = curY - curPiece.y(i);
            board[(y * BoardWidth) + x] = curPiece.getShape();
        }

        removeFullLines();

        if (!isFallingFinished)
            newPiece();
    }

    /**
     * Metoda tworząca nowy, losowy klocek.
     */
    private void newPiece()
    {
        curPiece.setRandomShape();
        curX = BoardWidth / 2  + curPiece.minX();
        curY = BoardHeight - 1 + curPiece.minY();

        if (!tryMove(curPiece, curX, curY)) {
            curPiece.setShape(Tetrominoes.NoShape);
            timer.stop();
            isStarted = false;
            statusbar.setText("game over");
        }
    }

    /**
     * Metoda sprawdzająca czy opisany ruch jest możliwy.
     * Jeżeli tak wykonuje ona go.
     * @param newPiece klocek który chcemy poruszyć
     * @param newX nowa współrzędna x na planszy
     * @param newY nowa współrzędna y na planszy
     * @return <code>true</code> jeżeli ruch jest możliwy. W przeciwnym wypadku zwraca <code>false</code>.
     */
    private boolean tryMove(Shape newPiece, int newX, int newY)
    {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            int y = newY - newPiece.y(i);
            if (x<0||x>=BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != Tetrominoes.NoShape)
                return false;


        }



        curX = newX;
        curY = newY;
        curPiece = newPiece;
        repaint();
        return true;
    }

    /**
     * Metoda usuwająca zapełnione linie pozime planszy i przydzielająca punkty.
     */
    private void removeFullLines()
    {
        int numFullLines = 0;

        for (int i = BoardHeight - 1; i >= 0; --i) {
            boolean lineIsFull = true;

            for (int j = 0; j < BoardWidth; ++j) {
                if (shapeAt(j, i) == Tetrominoes.NoShape) {
                    lineIsFull = false;
                    break;
                }
            }

            if (lineIsFull) {
                ++numFullLines;
                for (int k = i; k < BoardHeight - 1; ++k) {
                    for (int j = 0; j < BoardWidth; ++j)
                        board[(k * BoardWidth) + j] = shapeAt(j, k + 1);
                }
            }
        }

        if (numFullLines > 0) {
            numLinesRemoved += numFullLines;
            statusbar.setText(String.valueOf(numLinesRemoved));
            isFallingFinished = true;
            curPiece.setShape(Tetrominoes.NoShape);
            repaint();
        }
    }

    /**
     * Metoda rysująca bloki klocków Tetris.
     * @param g  obiekt służączy do rysowania
     * @param x współrzędna x lewego górnego rogu bloku
     * @param y  współrzędna y lewego górnego rogu bloku
     * @param shape  kształt klocka którego blok rysujemy
     */
    private void drawSquare(Graphics g, int x, int y, Tetrominoes shape)
    {
        Color colors[] = { new Color(0, 0, 0), new Color(204, 102, 102),
                new Color(102, 204, 102), new Color(102, 102, 204),
                new Color(204, 204, 102), new Color(204, 102, 204),
                new Color(102, 204, 204), new Color(218, 170, 0), new Color(120, 120, 120)
        };


        Color color = colors[shape.ordinal()];

        g.setColor(color);
        g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

        g.setColor(color.brighter());
        g.drawLine(x, y + squareHeight() - 1, x, y);
        g.drawLine(x, y, x + squareWidth() - 1, y);

        g.setColor(color.darker());
        g.drawLine(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1);
        g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1);
    }

    /**
     * Metoda restartujaca grę.
     */
    private void restart(){
        if(!timer.isRunning())
            timer.start();
        clearBoard();
        start();
    }

    /**
     * Kalsa odpowiadająca za posługiwanie się klawiaturą.
     */
    class TAdapter extends KeyAdapter {

        /**
         * Metoda mówiąca jak program reaguje na naciskanie przycisków na klawiaturze.
         * @param e zdarzenie wygenerowane przez klawiaturę
         */
        public void keyPressed(KeyEvent e) {

            if (!isStarted || curPiece.getShape() == Tetrominoes.NoShape) {
                return;
            }

            int keycode = e.getKeyCode();

            if (keycode == 'p' || keycode == 'P') {
                pause();
                return;
            }

            if (isPaused)
                return;

            switch (keycode) {
                case KeyEvent.VK_LEFT:
                    tryMove(curPiece, curX - 1, curY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMove(curPiece, curX + 1, curY);
                    break;
                case KeyEvent.VK_DOWN:
                    oneLineDown();
                    break;
                case KeyEvent.VK_UP:
                    tryMove(curPiece.rotate(), curX, curY);
                    break;
                case KeyEvent.VK_SPACE:
                    dropDown();
                    break;
                case 'd':
                    oneLineDown();
                    break;
                case 'D':
                    oneLineDown();
                    break;
                case 'r':
                    restart();
                    break;
                case 'R':
                    restart();
                    break;
            }

        }
    }
}