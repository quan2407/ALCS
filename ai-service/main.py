import sys
import io
import json
import os
import numpy as np
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException, Header,Depends,Security
from pydantic import BaseModel
from google import genai
from google.genai import types

load_dotenv()  # Load environment variables from .env file

INTERNAL_SECRET = os.getenv("ALCS_INTERNAL_SECRET","").strip()

sys.stdout = io.TextIOWrapper(sys.stdout.buffer, encoding='utf-8')

class NoteRequest(BaseModel):
    content: str
API_KEY = os.getenv("GEMINI_API_KEY")

client = genai.Client(api_key=API_KEY)

app = FastAPI(title="ALCS AI - Knowledge Engineering Assistant")

async def verify_internal_auth(x_internal_token: str = Header(None,alias="X-ALCS-Internal-Token")):
    if not x_internal_token or x_internal_token != INTERNAL_SECRET:
        raise HTTPException(status_code=403, detail="Forbidden: Invalid or missing internal security token.")
    return x_internal_token

#--- Tạo embeddings cho nội dung ghi chú ---
#Sử dụng model "text-embedding-004" để tạo vector biểu diễn cho nội dung ghi chú, giúp so sánh và loại bỏ các Knowledge Atoms tương tự nhau dựa trên độ tương đồng cosine.
def get_embeddings(text: str):
    try:
        # Sử dụng model mà script test vừa liệt kê
        result = client.models.embed_content(
            model="models/gemini-embedding-2-preview", 
            contents=text,
            config=types.EmbedContentConfig(task_type="RETRIEVAL_DOCUMENT")
        )
        return result.embeddings[0].values
    except Exception as e:
        print(f"Lỗi với gemini-embedding-2: {e}")
        # Fallback sang bản ổn định 001
        try:
            result = client.models.embed_content(
                model="models/gemini-embedding-001",
                contents=text
            )
            return result.embeddings[0].values
        except:
            return None
#--- Tính toán độ tương đồng cosine giữa hai vector ---
#--- Dùng cosine để tính góc giữa 2 vector
#--- Góc bằng 0 độ -> cosine similarity = 1 (rất giống nhau)
#--- Góc bằng 90 độ -> cosine similarity = 0 (không liên quan)
def cosine_similarity(vec1, vec2):
    return np.dot(vec1, vec2) / (np.linalg.norm(vec1) * np.linalg.norm(vec2))

def semantic_deduplication(atoms, threshold=0.90):
    if not atoms: return []
    
    # 1. Lấy Vector cho từng Atom
    for atom in atoms:
        text_to_embed = f"{atom.get('title', '')} {atom.get('content', '')}"
        atom['vector'] = get_embeddings(text_to_embed)

    unique_atoms = []
    for atom in atoms:
        if atom.get('vector') is None:
            unique_atoms.append(atom)
            continue
            
        is_duplicate = False
        for i in range(len(unique_atoms)):
            if unique_atoms[i].get('vector') is None: continue
            
            # Tính độ tương đồng Cosine
            similarity = cosine_similarity(atom['vector'], unique_atoms[i]['vector'])
            
            # Log ra để Quân theo dõi "bộ não" AI đang nghĩ gì
            print(f"Check: [{atom.get('title')[:20]}...] vs [{unique_atoms[i].get('title')[:20]}...] | Sim: {similarity:.4f}")

            if similarity > threshold:
                # Nếu trùng, giữ cái quan trọng hơn
                if atom.get('importanceScore', 0) > unique_atoms[i].get('importanceScore', 0):
                    unique_atoms[i] = atom
                is_duplicate = True
                break
        
        if not is_duplicate:
            unique_atoms.append(atom)

    # 3. Xóa vector tạm trước khi trả về cho Java
    for atom in unique_atoms:
        if 'vector' in atom: del atom['vector']
            
    return unique_atoms

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
async def analyze_text(
    request: NoteRequest,
    auth: str = Security(verify_internal_auth)):
    #Định hướng tư duy của AI để trích xuất 'Knowledge Atoms' từ nội dung ghi chú. Yêu cầu trả về chỉ là một mảng JSON với cấu trúc cụ thể.
    instructions = """
    Extract 'Knowledge Atoms' (S.I.N.G principles) from the note. 
    Return ONLY a JSON array of objects. 
    
    The "type" field MUST be one of these exact values: 
    DEFINITION, CODE_SNIPPET, FORMULA, CONCEPT, FACT.
    
    Structure:
    {
        "title": "Short descriptive title",
        "content": "Detailed atomic knowledge content",
        "type": "DEFINITION | CODE_SNIPPET | FORMULA | CONCEPT | FACT",
        "difficultyScore": 0.0 to 1.0,
        "importanceScore": 0.0 to 1.0,
        "tags": ["tag1", "tag2"]
    }
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
        
        # 1. Lấy dữ liệu thô từ AI
        raw_data = response.parsed if response.parsed else json.loads(response.text)
        
        # 2. Xử lý chuẩn hóa dữ liệu trước khi trả về cho Java
        if isinstance(raw_data, list):
            for atom in raw_data:
                if "type" in atom and isinstance(atom["type"], str):
                    atom["type"] = atom["type"].upper()
                
                # Đảm bảo điểm số là số thực (float)
                if "difficultyScore" in atom:
                    atom["difficultyScore"] = float(atom["difficultyScore"])
                if "importanceScore" in atom:
                    atom["importanceScore"] = float(atom["importanceScore"])

                clean_data = semantic_deduplication(raw_data)
                return clean_data

        return raw_data

    except Exception as e:
        print(f"Error detail: {str(e)}") 
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    uvicorn.run(app, host="127.0.0.1", port=8000)