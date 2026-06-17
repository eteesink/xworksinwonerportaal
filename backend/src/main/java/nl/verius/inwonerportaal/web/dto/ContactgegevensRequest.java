package nl.verius.inwonerportaal.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import nl.verius.inwonerportaal.acl.model.Contactgegevens;

/**
 * Verzoek om contactgegevens te wijzigen, met invoervalidatie.
 */
public record ContactgegevensRequest(

        @Size(max = 20, message = "Telefoonnummer is te lang")
        @Pattern(regexp = "^[0-9 +\\-()]*$", message = "Ongeldig telefoonnummer")
        String telefoon,

        @Email(message = "Ongeldig e-mailadres")
        @Size(max = 254, message = "E-mailadres is te lang")
        String email
) {
    public Contactgegevens toModel() {
        return new Contactgegevens(telefoon, email);
    }
}
