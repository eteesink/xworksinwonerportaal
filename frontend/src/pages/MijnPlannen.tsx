import { Fragment, useEffect, useState, type ReactNode } from "react";
import {
  Heading2,
  Heading3,
  Paragraph,
  PrimaryActionButton,
  SecondaryActionButton,
  SubtleButton,
  FormLabel,
  Textbox,
  Textarea,
  Select,
  SelectOption,
} from "@utrecht/component-library-react";
import { api } from "../api/client";
import type { Plan } from "../types";

/**
 * Integraal Plan (baseline Epic 10), conform het Verius-ontwerp:
 * Plannenlijst → Plan (Afspraken/Hoofddoelen/Samenvatting) → Hoofddoel (Subdoelen) → Subdoel (Acties).
 * Eenvoudige interne navigatie (geen router); data komt via de BFF uit de stub.
 */
type View =
  | { naam: "lijst" }
  | { naam: "plan"; planId: string }
  | { naam: "hoofddoel"; planId: string; hoofddoelId: string }
  | { naam: "subdoel"; planId: string; hoofddoelId: string; subdoelId: string };

export default function MijnPlannen() {
  const [view, setView] = useState<View>({ naam: "lijst" });

  if (view.naam === "lijst") return <Plannenlijst open={(planId) => setView({ naam: "plan", planId })} />;
  return <PlanWerkblad view={view} setView={setView} />;
}

function Plannenlijst({ open }: { open: (planId: string) => void }) {
  const [plannen, setPlannen] = useState<Plan[] | null>(null);
  const [fout, setFout] = useState<string | null>(null);

  useEffect(() => {
    api.getPlannen().then(setPlannen).catch((e) => setFout(e.message));
  }, []);

  return (
    <>
      <div className="sectie-kop">
        <Heading2>◎ Mijn plannen</Heading2>
      </div>
      {fout && <div className="melding fout">{fout}</div>}
      {!plannen && !fout && <Paragraph>Laden…</Paragraph>}
      {plannen?.length === 0 && <div className="binnenkort">U heeft nog geen plannen.</div>}
      {plannen?.map((p) => (
        <button key={p.id} className="lijst-rij" onClick={() => open(p.id)}>
          <span>
            <span className="titel">{p.titel}</span>
            <span className="meta-regel">
              Laatste wijziging door {p.laatsteWijzigingDoor} ·{" "}
              {new Date(p.laatsteWijzigingOp).toLocaleDateString("nl-NL")}
            </span>
          </span>
          <span className="pijl">→</span>
        </button>
      ))}
    </>
  );
}

/** Laadt het volledige plan en toont het juiste sub-scherm; herlaadt na mutaties. */
function PlanWerkblad({ view, setView }: { view: View; setView: (v: View) => void }) {
  const planId = "planId" in view ? view.planId : "";
  const [plan, setPlan] = useState<Plan | null>(null);
  const [fout, setFout] = useState<string | null>(null);

  function herlaad() {
    api.getPlan(planId).then(setPlan).catch((e) => setFout(e.message));
  }
  useEffect(() => {
    herlaad();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [planId]);

  if (fout) return <div className="melding fout">{fout}</div>;
  if (!plan) return <Paragraph>Laden…</Paragraph>;

  const hoofddoel =
    "hoofddoelId" in view ? plan.hoofddoelen.find((h) => h.id === view.hoofddoelId) : undefined;
  const subdoel =
    hoofddoel && "subdoelId" in view ? hoofddoel.subdoelen.find((s) => s.id === view.subdoelId) : undefined;

  return (
    <>
      <Kruimels view={view} plan={plan} setView={setView} />
      {view.naam === "plan" && <PlanDetail plan={plan} setView={setView} herlaad={herlaad} />}
      {view.naam === "hoofddoel" && hoofddoel && (
        <HoofddoelDetail
          plan={plan}
          hoofddoelId={hoofddoel.id}
          titel={hoofddoel.titel}
          subdoelen={hoofddoel.subdoelen}
          setView={setView}
          herlaad={herlaad}
        />
      )}
      {view.naam === "subdoel" && hoofddoel && subdoel && (
        <SubdoelDetail
          plan={plan}
          hoofddoelId={hoofddoel.id}
          subdoel={subdoel}
          herlaad={herlaad}
        />
      )}
    </>
  );
}

function Kruimels({ view, plan, setView }: { view: View; plan: Plan; setView: (v: View) => void }) {
  const items: { label: string; v?: View }[] = [{ label: "Mijn plannen", v: { naam: "lijst" } }];
  if (view.naam !== "lijst") items.push({ label: plan.titel, v: { naam: "plan", planId: plan.id } });
  if (view.naam === "hoofddoel" || view.naam === "subdoel") {
    const h = plan.hoofddoelen.find((x) => x.id === (view as any).hoofddoelId);
    if (h)
      items.push({
        label: h.titel,
        v: { naam: "hoofddoel", planId: plan.id, hoofddoelId: h.id },
      });
  }
  if (view.naam === "subdoel") {
    const h = plan.hoofddoelen.find((x) => x.id === view.hoofddoelId);
    const s = h?.subdoelen.find((x) => x.id === view.subdoelId);
    if (s) items.push({ label: s.titel });
  }
  return (
    <div className="breadcrumb" style={{ marginLeft: 0, paddingLeft: 0 }}>
      {items.map((it, i) => (
        <span key={i}>
          {i > 0 && <span className="sep">›</span>}{" "}
          {it.v ? (
            <a
              href="#"
              onClick={(e) => {
                e.preventDefault();
                setView(it.v!);
              }}
            >
              {it.label}
            </a>
          ) : (
            <span>{it.label}</span>
          )}{" "}
        </span>
      ))}
    </div>
  );
}

function PlanDetail({
  plan,
  setView,
  herlaad,
}: {
  plan: Plan;
  setView: (v: View) => void;
  herlaad: () => void;
}) {
  const [dialoog, setDialoog] = useState<null | "afspraak" | "hoofddoel">(null);

  return (
    <>
      <Heading2>{plan.titel}</Heading2>
      <p className="muted">
        Laatste wijziging door {plan.laatsteWijzigingDoor} ·{" "}
        {new Date(plan.laatsteWijzigingOp).toLocaleString("nl-NL")}
      </p>

      <div className="sectie-kop">
        <Heading3>📅 Afspraken</Heading3>
        <SecondaryActionButton onClick={() => setDialoog("afspraak")}>
          Afspraak toevoegen +
        </SecondaryActionButton>
      </div>
      {plan.afspraken.length === 0 && <p className="muted">Geen afspraken.</p>}
      {plan.afspraken.map((a) => (
        <div className="kaart" key={a.id}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
            <strong>{a.titel}</strong>
            {!a.aanpasbaar && <span className="muted">via {a.met} · niet aanpasbaar</span>}
          </div>
          <div className="meta-regel">
            🗓 {new Date(a.datum).toLocaleDateString("nl-NL")} · {a.van}–{a.tot}
          </div>
          <div className="meta-regel">📍 {a.locatie} · met {a.met}</div>
        </div>
      ))}

      <div className="sectie-kop">
        <Heading3>◎ Hoofddoelen</Heading3>
        <SecondaryActionButton onClick={() => setDialoog("hoofddoel")}>
          Hoofddoel toevoegen +
        </SecondaryActionButton>
      </div>
      {plan.hoofddoelen.map((h) => (
        <button
          key={h.id}
          className="lijst-rij"
          onClick={() => setView({ naam: "hoofddoel", planId: plan.id, hoofddoelId: h.id })}
        >
          <span>
            <span className="titel">{h.titel}</span>
            <span className="meta-regel">
              {h.subdoelen.length} subdoel{h.subdoelen.length === 1 ? "" : "en"}
            </span>
          </span>
          <span className="pijl">→</span>
        </button>
      ))}

      <div className="sectie-kop">
        <Heading3>▦ Samenvatting</Heading3>
      </div>
      <div className="kaart">
        <p className="muted">
          {plan.afspraken.length} afspraken · {plan.hoofddoelen.length} hoofddoelen ·{" "}
          {plan.hoofddoelen.reduce((n, h) => n + h.subdoelen.length, 0)} subdoelen
        </p>
      </div>

      {dialoog === "afspraak" && (
        <AfspraakDialog
          planId={plan.id}
          sluit={() => setDialoog(null)}
          klaar={() => {
            setDialoog(null);
            herlaad();
          }}
        />
      )}
      {dialoog === "hoofddoel" && (
        <TitelDialog
          titel="Hoofddoel toevoegen"
          label="Wat wilt u bereiken?"
          opslaan={async (t) => api.voegHoofddoelToe(plan.id, t)}
          sluit={() => setDialoog(null)}
          klaar={() => {
            setDialoog(null);
            herlaad();
          }}
        />
      )}
    </>
  );
}

function HoofddoelDetail({
  plan,
  hoofddoelId,
  titel,
  subdoelen,
  setView,
  herlaad,
}: {
  plan: Plan;
  hoofddoelId: string;
  titel: string;
  subdoelen: Plan["hoofddoelen"][number]["subdoelen"];
  setView: (v: View) => void;
  herlaad: () => void;
}) {
  const [dialoog, setDialoog] = useState(false);
  const maxBereikt = subdoelen.length >= 5;

  return (
    <>
      <Heading2>{titel}</Heading2>
      <div className="sectie-kop">
        <Heading3>◎ Subdoelen</Heading3>
        <SecondaryActionButton disabled={maxBereikt} onClick={() => setDialoog(true)}>
          Subdoel toevoegen +
        </SecondaryActionButton>
      </div>
      {maxBereikt && <p className="muted">Maximaal 5 subdoelen bereikt.</p>}
      {subdoelen.map((s) => (
        <button
          key={s.id}
          className="lijst-rij"
          onClick={() =>
            setView({ naam: "subdoel", planId: plan.id, hoofddoelId, subdoelId: s.id })
          }
        >
          <span>
            <span className="titel">{s.titel}</span>
            <span className="meta-regel">
              Aangemaakt op {new Date(s.aangemaaktOp).toLocaleDateString("nl-NL")} door {s.aangemaaktDoor}
            </span>
          </span>
          <span className={s.acties.length ? "status-dot" : "muted"}>
            {s.acties.length ? `${s.acties.length} acties` : "Geen acties"}
          </span>
        </button>
      ))}

      <div className="sectie-kop">
        <Heading3>⚡ Mijn voortgang</Heading3>
      </div>
      <div className="kaart">
        <p className="muted">
          {subdoelen.reduce((n, s) => n + s.acties.filter((a) => a.gereed).length, 0)} van{" "}
          {subdoelen.reduce((n, s) => n + s.acties.length, 0)} acties afgerond.
        </p>
      </div>

      {dialoog && (
        <TitelDialog
          titel="Subdoel toevoegen"
          label="Omschrijf het subdoel"
          opslaan={async (t) => api.voegSubdoelToe(plan.id, hoofddoelId, t)}
          sluit={() => setDialoog(false)}
          klaar={() => {
            setDialoog(false);
            herlaad();
          }}
        />
      )}
    </>
  );
}

function SubdoelDetail({
  plan,
  hoofddoelId,
  subdoel,
  herlaad,
}: {
  plan: Plan;
  hoofddoelId: string;
  subdoel: Plan["hoofddoelen"][number]["subdoelen"][number];
  herlaad: () => void;
}) {
  const [dialoog, setDialoog] = useState(false);
  return (
    <>
      <Heading2>{subdoel.titel}</Heading2>
      <p className="muted">
        Aangemaakt op {new Date(subdoel.aangemaaktOp).toLocaleDateString("nl-NL")} door{" "}
        {subdoel.aangemaaktDoor}
      </p>
      <div className="sectie-kop">
        <Heading3>✓ Acties</Heading3>
        <SecondaryActionButton onClick={() => setDialoog(true)}>Actie toevoegen +</SecondaryActionButton>
      </div>
      {subdoel.acties.length === 0 && <p className="muted">Nog geen acties.</p>}
      {subdoel.acties.map((a) => (
        <div className="kaart" key={a.id}>
          <div style={{ display: "flex", justifyContent: "space-between", alignItems: "baseline" }}>
            <strong>{a.omschrijving}</strong>
            <span className={a.gereed ? "status-dot" : "muted"}>{a.gereed ? "afgerond" : "open"}</span>
          </div>
          <div className="meta-regel">{a.type === "herhalend" ? "🔁 herhalend" : "1× eenmalig"}</div>
        </div>
      ))}

      {dialoog && (
        <ActieDialog
          planId={plan.id}
          hoofddoelId={hoofddoelId}
          subdoelId={subdoel.id}
          sluit={() => setDialoog(false)}
          klaar={() => {
            setDialoog(false);
            herlaad();
          }}
        />
      )}
    </>
  );
}

// --- Dialogen ---------------------------------------------------------------

function Modal({ titel, sluit, children }: { titel: string; sluit: () => void; children: ReactNode }) {
  return (
    <div className="modal-overlay" onClick={sluit}>
      <div className="modal" onClick={(e) => e.stopPropagation()}>
        <div className="modal-kop">
          <Heading3>{titel}</Heading3>
          <button className="modal-sluit" aria-label="Sluiten" onClick={sluit}>
            ×
          </button>
        </div>
        {children}
      </div>
    </div>
  );
}

function TitelDialog({
  titel,
  label,
  opslaan,
  sluit,
  klaar,
}: {
  titel: string;
  label: string;
  opslaan: (titel: string) => Promise<unknown>;
  sluit: () => void;
  klaar: () => void;
}) {
  const [waarde, setWaarde] = useState("");
  const [bezig, setBezig] = useState(false);
  const [fout, setFout] = useState<string | null>(null);

  async function verzend() {
    if (!waarde.trim()) return;
    setBezig(true);
    try {
      await opslaan(waarde.trim());
      klaar();
    } catch (e) {
      setFout((e as Error).message);
      setBezig(false);
    }
  }

  return (
    <Modal titel={titel} sluit={sluit}>
      {fout && <div className="melding fout">{fout}</div>}
      <div className="veld">
        <FormLabel htmlFor="titel-veld">{label}</FormLabel>
        <Textbox id="titel-veld" value={waarde} onChange={(e) => setWaarde(e.target.value)} autoFocus />
      </div>
      <div className="modal-acties">
        <SubtleButton onClick={sluit}>Annuleren</SubtleButton>
        <PrimaryActionButton onClick={verzend} disabled={bezig || !waarde.trim()}>
          Opslaan
        </PrimaryActionButton>
      </div>
    </Modal>
  );
}

function ActieDialog({
  planId,
  hoofddoelId,
  subdoelId,
  sluit,
  klaar,
}: {
  planId: string;
  hoofddoelId: string;
  subdoelId: string;
  sluit: () => void;
  klaar: () => void;
}) {
  const [omschrijving, setOmschrijving] = useState("");
  const [type, setType] = useState("eenmalig");
  const [bezig, setBezig] = useState(false);
  const [fout, setFout] = useState<string | null>(null);

  async function verzend() {
    if (!omschrijving.trim()) return;
    setBezig(true);
    try {
      await api.voegActieToe(planId, hoofddoelId, subdoelId, omschrijving.trim(), type);
      klaar();
    } catch (e) {
      setFout((e as Error).message);
      setBezig(false);
    }
  }

  return (
    <Modal titel="Actie toevoegen" sluit={sluit}>
      {fout && <div className="melding fout">{fout}</div>}
      <div className="veld">
        <FormLabel htmlFor="actie-oms">Wat gaat u doen?</FormLabel>
        <Textbox id="actie-oms" value={omschrijving} onChange={(e) => setOmschrijving(e.target.value)} autoFocus />
      </div>
      <div className="veld">
        <FormLabel htmlFor="actie-type">Type</FormLabel>
        <Select id="actie-type" value={type} onChange={(e) => setType((e.target as HTMLSelectElement).value)}>
          <SelectOption value="eenmalig">Eenmalig</SelectOption>
          <SelectOption value="herhalend">Herhalend</SelectOption>
        </Select>
      </div>
      <div className="modal-acties">
        <SubtleButton onClick={sluit}>Annuleren</SubtleButton>
        <PrimaryActionButton onClick={verzend} disabled={bezig || !omschrijving.trim()}>
          Opslaan
        </PrimaryActionButton>
      </div>
    </Modal>
  );
}

function AfspraakDialog({
  planId,
  sluit,
  klaar,
}: {
  planId: string;
  sluit: () => void;
  klaar: () => void;
}) {
  const [stap, setStap] = useState(1);
  const [titel, setTitel] = useState("");
  const [datum, setDatum] = useState("");
  const [van, setVan] = useState("10:00");
  const [tot, setTot] = useState("10:30");
  const [locatie, setLocatie] = useState("Bel");
  const [met, setMet] = useState("");
  const [bezig, setBezig] = useState(false);
  const [fout, setFout] = useState<string | null>(null);

  const TYPES = [
    { waarde: "Bel", label: "Bellen: ik krijg een belletje" },
    { waarde: "Bij de gemeente", label: "Op locatie: bij de gemeente" },
    { waarde: "Thuis", label: "Bij mij thuis" },
  ];

  function volgende() {
    if (stap === 1 && !titel.trim()) {
      setFout("Vul een reden in.");
      return;
    }
    if (stap === 2 && !datum) {
      setFout("Kies een datum.");
      return;
    }
    setFout(null);
    setStap(stap + 1);
  }

  async function verzend() {
    setBezig(true);
    try {
      await api.voegAfspraakToe(planId, { titel: titel.trim(), datum, van, tot, locatie, met: met.trim() });
      klaar();
    } catch (e) {
      setFout((e as Error).message);
      setBezig(false);
    }
  }

  return (
    <Modal titel="Nieuwe afspraak toevoegen" sluit={sluit}>
      <Stepper stap={stap} stappen={["Reden en type", "Datum en tijd", "Samenvatting"]} />
      {fout && <div className="melding fout">{fout}</div>}

      {stap === 1 && (
        <>
          <div className="veld">
            <FormLabel htmlFor="af-reden">Waarom wilt u een afspraak inplannen?</FormLabel>
            <Textarea id="af-reden" rows={3} value={titel} onChange={(e) => setTitel(e.target.value)} autoFocus />
          </div>
          <div className="veld">
            <FormLabel>Kies het type afspraak</FormLabel>
            <div className="segment">
              {TYPES.map((tpe) => (
                <label key={tpe.waarde} className={locatie === tpe.waarde ? "gekozen" : ""}>
                  <input
                    type="radio"
                    name="afspraaktype"
                    value={tpe.waarde}
                    checked={locatie === tpe.waarde}
                    onChange={() => setLocatie(tpe.waarde)}
                  />
                  {tpe.label}
                </label>
              ))}
            </div>
          </div>
        </>
      )}

      {stap === 2 && (
        <>
          <div style={{ display: "flex", gap: "0.75rem", flexWrap: "wrap" }}>
            <div className="veld" style={{ flex: "1 1 150px" }}>
              <FormLabel htmlFor="af-datum">Datum</FormLabel>
              <Textbox id="af-datum" type="date" value={datum} onChange={(e) => setDatum(e.target.value)} />
            </div>
            <div className="veld" style={{ flex: "1 1 110px" }}>
              <FormLabel htmlFor="af-van">Van</FormLabel>
              <Textbox id="af-van" type="time" value={van} onChange={(e) => setVan(e.target.value)} />
            </div>
            <div className="veld" style={{ flex: "1 1 110px" }}>
              <FormLabel htmlFor="af-tot">Tot</FormLabel>
              <Textbox id="af-tot" type="time" value={tot} onChange={(e) => setTot(e.target.value)} />
            </div>
          </div>
          <div className="veld">
            <FormLabel htmlFor="af-met">Met wie (optioneel)</FormLabel>
            <Textbox id="af-met" value={met} onChange={(e) => setMet(e.target.value)} />
          </div>
        </>
      )}

      {stap === 3 && (
        <div className="kaart">
          <p><strong>Reden:</strong> {titel || "—"}</p>
          <p><strong>Type:</strong> {TYPES.find((x) => x.waarde === locatie)?.label}</p>
          <p>
            <strong>Datum:</strong>{" "}
            {datum ? new Date(datum).toLocaleDateString("nl-NL") : "—"} · {van}–{tot}
          </p>
          <p><strong>Met:</strong> {met || "—"}</p>
        </div>
      )}

      <div className="modal-acties">
        <SubtleButton onClick={sluit}>Annuleren</SubtleButton>
        <div style={{ display: "flex", gap: "0.6rem" }}>
          {stap > 1 && (
            <SecondaryActionButton
              onClick={() => {
                setFout(null);
                setStap(stap - 1);
              }}
            >
              Vorige
            </SecondaryActionButton>
          )}
          {stap < 3 && <PrimaryActionButton onClick={volgende}>Volgende</PrimaryActionButton>}
          {stap === 3 && (
            <PrimaryActionButton onClick={verzend} disabled={bezig}>
              Afspraak bevestigen
            </PrimaryActionButton>
          )}
        </div>
      </div>
    </Modal>
  );
}

function Stepper({ stap, stappen }: { stap: number; stappen: string[] }) {
  return (
    <div className="stepper">
      {stappen.map((s, i) => (
        <Fragment key={s}>
          {i > 0 && <span className="lijn" />}
          <span className={"stap" + (stap === i + 1 ? " actief" : "")}>
            <span className="bol">{i + 1}</span>
            {s}
          </span>
        </Fragment>
      ))}
    </div>
  );
}
