# Étape 1 : Utilisation d'une image JDK 21 stable d'Eclipse Temurin pour compiler le code
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copie de l'intégralité du code source Java dans le conteneur
COPY src/ ./src/

# Compilation de toutes les classes Java du projet
RUN javac -d out src/main/java/Main.java src/main/java/generateur/Generateur_mot_de_passe.java src/main/java/generateur/ValidateurDocker.java

# Étape 2 : Image finale contenant uniquement l'environnement d'exécution (JRE)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Récupération des fichiers compilés (.class) depuis l'étape de build
COPY --from=builder /app/out ./out

# Commande par défaut pour exécuter l'application CLI
ENTRYPOINT ["java", "-cp", "out", "Main"]
