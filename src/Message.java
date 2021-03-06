public class Message {

    enum Performatif {
        REQUEST,
        INFO
    }

    enum Action {
        MOVE,
        MERCI
    }

    // émetteur
    private Agent emetteur;

    // destinataire
    private Agent destinataire;

    // performatif : demande "Request"
    private Performatif performatif;

    // Action : "Move"
    private Action action;

    // Position à libérer : ...
    private Position pos;

    public Agent getEmetteur() {
        return emetteur;
    }

    public void setEmetteur(Agent emetteur) {
        this.emetteur = emetteur;
    }

    public Agent getDestinataire() {
        return destinataire;
    }

    public void setDestinataire(Agent destinataire) {
        this.destinataire = destinataire;
    }

    public Performatif getPerformatif() {
        return performatif;
    }

    public void setPerformatif(Performatif performatif) {
        this.performatif = performatif;
    }

    @Override
    public String toString() {
        return "{"  + emetteur + " : " + pos + '}';
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public Position getPos() {
        return pos;
    }

    public void setPos(Position pos) {
        this.pos = pos;
    }
}
