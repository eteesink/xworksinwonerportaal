import i18n from "i18next";
import { initReactI18next } from "react-i18next";
import LanguageDetector from "i18next-browser-languagedetector";
import HttpBackend from "i18next-http-backend";
import nl from "./locales/nl.json";
import en from "./locales/en.json";

/**
 * Talen die altijd beschikbaar zijn als offline fallback. De feitelijke set talen
 * wordt beheerd in de backend (TranslationStore) en kan via de beheerinterface
 * worden uitgebreid; de switcher haalt de actuele lijst op uit {@code GET /api/i18n}.
 */
export const SUPPORTED_LANGS = [
  { code: "nl", label: "Nederlands" },
  { code: "en", label: "English" },
] as const;

export const LANG_STORAGE_KEY = "voorkeurstaal";

i18n
  .use(HttpBackend)
  .use(LanguageDetector)
  .use(initReactI18next)
  .init({
    // Gebundelde vertalingen als initial/offline fallback; de http-backend laadt de
    // actuele (door de beheerder bijgewerkte) bundels uit de BFF en voegt ze samen.
    resources: {
      nl: { translation: nl },
      en: { translation: en },
    },
    partialBundledLanguages: true,
    backend: {
      // Wordt door Vite/Authentik geproxyd naar de Spring-backend.
      loadPath: "/api/i18n/{{lng}}",
    },
    fallbackLng: "nl",
    load: "languageOnly", // "en-US" → "en", zodat de backend-bundel matcht
    // Platte, dotted sleutels (bv. "nav.home") — geen geneste lookup.
    keySeparator: false,
    nsSeparator: false,
    detection: {
      order: ["localStorage", "navigator"],
      caches: ["localStorage"],
      lookupLocalStorage: LANG_STORAGE_KEY,
    },
    interpolation: { escapeValue: false },
  });

export default i18n;
