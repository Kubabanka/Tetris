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
    enum Tetrominoes {
        NoShape, ZShape, SShape, LineShape,
        TShape, SquareShape, LShape, MirroredLShape, DotShape
    };

    /**
     * Zmienna mowiaca jaki ksztalt ma dany klocek.
     */
    private Tetrominoes pieceShape;

    /**
     * Tablica dzieki ktorej mozemy rysowac klocki.
     * Przechowuje wspolrzedne poszczegolnych blokow klocka.
     */
    private int coords[][];

    /**
     * Tablica przechowujaca wspolrzedne sluzace do nadawania ksztaltu klockom.
     * Pierwszy nawias odpowiada za typ klocka okreslany przez enum <code>Tetrominoes</code>.
     * Dwa pozostale to wspolrzedne blokow tworzacych klocki.
     */
    private int[][][] coordsTable;

    /**
     * Podstawowy konstruktor. Wypelnia on tabee <code>coords</code> 0 i ustawia ksztalt klocka na <code>NoShape</code>.
     */
    public Shape() {

        coords = new int[4][2];
        setShape(Tetrominoes.NoShape);

    }

    /**
     *Metoda ustawiajaca ksztalt klocka na ten podany w parametrze.
     * @param shape oczekiwany ksztalt klocka
     */
    public void setShape(Tetrominoes shape) {

        //ustalanie wspolrzednych poszczegolnych ksztaltow
        coordsTable = new int[][][]{
                {{0, 0}, {0, 0}, {0, 0}, {0, 0}},//NoShape
                {{0, -1}, {0, 0}, {-1, 0}, {-1, 1}},//ZShape
                {{0, -1}, {0, 0}, {1, 0}, {1, 1}},//SShape
                {{0, -1}, {0, 0}, {0, 1}, {0, 2}},//LineShape
                {{-1, 0}, {0, 0}, {1, 0}, {0, 1}},//TShape
                {{0, 0}, {1, 0}, {0, 1}, {1, 1}},//SquareShape
                {{-1, -1}, {0, -1}, {0, 0}, {0, 1}},//LShape
                {{1, -1}, {0, -1}, {0, 0}, {0, 1}},//MirroredLShape
                {{0, 1}, {0, 1}, {0, 1}, {0, 1}} //kropka?
        };

        //ustalanie ksztaltu aktualnego klocka i==x || y j==ktore
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 2; ++j) {
                coords[i][j] = coordsTable[shape.ordinal()][i][j];
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
        coords[index][0] = x;
    }

    /**
     * Metoda ustawiajaca wspolrzedna <code>index</code> na dany y.
     * @param index wspolrzedna y bloku klocka
     * @param y wartosc na jaka… zmieniamy obecna… wartosc
     */
    private void setY(int index, int y) {
        coords[index][1] = y;
    }

    /**
     * Metoda zwracajaca wartosc wspolrzednej x bloku klocka
     * @param index wartosc indeksu wspolrzednej x
     * @return wartosc wspolrzednej x
     */
    public int x(int index) {
        return coords[index][0];
    }

    /**
     * Metoda zwracajaca wartosc wspolrzednej y bloku klocka
     * @param index wartosc indeksu wspolrzednej y
     * @return Wartosc wspolrzednej y
     */
    public int y(int index) {
        return coords[index][1];
    }

    /**
     * Metoda zwracajaca wartosc wspolrzednej x bloku klocka
     * @return Ksztalt klocka
     */
    public Tetrominoes getShape() {
        return pieceShape;
    }

    /**
     * Metoda losujaca kasztalt klocka.
     */
    public void setRandomShape() {
        Random r = new Random();
        int x = Math.abs(r.nextInt()) % 8 + 1;
        Tetrominoes[] values = Tetrominoes.values();
        setShape(values[x]);
    }

    /**
     *
     * @return Minimalna wartosc wspolrzednej x.
     */
    public int minX() {
        int m = coords[0][0];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][0]);
        }
        return m;
    }

    /**
     *
     * @return Minimalna wartosc wspolrzednej y.
     */
    public int minY() {
        int m = coords[0][1];
        for (int i = 0; i < 4; i++) {
            m = Math.min(m, coords[i][1]);
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
        if (pieceShape == Tetrominoes.SquareShape)
            return this;

        Shape result = new Shape();
        result.pieceShape = pieceShape;

        for (int i = 0; i < 4; ++i) {
            result.setX(i, y(i));
            result.setY(i, -x(i));
        }
        return result;
    }

}