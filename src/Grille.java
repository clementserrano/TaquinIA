import java.util.ArrayList;
import java.util.List;

public class Grille {

    private static boolean jeton = true;

    private static String[][] courante = new String[][]{
            {"☺", "", "", "", ""},
            {"♠", "", "", "", ""},
            {"☼", "", "", "", ""},
            {"", "", "", "", ""},
            {"♫", "", "", "", ""}
    };

    private final static String[][] terminale = {
            {"", "", "", "", "☼"},
            {"", "♠", "", "", ""},
            {"", "", "", "", "♫"},
            {"", "", "", "", ""},
            {"", "", "", "", "☺"}
    };

    public boolean update(Position prev, Position next, String nom) {
        if (!jeton) return false;
        jeton = false;
        courante[prev.getX()][prev.getY()] = "";
        courante[next.getX()][next.getY()] = nom;
        Taquin.update(prev, next, nom);
        jeton = true;
        //System.out.println(toString());
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

    public List<Position> getAdjacents(Position pos) {
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
