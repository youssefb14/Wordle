package model;
import java.io.FileReader; // Pour lire un fichier
import java.io.IOException; // Pour gérer les erreurs d'entrée/sortie
import com.google.gson.Gson; // Pour lire un fichier JSON
import com.google.gson.JsonArray; // Pour lire un tableau JSON
import com.google.gson.JsonObject; // Pour lire un objet JSON

/*
 * Classe FrenchWordChecker
 * Cette classe permet de vérifier si un mot est présent dans le dictionnaire français.
 * Tache : 2.1.3
 * author: BOUDOOUNT Youssef
 */
public class FrenchWordChecker
{
    private static String[] generalDictionary;  // On utilise un tableau de String qui va contenir le dictionnaire général

    /*
     * Constructeur de la classe FrenchWordChecker
     * Ce constructeur permet d'initialiser le dictionnaire français
     * @param generalDictionaryFile: le fichier contenant le dictionnaire
     * @return void
     * author: BOUDOOUNT Youssef
     * source pour lire le fichier JSON: https://www.tutorialspoint.com/gson/gson_quick_guide.htm
     * source pour les erreurs IOExeption: https://www.tutorialspoint.com/java/java_exceptions.htm
     */
    public FrenchWordChecker(String generalDictionaryFile)
    {
        try
        {// On essaie d'initialiser le dictionnaire
            Gson gson = new Gson();     // On utilise un objet de type Gson pour pouvoir lire le fichier JSON
            FileReader fileReader = new FileReader(generalDictionaryFile);  // On utilise un objet de type FileReader pour pouvoir lire le fichier JSON
            JsonObject jsonObject = gson.fromJson(fileReader, JsonObject.class);    // On lis le fichier JSON et on le stocke dans un objet de type JsonObject
            JsonArray jsonArray = jsonObject.getAsJsonArray("words");   // On récupérer le tableau de String qui contient le dictionnaire général
            generalDictionary = gson.fromJson(jsonArray, String[].class);    // On lis le fichier JSON et on l'ajoute au tableau de String qui va contenir le dictionnaire général
            fileReader.close(); // On ferme le fichier
        }
        catch (IOException e)   // Si on a une erreur
        {
            e.printStackTrace();    // On affiche l'erreur
        }
    }

    /*
     * Méthode isWordInFrenchDictionary
     * Cette méthode permet de vérifier si un mot est présent dans le dictionnaire français.
     * @param word: le mot à vérifier
     * @return boolean: true si le mot est présent dans le dictionnaire, false sinon
     * author: BOUDOOUNT Youssef
     */
    public static boolean isWordInFrenchDictionary(String word)
    {
        for (String dictWord : generalDictionary)   // On parcours le dictionnaire général
        {
            if (dictWord.equalsIgnoreCase(word))    // Si le mot est présent dans le dictionnaire
            {
                return true;    // On retourne true
            }
        }
        return false;   // On retourne false si le mot n'est pas présent dans le dictionnaire
    }
}
