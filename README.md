# Projet Wordle Multijoueur

## Introduction
Le projet Wordle Multijoueur est une adaptation du célèbre jeu de mots Wordle, étendu pour supporter la fonctionnalité multijoueur. Ce projet permet à plusieurs joueurs de s'affronter en temps réel, enrichissant l'expérience classique de Wordle avec des éléments compétitifs et collaboratifs.

## Vue d'ensemble du jeu

### Qu'est-ce que Wordle ?
Wordle est un jeu de réflexion basé sur les mots où l'objectif est de deviner un mot cible à l'aide d'indices colorés pour chaque essai. Dans cette version multijoueur, les joueurs se concurrencent pour deviner le mot en premier, avec des règles supplémentaires pour synchroniser les tentatives et gérer les interactions en temps réel.

### Règles du jeu
- Le joueur doit deviner un mot de x lettres en un maximum de 5 essais.
- Chaque lettre du mot proposé reçoit un retour coloré :
  - Vert : Lettre correcte et bien placée.
  - Orange : Lettre correcte mais mal placée.
  - Rouge : Lettre incorrecte.
- En mode multijoueur, des règles supplémentaires assurent la synchronisation des tentatives et les interactions en temps réel entre les joueurs.

### Déroulement d'une partie
1. Un mot cible est choisi aléatoirement par le jeu.
2. Les joueurs saisissent leurs propositions.
3. Le jeu fournit des indices colorés pour chaque lettre du mot proposé.
4. Les joueurs continuent à proposer des mots jusqu'à ce qu'ils trouvent le mot cible ou épuisent leurs essais.
5. Le jeu se termine lorsque le mot est deviné ou lorsque les essais sont épuisés, révélant le mot si nécessaire.

## Description du projet
Ce projet documente le développement d'une adaptation en langue française de Wordle, spécifiquement conçue pour les interactions multijoueurs en ligne. Il intègre la communication en temps réel utilisant les websockets et utilise des frameworks d'IA pour créer un gameplay dynamique et réactif.

### Contexte et objectifs
Le projet vise à transformer Wordle en une expérience multijoueur en ligne enrichissante, testant le vocabulaire des utilisateurs et encourageant une compétition saine. Les fonctionnalités clés incluent :
- Jeux multijoueurs en ligne.
- Synchronisation en temps réel des actions des joueurs.
- Intégration d'un joueur IA pour défier les utilisateurs avec différents niveaux de difficulté.

### Méthodologie
Le projet suit une méthodologie de développement agile, permettant une adaptation continue en fonction des retours et des exigences évolutives. Chaque membre de l'équipe est responsable d'une composante spécifique du logiciel.

## Architecture
Le projet utilise une architecture Modèle-Vue-Contrôleur (MVC) :
- **Modèle** : Gère les données et la logique du jeu (par exemple, la validation et la sélection des mots).
- **Vue** : Gère l'interface utilisateur pour les modes local et en ligne.
- **Contrôleur** : Facilite l'interaction entre le modèle et les vues, gérant la logique du jeu et les communications réseau.

## Installation et utilisation

### Cloner le projet
1. Ouvrez un terminal sur votre ordinateur.
2. Naviguez vers le répertoire où vous souhaitez cloner le projet.
3. Exécutez la commande :
   ```bash
   git clone https://gitlab.com/ceri-projet-programmation-2023/semestre2-groupe3.git
