import re
import pandas as pd
import nltk
from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from nltk.stem import PorterStemmer

# Téléchargement des ressources NLTK
try:
    nltk.data.find('tokenizers/punkt')
except LookupError:
    nltk.download('punkt')

try:
    nltk.data.find('corpora/stopwords')
except LookupError:
    nltk.download('stopwords')

class TextPreprocessor:
    def __init__(self):
        self.stop_words = set(stopwords.words('english'))
        self.stemmer = PorterStemmer()

    def clean_text(self, text):
        if pd.isna(text):
            return ""

        text = str(text)

        # Supprimer les mentions Reuters typiques
        text = re.sub(r'\(Reuters\)\s*\-?\s*', '', text)

        # Conversion en minuscules
        text = text.lower()

        # Suppression des caractères spéciaux mais garder les apostrophes
        text = re.sub(r'[^a-zA-Z\s\']', ' ', text)

        # Suppression des URLs
        text = re.sub(r'http\S+', '', text)

        # Suppression des espaces multiples
        text = re.sub(r'\s+', ' ', text).strip()

        return text

    def remove_stopwords(self, text):
        words = word_tokenize(text)
        filtered_words = [self.stemmer.stem(word) for word in words
                          if word not in self.stop_words and len(word) > 2]
        return ' '.join(filtered_words)

    def preprocess_text(self, text):
        """Méthode principale pour prétraiter un texte"""
        cleaned_text = self.clean_text(text)
        processed_text = self.remove_stopwords(cleaned_text)
        return processed_text