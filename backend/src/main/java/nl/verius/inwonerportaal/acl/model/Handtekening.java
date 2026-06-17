package nl.verius.inwonerportaal.acl.model;

/**
 * Eén DigiD-geverifieerde ondertekening (gewone/geavanceerde e-handtekening).
 *
 * @param bsn             ondertekenaar
 * @param naam            weergavenaam
 * @param tijdstip        ISO-tijdstip van ondertekenen
 * @param assuranceNiveau DigiD betrouwbaarheidsniveau (bijv. "Midden", "Substantieel")
 */
public record Handtekening(
        String bsn,
        String naam,
        String tijdstip,
        String assuranceNiveau
) {}
