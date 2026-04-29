import api from "./api";

export const loginApi = async (email: string, password: string) => {
  const response = await api.post("/auth/authenticate", {
    email,
    password,
  });

  return response.data.data;
};

export const registerApi = async (data: {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
}) => {
  const response = await api.post("/auth/register", data);

  return response.data;
};

export const verifyApi = async (data: { email: string; code: string }) => {
  const response = await api.post("/auth/verify", data);

  return response.data.data;
};

export const resendCodeApi = async (data: { email: string }) => {
  const response = await api.post("/auth/resend-code", data);

  return response.data;
};
