import json
import unicodedata
import sys

# Vérifier si le nom du fichier a été donné en argument
if len(sys.argv) < 2:
    print("Usage: python convert_words.py filename.json")
    sys.exit(1)

# Le premier argument après le nom du script est le nom du fichier JSON
filename = sys.argv[1]

# Fonction pour enlever les accents des caractères
def remove_accents(input_str):
    nfkd_form = unicodedata.normalize('NFKD', input_str)
    return u"".join([c for c in nfkd_form if not unicodedata.combining(c)])

# Lire le fichier JSON
try:
    with open(filename, 'r', encoding='utf-8') as file:
        data = json.load(file)
except FileNotFoundError:
    print(f"Le fichier {filename} n'a pas été trouvé.")
    sys.exit(1)

# Modifier les mots pour être en majuscules et sans accents
updated_words = [remove_accents(word.upper()) for word in data['words']]

# Mettre à jour le dictionnaire avec les mots modifiés
data['words'] = updated_words

# Enregistrer le dictionnaire modifié dans le fichier JSON
with open(filename, 'w', encoding='utf-8') as file:
    json.dump(data, file, ensure_ascii=False, indent=2)

print(f"Les mots dans {filename} ont été transformés en majuscules et les accents ont été enlevés.")

// Pour executer : python3 convert_words.py (nom du fichier json)