import { useEffect, useState } from "react";
import { Heading2, Paragraph } from "@utrecht/component-library-react";
import { api } from "../api/client";
import type { Persoon } from "../types";

function adresregel(p: Persoon, soort: string) {
  const a = p.adressen.find((x) => x.soort === soort);
  if (!a) return "—";
  return `${a.straat} ${a.huisnummer}, ${a.postcode} ${a.woonplaats}`;
}

export default function MijnGegevens() {
  const [persoon, setPersoon] = useState<Persoon | null>(null);
  const [fout, setFout] = useState<string | null>(null);

  useEffect(() => {
    api.getPersoon().then(setPersoon).catch((e) => setFout(e.message));
  }, []);

  if (fout) return <div className="melding fout">{fout}</div>;
  if (!persoon) return <Paragraph>Gegevens laden…</Paragraph>;

  return (
    <div className="kaart">
      <Heading2>Mijn gegevens</Heading2>
      <div className="rij"><span className="label">Naam</span><span>{persoon.voornaam} {persoon.achternaam}</span></div>
      <div className="rij"><span className="label">BSN</span><span>{persoon.bsn}</span></div>
      <div className="rij"><span className="label">Klantnummer</span><span>{persoon.klantnummer}</span></div>
      <div className="rij"><span className="label">Geboortedatum</span><span>{persoon.geboortedatum}</span></div>
      <div className="rij"><span className="label">Verblijfsadres</span><span>{adresregel(persoon, "G")}</span></div>
      <div className="rij"><span className="label">Correspondentieadres</span><span>{adresregel(persoon, "C")}</span></div>
      <div className="rij"><span className="label">Telefoon</span><span>{persoon.contactgegevens.telefoon || "—"}</span></div>
      <div className="rij"><span className="label">E-mail</span><span>{persoon.contactgegevens.email || "—"}</span></div>
      <div className="rij"><span className="label">Bankrekening</span><span>{persoon.bankrekening.iban} ({persoon.bankrekening.tenaamstelling})</span></div>
      <p className="muted">Inzage-gegevens komen uit X-Works (entiteit PERSOON, gescoped op uw BSN).</p>
    </div>
  );
}
