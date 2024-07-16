import os
from dotenv import load_dotenv
load_dotenv()

client_id = os.getenv("client_id")
client_secret = os.getenv("client_secret")
fhir_base_url = os.getenv("fhir_base_url")
keycloak_base_url = os.getenv("keycloak_url")
realm = os.getenv("realm")

product_access_token = os.getenv("product_access_token")

username = os.getenv("username")
password = os.getenv("password")

accessToken = os.getenv("access_token")

access_token_url = os.getenv("access_token_url")
keycloak_url = os.getenv("keycloak_url")
