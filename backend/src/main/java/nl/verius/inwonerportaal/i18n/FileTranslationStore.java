package nl.verius.inwonerportaal.i18n;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * File-backed {@link TranslationStore}: houdt alle vertalingen in het geheugen en persisteert ze
 * als één JSON-bestand op schijf, zodat beheer-edits herstarts overleven. Bij een leeg/ontbrekend
 * bestand wordt geseed uit de classpath-resources {@code i18n-seed/<taal>.json}.
 *
 * <p>Persistentievorm: <code>{ "nl": { "nav.home": {"text":"Home","status":"source"} }, "en": {…} }</code>.
 * Later vervangbaar door een echte database zonder de poort te wijzigen.
 */
@Component
public class FileTranslationStore implements TranslationStore {

    private static final Logger log = LoggerFactory.getLogger(FileTranslationStore.class);
    private static final String[] SEED_TALEN = {"nl", "en"};

    private final ObjectMapper mapper = new ObjectMapper();
    private final Translator translator;
    private final Path storeFile;

    /** taal → (sleutel → vertaling). LinkedHashMap voor stabiele volgorde in de UI/het bestand. */
    private final Map<String, Map<String, Vertaling>> data = new LinkedHashMap<>();

    public FileTranslationStore(Translator translator,
                                @Value("${inwonerportaal.i18n.store-file:data/i18n-store.json}") String storeFilePad) {
        this.translator = translator;
        this.storeFile = Path.of(storeFilePad);
    }

    @PostConstruct
    synchronized void init() {
        if (Files.exists(storeFile)) {
            laad();
        } else {
            seed();
            bewaar();
        }
        log.info("TranslationStore geladen: talen={}, bestand={}", talen(), storeFile.toAbsolutePath());
    }

    @Override
    public synchronized Set<String> talen() {
        return new TreeSet<>(data.keySet());
    }

    @Override
    public synchronized Map<String, String> bundel(String taal) {
        Map<String, Vertaling> taalData = data.getOrDefault(taal, Map.of());
        Map<String, String> bundel = new LinkedHashMap<>();
        taalData.forEach((key, v) -> bundel.put(key, v.text()));
        return bundel;
    }

    @Override
    public synchronized List<I18nEntry> entries(String taal) {
        Map<String, Vertaling> bron = data.getOrDefault(BRON, Map.of());
        Map<String, Vertaling> taalData = data.getOrDefault(taal, Map.of());
        List<I18nEntry> entries = new java.util.ArrayList<>();
        // Bron-sleutels zijn canoniek; loop daaroverheen zodat ontbrekende doeltaal-sleutels zichtbaar zijn.
        bron.forEach((key, nl) -> {
            Vertaling v = taalData.get(key);
            String waarde = v == null ? "" : v.text();
            String status = v == null ? "missing" : v.status();
            entries.add(new I18nEntry(key, nl.text(), waarde, status));
        });
        return entries;
    }

    @Override
    public synchronized void zet(String taal, String key, String waarde) {
        if (BRON.equals(taal)) {
            throw new IllegalArgumentException("De bron-taal (" + BRON + ") wordt door ontwikkelaars beheerd, niet hier.");
        }
        data.computeIfAbsent(taal, t -> new LinkedHashMap<>()).put(key, new Vertaling(waarde, Vertaling.REVIEWED));
        bewaar();
    }

    @Override
    public synchronized int vertaalOntbrekende(String taal) {
        if (BRON.equals(taal)) {
            return 0;
        }
        Map<String, Vertaling> bron = data.getOrDefault(BRON, Map.of());
        Map<String, Vertaling> taalData = data.computeIfAbsent(taal, t -> new LinkedHashMap<>());

        // Verzamel sleutels die ontbreken of leeg zijn; reviewed/auto-bestaande blijven ongemoeid.
        Map<String, String> teVertalen = new LinkedHashMap<>();
        bron.forEach((key, nl) -> {
            Vertaling bestaand = taalData.get(key);
            if (bestaand == null || bestaand.text() == null || bestaand.text().isBlank()) {
                teVertalen.put(key, nl.text());
            }
        });
        if (teVertalen.isEmpty()) {
            return 0;
        }
        Map<String, String> vertaald = translator.vertaal(taal, teVertalen);
        vertaald.forEach((key, tekst) -> taalData.put(key, new Vertaling(tekst, Vertaling.AUTO)));
        bewaar();
        return vertaald.size();
    }

    @Override
    public synchronized void voegTaalToe(String taal) {
        String t = normaliseer(taal);
        data.computeIfAbsent(t, x -> new LinkedHashMap<>());
        bewaar();
    }

    @Override
    public synchronized void verwijderTaal(String taal) {
        if (BRON.equals(taal)) {
            throw new IllegalArgumentException("De bron-taal (" + BRON + ") kan niet worden verwijderd.");
        }
        data.remove(taal);
        bewaar();
    }

    // --- intern ------------------------------------------------------------

    private String normaliseer(String taal) {
        if (taal == null || taal.isBlank() || !taal.matches("[a-zA-Z]{2}")) {
            throw new IllegalArgumentException("Ongeldige taalcode (verwacht ISO 639-1, bv. 'de'): " + taal);
        }
        return taal.trim().toLowerCase();
    }

    private void seed() {
        for (String taal : SEED_TALEN) {
            Map<String, String> teksten = leesSeed(taal);
            String status = BRON.equals(taal) ? Vertaling.SOURCE : Vertaling.REVIEWED;
            Map<String, Vertaling> taalData = new LinkedHashMap<>();
            teksten.forEach((key, tekst) -> taalData.put(key, new Vertaling(tekst, status)));
            data.put(taal, taalData);
        }
        log.info("TranslationStore geseed uit i18n-seed/ ({} talen).", data.size());
    }

    private Map<String, String> leesSeed(String taal) {
        ClassPathResource res = new ClassPathResource("i18n-seed/" + taal + ".json");
        try (InputStream in = res.getInputStream()) {
            return mapper.readValue(in, new TypeReference<LinkedHashMap<String, String>>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Kon seed niet lezen: " + res.getPath(), e);
        }
    }

    private void laad() {
        try (InputStream in = Files.newInputStream(storeFile)) {
            Map<String, Map<String, Vertaling>> geladen =
                    mapper.readValue(in, new TypeReference<Map<String, Map<String, Vertaling>>>() {});
            data.clear();
            data.putAll(geladen);
        } catch (IOException e) {
            throw new IllegalStateException("Kon TranslationStore niet laden: " + storeFile, e);
        }
    }

    private void bewaar() {
        try {
            if (storeFile.getParent() != null) {
                Files.createDirectories(storeFile.getParent());
            }
            mapper.writerWithDefaultPrettyPrinter().writeValue(storeFile.toFile(), data);
        } catch (IOException e) {
            throw new IllegalStateException("Kon TranslationStore niet opslaan: " + storeFile, e);
        }
    }
}
