import api from "./api";

export const chatWithNote = async (
  noteId: number,
  data: {
    message: string;
    history: {
      role: string;
      content: string;
    }[];
  },
) => {
  const res = await api.post(`/notes/${noteId}/chat`, data);

  return res.data.data.answer;
};
