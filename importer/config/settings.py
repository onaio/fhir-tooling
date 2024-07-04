from services.fhir_keycloak_api import FhirKeycloakApi, FhirKeycloakApiOptions, ExternalAuthenticationOptions, \
    InternalAuthenticationOptions
from config.config import client_id, client_secret, fhir_base_url, keycloak_url, realm
import importlib
import sys

def dynamic_import(variable_name):
    try:
        config_module = importlib.import_module("temporal.config")
        value = getattr(config_module, variable_name, None)
        return value
    except ModuleNotFoundError:
        sys.exit(1)


username = dynamic_import("username")
password = dynamic_import("password")

# TODO - retrieving at and rt as args via the command line as well.
access_token = dynamic_import("access_token")
refresh_token = dynamic_import("refresh_token")
product_access_token = dynamic_import("product_access_token")

authentication_options = None
if username is not None and password is not None:
    authentication_options = InternalAuthenticationOptions(client_id=client_id, client_secret=client_secret, keycloak_base_uri=keycloak_url, realm=realm, user_username=username, user_password=password)
elif access_token is not None and refresh_token is not None:
    authentication_options = ExternalAuthenticationOptions(client_id=client_id, client_secret=client_secret, keycloak_base_uri=keycloak_url, realm=realm, access_token=access_token, refresh_token=refresh_token)
else:
    sys.exit(1)

api_service_options = FhirKeycloakApiOptions(fhir_base_uri=fhir_base_url, authentication_options=authentication_options)
api_service = FhirKeycloakApi(api_service_options)




