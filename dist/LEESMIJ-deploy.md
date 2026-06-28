# Inwonerportaal — overzetten naar een andere VM (versie 0.2.0)

Deze zip bevat de kant-en-klare Docker-image van het inwonerportaal. De doel-VM hoeft niets te
builden; alleen de image laden en starten. Vereiste op de doel-VM: **Docker** (met Compose v2).

De container start onder de naam **`vs-inwonerportaal`** en sluit aan op het bestaande netwerk
**`verius-gatekeeper_gatekeeper`** (van de gatekeeper-stack).

**Nieuw in 0.2.0:** Verius-huisstijl (NL Design System), meertaligheid (i18n) met beheerinterface,
en het Integraal Plan (Mijn plannen) + uitgebreide aanvraag-templates.

## Inhoud

| Bestand | Doel |
|---|---|
| `vs-inwonerportaal-0.2.0.tar` | de Docker-image (export via `docker save`) |
| `docker-compose.yml` | start-configuratie (container `vs-inwonerportaal`, extern netwerk, i18n-volume) |
| `LEESMIJ-deploy.md` | dit bestand |

## Vereiste: het gatekeeper-netwerk

De container hangt zich aan het externe netwerk `verius-gatekeeper_gatekeeper`. Dit netwerk moet
al bestaan (aangemaakt door de `verius-gatekeeper` compose-stack). Controleer:

```powershell
docker network ls | Select-String "verius-gatekeeper_gatekeeper"
```

Bestaat het netwerk nog niet? Start dan eerst de gatekeeper-stack, of maak het handmatig aan:

```powershell
docker network create verius-gatekeeper_gatekeeper
```

## Stappen op de doel-VM

1. Pak de zip uit (bijv. naar `C:\inwonerportaal`).

2. Laad de image:

   ```powershell
   docker load -i vs-inwonerportaal-0.2.0.tar
   ```

3. Start de container:

   ```powershell
   docker compose up -d
   ```

4. Bereikbaar: **uitsluitend via de gatekeeper** op het gedeelde netwerk, op hostnaam
   **`vs-inwonerportaal`** poort **10040**. Er is bewust geen host-portmapping. Configureer de
   gatekeeper om naar `http://vs-inwonerportaal:10040` te routeren.

## Bediening

```powershell
docker compose ps
docker compose logs -f
docker compose down
```

## Beheerinterface (vertalingen)

Bereikbaar op **`/?beheer=i18n`** (via de gatekeeper-host). Hier kies je een doeltaal, laat je
ontbrekende vertalingen automatisch vullen, bewerk je teksten en voeg je talen toe.

⚠️ **Toegang:** in deze build laat de backend beheer toe op basis van de header
`X-Demo-Rol: beheerder` (die de frontend bij beheer-calls meestuurt). **Voor productie** moet dit
worden vervangen door de echte rol/claim uit Authentik/IdP (door de gatekeeper geïnjecteerd). Tot
die koppeling er is, geldt: wie de beheer-URL kent kan beheren. Zet desgewenst
`INWONERPORTAAL_BEHEER_DEMO_OPEN` expliciet en beperk de toegang via de gatekeeper.

## Persistentie

- De **vertaalstore** staat in het volume `i18n-data` (`/data/i18n-store.json`) → beheer-edits
  blijven behouden over `docker compose down/up` en image-updates.
- Overige data (profiel `stub`): in-memory; aanvragen/plannen-mutaties zijn niet persistent.

## Let op

- Profiel `stub`: de X-Works-koppeling is afgevangen met in-memory testdata (demo-burger BSN 999993653);
  geen echte X-Works/Uniface-verbinding nodig. Zet `SPRING_PROFILES_ACTIVE=xworks` zodra de echte
  koppeling is gebouwd.
- De image is `linux/amd64` (gebouwd op een amd64-host).
