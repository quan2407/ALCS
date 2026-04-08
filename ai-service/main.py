import sys
import io
import json
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from google import genai
from google.genai import types

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

class NoteRequest(BaseModel):
    content: str

API_KEY = "AIzaSyB4glTyhImMD6q3Sv9Z5-UyDSfOexoiW-A"
client = genai.Client(api_key=API_KEY)

app = FastAPI(title="ALCS AI - Knowledge Engineering Assistant")
#Tư duy của AI được định hướng để trích xuất 'Knowledge Atoms' từ nội dung ghi chú. Yêu cầu trả về chỉ là một mảng JSON với cấu trúc cụ thể.
#Knowledge Atom là đơn vị kiến thức nhỏ nhất, 
#độc lập và không thể chia nhỏ thêm nữa mà vẫn giữ nguyên được ý nghĩa trọn vẹn

#4 đặc tính quan trọng S.I.N.G:
#S - Self-contained: Mỗi Knowledge Atom phải là một đơn vị kiến thức hoàn chỉnh, có thể hiểu và sử dụng độc lập mà không cần tham chiếu đến các phần khác.
#I - Indivisible: Knowledge Atom không thể chia nhỏ hơn nữa mà vẫn giữ nguyên ý nghĩa. Nếu chia nhỏ hơn, nó sẽ mất đi tính toàn vẹn và ý nghĩa của kiến thức.
#N - Networkable: Các Knowledge Atom có thể được kết nối với nhau để tạo thành một mạng lưới kiến thức, cho phép người dùng dễ dàng truy cập và hiểu mối quan hệ giữa các đơn vị kiến thức.
#G - Granular: Knowledge Atom phải đủ nhỏ để dễ dàng quản lý và sử dụng

#Tại sao cần Knowledge Atoms? 
#1. Personalize Learning: Nếu người học đã biết 1 atom cụ thể nhưng chưa biết atom liên kết với nó thì hệ thống chỉ cần gợi ý cái họ thiếu
#2. Search and Retrieval: Khi người học cần tìm kiếm thông tin, hệ thống có thể nhanh chóng truy xuất các Knowledge Atom liên quan để cung cấp câu trả lời chính xác và đầy đủ.
#3. Knowledge Graph Construction: Các Knowledge Atom có thể được kết nối với nhau để xây dựng một mạng lưới kiến thức, giúp người học hiểu rõ hơn về mối quan hệ giữa các khái niệm và thông tin.
@app.post("/analyze")
async def analyze_text(request: NoteRequest):
    #Định hướng tư duy của AI để trích xuất 'Knowledge Atoms' từ nội dung ghi chú. Yêu cầu trả về chỉ là một mảng JSON với cấu trúc cụ thể.
    instructions = """
    Extract 'Knowledge Atoms' from the note. Return ONLY a JSON array.
    Structure: [{"name": "...", "description": "...", "tags": [], "complexity": "..."}]
    """
    try:
        response = client.models.generate_content(
            model="gemini-3-flash-preview",
            contents=request.content,
            config=types.GenerateContentConfig(
                system_instruction=instructions,
                response_mime_type="application/json"
            ),
        )
        
        if response.parsed:
            return response.parsed
        
        if response.text:
            return json.loads(response.text)
            
        return {"error": "AI returned empty content"}

    except Exception as e:
        print(f"Error detail: {str(e)}") 
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)