# Ontwerp — X-Works vragenlijst-template als service voor het inwonerportaal

> Hoe halen we de vragenlijst-definitie (per gemeente) uit X-Works zodat de BFF die kan renderen?
> Dit werkt de "definitie"-helft van component 2 uit (de andere helft, `vragenlijst-eval`, is een aparte stap).

## 1. Waar de template-data zit (X-Works datamodel)

De template is in X-Works **opgeslagen data** — een boom van entiteiten, gescoped per `ADMINISTRATION` (gemeente):

```
VRAGENLIJSTTEMPLATE                 (de template-root)
  └─ PAGINA                         pagina's:     VC_CODE, VC_OMSCHRIJVING, PAGINA_ID
       └─ TAG_SVRAAG  (edge, NM_IDX = volgorde)
            └─ VRAAGBOOM            groep/sectie: VC_CODE, VC_OMSCHRIJVING, VC_TEKST, DM_GELDIG_VAN/TM
                 └─ TAG_VRAAG  (edge, NM_IDX = volgorde)
                      └─ SVRAAG     de vraag:     VC_CODE, VC_OMSCHRIJVING, VC_TEKST, DM_GELDIG_VAN/TM
```

Kenmerken die we tegenkwamen in de XSLT-laag (`wiz/wiz-vltmpl00`, `-pagina00`, `-vraagboom00`, `-svraag00`):

- **Scoping**: elk niveau draagt `ADMINISTRATION_ID` → de template verschilt per gemeente.
- **Ordening**: `NM_IDX` op de `TAG_*`-koppelentiteiten bepaalt de volgorde van vragen/groepen.
- **Geldigheid**: `DM_GELDIG_VAN` / `DM_GELDIG_TM` → versiebeheer/temporele geldigheid van vragen.
- **Concurrency/meta**: `_id`/`@uid`, `_status`, `_crc`, `VERSION`, `MODIFIEDBY/DATIM`.

**Open punt (discovery-spike):** het **veldtype** (tekst/getal/keuze/datum), **verplicht** en **keuzeopties** zijn in deze XSLT's niet één-op-één als kolom zichtbaar. In de burgerrenderer (`lsd-generate-form`) komen die uit `fieldTemplate`/`widget` (date/datetime/integer/decimal/text) en `valideer[@id]=required`. Vast te stellen waar die metadata per `SVRAAG` staat (CD_-veld, een widget-/fieldTemplate-referentie, of een aparte config-entiteit).

## 2. `wiz-vltmpl00-getocc` is een transform, geen endpoint

`wiz-vltmpl00-getocc.xslt` rendert de **beheerweergave** (pagina's van een template) naar METRO-UI. De echte ophaalactie is de **Uniface `getocc`-operatie** erachter, die het onderliggende **"XML Form document"** levert (de `dataSet`/`occ`-structuur met bovenstaande velden). Dát document is wat we willen — niet de HTML.

We consumeren dus de Uniface-operatie, niet de XSLT-output.

## 3. Drie manieren om te ontsluiten

| # | Aanpak | Voor | Tegen |
|---|---|---|---|
| **A. xws SOAP-adapter** | Ontsluit de bestaande `vltmpl`/`pagina`/`vraagboom`/`svraag` getocc-operaties via de `xws` SOAP-laag (`xws-adapter00`, `xws-soapWrap/Unwrap`, WSDL). | Bestaande X-Works-servicelaag; minste nieuwe Uniface-code. | SOAP/verbose; vereist dat deze operaties al (of met kleine aanpassing) als WSDL-operatie beschikbaar zijn. |
| **B. StUF** | Via een StUF-bevraging. | Standaard in de gemeentewereld. | StUF is gericht op zaak/persoon (ZDS/BG), niet op UI-template-metadata; onnatuurlijke fit. |
| **C. Dunne REST-wrapper** | Eén nieuwe X-Works/Uniface-operatie (of een kleine adapter) die de hele template-boom samenstelt en als **JSON/XML** teruggeeft op `GET /templates/{type}?administration={code}`. | Schoonste contract voor de BFF; één call levert de hele boom. | Vereist nieuwe Uniface-operatie/endpoint (leverancier). |

**Aanbeveling:** **A of C**, afhankelijk van wat de leverancier het snelst kan leveren. Begin met **A** als de getocc-operaties al via `xws` benaderbaar zijn (laagste drempel); kies **C** als je toch iets moet (her)bouwen — dan levert één endpoint de complete, samengestelde template (root → pagina's → vraagboom → svraag, inclusief volgorde en geldigheid), wat de BFF-kant het eenvoudigst maakt.

## 4. De keten in de BFF (profiel `xworks`)

```
React  →  BFF  XworksClient.getVragenlijstDefinitie(type)
              │
              ▼  (profiel xworks) XworksSoapClient
              │     1. roep X-Works template-service aan (A of C), met type + administration(gemeente)
              │     2. ontvang het "XML Form document" (dataSet/occ-boom)
              │     3. VragenlijstTemplateMapper.map(xml)  ← zelfde mapper als de stub
              ▼
        VragenlijstDefinitie  →  REST  →  React rendert generiek
```

Belangrijk: de **`VragenlijstTemplateMapper`** is al de scheidslijn. Nu mapt hij een vereenvoudigde XML; bij de echte koppeling laten we hem de X-Works `dataSet`/`occ`-structuur mappen. Niets anders in het portaal verandert.

### Wat de mapper moet doen op de echte structuur
- Loop `VRAGENLIJSTTEMPLATE → PAGINA` (pagina's), sorteer op `NM_IDX`.
- Per pagina: volg `TAG_SVRAAG → VRAAGBOOM` (groepen), sorteer op `NM_IDX` → map naar een `groep`-`VraagDefinitie`.
- Per vraagboom: volg `TAG_VRAAG → SVRAAG` (vragen), sorteer op `NM_IDX` → map naar veld-`VraagDefinitie`.
- Velden: `VC_OMSCHRIJVING`/`VC_TEKST` → label; **type/verplicht/opties** uit de nog te lokaliseren widget-/valideer-metadata (zie §1 open punt).
- Filter op geldigheid: alleen `SVRAAG`/`VRAAGBOOM` waar `DM_GELDIG_VAN ≤ vandaag ≤ DM_GELDIG_TM` (of leeg).
- `administration` = gemeentecode → bepaalt welke template-instantie wordt opgehaald.

## 5. Niet-functioneel

- **Caching**: templates wijzigen zelden → cache de gemapte `VragenlijstDefinitie` per (type, gemeente) in de BFF, met invalidatie op `MODIFIEDDATIM`/`VERSION`. Scheelt round-trips naar X-Works.
- **Read-only**: dit is een leesoperatie; geen `_crc`/`_status`-concurrency nodig (die geldt voor schrijven, zoals `vragenlijst-save`/`submit`).
- **Foutafhandeling**: onbekend type/gemeente → 404; X-Works onbereikbaar → 502/503 met nette melding in het portaal.

## 6. Concrete vervolgstappen

1. **Discovery-spike met de leverancier**: zijn `vltmpl`/`pagina`/`vraagboom`/`svraag` getocc al via `xws` (WSDL) benaderbaar? Zo nee, kan optie C (één samengestelde template-endpoint) gebouwd worden?
2. **Lokaliseer de veld-metadata** (type/verplicht/opties per SVRAAG) — nodig voor een volledige rendering.
3. Implementeer `XworksSoapClient.getVragenlijstDefinitie` + breid `VragenlijstTemplateMapper` uit naar de echte `dataSet`-structuur (voeg een tweede `map(...)`-pad toe; de bestaande test-resource blijft voor de stub).
4. Idem voor `getVragenlijstCatalogus` (lijst `VRAGENLIJSTTEMPLATE` per `ADMINISTRATION`).

---
*Gerelateerd: `architectuur-inwonerportaal.md` (§5 X-Works-endpoints), en de XSLT-bron in de kennisrepo (`wiki/topics/legacy/x-works/xslt`, module `wiz`).*
