import { useEffect, useState } from "react";
import MainLayout from "../../layouts/MainLayout";
import { getNotes, createNote } from "../../api/note";

export default function NotesPage() {
  const [notes, setNotes] = useState<any[]>([]);
  const [selectedNote, setSelectedNote] = useState<any>(null);

  useEffect(() => {
    getNotes().then((res) => {
      setNotes(res.list);
      if (res.list.length > 0) {
        setSelectedNote(res.list[0]);
      }
    });
  }, []);

  const handleCreateNote = async () => {
    const res = await createNote({
      title: "Untitled",
      content: "",
    });

    const newNote = res.data.data;

    setNotes((prev) => [newNote, ...prev]);
    setSelectedNote(newNote);
  };

  return (
    <MainLayout
      notes={notes}
      selectedNote={selectedNote}
      onSelectNote={setSelectedNote}
      onCreateNote={handleCreateNote}
    >
      {selectedNote ? (
        <>
          <h2>{selectedNote.title}</h2>
          <p>{selectedNote.content}</p>
        </>
      ) : (
        <div>Select a note</div>
      )}
    </MainLayout>
  );
}
