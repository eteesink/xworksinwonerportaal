package nl.verius.inwonerportaal.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import nl.verius.inwonerportaal.i18n.I18nEntry;
import nl.verius.inwonerportaal.i18n.TranslationStore;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Beheerinterface-API voor vertalingen (alleen beheerders, zie {@link Beheerder}).
 *
 * <p>Werkstroom: de Nederlandse bron is leidend; de beheerder laat ontbrekende doeltaal-vertalingen
 * automatisch genereren (vertaalmotor) en overschrijft waar nodig handmatig. Een handmatige edit
 * krijgt status {@code reviewed} en wordt niet door een latere AI-run overschreven.
 */
@RestController
@RequestMapping("/api/beheer/i18n")
public class BeheerI18nController {

    private final TranslationStore store;
    private final Beheerder beheerder;

    public BeheerI18nController(TranslationStore store, Beheerder beheerder) {
        this.store = store;
        this.beheerder = beheerder;
    }

    /** Beschikbare talen + bron-taal. */
    @GetMapping("/talen")
    public Map<String, Object> talen(HttpServletRequest request) {
        beheerder.vereisBeheerder(request);
        return Map.of("talen", store.talen(), "bron", TranslationStore.BRON);
    }

    /** Alle sleutels met bron (nl), waarde en status voor één doeltaal. */
    @GetMapping("/{taal}")
    public List<I18nEntry> entries(HttpServletRequest request, @PathVariable String taal) {
        beheerder.vereisBeheerder(request);
        return store.entries(taal);
    }

    /** Eén vertaling vastleggen (→ status reviewed). */
    @PutMapping("/{taal}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void zet(HttpServletRequest request, @PathVariable String taal, @RequestBody EntryRequest body) {
        beheerder.vereisBeheerder(request);
        if (body == null || body.key() == null || body.key().isBlank()) {
            throw new IllegalArgumentException("key is verplicht");
        }
        store.zet(taal, body.key(), body.waarde() == null ? "" : body.waarde());
    }

    /** Ontbrekende sleutels in de doeltaal automatisch (vertaalmotor) vullen. */
    @PostMapping("/{taal}/vertaal-ontbrekende")
    public Map<String, Object> vertaalOntbrekende(HttpServletRequest request, @PathVariable String taal) {
        beheerder.vereisBeheerder(request);
        int aantal = store.vertaalOntbrekende(taal);
        return Map.of("vertaald", aantal);
    }

    /** Nieuwe doeltaal toevoegen. */
    @PostMapping("/talen")
    @ResponseStatus(HttpStatus.CREATED)
    public void voegTaalToe(HttpServletRequest request, @RequestBody TaalRequest body) {
        beheerder.vereisBeheerder(request);
        store.voegTaalToe(body.taal());
    }

    /** Doeltaal verwijderen (bron nl kan niet). */
    @DeleteMapping("/{taal}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verwijderTaal(HttpServletRequest request, @PathVariable String taal) {
        beheerder.vereisBeheerder(request);
        store.verwijderTaal(taal);
    }

    public record EntryRequest(@NotBlank String key, String waarde) {
    }

    public record TaalRequest(@NotBlank String taal) {
    }
}
