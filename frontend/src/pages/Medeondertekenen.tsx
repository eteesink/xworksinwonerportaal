import { useEffect, useState } from "react";
import { api } from "../api/client";
import type { CosignView } from "../types";

/**
 * Deep-link ondertekenpagina: de partner landt hier ná DigiD-login via de uitnodigingslink
 * (/?cosign={token}). Toont het te ondertekenen document en de reeds geplaatste handtekening(en).
 */
export default function Medeondertekenen({ token }: { token: string }) {
  const [view, setView] = useState<CosignView | null>(null);
  const [melding, setMelding] = useState<{ type: "ok" | "fout"; tekst: string } | null>(null);
  const [bezig, setBezig] = useState(false);
  const [klaar, setKlaar] = useState(false);

  useEffect(() => {
    api.getCosign(token).then(setView).catch((e) => setMelding({ type: "fout", tekst: e.message }));
  }, [token]);

  async function onderteken() {
    setBezig(true);
    setMelding(null);
    try {
      const res = await api.partnerOndertekent(token);
      setKlaar(true);
      setMelding({ type: "ok", tekst: `${res.status} — zaaknummer ${res.zaaknummer}.` });
    } catch (e) {
      setMelding({ type: "fout", tekst: (e as Error).message });
    } finally {
      setBezig(false);
    }
  }

  return (
    <main className="layout">
      <div className="kaart">
        <h2>Document mede-ondertekenen</h2>
        {melding && <div className={`melding ${melding.type}`}>{melding.tekst}</div>}
        {!view && !melding && <p className="muted">Uitnodiging laden…</p>}

        {view && (
          <>
            <div className="rij"><span className="label">Document</span><span>{view.titel}</span></div>
            <div className="rij"><span className="label">Document-hash</span><span>{view.documentHash}</span></div>
            <div className="rij"><span className="label">Status</span><span>{view.status}</span></div>

            <h3 style={{ fontSize: "0.95rem" }}>Reeds ondertekend door</h3>
            <table>
              <thead><tr><th>Naam</th><th>BSN</th><th>Tijdstip</th><th>Niveau</th></tr></thead>
              <tbody>
                {view.reedsGetekendDoor.map((h, i) => (
                  <tr key={i}><td>{h.naam}</td><td>{h.bsn}</td><td>{h.tijdstip}</td><td>{h.assuranceNiveau}</td></tr>
                ))}
              </tbody>
            </table>

            {klaar ? (
              <p className="muted">U heeft ondertekend. Het document is volledig getekend en teruggekoppeld aan het portaal.</p>
            ) : view.magTekenen ? (
              <>
                <p className="muted">
                  Door te ondertekenen verklaart u akkoord met dit document. Uw DigiD-identiteit, het tijdstip en
                  de document-hash worden vastgelegd (geverifieerde elektronische handtekening).
                </p>
                <button className="primair" disabled={bezig} onClick={onderteken}>
                  {bezig ? "Ondertekenen…" : "Ondertekenen met DigiD"}
                </button>
              </>
            ) : (
              <div className="melding fout">{view.redenNietTekenen ?? "U kunt dit document niet ondertekenen."}</div>
            )}
          </>
        )}
      </div>
    </main>
  );
}
