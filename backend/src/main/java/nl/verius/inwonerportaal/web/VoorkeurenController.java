package nl.verius.inwonerportaal.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import nl.verius.inwonerportaal.service.InwonerService;
import nl.verius.inwonerportaal.web.dto.TaalRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Voorkeuren van de ingelogde burger (BSN via {@link HuidigeBurger}).
 *
 * <p>De voorkeurstaal is een per-burger-instelling die met de burger meereist (server-side, niet
 * alleen in de browser), zodat de standaardtaal over apparaten heen geldt. Loopt via de ACL naar
 * X-Works (zie {@code XworksClient#getVoorkeurstaal}).
 */
@RestController
@RequestMapping("/api/voorkeuren")
public class VoorkeurenController {

    private final InwonerService inwoners;
    private final HuidigeBurger huidigeBurger;

    public VoorkeurenController(InwonerService inwoners, HuidigeBurger huidigeBurger) {
        this.inwoners = inwoners;
        this.huidigeBurger = huidigeBurger;
    }

    /** Huidige standaard-voorkeurstaal van de burger (default {@code nl}). */
    @GetMapping("/taal")
    public Map<String, Object> taal(HttpServletRequest request) {
        return Map.of(
                "taal", inwoners.voorkeurstaal(huidigeBurger.bsn(request)),
                "ondersteund", InwonerService.ONDERSTEUNDE_TALEN
        );
    }

    /** Stel de standaard-voorkeurstaal in (400 bij een niet-ondersteunde taal). */
    @PutMapping("/taal")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void zetTaal(HttpServletRequest request, @Valid @RequestBody TaalRequest body) {
        inwoners.zetVoorkeurstaal(huidigeBurger.bsn(request), body.taal());
    }
}
