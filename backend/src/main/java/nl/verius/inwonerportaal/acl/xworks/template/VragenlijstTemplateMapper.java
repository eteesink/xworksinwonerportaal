package nl.verius.inwonerportaal.acl.xworks.template;

import nl.verius.inwonerportaal.acl.model.VraagDefinitie;
import nl.verius.inwonerportaal.acl.model.VragenlijstDefinitie;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Vertaalt de X-Works vragenlijst-template (zoals geleverd door {@code wiz-vltmpl00-getocc})
 * naar het portaal-model {@link VragenlijstDefinitie}.
 *
 * <p>Profiel-agnostisch en daarom herbruikbaar door zowel de stub (gesimuleerde respons) als de
 * echte {@code XworksSoapClient} (live getocc-respons). Hiermee blijft de template-definitie in
 * X-Works staan (per ADMINISTRATION/gemeente) en hoeft component 2 die niet te dupliceren.
 */
@Component
public class VragenlijstTemplateMapper {

    /** Parse + map een X-Works template-document uit een stream (bijv. de getocc-respons). */
    public VragenlijstDefinitie map(InputStream xworksTemplate) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // Hardening tegen XXE: geen externe entiteiten/DTD.
            dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            dbf.setExpandEntityReferences(false);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(xworksTemplate);
            return map(doc.getDocumentElement());
        } catch (Exception e) {
            throw new IllegalStateException("Kon X-Works vragenlijst-template niet verwerken: " + e.getMessage(), e);
        }
    }

    /** Map een reeds geparste {@code <vragenlijsttemplate>}-root naar het portaal-model. */
    public VragenlijstDefinitie map(Element root) {
        String type = root.getAttribute("type");
        String titel = root.getAttribute("titel");
        List<VraagDefinitie> vragen = new ArrayList<>();
        // Loop door pagina's (X-Works VRAGENLIJSTTEMPLATE_PAGINA), in documentvolgorde.
        for (Element pagina : kinderen(root, "pagina")) {
            for (Element vraag : kinderen(pagina, "vraag")) {
                vragen.add(mapVraag(vraag));
            }
        }
        return new VragenlijstDefinitie(type, titel, vragen);
    }

    private VraagDefinitie mapVraag(Element v) {
        String type = v.getAttribute("type");
        boolean verplicht = "true".equalsIgnoreCase(v.getAttribute("verplicht"));
        String zichtbaarAls = v.hasAttribute("zichtbaarAls") ? v.getAttribute("zichtbaarAls") : null;

        List<String> opties = null;
        if ("keuze".equals(type)) {
            opties = new ArrayList<>();
            for (Element o : kinderen(v, "optie")) {
                opties.add(o.getTextContent().trim());
            }
        }

        List<VraagDefinitie> subvragen = null;
        if ("groep".equals(type)) {
            subvragen = new ArrayList<>();
            for (Element sub : kinderen(v, "vraag")) {
                subvragen.add(mapVraag(sub));
            }
        }

        return new VraagDefinitie(
                v.getAttribute("id"),
                v.getAttribute("label"),
                type,
                verplicht,
                opties,
                zichtbaarAls,
                subvragen
        );
    }

    /** Directe kind-elementen met de gegeven naam (niet recursief). */
    private static List<Element> kinderen(Element parent, String naam) {
        List<Element> result = new ArrayList<>();
        NodeList kinderen = parent.getChildNodes();
        for (int i = 0; i < kinderen.getLength(); i++) {
            Node n = kinderen.item(i);
            if (n.getNodeType() == Node.ELEMENT_NODE && naam.equals(n.getNodeName())) {
                result.add((Element) n);
            }
        }
        return result;
    }
}
