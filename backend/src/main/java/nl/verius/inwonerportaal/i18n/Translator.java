package nl.verius.inwonerportaal.i18n;

import java.util.Map;

/**
 * Vertaalmotor-poort: vertaalt Nederlandse bronteksten naar een doeltaal.
 *
 * <p>Implementaties: {@link MockTranslator} (placeholder, nu actief) en later een
 * {@code ClaudeTranslator} die de Anthropic Messages API aanroept (model {@code claude-haiku-4-5}
 * / {@code claude-sonnet-4-6}). De motor vult alleen <b>ontbrekende</b> sleutels; door een
 * beheerder gereviewde vertalingen worden nooit overschreven (zie {@link TranslationStore}).
 */
public interface Translator {

    /**
     * Vertaal de gegeven sleutel→Nederlandse-tekst naar de doeltaal.
     *
     * @param doeltaal ISO 639-1 code (bv. {@code en}, {@code de})
     * @param nlTeksten sleutel → Nederlandse brontekst
     * @return sleutel → vertaalde tekst (zelfde sleutels)
     */
    Map<String, String> vertaal(String doeltaal, Map<String, String> nlTeksten);
}
