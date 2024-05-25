package online;

public class GameInfo {
    private Integer  serverPort; // Port du serveur
    private Integer  numberOfPlayers; // Nombre de joueurs
    private String difficulty; // Niveau de difficulté
    private Integer  gameDuration; // Durée de la partie
    private Integer  numberOfWords; // Nombre de mots
    private Integer  maxPlayers; // Nombre maximum de joueurs
    private String hostName; // Nom de l'hôte

    public GameInfo(Integer serverPort, Integer numberOfPlayers, String difficulty, Integer gameDuration, Integer numberOfWords, Integer maxPlayers, String hostName) {
        this.serverPort = serverPort;
        this.numberOfPlayers = numberOfPlayers;
        this.difficulty = difficulty;
        this.gameDuration = gameDuration;
        this.numberOfWords = numberOfWords;
        this.maxPlayers = maxPlayers;
        this.hostName = hostName;
    }

    public static GameInfo fromString(String gameInfoStr) {
        String[] parts = gameInfoStr.split(",");
        if (parts.length < 7) {
            System.out.println("Format incorrect des données GameInfo: " + gameInfoStr);
            return null;
        }
        Integer serverPort = Integer.parseInt(parts[0]);
        Integer numberOfPlayers = Integer.parseInt(parts[1]);
        String difficulty = parts[2];
        Integer gameDuration = Integer.parseInt(parts[3]);
        Integer numberOfWords = Integer.parseInt(parts[4]);
        Integer maxPlayers = Integer.parseInt(parts[5]);
        String hostName = parts[6];
        return new GameInfo(serverPort, numberOfPlayers, difficulty, gameDuration, numberOfWords, maxPlayers, hostName);
    }
    
    public void update(String newDifficulty, String newDuration, String newNumberOfWords) {
        if (newDifficulty != null && !newDifficulty.isEmpty()) {
            this.difficulty = newDifficulty;
        }
        try {
            this.gameDuration = (newDuration != null && !newDuration.isEmpty()) ? Integer.parseInt(newDuration) : this.gameDuration;
        } catch (NumberFormatException e) {
            System.out.println("Erreur lors de la conversion de la durée du jeu: " + e.getMessage());
        }
        try {
            this.numberOfWords = (newNumberOfWords != null && !newNumberOfWords.isEmpty()) ? Integer.parseInt(newNumberOfWords) : this.numberOfWords;
        } catch (NumberFormatException e) {
            System.out.println("Erreur lors de la conversion du nombre de mots: " + e.getMessage());
        }
    }
    
    @Override
    public String toString() {
        return serverPort + "," + numberOfPlayers + "," + difficulty + "," + gameDuration + "," + numberOfWords + "," + maxPlayers + "," + hostName;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public Integer getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public Integer getGameDuration() {
        return gameDuration;
    }

    public Integer getNumberOfWords() {
        return numberOfWords;
    }

    public Integer getMaxPlayers() {
        return maxPlayers;
    }

    public String getHostName() {
        return hostName;
    }

	public void setDifficulty(String difficulty2) {
		this.difficulty = difficulty2;
	}

	public void setGameDuration(int durationInt) {
		this.gameDuration = durationInt;
    }

	public void setNumberOfWords(int wordsInt) {
		this.numberOfWords = wordsInt;
	}
	
	public void setMaxPlayers(int playersInt) {
		this.maxPlayers = playersInt;
	}
	
	public void updateFromConfig(String config) {
	    // Exemple de mise à jour, dépend de la structure de vos données
	    String[] parts = config.split(",");
	    if (parts.length >= 4) {
	        try {
	            this.setDifficulty(parts[0]);
	            this.setGameDuration(Integer.parseInt(parts[1]));
	            this.setNumberOfWords(Integer.parseInt(parts[2]));
	            this.setMaxPlayers(Integer.parseInt(parts[3]));
	        } catch (NumberFormatException e) {
	            System.out.println("Error updating GameInfo from config: " + e.getMessage());
	        }
	    }
	}
}