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
import styles from "./MainLayout.module.css";
import { useState } from "react";
import { Dropdown } from "antd";
import { UserOutlined, LogoutOutlined } from "@ant-design/icons";
const { Header, Sider, Content } = Layout;

export default function MainLayout({ children }: { children: React.ReactNode }) {
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

            {!collapsed && (
              <div className={styles.logo}>SMART NOTE</div>
            )}
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

            {!collapsed && (
              <div className={styles.sectionTitle}>Recent</div>
            )}

            {!collapsed && (
              <>
                <div className={styles.noteItem}>Vấn đề không kéo đủ USDT</div>
                <div className={styles.noteItem}>Thành phố Pripyat tham quan</div>
                <div className={styles.noteItem}>Ký hiệu trên biểu đồ</div>
                <div className={styles.noteItem}>Greeting exchange</div>
                <div className={styles.noteItem}>So sánh ChatGPT Free Go</div>
                <div className={styles.noteItem}>Không được trứng game</div>
                <div className={styles.noteItem}>Giúp đỡ và hỗ trợ</div>
                <div className={styles.noteItem}>Giao tiếp ngắn gọn</div>
                <div className={styles.noteItem}>GPT phiên bản 5.2</div>
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
            {children}
          </div>
        </Content>
      </Layout>

    </Layout>
  );
}