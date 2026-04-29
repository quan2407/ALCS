import { Input, Button, Typography } from "antd";
import styles from "./Auth.module.css";
import { useState } from "react";
import { registerApi } from "../../api/auth";
import { useNavigate } from "react-router-dom";

export default function RegisterPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (key: string, value: string) => {
    setForm((prev) => ({ ...prev, [key]: value }));
    setError("");
  };

  const handleRegister = async () => {
    try {
      setLoading(true);
      await registerApi(form);

      navigate(`/verify?email=${form.email}`);
    } catch (err: any) {
      setError(err?.response?.data?.message || "Register failed");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.box}>
        <div className={styles.logo}>🧠</div>

        <Typography.Title level={2}>Create account</Typography.Title>

        <Typography.Text className={styles.subtitle}>
          Start using Smart Note
        </Typography.Text>

        <div className={styles.form}>
          <Input
            placeholder="First name"
            className={styles.input}
            onChange={(e) => handleChange("firstName", e.target.value)}
          />

          <Input
            placeholder="Last name"
            className={styles.input}
            onChange={(e) => handleChange("lastName", e.target.value)}
          />

          <Input
            placeholder="Email"
            className={styles.input}
            onChange={(e) => handleChange("email", e.target.value)}
          />

          <Input.Password
            placeholder="Password"
            className={styles.input}
            onChange={(e) => handleChange("password", e.target.value)}
          />

          {error && (
            <div style={{ color: "red", marginBottom: 10 }}>{error}</div>
          )}

          <Button
            type="primary"
            block
            className={styles.button}
            loading={loading}
            onClick={handleRegister}
          >
            Sign up
          </Button>
        </div>
      </div>
    </div>
  );
}
