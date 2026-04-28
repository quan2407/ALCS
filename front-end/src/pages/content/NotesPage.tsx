import { useEffect, useRef, useState } from "react";
import MainLayout from "../../layouts/MainLayout";
import { getNotes, createNote, updateNote } from "../../api/note";
import styles from "./NotesPage.module.css";
import { deleteNote, archiveNote } from "../../api/note";
import { Modal } from "antd";
export default function NotesPage() {
  const [notes, setNotes] = useState<any[]>([]);
  const [selectedNote, setSelectedNote] = useState<any>(null);

  const textareaRef = useRef<HTMLTextAreaElement | null>(null);

  // ===== LOAD =====
  useEffect(() => {
    getNotes().then((res) => {
      setNotes(res.list);
      if (res.list.length > 0) {
        setSelectedNote(res.list[0]);
      }
    });
  }, []);

  // ===== AUTO RESIZE (DÙNG CHUNG) =====
  const resizeTextarea = () => {
    if (!textareaRef.current) return;

    const el = textareaRef.current;
    el.style.height = "auto";
    el.style.height = el.scrollHeight + "px";
  };

  // ===== FIX BUG: reload không resize =====
  useEffect(() => {
    if (!selectedNote) return;

    // delay 1 tick để DOM render xong
    setTimeout(() => {
      resizeTextarea();
    }, 0);
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

      // sync sidebar
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

  const handleArchiveNote = () => {
    if (!selectedNote) return;

    Modal.confirm({
      title: "Archive this note?",
      content: "You can restore it later.",
      okText: "Archive",
      cancelText: "Cancel",

      onOk: async () => {
        await archiveNote(selectedNote.id);

        setNotes((prev) => prev.filter((n) => n.id !== selectedNote.id));
        setSelectedNote(null);
      },
    });
  };

  const handleDeleteNote = async () => {
    if (!selectedNote) return;
    Modal.confirm({
      title: "Delete this note?",
      content: "This action cannot be undone.",
      okText: "Delete",
      okType: "danger",
      cancelText: "Cancel",
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
    >
      {selectedNote ? (
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

              // resize realtime
              const el = e.target;
              el.style.height = "auto";
              el.style.height = el.scrollHeight + "px";
            }}
          />
        </div>
      ) : (
        <div style={{ padding: 40 }}>Select a note</div>
      )}
    </MainLayout>
  );
}
