// Datacontracten van de backend-API (spiegelt de ACL-modellen).

export interface Adres {
  straat: string;
  huisnummer: string;
  postcode: string;
  woonplaats: string;
  soort: string; // 'G' = verblijf, 'C' = correspondentie
}

export interface Contactgegevens {
  telefoon: string;
  email: string;
}

export interface Bankrekening {
  iban: string;
  tenaamstelling: string;
}

export interface Persoon {
  bsn: string;
  voornaam: string;
  achternaam: string;
  geboortedatum: string;
  klantnummer: string;
  adressen: Adres[];
  contactgegevens: Contactgegevens;
  bankrekening: Bankrekening;
}

export interface Zaak {
  zaaknummer: string;
  omschrijving: string;
  status: string;
  startdatum: string;
}

// --- Aanvraag / vragenlijst ---

export interface VraagDefinitie {
  id: string;
  label: string;
  type: "tekst" | "getal" | "bedrag" | "datum" | "keuze" | "groep";
  verplicht: boolean;
  opties: string[] | null;
  zichtbaarAls: string | null;
  subvragen: VraagDefinitie[] | null;
}

export interface VragenlijstDefinitie {
  type: string;
  titel: string;
  vragen: VraagDefinitie[];
}

export interface VragenlijstSamenvatting {
  type: string;
  titel: string;
}

export interface Bijlage {
  id: string;
  bestandsnaam: string;
  grootte: number;
}

export type Antwoorden = Record<string, unknown>;

export interface Aanvraag {
  id: string;
  type: string;
  titel: string;
  status: string;
  antwoorden: Antwoorden;
  bijlagen: Bijlage[];
  zaaknummer: string | null;
}

export interface Validatiefout {
  vraagId: string;
  melding: string;
}

export interface EvaluatieResultaat {
  afgeleideWaarden: Record<string, unknown>;
  zichtbaarheid: Record<string, boolean>;
  validaties: Validatiefout[];
  indienbaar: boolean;
}

export interface AanvraagResultaat {
  zaaknummer: string;
  status: string;
}

// --- Mede-ondertekenen (story 14408) ---

export interface Handtekening {
  bsn: string;
  naam: string;
  tijdstip: string;
  assuranceNiveau: string;
}

export interface MedeondertekenVerzoek {
  token: string;
  aanvraagId: string;
  titel: string;
  documentHash: string;
  partnerEmail: string;
  partnerBsn: string | null;
  status: string;
  verlooptOp: string;
  handtekeningen: Handtekening[];
}

export interface CosignView {
  aanvraagId: string;
  titel: string;
  documentHash: string;
  status: string;
  reedsGetekendDoor: Handtekening[];
  magTekenen: boolean;
  redenNietTekenen: string | null;
}
