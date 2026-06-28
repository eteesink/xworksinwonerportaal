package nl.verius.inwonerportaal.acl;

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

import java.util.List;
import java.util.Map;

/**
 * Anti-corruption layer naar X-Works.
 *
 * <p>De enige poort tussen het moderne portaal en de legacy X-Works/Uniface-omgeving. Alle lees-
 * en schrijfacties lopen hier doorheen. Belangrijk principe: het portaal schrijft <b>nooit</b>
 * rechtstreeks naar het X-Works datamodel, maar roept de X-Works servicelaag aan ({@code xws} SOAP
 * / StUF / de {@code xria-wiz_*}-operaties). Zo blijven business-logica, validatie, sleutelgeneratie
 * en concurrency in X-Works en ziet de gemeente de ingevoerde gegevens automatisch in haar omgeving.
 *
 * <p>De methoden hieronder spiegelen de handelingen die de burger in het huidige X-Works-portaal
 * uitvoert (module {@code lsd}); de bijbehorende X-Works-operatie staat per methode genoemd.
 *
 * <p>Implementaties: {@code stub} ({@link nl.verius.inwonerportaal.acl.stub.XworksClientStub}) en
 * {@code xworks} ({@link nl.verius.inwonerportaal.acl.xworks.XworksSoapClient}, nog te bouwen).
 */
public interface XworksClient {

    // --- Inzage ------------------------------------------------------------

    /** Eigen PERSOON-occurrence van de ingelogde burger (X-Works lsd-persoon). */
    Persoon getPersoon(String bsn);

    /** Zaken/dossiers van de burger (X-Works lsd-zaken). */
    List<Zaak> getZaken(String bsn);

    // --- Mutatie persoonsgegevens -----------------------------------------

    /** Contactgegevens muteren (X-Works CONTACTGEGEVENS editable, {@code showTransactionOnInput}). */
    void updateContactgegevens(String bsn, Contactgegevens contactgegevens);

    // --- Aanvraag / vragenlijst (X-Works lsd-generate-form) ----------------

    /** Beschikbare vragenlijsten (X-Works query over VRAGENLIJSTTEMPLATE per ADMINISTRATION). */
    List<VragenlijstSamenvatting> getVragenlijstCatalogus();

    /** Metadata-definitie van een vragenlijst ophalen (X-Works {@code wiz-vltmpl00-getocc}). */
    VragenlijstDefinitie getVragenlijstDefinitie(String type);

    /** Nieuwe aanvraag starten (concept). */
    Aanvraag startAanvraag(String bsn, String type);

    /** Bestaande aanvraag ophalen. */
    Aanvraag getAanvraag(String bsn, String aanvraagId);

    /** Regelevaluatie: afgeleide waarden, zichtbaarheid, validaties (X-Works {@code vragenlijst-eval}). */
    EvaluatieResultaat evalueer(String bsn, String aanvraagId, Map<String, Object> antwoorden);

    /** Concept tussentijds opslaan (X-Works {@code vragenlijst-save}). */
    Aanvraag bewaarConcept(String bsn, String aanvraagId, Map<String, Object> antwoorden);

    /** Bijlage toevoegen (X-Works {@code vragenlijst-dropzone-verwerk}). */
    Bijlage voegBijlageToe(String bsn, String aanvraagId, String bestandsnaam, long grootte);

    /** Bijlage verwijderen (X-Works {@code vragenlijst-removeAttachedFile}). */
    void verwijderBijlage(String bsn, String aanvraagId, String bijlageId);

    /** Aanvraag indienen; X-Works genereert het zaaknummer (X-Works {@code vragenlijst-submit}). */
    AanvraagResultaat dienIn(String bsn, String aanvraagId);

    /** Aanvraag met DigiD ondertekenen en indienen (X-Works {@code vragenlijst-signDigiD}). */
    AanvraagResultaat ondertekenMetDigiD(String bsn, String aanvraagId);

    /** Aanvraag afbreken/annuleren (X-Works {@code vragenlijst-abort}). */
    void breekAf(String bsn, String aanvraagId);

    // --- Mede-ondertekenen door een tweede persoon (partner), story 14408 -----

    /**
     * Nodig een partner uit om mee te ondertekenen. Legt de eerste handtekening (de initiator) vast,
     * maakt een eenmalige uitnodigingstoken (X-Works {@code PIE.ACCESSTOKEN}) en "verstuurt" de
     * uitnodigingslink per e-mail (X-Works core-email).
     *
     * @param partnerBsn optioneel: bindt de uitnodiging aan deze BSN (null = elke DigiD-gebruiker met de link)
     */
    MedeondertekenVerzoek nodigPartnerUit(String initiatorBsn, String aanvraagId,
                                          String partnerEmail, String partnerBsn);

    /**
     * Haalt het medeondertekenverzoek op via de token (de partner landt hier na DigiD-login).
     * Valideert token (bestaat/niet verlopen) en de BSN-binding tegen de ingelogde partner.
     */
    CosignView getMedeondertekenVerzoek(String token, String ingelogdeBsn);

    /**
     * De partner ondertekent (DigiD-akkoord op de document-hash). Valideert token + BSN-binding,
     * legt de handtekening + audit vast en zet de status op volledig-getekend.
     */
    AanvraagResultaat partnerOndertekent(String token, String ingelogdeBsn, String naam);

    // --- Integraal Plan (Epic 10): plannen, afspraken, doelen, acties ------

    /** Plannen van de burger (samenvatting + volledige boom). */
    java.util.List<nl.verius.inwonerportaal.acl.model.Plan> getPlannen(String bsn);

    /** Eén plan met afspraken, hoofddoelen, subdoelen en acties. */
    nl.verius.inwonerportaal.acl.model.Plan getPlan(String bsn, String planId);

    /** Afspraak toevoegen aan een plan. */
    nl.verius.inwonerportaal.acl.model.Afspraak voegAfspraakToe(
            String bsn, String planId, nl.verius.inwonerportaal.acl.model.Afspraak afspraak);

    /** Hoofddoel toevoegen aan een plan. */
    nl.verius.inwonerportaal.acl.model.Hoofddoel voegHoofddoelToe(String bsn, String planId, String titel);

    /** Subdoel toevoegen aan een hoofddoel (max. 5 — daarboven een conflict). */
    nl.verius.inwonerportaal.acl.model.Subdoel voegSubdoelToe(
            String bsn, String planId, String hoofddoelId, String titel);

    /** Actie toevoegen aan een subdoel ({@code eenmalig}/{@code herhalend}). */
    nl.verius.inwonerportaal.acl.model.Actie voegActieToe(
            String bsn, String planId, String hoofddoelId, String subdoelId, String omschrijving, String type);

    // --- Voorkeuren (per burger, reist mee met de BSN) ---------------------

    /**
     * Voorkeurstaal (ISO 639-1, bv. {@code nl}/{@code en}) van de burger. In X-Works hoort dit als
     * een per-persoon/-account-voorkeur (bv. op {@code ACCOUNT}/{@code PERSOON}), zodat de standaard
     * met de burger meereist over apparaten heen. Default {@code nl} als niets is ingesteld.
     */
    String getVoorkeurstaal(String bsn);

    /** Stelt de standaard-voorkeurstaal van de burger in (gevalideerd tegen de ondersteunde talen). */
    void setVoorkeurstaal(String bsn, String taal);
}
