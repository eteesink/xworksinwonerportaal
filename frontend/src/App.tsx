import { useState } from "react";
import MijnGegevens from "./pages/MijnGegevens";
import Contactgegevens from "./pages/Contactgegevens";
import MijnZaken from "./pages/MijnZaken";
import Aanvraag from "./pages/Aanvraag";
import Medeondertekenen from "./pages/Medeondertekenen";

type Tab = "gegevens" | "contact" | "aanvraag" | "zaken";

export default function App() {
  const [tab, setTab] = useState<Tab>("gegevens");

  // Deep-link: partner landt via de uitnodigingslink (/?cosign={token}) na DigiD-login
  // direct op de mede-ondertekenpagina.
  const cosignToken = new URLSearchParams(window.location.search).get("cosign");

  return (
    <>
      <header className="topbar">
        <div className="brand">
          <img className="logo" src="/logo-stipter.svg" alt="Stipter" />
          <span className="appnaam">Inwonerportaal</span>
        </div>
        <span className="burger">Ingelogd via DigiD · demo</span>
      </header>

      {cosignToken && <Medeondertekenen token={cosignToken} />}
      {!cosignToken && (
        <MainApp tab={tab} setTab={setTab} />
      )}
    </>
  );
}

function MainApp({ tab, setTab }: { tab: Tab; setTab: (t: Tab) => void }) {
  return (
    <>

      <main className="layout">
        <nav className="tabs">
          <button className={tab === "gegevens" ? "actief" : ""} onClick={() => setTab("gegevens")}>
            Mijn gegevens
          </button>
          <button className={tab === "contact" ? "actief" : ""} onClick={() => setTab("contact")}>
            Contactgegevens
          </button>
          <button className={tab === "aanvraag" ? "actief" : ""} onClick={() => setTab("aanvraag")}>
            Aanvraag indienen
          </button>
          <button className={tab === "zaken" ? "actief" : ""} onClick={() => setTab("zaken")}>
            Mijn zaken
          </button>
        </nav>

        {tab === "gegevens" && <MijnGegevens />}
        {tab === "contact" && <Contactgegevens />}
        {tab === "aanvraag" && <Aanvraag />}
        {tab === "zaken" && <MijnZaken />}
      </main>

      <footer className="footer">
        <span className="punt">●</span>
        <span>Stipter — Slimmer werken, beter zorgen · demo-inwonerportaal</span>
      </footer>
    </>
  );
}
