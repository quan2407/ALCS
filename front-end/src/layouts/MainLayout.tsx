import { Layout } from "antd";
import styles from "./MainLayout.module.css";
import Sidebar from "../components/Sidebar";

const { Header, Sider, Content } = Layout;

type Props = {
  children: React.ReactNode;
  notes: any[];
  selectedNote: any;
  onSelectNote: (note: any) => void;
  onCreateNote: () => void;
};

export default function MainLayout({
  children,
  notes,
  selectedNote,
  onSelectNote,
  onCreateNote,
}: Props) {
  return (
    <Layout className={styles.layout}>
      {/* SIDEBAR */}
      <Sider
        width={260}
        collapsedWidth={60}
        trigger={null}
        className={styles.sider}
      >
        <Sidebar
          notes={notes}
          selectedNote={selectedNote}
          onSelectNote={onSelectNote}
          onCreateNote={onCreateNote}
        />
      </Sider>

      {/* MAIN */}
      <Layout>
        <Header className={styles.header} />

        <Content className={styles.content}>
          <div className={styles.contentInner}>{children}</div>
        </Content>
      </Layout>
    </Layout>
  );
}
