from pydantic_settings import BaseSettings, SettingsConfigDict


class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", extra="ignore")

    database_url: str = "postgresql://navigator_user:password@postgres:5432/navigator"
    cors_origins: str = "*"
    debug: bool = True


settings = Settings()
