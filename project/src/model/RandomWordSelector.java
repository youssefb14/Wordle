package model;
import java.io.IOException; // Pour gérer les exceptions d'entrée/sortie
import java.nio.file.Files; // Pour lire un fichier
import java.nio.file.Paths; // Pour lire un fichier
import java.util.ArrayList; // Pour utiliser une liste
import java.util.List; // Pour utiliser une liste
import java.util.Random; // Pour sélectionner aléatoirement un mot

import com.google.gson.Gson;

/*
 * Classe WordList
 * Cette classe permet de stocker les mots a deviner extraits du site donné
 * Tache : ?
 * author: BOUDOOUNT Youssef
 */
class WordList
{
    String[] words; // Liste des mots
}

/*
 * Classe RandomWordSelector
 * Cette classe permet de sélectionner aléatoirement un mot dans un fichier JSON.
 * Tache : 2.1.2
 * author: BOUDOOUNT Youssef
 */
public class RandomWordSelector
{
    private String[] mots;  // Liste des mots

    /*
     * Getter getMots
     * Cette méthode permet de retourner la liste des mots
     * @param void
     * @return String[]: la liste des mots
     * author: BOUDOOUNT Youssef
     */
    public String[] getMots()
    {
        return mots;
    }

    /*
     * Constructeur de la classe RandomWordSelector
     * Ce constructeur permet d'initialiser la liste des mots a partir de la quelle on va sélectionner aléatoirement un mot a deviner
     * @param jsonFile: le fichier JSON contenant les mots a deviner extraits du site donné
     * @return void
     * author: BOUDOOUNT Youssef
     */
    public RandomWordSelector(String jsonFile)
    {
        try
        { // On essaie de lire le fichier JSON
            String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFile))); // On lit le fichier JSON et on le stocke dans une variable de type String

            Gson gson = new Gson(); // On utilise la bibliothèque GSON pour lire le fichier JSON
            WordList wordList = gson.fromJson(jsonContent, WordList.class);   // On stocke les mots dans une liste de type WordList
            mots = wordList.words;  // On stocke les mots dans une liste de type String
        }
        catch (IOException e)   // Si le fichier JSON n'est pas trouvé
        {
            e.printStackTrace();    // On affiche l'erreur
        }
    }

    /*
     * Méthode getRandomWord
     * Cette méthode permet de sélectionner aléatoirement un mot dans la liste des mots
     * @param nombreLettres: le nombre de lettres du mot a deviner
     * @return String: le mot a deviner
     * author: BOUDOOUNT Youssef
     * source pour la bibliothèque Random: https://www.tutorialspoint.com/java/util/java_util_random.htm
     */
    public String getRandomWord(int nombreLettres)
    {
        List<String> motsAvecNombreLettres = new ArrayList<>(); // On utilise une liste pour stocker les mots qui ont le nombre de lettres donné

        for (String mot : mots) // On parcours la liste des mots
        {
            if (mot.length() == nombreLettres)  // Si le mot a le nombre de lettres donné
            {
                motsAvecNombreLettres.add(mot); // On l'ajoute a la liste
            }
        }

        Random random = new Random();   // On utilise un objet de type Random pour sélectionner aléatoirement un mot
        int index = random.nextInt(motsAvecNombreLettres.size());   // On sélectionne aléatoirement un mot

        return motsAvecNombreLettres.get(index);    // On retourne le mot sélectionné
    }
}