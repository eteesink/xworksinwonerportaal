package nl.verius.inwonerportaal.acl.xworks;

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
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Echte X-Works-koppeling (placeholder).
 *
 * <p>Hier komt de implementatie tegen de X-Works servicelaag. Voorkeursvolgorde:
 * <ol>
 *   <li>StUF/ZDS (zaakgericht werken) voor zaken en aanvragen — module {@code StUF/} (ZDS0120, ZKN, DCR);</li>
 *   <li>de {@code xws} SOAP-adapter ({@code xws-adapter00}, {@code xws-soapWrap/Unwrap}) voor de rest;</li>
 *   <li>de {@code xria-wiz_*}-operaties ({@code vragenlijst-eval/save/submit/signDigiD}, {@code zak-saveWizard}).</li>
 * </ol>
 *
 * <p>Schrijf nooit rechtstreeks naar de Uniface-DB-tabellen: dat omzeilt sleutelgeneratie,
 * trigger-/proc-code, de {@code _crc}/{@code _status}-concurrency en referentiële regels.
 *
 * <p>Actief onder profiel {@code xworks}. Nu nog niet geïmplementeerd.
 */
@Component
@Profile("xworks")
public class XworksSoapClient implements XworksClient {

    private static final String TODO = "X-Works koppeling nog niet geïmplementeerd. "
            + "Implementeer tegen de StUF/xws-servicelaag. Draai voorlopig met --spring.profiles.active=stub.";

    private static UnsupportedOperationException tedoen() {
        return new UnsupportedOperationException(TODO);
    }

    @Override
    public Persoon getPersoon(String bsn) {
        throw tedoen();
    }

    @Override
    public List<Zaak> getZaken(String bsn) {
        throw tedoen();
    }

    @Override
    public void updateContactgegevens(String bsn, Contactgegevens contactgegevens) {
        throw tedoen();
    }

    @Override
    public List<VragenlijstSamenvatting> getVragenlijstCatalogus() {
        // Echte implementatie: query X-Works VRAGENLIJSTTEMPLATE voor de ADMINISTRATION (gemeente).
        throw tedoen();
    }

    @Override
    public VragenlijstDefinitie getVragenlijstDefinitie(String type) {
        // Echte implementatie: roep de Uniface vltmpl-getocc-operatie aan (het "XML Form document"
        // achter wiz-vltmpl00-getocc.xslt — geen SOAP/REST-endpoint op zich), per ADMINISTRATION/gemeente,
        // en map de respons met VragenlijstTemplateMapper (dezelfde mapper als de stub). Zie docs/xworks-template-service.md:
        //   var xml = xworksTemplateService.getTemplate(type, gemeentecode); // via xws SOAP / StUF / REST-wrapper
        //   return templateMapper.map(xml);
        throw tedoen();
    }

    @Override
    public Aanvraag startAanvraag(String bsn, String type) {
        throw tedoen();
    }

    @Override
    public Aanvraag getAanvraag(String bsn, String aanvraagId) {
        throw tedoen();
    }

    @Override
    public EvaluatieResultaat evalueer(String bsn, String aanvraagId, Map<String, Object> antwoorden) {
        throw tedoen();
    }

    @Override
    public Aanvraag bewaarConcept(String bsn, String aanvraagId, Map<String, Object> antwoorden) {
        throw tedoen();
    }

    @Override
    public Bijlage voegBijlageToe(String bsn, String aanvraagId, String bestandsnaam, long grootte) {
        throw tedoen();
    }

    @Override
    public void verwijderBijlage(String bsn, String aanvraagId, String bijlageId) {
        throw tedoen();
    }

    @Override
    public AanvraagResultaat dienIn(String bsn, String aanvraagId) {
        throw tedoen();
    }

    @Override
    public AanvraagResultaat ondertekenMetDigiD(String bsn, String aanvraagId) {
        throw tedoen();
    }

    @Override
    public void breekAf(String bsn, String aanvraagId) {
        throw tedoen();
    }

    @Override
    public MedeondertekenVerzoek nodigPartnerUit(String initiatorBsn, String aanvraagId,
                                                 String partnerEmail, String partnerBsn) {
        // Echte implementatie: PIE.ACCESSTOKEN aanmaken + core-email versturen met de deep-link.
        throw tedoen();
    }

    @Override
    public CosignView getMedeondertekenVerzoek(String token, String ingelogdeBsn) {
        throw tedoen();
    }

    @Override
    public AanvraagResultaat partnerOndertekent(String token, String ingelogdeBsn, String naam) {
        // Echte implementatie: vragenlijst-signDigiD voor de tweede ondertekenaar + audit.
        throw tedoen();
    }
}
