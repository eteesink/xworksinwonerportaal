package nl.verius.inwonerportaal.service;

import nl.verius.inwonerportaal.acl.XworksClient;
import nl.verius.inwonerportaal.acl.model.AanvraagResultaat;
import nl.verius.inwonerportaal.acl.model.Aanvraag;
import nl.verius.inwonerportaal.acl.model.Bijlage;
import nl.verius.inwonerportaal.acl.model.Contactgegevens;
import nl.verius.inwonerportaal.acl.model.CosignView;
import nl.verius.inwonerportaal.acl.model.EvaluatieResultaat;
import nl.verius.inwonerportaal.acl.model.MedeondertekenVerzoek;
import nl.verius.inwonerportaal.acl.model.Persoon;
import nl.verius.inwonerportaal.acl.model.VragenlijstDefinitie;
import nl.verius.inwonerportaal.acl.model.VragenlijstSamenvatting;
import nl.verius.inwonerportaal.acl.model.Zaak;
import nl.verius.inwonerportaal.acl.model.Actie;
import nl.verius.inwonerportaal.acl.model.Afspraak;
import nl.verius.inwonerportaal.acl.model.Hoofddoel;
import nl.verius.inwonerportaal.acl.model.Plan;
import nl.verius.inwonerportaal.acl.model.Subdoel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Toepassingslogica van het inwonerportaal. Orchestreert de ACL en is de plek waar later
 * autorisatie ("mag deze BSN dit?"), audit-logging en mapping/aggregatie landen.
 */
@Service
public class InwonerService {

    /** Ondersteunde portaaltalen (ISO 639-1). Uitbreidbaar; houd in sync met de frontend-resources. */
    public static final Set<String> ONDERSTEUNDE_TALEN = Set.of("nl", "en");

    private final XworksClient xworks;

    public InwonerService(XworksClient xworks) {
        this.xworks = xworks;
    }

    // Voorkeuren
    public String voorkeurstaal(String bsn) {
        return xworks.getVoorkeurstaal(bsn);
    }

    public void zetVoorkeurstaal(String bsn, String taal) {
        String genormaliseerd = taal == null ? "" : taal.trim().toLowerCase();
        if (!ONDERSTEUNDE_TALEN.contains(genormaliseerd)) {
            throw new IllegalArgumentException("Niet-ondersteunde taal: " + taal);
        }
        xworks.setVoorkeurstaal(bsn, genormaliseerd);
    }

    // Integraal Plan (Epic 10)
    public List<Plan> plannen(String bsn) {
        return xworks.getPlannen(bsn);
    }

    public Plan plan(String bsn, String planId) {
        return xworks.getPlan(bsn, planId);
    }

    public Afspraak voegAfspraakToe(String bsn, String planId, Afspraak afspraak) {
        return xworks.voegAfspraakToe(bsn, planId, afspraak);
    }

    public Hoofddoel voegHoofddoelToe(String bsn, String planId, String titel) {
        return xworks.voegHoofddoelToe(bsn, planId, titel);
    }

    public Subdoel voegSubdoelToe(String bsn, String planId, String hoofddoelId, String titel) {
        return xworks.voegSubdoelToe(bsn, planId, hoofddoelId, titel);
    }

    public Actie voegActieToe(String bsn, String planId, String hoofddoelId, String subdoelId,
                              String omschrijving, String type) {
        return xworks.voegActieToe(bsn, planId, hoofddoelId, subdoelId, omschrijving, type);
    }

    // Inzage
    public Persoon mijnGegevens(String bsn) {
        return xworks.getPersoon(bsn);
    }

    public List<Zaak> mijnZaken(String bsn) {
        return xworks.getZaken(bsn);
    }

    // Contactgegevens
    public void wijzigContactgegevens(String bsn, Contactgegevens contactgegevens) {
        // TODO: audit-logging van de mutatie (wie, wat, wanneer).
        xworks.updateContactgegevens(bsn, contactgegevens);
    }

    // Aanvraag / vragenlijst
    public List<VragenlijstSamenvatting> catalogus() {
        return xworks.getVragenlijstCatalogus();
    }

    public VragenlijstDefinitie definitie(String type) {
        return xworks.getVragenlijstDefinitie(type);
    }

    public Aanvraag startAanvraag(String bsn, String type) {
        return xworks.startAanvraag(bsn, type);
    }

    public Aanvraag aanvraag(String bsn, String aanvraagId) {
        return xworks.getAanvraag(bsn, aanvraagId);
    }

    public EvaluatieResultaat evalueer(String bsn, String aanvraagId, Map<String, Object> antwoorden) {
        return xworks.evalueer(bsn, aanvraagId, antwoorden);
    }

    public Aanvraag bewaarConcept(String bsn, String aanvraagId, Map<String, Object> antwoorden) {
        return xworks.bewaarConcept(bsn, aanvraagId, antwoorden);
    }

    public Bijlage voegBijlageToe(String bsn, String aanvraagId, String bestandsnaam, long grootte) {
        return xworks.voegBijlageToe(bsn, aanvraagId, bestandsnaam, grootte);
    }

    public void verwijderBijlage(String bsn, String aanvraagId, String bijlageId) {
        xworks.verwijderBijlage(bsn, aanvraagId, bijlageId);
    }

    public AanvraagResultaat dienIn(String bsn, String aanvraagId) {
        return xworks.dienIn(bsn, aanvraagId);
    }

    public AanvraagResultaat ondertekenMetDigiD(String bsn, String aanvraagId) {
        return xworks.ondertekenMetDigiD(bsn, aanvraagId);
    }

    public void breekAf(String bsn, String aanvraagId) {
        xworks.breekAf(bsn, aanvraagId);
    }

    // Mede-ondertekenen
    public MedeondertekenVerzoek nodigPartnerUit(String initiatorBsn, String aanvraagId,
                                                 String partnerEmail, String partnerBsn) {
        return xworks.nodigPartnerUit(initiatorBsn, aanvraagId, partnerEmail, partnerBsn);
    }

    public CosignView medeondertekenVerzoek(String token, String ingelogdeBsn) {
        return xworks.getMedeondertekenVerzoek(token, ingelogdeBsn);
    }

    public AanvraagResultaat partnerOndertekent(String token, String ingelogdeBsn, String naam) {
        return xworks.partnerOndertekent(token, ingelogdeBsn, naam);
    }
}
