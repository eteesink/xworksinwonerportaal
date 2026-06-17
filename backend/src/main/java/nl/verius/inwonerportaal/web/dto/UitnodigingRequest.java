package nl.verius.inwonerportaal.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Verzoek om een partner uit te nodigen voor mede-ondertekening.
 *
 * @param partnerBsn optioneel: bindt de uitnodiging aan deze BSN (aanrader voor beveiliging)
 */
public record UitnodigingRequest(
        @NotBlank(message = "E-mailadres van de partner is verplicht")
        @Email(message = "Ongeldig e-mailadres")
        String partnerEmail,
        String partnerBsn
) {}
