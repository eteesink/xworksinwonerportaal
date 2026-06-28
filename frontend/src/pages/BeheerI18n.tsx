import { useEffect, useState } from "react";
import { api, type I18nEntry } from "../api/client";

/**
 * Beheerinterface voor vertalingen. De Nederlandse bron is leidend; de beheerder laat
 * ontbrekende doeltaal-vertalingen automatisch genereren en past ze waar nodig handmatig aan
 * (een handmatige edit krijgt status "reviewed" en wordt niet door een latere AI-run overschreven).
 *
 * Deze tool is bewust Nederlandstalig (interne beheerfunctie) en niet zelf ge-i18n'd.
 */
export default function BeheerI18n() {
  const [talen, setTalen] = useState<string[]>([]);
  const [bron, setBron] = useState("nl");
  const [taal, setTaal] = useState<string>("");
  const [entries, setEntries] = useState<I18nEntry[]>([]);
  const [edits, setEdits] = useState<Record<string, string>>({});
  const [nieuweTaal, setNieuweTaal] = useState("");
  const [filter, setFilter] = useState("");
  const [bezig, setBezig] = useState(false);
  const [melding, setMelding] = useState<{ soort: "ok" | "fout"; tekst: string } | null>(null);

  useEffect(() => {
    api
      .beheerTalen()
      .then(({ talen, bron }) => {
        setTalen(talen);
        setBron(bron);
        const eerste = talen.find((t) => t !== bron) ?? bron;
        setTaal(eerste);
      })
      .catch((e) => setMelding({ soort: "fout", tekst: e.message }));
  }, []);

  useEffect(() => {
    if (!taal) return;
    laadEntries(taal);
  }, [taal]);

  function laadEntries(t: string) {
    setEdits({});
    api
      .beheerEntries(t)
      .then(setEntries)
      .catch((e) => setMelding({ soort: "fout", tekst: e.message }));
  }

  async function opslaan(key: string) {
    const waarde = edits[key];
    if (waarde === undefined) return;
    setBezig(true);
    try {
      await api.beheerZet(taal, key, waarde);
      setMelding({ soort: "ok", tekst: `"${key}" opgeslagen` });
      laadEntries(taal);
    } catch (e) {
      setMelding({ soort: "fout", tekst: (e as Error).message });
    } finally {
      setBezig(false);
    }
  }

  async function vertaalOntbrekende() {
    setBezig(true);
    try {
      const { vertaald } = await api.beheerVertaalOntbrekende(taal);
      setMelding({ soort: "ok", tekst: `${vertaald} ontbrekende vertaling(en) automatisch gevuld (te reviewen).` });
      laadEntries(taal);
    } catch (e) {
      setMelding({ soort: "fout", tekst: (e as Error).message });
    } finally {
      setBezig(false);
    }
  }

  async function taalToevoegen() {
    if (!nieuweTaal.trim()) return;
    setBezig(true);
    try {
      await api.beheerVoegTaalToe(nieuweTaal.trim().toLowerCase());
      const { talen } = await api.beheerTalen();
      setTalen(talen);
      setTaal(nieuweTaal.trim().toLowerCase());
      setNieuweTaal("");
      setMelding({ soort: "ok", tekst: "Taal toegevoegd. Gebruik 'Vertaal ontbrekende' om te vullen." });
    } catch (e) {
      setMelding({ soort: "fout", tekst: (e as Error).message });
    } finally {
      setBezig(false);
    }
  }

  async function taalVerwijderen() {
    if (!taal || taal === bron) return;
    if (!confirm(`Taal "${taal}" verwijderen?`)) return;
    setBezig(true);
    try {
      await api.beheerVerwijderTaal(taal);
      const { talen } = await api.beheerTalen();
      setTalen(talen);
      setTaal(talen.find((t) => t !== bron) ?? bron);
      setMelding({ soort: "ok", tekst: "Taal verwijderd." });
    } catch (e) {
      setMelding({ soort: "fout", tekst: (e as Error).message });
    } finally {
      setBezig(false);
    }
  }

  const zichtbaar = entries.filter(
    (e) =>
      !filter ||
      e.key.toLowerCase().includes(filter.toLowerCase()) ||
      e.bron.toLowerCase().includes(filter.toLowerCase())
  );
  const aantalMissing = entries.filter((e) => e.status === "missing").length;

  return (
    <>
      <div className="sectie-kop">
        <h2>⚙ Vertalingen beheren</h2>
      </div>

      <div className="kaart">
        <div style={{ display: "flex", flexWrap: "wrap", gap: "1rem", alignItems: "flex-end" }}>
          <div className="veld" style={{ margin: 0 }}>
            <label>Doeltaal</label>
            <select value={taal} onChange={(e) => setTaal(e.target.value)}>
              {talen
                .filter((t) => t !== bron)
                .map((t) => (
                  <option key={t} value={t}>
                    {t}
                  </option>
                ))}
            </select>
          </div>
          <button className="primair" onClick={vertaalOntbrekende} disabled={bezig || !taal}>
            ✨ Vertaal ontbrekende met AI{aantalMissing ? ` (${aantalMissing})` : ""}
          </button>
          <button onClick={taalVerwijderen} disabled={bezig || !taal}>
            Taal verwijderen
          </button>
          <div className="veld" style={{ margin: 0 }}>
            <label>Nieuwe taal (ISO 639-1)</label>
            <div style={{ display: "flex", gap: "0.4rem" }}>
              <input
                value={nieuweTaal}
                onChange={(e) => setNieuweTaal(e.target.value)}
                placeholder="bv. de"
                style={{ width: 90 }}
              />
              <button onClick={taalToevoegen} disabled={bezig}>
                Toevoegen
              </button>
            </div>
          </div>
        </div>
      </div>

      {melding && <div className={"melding " + melding.soort}>{melding.tekst}</div>}

      <div className="veld">
        <label>Filter op sleutel of brontekst</label>
        <input value={filter} onChange={(e) => setFilter(e.target.value)} placeholder="zoek…" />
      </div>

      <div className="kaart">
        <table>
          <thead>
            <tr>
              <th style={{ width: "22%" }}>Sleutel</th>
              <th style={{ width: "30%" }}>Nederlands (bron)</th>
              <th>Vertaling ({taal})</th>
              <th style={{ width: 90 }}>Status</th>
              <th style={{ width: 90 }}></th>
            </tr>
          </thead>
          <tbody>
            {zichtbaar.map((e) => {
              const waarde = edits[e.key] ?? e.waarde;
              const gewijzigd = edits[e.key] !== undefined && edits[e.key] !== e.waarde;
              return (
                <tr key={e.key}>
                  <td style={{ fontFamily: "monospace", fontSize: "0.82rem" }}>{e.key}</td>
                  <td className="muted">{e.bron}</td>
                  <td>
                    <input
                      style={{ width: "100%" }}
                      value={waarde}
                      onChange={(ev) => setEdits({ ...edits, [e.key]: ev.target.value })}
                    />
                  </td>
                  <td>
                    <StatusBadge status={e.status} />
                  </td>
                  <td>
                    <button onClick={() => opslaan(e.key)} disabled={bezig || !gewijzigd}>
                      Opslaan
                    </button>
                  </td>
                </tr>
              );
            })}
          </tbody>
        </table>
      </div>
    </>
  );
}

function StatusBadge({ status }: { status: I18nEntry["status"] }) {
  const kleur: Record<string, string> = {
    missing: "#b3261e",
    auto: "#e0600f",
    reviewed: "#2e7d32",
    source: "#5f6b66",
  };
  const tekst: Record<string, string> = {
    missing: "ontbreekt",
    auto: "auto",
    reviewed: "gereviewd",
    source: "bron",
  };
  return (
    <span
      style={{
        background: kleur[status] ?? "#5f6b66",
        color: "#fff",
        borderRadius: 6,
        padding: "0.1rem 0.45rem",
        fontSize: "0.75rem",
        whiteSpace: "nowrap",
      }}
    >
      {tekst[status] ?? status}
    </span>
  );
}
