import java.util.ArrayList;
import java.util.List;

public class Grille {

    private static boolean jeton = true;

    private static String[][] courante = new String[][]{
            {"☺", "G", "", "", "O"},
            {"♠", "", "N", "E", ""},
            {"☼", "", "", "C", "H"},
            {"A", "J", "B", "F", "I"},
            {"♫", "", "", "L", "K"}
    };

    private final static String[][] terminale = {
            {"", "E", "B", "F", "☼"},
            {"G", "♠", "H", "", "I"},
            {"", "J", "K", "L", "♫"},
            {"C", "", "O", "N", ""},
            {"", "A", "", "", "☺"}
    };

    public synchronized boolean update(Position prev, Position next, String nom) {
        if (!jeton || !courante[next.getX()][next.getY()].equals("")) return false;
        jeton = false;
        courante[prev.getX()][prev.getY()] = "";
        courante[next.getX()][next.getY()] = nom;
        Taquin.update(prev, next, nom);
        jeton = true;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String get(Position pos) {
        if (pos.getX() < 0 || pos.getX() >= courante.length
                || pos.getY() < 0 || pos.getY() >= courante[0].length) {
            return "null";
        }
        return courante[pos.getX()][pos.getY()];
    }

    public boolean estReconstitue() {
        for (int i = 0; i < courante.length; i++) {
            for (int j = 0; j < courante[0].length; j++) {
                if (!courante[i][j].equals(terminale[i][j])) {
                    return false;
                }
            }
        }
        return true;
    }

    public synchronized List<Position> getAdjacents(Position pos) {
        List<Position> positions = new ArrayList<>();
        Position gauche = new Position(pos.getX(), pos.getY() - 1);
        Position droite = new Position(pos.getX(), pos.getY() + 1);
        Position haut = new Position(pos.getX() - 1, pos.getY());
        Position bas = new Position(pos.getX() + 1, pos.getY());

        String sHaut = get(haut);
        String sBas = get(bas);
        String sGauche = get(gauche);
        String sDroite = get(droite);

        if (sHaut.equals("")) positions.add(haut);
        if (sBas.equals("")) positions.add(bas);
        if (sGauche.equals("")) positions.add(gauche);
        if (sDroite.equals("")) positions.add(droite);

        return positions;
    }

    public static String[][] get() {
        return courante;
    }

    public static Position findTerminale(String nom) {
        for (int i = 0; i < terminale.length; i++) {
            for (int j = 0; j < terminale[0].length; j++) {
                if (terminale[i][j].equals(nom)) {
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    public boolean isAllSideTermine() {
        for (int i = 0; i < courante.length; i++) {
            Position pos = new Position(i, 0);
            if (!pos.equals(findTerminale(courante[i][0]))) return false;

            pos = new Position(i, courante[0].length - 1);
            if (!pos.equals(findTerminale(courante[i][courante[0].length - 1]))) return false;
        }

        for (int j = 0; j < courante[0].length; j++) {
            Position pos = new Position(0, j);
            if (!pos.equals(findTerminale(courante[0][j]))) return false;

            pos = new Position(courante.length - 1, j);
            if (!pos.equals(findTerminale(courante[courante.length - 1][j]))) return false;
        }

        return true;
    }

    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < courante.length; i++) {
            for (int j = 0; j < courante[0].length; j++) {
                res += (courante[i][j].equals("") ? "." : courante[i][j]) + "";
            }
            res += "\n";
        }
        return res;
    }
}
