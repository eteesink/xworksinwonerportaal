import type {
  Aanvraag,
  AanvraagResultaat,
  Antwoorden,
  Bijlage,
  Contactgegevens,
  EvaluatieResultaat,
  CosignView,
  MedeondertekenVerzoek,
  Persoon,
  VragenlijstDefinitie,
  VragenlijstSamenvatting,
  Zaak,
} from "../types";

// /api wordt door Vite geproxyd naar de Spring-backend (zie vite.config.ts).
const BASE = "/api";

async function handle<T>(res: Response): Promise<T> {
  // Belangrijk: de API-laag doet NOOIT een page-reload of navigatie. Authentik
  // handelt de authenticatie aan de proxykant af; een verlopen sessie tonen we
  // als melding in het scherm i.p.v. een (verstorende) volledige herlaadactie.
  if (res.status === 401 || res.status === 403) {
    throw new Error("Niet ingelogd of sessie verlopen — ververs de pagina om opnieuw in te loggen.");
  }
  if (!res.ok) {
    let detail = `${res.status} ${res.statusText}`;
    try {
      const body = await res.json();
      if (body?.detail) detail = body.detail;
    } catch {
      /* geen JSON-body */
    }
    throw new Error(detail);
  }
  if (res.status === 204) return undefined as T;
  const contentType = res.headers.get("content-type") ?? "";
  if (!contentType.includes("application/json")) {
    throw new Error("Onverwachte (niet-JSON) respons van de server — mogelijk een loginpagina. Ververs de pagina.");
  }
  return res.json() as Promise<T>;
}

// POST/PUT/PATCH krijgen altijd een JSON-body (minstens {}) + Content-Type, ook als er
// geen payload is. Een bodyloze POST wordt door sommige WAF's/proxies geweigerd (403).
const json = (method: string, body?: unknown): RequestInit => {
  const heeftBody = body !== undefined || ["POST", "PUT", "PATCH"].includes(method);
  return {
    method,
    headers: heeftBody ? { "Content-Type": "application/json" } : undefined,
    body: heeftBody ? JSON.stringify(body ?? {}) : undefined,
  };
};

export const api = {
  // Inzage + persoon
  getPersoon: () => fetch(`${BASE}/persoon`).then((r) => handle<Persoon>(r)),
  getZaken: () => fetch(`${BASE}/zaken`).then((r) => handle<Zaak[]>(r)),
  updateContactgegevens: (cg: Contactgegevens) =>
    fetch(`${BASE}/persoon/contactgegevens`, json("PUT", cg)).then((r) => handle<void>(r)),

  // Aanvraag / vragenlijst
  getCatalogus: () =>
    fetch(`${BASE}/aanvragen/catalogus`).then((r) => handle<VragenlijstSamenvatting[]>(r)),
  getDefinitie: (type: string) =>
    fetch(`${BASE}/aanvragen/definities/${type}`).then((r) => handle<VragenlijstDefinitie>(r)),
  startAanvraag: (type: string) =>
    fetch(`${BASE}/aanvragen?type=${encodeURIComponent(type)}`, json("POST")).then((r) => handle<Aanvraag>(r)),
  evalueer: (id: string, antwoorden: Antwoorden) =>
    fetch(`${BASE}/aanvragen/${id}/evaluatie`, json("POST", antwoorden)).then((r) => handle<EvaluatieResultaat>(r)),
  bewaarConcept: (id: string, antwoorden: Antwoorden) =>
    fetch(`${BASE}/aanvragen/${id}/concept`, json("PUT", antwoorden)).then((r) => handle<Aanvraag>(r)),
  voegBijlageToe: (id: string, bestandsnaam: string, grootte: number) =>
    fetch(`${BASE}/aanvragen/${id}/bijlagen`, json("POST", { bestandsnaam, grootte })).then((r) => handle<Bijlage>(r)),
  verwijderBijlage: (id: string, bijlageId: string) =>
    fetch(`${BASE}/aanvragen/${id}/bijlagen/${bijlageId}`, json("DELETE")).then((r) => handle<void>(r)),
  dienIn: (id: string) =>
    fetch(`${BASE}/aanvragen/${id}/indienen`, json("POST")).then((r) => handle<AanvraagResultaat>(r)),
  ondertekenMetDigiD: (id: string) =>
    fetch(`${BASE}/aanvragen/${id}/ondertekenen`, json("POST")).then((r) => handle<AanvraagResultaat>(r)),
  breekAf: (id: string) =>
    fetch(`${BASE}/aanvragen/${id}`, json("DELETE")).then((r) => handle<void>(r)),

  // Mede-ondertekenen
  nodigPartnerUit: (id: string, partnerEmail: string, partnerBsn?: string) =>
    fetch(`${BASE}/aanvragen/${id}/medeondertekenaar`, json("POST", { partnerEmail, partnerBsn })).then(
      (r) => handle<MedeondertekenVerzoek>(r)
    ),
  getCosign: (token: string) =>
    fetch(`${BASE}/medeondertekenen/${token}`).then((r) => handle<CosignView>(r)),
  partnerOndertekent: (token: string) =>
    fetch(`${BASE}/medeondertekenen/${token}/ondertekenen`, json("POST")).then((r) =>
      handle<AanvraagResultaat>(r)
    ),
};
