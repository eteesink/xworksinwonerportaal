import { useEffect, useState } from "react";
import {
  Heading2,
  Heading3,
  Paragraph,
  PrimaryActionButton,
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHeaderCell,
  TableCell,
} from "@utrecht/component-library-react";
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
    <div className="kaart">
      <Heading2>Document mede-ondertekenen</Heading2>
      {melding && <div className={`melding ${melding.type}`}>{melding.tekst}</div>}
      {!view && !melding && <Paragraph>Uitnodiging laden…</Paragraph>}

      {view && (
        <>
          <div className="rij"><span className="label">Document</span><span>{view.titel}</span></div>
          <div className="rij"><span className="label">Document-hash</span><span>{view.documentHash}</span></div>
          <div className="rij"><span className="label">Status</span><span>{view.status}</span></div>

          <Heading3>Reeds ondertekend door</Heading3>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHeaderCell>Naam</TableHeaderCell>
                <TableHeaderCell>BSN</TableHeaderCell>
                <TableHeaderCell>Tijdstip</TableHeaderCell>
                <TableHeaderCell>Niveau</TableHeaderCell>
              </TableRow>
            </TableHeader>
            <TableBody>
              {view.reedsGetekendDoor.map((h, i) => (
                <TableRow key={i}>
                  <TableCell>{h.naam}</TableCell>
                  <TableCell>{h.bsn}</TableCell>
                  <TableCell>{h.tijdstip}</TableCell>
                  <TableCell>{h.assuranceNiveau}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>

          {klaar ? (
            <Paragraph>
              U heeft ondertekend. Het document is volledig getekend en teruggekoppeld aan het portaal.
            </Paragraph>
          ) : view.magTekenen ? (
            <>
              <Paragraph>
                Door te ondertekenen verklaart u akkoord met dit document. Uw DigiD-identiteit, het tijdstip en
                de document-hash worden vastgelegd (geverifieerde elektronische handtekening).
              </Paragraph>
              <PrimaryActionButton disabled={bezig} onClick={onderteken}>
                {bezig ? "Ondertekenen…" : "Ondertekenen met DigiD"}
              </PrimaryActionButton>
            </>
          ) : (
            <div className="melding fout">
              {view.redenNietTekenen ?? "U kunt dit document niet ondertekenen."}
            </div>
          )}
        </>
      )}
    </div>
  );
}
