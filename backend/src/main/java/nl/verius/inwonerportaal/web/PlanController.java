package nl.verius.inwonerportaal.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import nl.verius.inwonerportaal.acl.model.Actie;
import nl.verius.inwonerportaal.acl.model.Afspraak;
import nl.verius.inwonerportaal.acl.model.Hoofddoel;
import nl.verius.inwonerportaal.acl.model.Plan;
import nl.verius.inwonerportaal.acl.model.Subdoel;
import nl.verius.inwonerportaal.service.InwonerService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * Integraal Plan van de ingelogde burger (baseline Epic 10): plannen, afspraken, hoofddoelen,
 * subdoelen en acties. BSN via {@link HuidigeBurger}.
 */
@RestController
@RequestMapping("/api/plannen")
public class PlanController {

    private final InwonerService inwoners;
    private final HuidigeBurger huidigeBurger;

    public PlanController(InwonerService inwoners, HuidigeBurger huidigeBurger) {
        this.inwoners = inwoners;
        this.huidigeBurger = huidigeBurger;
    }

    @GetMapping
    public List<Plan> plannen(HttpServletRequest request) {
        return inwoners.plannen(huidigeBurger.bsn(request));
    }

    @GetMapping("/{planId}")
    public Plan plan(HttpServletRequest request, @PathVariable String planId) {
        return inwoners.plan(huidigeBurger.bsn(request), planId);
    }

    @PostMapping("/{planId}/afspraken")
    @ResponseStatus(HttpStatus.CREATED)
    public Afspraak voegAfspraakToe(HttpServletRequest request, @PathVariable String planId,
                                    @RequestBody AfspraakRequest body) {
        Afspraak invoer = new Afspraak(null, body.titel(), body.datum(), body.van(), body.tot(),
                body.locatie(), body.met(), Afspraak.VAN_INWONER, true);
        return inwoners.voegAfspraakToe(huidigeBurger.bsn(request), planId, invoer);
    }

    @PostMapping("/{planId}/hoofddoelen")
    @ResponseStatus(HttpStatus.CREATED)
    public Hoofddoel voegHoofddoelToe(HttpServletRequest request, @PathVariable String planId,
                                      @RequestBody TitelRequest body) {
        return inwoners.voegHoofddoelToe(huidigeBurger.bsn(request), planId, body.titel());
    }

    @PostMapping("/{planId}/hoofddoelen/{hoofddoelId}/subdoelen")
    @ResponseStatus(HttpStatus.CREATED)
    public Subdoel voegSubdoelToe(HttpServletRequest request, @PathVariable String planId,
                                  @PathVariable String hoofddoelId, @RequestBody TitelRequest body) {
        return inwoners.voegSubdoelToe(huidigeBurger.bsn(request), planId, hoofddoelId, body.titel());
    }

    @PostMapping("/{planId}/hoofddoelen/{hoofddoelId}/subdoelen/{subdoelId}/acties")
    @ResponseStatus(HttpStatus.CREATED)
    public Actie voegActieToe(HttpServletRequest request, @PathVariable String planId,
                              @PathVariable String hoofddoelId, @PathVariable String subdoelId,
                              @RequestBody ActieRequest body) {
        return inwoners.voegActieToe(huidigeBurger.bsn(request), planId, hoofddoelId, subdoelId,
                body.omschrijving(), body.type());
    }

    public record AfspraakRequest(@NotBlank String titel, LocalDate datum, String van, String tot,
                                  String locatie, String met) {
    }

    public record TitelRequest(@NotBlank String titel) {
    }

    public record ActieRequest(@NotBlank String omschrijving, String type) {
    }
}
