package nl.verius.inwonerportaal.acl.stub;

import nl.verius.inwonerportaal.acl.AanvraagNietGevondenException;
import nl.verius.inwonerportaal.acl.PersoonNietGevondenException;
import nl.verius.inwonerportaal.acl.UitnodigingNietGeldigException;
import nl.verius.inwonerportaal.acl.XworksClient;
import nl.verius.inwonerportaal.acl.model.Aanvraag;
import nl.verius.inwonerportaal.acl.model.AanvraagResultaat;
import nl.verius.inwonerportaal.acl.model.Adres;
import nl.verius.inwonerportaal.acl.model.Bankrekening;
import nl.verius.inwonerportaal.acl.model.Bijlage;
import nl.verius.inwonerportaal.acl.model.Contactgegevens;
import nl.verius.inwonerportaal.acl.model.CosignView;
import nl.verius.inwonerportaal.acl.model.EvaluatieResultaat;
import nl.verius.inwonerportaal.acl.model.Handtekening;
import nl.verius.inwonerportaal.acl.model.MedeondertekenVerzoek;
import nl.verius.inwonerportaal.acl.model.Persoon;
import nl.verius.inwonerportaal.acl.model.Validatiefout;
import nl.verius.inwonerportaal.acl.model.VraagDefinitie;
import nl.verius.inwonerportaal.acl.model.VragenlijstDefinitie;
import nl.verius.inwonerportaal.acl.model.VragenlijstSamenvatting;
import nl.verius.inwonerportaal.acl.model.Zaak;
import nl.verius.inwonerportaal.acl.model.Actie;
import nl.verius.inwonerportaal.acl.model.Afspraak;
import nl.verius.inwonerportaal.acl.model.Hoofddoel;
import nl.verius.inwonerportaal.acl.model.Plan;
import nl.verius.inwonerportaal.acl.model.Subdoel;
import nl.verius.inwonerportaal.acl.xworks.template.VragenlijstTemplateMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Stub-implementatie van de X-Works ACL.
 *
 * <p>Vangt de koppeling af met in-memory testdata zodat het portaal volledig draait zonder een
 * live X-Works/Uniface-omgeving. Mutaties (contactgegevens, aanvragen, bijlagen) worden in het
 * geheugen bewaard zodat de frontend ze terugziet. De {@link #evalueer} bevat een vereenvoudigde
 * regelevaluatie die het gedrag van de X-Works operatie {@code vragenlijst-eval} nabootst.
 *
 * <p>Actief onder het standaardprofiel {@code stub}.
 */
@Component
@Profile("stub")
public class XworksClientStub implements XworksClient {

    private static final Logger log = LoggerFactory.getLogger(XworksClientStub.class);

    /** Demo-BSN (Logius-testpersoon) waarvoor testdata bestaat. */
    public static final String DEMO_BSN = "999993653";

    private final Map<String, Persoon> personen = new ConcurrentHashMap<>();
    private final Map<String, List<Zaak>> zakenPerBsn = new ConcurrentHashMap<>();
    private final Map<String, Aanvraag> aanvragen = new ConcurrentHashMap<>();
    private final Map<String, MedeondertekenVerzoek> verzoekenPerToken = new ConcurrentHashMap<>();
    private final Map<String, String> voorkeurstaalPerBsn = new ConcurrentHashMap<>();
    private final Map<String, List<Plan>> plannenPerBsn = new ConcurrentHashMap<>();
    private final AtomicInteger idTeller = new AtomicInteger(1);
    private final AtomicInteger aanvraagTeller = new AtomicInteger(1000);
    private final AtomicInteger zaakTeller = new AtomicInteger(20250500);
    private final AtomicInteger bijlageTeller = new AtomicInteger(1);

    private final VragenlijstTemplateMapper templateMapper;

    public XworksClientStub(VragenlijstTemplateMapper templateMapper) {
        this.templateMapper = templateMapper;
        personen.put(DEMO_BSN, new Persoon(
                DEMO_BSN, "Jan", "de Vries", LocalDate.of(1980, 5, 17), "KLT-100245",
                List.of(
                        new Adres("Dorpsstraat", "12", "1234 AB", "Opmeer", "G"),
                        new Adres("Postbus", "99", "1700 AA", "Heerhugowaard", "C")
                ),
                new Contactgegevens("0226-123456", "jan.devries@example.nl"),
                new Bankrekening("NL02ABNA0123456789", "J. de Vries")
        ));
        zakenPerBsn.put(DEMO_BSN, new ArrayList<>(List.of(
                new Zaak("Z-2025-0481", "Aanvraag bijzondere bijstand", "In behandeling", LocalDate.of(2025, 3, 4)),
                new Zaak("Z-2024-1192", "Melding schuldhulpverlening", "Afgehandeld", LocalDate.of(2024, 11, 20))
        )));

        // Demo-plan conform het Verius-ontwerp (Plan pagina / Hoofddoel / Subdoel).
        Subdoel sub1 = new Subdoel("SD-1", "Meer mensen leren kennen/spreken",
                LocalDate.of(2026, 4, 23), "Dhr. van Tongeren", new ArrayList<>(List.of(
                new Actie("AC-1", "Naar de buurtkoffie gaan", Actie.HERHALEND, false),
                new Actie("AC-2", "Bellen met de buurtcoach", Actie.EENMALIG, true))));
        Subdoel sub2 = new Subdoel("SD-2", "Zelfstandig boodschappen doen",
                LocalDate.of(2026, 4, 23), "Dhr. van Tongeren", new ArrayList<>());
        Hoofddoel hd1 = new Hoofddoel("HD-1", "Zelfredzamer worden",
                new ArrayList<>(List.of(sub1, sub2)));
        Hoofddoel hd2 = new Hoofddoel("HD-2", "Ik wil mij zelf minder eenzaam voelen/zijn",
                new ArrayList<>(List.of(
                        new Subdoel("SD-3", "Wekelijks contact met familie", LocalDate.of(2026, 4, 24),
                                "Dhr. van Tongeren", new ArrayList<>()))));
        Afspraak af1 = new Afspraak("AF-1", "Intake reintegratie plan", LocalDate.of(2026, 5, 20),
                "10:30", "11:15", "Thuis", "Dhr. van Tongeren", Afspraak.VAN_CONSULENT, true);
        Afspraak af2 = new Afspraak("AF-2", "Rollator leverancier gesprek", LocalDate.of(2026, 5, 23),
                "10:30", "11:15", "Bel", "leverancier", Afspraak.VAN_LEVERANCIER, false);
        Plan reintegratie = new Plan("PL-1", "Reintegratie", "Daan Veenstra",
                LocalDateTime.of(2026, 3, 12, 10, 52),
                new ArrayList<>(List.of(af1, af2)), new ArrayList<>(List.of(hd1, hd2)));
        plannenPerBsn.put(DEMO_BSN, new ArrayList<>(List.of(reintegratie)));
    }

    // --- Integraal Plan ----------------------------------------------------

    @Override
    public List<Plan> getPlannen(String bsn) {
        return plannenPerBsn.getOrDefault(bsn, List.of());
    }

    @Override
    public Plan getPlan(String bsn, String planId) {
        return plannenPerBsn.getOrDefault(bsn, List.of()).stream()
                .filter(p -> p.id().equals(planId))
                .findFirst()
                .orElseThrow(() -> new AanvraagNietGevondenException(planId));
    }

    @Override
    public Afspraak voegAfspraakToe(String bsn, String planId, Afspraak afspraak) {
        Plan plan = getPlan(bsn, planId);
        Afspraak nieuw = new Afspraak("AF-" + idTeller.incrementAndGet(), afspraak.titel(),
                afspraak.datum(), afspraak.van(), afspraak.tot(), afspraak.locatie(), afspraak.met(),
                Afspraak.VAN_INWONER, true);
        plan.afspraken().add(nieuw);
        return nieuw;
    }

    @Override
    public Hoofddoel voegHoofddoelToe(String bsn, String planId, String titel) {
        Plan plan = getPlan(bsn, planId);
        Hoofddoel nieuw = new Hoofddoel("HD-" + idTeller.incrementAndGet(), titel, new ArrayList<>());
        plan.hoofddoelen().add(nieuw);
        return nieuw;
    }

    @Override
    public Subdoel voegSubdoelToe(String bsn, String planId, String hoofddoelId, String titel) {
        Hoofddoel hd = vindHoofddoel(bsn, planId, hoofddoelId);
        if (hd.subdoelen().size() >= Hoofddoel.MAX_SUBDOELEN) {
            throw new IllegalStateException("Een hoofddoel kan maximaal " + Hoofddoel.MAX_SUBDOELEN
                    + " subdoelen hebben.");
        }
        Subdoel nieuw = new Subdoel("SD-" + idTeller.incrementAndGet(), titel, LocalDate.now(),
                "Inwoner", new ArrayList<>());
        hd.subdoelen().add(nieuw);
        return nieuw;
    }

    @Override
    public Actie voegActieToe(String bsn, String planId, String hoofddoelId, String subdoelId,
                              String omschrijving, String type) {
        Subdoel sd = vindHoofddoel(bsn, planId, hoofddoelId).subdoelen().stream()
                .filter(s -> s.id().equals(subdoelId))
                .findFirst()
                .orElseThrow(() -> new AanvraagNietGevondenException(subdoelId));
        String t = Actie.HERHALEND.equals(type) ? Actie.HERHALEND : Actie.EENMALIG;
        Actie nieuw = new Actie("AC-" + idTeller.incrementAndGet(), omschrijving, t, false);
        sd.acties().add(nieuw);
        return nieuw;
    }

    private Hoofddoel vindHoofddoel(String bsn, String planId, String hoofddoelId) {
        return getPlan(bsn, planId).hoofddoelen().stream()
                .filter(h -> h.id().equals(hoofddoelId))
                .findFirst()
                .orElseThrow(() -> new AanvraagNietGevondenException(hoofddoelId));
    }

    // --- Voorkeuren --------------------------------------------------------

    @Override
    public String getVoorkeurstaal(String bsn) {
        return voorkeurstaalPerBsn.getOrDefault(bsn, "nl");
    }

    @Override
    public void setVoorkeurstaal(String bsn, String taal) {
        voorkeurstaalPerBsn.put(bsn, taal);
        log.info("Voorkeurstaal van burger {} gezet op {}", bsn, taal);
    }

    // --- Inzage ------------------------------------------------------------

    @Override
    public Persoon getPersoon(String bsn) {
        Persoon persoon = personen.get(bsn);
        if (persoon == null) {
            throw new PersoonNietGevondenException(bsn);
        }
        return persoon;
    }

    @Override
    public List<Zaak> getZaken(String bsn) {
        return zakenPerBsn.getOrDefault(bsn, List.of());
    }

    // --- Contactgegevens ---------------------------------------------------

    @Override
    public void updateContactgegevens(String bsn, Contactgegevens contactgegevens) {
        Persoon huidig = getPersoon(bsn);
        personen.put(bsn, new Persoon(
                huidig.bsn(), huidig.voornaam(), huidig.achternaam(), huidig.geboortedatum(),
                huidig.klantnummer(), huidig.adressen(), contactgegevens, huidig.bankrekening()
        ));
        log.info("[STUB] Contactgegevens bijgewerkt voor BSN {}: {}", bsn, contactgegevens);
    }

    // --- Aanvraag / vragenlijst -------------------------------------------

    @Override
    public List<VragenlijstSamenvatting> getVragenlijstCatalogus() {
        // Simuleert een X-Works query over VRAGENLIJSTTEMPLATE: scan de beschikbare templates.
        // Een nieuw vltmpl-*.xml bestand verschijnt automatisch als nieuwe vragenlijst.
        List<VragenlijstSamenvatting> catalogus = new ArrayList<>();
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver()
                    .getResources("classpath:xworks-templates/vltmpl-*.xml");
            for (Resource r : resources) {
                try (InputStream in = r.getInputStream()) {
                    VragenlijstDefinitie def = templateMapper.map(in);
                    catalogus.add(new VragenlijstSamenvatting(def.type(), def.titel()));
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Kon vragenlijst-catalogus niet laden: " + e.getMessage(), e);
        }
        catalogus.sort(Comparator.comparing(VragenlijstSamenvatting::titel));
        log.info("[STUB] Vragenlijst-catalogus opgehaald ({} vragenlijsten)", catalogus.size());
        return catalogus;
    }

    @Override
    public VragenlijstDefinitie getVragenlijstDefinitie(String type) {
        // Simuleert de X-Works operatie wiz-vltmpl00-getocc: haal de template-respons op en map die.
        // In de echte koppeling vervangt XworksSoapClient de resource door de live getocc-call;
        // dezelfde VragenlijstTemplateMapper verwerkt beide.
        String resource = "xworks-templates/vltmpl-" + type + ".xml";
        try (InputStream in = new ClassPathResource(resource).getInputStream()) {
            log.info("[STUB] Vragenlijst-template '{}' opgehaald via (gesimuleerde) wiz-vltmpl00-getocc", type);
            return templateMapper.map(in);
        } catch (IOException e) {
            throw new IllegalArgumentException("Onbekend vragenlijsttype: " + type);
        }
    }

    @Override
    public Aanvraag startAanvraag(String bsn, String type) {
        VragenlijstDefinitie def = getVragenlijstDefinitie(type);
        String id = "AANVR-" + aanvraagTeller.incrementAndGet();
        Map<String, Object> leeg = new LinkedHashMap<>();
        leeg.put("kostenposten", new ArrayList<>());
        Aanvraag aanvraag = new Aanvraag(id, type, def.titel(), "concept", leeg, new ArrayList<>(), null);
        aanvragen.put(sleutel(bsn, id), aanvraag);
        log.info("[STUB] Aanvraag gestart {} ({}) voor BSN {}", id, type, bsn);
        return aanvraag;
    }

    @Override
    public Aanvraag getAanvraag(String bsn, String aanvraagId) {
        Aanvraag aanvraag = aanvragen.get(sleutel(bsn, aanvraagId));
        if (aanvraag == null) {
            throw new AanvraagNietGevondenException(aanvraagId);
        }
        return aanvraag;
    }

    @Override
    @SuppressWarnings("unchecked")
    public EvaluatieResultaat evalueer(String bsn, String aanvraagId, Map<String, Object> antwoorden) {
        // Generieke, metadata-gedreven nabootsing van X-Works vragenlijst-eval: werkt voor elke
        // template. De regels worden afgeleid uit de definitie (verplicht, zichtbaarAls, groep/bedrag).
        Aanvraag aanvraag = getAanvraag(bsn, aanvraagId);
        VragenlijstDefinitie def = getVragenlijstDefinitie(aanvraag.type());

        Map<String, Object> afgeleid = new LinkedHashMap<>();
        Map<String, Boolean> zichtbaar = new LinkedHashMap<>();
        List<Validatiefout> fouten = new ArrayList<>();
        boolean heeftBedragGroep = false;
        double totaal = 0.0;

        for (VraagDefinitie v : def.vragen()) {
            boolean visible = bepaalZichtbaar(v, antwoorden);
            if (v.zichtbaarAls() != null) {
                zichtbaar.put(v.id(), visible);
            }
            if (!visible) {
                continue; // niet-zichtbare vragen worden niet gevalideerd
            }

            if ("groep".equals(v.type())) {
                List<Map<String, Object>> rijen = antwoorden.get(v.id()) instanceof List<?> l
                        ? (List<Map<String, Object>>) l : List.of();
                boolean groepHeeftBedrag = v.subvragen() != null
                        && v.subvragen().stream().anyMatch(s -> "bedrag".equals(s.type()));
                if (groepHeeftBedrag) {
                    heeftBedragGroep = true;
                }
                if (v.verplicht() && rijen.isEmpty()) {
                    fouten.add(new Validatiefout(v.id(), "Voeg minimaal één regel toe bij '" + v.label() + "'."));
                }
                boolean rijFout = false;
                for (Map<String, Object> rij : rijen) {
                    if (v.subvragen() != null) {
                        for (VraagDefinitie sub : v.subvragen()) {
                            if (sub.verplicht() && asText(rij.get(sub.id())).isBlank()) {
                                rijFout = true;
                            }
                            if ("bedrag".equals(sub.type())) {
                                totaal += toDouble(rij.get(sub.id()));
                            }
                        }
                    }
                }
                if (rijFout) {
                    fouten.add(new Validatiefout(v.id(), "Vul alle verplichte velden in de regels in."));
                }
            } else {
                if (v.verplicht() && asText(antwoorden.get(v.id())).isBlank()) {
                    fouten.add(new Validatiefout(v.id(), v.label() + " is verplicht."));
                }
            }
        }

        if (heeftBedragGroep) {
            afgeleid.put("totaalBedrag", Math.round(totaal * 100.0) / 100.0);
        }

        return new EvaluatieResultaat(afgeleid, zichtbaar, fouten, fouten.isEmpty());
    }

    /** Evalueert een eenvoudige zichtbaarheidsconditie {@code "veldId==waarde"}. */
    private static boolean bepaalZichtbaar(VraagDefinitie v, Map<String, Object> antwoorden) {
        String cond = v.zichtbaarAls();
        if (cond == null || cond.isBlank()) {
            return true;
        }
        int idx = cond.indexOf("==");
        if (idx < 0) {
            return true;
        }
        String veld = cond.substring(0, idx).trim();
        String waarde = cond.substring(idx + 2).trim();
        return waarde.equals(asText(antwoorden.get(veld)));
    }

    @Override
    public Aanvraag bewaarConcept(String bsn, String aanvraagId, Map<String, Object> antwoorden) {
        Aanvraag huidig = getAanvraag(bsn, aanvraagId);
        Aanvraag bijgewerkt = new Aanvraag(huidig.id(), huidig.type(), huidig.titel(), "concept",
                antwoorden, huidig.bijlagen(), huidig.zaaknummer());
        aanvragen.put(sleutel(bsn, aanvraagId), bijgewerkt);
        log.info("[STUB] Concept opgeslagen voor aanvraag {} (BSN {})", aanvraagId, bsn);
        return bijgewerkt;
    }

    @Override
    public Bijlage voegBijlageToe(String bsn, String aanvraagId, String bestandsnaam, long grootte) {
        Aanvraag huidig = getAanvraag(bsn, aanvraagId);
        Bijlage bijlage = new Bijlage("BIJL-" + bijlageTeller.incrementAndGet(), bestandsnaam, grootte);
        List<Bijlage> nieuw = new ArrayList<>(huidig.bijlagen());
        nieuw.add(bijlage);
        aanvragen.put(sleutel(bsn, aanvraagId), new Aanvraag(huidig.id(), huidig.type(), huidig.titel(),
                huidig.status(), huidig.antwoorden(), nieuw, huidig.zaaknummer()));
        log.info("[STUB] Bijlage '{}' ({} bytes) toegevoegd aan aanvraag {}", bestandsnaam, grootte, aanvraagId);
        return bijlage;
    }

    @Override
    public void verwijderBijlage(String bsn, String aanvraagId, String bijlageId) {
        Aanvraag huidig = getAanvraag(bsn, aanvraagId);
        List<Bijlage> nieuw = new ArrayList<>(huidig.bijlagen());
        nieuw.removeIf(b -> b.id().equals(bijlageId));
        aanvragen.put(sleutel(bsn, aanvraagId), new Aanvraag(huidig.id(), huidig.type(), huidig.titel(),
                huidig.status(), huidig.antwoorden(), nieuw, huidig.zaaknummer()));
        log.info("[STUB] Bijlage {} verwijderd uit aanvraag {}", bijlageId, aanvraagId);
    }

    @Override
    public AanvraagResultaat dienIn(String bsn, String aanvraagId) {
        return verwerkIndienen(bsn, aanvraagId, "ingediend", "Ontvangen");
    }

    @Override
    public AanvraagResultaat ondertekenMetDigiD(String bsn, String aanvraagId) {
        return verwerkIndienen(bsn, aanvraagId, "ondertekend", "Ontvangen (DigiD-ondertekend)");
    }

    @Override
    public void breekAf(String bsn, String aanvraagId) {
        aanvragen.remove(sleutel(bsn, aanvraagId));
        log.info("[STUB] Aanvraag {} afgebroken (BSN {})", aanvraagId, bsn);
    }

    // --- Mede-ondertekenen (story 14408) ----------------------------------

    @Override
    public MedeondertekenVerzoek nodigPartnerUit(String initiatorBsn, String aanvraagId,
                                                 String partnerEmail, String partnerBsn) {
        Aanvraag aanvraag = getAanvraag(initiatorBsn, aanvraagId);
        Persoon initiator = getPersoon(initiatorBsn);
        // De eerste handtekening (de initiator tekende al in het portaal).
        Handtekening eerste = new Handtekening(initiatorBsn,
                initiator.voornaam() + " " + initiator.achternaam(),
                LocalDateTime.now().toString(), "Midden");
        String token = UUID.randomUUID().toString();
        MedeondertekenVerzoek verzoek = new MedeondertekenVerzoek(
                token, aanvraagId, aanvraag.titel(), documentHash(aanvraag),
                partnerEmail, (partnerBsn == null || partnerBsn.isBlank()) ? null : partnerBsn.trim(),
                "wacht-op-partner", LocalDateTime.now().plusDays(7).toString(),
                new ArrayList<>(List.of(eerste)));
        verzoekenPerToken.put(token, verzoek);
        // Stub voor core-email: log de uitnodigingslink (deep-link met token).
        log.info("[STUB] Uitnodiging mede-ondertekenen verstuurd naar {} — link: /?cosign={}", partnerEmail, token);
        return verzoek;
    }

    @Override
    public CosignView getMedeondertekenVerzoek(String token, String ingelogdeBsn) {
        MedeondertekenVerzoek v = geldigVerzoek(token);
        boolean magTekenen = true;
        String reden = null;
        if ("volledig-getekend".equals(v.status())) {
            magTekenen = false;
            reden = "Dit document is al volledig ondertekend.";
        } else if (v.partnerBsn() != null && !v.partnerBsn().equals(ingelogdeBsn)) {
            magTekenen = false;
            reden = "Deze uitnodiging is gericht aan een andere persoon (BSN komt niet overeen).";
        }
        return new CosignView(v.aanvraagId(), v.titel(), v.documentHash(), v.status(),
                v.handtekeningen(), magTekenen, reden);
    }

    @Override
    public AanvraagResultaat partnerOndertekent(String token, String ingelogdeBsn, String naam) {
        MedeondertekenVerzoek v = geldigVerzoek(token);
        if ("volledig-getekend".equals(v.status())) {
            throw new IllegalStateException("Document is al volledig ondertekend.");
        }
        if (v.partnerBsn() != null && !v.partnerBsn().equals(ingelogdeBsn)) {
            throw new IllegalStateException("U bent niet de uitgenodigde medeondertekenaar (BSN komt niet overeen).");
        }
        List<Handtekening> nieuw = new ArrayList<>(v.handtekeningen());
        nieuw.add(new Handtekening(ingelogdeBsn, naam == null || naam.isBlank() ? "Partner" : naam,
                LocalDateTime.now().toString(), "Midden"));
        MedeondertekenVerzoek getekend = new MedeondertekenVerzoek(v.token(), v.aanvraagId(), v.titel(),
                v.documentHash(), v.partnerEmail(), v.partnerBsn(), "volledig-getekend",
                v.verlooptOp(), nieuw);
        verzoekenPerToken.put(token, getekend);
        log.info("[STUB] Document {} volledig ondertekend ({} handtekeningen, partner BSN {})",
                v.aanvraagId(), nieuw.size(), ingelogdeBsn);
        return new AanvraagResultaat(v.aanvraagId(), "Volledig ondertekend");
    }

    private MedeondertekenVerzoek geldigVerzoek(String token) {
        MedeondertekenVerzoek v = verzoekenPerToken.get(token);
        if (v == null) {
            throw new UitnodigingNietGeldigException("Onbekende of ingetrokken uitnodiging.");
        }
        if (LocalDateTime.parse(v.verlooptOp()).isBefore(LocalDateTime.now())) {
            throw new UitnodigingNietGeldigException("De uitnodiging is verlopen.");
        }
        return v;
    }

    private static String documentHash(Aanvraag aanvraag) {
        // Demo-hash (stabiel per aanvraag). In productie: echte hash van het PDF/document.
        int h = (aanvraag.id() + "|" + aanvraag.titel()).hashCode();
        return "sha256:demo-" + Integer.toHexString(h);
    }

    // --- intern ------------------------------------------------------------

    private AanvraagResultaat verwerkIndienen(String bsn, String aanvraagId, String status, String resultaatStatus) {
        Aanvraag huidig = getAanvraag(bsn, aanvraagId);
        EvaluatieResultaat eval = evalueer(bsn, aanvraagId, huidig.antwoorden());
        if (!eval.indienbaar()) {
            throw new IllegalStateException("Aanvraag is niet indienbaar: er zijn nog validatiefouten.");
        }
        String zaaknummer = "Z-2025-" + zaakTeller.incrementAndGet();
        aanvragen.put(sleutel(bsn, aanvraagId), new Aanvraag(huidig.id(), huidig.type(), huidig.titel(),
                status, huidig.antwoorden(), huidig.bijlagen(), zaaknummer));
        // De ingediende aanvraag verschijnt als zaak in het zakenoverzicht (zoals X-Works zak-saveWizard).
        zakenPerBsn.computeIfAbsent(bsn, k -> new ArrayList<>())
                .add(0, new Zaak(zaaknummer, huidig.titel(), "Ontvangen", LocalDate.of(2025, 6, 15)));
        log.info("[STUB] Aanvraag {} {} -> zaak {}", aanvraagId, status, zaaknummer);
        return new AanvraagResultaat(zaaknummer, resultaatStatus);
    }

    private static String sleutel(String bsn, String aanvraagId) {
        return bsn + "::" + aanvraagId;
    }

    private static String asText(Object o) {
        return o == null ? "" : o.toString().trim();
    }

    private static double toDouble(Object o) {
        if (o == null) return 0.0;
        if (o instanceof Number n) return n.doubleValue();
        try {
            return Double.parseDouble(o.toString().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
