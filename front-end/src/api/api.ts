import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api/v1",
  headers: {
    "Content-Type": "application/json",
  },
});

// ====== Request Interceptor======
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
    if (
    token &&
    !config.url?.includes("/auth/")
  ) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// ====== Response Interceptor======
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originRequest = error.config;
    if (error.response.status === 401 && !originRequest._retry) {
      originRequest._retry = true;
      try {
        const refreshToken = localStorage.getItem("refreshToken");
        if (!refreshToken) {
          window.location.href = "/login";
          return;
        }

        // gọi refresh token
        const res = await axios.post("http://localhost:8080/api/v1/auth/refresh-token", {
          refreshToken
        });
        const { access_token, refresh_token } = res.data.data;
        localStorage.setItem("accessToken", access_token);
        localStorage.setItem("refreshToken", refresh_token);
        originRequest.headers.Authorization = `Bearer ${access_token}`;
        return api.request(originRequest);
      } catch (err) {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);

export default api;