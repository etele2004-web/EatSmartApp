import os

class Config:
    SECRET_KEY = "supersecretkey"
    DATABASE = os.path.join(os.path.dirname(__file__), "eatsmart.db")
