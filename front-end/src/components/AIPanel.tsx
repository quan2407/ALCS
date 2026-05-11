import { useEffect, useRef, useState } from "react";
import styles from "./AIPanel.module.css";

type Atom = {
  id: number;
  title: string;
  content: string;
  type: string;
};

type Message = {
  role: "user" | "assistant";
  content: string;
};

export default function AIPanel({
  atoms = [],
  extracting = false,
}: {
  atoms?: Atom[];
  extracting?: boolean;
}) {
  const [tab, setTab] = useState<"atoms" | "chat">("atoms");

  // ===== CHAT =====
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState("");

  const bottomRef = useRef<HTMLDivElement | null>(null);

  // auto scroll
  useEffect(() => {
    bottomRef.current?.scrollIntoView({
      behavior: "smooth",
    });
  }, [messages]);

  // ===== SEND =====
  const handleSend = () => {
    if (!input.trim()) return;

    const userMessage: Message = {
      role: "user",
      content: input,
    };

    setMessages((prev) => [...prev, userMessage]);

    const userInput = input;

    setInput("");

    // fake AI response
    setTimeout(() => {
      const aiMessage: Message = {
        role: "assistant",
        content: `AI response for: "${userInput}"`,
      };

      setMessages((prev) => [...prev, aiMessage]);
    }, 700);
  };

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
        {/* ===== ATOMS ===== */}
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
                <div className={styles.atomTitle}>✨ {atom.title}</div>

                <div className={styles.atomContent}>{atom.content}</div>

                <div className={styles.atomType}>{atom.type}</div>
              </div>
            ))}
          </>
        )}

        {/* ===== CHAT ===== */}
        {tab === "chat" && (
          <div className={styles.chatWrapper}>
            {/* messages */}
            <div className={styles.chatMessages}>
              {messages.length === 0 && (
                <div className={styles.chatEmpty}>
                  Ask AI about this note...
                </div>
              )}

              {messages.map((msg, index) => (
                <div
                  key={index}
                  className={`${styles.messageRow} ${
                    msg.role === "user" ? styles.userRow : styles.aiRow
                  }`}
                >
                  <div
                    className={`${styles.messageBubble} ${
                      msg.role === "user" ? styles.userBubble : styles.aiBubble
                    }`}
                  >
                    {msg.content}
                  </div>
                </div>
              ))}

              <div ref={bottomRef} />
            </div>

            {/* input */}
            <div className={styles.chatInputWrapper}>
              <textarea
                className={styles.chatInput}
                placeholder="Ask AI..."
                value={input}
                onChange={(e) => setInput(e.target.value)}
                onKeyDown={(e) => {
                  if (e.key === "Enter" && !e.shiftKey) {
                    e.preventDefault();
                    handleSend();
                  }
                }}
              />

              <button className={styles.sendBtn} onClick={handleSend}>
                ↑
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
