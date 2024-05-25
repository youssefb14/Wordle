package online;

/**
 * Classe HumanPlayer
 * Cette classe représente un joueur humain.
 * @author: GENTI Anthony
 */
public class HumanPlayer extends Player {
	
	private boolean isReady = true; // Default value

    /**
     * Constructeur HumanPlayer()
     * Crée un joueur humain avec un nom donné
     * @param name Nom du joueur
     */
    public HumanPlayer(String name) {
        super(name);
    }
    
    public HumanPlayer(String name, boolean isReady) {
        super(name);
        this.isReady = isReady;
    }

    @Override
    public void play() {
        // TODO : Implementer la logique pour jouer
    }

	public boolean isReady() {
		return isReady;
	}

	public void setReady(boolean isReady) {
		this.isReady = isReady;
	}
}
