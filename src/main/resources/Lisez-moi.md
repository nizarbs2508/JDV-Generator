# Introduction

Le générateur de JDV de l'ANS permet :

- de générer les fichiers de JDV « .xml » à partir du fichier « ANS\_Contenu\_JDV\_vx.x de travail.xslx » en cliquant sur le bouton «Macro JDV ».
- de générer les fichiers de JDV « .xml » à partir des fichiers NOS en cliquant sur le bouton « Télécharger les JDV du NOS » puis en cliquant sur «Macro JDV ».
- de générer le fichier « JDV\_Generes.xml » à partir des fichiers de JDV générer précédemment en cliquant sur le bouton « Macro JDV AD ».
- De trier le fichier « JDV\_Generes.xml » et de l’afficher sous forme d’arborescence en cliquant sur le bouton « Arborescence Fichier AD généré ».
- D’ouvrir le fichier « JDV\_Generes.xml » avec l’éditeur approprié en cliquant sur le bouton « Ouvrir le fichier AD généré ».
- Initialiser la liste des fichiers  avec le bouton « Réinitialiser ».
- La mise à jour des terminologies suite au téléchargement des fichiers « .rdf » du site « <https://smt.esante.gouv.fr/catalogue-des-terminologies/> » .
- Valider les fichiers JDV à travers le menu « Valider Fichiers JDV ».
# Utilisation du générateur JDV de l'ANS

Pour ouvrir le générateur CDA, il suffit de double cliquer sur **JDV\_Converter.jar**.

Vous devez :

- Sélectionner le document « ANS\_Contenu\_JDV\_vx.x de travail.xslx » à travers le menu « Fichier » et générer les fichiers xml de JDV.
- Sélectionner les documents déjà générer par le bouton « Macro JDV » à travers le même menu « Fichier » et générer le fichier de JDV combiné à déposer dans art decor.
- Télécharger les fichiers des JDV NOS en cliquant sur le bouton « Télécharger les JDV du NOS ».
- Mise à jour des Terminologies des fichiers RDF téléchargés sur l’adresse annoncé précédemment.  
- Trier le fichier combiné déjà généré.
- Tous les champs de la formulaire sont modifiables et renseignés par défaut :
- Effective Date renseigné par défaut à 2021-03-15 00:00:00.
- Status code qui peut être « final, partiel, final tronqué, completed, active, aborted ».
- Version Label  renseigné par défaut à « 1.0 ».
- Level renseigné par défaut à « 0 ».
- Type renseigné par défaut à « L ».
- Chemin du fichier de sortie (AD FILE) qui représente le chemin de sortie du fichier de JDV généré pour art decor, par défaut il est à C:\JDV\JDV\_Generes.xml.
- Chemin des fichiers de sortie (JDV FILES) par défaut à C:\JDV\output\ et qui représente le dossier qui va accueillir les fichiers de JDV généré par le bouton « Macro JDV ».  
- URL des fichiers NOS pour télécharger les fichiers de JDV NOS.

# Prérequis
**Java doit être installé sur votre poste utilisateur.**

**testContenuCDA doit être installé sur votre poste utilisateur pour avoir accès au dossier de JDV.**

**Problème possible de lancement lié à Java**

Si vous avez l'exception "JVM Launcher exception", vous devez mettre à jour votre version de Java.

Pour cela, installez une nouvelle version de Java en cliquant sur le lien : <https://download.oracle.com/java/20/latest/jdk-20_windows-x64_bin.exe>.

Après l’installation de Java, ouvrez l’invite de commande (CMD) et tapez "java -version" pour vous assurer que la nouvelle version de Java est installée.

Vous pouvez alors relancer le générateur de JDV.
