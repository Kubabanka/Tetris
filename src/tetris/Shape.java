package tetris;

import java.util.Random;
import java.lang.Math;

/**
*Odwzorowuje klocek tetrisa.
**/
public class Shape {

    /**
     * Zmienna wyliczeniowa przechowujaca wszystkie mozliwe ksztalty klockow.
     **/
    enum TetroShapes {
        /**
         * Pusty klocek (brak bloku)
         */
        NoShape,
        /**
         * Litera Z
         */
        ZShape,
        /**
         * Litera S
         */
        SShape,
        /**
         * Linia prosta
         */
        LineShape,
        /**
         * Litera T
         */
        TShape,
        /**
         * Kwadrat
         */
        SquareShape,
        /**
         * Litera L
         */
        LShape,
        /**
         * Odwrócona litera L
         */
        MirroredLShape,
        /**
         * Kropka
         */
        DotShape,
        /**
         * Slash /
         */
        SlashShape
    };

    /**
     * Zmienna mowiaca jaki ksztalt ma dany klocek.
     */
    private TetroShapes pieceShape;

    /**
     * Tablica dzieki ktorej mozemy rysowac klocki.
     * Przechowuje wspolrzedne poszczegolnych blokow klocka.
     */
    private int coordinates[][];

    /**
     * Tablica przechowujaca współrzędne sluzace do nadawania ksztaltu klockom.
     * Pierwszy nawias odpowiada za typ klocka okreslany przez enum <code>TetroShapes</code>.
     * Dwa pozostale to wspolrzedne blokow tworzacych klocki.
     */
    private int[][][] shapeTable;

    /**
     * Podstawowy konstruktor. Wypelnia on tabee <code>coordinates</code> 0 i ustawia ksztalt klocka na <code>NoShape</code>.
     */
    public Shape() {

        coordinates = new int[4][2];
        setShape(TetroShapes.NoShape);

    }

    /**
     * Metoda ustawiajaca kształt klocka na ten podany w parametrze.
     * Kształty zbudowane są z maksymalnie 4 bloków rozmieszczonych wokół środka układu współrzędnych x-y, tj. punktu {0, 0}
     * Każdy kształt to tablica dwuwymiarowa zawierająca po 4 współrzędne dla każdego z bloków.
     * NoShape to oznaczenie pustego kształtu.
     * @param shape oczekiwany kształt klocka
     */
    public void setShape(TetroShapes shape) {

        // Ustalanie współrzędnych poszczególnych kształtów
        shapeTable = new int[][][]{
                {{0, 0}, {0, 0}, {0, 0}, {0, 0}},   //NoShape
                {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},//ZShape
                {{0, -1}, {0, 0}, {1, 0}, {1, 1}},  //SShape
                {{0, -1}, {0, 0}, {0, 1}, {0, 2}},  //LineShape
                {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},  //TShape
                {{0, 0}, {1, 0}, {0, 1}, {1, 1}},   //SquareShape
                {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},//LShape
                {{1, -1}, {0, -1}, {0, 0}, {0, 1}}, //MirroredLShape
                {{0, 1}, {0, 1}, {0, 1}, {0, 1}},   //DotShape
                {{-1, -1}, {0, 0}, {1, 1}, {1, 1}}  //SlashShape
        };

        //ustalanie ksztaltu aktualnego klocka i==x || y j==ktore
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; ++j) {
                coordinates[i][j] = shapeTable[shape.ordinal()][i][j];
            }
        }
        pieceShape = shape;

    }

    /**
     * Metoda ustawiajaca wspolrzedna <code>index</code> na dany x.
     * @param index wspolrzedna x bloku klocka
     * @param x wartosc na jaka… zmieniamy obecna… wartosc
     */
    private void setX(int index, int x) {
        coordinates[index][0] = x;
    }

    /**
     * Metoda ustawiajaca wspolrzedna <code>index</code> na dany y.
     * @param index wspolrzedna y bloku klocka
     * @param y wartosc na jaka… zmieniamy obecna… wartosc
     */
    private void setY(int index, int y) {
        coordinates[index][1] = y;
    }

    /**
     * Metoda zwracajaca wartosc wspolrzednej x bloku klocka
     * @param index wartosc indeksu wspolrzednej x
     * @return wartosc wspolrzednej x
     */
    public int x(int index) {
        return coordinates[index][0];
    }

    /**
     * Metoda zwracajaca wartosc wspolrzednej y bloku klocka
     * @param index wartosc indeksu wspolrzednej y
     * @return Wartosc wspolrzednej y
     */
    public int y(int index) {
        return coordinates[index][1];
    }

    /**
     * Metoda zwracajaca wartosc wspolrzednej x bloku klocka
     * @return Ksztalt klocka
     */
    public TetroShapes getShape() {
        return pieceShape;
    }

    /**
     * Metoda losujaca kasztalt klocka.
     */
    public void setRandomShape() {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 9 + 1;
        TetroShapes[] values = TetroShapes.values();
        setShape(values[x]);
    }

    /**
     * Metoda zwracająca minimalną wartość współrzędnej x.
     * @return Minimalną wartość współrzędnej x.
     */
    public int minX() {
        int m = coordinates[0][0];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coordinates[i][0]);
        }
        return m;
    }

    /**
     * Metoda zwracająca minimalną wartość współrzędnej y.
     * @return Minimalną wartość współrzędnej y.
     */
    public int minY() {
        int m = coordinates[0][1];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coordinates[i][1]);
        }
        return m;
    }

    /**
     * Metoda obracajaca klocek.
     * Jezeli klocek jest kwadratowy zwraca <code>this</code>,
     * w innym przypadku zamienia miejscami wspolrzedne
     * @return Obrocony klocek.
     */
    public Shape rotate() {
        if (pieceShape == TetroShapes.SquareShape || pieceShape == TetroShapes.DotShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }

        Sound.play("Sounds/rotate.wav");
        return result;

    }

}