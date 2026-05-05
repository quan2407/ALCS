import { Layout, Dropdown, Button } from "antd";
import { MoreOutlined, DeleteOutlined, InboxOutlined } from "@ant-design/icons";
import styles from "./MainLayout.module.css";
import Sidebar from "../components/Sidebar";
import AIPanel from "../components/AIPanel";

const { Header, Sider, Content } = Layout;

type Props = {
  children: React.ReactNode;
  notes: any[];
  selectedNote: any;
  onSelectNote: (note: any) => void;
  onCreateNote: () => void;
  onArchiveNote: () => void;
  onDeleteNote: () => void;

  // 🔥 AI props
  onExtract?: () => void;
  extracting?: boolean;
  atoms?: any[];
};

export default function MainLayout({
  children,
  notes,
  selectedNote,
  onSelectNote,
  onCreateNote,
  onArchiveNote,
  onDeleteNote,
  onExtract,
  extracting,
  atoms = [],
}: Props) {
  const menuItems = [
    {
      key: "archive",
      label: (
        <div className={styles.menuItem}>
          <InboxOutlined />
          Archive
        </div>
      ),
    },
    {
      key: "delete",
      label: (
        <div className={`${styles.menuItem} ${styles.delete}`}>
          <DeleteOutlined />
          Delete
        </div>
      ),
    },
  ];

  return (
    <Layout className={styles.layout}>
      {/* SIDEBAR */}
      <Sider width={260} theme="light" className={styles.sider}>
        <Sidebar
          notes={notes || []}
          selectedNote={selectedNote}
          onSelectNote={onSelectNote}
          onCreateNote={onCreateNote}
        />
      </Sider>

      {/* MAIN */}
      <Layout>
        <Header className={styles.header}>
          <div className={styles.headerRight}>
            {/* 🔥 EXTRACT */}
            {selectedNote && (
              <Button
                className={styles.extractBtn}
                onClick={onExtract}
                loading={extracting}
                disabled={!selectedNote?.content}
              >
                ✨ Extract
              </Button>
            )}

            {/* MENU */}
            {selectedNote && (
              <Dropdown
                menu={{
                  items: menuItems,
                  onClick: ({ key }) => {
                    if (key === "archive") onArchiveNote();
                    if (key === "delete") onDeleteNote();
                  },
                }}
                trigger={["click"]}
                placement="bottomRight"
              >
                <MoreOutlined className={styles.moreIcon} />
              </Dropdown>
            )}
          </div>
        </Header>

        <Content className={styles.content}>
          <div className={styles.mainContainer}>
            {/* EDITOR */}
            <div className={styles.editorArea}>
              <div className={styles.contentInner}>{children}</div>
            </div>

            {/* 🔥 AI PANEL */}
            <div className={styles.aiPanel}>
              <AIPanel atoms={atoms} extracting={extracting} />
            </div>
          </div>
        </Content>
      </Layout>
    </Layout>
  );
}
