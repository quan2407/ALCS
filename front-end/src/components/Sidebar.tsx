import { Input } from "antd";
import {
  FileTextOutlined,
  SearchOutlined,
  ReloadOutlined,
  BulbOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  PlusOutlined,
} from "@ant-design/icons";
import styles from "./Sidebar.module.css";
import { useState } from "react";

type Props = {
  notes: any[];
  selectedNote: any;
  onSelectNote: (note: any) => void;
  onCreateNote: () => void;
};

export default function Sidebar({
  notes,
  selectedNote,
  onSelectNote,
  onCreateNote,
}: Props) {
  const [collapsed, setCollapsed] = useState(false);

  return (
    <div className={styles.sidebarInner}>
      {/* TOP */}
      <div className={styles.topBar}>
        <div
          className={styles.toggleBtn}
          onClick={() => setCollapsed(!collapsed)}
        >
          {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
        </div>

        {!collapsed && <div className={styles.logo}>SMART NOTE</div>}
      </div>

      {/* SEARCH */}
      {!collapsed && (
        <div className={styles.searchWrapper}>
          <Input placeholder="Search notes..." prefix={<SearchOutlined />} />
        </div>
      )}

      {/* MENU */}
      <div className={styles.coreMenu}>
        <div className={styles.menuItem}>
          <FileTextOutlined />
          {!collapsed && <span>Notes</span>}
        </div>

        <div className={styles.menuItem}>
          <ReloadOutlined />
          {!collapsed && <span>Review</span>}
        </div>

        <div className={styles.menuItem}>
          <BulbOutlined />
          {!collapsed && <span>Insights</span>}
        </div>
      </div>

      {/* NOTES */}
      <div className={styles.noteList}>
        <div className={styles.noteItem} onClick={onCreateNote}>
          <PlusOutlined />
          {!collapsed && <span>New Note</span>}
        </div>

        {!collapsed && <div className={styles.sectionTitle}>Recent</div>}

        {!collapsed &&
          notes.map((note) => (
            <div
              key={note.id}
              className={`${styles.noteItem} ${
                selectedNote?.id === note.id ? styles.active : ""
              }`}
              onClick={() => onSelectNote(note)}
            >
              {note.title}
            </div>
          ))}
      </div>
    </div>
  );
}
