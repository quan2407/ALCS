import api from "./api";

export const loginApi = async (email: string, password: string) => {
  const response = await api.post("/auth/authenticate", {
    email,
    password,
  });

  // trả luôn phần data bên trong cho gọn
  return response.data.data;
};