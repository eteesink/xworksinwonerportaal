package nl.verius.inwonerportaal.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

/**
 * Autorisatie voor de beheerinterface.
 *
 * <p><b>Eerste opzet (POC):</b> beheerderschap wordt afgeleid uit de header {@code X-Demo-Rol}
 * (bevat "beheer"). Optioneel kan in een demo-omgeving alles worden opengezet via
 * {@code inwonerportaal.beheer.demo-open=true} — standaard <b>uit</b>, zodat burgers er niet bij kunnen.
 *
 * <p><b>TODO (productie):</b> vervang dit door een echte rol/claim uit Authentik/IdP (bv. een
 * groepsclaim in het OIDC-token of een door de gatekeeper geïnjecteerde, vertrouwde header).
 */
@Component
public class Beheerder {

    @Value("${inwonerportaal.beheer.demo-open:false}")
    private boolean demoOpen;

    public boolean isBeheerder(HttpServletRequest request) {
        if (demoOpen) {
            return true;
        }
        String rol = request.getHeader("X-Demo-Rol");
        return rol != null && rol.toLowerCase().contains("beheer");
    }

    /** Gooit 403 als de aanvrager geen beheerder is. */
    public void vereisBeheerder(HttpServletRequest request) {
        if (!isBeheerder(request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Alleen voor beheerders.");
        }
    }
}
