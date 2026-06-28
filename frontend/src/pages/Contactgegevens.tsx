import { useEffect, useState } from "react";
import { Heading2, FormLabel, Textbox, PrimaryActionButton } from "@utrecht/component-library-react";
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
      <Heading2>Contactgegevens wijzigen</Heading2>
      {melding && <div className={`melding ${melding.type}`}>{melding.tekst}</div>}
      <form onSubmit={opslaan}>
        <div className="veld">
          <FormLabel htmlFor="telefoon">Telefoonnummer</FormLabel>
          <Textbox id="telefoon" value={telefoon} onChange={(e) => setTelefoon(e.target.value)} />
        </div>
        <div className="veld">
          <FormLabel htmlFor="email">E-mailadres</FormLabel>
          <Textbox id="email" type="email" value={email} onChange={(e) => setEmail(e.target.value)} />
        </div>
        <PrimaryActionButton type="submit" disabled={bezig}>
          {bezig ? "Opslaan…" : "Opslaan"}
        </PrimaryActionButton>
      </form>
      <p className="muted">
        Dit is het enige persoonsblok dat de burger in het oude portaal zelf kon muteren
        (X-Works CONTACTGEGEVENS, editable). De wijziging loopt via de X-Works ACL.
      </p>
    </div>
  );
}
