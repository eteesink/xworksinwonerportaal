package nl.verius.inwonerportaal.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nl.verius.inwonerportaal.acl.model.Persoon;
import nl.verius.inwonerportaal.acl.model.Zaak;
import nl.verius.inwonerportaal.service.InwonerService;
import nl.verius.inwonerportaal.web.dto.ContactgegevensRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST-API voor inzage en persoonsmutaties van de ingelogde burger
 * (BSN bepaald door {@link HuidigeBurger}).
 */
@RestController
@RequestMapping("/api")
public class PortaalController {

    private final InwonerService inwoners;
    private final HuidigeBurger huidigeBurger;

    public PortaalController(InwonerService inwoners, HuidigeBurger huidigeBurger) {
        this.inwoners = inwoners;
        this.huidigeBurger = huidigeBurger;
    }

    /** Inzage: eigen persoonsgegevens (X-Works lsd-persoon). */
    @GetMapping("/persoon")
    public Persoon mijnGegevens(HttpServletRequest request) {
        return inwoners.mijnGegevens(huidigeBurger.bsn(request));
    }

    /** Inzage: eigen zaken/dossiers (X-Works lsd-zaken). */
    @GetMapping("/zaken")
    public List<Zaak> mijnZaken(HttpServletRequest request) {
        return inwoners.mijnZaken(huidigeBurger.bsn(request));
    }

    /** Mutatie: contactgegevens wijzigen (X-Works CONTACTGEGEVENS editable). */
    @PutMapping("/persoon/contactgegevens")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void wijzigContactgegevens(HttpServletRequest request,
                                      @Valid @RequestBody ContactgegevensRequest body) {
        inwoners.wijzigContactgegevens(huidigeBurger.bsn(request), body.toModel());
    }
}
