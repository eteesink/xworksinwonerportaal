# Verius inwonerportaal — design system & IA (uit de visual-design-set)

**Bron:** `docs/design/verius-visual-design/` (22 PDF's: paginamockups, interactie-flows en flow-boards, geleverd 25-06-2026).
**Status:** afgeleid uit de mockups; exacte tokens/assets (hex, fonts, logo, illustraties) nog te bevestigen tegen de Figma-bron.
**Doel:** durende referentie om de frontend van het inwonerportaal in de nieuwe **Verius** look & feel te bouwen.

> ⚠️ Dit vervangt de huidige **Stipter**-huisstijl in de POC (blauw `#1A405A` / groen `#77B31D`). De nieuwe identiteit is **Verius**: donkergroen met oranje accent. Zie "Migratie van de POC" onderaan.

## 1. Merk & identiteit

- Woordmerk linksboven: **"inwonerportaal / verius"** (twee regels, wit op donkergroen).
- Donkergroene header- en footerbalk over de volledige breedte; witte content-canvas ertussen.
- Footer is een donkergroen blok met centraal "verius".

## 2. Kleur-tokens (approximatief — bevestigen tegen Figma)

| Token | Waarde (≈) | Gebruik |
|---|---|---|
| `--verius-groen` | `#12302A` | header, footer, primaire donkere vlakken |
| `--verius-groen-actief` | `#1C3F36` | actieve nav-pill (bv. "Mijn plannen") |
| `--verius-oranje` | `#E0600F` | accent: actieve sidebar-rand, status-dot ("● 2 acties") |
| `--verius-zand` | `#F4EFE3` | highlight-kaart ("Niet om te missen") |
| `--kleur-tekst` | `#1A1A1A` | bodytekst, koppen |
| `--kleur-muted` | `#5F6B66` | metadata ("Laatste wijziging door…", datums) |
| `--kleur-rand` | `#E2E2DD` | kaart-/lijstranden |
| `--kleur-oppervlak` | `#FFFFFF` | kaarten, content-achtergrond |
| `--kleur-link` | `#12302A` | links (onderstreept) |

Dominantie: donkergroen draagt de chrome, oranje is **spaarzaam** accent (actief/aandacht), zand alleen voor de highlight-kaart. Geen accentstrepen elders.

## 3. Typografie

- Schreefloos, modern (te bevestigen — waarschijnlijk een NL Design System / Amsterdam-font; zie §7).
- Paginatitel groot/bold (~28–32px), sectiekoppen bold (~20–22px) met outline-icoon ervoor.
- Bodytekst ~16px; metadata ~14px muted.
- Links **onderstreept** en vaak bold in lijstitems.

## 4. App-shell & layout

```
┌───────────────────────────────────────────────────────────────┐
│ [inwonerportaal/verius]  Home  Mijn plannen  Gegevens  Berichten   🌐 Nederlands  Welcome Daan ▾ │   ← donkergroene header
├───────────────────────────────────────────────────────────────┤
│ Home › Mijn plannen › Reintegratie                              │   ← breadcrumb (onderstreepte links)
│ ┌───────────┐  Reintegratie                                     │
│ │ Afspraken │  Laatste wijziging door … · [🔊 Luister]          │   ← linker sectie-nav (sticky)
│ │ Hoofddoel.│  ┌───────────────────────────────────────────┐    │      actief = oranje linkerrand + tekst
│ │ Samenvat. │  │ Niet om te missen      [illustratie]      │    │   ← zand highlight-kaart
│ └───────────┘  └───────────────────────────────────────────┘    │
│                ▣ Afspraken            [Afspraak toevoegen ⊕]     │   ← sectiekop + outlined actieknop
│                ┌ kaart ─────────────────────────────────────┐    │
│                │ Intake … [✎ Aanpassen] · datum · ⌂ Thuis …  │    │
│                └────────────────────────────────────────────┘    │
│                ◎ Hoofddoelen          [Hoofddoel toevoegen ⊕]    │
│                Plan-kaarten met [→]                              │
│                ▦ Samenvatting                                    │
├───────────────────────────────────────────────────────────────┤
│                        Footer · verius                          │   ← donkergroene footer
└───────────────────────────────────────────────────────────────┘
```

- **Header**: logo links; hoofdmenu (Home / Mijn plannen / Gegevens / Berichten) met de actieve als lichtgroene pill; rechts taalknop (🌐 Nederlands) + gebruikersmenu ("Welcome Daan ▾").
- **Breadcrumb** onder de header, onderstreepte links.
- **Twee koloms** content: links een sticky **sectie-navigatie** (anchors binnen de pagina), rechts de content. Actief sectie-item = oranje linkerrand + oranje tekst.
- **Footer**: donkergroen blok, gecentreerd "verius".
- **Responsive**: header klapt in tot een **"Menu"**-knop (hamburger); alles stapelt verticaal; de highlight-kaart, afspraken en doelen worden volle-breedte kaarten. (zie de mobiele variant in `Plan pagina.pdf`.)

## 5. Componenten

- **Knoppen**: outlined, afgeronde hoeken, label + rond `⊕`/`→`-icoon ("Afspraak toevoegen ⊕", navigeren met `→`). Geen gevulde kleurknoppen in de mockups.
- **Highlight-kaart "Niet om te missen"**: zand-achtergrond, met illustratie (wuivende persoon), opsomming van komende afspraak + recente acties (icoon + bold titel + subtekst).
- **Sectiekop**: outline-icoon (📅 afspraken, ◎ doelen, ▦ samenvatting, ⚡ voortgang) + bold titel, met rechts een toevoeg-knop.
- **Lijst-/contentkaart**: dunne rand, titel als **onderstreepte bold link**, metadata-regel ("Aangemaakt om … door …"), rechts status (bv. oranje **"● 2 acties"** / grijs "Geen acties") of een `[✎ Aanpassen]`-link.
- **Afspraakkaart**: titel + `Aanpassen`, datum, tijdvak, locatie-icoon (⌂ Thuis / ☎ Bel), "Met <persoon>".
- **"Luister"-knop**: outlined met luidspreker-icoon — tekst-naar-spraak (toegankelijkheid).
- **Breadcrumbs, taal-toggle, gebruikersmenu** zoals in de header.

## 6. Informatie-architectuur & domein

De nieuwe IA is **plan-centrisch** — dit is het **Integraal Plan** (baseline Epic 10.2) met **Afspraken** (Epic 10.1):

```
Home
Mijn plannen ─► Plan (bv. "Reintegratie")
                 ├─ Afspraken           (afspraakkaarten; toevoegen/aanpassen)
                 ├─ Hoofddoelen ─► Hoofddoel (bv. "Zelfredzamer worden")
                 │                   ├─ Subdoelen ─► Subdoel  ─► Acties (eenmalig/herhalend)
                 │                   └─ Mijn voortgang
                 └─ Samenvatting
Gegevens      (persoonlijke gegevens — sluit aan op huidige POC Epic 2)
Berichten     (tweerichtingscommunicatie — baseline Epic 9 / Feature 9.3)
```

**Afspraak-regels** (uit het flow-board `Screenshot …10.23.07`): een afspraak heeft *reden → consulent kiezen → type (Bellen; Waar: gemeente/thuis/locatie) → datum/tijd*; **wijzigen kan tot 48 uur** ervoor (anders bellen). Aanpasbaarheid per herkomst:

| Afspraak van | Aanpasbaar | Bijzonderheid |
|---|---|---|
| consulent | ja | |
| inwoner | ja | |
| leverancier | **nee** | bericht sturen naar consulent dat de afspraak niet doorgaat |
| interne leverancier (betrokken professional) | ja | |

**Doelen/acties** (uit de flows): subdoelen hebben acties (eenmalig of herhalend); **max. 5 subdoelen** per hoofddoel ("na 5 niet meer mogelijk een 6e toe te voegen").

## 7. Onderliggend designsysteem — te bevestigen

Bestandsnamen `ams-dialog*.pdf` wijzen op het **Amsterdam Design System / NL Design System** als basis (met een Verius-thema/-tokens). Aanbeveling: **bevestigen** en zo ja de NL Design System-componenten + design-tokens adopteren i.p.v. eigen CSS. Voordelen: out-of-the-box **WCAG 2.1 AA** (baseline-uitgangspunt **U7**), toegankelijke componenten, en consistentie met de overheidsstandaard. De Verius-identiteit wordt dan een token-set bovenop het systeem.

## 8. Inventaris van de design-set

- **Paginamockups**: `Plan pagina`, `Hoofddoel pagina`, `Subdoel pagina`.
- **Afspraak-flows**: `Maak nieuwe afspraak`, `Afspraak met consulent aanpassen`, `Afspraak met externe mensen aanpassen`.
- **Doel/actie-flows**: `Eerste subdoel toevoegen`, `Tweede (of meer) subdoel toevoegen`, `Na 5 subdoelen …`, `Nieuwe actie toevoegen [eenmalige]`, `Nieuwe actie toevoegen [herhalende]`, `Simpele versie: Nieuwe doel toevoegen`.
- **Dialogen/overig**: `ams-dialog`, `ams-dialog-1`, `ams-dialog-2`, `Maak nieuwe afspraak - 9 versie 1`, `Rectangle 5/6`, diverse `Screenshot …` (flow-boards van Valeria).

## 9. Migratie van de POC

De huidige POC-frontend (`frontend/src/`, tab-IA: Mijn gegevens / Contactgegevens / Mijn zaken / Aanvraag / Medeondertekenen, Stipter-huisstijl) dekt **andere** functionaliteit dan dit ontwerp (dat het Integraal Plan toont, baseline Epic 10). Twee sporen:

1. **Rebrand-laag** (laag risico, direct): Stipter → Verius tokens, donkergroene header/footer, app-shell (header-nav + breadcrumb + dark footer), outlined knoppen, kaartstijl. Raakt `styles.css` + de shell in `App.tsx`.
2. **Nieuwe plan-IA** (groot, nieuwe feature): de plan-centrische schermen bouwen (Plan → Hoofddoel → Subdoel → Acties + Afspraken + Berichten). Dit is baseline **Epic 10** (Integraal Plan / WIZportaal) — in `poc-gap-plan.md` stond dit als increment E; de komst van een afgerond ontwerp pleit ervoor dit naar voren te halen.

Aanbevolen volgorde: eerst het **designsysteem/tokens + app-shell** neerzetten (spoor 1, herbruikbaar), daarna de plan-schermen (spoor 2) datagedreven uit X-Works/WIZ vullen.

**Stand van zaken (fase 1 gedaan).** De app-shell + Verius-tokens zijn gebouwd, **framework-neutraal in eigen CSS** (`frontend/src/styles.css` + `App.tsx`): donkergroene header-nav (Home / Mijn plannen / Gegevens / Berichten), breadcrumb, donkere footer, gevulde primaire knoppen, kaart-/sectiestijl, responsive Menu-toggle. De bestaande POC-pagina's draaien onder "Gegevens"; "Mijn plannen" en "Berichten" zijn placeholders voor fase 2. Token-first opzet zodat een latere NL Design System-adoptie alleen het mappen van deze tokens vergt.

## 10. Te bevestigen bij de designers (vóór de component-frameworkkeuze)

Open keuze: **NL Design System / Amsterdam adopteren** vs. **eigen CSS met Verius-tokens**. Eerst verifiëren:

1. **Onderliggend systeem**: is dit ontwerp gebaseerd op het **Amsterdam Design System / NL Design System** (de bestandsnamen `ams-dialog*` suggereren dit)? Zo ja, welke versie/welke component-bibliotheek (`@amsterdam/design-system-*` / NLDS Web Components)?
2. **Design-tokens**: leveren jullie de **token-set** (kleuren, spacing, radii, typografie) als bestand (Figma Tokens / Style Dictionary / CSS custom properties)? Onze voorlopige hex (§2) zijn benaderingen en moeten vervangen worden door de bron.
3. **Typografie**: welk **lettertype** (familie + gewichten) en zijn er licentie-/hosting-afspraken (self-hosted i.v.m. de strikte CSP/offline)?
4. **Assets**: het **Verius-woordmerk/logo** (SVG), de **illustratie** uit de "Niet om te missen"-kaart, en eventuele iconen-set — als losse bestanden.
5. **Per-gemeente thematisering**: de dialoog noemt "Gemeente Haarlem". Is de huisstijl **per gemeente** te thematiseren (logo/naam/kleur) bovenop het Verius-basisthema? (sluit aan op Story 1.3.3 en deployment per klant).
6. **Figma-toegang**: een link naar het bronbestand, zodat exacte maten/states/tokens herleidbaar zijn.
7. **WCAG**: is het ontwerp al op **WCAG 2.1 AA** getoetst (kleurcontrast donkergroen/oranje, focus-states)? Dit is baseline-uitgangspunt **U7**.

> Advies: bij bevestiging "NL Design System" → dat adopteren (out-of-the-box WCAG AA + overheidsstandaard), met de Verius-tokens als thema. De huidige token-first CSS-shell is daarmee compatibel en gaat niet verloren.

## 11. BESLISSING (2026-06-28): NL Design System — Utrecht white-label + Verius-thema

Gekozen: **NL Design System** (het wordt een overheidssite). Implementatie: de **vendor-neutrale Utrecht white-label componentbibliotheek**:

- `@utrecht/component-library-react` — React-componenten (white-label, geen eigen merk).
- `@utrecht/component-library-css` — bijbehorende component-CSS.
- `@utrecht/design-tokens` — design-tokens (CSS custom properties).

Thematisering: importeer de component-CSS + de design-tokens-CSS, wikkel de app in `className="utrecht-theme"`, en lever een **`verius-theme.css`** die de relevante `--utrecht-*`-tokens overschrijft met de Verius-waarden (donkergroen `#12302A`, oranje accent, typografie). De eigen token-CSS-shell wordt zo vervangen door NLDS-componenten + een Verius-tokenlaag.

> Niet de Rijkshuisstijl-set (dat is de Rijksoverheid-look); wél vendor-neutraal NLDS met Verius-branding. Per-gemeente thematisering (bv. Haarlem) = extra token-set bovenop het Verius-thema.

Nog te ontvangen van de designers (versnelt en borgt correctheid): de **token-export** (kleur/typografie/spacing), het **lettertype** + licentie, en het **Figma-bestand**. Tot die er zijn, thematiseer ik op de afgeleide waarden uit §2.
