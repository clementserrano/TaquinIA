import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Agent extends Thread {
    // ----- Connaissances -----

    private static final int timeToSleep = 0;

    // numero agent
    private String nom;

    // Position initiale
    private Position pos;

    // Position terminale
    private Position posEnd;

    private List<Position> freePos;

    // Grille static, gérer la concurrence (2 individus essaient de modifier simultanément une case
    public static Grille grille = new Grille();

    // message, méthode simple : structure partagée pour stocker les messages de tout le monde
    // structure de communication
    public static Communication communication = new Communication();

    private static int border = 0;

    public Agent(String nom, Position pos, Position posEnd) {
        this.nom = nom;
        this.pos = pos;
        this.posEnd = posEnd;
        this.freePos = new ArrayList<>();
    }

    // ----- Comportement -----

    // Run, cerveau de l'agent
    public void run() {
        try {
            // while : tant que le puzzle non reconstitué do
            while (!grille.estReconstitue()) {
                Position newPos = null;
                // traiter ses messages, même si en position terminale
                List<Message> listMessages = communication.getAndClear(nom);
                if (listMessages != null && listMessages.size() > 0) {
                    for (Message message : listMessages) {
                        if (message != null) {
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
                        }
                    }
                }

                // décider la stratégie à adopter
                if (isBorder() && !isTermine()) {
                    newPos = meilleurePosition();
                }

                if (freePos.contains(pos) && !(isTermine() && isBorderEx())) {
                    List<Position> poss = grille.getAdjacents(pos, true);
                    Random r = new Random();
                    if (poss.size() > 0) {
                        newPos = poss.get(r.nextInt(poss.size()));
                        //newPos = poss.get(0);
                    } else {
                        poss = grille.getAdjacents(pos, false);
                        newPos = poss.get(r.nextInt(poss.size()));
                        //newPos = poss.get(0);
                    }
                }

                if (newPos != null) {
                    if (grille.update(pos, newPos, nom)) {
                        sendToEveryone(newPos, Message.Performatif.INFO, Message.Action.MERCI);
                        pos = newPos;
                        updateBorder();
                        Thread.sleep(timeToSleep);
                    } else {
                        sendToEveryone(newPos, Message.Performatif.REQUEST, Message.Action.MOVE);
                    }
                }
            }
            System.out.println(nom + " a fini !");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // fonction meilleure position, cherche à satisfraire son but
    // en premier temps, rapprochement brut
    private synchronized Position meilleurePosition() {
        List<Position> positions = grille.getAdjacents(pos, false);

        if (positions.size() == 0) return null;

        List<Position> best = new ArrayList<>();
        int distance = posEnd.distance(positions.get(0));

        for (Position adjPos : positions) {
            int adjDistance = posEnd.distance(adjPos);
            if (adjDistance <= distance) {
                best.add(adjPos);
                distance = adjDistance;
            }
        }

        Random r = new Random();

        return best.get(r.nextInt(best.size()));
    }

    private void sendToEveryone(Position newPos, Message.Performatif performatif, Message.Action action) {
        for (String agentDestinataire : communication.keySet()) {
            Message newMessage = new Message();
            newMessage.setDestinataire(Taquin.findAgent(agentDestinataire));
            newMessage.setEmetteur(this);
            newMessage.setPerformatif(performatif);
            newMessage.setAction(action);
            newMessage.setPos(newPos);
            boolean ok = false;
            while(!ok){
                try{
                    communication.get(agentDestinataire).add(newMessage);
                    ok = true;
                }catch (ArrayIndexOutOfBoundsException e){
                    ok = false;
                }
            }
        }
    }

    public boolean isBorder() {
        return posEnd.getX() <= border || posEnd.getX() >= grille.get()[0].length - 1 - border ||
                posEnd.getY() <= border || posEnd.getY() >= grille.get().length - 1 - border;
    }

    public boolean isBorderEx() {
        return posEnd.getX() < border || posEnd.getX() > grille.get()[0].length - 1 - border ||
                posEnd.getY() < border || posEnd.getY() > grille.get().length - 1 - border;
    }

    public boolean isTermine() {
        return pos.equals(posEnd);
    }

    private synchronized void updateBorder() {
        if (grille.isBorderTermine()) {
            Agent.setBorder(Agent.getBorder() + 1);
        }
    }

    @Override
    public String toString() {
        return nom;
    }

    public String getNom() {
        return nom;
    }

    public static int getBorder() {
        return border;
    }

    public static void setBorder(int border) {
        Agent.border = border;
    }

    public static Communication getCommunication() {
        return communication;
    }

    public static Grille getGrille() {
        return grille;
    }
}
