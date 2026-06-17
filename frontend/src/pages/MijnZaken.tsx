import { useEffect, useState } from "react";
import { api } from "../api/client";
import type { Zaak } from "../types";

export default function MijnZaken() {
  const [zaken, setZaken] = useState<Zaak[] | null>(null);
  const [fout, setFout] = useState<string | null>(null);

  useEffect(() => {
    api.getZaken().then(setZaken).catch((e) => setFout(e.message));
  }, []);

  if (fout) return <div className="melding fout">{fout}</div>;
  if (!zaken) return <p className="muted">Zaken laden…</p>;

  return (
    <div className="kaart">
      <h2>Mijn zaken</h2>
      {zaken.length === 0 ? (
        <p className="muted">U heeft geen lopende zaken.</p>
      ) : (
        <table>
          <thead>
            <tr>
              <th>Zaaknummer</th>
              <th>Omschrijving</th>
              <th>Status</th>
              <th>Startdatum</th>
            </tr>
          </thead>
          <tbody>
            {zaken.map((z) => (
              <tr key={z.zaaknummer}>
                <td>{z.zaaknummer}</td>
                <td>{z.omschrijving}</td>
                <td>{z.status}</td>
                <td>{z.startdatum}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
      <p className="muted">Inzage in zaken/dossiers uit X-Works (zaakgericht werken, ZS/ZKN).</p>
    </div>
  );
}
