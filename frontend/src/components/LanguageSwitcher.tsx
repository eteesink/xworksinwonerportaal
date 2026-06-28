import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import { SUPPORTED_LANGS } from "../i18n";
import { api } from "../api/client";

// Labels voor bekende talen; onbekende (door beheer toegevoegde) talen tonen we als code.
const LABELS: Record<string, string> = {
  nl: "Nederlands",
  en: "English",
  de: "Deutsch",
  fr: "Français",
  ar: "العربية",
  uk: "Українська",
  tr: "Türkçe",
  pl: "Polski",
};

function label(code: string): string {
  return LABELS[code] ?? code.toUpperCase();
}

/**
 * Taalkeuze in de header. Wisselt de actieve taal direct én slaat de keuze op als
 * standaardtaal van de ingelogde burger (server-side). De talenlijst komt uit de backend,
 * zodat door de beheerder toegevoegde talen automatisch verschijnen.
 */
export default function LanguageSwitcher() {
  const { i18n } = useTranslation();
  const huidige = i18n.resolvedLanguage ?? "nl";
  const [talen, setTalen] = useState<string[]>(SUPPORTED_LANGS.map((l) => l.code));

  useEffect(() => {
    api
      .getTalen()
      .then(({ talen }) => {
        if (talen?.length) setTalen(talen);
      })
      .catch(() => {
        /* val terug op de gebundelde talen */
      });
  }, []);

  async function kies(taal: string) {
    await i18n.changeLanguage(taal); // direct wisselen + cachen in localStorage
    try {
      await api.setTaal(taal); // als standaard vastleggen bij de burger
    } catch {
      /* sessie staat al goed; alleen server-side opslaan faalde */
    }
  }

  return (
    <label className="lang">
      🌐
      <select aria-label="Taal / Language" value={huidige} onChange={(e) => kies(e.target.value)}>
        {talen.map((code) => (
          <option key={code} value={code}>
            {label(code)}
          </option>
        ))}
      </select>
    </label>
  );
}
