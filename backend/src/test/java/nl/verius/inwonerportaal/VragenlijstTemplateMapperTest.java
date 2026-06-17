package nl.verius.inwonerportaal;

import nl.verius.inwonerportaal.acl.model.VraagDefinitie;
import nl.verius.inwonerportaal.acl.model.VragenlijstDefinitie;
import nl.verius.inwonerportaal.acl.xworks.template.VragenlijstTemplateMapper;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VragenlijstTemplateMapperTest {

    private final VragenlijstTemplateMapper mapper = new VragenlijstTemplateMapper();

    private static final String XML = """
            <vragenlijsttemplate type="test-type" titel="Testformulier">
              <pagina volgorde="1" naam="P1">
                <vraag id="keuzeVeld" label="Kies" type="keuze" verplicht="true">
                  <optie>A</optie><optie>B</optie>
                </vraag>
                <vraag id="toelichting" label="Toelichting" type="tekst" verplicht="false" zichtbaarAls="keuzeVeld==B"/>
                <vraag id="regels" label="Regels" type="groep" verplicht="true">
                  <vraag id="oms" label="Omschrijving" type="tekst" verplicht="true"/>
                  <vraag id="bedrag" label="Bedrag" type="bedrag" verplicht="true"/>
                </vraag>
              </pagina>
            </vragenlijsttemplate>
            """;

    @Test
    void mapt_xworks_template_naar_definitie() {
        VragenlijstDefinitie def = mapper.map(
                new ByteArrayInputStream(XML.getBytes(StandardCharsets.UTF_8)));

        assertEquals("test-type", def.type());
        assertEquals("Testformulier", def.titel());
        assertEquals(3, def.vragen().size());

        VraagDefinitie keuze = def.vragen().get(0);
        assertEquals("keuze", keuze.type());
        assertTrue(keuze.verplicht());
        assertEquals(2, keuze.opties().size());

        VraagDefinitie toelichting = def.vragen().get(1);
        assertEquals("keuzeVeld==B", toelichting.zichtbaarAls());

        VraagDefinitie groep = def.vragen().get(2);
        assertEquals("groep", groep.type());
        assertNotNull(groep.subvragen());
        assertEquals(2, groep.subvragen().size());
        assertEquals("bedrag", groep.subvragen().get(1).id());
    }
}
