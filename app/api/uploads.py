from fastapi import APIRouter, UploadFile, File, HTTPException
import os
import shutil

router = APIRouter(prefix="/upload", tags=["Upload"])

UPLOAD_DIR = "app/storage/uploads"

os.makedirs(UPLOAD_DIR, exist_ok=True)

@router.post("/")
async def upload_file(file: UploadFile = File(...)):

    # valida extensão
    if not file.filename.endswith(".csv"):
        raise HTTPException(status_code=400, detail="Only CSV files are allowed")

    # lê conteúdo
    content = await file.read()

    # valida tamanho (2MB)
    if len(content) > 2 * 1024 * 1024:
        raise HTTPException(status_code=400, detail="File too large")

    # nome seguro
    safe_name = file.filename.replace(" ", "_").lower()

    file_path = os.path.join(UPLOAD_DIR, safe_name)

    # evitar sobrescrever
    if os.path.exists(file_path):
        raise HTTPException(status_code=400, detail="File already exists")

    # salvar arquivo
    with open(file_path, "wb") as buffer:
        buffer.write(content)

    return {
        "filename": safe_name,
        "size": len(content),
        "status": "uploaded successfully"
    }
