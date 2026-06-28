package nl.verius.inwonerportaal.i18n;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Placeholder-vertaalmotor. Doet geen echte vertaling maar markeert de waarde met de doeltaal-code
 * ({@code "[en] <bron>"}), zodat de volledige flow + beheerinterface werkt en auto-gegenereerde
 * regels zichtbaar "nog te reviewen" zijn.
 *
 * <p><b>Vervang later door een {@code ClaudeTranslator}</b> die de Anthropic Messages API aanroept
 * voor echte vertalingen. Maak die dan {@code @Primary} of zet deze mock achter een profiel.
 */
@Component
public class MockTranslator implements Translator {

    private static final Logger log = LoggerFactory.getLogger(MockTranslator.class);

    @Override
    public Map<String, String> vertaal(String doeltaal, Map<String, String> nlTeksten) {
        log.info("MockTranslator: {} sleutels 'vertaald' naar {} (placeholder — nog geen echte AI).",
                nlTeksten.size(), doeltaal);
        Map<String, String> resultaat = new LinkedHashMap<>();
        nlTeksten.forEach((key, nl) -> resultaat.put(key, "[" + doeltaal + "] " + nl));
        return resultaat;
    }
}
