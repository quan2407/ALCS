import api from "./api";

export const chatWithNote = async (noteId: number, message: string) => {
  const res = await api.post(`/notes/${noteId}/chat`, {
    message,
  });

  return res.data.data.answer;
};
