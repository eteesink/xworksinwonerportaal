package nl.verius.inwonerportaal.acl.model;

import java.util.List;

/**
 * Definitie van één vraag in een vragenlijst, afgeleid van de X-Works {@code vragenlijstTemplate}.
 *
 * @param id           technische sleutel van de vraag
 * @param label        weergavetekst
 * @param type         {@code tekst} | {@code getal} | {@code bedrag} | {@code keuze} | {@code groep}
 * @param verplicht    of de vraag verplicht is (X-Works {@code valideer = required})
 * @param opties       keuzeopties (alleen bij type {@code keuze})
 * @param zichtbaarAls eenvoudige conditie {@code "veldId==waarde"} (X-Works conditionele zichtbaarheid);
 *                     leeg = altijd zichtbaar
 * @param subvragen    sub-velden bij een herhaalbare {@code groep} (X-Works addocc/remocc)
 */
public record VraagDefinitie(
        String id,
        String label,
        String type,
        boolean verplicht,
        List<String> opties,
        String zichtbaarAls,
        List<VraagDefinitie> subvragen
) {}
