import api from "./api";

export const getNotes = async () => {
  const res = await api.get("/notes");

  return {
    list: res.data.data.data,
    total: res.data.data.total_elements,
    page: res.data.data.current_page,
  };
};
export const createNote = (data: any) => {
  return api.post("/notes", data);
}