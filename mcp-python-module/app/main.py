from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from .fake_news_detector import FakeNewsDetector

app = FastAPI(title="MCP Python Module")
detector = FakeNewsDetector()

class AnalyzeRequest(BaseModel):
    text: str

class AnalyzeResponse(BaseModel):
    prediction: int
    confidence: float
    processed_text: str
    model_used: bool

@app.get("/")
async def root():
    return {"message": "MCP Python Module - Fake News Detection"}

@app.get("/health")
async def health():
    return {
        "status": "healthy",
        "models_loaded": detector.models_loaded
    }

@app.post("/analyze", response_model=AnalyzeResponse)
async def analyze_news(request: AnalyzeRequest):
    try:
        if not request.text.strip():
            raise HTTPException(status_code=400, detail="Text cannot be empty")

        result = detector.predict(request.text)
        return AnalyzeResponse(**result)

    except Exception as e:
        raise HTTPException(status_code=500, detail=f"Analysis error: {str(e)}")