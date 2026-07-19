# Frontend Architecture

- Runtime: React + TypeScript
- Build: Vite
- Routing: React Router
- State: Redux Toolkit
- HTTP: Axios
- Direction: RTL by default
- Organization: feature-first, with shared infrastructure under app/components/services/store/types.

Frontend is intentionally outside Maven. Backend remains a single Maven module.
