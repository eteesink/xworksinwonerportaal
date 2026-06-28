# Baseline-beschrijving Nieuw Inwonerportaal

**Versie:** 1.0 — **Datum:** 25-06-2026 — **Status:** consolidatie na verwerking klantbevindingen (rondes 1–5)
**Doel:** Volledige functionele baseline (epic → feature → story) voor het nieuwe, schema-gedreven inwonerportaal. Deze versie is de geconsolideerde beschrijving: de oorspronkelijke handleiding-functionaliteit is aangevuld met de beoordeelde klantbevindingen en PvE-kaders.
**Bronnen:** Handleiding Klantenportaal X-Works v1.2 (BLINQT) als basis, aangevuld met bevindingen van Eemsdelta/Midden-Groningen, Westerkwartier, de WIZportaal-handleiding en de PvE-stukken van Opmeer/Koggenland. De volledige herkomst per wijziging staat in Bijlage C.
**Detailniveau:** Tot storyniveau, met toetsbare acceptatiecriteria per story. Taken volgen in een latere uitwerking.

---

## Leeswijzer

De beschrijving volgt de hoofdstructuur van het portaal en is daarna uitgebreid met functionaliteit uit de klantbevindingen en PvE-kaders:

- **Mijn overzicht** — de inwoner ziet (en wijzigt waar toegestaan) zijn eigen dossier: gegevens, zaken, voorzieningen, uitkeringen en documenten (Epics 2–6).
- **Zelf regelen** — de inwoner dient zelfstandig aanvragen in, vult ze aan en geeft akkoord (Epic 7).
- **Communicatie** — de inwoner wordt geïnformeerd en communiceert met de gemeente (Epic 9).
- **Inrichting / beheer** — functioneel beheerders bepalen wat zichtbaar en mogelijk is (Epic 8).
- **Optionele modules** — configureerbare functionaliteit uit een tweede systeem (Epic 10).

Aan elke story hangen concrete, toetsbare acceptatiecriteria (als opsomming onder de story). Veldnamen en beheerpaden zijn waar mogelijk overgenomen uit de bronhandleiding, zodat ze herleidbaar blijven. De kaderstellende (PvE-)eisen die over alle epics heen gelden staan in de sectie *Uitgangspunten*.

**Rollen**
- *Inwoner* — ingelogde burger (via DigiD; in de optionele WIZ-variant is DigiD configureerbaar, zie Feature 10.5).
- *Functioneel beheerder* — beheert de inrichting (welke zaken, statussen, documenttypen, producten en formulieren zichtbaar en mogelijk zijn).
- *Professional / behandelaar* — gemeentemedewerker die zaken behandelt, met de inwoner communiceert (Epic 9) en de burgerpagina kan inzien (Story 10.3.2).
- *Contactpersoon* — externe betrokkene (bijv. zorgverlener) met eigen, beperkte toegang via het contactpersonenportaal (Feature 10.4).
- *Partner / helper* — mede-ondertekenaar of ondersteuner bij een aanvraag (Feature 7.7).
- *Gemeente* — de organisatie als geheel, in stories gebruikt voor beleids-/AVG-keuzes.

---

## Uitgangspunten & kaderstellende eisen (PvE)
*Toegevoegd n.a.v. de Stuurgroepnotitie Opmeer–Koggenland (26-01-2026). Dit zijn harde, kaderstellende eisen uit het Programma van Eisen die over alle epics heen gelden. Ze bepalen niet alleen wélke functionaliteit er is, maar dat deze als één samenhangend, geïntegreerd portaal wordt opgeleverd. Conform deze baseline bouwen betekent: aan deze uitgangspunten voldoen.*

- **U1 — Eén integraal inwonersportaal (Eis 35).** Er is één inwonersportaal met DigiD-toegang waarin alle digitale dienstverlening over alle domeinen is geïntegreerd. Functionaliteit is niet versnipperd over meerdere losse portalen; de inwoner heeft één ingang en een samenhangende ervaring.
- **U2 — Volledige, integrale aanvraagmodule (Eis 35 & 58).** De inwoner kan via dit ene portaal alle aanvragen voor alle regelingen digitaal indienen (o.a. domein Inkomen: Bijzondere Bijstand, Minimabeleid, SMI), uitbreidbaar naar alle overige domeinen — niet beperkt tot één deelportaal of alleen binnen lopende aanvragen.
- **U3 — Geen dubbele registratie (Eis 60).** Processen zijn volledig geïntegreerd; gegevens worden eenmalig vastgelegd en hergebruikt, zonder dubbele registratie.
- **U4 — Off-premise en volledig geserviced (Eis 57).** Het self-service portaal wordt off-premise geleverd en volledig geserviced (SaaS-model).
- **U5 — Beveiliging & certificering (Eis 65).** Beveiliging en certificering zijn aantoonbaar op orde.
- **U6 — Digitaal akkoord op plannen (Eis 58).** De inwoner kan digitaal akkoord geven op plannen.
- **U7 — Toegankelijkheid (WCAG 2.1 AA / EN 301 549).** Het portaal voldoet aan WCAG 2.1 AA en aan EN 301 549, voor het volledige portaal inclusief de schermen achter de DigiD-login.

**Scope-afbakening.** Binnen scope vallen de domeinen Wmo, Jeugdwet en Participatiewet (bijzondere bijstand / minimaregelingen). **Leerlingenvervoer valt buiten scope.**

> **Relatie tot Epic 10 (WIZportaal-modules):** ook configureerbare modules uit een tweede systeem moeten binnen dit ene integrale portaal worden ontsloten (één ingang, één ervaring), conform U1 — niet als een afzonderlijk portaal naast het hoofdportaal.

---

# EPIC 1 — Toegang & Authenticatie

De inwoner krijgt veilig toegang tot zijn persoonlijke dossier en blijft beschermd via sessiebeheer.

## Feature 1.1 — Inloggen met DigiD
- **Story 1.1.1** — Als inwoner wil ik inloggen met DigiD, zodat ik veilig en herleidbaar toegang krijg tot mijn eigen dossier.
  - Het portaal is uitsluitend toegankelijk na succesvolle DigiD-authenticatie.
  - Na inloggen worden uitsluitend gegevens van de ingelogde inwoner (op basis van BSN) getoond.
  - Bij mislukte of afgebroken authenticatie krijgt de inwoner geen toegang tot dossiergegevens.

## Feature 1.2 — Sessiebeheer
- **Story 1.2.1** — Als inwoner wil ik dat mijn sessie automatisch wordt beëindigd na inactiviteit, zodat mijn gegevens beschermd zijn.
  - Er wordt een resterende sessietijd getoond ("… tot uw sessie automatisch wordt beëindigd").
  - Na verstrijken van de sessietijd wordt de inwoner automatisch afgemeld.
- **Story 1.2.2** — Als inwoner wil ik handmatig kunnen afmelden, zodat ik mijn sessie zelf kan afsluiten.
  - Er is op elke pagina een knop **Afmelden** beschikbaar.
  - Na afmelden zijn dossiergegevens niet meer toegankelijk zonder opnieuw in te loggen.

## Feature 1.3 — Portaalstructuur & navigatie
- **Story 1.3.1** — Als inwoner wil ik een vaste, herkenbare menustructuur, zodat ik mijn weg makkelijk vind.
  - Het portaal toont een hoofdmenu **Mijn overzicht** met submenu's: Persoonlijke gegevens, Lopende zaken, Wmo, Jeugdwet, Uitkering(en), Documenten.
  - Het portaal toont een hoofdmenu **Zelf regelen** met submenu: Aanvragen.
  - De gemeentenaam/huisstijl (logo, "Loket Sociaal Domein") is zichtbaar in de header.
  - Het portaal is responsive en geschikt voor desktop, tablet en smartphone. *(verfijning n.a.v. WIZportaal-handleiding)*
- **Story 1.3.2** — Als inwoner wil ik dat menu-items die voor mij niet van toepassing zijn passend worden afgehandeld, zodat ik geen lege of verwarrende schermen zie.
  - Modules zonder gegevens tonen een duidelijke lege staat (bijv. "Geen … gevonden") in plaats van een fout.
  - *(Aandachtspunt voor latere klantbevindingen: tonen/verbergen van lege modules.)*
- **Story 1.3.3** — Als gemeente wil ik het portaal kunnen aanpassen aan onze huisstijl, zodat het herkenbaar bij onze gemeente past. *(toegevoegd n.a.v. bevinding Westerkwartier #27)*
  - Naast logo en gemeentenaam zijn ook lettertype en kleurstelling instelbaar.

---

# EPIC 2 — Persoonlijke gegevens inzien

De inwoner ziet welke gegevens bij de gemeente bekend zijn.

## Feature 2.1 — Persoonsgegevens
- **Story 2.1.1** — Als inwoner wil ik mijn persoonsgegevens inzien, zodat ik kan controleren of ze kloppen.
  - Getoond worden minimaal: Naam, BSN, Geboortedatum, Verblijfsadres (straat + postcode/plaats).
  - De gegevens zijn read-only en herkenbaar afkomstig "zoals bij ons bekend".
- **Story 2.1.2** — Als inwoner wil ik mijn correspondentieadres zien als dat afwijkt, zodat ik weet waar post heen gaat. *(toegevoegd n.a.v. bevinding Westerkwartier #2)*
  - Indien een correspondentieadres bekend en actueel is, wordt dit ook getoond onder Persoonsgegevens.

## Feature 2.2 — Contactgegevens
- **Story 2.2.1** — Als inwoner wil ik mijn contactgegevens inzien, zodat ik weet welke contactinformatie de gemeente gebruikt.
  - Contactgegevens zijn bereikbaar via een eigen tabblad binnen Persoonlijke gegevens.
- **Story 2.2.2** — Als inwoner wil ik al mijn bekende telefoonnummers zien, zodat ik niet onterecht denk dat een nummer ontbreekt. *(toegevoegd n.a.v. bevinding Eemsdelta/MG #7)*
  - Alle bij de gemeente bekende nummers worden getoond (bijv. telefoonnummer, GSM/mobiel), niet alleen het primaire nummer.
  - Per nummer is het soort/type herkenbaar (vast, mobiel, e.d.).

## Feature 2.3 — Bankrekeningen
- **Story 2.3.1** — Als inwoner wil ik mijn bekende bankrekening(en) inzien, zodat ik kan controleren waarop betalingen plaatsvinden.
  - Bankrekeningen zijn bereikbaar via een eigen tabblad binnen Persoonlijke gegevens.

> Persoonlijke gegevens bestaat uit drie tabbladen: **Persoonsgegevens · Contactgegevens · Bankrekeningen**.

## Feature 2.4 — Gegevens wijzigen / doorgeven
*Toegevoegd n.a.v. bevinding Westerkwartier #1; verbreed n.a.v. acceptatiecriteria Opmeer/Koggenland (PvE 35/58 — wijzigingen doorgeven). Verbreedt het portaal van alleen inzage naar gecontroleerde mutatie.*

- **Story 2.4.1** — Als inwoner wil ik mijn e-mailadres en telefoonnummer zelf kunnen wijzigen, zodat mijn contactgegevens actueel blijven.
  - De inwoner kan e-mailadres en telefoonnummer wijzigen vanuit Contactgegevens.
  - De wijziging wordt geverifieerd via een verificatiecode (per e-mail of sms).
  - Na succesvolle verificatie wordt de wijziging (geautomatiseerd) verwerkt in X-Works.
  - *(Te bevestigen: wel/geen handmatige goedkeuringsstap; ticket #1757 — status nog te verifiëren.)*
- **Story 2.4.2** — Als inwoner wil ik mijn bankrekeningnummer (IBAN) kunnen doorgeven/wijzigen, zodat betalingen op het juiste rekeningnummer plaatsvinden. *(n.a.v. acceptatiecriteria O/K)*
  - De inwoner kan een gewijzigd IBAN doorgeven vanuit Bankrekeningen.
  - De wijziging doorloopt een passende controle/verificatie voordat deze in X-Works wordt verwerkt.
- **Story 2.4.3** — Als inwoner wil ik wijzigingen in mijn inkomen kunnen doorgeven, zodat mijn dossier actueel blijft. *(n.a.v. acceptatiecriteria O/K)*
  - De inwoner kan een inkomenswijziging digitaal doorgeven.
  - De doorgegeven wijziging leidt tot de juiste verwerking/processtart in de backoffice (bijv. mutatie/heronderzoek), zonder dubbele registratie.

---

# EPIC 3 — Lopende zaken

De inwoner volgt de voortgang van zijn zaken bij de gemeente.

## Feature 3.1 — Overzicht lopende zaken
- **Story 3.1.1** — Als inwoner wil ik een overzicht van mijn lopende zaken, zodat ik de status van mijn aanvragen kan volgen.
  - Per zaak worden getoond: Zaaknummer / Onderwerp, Ontvangen (datum), Status.
  - Het tabblad toont het aantal lopende zaken (bijv. "Overzicht (17)").
  - Alleen zaken van zaaktype-processen die door beheer zijn gepubliceerd worden getoond (zie Epic 8).
  - Het openen van een zaak vanuit het overzicht presteert acceptabel (geen merkbare wachttijd). *(n.a.v. bevinding Westerkwartier #24, ticket #4144)*

## Feature 3.2 — Afgehandelde zaken
- **Story 3.2.1** — Als inwoner wil ik mijn afgehandelde zaken kunnen terugzien, zodat ik mijn historie kan raadplegen.
  - Afgehandelde zaken staan onder een apart tabblad **Afgehandeld**, met telling.

## Feature 3.3 — Zaakstatus weergave
- **Story 3.3.1** — Als inwoner wil ik per zaak een begrijpelijke status zien, zodat ik weet waar mijn zaak staat.
  - De getoonde status is de meest recente status die door beheer is gepubliceerd ("Publiceren op digitaal loket").
  - De status wordt getoond met de generieke (publieksvriendelijke) omschrijving, niet de interne code.
  - De status wordt per zaak correct getoond, óók wanneer de inwoner meerdere zaken van hetzelfde zaaktype heeft. *(n.a.v. bevinding Westerkwartier #5)*

## Feature 3.4 — Besluiten inzien
*Toegevoegd n.a.v. acceptatiecriteria Opmeer/Koggenland (besluit A2).*

- **Story 3.4.1** — Als inwoner wil ik het besluit op mijn aanvraag/zaak kunnen inzien, zodat ik weet wat er is beslist en waarom.
  - Bij een zaak met een genomen besluit is het besluit voor de inwoner in te zien.
  - Het besluit is gekoppeld aan de betreffende zaak en (waar van toepassing) als document beschikbaar (zie Epic 6).

---

# EPIC 4 — Voorzieningen (Wmo & Jeugdwet)

De inwoner ziet zijn toegekende voorzieningen, zowel actueel als historisch.

## Feature 4.1 — Wmo-voorzieningen
- **Story 4.1.1** — Als inwoner wil ik mijn actuele Wmo-voorzieningen inzien, zodat ik weet welke ondersteuning loopt.
  - Per voorziening worden getoond: Omschrijving, Zorgleverancier, Datum vanaf, Datum t/m.
  - Het tabblad **Actueel** toont het aantal lopende voorzieningen.
- **Story 4.1.2** — Als inwoner wil ik mijn historische Wmo-voorzieningen inzien, zodat ik eerdere ondersteuning kan terugzien.
  - Historische voorzieningen staan onder tabblad **Historisch**, met telling.
  - De getoonde omschrijving is de generieke productomschrijving (zie Epic 8).

## Feature 4.2 — Jeugdwet-voorzieningen
- **Story 4.2.1** — Als inwoner wil ik mijn actuele Jeugdwet-voorzieningen inzien, zodat ik weet welke jeugdhulp loopt.
  - Per voorziening worden getoond: Omschrijving, Zorgleverancier, Datum vanaf, Datum t/m.
  - Tabblad **Actueel** met telling.
- **Story 4.2.2** — Als inwoner wil ik mijn historische Jeugdwet-voorzieningen inzien, zodat ik eerdere jeugdhulp kan terugzien.
  - Historische voorzieningen onder tabblad **Historisch**, met telling.

---

# EPIC 5 — Uitkeringen & vorderingen

De inwoner ziet zijn uitkeringen en bijbehorende specificaties, en zijn eventuele vorderingen.

## Feature 5.1 — Uitkeringenoverzicht
- **Story 5.1.1** — Als inwoner wil ik mijn actuele uitkering(en) inzien, zodat ik weet welke uitkeringen lopen.
  - Per uitkering worden getoond: Soort uitkering, Uitkeringsnummer, Datum vanaf, Datum t/m.
  - Tabblad **Actueel** met telling.
- **Story 5.1.2** — Als inwoner wil ik mijn historische uitkeringen inzien, zodat ik mijn uitkeringshistorie kan raadplegen.
  - Historische uitkeringen onder tabblad **Historisch**, met telling.

## Feature 5.2 — Uitkeringsspecificaties
- **Story 5.2.1** — Als inwoner wil ik mijn uitkeringsspecificaties kunnen raadplegen, zodat ik inzicht heb in mijn betalingen.
  - Uitkeringsspecificaties zijn vindbaar onder Documenten (zie Epic 6) en herkenbaar als documenttype "Uitkeringsspecificatie".

## Feature 5.3 — Vorderingen inzien
*Toegevoegd n.a.v. acceptatiecriteria Opmeer/Koggenland (besluit A2).*

- **Story 5.3.1** — Als inwoner wil ik mijn vorderingen kunnen inzien, zodat ik weet welke bedragen openstaan of worden teruggevorderd.
  - Per vordering worden minimaal getoond: omschrijving/soort, bedrag, datum en (waar van toepassing) de gerelateerde zaak/regeling.
  - Waar van toepassing is onderscheid zichtbaar tussen openstaand en afgehandeld.

---

# EPIC 6 — Documenten

De inwoner raadpleegt en downloadt zijn documenten.

## Feature 6.1 — Documentenoverzicht & download
- **Story 6.1.1** — Als inwoner wil ik mijn documenten inzien, zodat ik mijn correspondentie en specificaties bij de hand heb.
  - Per document worden getoond: Documentnr, Omschrijving, Documenttype.
  - Documenten zijn gegroepeerd per documenttype (bijv. tabblad "Uitkeringsspecificaties (1)") met telling.
  - Alleen documenten van documenttypen die door beheer zijn gepubliceerd worden getoond (zie Epic 8).
- **Story 6.1.2** — Als inwoner wil ik een document kunnen downloaden, zodat ik het kan bewaren of delen.
  - Per document is een downloadactie beschikbaar.

---

# EPIC 7 — Zelf regelen (Aanvragen)

De inwoner regelt zaken zelfstandig: aanvragen starten en vervolgen via webformulieren, aanvragen aanvullen en herstellen, en digitaal akkoord geven op plannen.

## Feature 7.1 — Aanvraag starten
- **Story 7.1.1** — Als inwoner wil ik zelfstandig een nieuwe aanvraag starten, zodat ik zonder tussenkomst een verzoek kan indienen.
  - Onder **Zelf regelen > Aanvragen** is de actie **Aanvraag starten** beschikbaar.
  - De aanvraag verloopt via een webformulier (ingericht conform de Freshdesk-handleiding "Webformulieren", zie Epic 8).
  - De ingediende aanvraag verschijnt als zaak onder Lopende zaken (mits het zaaktype-proces is gepubliceerd).
- **Story 7.1.2** — Als inwoner wil ik mijn aanvraag met één duidelijke actie indienen, zodat ik niet twijfel of het gelukt is. *(n.a.v. bevinding Westerkwartier #20, ticket #4142)*
  - "Indienen" rondt de aanvraag definitief af; er is geen verwarrende extra stap ("opslaan en afsluiten") nodig na het indienen.
  - Na indienen krijgt de inwoner een duidelijke bevestiging dat de aanvraag is verstuurd.
- **Story 7.1.3** — Als inwoner wil ik een specifiek aanvraagformulier rechtstreeks vanaf de gemeentewebsite kunnen openen, zodat ik niet eerst in het portaal hoef te zoeken. *(n.a.v. bevinding Westerkwartier #26, ticket #4238)*
  - Een aanvraagformulier is via een directe link (deep link) vanaf de website te starten.
- **Story 7.1.4** — Als inwoner wil ik alle aanvragen voor alle regelingen via dit ene portaal kunnen indienen, zodat ik niet hoef te wisselen tussen portalen. *(n.a.v. PvE-notitie Opmeer/Koggenland — Eis 35 & 58, uitgangspunt U2)*
  - Alle regelingen zijn digitaal indienbaar via één portaal (o.a. Bijzondere Bijstand, Minimabeleid, SMI), uitbreidbaar naar alle domeinen.
  - De aanvraagfunctionaliteit is niet beperkt tot één deelportaal of alleen beschikbaar binnen reeds lopende aanvragen.

## Feature 7.2 — Vervolg aanvraag
- **Story 7.2.1** — Als inwoner wil ik een eerder gestarte aanvraag vervolgen, zodat ik een onderbroken of vervolgstap kan afronden.
  - Onder **Zelf regelen > Aanvragen** is de actie **Vervolg aanvraag** beschikbaar.
  - Te vervolgen aanvragen worden in een logische, chronologische volgorde getoond. *(n.a.v. bevinding Westerkwartier #21, ticket #4143)*

## Feature 7.3 — Slimme formulierlogica
*Toegevoegd n.a.v. bevindingen Eemsdelta/MG. Beschrijft het gewenste gedrag van de webformulier-engine voor de nieuwe bouw, ongeacht of het nu een bug of een wens is.*

- **Story 7.3.1** — Als inwoner wil ik alleen de vragen en teksten zien die voor mijn situatie relevant zijn, zodat het formulier overzichtelijk blijft. *(bevinding Eemsdelta #3 / #11; Westerkwartier #18 (#4141))*
  - Vragen kunnen conditioneel getoond/verborgen worden op basis van eerder ingevulde waarden.
  - Tekstvragen én tekstfragmenten kunnen óók aan condities gekoppeld worden (gelijk aan reguliere vragen).
  - Condities kunnen op een groep pagina's tegelijk worden toegepast, zodat een route niet pagina-voor-pagina hoeft te worden ingericht. *(n.a.v. bevinding Westerkwartier #15, ticket #4136)*
- **Story 7.3.2** — Als inwoner wil ik dat bekende gegevens automatisch zijn ingevuld, zodat ik niet alles opnieuw hoef in te voeren. *(bevinding Eemsdelta #4 / #8 / #10)*
  - Velden worden automatisch gevuld met gegevens uit X-Works (mits de data-query's aan het zaaktype-proces zijn gekoppeld).
  - Condities mogen werken op basis van opgehaalde persoonsgegevens (bijv. controle of de inwoner in de gemeente woont).
- **Story 7.3.3** — Als beheerder/inwoner wil ik dat tekstblokken klikbare links en correct opgemaakte tekst tonen, zodat verwijzingen bruikbaar zijn. *(bevinding Eemsdelta #2 / #1)*
  - In tekstblokken kunnen klikbare URL's worden opgenomen (bijv. via een hyperlink die in een nieuw tabblad opent).
  - Speciale tekens en diakrieten (bijv. É, ë, ï) worden correct weergegeven, zowel in invoer als in labels.
- **Story 7.3.4** — Als inwoner wil ik toelichting bij een vraag kunnen opvragen, zodat ik begrijp wat gevraagd wordt. *(bevinding Eemsdelta #9; Westerkwartier #13 – info-icoon #3688)*
  - Het informatie-/toelichtingsicoon ("i") werkt in productie (bij hoveren/klikken), niet alleen in de testomgeving.
- **Story 7.3.5** — Als beheerder wil ik passende invoervelden kunnen kiezen, zodat het formulier aansluit op de hoeveelheid gevraagde informatie. *(n.a.v. bevindingen Westerkwartier #8 (#4007), #9, #23)*
  - Er is naast een groot tekstvak ook een enkelvoudige **tekstregel** beschikbaar.
  - Tekstvakken passen zich aan / zijn uitbreidbaar zodat ingevoerde tekst volledig zichtbaar blijft (letters vallen niet weg).
- **Story 7.3.6** — Als inwoner wil ik dat mijn ingevulde antwoorden direct en betrouwbaar worden vastgelegd, zodat ik geen gegevens verlies. *(n.a.v. bevinding Westerkwartier #12, ticket #4138)*
  - Een ingevuld/geselecteerd antwoord wordt vastgelegd zonder dat eerst elders geklikt hoeft te worden; er treedt geen dataverlies of storende 'notify'-melding op.

## Feature 7.4 — Navigatie & validatie in het aanvraagformulier
*Toegevoegd n.a.v. bevindingen Eemsdelta/MG.*

- **Story 7.4.1** — Als inwoner wil ik in het formulier terug kunnen naar een vorige stap, zodat ik antwoorden kan corrigeren. *(bevinding Eemsdelta #13)*
  - Er is een duidelijke **Vorige**-knop, naast navigatie via de pagina-overzicht/blokken.
- **Story 7.4.2** — Als inwoner wil ik mijn aanvraag op elk moment kunnen afbreken, zodat ik niet vastloop. *(bevinding Eemsdelta #12)*
  - De optie **Afbreken** is op elke pagina zichtbaar, inclusief de laatste pagina.
- **Story 7.4.3** — Als beheerder wil ik de verplicht-markering van een vraag kunnen aan- én uitzetten, zodat ik fouten in de inrichting kan herstellen. *(bevinding Eemsdelta #14; Westerkwartier #10 (#4009))*
  - Het verplicht maken van een vraag is omkeerbaar.
- **Story 7.4.4** — Als beheerder wil ik navigaties binnen een vragenlijsttemplate kunnen verwijderen, zodat ik de routing kan corrigeren. *(n.a.v. bevinding Westerkwartier #11, ticket #4009)*
  - Een toegevoegde navigatie/route in een vragenlijsttemplate is verwijderbaar.
- **Story 7.4.5** — Als inwoner wil ik dat het verplicht-teken (*) op een logische plek staat, zodat het formulier leesbaar blijft. *(n.a.v. bevinding Westerkwartier #17, ticket #4140)*
  - Het verplicht-teken staat consistent en logisch bij de betreffende vraag (niet losgekoppeld onderaan een zin).

## Feature 7.5 — Bevestiging & notificatie van een aanvraag
*Toegevoegd n.a.v. bevinding Eemsdelta/MG #5.*

- **Story 7.5.1** — Als inwoner wil ik na het indienen een bevestiging met samenvatting ontvangen, zodat ik weet dat mijn aanvraag is ontvangen.
  - Na indienen wordt een bevestiging met samenvatting van de aanvraag verstuurd.
  - Beveiligd mailen wordt ondersteund (via de daarvoor bestemde module).
- **Story 7.5.2** — Als inwoner wil ik duidelijkheid over de bevestiging wanneer mijn e-mailadres niet bekend is, zodat ik niet ten onrechte op een bevestiging wacht.
  - Is er geen e-mailadres bekend, dan wordt geen bevestiging verstuurd en wordt de inwoner hier vooraf op gewezen (bij de afsluiting wordt aangegeven dat een e-mailadres nodig is voor de bevestiging).

## Feature 7.6 — Privacy bij niet-voltooide aanvragen (AVG)
*Toegevoegd n.a.v. bevinding Eemsdelta/MG #6.*

- **Story 7.6.1** — Als gemeente wil ik dat een persoon niet onterecht in het systeem blijft staan na een niet-afgeronde aanvraag, zodat we AVG-conform werken.
  - Een aanvraag wordt pas een zaak op het moment van indienen.
  - Persoonsgegevens die alleen door een gestarte, niet-ingediende aanvraag zijn aangemaakt worden **geautomatiseerd** opgeschoond conform bewaarbeleid (archiefwet-traject).
  - Elke opschoning wordt vastgelegd met passende **audit-logging** (wat, wanneer, op welke grond), zodat de verwerking aantoonbaar en herleidbaar is.
- **Story 7.6.2** — Als gemeente wil ik dat gegevens van een inwoner die niet in onze gemeente woont worden opgeschoond, zodat we geen onnodige gegevens bewaren. *(n.a.v. bevinding Westerkwartier #25; besluit C4)*
  - Wanneer na DigiD-inlog blijkt dat de inwoner niet in de gemeente woont en er verder niets bij die persoon bekend is in X-Works, worden de aangemaakte gegevens **geautomatiseerd** opgeschoond.
  - Ook deze opschoning wordt vastgelegd met passende audit-logging (wat, wanneer, op welke grond).

## Feature 7.7 — Mede-ondertekening & helpers
*Toegevoegd n.a.v. bevinding Westerkwartier #16 (#4139); uitgebreid n.a.v. PvE-notitie Opmeer/Koggenland (NvI — self-service).*

- **Story 7.7.1** — Als partner van de aanvrager wil ik een aanvraag betrouwbaar kunnen mede-ondertekenen ná eigen DigiD-inlog, zodat de medeondertekening rechtsgeldig en herleidbaar is. *(harde eis, besluit C3)*
  - **De partner logt verplicht apart in via DigiD** voordat hij/zij kan mede-ondertekenen; ondertekenen zonder eigen DigiD-inlog is niet toegestaan.
  - De mede-ondertekening is herleidbaar tot de DigiD-geïdentificeerde partner.
  - Na ondertekenen keert de gebruiker terug op de juiste plek (niet de beginpagina).
- **Story 7.7.2** — Als inwoner wil ik een 'helper' kunnen betrekken bij mijn aanvraag, zodat iemand mij kan ondersteunen bij het invullen/indienen. *(n.a.v. PvE-notitie — NvI self-service)*
  - De inwoner kan een helper betrekken bij het doorlopen van een aanvraag.

## Feature 7.8 — Overzichtelijke samenvatting
*Toegevoegd n.a.v. bevinding Westerkwartier #19.*

- **Story 7.8.1** — Als inwoner wil ik vóór het indienen een overzichtelijke samenvatting zien, zodat ik mijn antwoorden makkelijk kan controleren.
  - De samenvatting toont vraag en antwoord duidelijk gecombineerd en compact (niet onnodig lang of losgekoppeld).

## Feature 7.9 — Akkoord geven op plannen
*Toegevoegd n.a.v. PvE-notitie Opmeer/Koggenland (Eis 58, uitgangspunt U6).*

- **Story 7.9.1** — Als inwoner wil ik digitaal akkoord kunnen geven op een (ondersteunings)plan, zodat ik niet op papier hoef te tekenen.
  - De inwoner kan een plan inzien en daar digitaal akkoord op geven.
  - Het gegeven akkoord wordt vastgelegd en is herleidbaar bij de betreffende zaak/het plan.
  - *(Relatie: sluit aan op het Integraal Plan, Feature 10.2, wanneer die module actief is.)*

## Feature 7.10 — Digitaal herstel van onvolledige aanvragen (hersteltermijnen)
*Toegevoegd n.a.v. acceptatiecriteria Opmeer/Koggenland (PvE 35/58).*

- **Story 7.10.1** — Als inwoner wil ik een onvolledige aanvraag digitaal kunnen aanvullen binnen de hersteltermijn, zodat mijn aanvraag in behandeling kan blijven zonder papieren traject.
  - Bij een onvolledige aanvraag wordt de inwoner geïnformeerd over wat ontbreekt en binnen welke hersteltermijn dit aangevuld moet worden.
  - De inwoner kan de ontbrekende gegevens en/of documenten digitaal aanleveren bij de bestaande aanvraag/zaak (zie Feature 9.2).
  - De resterende hersteltermijn is voor de inwoner zichtbaar.
  - Na (tijdige) aanvulling loopt de behandeling van de aanvraag door; het herstel wordt vastgelegd bij de zaak.

---

# EPIC 8 — Inrichting & Beheer (functioneel beheerder)

Functioneel beheerders bepalen welke onderdelen uit X-Works zichtbaar zijn in het portaal. Dit is randvoorwaardelijk voor Epics 3 t/m 7.

> **Niet-functionele kaders (PvE, zie Uitgangspunten):** de oplossing voldoet aantoonbaar aan: geen dubbele registratie / volledig geïntegreerde processen (U3/Eis 60), off-premise en volledig geserviced levering (U4/Eis 57), en aantoonbare beveiliging & certificering (U5/Eis 65).

## Feature 8.1 — Zaaktype-processen publiceren
- **Story 8.1.1** — Als functioneel beheerder wil ik per zaaktype-proces instellen of het zichtbaar is in het portaal, zodat de juiste zaken onder Lopende zaken verschijnen.
  - Via **Beheer > Zaaksysteem > Zaaktype processen (RC)** wordt bij **Omschrijving generiek** de publieksvriendelijke zaaknaam ingevuld.
  - Per zichtbaar proces wordt de rol **[_system] via DigiD ingelogde inwoner** gekoppeld onder **Gekoppelde rollen / teams**.
  - Bij elk gepubliceerd proces moeten de bijbehorende statussen óók gepubliceerd zijn (zie Feature 8.2).

## Feature 8.2 — Zaakstatussen publiceren & inrichten
- **Story 8.2.1** — Als functioneel beheerder wil ik bestaande zaakstatussen publiceren op het portaal, zodat de inwoner een begrijpelijke status ziet.
  - Via **Beheer > Zaaksysteem > Zaakstatussen** wordt bij **Omschrijving generiek** de publieksvriendelijke status ingevuld en **Publiceren op digitaal loket** aangevinkt.
  - De zaak toont in het portaal de meest recente status waarbij dit vinkje aanstaat.
- **Story 8.2.2** — Als functioneel beheerder wil ik nieuwe zaakstatussen kunnen toevoegen en koppelen, zodat ik statussen kan tonen die nog niet bestaan.
  - Nieuwe status toevoegen, publiceren als processtap, koppelen aan het zaaktype-proces (Gekoppelde rule definities) en aan het zaaktype (Gekoppelde status(sen)).

## Feature 8.3 — Documenttypen publiceren
- **Story 8.3.1** — Als functioneel beheerder wil ik per documenttype instellen of documenten zichtbaar zijn in het portaal, zodat de juiste documenten getoond worden.
  - Via **Beheer > Zaaksysteem > Documenttypen** wordt bij **Omschrijving generiek** de publieksvriendelijke naam ingevuld en **Publiceren op digitaal loket** aangevinkt.
- **Story 8.3.2** — Als functioneel beheerder wil ik nieuwe documenttypen kunnen toevoegen en koppelen, zodat documenten van nieuwe aanvragen getoond kunnen worden.
  - Nieuw documenttype toevoegen, koppelen aan zaaktype-proces, zaaktype (Gekoppelde documenttype(n)) en waar nodig aan documentsjablonen.

## Feature 8.4 — Producten / omschrijvingen inrichten
- **Story 8.4.1** — Als functioneel beheerder wil ik de publieksomschrijving van producten instellen, zodat Wmo-, Jeugdwet- en uitkeringsregels begrijpelijk worden weergegeven.
  - Via **Beheer > Producten** wordt bij **Omschrijving generiek** de portaal-omschrijving ingevuld per product (kostensoort/verstrekking).

## Feature 8.5 — Aanvragen / webformulieren inrichten
- **Story 8.5.1** — Als functioneel beheerder wil ik webformulieren inrichten, zodat inwoners zelfstandig aanvragen kunnen indienen.
  - Inrichting verloopt conform de handleiding "Webformulieren" (Freshdesk).
- **Story 8.5.2** — Als functioneel beheerder wil ik meerdere webformulieren/vragenlijsten kunnen koppelen aan één zaaktype-proces, zodat één zaaktype via verschillende formulieren gestart kan worden. *(n.a.v. bevinding Westerkwartier #3)*
  - Eén zaaktype-proces kan meerdere processtappen "Vragenlijst" / webformulieren bevatten.
- **Story 8.5.3** — Als functioneel beheerder wil ik weten hoe digitale en fysieke aanvragen zich verhouden en of meerdere (digitale) aanvragen aan één zaak gekoppeld kunnen worden, zodat ik dubbele inrichting voorkom. *(n.a.v. bevinding Westerkwartier #22 — informatieverzoek/ontwerpkeuze)*
  - Ontwerpkeuze vast te leggen: kunnen meerdere digitale aanvragen (bijv. verschillende aanvragen bijzondere bijstand) in één proces/zaak gecombineerd worden, of is gescheiden inrichting nodig.
- **Story 8.5.4** — Als systeem wil ik de vragenlijst-/webformulier-definitie dynamisch uit X-Works ophalen, zodat het portaal formulieren toont vanuit één bron zonder eigen kopie en het beheer in X-Works blijft. *(architectuur-enabler, toegevoegd 26-06-2026; realiseert het dynamische-formulier-uitgangspunt van de POC)*
  - Het portaal haalt de catalogus van beschikbare vragenlijsten op uit X-Works (query over `VRAGENLIJSTTEMPLATE` per `ADMINISTRATION`).
  - Het portaal haalt per formulier de volledige definitie (vragen, veldtypen/widgets, opties, condities, routing) op uit X-Works (`wiz-vltmpl00-getocc` → `VRAGENLIJSTTEMPLATE.SC_XMLDATA`).
  - Het portaal houdt **geen eigen kopie** van de template aan; een wijziging in de inrichting (Story 8.5.1) is na ophalen direct zichtbaar voor de inwoner.
  - Veldtypen, verplicht-markering en bronantwoorden/opties komen uit de metadata (o.a. `CD_WIDGETTYPE`, `CD_BRONANTWOORD`/`VC_BRONANTWOORD`), zodat de inwoner-rendering (Feature 7.3) en de auto-vulling (Story 7.3.2) hierop kunnen steunen.
  - Bij een onbekend/niet-gepubliceerd formulier of een mislukte ophaalactie krijgt de inwoner een nette foutmelding, geen halve render.

---

# EPIC 9 — Notificaties & tweerichtingscommunicatie
*Toegevoegd n.a.v. bevindingen Westerkwartier #4 en #28 (sluit aan op Feature 7.5).*

De inwoner wordt actief geïnformeerd over de voortgang van zijn zaak en kan documenten uitwisselen met de gemeente.

## Feature 9.1 — Notificaties bij voortgang
- **Story 9.1.1** — Als inwoner wil ik een notificatie ontvangen bij een statuswijziging of genomen besluit, zodat ik op de hoogte blijf zonder zelf te hoeven controleren.
  - Bij een statuswijziging of besluit ontvangt de inwoner een notificatie (kanaal in te richten: portaalnotificatie, e-mail en/of sms).
  - Notificaties houden rekening met beveiligd mailen waar nodig (zie Feature 7.5).
- **Story 9.1.2** — Als gemeente wil ik de inwoner kunnen verzoeken aanvullende stukken aan te leveren, zodat de behandeling kan doorlopen.
  - De gemeente kan een verzoek om aanvullende stukken naar de inwoner sturen.

## Feature 9.2 — Documenten aanleveren bij een bestaande zaak
- **Story 9.2.1** — Als inwoner wil ik aanvullende bewijsstukken kunnen toevoegen aan een bestaande aanvraag/zaak, zodat ik hiervoor geen nieuwe zaak hoef te starten. *(n.a.v. bevinding Westerkwartier #28)*
  - De inwoner kan documenten uploaden bij een lopende zaak.
  - Aangeleverde stukken komen bij de bestaande zaak terecht (niet als losse nieuwe zaak), zodat het voor inwoner én medewerker efficiënt en overzichtelijk blijft.

## Feature 9.3 — Tweerichtingsberichten met de professional
*Toegevoegd n.a.v. PvE-notitie Opmeer/Koggenland (NvI — self-service).*

- **Story 9.3.1** — Als inwoner wil ik berichten kunnen sturen naar en ontvangen van de gemeenteprofessional, zodat ik direct in het portaal kan communiceren over mijn aanvraag/zaak.
  - De inwoner kan een bericht naar de behandelend professional sturen.
  - De inwoner ontvangt en leest reacties van de professional in het portaal.
  - Berichten zijn gekoppeld aan de betreffende zaak/aanvraag.

---

# EPIC 10 — Optionele modules via WIZportaal-koppeling
*Toegevoegd n.a.v. de handleiding "WIZportaal algemeen — Inwonerportaal". Dit is een tweede systeem dat een smaller deel van de inwonerportaal-functionaliteit aanbiedt.*

> **Configuratie-uitgangspunt (geldt voor het hele epic):** alle onderstaande modules zijn **configureerbaar en per omgeving/gemeente in- en uitschakelbaar**. Ze zijn afhankelijk van een koppeling/synchronisatie met WIZportaal. Wanneer die koppeling of de betreffende module **niet is geleverd of niet is ingericht, moet de functionaliteit uitgeschakeld kunnen worden** en dan niet zichtbaar zijn voor de inwoner. Functionaliteit die al elders in deze baseline is afgedekt (DigiD-inlog, documenten uploaden + melding aan behandelaar) wordt hier niet herhaald.

## Feature 10.1 — Afspraken inzien
- **Story 10.1.1** — Als inwoner wil ik mijn geplande afspraken inzien, zodat ik weet wat er gepland staat.
  - De inwoner ziet alle geplande afspraken; afspraken uit het verleden zijn niet zichtbaar.
  - De afspraken komen overeen met de agenda-items uit WIZportaal.
  - **Configureerbaar:** de module Afspraken is aan/uit te zetten en is alleen actief bij een werkende agenda-koppeling met WIZportaal.

## Feature 10.2 — Integraal Plan
- **Story 10.2.1** — Als inwoner wil ik mijn Integraal Plan inzien en aanvullen, zodat ik kan meewerken aan mijn eigen plan.
  - De inwoner kan het Integraal Plan inzien en aanvullen.
  - De inwoner kan acties en voortgang toevoegen bij doelen.
  - Voorwaarde: het Integraal Plan is eerst ingericht in WIZportaal.
- **Story 10.2.2** — Als gemeente wil ik de toegang tot het Integraal Plan kunnen regelen, zodat de inwoner alleen ziet/bewerkt wat is toegestaan.
  - Toegang (inzien/bewerken) is per groepering instelbaar.
  - **Configureerbaar:** de module Integraal Plan is aan/uit te zetten; zonder inrichting/koppeling in WIZportaal is de module niet beschikbaar.

## Feature 10.3 — Toegang & zichtbaarheid (beheer)
- **Story 10.3.1** — Als gemeente wil ik per persoon en per traject bepalen wat zichtbaar is in het inwonerportaal, zodat alleen passende informatie wordt getoond.
  - Toegang voor de inwoner wordt verleend door "inzage in eigen dossier" op **ja** te zetten op de persoonsdetailpagina.
  - Per traject is "traject onzichtbaar voor persoon" in/uit te schakelen.
  - **Configureerbaar** per persoon en per traject.
- **Story 10.3.2** — Als toegewezen professional wil ik het inwonerportaal van de inwoner kunnen inzien, zodat ik kan zien wat de inwoner ziet.
  - De professional opent de burgerpagina via de persoonspagina (drie puntjes naast de naam → 'burgerpagina').

## Feature 10.4 — Contactpersonenportaal
- **Story 10.4.1** — Als contactpersoon (bijv. zorgverlener of professional van een andere organisatie) wil ik via een eigen portaal inzage hebben in het Integraal Plan, zodat ik kan meekijken/meewerken.
  - Er is een apart contactpersonenportaal, bereikbaar via dezelfde URL als WIZportaal.
- **Story 10.4.2** — Als gemeente wil ik de toegang en rechten van een contactpersoon kunnen regelen, zodat deze alleen ziet/doet wat is toegestaan.
  - Toegang ('inzage in traject') en rechten zijn per contactpersoon en per Integraal Plan instelbaar.
- **Story 10.4.3** — Als contactpersoon wil ik automatisch mijn inloggegevens ontvangen, zodat ik direct toegang heb.
  - De contactpersoon ontvangt automatisch een e-mail met de inloggegevens voor het contactpersonenportaal.
- **Configureerbaar:** het contactpersonenportaal is als geheel aan/uit te zetten en afhankelijk van de WIZportaal-koppeling.

## Feature 10.5 — Alternatieve toegang/inlog (optioneel)
- **Story 10.5.1** — Als inwoner wil ik kunnen inloggen op het WIZ-inwonerportaal, eventueel via DigiD, zodat ik toegang heb tot mijn dossier.
  - Inloggen gaat via dezelfde URL als WIZportaal (voor professionals).
  - Een DigiD-koppeling is **optioneel** (zonder machtigingsservice).
  - **Configureerbaar:** of en hoe DigiD-inlog wordt gebruikt is instelbaar; deze WIZ-variant staat los van de (verplichte) DigiD-inlog in Epic 1 voor het X-Works-portaal.

> **Let op (verfijning, al gedekt):** "Documenten uploaden door de inwoner met melding aan de behandelaar" is functioneel al afgedekt in Feature 9.2 / Story 9.1.2. In de WIZ-variant landen geüploade bestanden in Multimedia in een aparte map 'Inwonerportaal'. Ook deze koppeling is configureerbaar/uitschakelbaar.

---

## Bijlage A — Herkomst per epic (traceerbaarheid naar handleiding)

| Epic | Handleiding-paragraaf |
|------|------------------------|
| 1 — Toegang & Authenticatie | H1 (DigiD, sessie, menustructuur) |
| 2 — Persoonlijke gegevens | 1.1 |
| 3 — Lopende zaken | 1.2 |
| 4 — Voorzieningen (Wmo/Jeugd) | 1.3, 1.4 |
| 5 — Uitkeringen & vorderingen | 1.5 (vorderingen: PvE/acceptatiecriteria O/K) |
| 6 — Documenten | 1.6 |
| 7 — Zelf regelen / Aanvragen | H1 (Zelf regelen), 2.5 |
| 8 — Inrichting & Beheer | 2.1, 2.2, 2.3, 2.4, 2.5 |
| 9 — Notificaties & tweerichtingscommunicatie | (geen; voortgekomen uit klantbevindingen) |
| 10 — Optionele modules via WIZportaal-koppeling | Handleiding WIZportaal algemeen — Inwonerportaal |

## Bijlage B — Aandachtspunten voor de volgende stap (klantbevindingen)

De handleiding-baseline is inmiddels aangevuld met klantbevindingen (zie Bijlage C). Resterende, nog niet via een bevinding afgedekte aandachtspunten:

- Lege/niet-relevante modules: tonen, verbergen of contextueel afhandelen.
- ~~Wijzigen van gegevens door de inwoner~~ → afgedekt (Feature 2.4 — Gegevens wijzigen / doorgeven).
- ~~Notificaties/berichten bij statuswijziging~~ → afgedekt (Epic 9).
- ~~Toegankelijkheid (WCAG)~~ → afgedekt (Uitgangspunt U7). Responsive → afgedekt (Story 1.3.1).
- Taalniveau (B1) — nog open.
- Statusbeleving: van interne codes naar begrijpelijke voortgangsstappen.
- Bewust niet opgenomen (besluit): overige techniek-NFR's (performance 95%/3s, HTTPS/TLS/DNSSEC/HSTS, bredere AVG-logging buiten opschoning) en het uitgangspunt toekomstvastheid (PvE 66). Deze horen in het project-/contractplan, niet in de functionele baseline.

---

## Bijlage C — Wijzigingslog (verwerkte klantbevindingen)

### Ronde 1 — Eemsdelta & Midden-Groningen (verwerkt 25-06-2026)

Honoreerbare aanvullingen verwerkt in de baseline:

| Bevinding | Aard | Verwerkt in |
|-----------|------|-------------|
| #7 Meerdere telefoonnummers | Wens | Story 2.2.2 |
| #3 / #11 Condities op tekstvragen & tekstfragmenten | Wens (gap) | Story 7.3.1 |
| #4 / #8 / #10 Auto-vullen & condities op X-Works-gegevens | Functionele eis (nu bug/config) | Story 7.3.2 |
| #2 Klikbare URL in tekstblok | Reeds mogelijk → vastgelegd | Story 7.3.3 |
| #1 Diakrieten correct weergeven | Kwaliteitseis (nu bug) | Story 7.3.3 |
| #9 Info-icoon ("i") | Bug → als eis vastgelegd | Story 7.3.4 |
| #13 Knop "Vorige" | Wens | Story 7.4.1 |
| #12 Afbreken op laatste pagina | Reeds mogelijk → vastgelegd | Story 7.4.2 |
| #14 Verplicht-vlag omkeerbaar | Bug (#4009) → als eis vastgelegd | Story 7.4.3 |
| #5 Bevestigingsmail / beveiligd mailen | Aanvulling (aparte module) | Feature 7.5 |
| #6 Opschonen niet-voltooide aanvragen | AVG-eis (archiefwet-traject) | Feature 7.6 |

Niet verwerkt (zuivere bugs in bestaande software; lossen op via ticket, geen baseline-impact):

- Geen losse bevindingen zonder baseline-relevantie; bug-getallen (#4009, #3688) zijn als functionele eis geborgd in bovenstaande stories zodat een nieuwe bouw conform baseline het gedrag afdwingt.

> Onderbouwing per bevinding staat in het aparte document *Verwerkingslog bevindingen — Eemsdelta en Midden-Groningen*.

### Ronde 2 — Westerkwartier (verwerkt 25-06-2026)

Honoreerbare aanvullingen verwerkt in de baseline (28 bevindingen beoordeeld):

| Bevinding | Aard | Verwerkt in |
|-----------|------|-------------|
| Wijzigen e-mail/telefoon met verificatiecode (#1757) | Wens | Feature 2.4 |
| Correspondentieadres tonen (#1757) | Incident → eis | Story 2.1.2 |
| Meerdere webformulieren per zaaktype | Informatieverzoek → eis | Story 8.5.2 |
| Dubbele/gecombineerde aanvragen per zaak | Informatieverzoek → ontwerpkeuze | Story 8.5.3 |
| Notificatie bij statuswijziging/besluit | Wens | Feature 9.1 |
| Verzoek aanvullende stukken | Wens | Story 9.1.2 |
| Documenten toevoegen aan bestaande zaak (#4238 e.a.) | Wens | Feature 9.2 |
| Zaakstatus bij meerdere zaken zelfde type | Incident → eis | Story 3.3.1 |
| Tekstregel i.p.v. groot tekstvak (#4007) | Wens | Story 7.3.5 |
| Tekstveld passend / uitbreidbaar | Incident/Wens → eis | Story 7.3.5 |
| Verwijderen conditie 'verplicht' (#4009) | Wens | Story 7.4.3 |
| Verwijderen navigaties template (#4009) | Wens | Story 7.4.4 |
| Dataverlies/'notify' bij invullen (#4138) | Incident → eis | Story 7.3.6 |
| 'i'-icoon werkt niet in productie (#3688) | Incident → eis | Story 7.3.4 |
| Pagina's groeperen voor condities (#4136) | Wens | Story 7.3.1 |
| Ondertekening partner werkt niet (#4139) | Incident → eis | Feature 7.7 |
| Plaatsing verplicht-teken '*' (#4140) | Incident → eis | Story 7.4.5 |
| Voorwaardelijke tekstvertoning (#4141) | Wens | Story 7.3.1 |
| Onoverzichtelijke samenvatting | Wens | Feature 7.8 |
| 'Indienen' vs. 'opslaan en afsluiten' (#4142) | Incident → eis | Story 7.1.2 |
| Chronologische volgorde aanvraag vervolgen (#4143) | Wens | Story 7.2.1 |
| Opschonen persoon niet-inwoner | Informatieverzoek → AVG-eis | Story 7.6.2 |
| Formulier direct vanaf website starten (#4238) | Wens | Story 7.1.3 |
| Look & feel / huisstijl uitbreiden | Wens | Story 1.3.3 |
| Meerdere telefoonnummers (was al ronde 1) | Wens | Story 2.2.2 |
| Lopende zaken openen traag (#4144) | Incident → prestatie-eis | Story 3.1.1 |

Niet verwerkt — zuivere bugs in bestaande software (lopen via ticket, geen baseline-impact):

| Bevinding | Ticket | Reden |
|-----------|--------|-------|
| Keuzelijst/dropdown werkt niet | #4200 | Cosmetisch/technisch defect; nieuwe bouw gebruikt eigen formuliercomponenten |
| Scrollen binnen loket werkt niet | #4137 | Technisch defect bestaande UI |
| Knop niet goed gecentreerd / leeg | #4202 | Cosmetisch defect bestaande UI |

> Onderbouwing per bevinding staat in het aparte document *Verwerkingslog bevindingen — Westerkwartier*.

### Ronde 3 — WIZportaal (2e systeem) (verwerkt 25-06-2026)

De handleiding "WIZportaal algemeen — Inwonerportaal" beschrijft een tweede systeem. Nieuwe functionaliteit is toegevoegd als **optionele, configureerbare en uitschakelbare** modules (Epic 10), afhankelijk van de WIZportaal-koppeling.

| Onderdeel WIZportaal | Beslissing | Verwerkt in |
|----------------------|-----------|-------------|
| DigiD-inlog | Al gedekt | Epic 1 (X-Works) + Story 10.5.1 (WIZ-variant optioneel) |
| Responsive weergave | Verfijning | Story 1.3.1 |
| Afspraken inzien | Nieuw (configureerbaar) | Feature 10.1 |
| Integraal Plan inzien/aanvullen | Nieuw (configureerbaar) | Feature 10.2 |
| Toegang per persoon/traject + inzage eigen dossier | Nieuw (configureerbaar) | Feature 10.3 |
| Professional bekijkt burgerpagina | Nieuw | Story 10.3.2 |
| Contactpersonenportaal | Nieuw (configureerbaar) | Feature 10.4 |
| Optionele/alternatieve inlog (DigiD optioneel) | Nieuw (configureerbaar) | Feature 10.5 |
| Documenten uploaden + melding behandelaar | Al gedekt (verfijnd) | Feature 9.2 / Story 9.1.2 (+ noot bij Epic 10) |

> Onderbouwing staat in het aparte document *Verwerkingslog bevindingen — WIZportaal*.

### Ronde 4 — Opmeer & Koggenland (PvE-notitie) (verwerkt 25-06-2026)

De Stuurgroepnotitie (26-01-2026) stelt vast dat de oplevering niet aan het PvE voldoet. Dit zijn kaderstellende/harde eisen, verwerkt als nieuwe sectie *Uitgangspunten & kaderstellende eisen (PvE)* plus enkele functionele aanvullingen.

| Eis / onderwerp | Aard | Verwerkt in |
|-----------------|------|-------------|
| Eis 35 — één integraal portaal | Hard kader | Uitgangspunt U1 (+ noot bij Epic 10) |
| Eis 35/58 — integrale aanvraagmodule, alle regelingen | Hard kader + functioneel | U2 + Story 7.1.4 |
| Eis 60 — geen dubbele registratie | NFR/kader | U3 + noot Epic 8 |
| Eis 57 — off-premise/volledig geserviced | NFR/kader | U4 + noot Epic 8 |
| Eis 65 — beveiliging & certificering | NFR/kader | U5 + noot Epic 8 |
| Eis 58 — akkoord op plannen | Functioneel | U6 + Feature 7.9 |
| NvI — helpers betrekken | Functioneel | Story 7.7.2 |
| NvI — berichten sturen/ontvangen professional | Functioneel | Feature 9.3 |
| NvI — zelfstandig indienen, uploaden, controlestappen | Al gedekt | Epic 7, Feature 9.2 |

> Onderbouwing staat in het aparte document *Verwerkingslog bevindingen — Opmeer en Koggenland*.

### Ronde 5 — Acceptatiecriteria Opmeer/Koggenland (selectief verwerkt 25-06-2026)

Na gap-analyse (zie *Gap-analyse acceptatiecriteria Opmeer-Koggenland*) zijn alleen de door de opdrachtgever akkoord bevonden, redelijke functionele gaps doorgevoerd:

| Criterium | Beslissing | Verwerkt in |
|-----------|-----------|-------------|
| Wijzigingen doorgeven — IBAN | Toevoegen (akkoord) | Story 2.4.2 |
| Wijzigingen doorgeven — inkomen | Toevoegen (akkoord) | Story 2.4.3 |
| Digitaal herstel onvolledige aanvragen (hersteltermijnen) | Toevoegen (akkoord) | Feature 7.10 |
| Inloggen via eHerkenning | Niet overnemen (akkoord) | — (vervalt; niet passend bij inwonersportaal) |

Overige gaps uit de gap-analyse en de project-/DoD-criteria zijn op dat moment geparkeerd; de besluiten daarover staan in Ronde 6.

### Ronde 6 — Besluiten opdrachtgever op openstaande gaps (verwerkt 25-06-2026)

| Punt | Besluit | Verwerkt in |
|------|---------|-------------|
| A1 — WCAG 2.1 AA / EN 301 549 | Opnemen | Uitgangspunt U7 |
| A2 — Besluiten inzien | Opnemen | Feature 3.4 |
| A2 — Vorderingen inzien | Opnemen | Feature 5.3 (Epic 5 → "Uitkeringen & vorderingen") |
| A3 — Overige techniek-NFR's (performance/security/AVG-logging) | Niet in baseline | — (project-/contractplan) |
| A4 — Toekomstvastheid (PvE 66) | Niet in baseline | — (project-/contractplan) |
| B4 — Leerlingenvervoer | Buiten scope | Scope-afbakening (Uitgangspunten) |
| C3 — Partner mede-ondertekenen | **Verplicht apart inloggen via DigiD** (harde eis) | Story 7.7.1 |
| C4 — Opschonen niet-voltooide/niet-inwoner | Automatiseren **met audit-logging** | Feature 7.6 (7.6.1 & 7.6.2) |

Nog open (niet besloten): taalniveau B1; en de inhoudelijke keuzes C1 (handmatige goedkeuringsstap bij wijzigen contactgegevens, ticket #1757) en C2 (meerdere digitale aanvragen aan één zaak, Story 8.5.3).
