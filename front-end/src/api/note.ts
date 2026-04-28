import api from "./api";

export const getNotes = async () => {
  const res = await api.get("/notes");

  return {
    list: res.data.data.data,
    total: res.data.data.total_elements,
    page: res.data.data.current_page,
  };
};
export const createNote = async () => {
  const res = await api.post("/notes", {
    title: "",
    content: "",
  });

  return res.data.data;
};
export const updateNote = (id: number, data: any) => {
  return api.put(`/notes/${id}`, data);
};