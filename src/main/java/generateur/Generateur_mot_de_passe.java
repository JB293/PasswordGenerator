package generateur;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Classe responsable de la génération de mot de passe sécurisés
 */
public class Generateur_mot_de_passe {

    // Groupe de caractères définis de manière immuable

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String NUMBERS = "0123456789";
    private static final String SYMBOLS = "!@#$%^&*()-_=+[{]};:\",<.>/?";

    // Générateur de nombres aléatoires sécurisé pour la cryptographie
    private final SecureRandom random = new SecureRandom();

    public String generate(int length, boolean lowercase, boolean uppercase, boolean numbers, boolean symbols) {
        if (length < 1){
            throw new IllegalArgumentException("La longueur du mot de passe doit être d'au moins 1 caractère");
        }
        StringBuilder poolDeCaracteres = new StringBuilder();

        List<Character> caracteresObligatoires = new ArrayList<>();
        if (lowercase){
            poolDeCaracteres.append(LOWERCASE);
            caracteresObligatoires.add(RandomChar(LOWERCASE));
        }
        if (uppercase){
            poolDeCaracteres.append(UPPERCASE);
            caracteresObligatoires.add(RandomChar(UPPERCASE));
        }
        if (numbers){
            poolDeCaracteres.append(NUMBERS);
            caracteresObligatoires.add(RandomChar(NUMBERS));
        }
        if (symbols){
            poolDeCaracteres.append(SYMBOLS);
            caracteresObligatoires.add(RandomChar(SYMBOLS));
        }
        // Validation si au moins un groupe a été selectionné
        if (poolDeCaracteres.isEmpty()){
            throw new IllegalArgumentException("Vous devez selectionner au moins un type de caractère");
        }
        // Validation que la longueur est suffisante pour contenir les cartères obligatoires
        if (length < caracteresObligatoires.size()){
            throw new IllegalArgumentException("La longueur est trop courte pour inclure tous les types de caractères sélectionnés.");
        }
        // Remplissage du reste du mot de passe de façon aléatoire
        StringBuilder passwordBuilder = new StringBuilder();
        int restantLength = length - caracteresObligatoires.size();

        for (int i = 0; i < restantLength; i++){
            passwordBuilder.append(RandomChar(poolDeCaracteres.toString()));
        }

        // Ajout des cartères obligatoires

        for (char c : caracteresObligatoires){
            passwordBuilder.append(c);
        }
        // Melange final pour eviter que les caractères obligatoires ne soient toujours a la fin
        return melangeString(passwordBuilder.toString());

    }

    /**
     * Sélectionne un caractère aléatoire dans une chaîne donnée.
     */

    private char RandomChar(String base){
        int index = random.nextInt(base.length());
        return base.charAt(index);
    }

    /**
     * Mélange les caractères d'une chaîne de manière aléatoire.
     */
    private String melangeString(String input) {
        List<Character> characters = new ArrayList<>();
        for (char c : input.toCharArray()){
            characters.add(c);
        }
        Collections.shuffle(characters, random);

        StringBuilder result = new StringBuilder();
        for (char c : characters){
            result.append(c);
        }
        return result.toString();
    }
}
