package nl.verius.inwonerportaal.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Bepaalt de BSN van de ingelogde burger.
 *
 * <p><b>Eerste opzet:</b> de BSN komt uit de header {@code X-Demo-Bsn} of valt terug op een
 * demo-BSN. Dit is een tijdelijke stand-in.
 *
 * <p><b>TODO (DigiD):</b> vervang dit door de BSN uit de DigiD/SAML-sessie. In X-Works levert de
 * Assertion Consumer Service ({@code samlp-acs-SAML-receive}) de {@code NameID}/BSN; in de moderne
 * stack komt die uit Spring Security SAML2 (of een Logius-broker/OIDC) en uit de sessie — nooit uit
 * een door de client meegestuurde header.
 */
@Component
public class HuidigeBurger {

    @Value("${inwonerportaal.demo-bsn:999993653}")
    private String demoBsn;

    public String bsn(HttpServletRequest request) {
        String header = request.getHeader("X-Demo-Bsn");
        return (header != null && !header.isBlank()) ? header.trim() : demoBsn;
    }
}
