from flask import Flask
from config import Config
from database import init_db

# Blueprint importok
from blueprints.auth import auth_bp
from blueprints.meals import meals_bp
from blueprints.summary import summary_bp
from blueprints.stats import stats_bp

def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)

    # adatbázis inicializálás
    init_db()

    # blueprintek regisztrálása
    app.register_blueprint(auth_bp)
    app.register_blueprint(meals_bp)
    app.register_blueprint(summary_bp)
    app.register_blueprint(stats_bp)

    return app

if __name__ == "__main__":
    app = create_app()
    app.run(debug=True)

