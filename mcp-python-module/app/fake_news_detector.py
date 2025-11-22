import joblib
import pickle
import os
import random
import logging
from app.text_preprocessor import TextPreprocessor

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

class FakeNewsDetector:
    def __init__(self):
        self.models_loaded = False
        self.model = None
        self.vectorizer = None
        self.preprocessor = TextPreprocessor()
        logger.info(" FakeNewsDetector initialized")
        self._load_models()

    def _load_models(self):
        try:
            # Chemin relatif depuis la racine du projet
            base_dir = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
            model_dir = os.path.join(base_dir, "model", "trained")

            model_path = os.path.join(model_dir, "fake_news_model.pkl")
            vectorizer_path = os.path.join(model_dir, "tfidf_vectorizer.pkl")
            preprocessor_path = os.path.join(model_dir, "text_preprocessor.pkl")

            logger.info(f" Loading models from: {model_dir}")
            logger.info(f" File check:")
            logger.info(f"   Model: {os.path.exists(model_path)} - {model_path}")
            logger.info(f"   Vectorizer: {os.path.exists(vectorizer_path)} - {vectorizer_path}")
            logger.info(f"   Preprocessor: {os.path.exists(preprocessor_path)} - {preprocessor_path}")

            if not all(os.path.exists(p) for p in [model_path, vectorizer_path, preprocessor_path]):
                logger.error(" Some model files are missing")
                return

            logger.info(" All files found, loading models...")

            # Charger le modèle
            self.model = joblib.load(model_path)
            logger.info(" Model loaded successfully")

            # Charger le vectorizer
            self.vectorizer = joblib.load(vectorizer_path)
            logger.info(" Vectorizer loaded successfully")

            # On utilise déjà self.preprocessor = TextPreprocessor() dans __init__
            logger.info(" Preprocessor ready")

            self.models_loaded = True
            logger.info(" ALL MODELS LOADED SUCCESSFULLY!")

        except Exception as e:
            logger.error(f" Error loading models: {e}")
            import traceback
            logger.error(f"Full error: {traceback.format_exc()}")

    def predict(self, text: str) -> dict:
        if not self.models_loaded:
            logger.warning("🔧 Using simulation mode - models not loaded")
            return self._simulate_prediction(text)

        try:
            logger.info(f" Analyzing text: {text[:50]}...")

            processed_text = self.preprocessor.preprocess_text(text)
            logger.info(f" Processed text: {processed_text[:50]}...")

            text_vectorized = self.vectorizer.transform([processed_text])
            logger.info("Text vectorized")

            prediction = self.model.predict(text_vectorized)[0]
            probability = self.model.predict_proba(text_vectorized)[0]
            confidence = probability[1] if prediction == 1 else probability[0]

            logger.info(f" Prediction: {'REAL' if prediction == 1 else 'FAKE'} (confidence: {confidence:.2f})")

            return {
                "prediction": int(prediction),
                "confidence": round(float(confidence), 4),
                "processed_text": processed_text,
                "model_used": True
            }

        except Exception as e:
            logger.error(f" Prediction error: {e}")
            return self._simulate_prediction(text)

    def _simulate_prediction(self, text: str) -> dict:
        logger.warning(" Using simulation mode")
        prediction = random.randint(0, 1)
        confidence = 0.5 + (random.random() * 0.4)

        return {
            "prediction": prediction,
            "confidence": round(confidence, 4),
            "processed_text": text[:100] + "..." if len(text) > 100 else text,
            "model_used": False
        }