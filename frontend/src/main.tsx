import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
// NL Design System (Utrecht white-label) + Verius-thema, daarna de eigen app-frame-CSS.
import "@utrecht/design-tokens/dist/index.css";
import "@utrecht/component-library-css/dist/index.css";
import "./verius-theme.css";
import "./i18n";
import "./styles.css";

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
