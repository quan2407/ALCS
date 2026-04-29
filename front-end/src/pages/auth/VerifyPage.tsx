import { Button, Typography } from "antd";
import styles from "./Auth.module.css";
import { useNavigate, useSearchParams } from "react-router-dom";
import { useState, useRef, useEffect } from "react";
import { verifyApi, resendCodeApi } from "../../api/auth";

export default function VerifyPage() {
  const navigate = useNavigate();
  const [params] = useSearchParams();

  const email = params.get("email") || localStorage.getItem("verifyEmail");

  const [otp, setOtp] = useState(["", "", "", "", "", ""]);
  const [loading, setLoading] = useState(false);
  const [resendLoading, setResendLoading] = useState(false);
  const [error, setError] = useState("");

  const [cooldown, setCooldown] = useState(60);
  const [canResend, setCanResend] = useState(false);

  const inputsRef = useRef<(HTMLInputElement | null)[]>([]);

  // 🔥 guard
  useEffect(() => {
    if (!email) {
      navigate("/register");
    } else {
      localStorage.setItem("verifyEmail", email);
    }
  }, [email]);

  // ===== INIT COOLDOWN =====
  useEffect(() => {
    const saved = localStorage.getItem("otpCooldown");

    if (saved) {
      const remain = Number(saved) - Date.now();

      if (remain > 0) {
        setCooldown(Math.floor(remain / 1000));
        setCanResend(false);
      } else {
        setCanResend(true);
      }
    }
  }, []);

  // ===== COUNTDOWN =====
  useEffect(() => {
    if (cooldown <= 0) {
      setCanResend(true);
      return;
    }

    const timer = setTimeout(() => {
      setCooldown((prev) => prev - 1);
    }, 1000);

    return () => clearTimeout(timer);
  }, [cooldown]);

  useEffect(() => {
    if (!canResend) {
      localStorage.setItem("otpCooldown", String(Date.now() + cooldown * 1000));
    }
  }, [cooldown]);

  useEffect(() => {
    inputsRef.current[0]?.focus();
  }, []);

  const handleChangeOtp = (value: string, index: number) => {
    if (!/^[0-9]?$/.test(value)) return;

    const newOtp = [...otp];
    newOtp[index] = value;
    setOtp(newOtp);

    if (value && index < 5) {
      inputsRef.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (e: React.KeyboardEvent, index: number) => {
    if (e.key === "Backspace") {
      if (!otp[index] && index > 0) {
        inputsRef.current[index - 1]?.focus();
      }
    }
  };

  const handlePaste = (e: React.ClipboardEvent) => {
    const paste = e.clipboardData.getData("text").slice(0, 6);

    if (!/^\d+$/.test(paste)) return;

    const arr = paste.split("");
    setOtp(arr);
  };

  const handleVerify = async () => {
    if (!email) return;

    const code = otp.join("");

    if (code.length !== 6) {
      setError("Please enter full 6-digit code");
      return;
    }

    try {
      setLoading(true);
      setError("");

      const data = await verifyApi({ email, code });

      localStorage.setItem("accessToken", data.access_token);
      localStorage.setItem("refreshToken", data.refresh_token);
      localStorage.removeItem("verifyEmail");

      navigate("/app");
    } catch (err: any) {
      setError(err?.response?.data?.message || "Invalid code");
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    if (!email || !canResend) return;

    try {
      setResendLoading(true);

      await resendCodeApi({ email });

      setCooldown(60);
      setCanResend(false);
    } catch (err) {
      console.log(err);
    } finally {
      setResendLoading(false);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.box}>
        <Typography.Title level={2}>Verify your email</Typography.Title>

        <Typography.Text className={styles.subtitle}>
          Enter the code sent to {email}
        </Typography.Text>

        <div style={{ display: "flex", gap: 10, justifyContent: "center" }}>
          {otp.map((digit, i) => (
            <input
              key={i}
              ref={(el) => {
                inputsRef.current[i] = el;
              }}
              value={digit}
              onChange={(e) => handleChangeOtp(e.target.value, i)}
              onKeyDown={(e) => handleKeyDown(e, i)}
              maxLength={1}
              style={{
                width: 44,
                height: 48,
                textAlign: "center",
                fontSize: 20,
                borderRadius: 8,
                border: "1px solid #d9d9d9",
                background: "#fff",
                color: "#000",
              }}
            />
          ))}
        </div>

        {error && (
          <div style={{ color: "#ff4d4f", marginTop: 10 }}>{error}</div>
        )}

        <Button
          type="primary"
          block
          className={styles.button}
          loading={loading}
          onClick={handleVerify}
          style={{ marginTop: 16 }}
        >
          Verify
        </Button>

        <div style={{ marginTop: 12 }}>
          {resendLoading ? (
            <span style={{ color: "#999" }}>Sending...</span>
          ) : canResend ? (
            <span
              onClick={handleResend}
              style={{ color: "#1677ff", cursor: "pointer" }}
            >
              Resend code
            </span>
          ) : (
            <span style={{ color: "#999" }}>Resend in {cooldown}s</span>
          )}
        </div>
      </div>
    </div>
  );
}
