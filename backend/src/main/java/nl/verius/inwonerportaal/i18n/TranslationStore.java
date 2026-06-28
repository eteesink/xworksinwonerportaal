package nl.verius.inwonerportaal.i18n;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Opslag van de portaalvertalingen (applicatie-/beheerdata, geen per-burger X-Works-data — loopt
 * dus niet via de {@code XworksClient}-ACL). De Nederlandse laag ({@code nl}) is de bron; doeltalen
 * worden door de {@link Translator} gevuld en door beheerders verfijnd.
 */
public interface TranslationStore {

    /** Bronnentaal (de canonieke sleutels). */
    String BRON = "nl";

    /** Alle aanwezige talen, inclusief {@link #BRON}. */
    Set<String> talen();

    /** Platte sleutel→tekst-bundel voor de frontend ({@code GET /api/i18n/{taal}}). */
    Map<String, String> bundel(String taal);

    /** Beheer-overzicht: per bronsleutel de waarde + status in de doeltaal. */
    List<I18nEntry> entries(String taal);

    /** Zet een waarde vast (beheerder) → status {@code reviewed}. */
    void zet(String taal, String key, String waarde);

    /** Vult ontbrekende sleutels in de doeltaal via de {@link Translator} (status {@code auto}). */
    int vertaalOntbrekende(String taal);

    /** Voegt een (lege) doeltaal toe; daarna kan {@link #vertaalOntbrekende} hem vullen. */
    void voegTaalToe(String taal);

    /** Verwijdert een doeltaal (de bron {@code nl} kan niet worden verwijderd). */
    void verwijderTaal(String taal);
}
