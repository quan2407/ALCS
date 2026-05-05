import { useState } from "react";
import styles from "./AIPanel.module.css";

type Atom = {
  id: number;
  title: string;
  content: string;
  type: string;
};

export default function AIPanel({
  atoms = [],
  extracting = false,
}: {
  atoms?: Atom[];
  extracting?: boolean;
}) {
  const [tab, setTab] = useState<"atoms" | "chat">("atoms");

  return (
    <div className={styles.panel}>
      {/* TABS */}
      <div className={styles.tabs}>
        <button
          className={tab === "atoms" ? styles.active : ""}
          onClick={() => setTab("atoms")}
        >
          Atoms
        </button>

        <button
          className={tab === "chat" ? styles.active : ""}
          onClick={() => setTab("chat")}
        >
          Chat
        </button>
      </div>

      {/* BODY */}
      <div className={styles.body}>
        {tab === "atoms" && (
          <>
            {extracting && <div className={styles.loading}>Extracting...</div>}

            {!extracting && atoms.length === 0 && (
              <div className={styles.empty}>
                <div className={styles.emptyTitle}>No atoms yet</div>
                <div className={styles.emptyDesc}>
                  Click Extract to generate knowledge
                </div>
              </div>
            )}

            {atoms.map((atom) => (
              <div key={atom.id} className={styles.atom}>
                <div className={styles.atomTitle}>{atom.title}</div>
                <div className={styles.atomContent}>{atom.content}</div>
                <div className={styles.atomType}>{atom.type}</div>
              </div>
            ))}
          </>
        )}

        {tab === "chat" && <div>Chat coming soon...</div>}
      </div>
    </div>
  );
}
