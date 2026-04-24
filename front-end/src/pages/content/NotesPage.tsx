import { useEffect, useState } from "react";
import { getNotes } from "../../api/note";

export default function NotesPage() {
  const [notes, setNotes] = useState<any[]>([]);

  useEffect(() => {
  getNotes()
    .then((data) => {
      setNotes(data.list);
    })
    .catch(console.log);
}, []);

  return (
    <div>
      <h2>My Notes</h2>

      {notes.map((n: any) => (
        <div key={n.id}>{n.title}</div>
      ))}
    </div>
  );
}