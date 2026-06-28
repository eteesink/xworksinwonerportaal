import { useEffect, useRef, useState } from "react";
import {
  Heading2,
  Heading3,
  Paragraph,
  PrimaryActionButton,
  SecondaryActionButton,
  SubtleButton,
  FormLabel,
  Textbox,
  Select,
  SelectOption,
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHeaderCell,
  TableCell,
} from "@utrecht/component-library-react";
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
  if (!catalogus) return <Paragraph>Vragenlijsten laden…</Paragraph>;

  return (
    <div className="kaart">
      <Heading2>Een aanvraag indienen</Heading2>
      <Paragraph>
        Kies een formulier. De lijst en de formulieren komen dynamisch uit X-Works
        (VRAGENLIJSTTEMPLATE per gemeente) — er is niets vast ingebouwd.
      </Paragraph>
      {catalogus.map((c) => (
        <button key={c.type} className="lijst-rij" onClick={() => setType(c.type)}>
          <span>
            <span className="titel">{c.titel}</span>
            <span className="meta-regel">type: {c.type}</span>
          </span>
          <span className="pijl">→</span>
        </button>
      ))}
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
  if (!definitie || !aanvraag) return <Paragraph>Formulier laden…</Paragraph>;

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

  function inputVoor(v: VraagDefinitie, waarde: string, onChange: (val: string) => void, id?: string) {
    if (v.type === "keuze") {
      return (
        <Select id={id} value={waarde} onChange={(e) => onChange((e.target as HTMLSelectElement).value)}>
          <SelectOption value="">— kies —</SelectOption>
          {v.opties?.map((o) => (
            <SelectOption key={o} value={o}>
              {o}
            </SelectOption>
          ))}
        </Select>
      );
    }
    const htmlType =
      v.type === "bedrag" || v.type === "getal" ? "number" : v.type === "datum" ? "date" : "text";
    return <Textbox id={id} type={htmlType} value={waarde} onChange={(e) => onChange(e.target.value)} />;
  }

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
      setAanvraag((a) =>
        a ? { ...a, status: metDigiD ? "ondertekend" : "ingediend", zaaknummer: res.zaaknummer } : a
      );
      setMelding({ type: "ok", tekst: `${res.status} — zaaknummer ${res.zaaknummer}.` });
    } catch (e) {
      setMelding({ type: "fout", tekst: (e as Error).message });
    }
  }

  async function nodigPartnerUit(e: React.FormEvent) {
    e.preventDefault();
    try {
      const v = await api.nodigPartnerUit(aanvraag!.id, partnerEmail);
      setUitnodigingLink(`${window.location.origin}/?cosign=${v.token}`);
      setMelding({ type: "ok", tekst: `Uitnodiging verstuurd naar ${partnerEmail}.` });
    } catch (err) {
      setMelding({ type: "fout", tekst: (err as Error).message });
    }
  }

  if (ingediend) {
    return (
      <div className="kaart">
        <Heading2>{aanvraag.titel}</Heading2>
        <div className="melding ok">{melding?.tekst ?? "Aanvraag ingediend."}</div>
        <div className="rij"><span className="label">Status</span><span>{aanvraag.status}</span></div>
        <div className="rij"><span className="label">Zaaknummer</span><span>{aanvraag.zaaknummer}</span></div>
        <p className="muted">De aanvraag is als zaak naar X-Works geschreven en verschijnt onder "Mijn zaken".</p>

        <Heading3>Laten mede-ondertekenen (partner)</Heading3>
        <form onSubmit={nodigPartnerUit} style={{ display: "flex", gap: "0.6rem", alignItems: "flex-end", flexWrap: "wrap" }}>
          <div className="veld" style={{ flex: "1 1 260px" }}>
            <FormLabel htmlFor="partnerEmail">E-mailadres partner</FormLabel>
            <Textbox id="partnerEmail" type="email" value={partnerEmail}
                     onChange={(e) => setPartnerEmail(e.target.value)} required />
          </div>
          <PrimaryActionButton type="submit">Uitnodiging versturen</PrimaryActionButton>
        </form>
        {uitnodigingLink && (
          <p className="muted">
            Uitnodigingslink (demo — normaliter per e-mail): <a href={uitnodigingLink}>{uitnodigingLink}</a><br />
            De partner logt via deze link in met DigiD en landt direct op de ondertekenpagina.
          </p>
        )}

        <div style={{ marginTop: "1rem" }}>
          <SubtleButton onClick={onTerug}>← Andere aanvraag</SubtleButton>
        </div>
      </div>
    );
  }

  const totaal = eval_?.afgeleideWaarden?.totaalBedrag;

  return (
    <div className="kaart">
      <SubtleButton onClick={onTerug}>← Andere aanvraag</SubtleButton>
      <Heading2>{definitie.titel}</Heading2>
      {melding && <div className={`melding ${melding.type}`}>{melding.tekst}</div>}

      {definitie.vragen.map((v) => {
        if (!zichtbaar(v)) return null;

        if (v.type === "groep") {
          const rijen = rijenVan(v.id);
          return (
            <div key={v.id}>
              <Heading3>{v.label}{v.verplicht ? " *" : ""}</Heading3>
              {foutVoor(v.id) && <div className="melding fout">{foutVoor(v.id)}</div>}
              <Table>
                <TableHeader>
                  <TableRow>
                    {v.subvragen?.map((s) => <TableHeaderCell key={s.id}>{s.label}</TableHeaderCell>)}
                    <TableHeaderCell></TableHeaderCell>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {rijen.map((r, i) => (
                    <TableRow key={i}>
                      {v.subvragen?.map((s) => (
                        <TableCell key={s.id}>
                          {inputVoor(s, String(r[s.id] ?? ""), (val) => setRijVeld(v.id, i, s.id, val))}
                        </TableCell>
                      ))}
                      <TableCell>
                        <SubtleButton onClick={() => verwijderRij(v.id, i)} title="Rij verwijderen">
                          ✕
                        </SubtleButton>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
              <SecondaryActionButton onClick={() => voegRijToe(v.id)}>+ Regel toevoegen</SecondaryActionButton>
            </div>
          );
        }

        return (
          <div className="veld" key={v.id}>
            <FormLabel htmlFor={v.id}>{v.label}{v.verplicht ? " *" : ""}</FormLabel>
            {inputVoor(v, String(antwoorden[v.id] ?? ""), (val) => setVeld(v.id, val), v.id)}
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

      <Heading3>Bijlagen</Heading3>
      <ul>
        {aanvraag.bijlagen.map((b) => (
          <li key={b.id}>
            {b.bestandsnaam} ({b.grootte} bytes){" "}
            <SubtleButton onClick={() => verwijderBijlage(b)}>verwijderen</SubtleButton>
          </li>
        ))}
      </ul>
      <input ref={fileRef} type="file" onChange={uploadBijlage} />

      <div style={{ display: "flex", gap: "0.6rem", marginTop: "1.25rem", flexWrap: "wrap" }}>
        <SecondaryActionButton onClick={bewaar}>Concept opslaan</SecondaryActionButton>
        <PrimaryActionButton disabled={!eval_?.indienbaar} onClick={() => indienen(false)}>
          Indienen
        </PrimaryActionButton>
        <PrimaryActionButton disabled={!eval_?.indienbaar} onClick={() => indienen(true)}>
          Ondertekenen met DigiD
        </PrimaryActionButton>
      </div>
      <p className="muted">
        Dit formulier is volledig opgebouwd uit de X-Works-template (wiz-vltmpl00-getocc). Validatie en
        eventuele berekening komen uit de regelevaluatie (vragenlijst-eval); indienen schrijft via de ACL een zaak naar X-Works.
      </p>
    </div>
  );
}
