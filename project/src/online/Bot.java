package online;

/**
 * Classe Bot
 * Cette classe représente un joueur artificiel (bot).
 * Tache : 1.4
 * @author: GENTI Anthony
 */
public class Bot extends Player {

    private static final String[] BOT_NAMES = {
            "Alpha",
            "Beta",
            "Gamma",
            "Delta",
            "Epsilon",
            "Zeta",
            "Eta",
            "Theta",
            "Iota",
            "Kappa",
            "Lambda",
            "Mu",
            "Nu",
            "Xi",
            "Omicron",
            "Pi",
            "Rho",
            "Sigma",
            "Tau",
            "Upsilon",
            "Phi",
            "Chi",
            "Psi",
            "Omega"
    };  // Noms des bots

    /**
     * Constructeur Bot()
     * Crée un bot avec un nom aléatoire
     */
    public Bot() {
        super(BOT_NAMES[(int) (Math.random() * BOT_NAMES.length)]);
        System.out.println("Bot " + getName() + " created.");
    }

    /**
     * Methode chooseWord()
     * Cette methode permet au bot de choisir un mot
     * @param words les mots disponibles
     * @return le mot choisi
     */
	public String chooseWord() {
		// TODO : Implementer la logique pour choisir un mot
		return null;
	}

    /**
     * Methode readFeedbacks()
     * Cette methode permet au bot de lire les feedbacks
     * @param feedbacks les feedbacks
     * @return les feedbacks
     */
	public void readFeedbacks() {
		// TODO : Implementer la logique pour lire les feedbacks
	}

    /**
     * Methode readHints()
     * Cette methode permet au bot de lire les indices
     * @param hints les indices
     * @return les indices
     */
    public void readHints() {
		// TODO : Implementer la logique pour lire les indices
	}

    /**
     * Methode play()
     * Cette methode permet au bot de jouer
     * @param feedbacks les feedbacks
     * @return void
     */
    @Override
    public void play() {
        // TODO : Implementer la logique du bot pour jouer
    }
}
