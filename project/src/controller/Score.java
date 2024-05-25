package controller;

/*
 * Classe Score
 * Cette classe permet de gérer le score du jeu Wordle.
 * author: BOUDOOUNT Youssef
 */
public class Score
{
    private int score; // Attribut score qui va contenir le score du joueur

    /*
     * Constructeur Score()
     * Ce constructeur permet d'initialiser le score à 0.
     * @param void
     * @return void
     * author: BOUDOOUNT Youssef
     */
    public Score() {
        this.score = 0;
    }

    /*
     * Methodes correctLetter()
     * Cette méthode permet d'augmenter le score de 10 points lorsqu'une lettre est correctement devinée.
     * @param void
     * @return void 
     * author: BOUDOOUNT Youssef
     */
    public void correctLetter() {
        score += 10;
    }

    /*
     * Methodes correctWord()
     * Cette méthode permet d'augmenter le score de 100 points lorsqu'un mot est correctement deviné.
     * @param void
     * @return void
     * author: BOUDOOUNT Youssef
     */
    public void correctWord() {
        score += 100;
    }

    /*
     * Methodes getScore()
     * Cette méthode permet de retourner le score du joueur.
     * @param void
     * @return int
     * author: BOUDOOUNT Youssef
     */
    public int getScore() {
        return score;
    }

    /*
     * Methodes reset()
     * Cette méthode permet de réinitialiser le score à 0.
     * @param void
     * @return void
     * author: BOUDOOUNT Youssef
     */
    public void reset() {
        score = 0;
    }
}
