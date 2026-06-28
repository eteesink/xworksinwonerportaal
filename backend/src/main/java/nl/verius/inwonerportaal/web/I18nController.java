package nl.verius.inwonerportaal.web;

import nl.verius.inwonerportaal.i18n.TranslationStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Publieke i18n-API: levert de vertaalbundels aan de frontend (i18next-http-backend) en de lijst
 * met beschikbare talen voor de taal-switcher. Geen autorisatie — elke (ingelogde) gebruiker mag
 * de teksten ophalen.
 */
@RestController
@RequestMapping("/api/i18n")
public class I18nController {

    private final TranslationStore store;

    public I18nController(TranslationStore store) {
        this.store = store;
    }

    /** Beschikbare talen (voor de switcher). */
    @GetMapping
    public Map<String, Object> talen() {
        return Map.of("talen", store.talen(), "bron", TranslationStore.BRON);
    }

    /** Platte sleutel→tekst-bundel voor één taal (door i18next geladen). */
    @GetMapping("/{taal}")
    public Map<String, String> bundel(@PathVariable String taal) {
        return store.bundel(taal);
    }
}
