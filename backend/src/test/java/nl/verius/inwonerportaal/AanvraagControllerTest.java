package nl.verius.inwonerportaal;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AanvraagControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper json;

    private String startAanvraag() throws Exception {
        String body = mvc.perform(post("/api/aanvragen").param("type", "bijzondere-bijstand"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("concept"))
                .andReturn().getResponse().getContentAsString();
        return json.readTree(body).get("id").asText();
    }

    @Test
    void definitie_bevat_velden_en_herhaalbare_groep() throws Exception {
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/aanvragen/definities/bijzondere-bijstand"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vragen[0].id").value("soortKosten"))
                .andExpect(jsonPath("$.vragen[2].type").value("groep"));
    }

    @Test
    void evaluatie_berekent_totaal_en_meldt_validatiefouten() throws Exception {
        String id = startAanvraag();
        String antwoorden = """
                {"soortKosten":"Anders","kostenposten":[{"omschrijving":"Bril","bedrag":120.50}]}
                """;
        String body = mvc.perform(post("/api/aanvragen/" + id + "/evaluatie")
                        .contentType(MediaType.APPLICATION_JSON).content(antwoorden))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.afgeleideWaarden.totaalBedrag").value(120.50))
                .andExpect(jsonPath("$.zichtbaarheid.toelichtingAnders").value(true))
                .andReturn().getResponse().getContentAsString();
        // 'Anders' zonder toelichting -> minstens één validatiefout, dus niet indienbaar.
        JsonNode node = json.readTree(body);
        org.junit.jupiter.api.Assertions.assertFalse(node.get("indienbaar").asBoolean());
    }

    @Test
    void volledige_aanvraag_kan_worden_ingediend_en_wordt_een_zaak() throws Exception {
        String id = startAanvraag();
        String antwoorden = """
                {"soortKosten":"Medische kosten","kostenposten":[{"omschrijving":"Bril","bedrag":120.50}]}
                """;
        mvc.perform(put("/api/aanvragen/" + id + "/concept")
                .contentType(MediaType.APPLICATION_JSON).content(antwoorden)).andExpect(status().isOk());

        mvc.perform(post("/api/aanvragen/" + id + "/indienen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zaaknummer").exists())
                .andExpect(jsonPath("$.status").value("Ontvangen"));
    }

    @Test
    void onvolledige_aanvraag_indienen_geeft_409() throws Exception {
        String id = startAanvraag();
        // geen kostenposten -> niet indienbaar
        mvc.perform(put("/api/aanvragen/" + id + "/concept")
                .contentType(MediaType.APPLICATION_JSON).content("{\"soortKosten\":\"Medische kosten\"}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/aanvragen/" + id + "/indienen"))
                .andExpect(status().isConflict());
    }

    @Test
    void bijlage_toevoegen_en_verwijderen() throws Exception {
        String id = startAanvraag();
        String body = mvc.perform(post("/api/aanvragen/" + id + "/bijlagen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"bestandsnaam\":\"factuur.pdf\",\"grootte\":2048}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.bestandsnaam").value("factuur.pdf"))
                .andReturn().getResponse().getContentAsString();
        String bijlageId = json.readTree(body).get("id").asText();

        mvc.perform(delete("/api/aanvragen/" + id + "/bijlagen/" + bijlageId))
                .andExpect(status().isNoContent());
    }

    @Test
    void ondertekenen_met_digid_levert_ondertekende_zaak() throws Exception {
        String id = startAanvraag();
        mvc.perform(put("/api/aanvragen/" + id + "/concept")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"soortKosten\":\"Woninginrichting\",\"kostenposten\":[{\"omschrijving\":\"Kast\",\"bedrag\":300}]}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/aanvragen/" + id + "/ondertekenen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Ontvangen (DigiD-ondertekend)"));
    }

    @Test
    void aanvraag_afbreken() throws Exception {
        String id = startAanvraag();
        mvc.perform(delete("/api/aanvragen/" + id)).andExpect(status().isNoContent());
        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders
                        .get("/api/aanvragen/" + id))
                .andExpect(status().isNotFound());
    }
}
