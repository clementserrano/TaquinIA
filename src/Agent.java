import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
            List<Position> freePos = new ArrayList<>();
            boolean ok = false;
            Position newPos = null;
            while (!ok) {
                // traiter ses messages, même si en position terminale
                List<Message> listMessages = communication.get(nom);
                if (listMessages != null) {
                    for (Message message : listMessages) {
                        switch (message.getPerformatif()) {
                            case REQUEST:
                                switch (message.getAction()) {
                                    case MOVE:
                                        freePos.add(message.getPos());
                                        break;
                                }
                                break;
                            case INFO:
                                switch (message.getAction()) {
                                    case MERCI:
                                        freePos.remove(message.getPos());
                                        break;
                                }
                                break;
                        }
                        listMessages.remove(message);
                    }
                }

                // meilleure position
                if (pos.equals(posEnd) && !freePos.contains(pos)) {
                    newPos = null;
                } else if (posEnd.getY() == 0 || posEnd.getY() == 0 || grille.isAllSideTermine()) {
                    newPos = meilleurePosition(freePos);
                } else {
                    List<Position> poss = grille.getAdjacents(pos);
                    if (poss.size() > 0) {
                        newPos = poss.get(0);
                    } else {
                        Position gauche = new Position(pos.getX(), pos.getY() - 1);
                        Position droite = new Position(pos.getX(), pos.getY() + 1);
                        Position haut = new Position(pos.getX() - 1, pos.getY());
                        Position bas = new Position(pos.getX() + 1, pos.getY());
                        List<Position> randomPos = new ArrayList<>();
                        if (!grille.get(gauche).equals("null")) randomPos.add(gauche);
                        if (!grille.get(droite).equals("null")) randomPos.add(droite);
                        if (!grille.get(haut).equals("null")) randomPos.add(haut);
                        if (!grille.get(bas).equals("null")) randomPos.add(bas);
                        Random r = new Random();
                        newPos = randomPos.get(r.nextInt(randomPos.size()));
                    }
                }

                if (newPos != null) {
                    sendToEveryone(newPos, Message.Performatif.REQUEST, Message.Action.MOVE);
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
                sendToEveryone(newPos, Message.Performatif.INFO, Message.Action.MERCI);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println(nom + " a fini !");
    }

    // fonction meilleure position, cherche à satisfraire son but
    // en premier temps, rapprochement brut
    public synchronized Position meilleurePosition(List<Position> freePos) {
        List<Position> positions = grille.getAdjacents(pos);
        for (Position pos : freePos) {
            positions.remove(pos);
        }

        if (positions.size() == 0) return null;

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

    private void sendToEveryone(Position newPos, Message.Performatif performatif, Message.Action action) {
        for (String agentDestinataire : communication.keySet()) {
            Message newMessage = new Message();
            newMessage.setDestinataire(Taquin.findAgent(agentDestinataire));
            newMessage.setEmetteur(this);
            newMessage.setPerformatif(performatif);
            newMessage.setAction(action);
            newMessage.setPos(newPos);
            communication.get(agentDestinataire).add(newMessage);
        }
    }
}
