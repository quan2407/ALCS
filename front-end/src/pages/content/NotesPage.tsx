import { useEffect, useRef, useState } from "react";
import MainLayout from "../../layouts/MainLayout";
import {
  getNotes,
  createNote,
  updateNote,
  getAtoms,
  extractNote,
} from "../../api/note";
import styles from "./NotesPage.module.css";
import { deleteNote, archiveNote } from "../../api/note";
import { Modal } from "antd";
export default function NotesPage() {
  const [notes, setNotes] = useState<any[]>([]);
  const [selectedNote, setSelectedNote] = useState<any>(null);

  const textareaRef = useRef<HTMLTextAreaElement | null>(null);
  const [atoms, setAtoms] = useState<any[]>([]);
  const [extracting, setExtracting] = useState(false);
  const handleExtract = async () => {
    if (!selectedNote) return;

    try {
      setExtracting(true);

      // 1. trigger AI
      await extractNote(selectedNote.id);

      // 2. get atoms
      const rawAtoms = await getAtoms(selectedNote.id);

      // 3. map UI
      const mapped = rawAtoms.map((a: any) => ({
        id: a.id,
        title: a.title,
        content: a.content,
        type: a.type,
      }));

      setAtoms(mapped);
    } finally {
      setExtracting(false);
    }
  };
  // ===== LOAD =====
  useEffect(() => {
    getNotes().then((res) => {
      setNotes(res.list);
      if (res.list.length > 0) {
        setSelectedNote(res.list[0]);
      }
    });
  }, []);

  // ===== AUTO RESIZE =====
  const resizeTextarea = () => {
    if (!textareaRef.current) return;
    const el = textareaRef.current;
    el.style.height = "auto";
    el.style.height = el.scrollHeight + "px";
  };

  useEffect(() => {
    if (!selectedNote) return;
    setTimeout(resizeTextarea, 0);
  }, [selectedNote]);

  // ===== CREATE =====
  const handleCreateNote = async () => {
    const newNote = await createNote();
    setNotes((prev) => [newNote, ...prev]);
    setSelectedNote(newNote);
  };

  // ===== CHANGE =====
  const handleChange = (field: string, value: string) => {
    setSelectedNote((prev: any) => {
      const updated = { ...prev, [field]: value };

      setNotes((prevNotes) =>
        prevNotes.map((n) =>
          n.id === updated.id ? { ...n, [field]: value } : n,
        ),
      );

      return updated;
    });
  };

  // ===== AUTOSAVE =====
  useEffect(() => {
    if (!selectedNote) return;

    const timeout = setTimeout(() => {
      updateNote(selectedNote.id, {
        title: selectedNote.title || "",
        content: selectedNote.content || "",
      });
    }, 800);

    return () => clearTimeout(timeout);
  }, [selectedNote]);

  // ===== ACTIONS =====
  const handleArchiveNote = () => {
    if (!selectedNote) return;

    Modal.confirm({
      title: "Archive this note?",
      content: "You can restore it later.",
      okText: "Archive",
      onOk: async () => {
        await archiveNote(selectedNote.id);
        setNotes((prev) => prev.filter((n) => n.id !== selectedNote.id));
        setSelectedNote(null);
      },
    });
  };

  const handleDeleteNote = () => {
    if (!selectedNote) return;

    Modal.confirm({
      title: "Delete this note?",
      okType: "danger",
      onOk: async () => {
        await deleteNote(selectedNote.id);
        setNotes((prev) => prev.filter((n) => n.id !== selectedNote.id));
        setSelectedNote(null);
      },
    });
  };

  return (
    <MainLayout
      notes={notes}
      selectedNote={selectedNote}
      onSelectNote={setSelectedNote}
      onCreateNote={handleCreateNote}
      onArchiveNote={handleArchiveNote}
      onDeleteNote={handleDeleteNote}
      onExtract={handleExtract}
      extracting={extracting}
      atoms={atoms}
    >
      {!selectedNote ? (
        <div style={{ padding: 40, color: "#888" }}>Select a note</div>
      ) : (
        <div className={styles.editor}>
          {/* TITLE */}
          <input
            className={styles.titleInput}
            placeholder="Untitled"
            value={selectedNote.title || ""}
            onChange={(e) => handleChange("title", e.target.value)}
          />

          {/* CONTENT */}
          <textarea
            ref={textareaRef}
            className={styles.contentInput}
            value={selectedNote.content || ""}
            placeholder="Start writing your note..."
            onChange={(e) => {
              handleChange("content", e.target.value);

              const el = e.target;
              el.style.height = "auto";
              el.style.height = el.scrollHeight + "px";
            }}
          />
        </div>
      )}
    </MainLayout>
  );
}
