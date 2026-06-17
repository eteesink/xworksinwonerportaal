package nl.verius.inwonerportaal;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CatalogusTest {

    @Autowired
    MockMvc mvc;

    @Test
    void catalogus_levert_alle_vragenlijsten_dynamisch() throws Exception {
        mvc.perform(get("/api/aanvragen/catalogus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(4)))
                .andExpect(jsonPath("$[*].type", hasItem("schuldhulp")))
                .andExpect(jsonPath("$[*].type", hasItem("wmo-voorziening")))
                .andExpect(jsonPath("$[*].type", hasItem("kwijtschelding")));
    }

    @Test
    void generieke_evaluatie_werkt_voor_kwijtschelding() throws Exception {
        // Start een kwijtscheldings-aanvraag en evalueer met conditioneel-verplicht vermogenBedrag.
        String body = mvc.perform(post("/api/aanvragen").param("type", "kwijtschelding"))
                .andExpect(status().isCreated()).andReturn().getResponse().getContentAsString();
        String id = com.jayway.jsonpath.JsonPath.read(body, "$.id");

        // heeftVermogen=Ja -> vermogenBedrag zichtbaar en verplicht; leeg -> niet indienbaar.
        mvc.perform(post("/api/aanvragen/" + id + "/evaluatie")
                        .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                        .content("{\"belastingsoort\":\"Rioolheffing\",\"aanslagnummer\":\"A-1\",\"nettoMaandinkomen\":1200,\"heeftVermogen\":\"Ja\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.zichtbaarheid.vermogenBedrag").value(true))
                .andExpect(jsonPath("$.indienbaar").value(false));
    }
}
