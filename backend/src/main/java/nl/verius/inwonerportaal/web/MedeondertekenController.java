package nl.verius.inwonerportaal.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nl.verius.inwonerportaal.acl.model.AanvraagResultaat;
import nl.verius.inwonerportaal.acl.model.CosignView;
import nl.verius.inwonerportaal.acl.model.MedeondertekenVerzoek;
import nl.verius.inwonerportaal.service.InwonerService;
import nl.verius.inwonerportaal.web.dto.UitnodigingRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Mede-ondertekenen van een document door een tweede persoon (partner) via DigiD — story 14408.
 *
 * <p>De initiator (ingelogde burger) nodigt de partner uit; de partner opent de uitnodigingslink,
 * logt met DigiD in, landt direct op de ondertekenpagina en tekent. Het getekende resultaat blijft
 * in het portaal — er wordt geen te-tekenen document gemaild.
 */
@RestController
@RequestMapping("/api")
public class MedeondertekenController {

    private final InwonerService inwoners;
    private final HuidigeBurger huidigeBurger;

    public MedeondertekenController(InwonerService inwoners, HuidigeBurger huidigeBurger) {
        this.inwoners = inwoners;
        this.huidigeBurger = huidigeBurger;
    }

    /** De initiator nodigt een partner uit (verstuurt de uitnodigingslink per e-mail). */
    @PostMapping("/aanvragen/{id}/medeondertekenaar")
    @ResponseStatus(HttpStatus.CREATED)
    public MedeondertekenVerzoek nodigUit(HttpServletRequest request, @PathVariable String id,
                                          @Valid @RequestBody UitnodigingRequest body) {
        return inwoners.nodigPartnerUit(huidigeBurger.bsn(request), id, body.partnerEmail(), body.partnerBsn());
    }

    /** De partner opent de uitnodiging (na DigiD-login) en ziet wat er te ondertekenen valt. */
    @GetMapping("/medeondertekenen/{token}")
    public CosignView open(HttpServletRequest request, @PathVariable String token) {
        return inwoners.medeondertekenVerzoek(token, huidigeBurger.bsn(request));
    }

    /** De partner ondertekent (DigiD-akkoord). */
    @PostMapping("/medeondertekenen/{token}/ondertekenen")
    public AanvraagResultaat onderteken(HttpServletRequest request, @PathVariable String token) {
        return inwoners.partnerOndertekent(token, huidigeBurger.bsn(request), null);
    }
}
