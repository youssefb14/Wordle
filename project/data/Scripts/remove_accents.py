# -*- coding: utf-8 -*-
import json
from unidecode import unidecode

# Fonction pour enlever les accents et caractères spéciaux
def remove_accents(input_str):
    return unidecode(input_str)

# Charger le fichier JSON
with open('diccoGeneral.json', 'r', encoding='utf-8') as file:
    data = json.load(file)

# Enlever les accents et caractères spéciaux de chaque mot
for i, word in enumerate(data['words']):
    data['words'][i] = remove_accents(word)

# Enregistrer le résultat dans un nouveau fichier JSON
with open('diccoGeneral_sans_accents.json', 'w', encoding='utf-8') as output_file:
    json.dump(data, output_file, ensure_ascii=False, indent=4)

print("Les accents et caractères spéciaux ont été supprimés avec succès.")

