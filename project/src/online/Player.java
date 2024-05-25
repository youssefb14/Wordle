package online;

/**
 * Classe Player
 * Cette classe représente un joueur.
 * @author: GENTI Anthony
 */
public abstract class Player {
    protected String name;  // Nom du joueur
    protected int score;    // Score du joueur
    protected boolean isInGame; // Indique si le joueur est dans une partie

    /**
     * Constructeur Player()
     * Crée un joueur avec un nom donné
     * @param name Nom du joueur
     */
    public Player(String name) {
        this.name = name;   // On initialise le nom du joueur
        this.score = 0;    // On initialise le score du joueur à 0
        this.isInGame = false; // On initialise le joueur à ne pas être dans une partie
    }

    /**
     * Méthode getName()
     * Cette méthode permet de récupérer le nom du joueur
     * @return name Nom du joueur
     */
    public String getName() {
        return name;
    }

    /**
     * Méthode getScore()
     * Cette méthode permet de récupérer le score du joueur
     * @return score Score du joueur
     */
    public int getScore() {
        return score;
    }

    /**
     * Méthode setScore()
     * Cette méthode permet de définir le score du joueur
     * @param score Score du joueur
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Méthode isInGame()
     * Cette méthode permet de savoir si le joueur est dans une partie
     * @return isInGame Indique si le joueur est dans une partie
     */
    public boolean isInGame() {
        return isInGame;
    }

    /**
     * Méthode setInGame()
     * Cette méthode permet de définir si le joueur est dans une partie
     * @param isInGame Indique si le joueur est dans une partie
     */
    public void setInGame(boolean isInGame) {
        this.isInGame = isInGame;
    }

    /**
     * Méthode addScore()
     * Cette méthode permet d'ajouter un score au joueur
     * @param increment Score à ajouter
     */
    public void addScore(int increment) {
        this.score += increment;
    }

    /**
     * Méthode resetScore()
     * Cette méthode permet de réinitialiser le score du joueur
     */
    public void resetScore() {
        this.score = 0;
    }

    /**
     * Méthode play()
     * Cette méthode permet au joueur de jouer
     */
    public abstract void play();

}
