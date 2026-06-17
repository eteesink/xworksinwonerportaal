package nl.verius.inwonerportaal.acl.model;

import java.time.LocalDate;
import java.util.List;

/**
 * Persoon, afgeleid van X-Works entiteit PERSOON.
 *
 * <p>De {@code bsn} (X-Works {@code NM_BSN}) is de sleutel waarop de DigiD-sessie scope't:
 * een ingelogde burger ziet uitsluitend zijn eigen PERSOON-occurrence.
 */
public record Persoon(
        String bsn,
        String voornaam,
        String achternaam,
        LocalDate geboortedatum,
        String klantnummer,
        List<Adres> adressen,
        Contactgegevens contactgegevens,
        Bankrekening bankrekening
) {}
