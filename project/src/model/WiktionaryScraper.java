package model;

import java.util.ArrayList; // Pour utiliser une liste
import java.util.List; // Pour utiliser une liste
import org.apache.hc.client5.http.classic.methods.HttpGet; // Pour envoyer une requête HTTP GET
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient; // Pour envoyer une requête HTTP au serveur web
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse; // Pour récupérer la réponse du serveur web
import org.apache.hc.client5.http.impl.classic.HttpClients; // Pour créer un client HTTP
import org.jsoup.Jsoup; // Pour analyser le contenu HTML d'une page web
import org.jsoup.nodes.Document; // Pour représenter un document HTML
import org.jsoup.nodes.Element; // Pour représenter un élément HTML
import org.jsoup.select.Elements; // Pour représenter une liste d'éléments HTML
import com.google.gson.JsonArray; // Pour stocker un tableau JSON
import com.google.gson.JsonObject; // Pour stocker un objet JSON

/*
 * Classe WiktionaryScraper
 * Cette classe permet de récupérer les mots du Wiktionnaire.
 * Tache : 2.1.1
 * author: BOUDOOUNT Youssef
 * source pour la bibliothèque JSoup :  https://jsoup.org/cookbook/extracting-data/selector-syntax
 * source pour la bibliothèque Apache HTTP Client : https://hc.apache.org/httpcomponents-client-5.1.x/quickstart.html
 */
public class WiktionaryScraper
{
    /*
     * Méthode main
     * Cette méthode permet de récupérer les mots du Wiktionnaire et de les stocker dans un fichier JSON.
     * @param args: les arguments de la ligne de commande
     * @return void
     * author: BOUDOOUNT Youssef
     */
    public static void main(String[] args)
    {
        try
        { // On essaie de récupérer les mots du Wiktionnaire
            CloseableHttpClient httpClient = HttpClients.createDefault();   // On crée un client HTTP pour envoyer des requêtes HTTP au serveur web
            String url = "https://fr.wiktionary.org/wiki/Wiktionnaire:Liste_de_1750_mots_fran%C3%A7ais_les_plus_courants"; // L'URL de la page web à analyser
            HttpGet httpGet = new HttpGet(url); // On crée une requête HTTP GET pour récupérer la page web
            @SuppressWarnings("deprecation")    // On ignore le warning de la méthode setURI
			CloseableHttpResponse response = httpClient.execute(httpGet); // On exécute la requête HTTP GET et on récupère la réponse du serveur web
            Document doc = Jsoup.parse(response.getEntity().getContent(), "UTF-8", url);    // On crée un objet Document à partir de la réponse du serveur web (le contenu de la page web)
            Elements words = doc.select("a[href^=\"/wiki/\"]"); // On récupère les éléments HTML <a> qui ont un attribut href commençant par "/wiki/"

            List<String> wordList = new ArrayList<>();  // On crée une liste de mots
            for (Element wordElement : words)   // Pour chaque élément HTML <a> récupéré
            {
                String word = wordElement.text();   // On récupère le texte de l'élément HTML <a>
                if (!isUndesiredWord(word))     // Si le mot n'est pas indésirable
                {
                    System.out.println(word);   // On affiche le mot dans la console (pour le débogage)
                    wordList.add(word);     // On ajoute le mot à la liste des mots
                }
            }

            JsonArray jsonWords = new JsonArray();  // On crée un tableau JSON pour stocker les mots
            for (String word : wordList)    // Pour chaque mot
            {
                jsonWords.add(word);    // On ajoute le mot au tableau JSON
            }

            JsonObject jsonObject = new JsonObject();   // On crée un objet JSON qui va contenir le tableau JSON des mots
            jsonObject.add("words", jsonWords);    // On ajoute le tableau JSON des mots à l'objet JSON

            /*// On essaie d'écrire l'objet JSON dans un fichier JSON (data/mots.json)
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/mots.json")))
            {
                writer.write(jsonObject.toString());    // On écrit l'objet JSON dans le fichier JSON
            } catch (IOException e)     // Si une erreur se produit
            {
                e.printStackTrace();    // On affiche l'erreur dans la console
            }

            response.close();   // On ferme la réponse du serveur web
            httpClient.close(); // On ferme le client HTTP*/
        }
        catch (Exception e)    // Si une erreur se produit c'est a dire si on n'arrive pas à récupérer les mots du Wiktionnaire
        {
            e.printStackTrace();   // On affiche l'erreur dans la console
        }
    }


    /*
     * Méthode isUndesiredWord
     * Cette méthode permet de vérifier si un mot est indésirable ou non.
     * @param word: le mot à vérifier
     * @return boolean: true si le mot est indésirable, false sinon
     * author: BOUDOOUNT Youssef
     */
    private static boolean isUndesiredWord(String word)
    {
        // Liste des mots indésirables
        String[] undesiredWords = {
                "Page d’accueil", "Recherche avancée, anagrammes et rimes", "Portails", "Page au hasard",
                "Page au hasard par langue", "Poser une question", "Journal des contributeurs", "La Wikidémie",
                "Communauté", "Discuter sur Discord", "Modifications récentes", "Forum d’entraide", "Aide",
                "Modèles", "Conventions", "Créer un article", "Télécharger", "Rechercher", "en savoir plus",
                "Contributions", "Discussion", "Page", "Discussion", "Lire", "Lire", "Pages liées",
                "Suivi des pages liées", "Pages spéciales", "",
                "Wiktionnaire:Liste des mots français que tous les Wiktionnaires devraient avoir",
                "À propos du Wiktionnaire", "Licence"
        };

        for (String undesiredWord : undesiredWords)  // Pour chaque mot indésirable
        {
            if (word.equals(undesiredWord))    // Si le mot est indésirable
            {
                return true;    // On retourne true
            }
        }
        return false;   // Si le mot n'est pas indésirable on retourne false
    }
}
