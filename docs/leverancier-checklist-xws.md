# Checklist / vragen aan de X-Works-leverancier — ontsluiting vragenlijst-template

> Doel: vaststellen of we de vragenlijst-template (per gemeente) en de bijbehorende schrijfacties
> uit X-Works kunnen ophalen/aanroepen vanuit een nieuw inwonerportaal (Spring BFF), bij voorkeur via
> de bestaande `xws` SOAP-laag, of anders via een nieuw te bouwen endpoint (optie C).
> Onderstaande termen komen uit de X-Works XSLT-resources (module `wiz`/`xws`).

## Context (kort, voor de leverancier)
We bouwen een moderne voorkant (React + Spring) die het X-Works "klantenportaal" vervangt. De koppeling
loopt via één anti-corruption layer; we schrijven **niet** rechtstreeks naar de Uniface-DB, maar willen
de bestaande X-Works-operaties/services aanroepen. We zien dat de vragenlijst-template als data bestaat
(`VRAGENLIJSTTEMPLATE → PAGINA → VRAAGBOOM → SVRAAG`, gekoppeld via `TAG_*`, per `ADMINISTRATION`), en dat
`wiz-vltmpl00-getocc.xslt` slechts de METRO-UI-rendering is. We willen het onderliggende data-document.

## A. xws-endpoint & WSDL
1. Wat is de **xws SOAP-endpoint-URL** per omgeving (dev / acc / prod)?
2. Is er een **WSDL** beschikbaar? Waar (URL of bestand in `resources/fil`)? Kunnen we die ontvangen?
3. Welke **operaties** publiceert de WSDL nu? (lijst, of de WSDL zelf)
4. Welke **authenticatie** geldt op het endpoint (mTLS, basic, API-key, IP-allowlist)?
5. Welke **namespace(s)** en berichtnaam-conventie worden gebruikt (we zien `http://wxs.company-x.nl`, `ZKN`)?

## B. Template-ophaal (lezen)
6. Zijn de **getocc-operaties** voor de template als **web-service/`USIOPER`** gepubliceerd, of alleen intern (XRIA)?
   Concreet: `vltmpl00-getocc`, `pagina00-getocc`, `vraagboom00-getocc`, `svraag00-getocc`.
7. Zo ja: kunnen we per **`ADMINISTRATION`** (gemeentecode, bijv. `opmr`) en per **template-type** het
   **"XML Form document"** (de `dataSet`/`occ`-structuur) ophalen? Graag een **voorbeeldrespons**.
8. **Veld-metadata** (deels al gevonden op de `VRAAG`-entiteit): bevestig svp:
   - de **domeinwaarden van `VRAAG.CD_WIDGETTYPE`** en hun betekenis (welke code = tekst/getal/bedrag/datum/keuze);
   - de **codelijst achter `VRAAG.CD_BRONANTWOORD`** (optie-waarden + labels) — en wanneer `VC_BRONANTWOORD` (inline) wordt gebruikt;
   - **waar `verplicht` wordt vastgelegd**: een tag op `PAGINA_ELEMENTEN`/`TAG_VRAAG` (`CD_TAG`/`VC_VALUE`/`JSON_VALUE`) of een proc-validatie?
9. Hoe is **volgorde** (we zien `NM_IDX` op `TAG_*`) en **geldigheid** (`DM_GELDIG_VAN`/`DM_GELDIG_TM`) bedoeld?
10. Is er een **catalogus**-bevraging (lijst beschikbare `VRAGENLIJSTTEMPLATE` per `ADMINISTRATION`)?
11. **Versie/wijziging**: kunnen we `MODIFIEDDATIM`/`VERSION` meekrijgen voor caching/invalidatie?

## C. Optie C — samengesteld template-endpoint (als B niet kan)
12. Kunnen jullie een **nieuwe operatie/endpoint** leveren die de **complete template-boom** in één call
    teruggeeft (root → pagina's → vraagboom → svraag, mét volgorde, geldigheid en veld-metadata),
    bij voorkeur als **JSON** (of XML), op iets als `GET /templates/{type}?administration={code}`?
13. Wat is daarvoor de **inschatting** (bouw + test) en op welke termijn?

## D. Schrijfacties (voor later, zelfde koppeling)
14. Zijn deze operaties ook als service aanroepbaar (of te ontsluiten)?
    - aanvraag opslaan/indienen: `vragenlijst-save`, `vragenlijst-submit` → `zak-saveWizard`
    - regelevaluatie: `vragenlijst-eval`
    - contactgegevens muteren (CONTACTGEGEVENS, `showTransactionOnInput`)
    - bijlagen: `dropzone-verwerk` / `removeAttachedFile` (en documentcreatie via StUF-DCR)
15. Hoe werkt het **transactie-/concurrencyprotocol** bij schrijven (`_id`/`_status`/`_crc`/`VERSION`)?

## E. Inzage (lezen, voor de overige portaalschermen)
16. Persoon (`PERSOON` + `ADRES`/`CONTACTGEGEVENS`/`BANKREKENING`) per BSN — via service of StUF-BG (`npsLv01`)?
17. Zaken/dossiers per BSN — via StUF-ZDS (`ZDS0120-beantwoordVraag`) of een eigen operatie?

## F. Randvoorwaarden
18. **Omgevingen & testdata**: een dev/acc-endpoint + een bekende template-`type` per `ADMINISTRATION` om tegen te testen.
19. **Autorisatie/scoping**: hoe borgt X-Works dat een burger alleen zijn eigen gegevens ziet (BSN→`PERSOON.NM_BSN`)?
20. **Contactpersoon** voor de technische afstemming (we zien `richardbollee` als SVN-author van de `wiz`/`xws`-resources).

## G. BSN-ontvangst & sessie-ontkoppeling (DigiD verhuist naar de BFF)
*Context: in de nieuwe opzet termineert DigiD in de BFF (via broker), niet meer in X-Works (`samlp`). De BSN is dan geen ambient sessiegegeven meer in Uniface, maar wordt expliciet door de (vertrouwde) BFF meegegeven. We zien dat `samlp-acs-SAML-receive.xslt` nu de `NameID` (BSN) → Uniface-parameterlijst → sessie/`ACCOUNT`-binding doet. Onderstaande vragen bepalen hoeveel Uniface-aanpassing nodig is en of het backwards-compatible kan.*
21. **Hoe bepalen de `_system-lsd…`-operaties nu de BSN?** Uit een **sessie-global** (gezet na `samlp`-ACS via `ACCOUNT`/`IDPACCOUNT`), of uit een parameter? Welke variabele/proc is dat (bijv. een `get-current-bsn`)?
22. **Is er één centrale plek** waar "de huidige BSN" wordt gelezen, of leest elke operatie dit zelf? (bepaalt of één seam volstaat).
23. **Kan een vertrouwde systeemaanroeper (de BFF) "namens BSN X" handelen** — d.w.z. die sessie-global/parameter vullen vanuit de call — zónder de `samlp`-route te raken? Liefst een **additieve** ingang (parallel), zodat het bestaande portaal ongewijzigd blijft.
24. **System-auth voor de act-as-BSN-ingang**: welke trust geldt (service-account / mTLS / token)? Kan het bestaande **`PIE.ACCESSTOKEN`**-patroon hiervoor dienen? X-Works mag een meegegeven BSN **alleen** van een geauthenticeerd systeem accepteren, nooit anoniem.
25. **StUF-route**: voor StUF/ZDS/BG-operaties zit de subject-BSN al in het bericht (`inp.bsn`/stuurgegevens). Klopt dat de inzage-data (persoon/zaken/documenten) via die operaties al per-BSN bevraagbaar is **zonder** sessie — en dus zonder Uniface-codewijziging (alleen system-auth)?
26. **Backwards compatibility**: blijven `samlp` + de sessiebinding volledig functioneel naast de nieuwe ingang? Is er een feature-flag/namespace-scheiding om de twee paden te isoleren?
27. **Inschatting**: als 21–24 een wrapper vergen — wat is de bouw-/testinschatting per session-gebonden `lsd`-operatie?

### G2. Voorkeursroute: BFF als lokale SAML-IdP (samlp identity-bridge)
*Idee: de BFF doet de DigiD-login (SP) en levert de identiteit door aan X-Works door zélf als IdP op te treden naar `samlp`. X-Works zet dan zijn sessie via de bestaande route; alleen configuratie wijzigt, geen code. We zien dat `samlp-AuthnRequest-send` configureerbare `Issuer`/`Destination`/`AssertionConsumerServiceURL`/`NameIDPolicy` heeft en `samlp-acs` `Issuer`/`NameID`/attributen generiek uitleest.*
28. **Configureerbare IdP**: kunnen we de **IdP/Issuer waar `samlp` op vertrouwt** vervangen door onze eigen IdP (de BFF/"Verius DigiD")? Welke config: IdP-**metadata**, **signing-certificaat**, `Destination`/SSO-URL, entityID? Per omgeving?
29. **Handtekeningvalidatie**: waar en hoe valideert X-Works de SAMLResponse/assertie-handtekening (welke library/trust-store), zodat we onze IdP-cert kunnen laten vertrouwen?
30. **NameID & attributen**: welk **NameID-format** en welke **attribuutnamen** verwacht `samlp-acs` exact (we zien `http://schemas.xmlsoap.org/ws/2005/05/identity/claims/…`)? Zodat onze assertie 1-op-1 op de bestaande account-binding past.
31. **SP- vs IdP-initiated**: `samlp` stuurt een AuthnRequest (SP-initiated). Accepteert de ACS ook **unsolicited/IdP-initiated** responses, of moeten we `InResponseTo` correct beantwoorden? Wat zijn de eisen aan `Recipient`/`Audience`/`NotOnOrAfter`?
32. **Gevolg voor DigiD-aansluiting**: bevestig dat X-Works na deze wijziging géén eigen DigiD/Logius-aansluiting meer nodig heeft (het vertrouwt alleen onze interne IdP), zodat de DigiD-aansluiting centraal bij de BFF/"Verius DigiD" ligt.
33. **Inschatting**: bouw-/testinschatting voor deze config-only route vs. de `act-as-BSN`-wrapper (sectie G).

---
*Antwoorden op A–B bepalen of we direct via `xws` kunnen (snelste), C is de fallback (zie `xworks-template-service.md` en `optie-c-template-endpoint.md`). Sectie G bepaalt de BSN-/sessie-aanpak (zie `wiki/synthesis/xworks-inwonerportaal-moderne-stack.md`).*
