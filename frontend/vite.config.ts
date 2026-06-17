import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";

// De frontend draait op :5173 en proxy't /api naar de Spring-backend op :8080.
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      "/api": "http://localhost:8080",
    },
  },
});
