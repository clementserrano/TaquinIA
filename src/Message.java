public class Message {

    enum Performatif {
        REQUEST
    }

    enum Action {
        MOVE
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

    public Message(Agent emetteur, Agent destinataire, Performatif performatif, Action action, Position pos) {
        this.emetteur = emetteur;
        this.destinataire = destinataire;
        this.performatif = performatif;
        this.action = action;
        this.pos = pos;
    }

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
