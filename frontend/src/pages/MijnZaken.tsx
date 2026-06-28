import { useEffect, useState } from "react";
import {
  Heading2,
  Paragraph,
  Table,
  TableHeader,
  TableBody,
  TableRow,
  TableHeaderCell,
  TableCell,
} from "@utrecht/component-library-react";
import { api } from "../api/client";
import type { Zaak } from "../types";

export default function MijnZaken() {
  const [zaken, setZaken] = useState<Zaak[] | null>(null);
  const [fout, setFout] = useState<string | null>(null);

  useEffect(() => {
    api.getZaken().then(setZaken).catch((e) => setFout(e.message));
  }, []);

  if (fout) return <div className="melding fout">{fout}</div>;
  if (!zaken) return <Paragraph>Zaken laden…</Paragraph>;

  return (
    <div className="kaart">
      <Heading2>Mijn zaken</Heading2>
      {zaken.length === 0 ? (
        <Paragraph>U heeft geen lopende zaken.</Paragraph>
      ) : (
        <Table>
          <TableHeader>
            <TableRow>
              <TableHeaderCell>Zaaknummer</TableHeaderCell>
              <TableHeaderCell>Omschrijving</TableHeaderCell>
              <TableHeaderCell>Status</TableHeaderCell>
              <TableHeaderCell>Startdatum</TableHeaderCell>
            </TableRow>
          </TableHeader>
          <TableBody>
            {zaken.map((z) => (
              <TableRow key={z.zaaknummer}>
                <TableCell>{z.zaaknummer}</TableCell>
                <TableCell>{z.omschrijving}</TableCell>
                <TableCell>{z.status}</TableCell>
                <TableCell>{z.startdatum}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      )}
      <p className="muted">Inzage in zaken/dossiers uit X-Works (zaakgericht werken, ZS/ZKN).</p>
    </div>
  );
}
