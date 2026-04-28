import { Layout, Dropdown } from "antd";
import { MoreOutlined, DeleteOutlined, InboxOutlined } from "@ant-design/icons";
import styles from "./MainLayout.module.css";
import Sidebar from "../components/Sidebar";

const { Header, Sider, Content } = Layout;

type Props = {
  children: React.ReactNode;
  notes: any[];
  selectedNote: any;
  onSelectNote: (note: any) => void;
  onCreateNote: () => void;
  onArchiveNote: () => void;
  onDeleteNote: () => void;
};

export default function MainLayout({
  children,
  notes,
  selectedNote,
  onSelectNote,
  onCreateNote,
  onArchiveNote,
  onDeleteNote,
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
          {selectedNote && (
            <Dropdown
              menu={{
                items: menuItems,
                onClick: ({ key }) => {
                  if (!selectedNote) return;
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
        </Header>

        <Content className={styles.content}>
          <div className={styles.contentInner}>{children}</div>
        </Content>
      </Layout>
    </Layout>
  );
}
