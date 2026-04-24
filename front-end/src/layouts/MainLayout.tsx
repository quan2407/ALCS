import { Layout, Input } from "antd";
import {
  FileTextOutlined,
  SearchOutlined,
  ReloadOutlined,
  BulbOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  PlusOutlined,
} from "@ant-design/icons";
import { getNotes } from "../api/note";
import styles from "./MainLayout.module.css";
import { useEffect, useState } from "react";
import { Dropdown } from "antd";
import { UserOutlined, LogoutOutlined } from "@ant-design/icons";
const { Header, Sider, Content } = Layout;

export default function MainLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  const [collapsed, setCollapsed] = useState(false);
  const [notes, setNotes] = useState<any[]>([]);
  const [selectedNote, setSelectedNote] = useState<any>(null);
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
  useEffect(() => {
    getNotes().then((res) => {
      setNotes(res.list);
      if (res.list.length > 0) {
        setSelectedNote(res.list[0]);
      }
    });
  }, []);

  return (
    <Layout className={styles.layout}>
      {/* ===== SIDEBAR ===== */}
      <Sider
        width={260}
        collapsedWidth={60}
        collapsed={collapsed}
        trigger={null}
        className={styles.sider}
      >
        <div className={styles.sidebarInner}>
          {/* ===== TOP BAR (ALIGN HEADER) ===== */}
          <div className={styles.topBar}>
            <div
              className={styles.toggleBtn}
              onClick={() => setCollapsed(!collapsed)}
            >
              {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
            </div>

            {!collapsed && <div className={styles.logo}>SMART NOTE</div>}
          </div>

          {/* ===== SEARCH ===== */}
          {!collapsed && (
            <div className={styles.searchWrapper}>
              <Input
                placeholder="Search notes..."
                prefix={<SearchOutlined />}
              />
            </div>
          )}

          {/* ===== CORE MENU ===== */}
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

          {/* ===== NOTE LIST ===== */}
          <div className={styles.noteList}>
            <div className={styles.noteItem}>
              <PlusOutlined />
              {!collapsed && <span>New Note</span>}
            </div>

            {!collapsed && <div className={styles.sectionTitle}>Recent</div>}

            {!collapsed && (
              <>
                {notes.map((note) => (
                  <div
                    key={note.id}
                    className={`${styles.noteItem} ${
                      selectedNote?.id === note.id ? styles.active : ""
                    }`}
                    onClick={() => setSelectedNote(note)}
                  >
                    {note.title}
                  </div>
                ))}
              </>
            )}
          </div>

          {/* ===== PROFILE ===== */}
          <Dropdown
            menu={{
              items: menuItems,
              onClick: ({ key }) => {
                if (key === "profile") {
                  console.log("Go to profile");
                }
                if (key === "logout") {
                  console.log("Logout");
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
      </Sider>

      {/* ===== MAIN ===== */}
      <Layout>
        <Header className={styles.header} />

        <Content className={styles.content}>
          <div className={styles.contentInner}>
            {selectedNote ? (
              <>
                <h2>{selectedNote.title}</h2>
                <p>{selectedNote.content}</p>
              </>
            ) : (
              <div>Select a note</div>
            )}
          </div>
        </Content>
      </Layout>
    </Layout>
  );
}
