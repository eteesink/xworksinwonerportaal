import { useEffect, useState } from "react";
import { api } from "../api/client";

export default function Contactgegevens() {
  const [telefoon, setTelefoon] = useState("");
  const [email, setEmail] = useState("");
  const [bezig, setBezig] = useState(false);
  const [melding, setMelding] = useState<{ type: "ok" | "fout"; tekst: string } | null>(null);

  useEffect(() => {
    api.getPersoon().then((p) => {
      setTelefoon(p.contactgegevens.telefoon ?? "");
      setEmail(p.contactgegevens.email ?? "");
    });
  }, []);

  async function opslaan(e: React.FormEvent) {
    e.preventDefault();
    setBezig(true);
    setMelding(null);
    try {
      await api.updateContactgegevens({ telefoon, email });
      setMelding({ type: "ok", tekst: "Uw contactgegevens zijn opgeslagen in X-Works." });
    } catch (err) {
      setMelding({ type: "fout", tekst: (err as Error).message });
    } finally {
      setBezig(false);
    }
  }

  return (
    <div className="kaart">
      <h2>Contactgegevens wijzigen</h2>
      {melding && <div className={`melding ${melding.type}`}>{melding.tekst}</div>}
      <form onSubmit={opslaan}>
        <div className="veld">
          <label htmlFor="telefoon">Telefoonnummer</label>
          <input id="telefoon" value={telefoon} onChange={(e) => setTelefoon(e.target.value)} />
        </div>
        <div className="veld">
          <label htmlFor="email">E-mailadres</label>
          <input id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>
        <button className="primair" type="submit" disabled={bezig}>
          {bezig ? "Opslaan…" : "Opslaan"}
        </button>
      </form>
      <p className="muted">
        Dit is het enige persoonsblok dat de burger in het oude portaal zelf kon muteren
        (X-Works CONTACTGEGEVENS, editable). De wijziging loopt via de X-Works ACL.
      </p>
    </div>
  );
}
