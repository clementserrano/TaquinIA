import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grille {

    private final int timeToSleep = 0;

    private boolean jeton = true;

    private String[][] courante;

    private String[][] terminale;

    public synchronized boolean update(Position prev, Position next, String nom) {
        try {
            if (!jeton || !courante[next.getX()][next.getY()].equals("")) return false;
            jeton = false;
            courante[prev.getX()][prev.getY()] = "";
            courante[next.getX()][next.getY()] = nom;
            Taquin.update(prev, next, nom);
            jeton = true;
            Thread.sleep(timeToSleep);
            return true;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
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

    public synchronized List<Position> getAdjacents(Position pos, boolean isEmpty) {
        List<Position> positions = new ArrayList<>();
        Position gauche = new Position(pos.getX(), pos.getY() - 1);
        Position droite = new Position(pos.getX(), pos.getY() + 1);
        Position haut = new Position(pos.getX() - 1, pos.getY());
        Position bas = new Position(pos.getX() + 1, pos.getY());

        String sHaut = get(haut);
        String sBas = get(bas);
        String sGauche = get(gauche);
        String sDroite = get(droite);

        if (isAddable(haut, sHaut, isEmpty)) positions.add(haut);
        if (isAddable(bas, sBas, isEmpty)) positions.add(bas);
        if (isAddable(gauche, sGauche, isEmpty)) positions.add(gauche);
        if (isAddable(droite, sDroite, isEmpty)) positions.add(droite);

        return positions;
    }

    private boolean isAddable(Position pos, String nom, boolean isEmpty) {
        int border = Agent.getBorder();
        return nom.equals("") || (!nom.equals("null") && !isEmpty)
                /*&& !(pos.getX() < border || pos.getX() > courante[0].length - 1 - border ||
                pos.getY() < border || pos.getY() > courante.length - 1 - border)*/;
    }

    public String[][] get() {
        return courante;
    }

    public Position findTerminale(String nom) {
        for (int i = 0; i < terminale.length; i++) {
            for (int j = 0; j < terminale[0].length; j++) {
                if (terminale[i][j].equals(nom)) {
                    return new Position(i, j);
                }
            }
        }
        return null;
    }

    public boolean isBorderTermine() {
        return Taquin.getAgents().stream().filter(Agent::isBorder).allMatch(Agent::isTermine);
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

    public void create(int tailleGrille, int pourcentageAgent) {
        courante = new String[tailleGrille][tailleGrille];
        terminale = new String[tailleGrille][tailleGrille];

        int nbAgents = (tailleGrille * tailleGrille * pourcentageAgent) / 100;
        if (nbAgents == tailleGrille * tailleGrille) nbAgents = tailleGrille * tailleGrille - 1;

        List<Integer> noms = new ArrayList<>();

        int nom = 1;
        for (int i = 0; i < terminale.length; i++) {
            for (int j = 0; j < terminale[0].length; j++) {
                if (nom <= nbAgents) {
                    terminale[i][j] = nom + "";
                    noms.add(nom);
                    nom++;
                }else{
                    terminale[i][j] = "";
                }
            }
        }

        for (int i = 0; i < terminale.length; i++) {
            for (int j = 0; j < terminale[0].length; j++) {
                if (!noms.isEmpty()) {
                    Random r = new Random();
                    Integer name = noms.get(r.nextInt(noms.size()));
                    courante[i][j] = name + "";
                    noms.remove(name);
                }else{
                    courante[i][j] = "";
                }
            }
        }
    }
}
