package nl.verius.inwonerportaal.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nl.verius.inwonerportaal.acl.model.AanvraagResultaat;
import nl.verius.inwonerportaal.acl.model.Aanvraag;
import nl.verius.inwonerportaal.acl.model.Bijlage;
import nl.verius.inwonerportaal.acl.model.EvaluatieResultaat;
import nl.verius.inwonerportaal.acl.model.VragenlijstDefinitie;
import nl.verius.inwonerportaal.acl.model.VragenlijstSamenvatting;
import nl.verius.inwonerportaal.service.InwonerService;
import nl.verius.inwonerportaal.web.dto.BijlageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST-API voor het invullen, opslaan, evalueren, indienen en ondertekenen van aanvragen.
 *
 * <p>Spiegelt de handelingen die de burger in het X-Works-portaal uitvoert via de
 * {@code lsd-generate-form}/{@code xria-wiz_vrgnlijst00}-operaties.
 */
@RestController
@RequestMapping("/api/aanvragen")
public class AanvraagController {

    private final InwonerService inwoners;
    private final HuidigeBurger huidigeBurger;

    public AanvraagController(InwonerService inwoners, HuidigeBurger huidigeBurger) {
        this.inwoners = inwoners;
        this.huidigeBurger = huidigeBurger;
    }

    /** Catalogus van beschikbare vragenlijsten (X-Works VRAGENLIJSTTEMPLATE). */
    @GetMapping("/catalogus")
    public List<VragenlijstSamenvatting> catalogus() {
        return inwoners.catalogus();
    }

    /** Metadata-definitie van een vragenlijst (X-Works wiz-vltmpl00-getocc). */
    @GetMapping("/definities/{type}")
    public VragenlijstDefinitie definitie(@PathVariable String type) {
        return inwoners.definitie(type);
    }

    /** Nieuwe aanvraag starten (concept). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Aanvraag start(HttpServletRequest request, @RequestParam String type) {
        return inwoners.startAanvraag(huidigeBurger.bsn(request), type);
    }

    /** Aanvraag ophalen. */
    @GetMapping("/{id}")
    public Aanvraag get(HttpServletRequest request, @PathVariable String id) {
        return inwoners.aanvraag(huidigeBurger.bsn(request), id);
    }

    /** Regelevaluatie (X-Works vragenlijst-eval): afgeleide waarden, zichtbaarheid, validatie. */
    @PostMapping("/{id}/evaluatie")
    public EvaluatieResultaat evalueer(HttpServletRequest request, @PathVariable String id,
                                       @RequestBody Map<String, Object> antwoorden) {
        return inwoners.evalueer(huidigeBurger.bsn(request), id, antwoorden);
    }

    /** Concept tussentijds opslaan (X-Works vragenlijst-save). */
    @PutMapping("/{id}/concept")
    public Aanvraag bewaarConcept(HttpServletRequest request, @PathVariable String id,
                                  @RequestBody Map<String, Object> antwoorden) {
        return inwoners.bewaarConcept(huidigeBurger.bsn(request), id, antwoorden);
    }

    /** Bijlage toevoegen (X-Works vragenlijst-dropzone-verwerk). */
    @PostMapping("/{id}/bijlagen")
    @ResponseStatus(HttpStatus.CREATED)
    public Bijlage voegBijlageToe(HttpServletRequest request, @PathVariable String id,
                                  @Valid @RequestBody BijlageRequest body) {
        return inwoners.voegBijlageToe(huidigeBurger.bsn(request), id, body.bestandsnaam(), body.grootte());
    }

    /** Bijlage verwijderen (X-Works vragenlijst-removeAttachedFile). */
    @DeleteMapping("/{id}/bijlagen/{bijlageId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void verwijderBijlage(HttpServletRequest request, @PathVariable String id,
                                 @PathVariable String bijlageId) {
        inwoners.verwijderBijlage(huidigeBurger.bsn(request), id, bijlageId);
    }

    /** Aanvraag indienen (X-Works vragenlijst-submit). */
    @PostMapping("/{id}/indienen")
    public AanvraagResultaat dienIn(HttpServletRequest request, @PathVariable String id) {
        return inwoners.dienIn(huidigeBurger.bsn(request), id);
    }

    /** Aanvraag met DigiD ondertekenen en indienen (X-Works vragenlijst-signDigiD). */
    @PostMapping("/{id}/ondertekenen")
    public AanvraagResultaat ondertekenMetDigiD(HttpServletRequest request, @PathVariable String id) {
        return inwoners.ondertekenMetDigiD(huidigeBurger.bsn(request), id);
    }

    /** Aanvraag afbreken (X-Works vragenlijst-abort). */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void breekAf(HttpServletRequest request, @PathVariable String id) {
        inwoners.breekAf(huidigeBurger.bsn(request), id);
    }
}
