import axios from "axios";

// Instance chính cho app
const api = axios.create({
  baseURL: "http://localhost:8080/api/v1",
  headers: { "Content-Type": "application/json" },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  // Chỉ đính kèm token nếu KHÔNG PHẢI request auth
  if (token && !config.url?.includes("/auth/")) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

let isRefreshing = false;
let pendingRequests: any[] = [];

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originRequest = error.config;

    if ((error.response?.status === 401 || error.response?.status === 403) && !originRequest._retry) {
      if (isRefreshing) {
        return new Promise((resolve) => {
          pendingRequests.push((token: string) => {
            originRequest.headers.Authorization = `Bearer ${token}`;
            resolve(api(originRequest));
          });
        });
      }

      originRequest._retry = true;
      isRefreshing = true;

      const refreshToken = localStorage.getItem("refreshToken");
      console.log("Attempting token refresh with refreshToken:", refreshToken);
      if (!refreshToken) {
        localStorage.clear();
        window.location.href = "/login";
        return Promise.reject(error);
      }

      try {
        // KHÔNG dùng instance, dùng axios gốc để đảm bảo Header sạch 100%
        const res = await axios.post("http://localhost:8080/api/v1/auth/refresh-token", 
          { refresh_token: refreshToken }, 
          { 
            headers: { 
                "Content-Type": "application/json",
                "Authorization": "" // Ép header này trống để Filter Backend không xử lý
            } 
          }
        );

        // Kiểm tra đúng tên field Backend trả về (snake_case hay camelCase)
        const { access_token, refresh_token } = res.data.data;

        localStorage.setItem("accessToken", access_token);
        localStorage.setItem("refreshToken", refresh_token);

        pendingRequests.forEach((cb) => cb(access_token));
        pendingRequests = [];

        originRequest.headers.Authorization = `Bearer ${access_token}`;
        return api(originRequest); 

      } catch (err) {
        localStorage.clear();
        // window.location.href = "/login";
        return Promise.reject(err);
      } finally {
        isRefreshing = false;
      }
    }
    return Promise.reject(error);
  }
);

export default api;