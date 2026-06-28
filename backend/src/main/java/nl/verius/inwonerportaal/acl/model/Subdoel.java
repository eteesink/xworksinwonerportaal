package nl.verius.inwonerportaal.acl.model;

import java.time.LocalDate;
import java.util.List;

/** Een subdoel onder een hoofddoel, met bijbehorende acties. */
public record Subdoel(String id, String titel, LocalDate aangemaaktOp, String aangemaaktDoor,
                      List<Actie> acties) {
}
