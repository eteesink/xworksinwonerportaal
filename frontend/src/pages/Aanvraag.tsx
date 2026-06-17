import { useEffect, useRef, useState } from "react";
import { api } from "../api/client";
import type {
  Aanvraag as AanvraagModel,
  Antwoorden,
  Bijlage,
  EvaluatieResultaat,
  VraagDefinitie,
  VragenlijstDefinitie,
  VragenlijstSamenvatting,
} from "../types";

type Rij = Record<string, unknown>;

/** Tab-niveau: kies eerst een vragenlijst uit de catalogus, daarna het formulier. */
export default function Aanvraag() {
  const [catalogus, setCatalogus] = useState<VragenlijstSamenvatting[] | null>(null);
  const [type, setType] = useState<string | null>(null);
  const [fout, setFout] = useState<string | null>(null);

  useEffect(() => {
    api.getCatalogus().then(setCatalogus).catch((e) => setFout(e.message));
  }, []);

  if (type) return <AanvraagFormulier type={type} onTerug={() => setType(null)} />;
  if (fout) return <div className="melding fout">{fout}</div>;
  if (!catalogus) return <p className="muted">Vragenlijsten laden…</p>;

  return (
    <div className="kaart">
      <h2>Een aanvraag indienen</h2>
      <p className="muted">
        Kies een formulier. De lijst en de formulieren komen dynamisch uit X-Works
        (VRAGENLIJSTTEMPLATE per gemeente) — er is niets vast ingebouwd.
      </p>
      <ul style={{ listStyle: "none", padding: 0 }}>
        {catalogus.map((c) => (
          <li key={c.type} style={{ padding: "0.5rem 0", borderBottom: "1px solid #eef1f4" }}>
            <button className="primair" onClick={() => setType(c.type)}>{c.titel}</button>
            <span className="muted" style={{ marginLeft: "0.75rem" }}>type: {c.type}</span>
          </li>
        ))}
      </ul>
    </div>
  );
}

function AanvraagFormulier({ type, onTerug }: { type: string; onTerug: () => void }) {
  const [definitie, setDefinitie] = useState<VragenlijstDefinitie | null>(null);
  const [aanvraag, setAanvraag] = useState<AanvraagModel | null>(null);
  const [antwoorden, setAntwoorden] = useState<Antwoorden>({});
  const [eval_, setEval] = useState<EvaluatieResultaat | null>(null);
  const [melding, setMelding] = useState<{ type: "ok" | "fout"; tekst: string } | null>(null);
  const fileRef = useRef<HTMLInputElement>(null);
  const [partnerEmail, setPartnerEmail] = useState("");
  const [uitnodigingLink, setUitnodigingLink] = useState<string | null>(null);

  useEffect(() => {
    Promise.all([api.getDefinitie(type), api.startAanvraag(type)])
      .then(([def, aan]) => {
        setDefinitie(def);
        setAanvraag(aan);
        // initialiseer herhaalbare groepen als lege lijst
        const init: Antwoorden = { ...aan.antwoorden };
        def.vragen.filter((v) => v.type === "groep").forEach((g) => {
          if (!Array.isArray(init[g.id])) init[g.id] = [];
        });
        setAntwoorden(init);
      })
      .catch((e) => setMelding({ type: "fout", tekst: e.message }));
  }, [type]);

  useEffect(() => {
    if (!aanvraag) return;
    const t = setTimeout(() => {
      api.evalueer(aanvraag.id, antwoorden).then(setEval).catch(() => {});
    }, 250);
    return () => clearTimeout(t);
  }, [antwoorden, aanvraag]);

  if (melding?.type === "fout" && !aanvraag) return <div className="melding fout">{melding.tekst}</div>;
  if (!definitie || !aanvraag) return <p className="muted">Formulier laden…</p>;

  const ingediend = aanvraag.status === "ingediend" || aanvraag.status === "ondertekend";

  function setVeld(id: string, waarde: unknown) {
    setAntwoorden((a) => ({ ...a, [id]: waarde }));
  }
  function zichtbaar(v: VraagDefinitie): boolean {
    if (eval_ && v.id in eval_.zichtbaarheid) return eval_.zichtbaarheid[v.id];
    if (!v.zichtbaarAls) return true;
    const [veld, waarde] = v.zichtbaarAls.split("==");
    return String(antwoorden[veld] ?? "") === waarde;
  }
  function foutVoor(id: string): string | undefined {
    return eval_?.validaties.find((f) => f.vraagId === id)?.melding;
  }

  function inputVoor(v: VraagDefinitie, waarde: string, onChange: (val: string) => void) {
    if (v.type === "keuze") {
      return (
        <select value={waarde} onChange={(e) => onChange(e.target.value)}>
          <option value="">— kies —</option>
          {v.opties?.map((o) => <option key={o} value={o}>{o}</option>)}
        </select>
      );
    }
    const htmlType = v.type === "bedrag" || v.type === "getal" ? "number" : v.type === "datum" ? "date" : "text";
    return <input type={htmlType} value={waarde} onChange={(e) => onChange(e.target.value)} />;
  }

  // Herhaalbare groepen (X-Works addocc/remocc), generiek per definitie.
  function rijenVan(groepId: string): Rij[] {
    return (antwoorden[groepId] as Rij[]) ?? [];
  }
  function voegRijToe(groepId: string) {
    setAntwoorden((a) => ({ ...a, [groepId]: [...rijenVan(groepId), {}] }));
  }
  function verwijderRij(groepId: string, i: number) {
    setAntwoorden((a) => ({ ...a, [groepId]: rijenVan(groepId).filter((_, idx) => idx !== i) }));
  }
  function setRijVeld(groepId: string, i: number, veld: string, waarde: unknown) {
    setAntwoorden((a) => ({
      ...a,
      [groepId]: rijenVan(groepId).map((r, idx) => (idx === i ? { ...r, [veld]: waarde } : r)),
    }));
  }

  async function bewaar() {
    try {
      await api.bewaarConcept(aanvraag!.id, antwoorden);
      setMelding({ type: "ok", tekst: "Concept opgeslagen." });
    } catch (e) {
      setMelding({ type: "fout", tekst: (e as Error).message });
    }
  }
  async function uploadBijlage(e: React.ChangeEvent<HTMLInputElement>) {
    const f = e.target.files?.[0];
    if (!f) return;
    try {
      const b = await api.voegBijlageToe(aanvraag!.id, f.name, f.size);
      setAanvraag((a) => (a ? { ...a, bijlagen: [...a.bijlagen, b] } : a));
    } catch (err) {
      setMelding({ type: "fout", tekst: (err as Error).message });
    } finally {
      if (fileRef.current) fileRef.current.value = "";
    }
  }
  async function verwijderBijlage(b: Bijlage) {
    await api.verwijderBijlage(aanvraag!.id, b.id);
    setAanvraag((a) => (a ? { ...a, bijlagen: a.bijlagen.filter((x) => x.id !== b.id) } : a));
  }
  async function indienen(metDigiD: boolean) {
    try {
      await api.bewaarConcept(aanvraag!.id, antwoorden);
      const res = metDigiD ? await api.ondertekenMetDigiD(aanvraag!.id) : await api.dienIn(aanvraag!.id);
      setAanvraag((a) => (a ? { ...a, status: metDigiD ? "ondertekend" : "ingediend", zaaknummer: res.zaaknummer } : a));
      setMelding({ type: "ok", tekst: `${res.status} — zaaknummer ${res.zaaknummer}.` });
    } catch (e) {
      setMelding({ type: "fout", tekst: (e as Error).message });
    }
  }

  async function nodigPartnerUit(e: React.FormEvent) {
    e.preventDefault();
    try {
      const v = await api.nodigPartnerUit(aanvraag!.id, partnerEmail);
      // In de demo tonen we de link (echte koppeling verstuurt 'm per e-mail).
      setUitnodigingLink(`${window.location.origin}/?cosign=${v.token}`);
      setMelding({ type: "ok", tekst: `Uitnodiging verstuurd naar ${partnerEmail}.` });
    } catch (err) {
      setMelding({ type: "fout", tekst: (err as Error).message });
    }
  }

  if (ingediend) {
    return (
      <div className="kaart">
        <h2>{aanvraag.titel}</h2>
        <div className="melding ok">{melding?.tekst ?? "Aanvraag ingediend."}</div>
        <div className="rij"><span className="label">Status</span><span>{aanvraag.status}</span></div>
        <div className="rij"><span className="label">Zaaknummer</span><span>{aanvraag.zaaknummer}</span></div>
        <p className="muted">De aanvraag is als zaak naar X-Works geschreven en verschijnt onder "Mijn zaken".</p>

        <h3 style={{ fontSize: "0.95rem" }}>Laten mede-ondertekenen (partner)</h3>
        <form onSubmit={nodigPartnerUit} style={{ display: "flex", gap: "0.6rem", alignItems: "flex-end", flexWrap: "wrap" }}>
          <div className="veld" style={{ flex: "1 1 260px" }}>
            <label htmlFor="partnerEmail">E-mailadres partner</label>
            <input id="partnerEmail" type="email" value={partnerEmail}
                   onChange={(e) => setPartnerEmail(e.target.value)} required />
          </div>
          <button className="primair" type="submit">Uitnodiging versturen</button>
        </form>
        {uitnodigingLink && (
          <p className="muted">
            Uitnodigingslink (demo — normaliter per e-mail): <a href={uitnodigingLink}>{uitnodigingLink}</a><br />
            De partner logt via deze link in met DigiD en landt direct op de ondertekenpagina.
          </p>
        )}

        <div style={{ marginTop: "1rem" }}>
          <button onClick={onTerug}>← Andere aanvraag</button>
        </div>
      </div>
    );
  }

  const totaal = eval_?.afgeleideWaarden?.totaalBedrag;

  return (
    <div className="kaart">
      <button onClick={onTerug} style={{ marginBottom: "0.75rem" }}>← Andere aanvraag</button>
      <h2>{definitie.titel}</h2>
      {melding && <div className={`melding ${melding.type}`}>{melding.tekst}</div>}

      {definitie.vragen.map((v) => {
        if (!zichtbaar(v)) return null;

        if (v.type === "groep") {
          const rijen = rijenVan(v.id);
          return (
            <div key={v.id}>
              <h3 style={{ fontSize: "0.95rem" }}>{v.label}{v.verplicht ? " *" : ""}</h3>
              {foutVoor(v.id) && <div className="melding fout">{foutVoor(v.id)}</div>}
              <table>
                <thead>
                  <tr>{v.subvragen?.map((s) => <th key={s.id}>{s.label}</th>)}<th></th></tr>
                </thead>
                <tbody>
                  {rijen.map((r, i) => (
                    <tr key={i}>
                      {v.subvragen?.map((s) => (
                        <td key={s.id}>
                          {inputVoor(s, String(r[s.id] ?? ""), (val) => setRijVeld(v.id, i, s.id, val))}
                        </td>
                      ))}
                      <td><button onClick={() => verwijderRij(v.id, i)} title="Rij verwijderen">✕</button></td>
                    </tr>
                  ))}
                </tbody>
              </table>
              <button onClick={() => voegRijToe(v.id)}>+ Regel toevoegen</button>
            </div>
          );
        }

        return (
          <div className="veld" key={v.id}>
            <label htmlFor={v.id}>{v.label}{v.verplicht ? " *" : ""}</label>
            {inputVoor(v, String(antwoorden[v.id] ?? ""), (val) => setVeld(v.id, val))}
            {foutVoor(v.id) && <span className="muted" style={{ color: "#b3261e" }}>{foutVoor(v.id)}</span>}
          </div>
        );
      })}

      {totaal !== undefined && (
        <div className="rij" style={{ marginTop: "1rem" }}>
          <span className="label">Totaalbedrag (berekend)</span>
          <span>€ {Number(totaal).toFixed(2)}</span>
        </div>
      )}

      <h3 style={{ fontSize: "0.95rem" }}>Bijlagen</h3>
      <ul>
        {aanvraag.bijlagen.map((b) => (
          <li key={b.id}>
            {b.bestandsnaam} ({b.grootte} bytes) <button onClick={() => verwijderBijlage(b)}>verwijderen</button>
          </li>
        ))}
      </ul>
      <input ref={fileRef} type="file" onChange={uploadBijlage} />

      <div style={{ display: "flex", gap: "0.6rem", marginTop: "1.25rem", flexWrap: "wrap" }}>
        <button onClick={bewaar}>Concept opslaan</button>
        <button className="primair" disabled={!eval_?.indienbaar} onClick={() => indienen(false)}>Indienen</button>
        <button className="primair" disabled={!eval_?.indienbaar} onClick={() => indienen(true)}>Ondertekenen met DigiD</button>
      </div>
      <p className="muted">
        Dit formulier is volledig opgebouwd uit de X-Works-template (wiz-vltmpl00-getocc). Validatie en
        eventuele berekening komen uit de regelevaluatie (vragenlijst-eval); indienen schrijft via de ACL een zaak naar X-Works.
      </p>
    </div>
  );
}
