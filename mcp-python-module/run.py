#!/usr/bin/env python3
"""
Script de lancement pour MCP Python Module
"""
import uvicorn

if __name__ == "__main__":
    print(" Starting MCP Python Module...")
    print(" Server will be available at: http://localhost:8090")
    print(" Press CTRL+C to stop the server\n")

    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",
        port=8090,  # Port
        reload=True,  # Redémarrage auto en développement
        log_level="info"
    )