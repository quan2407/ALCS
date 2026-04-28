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
import { Dropdown } from "antd";
import { UserOutlined, LogoutOutlined } from "@ant-design/icons";type Props = {
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
  const menuItems = [
  {
    key: "profile",
    label: (
      <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
        <UserOutlined />
        Profile
      </div>
    ),
  },
  {
    type: "divider" as const,
  },
  {
    key: "logout",
    label: (
      <div style={{ display: "flex", alignItems: "center", gap: 8 }}>
        <LogoutOutlined />
        Log out
      </div>
    ),
  },
];
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
      {/* PROFILE */}
<Dropdown
  menu={{
    items: menuItems,
    onClick: ({ key }) => {
      if (key === "profile") {
        console.log("Go to profile");
      }
      if (key === "logout") {
        console.log("Logout");
        localStorage.clear();
        window.location.href = "/login";
      }
    },
  }}
  trigger={["click"]}
  placement="topLeft"
>
  <div className={styles.profile}>
    <div className={styles.avatar}>QN</div>

    {!collapsed && (
      <div className={styles.profileInfo}>
        <div className={styles.username}>Quân Nguyễn</div>
        <div className={styles.plan}>Go</div>
      </div>
    )}
  </div>
</Dropdown>
    </div>
  );
}
