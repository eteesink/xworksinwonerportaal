# Inwonerportaal — overzetten naar een andere VM

Deze zip bevat de kant-en-klare Docker-image van het inwonerportaal. De doel-VM hoeft niets te
builden; alleen de image laden en starten. Vereiste op de doel-VM: **Docker** (met Compose v2).

De container start onder de naam **`vs-inwonerportaal`** en sluit aan op het bestaande netwerk
**`verius-gatekeeper_gatekeeper`** (van de gatekeeper-stack).

## Inhoud

| Bestand | Doel |
|---|---|
| `vs-inwonerportaal-0.1.0.tar` | de Docker-image (export via `docker save`) |
| `docker-compose.yml` | start-configuratie (container `vs-inwonerportaal`, extern netwerk, geen build) |
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
   docker load -i vs-inwonerportaal-0.1.0.tar
   ```

3. Start de container:

   ```powershell
   docker compose up -d
   ```

4. Bereikbaar: **uitsluitend via de gatekeeper** op het gedeelde netwerk, op hostnaam
   **`vs-inwonerportaal`** poort **10040**. Er is bewust geen host-portmapping; de container is
   dus niet rechtstreeks op `localhost` benaderbaar. Configureer de gatekeeper om naar
   `http://vs-inwonerportaal:10040` te routeren.

## Bediening

```powershell
docker compose ps
docker compose logs -f
docker compose down
```

## Aanpassingen

- **Toch een directe host-poort gewenst** (bijv. om te testen)? Voeg dan een `ports`-blok toe aan
  de service in `docker-compose.yml`, bijv.:

  ```yaml
      ports:
        - "10040:10040"
  ```

## Let op

- Profiel `stub`: de X-Works-koppeling is afgevangen met in-memory testdata (demo-burger BSN 999993653);
  geen echte X-Works/Uniface-verbinding nodig.
- Data is niet persistent: bij `docker compose down` gaan ingevoerde wijzigingen verloren.
- De image is `linux/amd64` (gebouwd op een amd64-host).
