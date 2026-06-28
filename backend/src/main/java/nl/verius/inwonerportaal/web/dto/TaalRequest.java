package nl.verius.inwonerportaal.web.dto;

import jakarta.validation.constraints.NotBlank;

/** Verzoek om de standaard-voorkeurstaal van de ingelogde burger in te stellen. */
public record TaalRequest(@NotBlank String taal) {
}
