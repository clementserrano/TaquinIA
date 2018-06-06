import java.util.ArrayList;
import java.util.List;

public class Agent extends Thread {
    // ----- Connaissances -----

    // numero agent
    private String nom;

    // Position initiale
    private Position pos;

    // Position terminale
    private Position posEnd;

    // Déplacements : garder un trace des déplacements pour éviter un blocage
    private List<Position> deplacements;

    // Grille static, gérer la concurrence (2 individus essaient de modifier simultanément une case
    public static Grille grille = new Grille();

    // message, méthode simple : structure partagée pour stocker les messages de tout le monde
    // structure de communication
    public static Communication communication = new Communication();

    public Agent(String nom, Position pos, Position posEnd) {
        this.nom = nom;
        this.pos = pos;
        this.posEnd = posEnd;
        this.deplacements = new ArrayList<>();
    }

    // ----- Comportement -----

    // Run, cerveau de l'agent
    public void run() {
        // while : tant que le puzzle non reconstitué do
        while (!grille.estReconstitue()) {
            boolean ok = false;
            Position newPos = new Position(0, 0);
            while (!ok) {
                // meilleure position
                if (pos.equals(posEnd)) {
                    newPos = null;
                } else {
                    newPos = meilleurePosition();
                }

                // traiter ses messages, même si en position terminale
                if (pos.equals(posEnd)) {
                    newPos = null;
                }

                // décider la stratégie à adopter
                if (newPos != null) {
                    ok = grille.update(pos, newPos, nom);
                } else {
                    ok = true;
                }
            }
            if (newPos != null) {
                deplacements.add(newPos);
                pos = newPos;
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(nom + " a fini !");
    }

    // fonction meilleure position, cherche à satisfraire son but
    // en premier temps, rapprochement brut
    public Position meilleurePosition() {
        List<Position> positions = grille.getAdjacents(pos);

        Position best = positions.get(0);
        int distance = posEnd.distance(positions.get(0));

        for (Position adjPos : positions) {
            int adjDistance = posEnd.distance(adjPos);
            if (adjDistance < distance) {
                best = adjPos;
                distance = adjDistance;
            }
        }

        return best;
    }
}
