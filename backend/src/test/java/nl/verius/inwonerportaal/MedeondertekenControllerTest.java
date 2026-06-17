package nl.verius.inwonerportaal;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class MedeondertekenControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper json;

    /** Start, vul, en onderteken een aanvraag als initiator; geeft het aanvraag-id terug. */
    private String getekendeAanvraag() throws Exception {
        String body = mvc.perform(post("/api/aanvragen").param("type", "bijzondere-bijstand"))
                .andReturn().getResponse().getContentAsString();
        String id = json.readTree(body).get("id").asText();
        mvc.perform(put("/api/aanvragen/" + id + "/concept")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"soortKosten\":\"Woninginrichting\",\"kostenposten\":[{\"omschrijving\":\"Kast\",\"bedrag\":300}]}"))
                .andExpect(status().isOk());
        mvc.perform(post("/api/aanvragen/" + id + "/ondertekenen")).andExpect(status().isOk());
        return id;
    }

    private String nodigUit(String aanvraagId, String partnerBsn) throws Exception {
        String payload = partnerBsn == null
                ? "{\"partnerEmail\":\"partner@example.nl\"}"
                : "{\"partnerEmail\":\"partner@example.nl\",\"partnerBsn\":\"" + partnerBsn + "\"}";
        String body = mvc.perform(post("/api/aanvragen/" + aanvraagId + "/medeondertekenaar")
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("wacht-op-partner"))
                .andExpect(jsonPath("$.handtekeningen[0].bsn").value("999993653"))
                .andReturn().getResponse().getContentAsString();
        return json.readTree(body).get("token").asText();
    }

    @Test
    void partner_kan_uitnodiging_openen_en_ondertekenen() throws Exception {
        String token = nodigUit(getekendeAanvraag(), null);

        mvc.perform(get("/api/medeondertekenen/" + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.magTekenen").value(true))
                .andExpect(jsonPath("$.documentHash").exists())
                .andExpect(jsonPath("$.reedsGetekendDoor.length()").value(1));

        mvc.perform(post("/api/medeondertekenen/" + token + "/ondertekenen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Volledig ondertekend"));
    }

    @Test
    void bsn_binding_blokkeert_verkeerde_persoon() throws Exception {
        // Uitnodiging gebonden aan een ANDERE BSN dan de ingelogde demo-burger (999993653).
        String token = nodigUit(getekendeAanvraag(), "111111110");

        mvc.perform(get("/api/medeondertekenen/" + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.magTekenen").value(false));

        mvc.perform(post("/api/medeondertekenen/" + token + "/ondertekenen"))
                .andExpect(status().isConflict());
    }

    @Test
    void onbekende_token_geeft_410() throws Exception {
        mvc.perform(get("/api/medeondertekenen/bestaat-niet"))
                .andExpect(status().isGone());
    }
}
