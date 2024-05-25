import json

def filtrer_mots(fichier_source, fichier_destination):
    try:
        # Charger le contenu du fichier source
        with open(fichier_source, 'r', encoding='utf-8') as file:
            data = json.load(file)

        # Filtrer les mots sans espace et sans apostrophe
        mots_filtrés = [mot for mot in data["words"] if " " not in mot and "’" not in mot and "'" not in mot]

        # Créer un nouveau dictionnaire JSON avec les mots filtrés
        nouveau_json = {"words": mots_filtrés}

        # Enregistrer dans le fichier de destination
        with open(fichier_destination, 'w', encoding='utf-8') as file:
            json.dump(nouveau_json, file, ensure_ascii=False, indent=4)

        return "Filtrage terminé. Résultats sauvegardés dans " + fichier_destination
    except Exception as e:
        return "Erreur lors du traitement : " + str(e)


# Appel de la fonction avec les chemins des fichiers
resultat = filtrer_mots('mots.json', 'mots2.json')
print(resultat)
