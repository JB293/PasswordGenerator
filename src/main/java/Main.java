import generateur.Generateur_mot_de_passe;
import generateur.ValidateurDocker;
import java.util.Scanner;

/*
 Application CLI pour la génération de mot de passe
 */
public class Main {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Generateur_mot_de_passe generateur = new Generateur_mot_de_passe();
        ValidateurDocker validateur = new ValidateurDocker();

        System.out.println("=========================================");
        System.out.println("GENERATEUR DE MOT DE PASSE SECURISE (CLI)");
        System.out.println("=========================================");

        int longueur = 0;
        boolean inclureMiniscule = false;
        boolean inclureMajuscule = false;
        boolean inclureChiffres = false;
        boolean inclureSymboles = false;


        while (true) {
            longueur = 0;
            while (longueur < 1) {
                System.out.print("Entrez la longueur du mot de passe (min 1): ");
                if (sc.hasNextInt()) {
                    longueur = sc.nextInt();
                    if (longueur < 1) {
                        System.out.println("La longueur du mot de passe doit être supérieure à 0.");
                    }
                } else {
                    System.out.println("Entrez un nombre valide.");
                    sc.next();
                }
            }

            inclureMiniscule = demanderOption(sc, "Inclure des minuscules (abcd)");
            inclureMajuscule = demanderOption(sc, "Inclure des majuscules (ABCD)");
            inclureChiffres = demanderOption(sc, "Inclure des chiffres (0123)");
            inclureSymboles = demanderOption(sc, "Inclure des symboles (%@#)");

            int nombreDeCriteres = 0;
            if (inclureMiniscule) nombreDeCriteres++;
            if (inclureMajuscule) nombreDeCriteres++;
            if (inclureChiffres) nombreDeCriteres++;
            if (inclureSymboles) nombreDeCriteres++;

            if (nombreDeCriteres == 0) {
                System.out.println("Vous devez sélectionner au moins un type de caractère. Recommençons.\n");
                continue;
            }

            if (longueur < nombreDeCriteres) {
                System.out.printf("Erreur : Une longueur de %d est trop courte pour inclure les %d types choisis !%n", longueur, nombreDeCriteres);
                System.out.println("Veuillez réinitialiser vos choix.\n");
                continue;
            }

            break;
        }

        // Mode rafale
        int quantite = 0;
        while (quantite < 1) {
            System.out.print("Combien de mots de passe souhaitez-vous générer ? ");
            if (sc.hasNextInt()) {
                quantite = sc.nextInt();
                if (quantite < 1) {
                    System.out.println("Vous devez générer au moins 1 mot de passe.");
                }
            } else {
                System.out.println("Veuillez entrer un nombre valide.");
                sc.next();
            }
        }

        System.out.println("\n=========================================");
        System.out.println("RESULTATS DE GENERATION DU MOT DE PASSE");
        System.out.println("=========================================");

        try {
            for (int i = 1; i <= quantite; i++) {

                String password = generateur.generate(
                        longueur,
                        inclureMiniscule,
                        inclureMajuscule,
                        inclureChiffres,
                        inclureSymboles
                );


                String force = validateur.evaluerRobustesse(password);


                System.out.printf("[%d] %s  -->  [Indicateur: %s]%n", i, password, force);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Une erreur est survenue lors de la génération : " + e.getMessage());
        }

        sc.close();
    }

    private static boolean demanderOption(Scanner sc, String message) {
        while (true) {
            System.out.print(message + " (o/n) : ");
            String reponse = sc.nextLine().trim().toLowerCase();
            if (reponse.equals("o") || reponse.equals("oui")) {
                return true;
            } else if (reponse.equals("n") || reponse.equals("non")) {
                return false;
            }
            System.out.println("Réponse invalide. Tapez 'o' ou 'n'.");
        }
    }
}
