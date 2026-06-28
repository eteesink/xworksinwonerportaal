# Inwonerportaal — design & scope-notities

> Begeleidende notities bij `Inwonerportaal.pptx` (de UX-/scope-slide met de
> genummerde punten **#0–#5**) en de UX-design-screenshots in `screenshots/`.
> Bron: presentatieslide + toelichtende mail. Dit is **design-/planmateriaal**
> voor de POC-herbouw, geen wiki-bron.
>
> **Detaillaag:** de slide #0–#6 is de samenvatting; de volledige functionele
> uitwerking (epic → feature → story met acceptatiecriteria) staat in
> `Baseline beschrijving nieuw inwonerportaal.md` — een reverse-engineering van
> de bestaande X-Works/WIZ-inwonerportalen, aangevuld met klantbevindingen
> (Eemsdelta/Midden-Groningen, Westerkwartier) en PvE-kaders (Opmeer/Koggenland).

## Uitgangspunt

Het ontwerp van het nieuwe **Verius Samen** inwonerportaal (IP-VS) is vrijwel
afgerond en is in de nieuwe huisstijl. Het dekt functioneel de bestaande
inwonerportalen af. **Aanbeveling: deze UX-designs als uitgangspunt nemen** —
zowel functioneel als qua design. Dat verzwaart de complexiteit niet en kan
worden gepresenteerd als de **eerste spin-off van Verius Samen!**

Kernrelaties uit de slide:

- De functionaliteit van **IP-VS** moet die van **IP-XW** (X-Works) afdekken.
- De functionaliteit van **IP-VS** moet die van **WIZ** afdekken.
- Vanuit IP-VS moeten **aanvraagformulieren richting WIZ** gestuurd kunnen worden.
- **DigiD** is randvoorwaardelijk, inclusief ondertekening door individu én
  partners afzonderlijk.

## De genummerde punten (#0–#5 + #6)

| # | Onderwerp | Toelichting |
|---|---|---|
| **#0** | **Design** | UX-design is al vrijwel klaar. Daardoor kunnen we functioneel én qua design het nieuwe inwonerportaal als uitgangspunt nemen. Presenteerbaar als eerste spin-off van Verius Samen. |
| **#1** | **X-Works-functionaliteit afdekken** | Belangrijkste doel van deze stap: de functionaliteit in X-Works afdekken. Groot openstaand vraagstuk: **hoe doen we de integratie met X-Works?** Reeds besproken: **deployment per klant**, dus op de webserver van elke X-Works-instantie. |
| **#2** | **Dubbele ondertekening via DigiD** | Onmisbaar bij #1. Ondertekening door individu **en** door partners afzonderlijk. Waar mogelijk **centraal** opzetten als **Verius DigiD**. |
| **#3** | **2 webformulieren → Signaal API** | Eenvoudige functionele uitbreiding: vanuit dit scherm 2 eenvoudige webformulieren bieden die na een POST in de **Signaal API** terechtkomen. **Alleen relevant voor klanten die X-Works én WIZ gebruiken** (vermoedelijk **Opmeer** en **Koggenland**). |
| **#4** | **Integraal plan vervangen** | Het vervangen van het integraal plan in het X-Works inwonerportaal — complexiteit nog onbekend. Vergt nader onderzoek en houdt mogelijk een **2e versie** in. |
| **#5** | **Bevindingenlijst oud portaal** | Er is een lijst met bevindingen van het oude X-Works inwonerportaal. Waardevol om te checken of die nog relevant zijn in de nieuwe versie. (Lijst wordt nog opgezocht.) |
| **#6** | **WIZ-inwonerportaal (optioneel)** | Als alles meezit: onderzoeken of we iets met het **WIZ-inwonerportaal** kunnen. |

## Volgorde / prioritering

Afgesproken werkvolgorde:

1. **Cruciaal eerst:** kunnen **lezen en schrijven naar X-Works** (#1).
2. Daarna **integratie met DigiD** mogelijk maken (#2).

Als dat werkt, in **aflopende prioriteit**:

3. Bestaande functionaliteit **mappen**.
4. **WIZ-API** aanroepen (#3).
5. **Issues vanuit bestaande klanten** afvinken (#5).

En als alles meezit:

6. Onderzoeken of we iets met het **WIZ-inwonerportaal** kunnen (#6).

## Relatie met de huidige POC

De bestaande POC in dit repo dekt al een deel hiervan af als werkende demo:

- **Lezen/schrijven naar X-Works** (#1) — nu via de `XworksClient`-ACL met
  `stub`-profiel; het echte `xworks`-profiel (SOAP/StUF) moet nog gebouwd.
  Let op de afspraak **deployment per klant** (per X-Works-webserver) — dit
  raakt de huidige single-container-opzet en de `xworks`-endpointconfiguratie.
- **DigiD-(mede)ondertekening** (#2) — partner-medeondertekening via DigiD is
  als demo gebouwd (story 14408). "Verius DigiD" als centrale voorziening is
  nog een open ontwerpkeuze.
- **Dynamische vragenlijsten** uit X-Works-templates — sluit aan bij #1 en het
  mappen van bestaande functionaliteit.

Nog **niet** in de POC: webformulieren → Signaal API (#3), vervangen integraal
plan (#4), afvinken bevindingenlijst (#5), WIZ-inwonerportaal (#6).

## Afkortingen

- **IP-VS** — Inwonerportaal Verius Samen (het nieuwe portaal).
- **IP-XW** — Inwonerportaal X-Works (het huidige/legacy portaal).
- **WIZ** — bestaande WIZ-applicatie/-API.
- **Signaal API** — endpoint waar webformulier-posts in terechtkomen.

## Nog te ontvangen

- UX-design-screenshots → `screenshots/` (volgen nog).
- Bevindingenlijst oud X-Works inwonerportaal (#5).
