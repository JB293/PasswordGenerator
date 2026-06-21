package generateur;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Classe chargée de communiquer avec le conteneur Docker externe
 * pour obtenir l'évaluation de la robustesse d'un mot de passe.
 */
public class ValidateurDocker {

    // URL principale pour la communication inter-conteneurs Docker
    private static final String DOCKER_CONTAINER_URL = "http://validateur-securite:8080/";

    // URL de secours pour les tests en local directement depuis l'IDE (IntelliJ)
    private static final String LOCALHOST_URL = "http://localhost:8080/";

    private final HttpClient httpClient;

    public ValidateurDocker() {
        // Initialisation du client HTTP natif de Java 21 avec un timeout de 10 secondes
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();
    }

    /**
     * Envoie le mot de passe au conteneur Docker et traduit le score numérique en texte lisible.
     *
     * @param motDePasse Le mot de passe à faire auditer.
     * @return Une chaîne décrivant le niveau de robustesse (Très faible à Très fort).
     */
    public String evaluerRobustesse(String motDePasse) {
        // Tente d'abord de joindre le conteneur dans le réseau Docker isolé
        String resultat = envoyerRequete(DOCKER_CONTAINER_URL, motDePasse);

        // Si le réseau Docker n'est pas encore actif (test local sur IntelliJ)
        if (resultat.startsWith("[Erreur")) {
            resultat = envoyerRequete(LOCALHOST_URL, motDePasse);
        }

        return resultat;
    }

    /**
     * Exécute la requête HTTP POST vers l'API zxcvbn externe.
     */
    private String envoyerRequete(String url, String motDePasse) {
        try {
            // Échappement basique des guillemets pour éviter de casser le format JSON
            String motDePasseSecurise = motDePasse.replace("\"", "\\\"");
            String jsonPayload = "{\"password\":\"" + motDePasseSecurise + "\"}";

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // L'API zxcvbn renvoie un JSON contenant '"score":X'
                int score = extraireScore(response.body());
                return traduireScore(score);
            } else {
                return "[Docker API Statut: " + response.statusCode() + "]";
            }

        } catch (Exception e) {
            return "[Erreur : Validateur externe injoignable]";
        }
    }

    /**
     * Extrait la valeur numérique du score depuis la réponse JSON de zxcvbn.
     */
    private int extraireScore(String json) {
        if (json.contains("\"score\":")) {
            int index = json.indexOf("\"score\":") + 8;
            char scoreChar = json.charAt(index);
            if (Character.isDigit(scoreChar)) {
                return Character.getNumericValue(scoreChar);
            }
        }
        return 0;
    }

    /**
     *  score d'entropie zxcvbn (0 à 4) selon les critères
     */
    private String traduireScore(int score) {
        return switch (score) {
            case 0 -> "Très faible ";
            case 1 -> "Faible ";
            case 2 -> "Moyen ";
            case 3 -> "Fort ";
            case 4 -> "Très fort ";
            default -> "Inconnu ";
        };
    }
}
