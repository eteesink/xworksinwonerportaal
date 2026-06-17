package nl.verius.inwonerportaal;

import nl.verius.inwonerportaal.acl.stub.XworksClientStub;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PortaalControllerTest {

    @Autowired
    MockMvc mvc;

    @Test
    void persoon_van_demo_burger_wordt_geleverd() throws Exception {
        mvc.perform(get("/api/persoon"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bsn").value(XworksClientStub.DEMO_BSN))
                .andExpect(jsonPath("$.achternaam").value("de Vries"));
    }

    @Test
    void zaken_van_demo_burger_worden_geleverd() throws Exception {
        // Volgorde-onafhankelijk: ingediende aanvragen uit andere tests kunnen ook zaken toevoegen.
        mvc.perform(get("/api/zaken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].zaaknummer", org.hamcrest.Matchers.hasItem("Z-2025-0481")));
    }

    @Test
    void contactgegevens_kunnen_worden_gewijzigd() throws Exception {
        mvc.perform(put("/api/persoon/contactgegevens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"telefoon\":\"06-12345678\",\"email\":\"nieuw@example.nl\"}"))
                .andExpect(status().isNoContent());

        mvc.perform(get("/api/persoon"))
                .andExpect(jsonPath("$.contactgegevens.email").value("nieuw@example.nl"));
    }

    @Test
    void ongeldig_emailadres_geeft_400() throws Exception {
        mvc.perform(put("/api/persoon/contactgegevens")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"telefoon\":\"06-12345678\",\"email\":\"geen-email\"}"))
                .andExpect(status().isBadRequest());
    }
}
