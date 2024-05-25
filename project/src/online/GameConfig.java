package online;


public class GameConfig {
    private final int maxPlayers;  // Nombre maximum de joueurs
    private int gameDuration;  // Durée de la partie
    private int difficultyLevel; // Niveau de difficulté
    private int maxWordFindTime; // Temps maximum pour trouver un mot
    private int wordLength; // Longueur du mot
    private int numberOfWords; // Nombre de mots
    private final int defaultDifficultyLevel; // Niveau de difficulté par défaut
    private final int defaultWordLength; // Longueur de mot par défaut

    public GameConfig(int maxPlayers, int gameDuration, int difficultyLevel, int wordLength) {
        this.maxPlayers = maxPlayers; // On initialise le nombre maximum de joueurs
        this.gameDuration = gameDuration; // On initialise la durée de la partie
        this.difficultyLevel = difficultyLevel; // On initialise le niveau de difficulté
        this.defaultDifficultyLevel = difficultyLevel; // On initialise le niveau de difficulté par défaut
        this.defaultWordLength = wordLength; // On initialise la longueur de mot par défaut
    }

    public GameConfig(int maxPlayers, int gameDuration, int difficultyLevel) {
        this.maxPlayers = maxPlayers; // On initialise le nombre maximum de joueurs
        this.gameDuration = gameDuration; // On initialise la durée de la partie
        this.difficultyLevel = difficultyLevel; // On initialise le niveau de difficulté
        this.defaultDifficultyLevel = difficultyLevel; // On initialise le niveau de difficulté par défaut
        this.defaultWordLength = 5; // On initialise la longueur de mot par défaut
    }

    public int getMaxWordFindTime() {
        return maxWordFindTime;
    }

    public void setMaxWordFindTime(int maxWordFindTime) {
        this.maxWordFindTime = maxWordFindTime;
    }

    public int getWordLength() {
        return wordLength;
    }

    public void setWordLength(int wordLength) {
        this.wordLength = wordLength;
    }

    public int getNumberOfWords() {
        return numberOfWords;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getGameDuration() {
        return gameDuration;
    }

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public int getDefaultDifficultyLevel() {
        return this.defaultDifficultyLevel;
    }

    public int getDefaultWordLength() {
        return this.defaultWordLength;
    }

    public void setDifficultyLevel(int difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public void setGameDuration(int gameDuration) {
        this.gameDuration = gameDuration;
    }

	public void setNumberOfWords(int numberOfWords) {
	    this.numberOfWords = numberOfWords;
	}

}
