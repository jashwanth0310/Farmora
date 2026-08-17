import React, { createContext, useContext, useState, useEffect } from "react";

const SessionContext = createContext(null);

export function SessionProvider({ children }) {
  const [session, setSession] = useState(() => {
    const raw = localStorage.getItem("farmora_session");
    return raw ? JSON.parse(raw) : null;
  });

  useEffect(() => {
    if (session) {
      localStorage.setItem("farmora_session", JSON.stringify(session));
    } else {
      localStorage.removeItem("farmora_session");
    }
  }, [session]);

  return (
    <SessionContext.Provider value={{ session, setSession }}>
      {children}
    </SessionContext.Provider>
  );
}

export function useSession() {
  return useContext(SessionContext);
}
