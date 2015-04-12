package tetris;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

import tetris.Shape.Tetrominoes;


/**
 * Klasa odwzorowywująca planszę do gry.
 * Jest to panel implementujący zdarzenia.
 */
public class Board extends JPanel implements ActionListener {


    /* TODO wczytywanie z pliku, power-upy. */

    /**
     * Stała mówiąca ile bloków szerokości ma plansza.
     */
    int BoardWidth = 10;

    /**
     * Stała mówiąca ile bloków wysokości ma plansza.
     */
    int BoardHeight = 22;

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
    int score = 0;

    /**
     * Aktualna współrzędna x na planszy
     */
    int curX = 0;

    /**
     * Aktualna współrzędna y na planszy
     */
    int curY = 0;

    /**
     * Ilość punktów za skasowanie jednej linii.
     */
    int lineScore;

    /**
     * Liczba punktów za zniszczenie jednego bloku przy wybuchu.
     */
    int blockScore;

    /**
     * Kara za użycie power-upu.
     */
    int penalty;

    /**
     * Liczba punktów za przejście na kolejny poziom.
     */
    int levelScore;

    /**
     * Prędkość opadania klocka.
     */
    int speed;

    /**
     * Maksymalna ilość power-upów.
     */
    int maxPowerUp;

    /**
     * Etykieta do wyświetlania stanu gry.
     */
    JLabel statusbar;

    /**
     * Etykieta do wyświetlania wyniku.
     */
    JLabel pointsbar;

    /**
     * Aktualny spadający klocek.
     */
    Shape curPiece;

    /**
     * Tablica klocków które skończyły opadanie.
     */
    Tetrominoes[] board;

    /**
     * Nazwa zawodnika.
     */
    String playerName;


    /**
     * Konstruktor. Tworzy planszę do gry.
     * @param parent kontener zawierający planszę.
     */
    public Board(Tetris parent) {

        setFocusable(true);
        try {
            LoadFromFile();
        } catch (Exception e) {
            System.out.println("Blad wczytania pliku");
        }
        curPiece = new Shape();
        timer = new Timer(speed, this);
        timer.start();
        pointsbar = parent.getSidePanel().getPointsbar();
        statusbar = parent.getSidePanel().getStatusBar();
        board = new Tetrominoes[BoardWidth * BoardHeight];
        addKeyListener(new TAdapter());
        clearBoard();
    }

    /**
     * Metoda wczytująca konfigurację z pliku config.properties
     *
     * @throws IOException
     */

    public void LoadFromFile() throws IOException {
        java.util.Properties properties = new java.util.Properties();
        InputStream input = null;
        try {
            input = new FileInputStream("config.properties");
            properties.load(input);
            String _lineScore = properties.getProperty("lineScore");
            String _blockScore = properties.getProperty("blockScore");
            String _width = properties.getProperty("width");
            String _height = properties.getProperty("height");
            String _penalty = properties.getProperty("penalty");
            String _levelScore = properties.getProperty("levelScore");
            String _speed = properties.getProperty("speed");
            String _maxPowerUp = properties.getProperty("maxPowerUp");

            playerName = properties.getProperty("playerName");

            lineScore = Integer.parseInt(_lineScore);
            blockScore = Integer.parseInt(_blockScore);
            BoardWidth = Integer.parseInt(_width);
            BoardHeight = Integer.parseInt(_height);
            penalty = Integer.parseInt(_penalty);
            levelScore = Integer.parseInt(_levelScore);
            speed = Integer.parseInt(_speed);
            maxPowerUp = Integer.parseInt(_maxPowerUp);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

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
        clearBoard();
        score = 0;
        statusbar.setText("");
        pointsbar.setText("Score: " + score);
        newPiece();
        timer.start();
    }


    /**
     * Metoda zatrzymująca grę.
     */
    void pause()
    {
        if (!isStarted)
            return;

        isPaused = !isPaused;
        if (isPaused) {
            timer.stop();
            statusbar.setText("Paused");
            pointsbar.setText("Score: " + score);
        } else {
            timer.start();
            statusbar.setText("");
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
            statusbar.setText("<html>GAME OVER!");
            pointsbar.setText("Score:" + score);
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
            for (int z = 1; z <= numFullLines; z++) {
                score += z * lineScore;
            }
            pointsbar.setText("Score: " + String.valueOf(score));
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
        Color colors[] = { new Color(0, 0, 0), new Color(255, 200, 181),
                new Color(255, 180, 181), new Color(255, 160, 181),
                new Color(255, 140, 181), new Color(255, 120, 181),
                new Color(255, 100, 181), new Color(255, 80, 181),
                new Color(255, 60, 181), new Color(255, 40, 181)
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
    void restart() {
        if(!timer.isRunning())
            timer.start();
        clearBoard();
        pause();
        start();
    }

    /**
     * Metoda aktywujaca power_up
     */
    private void activatePowerUp() {
        if (score > 0)
            score -= penalty;
        pointsbar.setText("Score: " + score);
        newPiece();
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
                    activatePowerUp();
                    break;
                case 'D':
                    activatePowerUp();
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