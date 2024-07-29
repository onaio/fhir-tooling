import os

from dotenv import load_dotenv

from importer.services.fhir_keycloak_api import (ExternalAuthenticationOptions,
                                                 FhirKeycloakApi,
                                                 FhirKeycloakApiOptions,
                                                 InternalAuthenticationOptions)

load_dotenv()

client_id = os.getenv("client_id")
client_secret = os.getenv("client_secret")
fhir_base_url = os.getenv("fhir_base_url")
keycloak_url = os.getenv("keycloak_url")
realm = os.getenv("realm")

product_access_token = os.getenv("product_access_token")

username = os.getenv("username")
password = os.getenv("password")

access_token = os.getenv("access_token")
refresh_token = os.getenv("refresh_token")

authentication_options = None
if username is not None and password is not None:
    authentication_options = InternalAuthenticationOptions(
        client_id=client_id,
        client_secret=client_secret,
        keycloak_base_uri=keycloak_url,
        realm=realm,
        user_username=username,
        user_password=password,
    )
elif access_token is not None and refresh_token is not None:
    authentication_options = ExternalAuthenticationOptions(
        client_id=client_id,
        client_secret=client_secret,
        keycloak_base_uri=keycloak_url,
        realm=realm,
        access_token=access_token,
        refresh_token=refresh_token,
    )
else:
    raise ValueError("Unable to get authentication parameters")

api_service_options = FhirKeycloakApiOptions(
    fhir_base_uri=fhir_base_url, authentication_options=authentication_options
)
api_service = FhirKeycloakApi(api_service_options)
