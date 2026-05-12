import { useEffect, useRef, useState } from "react";
import styles from "./AIPanel.module.css";
import { chatWithNote } from "../api/ai";
import ReactMarkdown from "react-markdown";
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
  selectedNote,
}: {
  atoms?: Atom[];
  extracting?: boolean;
  selectedNote?: any;
}) {
  const [tab, setTab] = useState<"atoms" | "chat">("atoms");

  // ===== CHAT =====
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState("");
  const [chatLoading, setChatLoading] = useState(false);
  const bottomRef = useRef<HTMLDivElement | null>(null);

  // auto scroll
  useEffect(() => {
    bottomRef.current?.scrollIntoView({
      behavior: "smooth",
    });
  }, [messages, chatLoading]);
  useEffect(() => {
    setMessages([]);
  }, [selectedNote?.id]);
  const streamText = async (text: string) => {
    let current = "";

    // tạo bubble rỗng trước
    setMessages((prev) => [
      ...prev,
      {
        role: "assistant",
        content: "",
      },
    ]);

    for (let i = 0; i < text.length; i++) {
      current += text[i];

      await new Promise((resolve) => setTimeout(resolve, 12));

      setMessages((prev) => {
        const updated = [...prev];

        updated[updated.length - 1] = {
          role: "assistant",
          content: current,
        };

        return updated;
      });
    }
  };
  // ===== SEND =====
  const handleSend = async () => {
    if (!input.trim()) return;
    if (!selectedNote) return;

    const userMessage: Message = {
      role: "user",
      content: input,
    };

    setMessages((prev) => [...prev, userMessage]);

    const userInput = input;

    setInput("");

    try {
      setChatLoading(true);

      const answer = await chatWithNote(selectedNote.id, {
        message: userInput,
        history: messages,
      });
      await streamText(answer);
    } finally {
      setChatLoading(false);
    }
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
                  <div className={styles.emptyIcon}>✨</div>
                  <div className={styles.emptyTitle}>
                    Ask AI about this note
                  </div>

                  <div className={styles.emptyDesc}>
                    Summarize concepts, explain ideas, or generate quizzes
                  </div>
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
                    <ReactMarkdown>{msg.content}</ReactMarkdown>
                  </div>
                </div>
              ))}
              {chatLoading && (
                <div className={styles.aiRow}>
                  <div className={styles.aiBubble}>AI is thinking...</div>
                </div>
              )}
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

              <button
                className={styles.sendBtn}
                onClick={handleSend}
                disabled={chatLoading}
              >
                {chatLoading ? "..." : "↑"}
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
