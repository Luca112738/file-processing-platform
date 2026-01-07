from fastapi import FastAPI
from app.api.upload import router as upload_router

app = FastAPI(title="File Processing Platform")

app.include_router(upload_router)

@app.get("/")
def health_check():
    return {"status": "ok"}
