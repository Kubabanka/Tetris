package tetris;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.geom.Line2D;
import java.io.*;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;


/**
 * Klasa odwzorowywująca planszę do gry.
 * Jest to panel implementujący zdarzenia.
 */
public class Board extends JPanel implements ActionListener {


    /**
     * Stała mówiąca ile bloków szerokości ma plansza.
     */
    int BoardWidth;

    /**
     * Stała mówiąca ile bloków wysokości ma plansza.
     */
    int BoardHeight;

    int scale;

    int currentLevel;

    int internalScore = 0;

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
    int currentX = 0;

    /**
     * Aktualna współrzędna y na planszy
     */
    double currentY = 0;

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
    Shape currentPiece;

    /**
     * Tablica klocków które skończyły opadanie.
     */
    Shape.TetroShapes[] board;

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
        currentPiece = new Shape();
        timer = new Timer(speed, this);
        timer.start();
        pointsbar = parent.getSidePanel().getPointsbar();
        statusbar = parent.getSidePanel().getStatusBar();
        board = new Shape.TetroShapes[BoardWidth * BoardHeight];
        addKeyListener(new MyAdapter());
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
            String _scale = properties.getProperty("scale");
            String _penalty = properties.getProperty("penalty");
            String _levelScore = properties.getProperty("levelScore");
            String _speed = properties.getProperty("speed");
            String _maxPowerUp = properties.getProperty("maxPowerUp");

            playerName = properties.getProperty("playerName");

            lineScore = Integer.parseInt(_lineScore);
            blockScore = Integer.parseInt(_blockScore);
            BoardWidth = Integer.parseInt(_width);
            BoardHeight = Integer.parseInt(_height);
            scale = Integer.parseInt(_scale);
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
    double squareHeight() { return getSize().getHeight() / BoardHeight; }

    /**
     *  @param x współrzędna x planszy
     *  @param y  współrzędna y planszy
     * @return Kształt z jakiego pochodzi blok klocka na współrzędnych (x,y)
     */
    Shape.TetroShapes shapeAt(int x, double y) { return board[(int)(StrictMath.floor(y) * BoardWidth) + x]; }

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
        currentLevel = 1;
        loadLevel(currentLevel);
        score = 0;
        statusbar.setText("");
        pointsbar.setText("Score: " + score);
        newPiece();
        timer.start();
    }

    private void loadLevel(int levelNumber) {
        BufferedWriter levelWriter = null;
        try {
            BufferedReader levelParser = new BufferedReader(new FileReader(new File("levels/level" + levelNumber + ".eiti")));
            levelWriter = new BufferedWriter(new FileWriter(new File("levels/level" + levelNumber + ".out")));

            String line;
            int row = BoardHeight - 1;
            while ((line = levelParser.readLine()) != null) {
                String[] shapesNumbers = line.split(" ");
                int tetroShape;

                for (int i = 0; i < shapesNumbers.length; ++i) {
                    tetroShape = Integer.parseInt(shapesNumbers[i]);
                    board[row * BoardWidth + i] = Shape.TetroShapes.values()[tetroShape];
                }

                --row;
            }
        } catch (IOException e) {

        } finally {
            if (levelWriter != null) {
                try {
                    levelWriter.close();
                } catch (IOException e) {
                    ;
                }
            }
        }
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
        double boardTop = size.getHeight() - BoardHeight * squareHeight();


        for (int i = 0; i < BoardHeight; ++i) {
            for (int j = 0; j < BoardWidth; ++j) {
                Shape.TetroShapes shape = shapeAt(j, BoardHeight - i - 1);
                if (shape != Shape.TetroShapes.NoShape)
                    drawBlock(g, 0 + j * squareWidth(),
                            boardTop + i * squareHeight(), shape);
            }
        }

        if (currentPiece.getShape() != Shape.TetroShapes.NoShape) {
            for (int i = 0; i < 4; ++i) {
                int x = currentX + currentPiece.x(i);
                double y = currentY - currentPiece.y(i);
                drawBlock(g, 0 + x * squareWidth(),
                        boardTop + (BoardHeight - y - 1) * squareHeight(),
                        currentPiece.getShape());
            }
        }
}

    /**
     * Metoda powodująca natychmiastowe opadniecie klocka.
     */
    private void dropDown()
    {
        double newY = currentY;
        while (newY > 0) {
            if (!tryMoving(currentPiece, currentX, newY - squareHeight() / (scale * BoardHeight)))
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
        if (!tryMoving(currentPiece, currentX, currentY - squareHeight() / (scale * BoardHeight)))
            pieceDropped();
    }

    /**
     * Metoda czyszcząca planszę z klocków które już upadły.
     */
    private void clearBoard()
    {
        for (int i = 0; i < BoardHeight * BoardWidth; ++i)
            board[i] = Shape.TetroShapes.NoShape;
    }

    /**
     * Metoda wywoływana po skończonym opadaniu klocka.
     * Aktualizuje ona tablicę <code>board</code>.
     */
    private void pieceDropped()
    {
        for (int i = 0; i < 4; ++i) {
            int x = currentX + currentPiece.x(i);
            double y = currentY - currentPiece.y(i);
            board[((int) y * BoardWidth) + x] = currentPiece.getShape();
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
        currentPiece.setRandomShape();
        currentX = BoardWidth / 2  + currentPiece.minX();
        currentY = BoardHeight - 1 + currentPiece.minY();

        if (!tryMoving(currentPiece, currentX, currentY)) {
            currentPiece.setShape(Shape.TetroShapes.NoShape);
            timer.stop();
            isStarted = false;
            statusbar.setText("GAME OVER!");
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
    int counter = 0;
    private boolean tryMoving(Shape newPiece, int newX, double newY)
    {
        for (int i = 0; i < 4; ++i) {
            int x = newX + newPiece.x(i);
            double y = newY - newPiece.y(i);
            if (x<0||x>=BoardWidth || y < 0 || y >= BoardHeight)
                return false;
            if (shapeAt(x, y) != Shape.TetroShapes.NoShape)
                return false;


        }

        if (newY != currentY) {
            statusbar.setText("" + ++counter);
        }

        currentX = newX;
        currentY = newY;
        currentPiece = newPiece;
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
                if (shapeAt(j, i) == Shape.TetroShapes.NoShape) {
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
                internalScore += z * lineScore;
            }

            if (internalScore >= levelScore) {
                internalScore = 0;
                ++currentLevel;
                loadLevel(currentLevel);
            }

            pointsbar.setText("Score: " + String.valueOf(score));
            isFallingFinished = true;
            currentPiece.setShape(Shape.TetroShapes.NoShape);
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
    private void drawBlock(Graphics g, int x, double y, Shape.TetroShapes shape)
    {
        Color colors[] = { new Color(0, 0, 0), new Color(255, 200, 181),
                new Color(255, 180, 181), new Color(255, 160, 181),
                new Color(255, 140, 181), new Color(255, 120, 181),
                new Color(255, 100, 181), new Color(255, 80, 181),
                new Color(255, 60, 181), new Color(255, 40, 181)
        };


        Color color = colors[shape.ordinal()];

        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(color);
        g2.fill(new Rectangle.Double(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2));

        g2.setColor(color.brighter());
        g2.draw(new Line2D.Double(x, y + squareHeight() - 1, x, y));
        g2.draw(new Line2D.Double(x, y, x + squareWidth() - 1, y));

        g2.setColor(color.darker());
        g2.draw(new Line2D.Double(x + 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + squareHeight() - 1));
        g2.draw(new Line2D.Double(x + squareWidth() - 1, y + squareHeight() - 1,
                x + squareWidth() - 1, y + 1));
    }

    /**
     * Metoda restartująca grę.
     */
    void restart() {
        if(!timer.isRunning())
            timer.start();
        clearBoard();
        if (isPaused)
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
    class MyAdapter extends KeyAdapter {

        /**
         * Metoda mówiąca jak program reaguje na naciskanie przycisków na klawiaturze.
         * @param e zdarzenie wygenerowane przez klawiaturę
         */
        public void keyPressed(KeyEvent e) {

            if (!isStarted || currentPiece.getShape() == Shape.TetroShapes.NoShape) {
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
                    tryMoving(currentPiece, currentX - 1, currentY);
                    break;
                case KeyEvent.VK_RIGHT:
                    tryMoving(currentPiece, currentX + 1, currentY);
                    break;
                case KeyEvent.VK_DOWN:
                    oneLineDown();
                    break;
                case KeyEvent.VK_UP:
                    tryMoving(currentPiece.rotate(), currentX, currentY);
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