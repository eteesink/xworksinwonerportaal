package nl.verius.inwonerportaal.acl.model;

import java.util.List;
import java.util.Map;

/**
 * Resultaat van de regelevaluatie van een vragenlijst.
 *
 * <p>Equivalent van de X-Works operatie {@code vragenlijst-eval}: bij elke wijziging worden
 * afgeleide waarden herberekend, de zichtbaarheid van vragen bepaald en validaties uitgevoerd.
 *
 * @param afgeleideWaarden door regels berekende waarden (bijv. een totaalbedrag)
 * @param zichtbaarheid    per vraag-id of die zichtbaar is
 * @param validaties       gevonden validatiefouten
 * @param indienbaar       of de aanvraag in deze staat ingediend mag worden
 */
public record EvaluatieResultaat(
        Map<String, Object> afgeleideWaarden,
        Map<String, Boolean> zichtbaarheid,
        List<Validatiefout> validaties,
        boolean indienbaar
) {}
