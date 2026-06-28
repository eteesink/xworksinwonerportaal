import { useEffect, useState } from "react";
import { useTranslation } from "react-i18next";
import MijnGegevens from "./pages/MijnGegevens";
import Contactgegevens from "./pages/Contactgegevens";
import MijnZaken from "./pages/MijnZaken";
import Aanvraag from "./pages/Aanvraag";
import Medeondertekenen from "./pages/Medeondertekenen";
import BeheerI18n from "./pages/BeheerI18n";
import MijnPlannenPagina from "./pages/MijnPlannen";
import LanguageSwitcher from "./components/LanguageSwitcher";
import { api } from "./api/client";
import { Heading2, Paragraph, PrimaryActionButton, SecondaryActionButton } from "@utrecht/component-library-react";

// Hoofdnavigatie volgens het Verius-ontwerp (docs/design/verius-design-system.md).
type Sectie = "home" | "plannen" | "gegevens" | "berichten";
// Subnavigatie binnen "Gegevens" — de huidige POC-functionaliteit.
type GegevensTab = "gegevens" | "contact" | "aanvraag" | "zaken";

const SECTIE_IDS: Sectie[] = ["home", "plannen", "gegevens", "berichten"];

export default function App() {
  const { i18n } = useTranslation();
  const [sectie, setSectie] = useState<Sectie>("home");
  const [menuOpen, setMenuOpen] = useState(false);

  // Standaardtaal van de ingelogde burger toepassen (server-side voorkeur, reist mee met de BSN).
  // localStorage stuurt de eerste paint; deze fetch corrigeert naar de opgeslagen voorkeur.
  useEffect(() => {
    api
      .getTaal()
      .then(({ taal }) => {
        if (taal && taal !== i18n.resolvedLanguage) i18n.changeLanguage(taal);
      })
      .catch(() => {
        /* geen voorkeur beschikbaar — val terug op detectie/fallback */
      });
  }, [i18n]);

  const params = new URLSearchParams(window.location.search);

  // Beheerinterface (alleen beheerders) via ?beheer=i18n.
  if (params.get("beheer") === "i18n") {
    return (
      <div className="app utrecht-theme">
        <AppHeader sectie={sectie} setSectie={() => {}} menuOpen={false} setMenuOpen={() => {}} />
        <main className="layout">
          <BeheerI18n />
        </main>
        <AppFooter />
      </div>
    );
  }

  // Deep-link: partner landt via de uitnodigingslink (/?cosign={token}) na DigiD-login
  // direct op de mede-ondertekenpagina (buiten de hoofd-shell).
  const cosignToken = params.get("cosign");
  if (cosignToken) {
    return (
      <div className="app utrecht-theme">
        <AppHeader sectie={sectie} setSectie={() => {}} menuOpen={false} setMenuOpen={() => {}} />
        <main className="layout">
          <Medeondertekenen token={cosignToken} />
        </main>
        <AppFooter />
      </div>
    );
  }

  return (
    <div className="app utrecht-theme">
      <AppHeader
        sectie={sectie}
        setSectie={(s) => {
          setSectie(s);
          setMenuOpen(false);
        }}
        menuOpen={menuOpen}
        setMenuOpen={setMenuOpen}
      />

      <Breadcrumb sectie={sectie} />

      <main className="layout">
        {sectie === "home" && <Home setSectie={setSectie} />}
        {sectie === "plannen" && <MijnPlannenPagina />}
        {sectie === "gegevens" && <Gegevens />}
        {sectie === "berichten" && <Berichten />}
      </main>

      <AppFooter />
    </div>
  );
}

function AppHeader({
  sectie,
  setSectie,
  menuOpen,
  setMenuOpen,
}: {
  sectie: Sectie;
  setSectie: (s: Sectie) => void;
  menuOpen: boolean;
  setMenuOpen: (b: boolean) => void;
}) {
  const { t } = useTranslation();
  return (
    <header className="app-header">
      <div className="brand-wordmark">
        <span>inwonerportaal</span>
        <span className="merk">verius</span>
      </div>

      <button
        className="menu-toggle"
        aria-expanded={menuOpen}
        aria-label="Menu"
        onClick={() => setMenuOpen(!menuOpen)}
      >
        ☰ Menu
      </button>

      <div className={"app-menu" + (menuOpen ? " open" : "")}>
        <nav className="app-nav">
          {SECTIE_IDS.map((id) => (
            <button
              key={id}
              className={"nav-link" + (sectie === id ? " actief" : "")}
              onClick={() => setSectie(id)}
            >
              {t(`nav.${id}`)}
            </button>
          ))}
        </nav>

        <div className="header-right">
          <LanguageSwitcher />
          <span className="usermenu">{t("header.loggedInDemo")} ▾</span>
        </div>
      </div>
    </header>
  );
}

function Breadcrumb({ sectie }: { sectie: Sectie }) {
  const { t } = useTranslation();
  if (sectie === "home") {
    return (
      <div className="breadcrumb">
        <span>{t("breadcrumb.home")}</span>
      </div>
    );
  }
  return (
    <div className="breadcrumb">
      <a href="#" onClick={(e) => e.preventDefault()}>
        {t("breadcrumb.home")}
      </a>
      <span className="sep">›</span>
      <span>{t(`nav.${sectie}`)}</span>
    </div>
  );
}

function AppFooter() {
  const { t } = useTranslation();
  return (
    <footer className="app-footer">
      <div className="merk">verius</div>
      <div className="muted" style={{ color: "rgba(255,255,255,0.7)" }}>
        {t("footer.tagline")}
      </div>
    </footer>
  );
}

function Home({ setSectie }: { setSectie: (s: Sectie) => void }) {
  const { t } = useTranslation();
  return (
    <>
      <div className="highlight">
        <Heading2>{t("home.welcomeTitle")}</Heading2>
        <Paragraph>{t("home.welcomeBody")}</Paragraph>
      </div>
      <div className="sectie-kop">
        <Heading2>{t("home.quickTo")}</Heading2>
      </div>
      <div className="kaart">
        <div style={{ display: "flex", flexWrap: "wrap", gap: "0.6rem" }}>
          <PrimaryActionButton onClick={() => setSectie("plannen")}>
            {t("nav.plannen")}
          </PrimaryActionButton>
          <SecondaryActionButton onClick={() => setSectie("gegevens")}>
            {t("gegevens.mijnGegevens")}
          </SecondaryActionButton>
          <SecondaryActionButton onClick={() => setSectie("berichten")}>
            {t("nav.berichten")}
          </SecondaryActionButton>
        </div>
      </div>
    </>
  );
}

function Berichten() {
  const { t } = useTranslation();
  return (
    <>
      <div className="sectie-kop">
        <Heading2>✉ {t("berichten.title")}</Heading2>
      </div>
      <div className="binnenkort">
        <Paragraph>{t("berichten.soon")}</Paragraph>
      </div>
    </>
  );
}

function Gegevens() {
  const { t } = useTranslation();
  const [tab, setTab] = useState<GegevensTab>("gegevens");
  return (
    <>
      <nav className="tabs">
        <button className={tab === "gegevens" ? "actief" : ""} onClick={() => setTab("gegevens")}>
          {t("gegevens.mijnGegevens")}
        </button>
        <button className={tab === "contact" ? "actief" : ""} onClick={() => setTab("contact")}>
          {t("gegevens.contactgegevens")}
        </button>
        <button className={tab === "aanvraag" ? "actief" : ""} onClick={() => setTab("aanvraag")}>
          {t("gegevens.aanvraagIndienen")}
        </button>
        <button className={tab === "zaken" ? "actief" : ""} onClick={() => setTab("zaken")}>
          {t("gegevens.mijnZaken")}
        </button>
      </nav>

      {tab === "gegevens" && <MijnGegevens />}
      {tab === "contact" && <Contactgegevens />}
      {tab === "aanvraag" && <Aanvraag />}
      {tab === "zaken" && <MijnZaken />}
    </>
  );
}
