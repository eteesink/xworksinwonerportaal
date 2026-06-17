# Discovery — veld-metadata van vragenlijst-vragen (type / verplicht / opties)

> ⚠️ **Dit is verkenning, geen productiepad.** De DB-query's hieronder dienen uitsluitend om te
> *begrijpen* waar en hoe de metadata in X-Works is vastgelegd (reverse-engineering, read-only, eenmalig
> op ACC/dev). Ze worden **nooit** het runtime-datapad van het portaal.
>
> **Runtime loopt altijd via de X-Works servicelaag** (optie A getocc / optie C endpoint) — niet via
> directe DB-toegang. Reden: het samenstellen van de template bevat Uniface-logica (codelijst-resolutie
> achter `CD_BRONANTWOORD`, geldigheidsfiltering, `SVRAAG→VRAAG`-joins en mogelijk de `verplicht`-/
> conditionele regels). Die logica willen we **niet** herimplementeren. Schrijfacties lopen sowieso
> uitsluitend via de servicelaag (sleutels/triggers/`_crc`/`_status`/validatie).
>
> Sterker nog: als deze verkenning aantoont dat `verplicht` of de opties-resolutie in de **proc-/app-laag**
> zit (niet als platte data), is dat juist het bewijs dat het runtime-pad door Uniface moet — niet langs de DB.

> Het sluitstuk voor optie C: per vraag het **veldtype**, **verplicht** en de **keuzeopties** bepalen.
> Goed nieuws: dit is grotendeels in het X-Works datamodel te vinden — **zonder de leverancier**, via een
> read-only DB-query op een gemeente-database (puur ter verkenning, zie waarschuwing hierboven).

## 1. Waar de metadata zit (uit het Uniface-model)

De vraag-metadata zit **niet** op `SVRAAG` (die heeft alleen code/omschrijving/tekst/status), maar op de
**`VRAAG`**-entiteit (model WIZ), waarnaar `SVRAAG` verwijst via `SVRAAG_VRAAG`.

Keten: `VRAGENLIJSTTEMPLATE → PAGINA → PAGINA_ELEMENTEN/TAG_* → VRAAGBOOM → SVRAAG → SVRAAG_VRAAG → VRAAG`.

| Nodig (portal) | X-Works-bron | Opmerking |
|---|---|---|
| veldtype | `VRAAG.CD_WIDGETTYPE` | domeinwaarden nog te mappen op tekst/getal/bedrag/datum/keuze |
| opties (codelijst) | `VRAAG.CD_BRONANTWOORD` | verwijst naar een referentie-/codelijst |
| opties (inline) | `VRAAG.VC_BRONANTWOORD` | "keuzes indien gesloten vraag" |
| label (burger) | `VRAAG.VC_LABEL_EXTERN` | `VC_LABEL_INTERN` = intern |
| hulptekst | `VRAAG.VC_TOELICHTING` | |
| geldigheid | `VRAAG.DM_GELDIG_VAN` / `DM_GELDIG_TM` | temporele filtering |
| volgorde | `TAG_*`/`PAGINA_ELEMENTEN.NM_IDX` | |
| **verplicht** | **onbekend** — vermoedelijk tag op `PAGINA_ELEMENTEN`/`TAG_VRAAG` (`CD_TAG`/`VC_VALUE`/`JSON_VALUE`) of proc-validatie | te bevestigen |

`PAGINA_ELEMENTEN`/`TAG_*` is een generiek **key-value-tagmechanisme** (`CD_TAG`, `VC_VALUE`, `JSON_VALUE`,
`NM_IDX`, `VALID_FROM/UNTIL`) dat vragen aan pagina's/bomen koppelt én per plaatsing extra eigenschappen
kan dragen — de meest waarschijnlijke plek voor "verplicht".

## 2. Aanpak — in-house, zonder leverancier

### Stap A — Domein van `CD_WIDGETTYPE`
```sql
SELECT CD_WIDGETTYPE, COUNT(*) FROM VRAAG GROUP BY CD_WIDGETTYPE ORDER BY 2 DESC;
```
Levert de volledige lijst widgettype-codes. Map die op de portal-types (`tekst/getal/bedrag/datum/keuze`).

### Stap B — Eén echte vragenlijst doorlichten
Kies een bekende template (bijv. schuldhulp, gemeente Opmeer / `opmr`) en bekijk de vragen:
```sql
SELECT v.VC_CODE, v.VC_LABEL_EXTERN, v.CD_WIDGETTYPE,
       v.CD_BRONANTWOORD, v.VC_BRONANTWOORD, v.DM_GELDIG_VAN, v.DM_GELDIG_TM
FROM   VRAAG v
WHERE  v.ADMINISTRATION_ID = :administration;
```
En de plaatsing/tags (waar "verplicht" vermoedelijk staat):
```sql
SELECT CD_TAG, VC_VALUE, JSON_VALUE, NM_IDX, RELATED_ID, CONNECTED_ID
FROM   PAGINA_ELEMENTEN          -- of TAG_VRAAG
WHERE  ADMINISTRATION_ID = :administration
ORDER  BY NM_IDX;
```
Zoek hier naar een `CD_TAG`/`VC_VALUE`/`JSON_VALUE` die "verplicht/required/mandatory" uitdrukt.

> Veld- en tabelnamen volgen het Uniface-model; de fysieke kolomnamen kunnen per DB licht afwijken.
> Controleer tegen de entity-pagina's in de kennisrepo (`wiki/topics/legacy/x-works/wiz/xworks-wiz-vraag`).

### Stap C — Codelijst achter `CD_BRONANTWOORD`
Resolve de optie-waarden + labels van de referentielijst waarnaar `CD_BRONANTWOORD` wijst (codelijst-tabel
in de DB, of korte navraag bij de ontwikkelaar `richardbollee`). Referentiewaarden van `CD_`-velden zitten
**niet** in de model-export, dus deze komen uit de DB of van de leverancier.

### Stap D — Vastleggen
1. Map `CD_WIDGETTYPE`-codes → portal-types (tabel in `optie-c-template-endpoint.md` §4).
2. Bepaal de bron voor `verplicht` (tag vs. proc) en hoe die mee te geven in het contract.
3. Werk de `VragenlijstTemplateMapper` en de optie-C-spec bij met de bevestigde mapping.
4. Verifieer tegen het gekozen voorbeeldformulier (stap B).

## 2b. Controleren via de X-Works/Uniface-UI (geen bypass — de nette weg)

Uniface-componenten (uit de model-export, `USPECNAM`) voor het vragenlijst-beheer:

| Component | Onderhoudt |
|---|---|
| `WIZ_VRLYSTTMPL00` | vragenlijsttemplate (startpunt) |
| `WIZ_VLTMPL00` / `WIZ_PAGINA00` / `WIZ_VRAAGBOOM00` / `WIZ_SVRAAG00` | template / pagina / vraagboom / samengestelde vraag |
| **`WIZ_VRAAG00`** | **losse vraag — toont Widgettype + Keuzelijst** |
| `WIZ_VRGNLIJST00` / `WIZ_VRAGENLST00` | de lopende vragenlijst (burgerkant) |

Open `WIZ_VRLYSTTMPL00` via het beheermenu en drill door naar `WIZ_VRAAG00`; noteer per vraag het
**Widgettype** (`CD_WIDGETTYPE`) en de **Keuzelijst** (`VC_BRONANTWOORD` / `CD_BRONANTWOORD`).

### Belangrijke vondst: het samengestelde template is `SC_XMLDATA`
De proc-code zet de template als XML-blob op de entiteit en geeft die door aan de renderer:
```
putitem/id vXsltParams, "vragenlijstTemplate", sc_xmldata.VRAGENLIJSTTEMPLATE
```
Dus **`VRAGENLIJSTTEMPLATE.SC_XMLDATA`** bevat het al samengestelde template-document (widget/valideer/opties)
— exact de `vragenlijstTemplate`-parameter die `lsd-generate-form` consumeert. Voor de echte koppeling is
dít de natuurlijke payload (de service hoeft de boom niet opnieuw te assembleren), en de
`VragenlijstTemplateMapper` mapt dezelfde structuur als `lsd-generate-form`.

> Bevestiging dat runtime door Uniface moet: `CD_BRONANTWOORD` heeft FEATURE-vlaggen (`CALC`, multi-select)
> en de opties komen uit een Uniface `$valrep(...)`. De optie-/widget-afleiding is dus deels Uniface-logica;
> de platte DB-tabel geeft niet het volledige plaatje.

## 3. Wat alleen de leverancier kan bevestigen
- De **betekenis/semantiek** van de `CD_WIDGETTYPE`-codes als die niet zelf-evident zijn.
- Of `verplicht`/conditionele zichtbaarheid in de **proc-/app-laag** zit i.p.v. als data (dan is het niet
  via een query te vinden en moet het meegeleverd worden in het optie-C-endpoint).

---
*Zie `optie-c-template-endpoint.md` §4 (mapping) en `leverancier-checklist-xws.md` (vraag B8).*
