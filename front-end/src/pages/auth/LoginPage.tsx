import { Input, Button, Typography } from "antd";
import { GoogleOutlined } from "@ant-design/icons";
import styles from "./Auth.module.css";
import { Link, useNavigate } from "react-router-dom";
import { useState } from "react";
import { loginApi } from "../../api/auth";

export default function LoginPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);

  const [emailError, setEmailError] = useState(false);
  const [passwordError, setPasswordError] = useState(false);
  const [serverError, setServerError] = useState("");

  const navigate = useNavigate();

  const handleLogin = async () => {
    let hasError = false;

    setEmailError(false);
    setPasswordError(false);
    setServerError("");

    if (!email) {
      setEmailError(true);
      hasError = true;
    }

    if (!password) {
      setPasswordError(true);
      hasError = true;
    }

    if (hasError) return;

    try {
      setLoading(true);

      const data = await loginApi(email, password);
      const { access_token, refresh_token } = data;

      localStorage.setItem("accessToken", access_token);
      localStorage.setItem("refreshToken", refresh_token);

      navigate("/app");
    } catch (error: any) {
      const msg = error?.response?.data?.message || "Login failed";
      console.log("FULL ERROR:", error?.response?.data);
      if (msg.toLowerCase().includes("chưa được xác thực")) {
        localStorage.setItem("verifyEmail", email);
        navigate(`/verify?email=${email}`);
        return;
      }

      setServerError(msg);
      setEmailError(true);
      setPasswordError(true);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.box}>
        <div className={styles.logo}>🧠</div>

        <Typography.Title level={2} className={styles.title}>
          Your AI workspace.
        </Typography.Title>

        <Typography.Text className={styles.subtitle}>
          Log in to your Smart Note account
        </Typography.Text>

        <div className={styles.form}>
          <Input
            placeholder="Enter your email"
            className={styles.input}
            value={email}
            status={emailError ? "error" : ""}
            onChange={(e) => {
              setEmail(e.target.value);
              setEmailError(false);
              setServerError("");
            }}
          />

          <Input.Password
            placeholder="Enter your password"
            className={styles.input}
            value={password}
            status={passwordError ? "error" : ""}
            onChange={(e) => {
              setPassword(e.target.value);
              setPasswordError(false);
              setServerError("");
            }}
            onPressEnter={handleLogin}
          />

          {serverError && (
            <div style={{ color: "#ff4d4f", marginBottom: 10, fontSize: 13 }}>
              {serverError}
            </div>
          )}

          <Button
            type="primary"
            block
            className={styles.button}
            loading={loading}
            onClick={handleLogin}
          >
            Log in
          </Button>
        </div>

        <div className={styles.divider}>
          <span>or continue with</span>
        </div>

        <Button
          block
          className={styles.googleBtn}
          icon={<GoogleOutlined />}
          disabled
        >
          Continue with Google
        </Button>

        <div className={styles.signup}>
          New user? <Link to="/register">Sign up</Link>
        </div>
      </div>
    </div>
  );
}
