FROM python:3.9-slim

WORKDIR /app

# Installer curl pour healthcheck
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# Copier requirements d'abord
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copier l'application
COPY . .

EXPOSE 8090

CMD ["python", "run.py"]