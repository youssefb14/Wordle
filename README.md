ORGANISTION

Voici un guide simple étape par étape pour que tous les membres du groupe puisse travailler correctement.

Clonage du projet :

1. Ouvrez un terminal (invite de commande) sur votre ordinateur.
2. Naviguez vers le répertoire où vous souhaitez cloner le projet.
3. Utilisez la commande git clone suivie de l'URL du dépôt GitLab :

git clone https://gitlab.com/ceri-projet-programmation-2023/semestre2-groupe3.git

Chaque membre du groupe possède sa propre branche dédiée au sein du projet, en plus de la branche principale "main". Cela signifie qu'il y a cinq branches distinctes, une pour chaque membre du groupe, où nous pouvons travailler de manière indépendante sur nos tâches et nos contributions.

Chaque membre peut ainsi ajouter, committer et pousser ses modifications dans sa propre branche sans interférer avec le travail des autres membres.

Liste des branches :

- youssef-boudount
- anthony-genti
- louis-rives
- samy-seghir

Aller dans sa propre branche :

1. Une fois le projet cloné, naviguez dans le répertoire du projet en utilisant la commande cd.

   "git checkout `<nom-de-votre-branche-personelle>`"

Ajout de modifications :

1. Travaillez sur vos fichiers DANS VOTRE BRANCHE dans le répertoire du projet.
2. Pour ajouter toutes vos modifications pour le prochain commit, utilisez la commande

   "git add ."

Commit :

1. Ensuite, committez vos modifications avec un message descriptif en utilisant la commande git commit :

   "git commit -m "Votre message descriptif" "

Pousser vos modifications sur GitLab :

1. Enfin, poussez vos modifications vers VOTRE BRNACHE sur GitLab avec la commande :

   "git push origin `<nom-de-votre-branche>`"

ATTENTION : Assurez-vous d'utiliser le nom correct de votre branche.

Remarque : Pour éviter de saisir votre ID GitLab et votre mot de passe à chaque PUSH Git, vous pouvez utiliser un gestionnaire de trousseaux :

    "git config --global credential.helper manager"
