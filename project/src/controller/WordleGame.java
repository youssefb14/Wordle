package controller;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color; // Pour pouvoir utiliser les couleurs
import model.FrenchWordChecker;     // Pour pouvoir utiliser le dictionnaire
import model.RandomWordSelector;    // Pour pouvoir utiliser le sélecteur de mots

/*
 * Classe WordleGame
 * Cette classe permet de gérer le jeu Wordle.
 * Tache : 2.1.4
 * author: BOUDOOUNT Youssef
 */
public class WordleGame
{
    private RandomWordSelector wordSelector;    // On utilise un variable de type RandomWordSelector qui va contenir le sélecteur de mots
    private static String targetWord;        // On utilise un variable de type String qui va contenir le mot cible

    /*
     * Méthode getRandomWord
     * Cette méthode permet de retourner un mot aléatoire d'une longueur donnée.
     * @param wordLength: la longueur du mot
     * @return String: le mot aléatoire de la longueur donnée
     * author: BOUDOOUNT Youssef
     */
    public String getRandomWord(int wordLength)
    {
        return wordSelector.getRandomWord(wordLength);
    }

    /*
     * Méthode getSelectedWord
     * Cette méthode permet de retourner le mot cible.
     * @param void
     * @return String: le mot cible
     * author: BOUDOOUNT Youssef
     */
    public String getSelectedWord()
    {
        return WordleGame.targetWord;
    }

    /*
     * Constructeur de la classe WordleGame
     * Ce constructeur permet d'initialiser le jeu Wordle.
     * @param motsJsonFile: le fichier JSON contenant les mots
     * @param generalDictionaryFile: le fichier contenant le dictionnaire
     * @return void
     * author: BOUDOOUNT Youssef
     */
    public WordleGame(String motsJsonFile, String generalDictionaryFile)
    {
        wordSelector = new RandomWordSelector(motsJsonFile);   // On initialise le sélecteur de mots avec le fichier JSON contenant les mots à deviner
        new FrenchWordChecker(generalDictionaryFile);       // On initialise le dictionnaire avec le fichier contenant le dictionnaire
        targetWord = wordSelector.getRandomWord(5); // On initialise le mot cible avec un mot de 5 lettres intialment (jusqu'a ajouts de niveaux)
    }

    /*
     * Méthode compareWords
     * Cette méthode permet de comparer deux mots et de retourner une liste de couleurs.
     * @param userGuess: le mot proposé par l'utilisateur
     * @param targetWord: le mot cible
     * @return feedbackColors: la liste de couleurs
     * author: BOUDOOUNT Youssef
     * source: https://stackoverflow.com/questions/5238491/check-if-string-contains-only-letters
     */
    public static List<Color> compareWords(String userGuess, String targetWord)
    {
        userGuess = userGuess.toUpperCase();    // On convertit le mot proposé par l'utilisateur en majuscules
        targetWord = targetWord.toUpperCase();  // On convertit le mot cible en majuscules

        System.out.println("compareWords ---- User Guess: " + userGuess);   // On affiche le mot proposé par l'utilisateur dans la console pour le débogage
        System.out.println("compareWords ---- Target Word: " + targetWord); // On affiche le mot cible dans la console pour le débogage


        if (userGuess.length() != targetWord.length())  // Si les deux mots n'ont pas la même longueur
        {
            System.out.println("compareWords -- if -- User Guess: " + userGuess);   // On affiche le mot proposé par l'utilisateur dans la console pour le débogage
            System.out.println("compareWords -- if -- Target Word: " + targetWord); // On affiche le mot cible dans la console pour le débogage
            throw new IllegalArgumentException("Les mots à comparer doivent avoir la même longueur.");  // On lève une exception qui indique que les mots doivent avoir la même longueur
        }

        List<Color> feedbackColors = new ArrayList<>(); // On initialise une liste de couleurs qui va contenir les couleurs de feedback pour chaque caractère

        for (int i = 0; i < userGuess.length(); i++)    // On parcourt les caractères du mot proposé par l'utilisateur
        {
            if (userGuess.charAt(i) == targetWord.charAt(i))    // Si le caractère est correct et à la bonne place
            {
                feedbackColors.add(Color.GREEN);   // On ajoute la couleur verte à la liste de couleurs
            }
            else if (targetWord.contains(Character.toString(userGuess.charAt(i))))  // Si le caractère est correct mais pas à la bonne place
            {
                feedbackColors.add(Color.ORANGE);  // On ajoute la couleur orange à la liste de couleurs
            }
            else
            {
                feedbackColors.add(Color.RED); // On ajoute la couleur rouge à la liste de couleurs
            }
        }

        return feedbackColors;  // On retourne la liste de couleurs de feedback pour chaque caractère du mot proposé par l'utilisateur
    }

    /*
     * Méthode startGame
     * Cette méthode permet de démarrer le jeu Wordle.
     * @param wordLength: la longueur du mot cible
     * @return void
     * author: BOUDOOUNT Youssef
     */
    public void startGame(int wordLength)
    {
        WordleGame.targetWord = wordSelector.getRandomWord(wordLength);
    }

    /*
     * Méthode evaluateGuess
     * Cette méthode permet d'évaluer le mot deviné.
     * @param guess: le mot deviné
     * @return boolean: true si correct, false sinon
     * author: BOUDOOUNT Youssef
     */
    public boolean evaluateGuess(String guess) {
        // TODO : implementer la méthode evaluateGuess
        return false; // Retourner true si correct, false sinon
    }

	public void setTargetWord(String chosenWord) {
		WordleGame.targetWord = chosenWord;
		
	}

}
