"""
Class that provides utility to:
    1. get access and refresh tokens from keycloak
    2. Update the tokens when expired.
    3. upload/update keycloak resources
    4. upload/update fhir resources
"""

import os
import time
from dataclasses import dataclass, field, fields
from typing import Union
import logging

import backoff
import jwt
import requests
from oauthlib.oauth2 import LegacyApplicationClient, TokenExpiredError
from requests_oauthlib import OAuth2Session

keycloak_url = os.getenv("keycloak_url")


def is_readable_string(s):
    """
    Check if a variable is not an empty string.
    Args:
        s (str): The variable to check.
    Returns:
        bool: True if s is not an empty string, False otherwise.
    """
    return isinstance(s, str) and s.strip() != ""


@dataclass
class IamUri:
    """Keycloak authentication uris"""

    keycloak_base_uri: str
    realm: str
    client_id: str
    client_secret: str
    token_uri: str = field(init=False)

    def __post_init__(self):
        for _field in fields(self):
            if _field.init and not is_readable_string(getattr(self, _field.name)):
                raise ValueError(
                    f"{self.__class__.__name__} can only be initialized with str values"
                )
        self.token_uri = (
            self.keycloak_base_uri
            + "/realms/"
            + self.realm
            + "/protocol/openid-connect/token"
        )
        self.keycloak_realm_uri = self.keycloak_base_uri + "/admin/realms/" + self.realm


@dataclass
class InternalAuthenticationOptions(IamUri):
    """Describes config options for authentication that we have to handle ourselves"""

    user_username: str
    user_password: str


@dataclass
class ExternalAuthenticationOptions(IamUri):
    """Describes config options for authentication that we don't have to handle ourselves"""

    access_token: str
    refresh_token: str


@dataclass
class FhirKeycloakApiOptions:
    authentication_options: Union[
        InternalAuthenticationOptions, ExternalAuthenticationOptions
    ]
    fhir_base_uri: str


class FhirKeycloakApi:
    def __init__(self, options: FhirKeycloakApiOptions):
        auth_options = options.authentication_options
        if isinstance(auth_options, ExternalAuthenticationOptions):
            self.authentication_Side = "external"
            self.api_service = ExternalAuthenticationService(auth_options)
        if isinstance(auth_options, InternalAuthenticationOptions):
            self.authentication_Side = "internal"
            self.api_service = InternalAuthenticationService(auth_options)
        self.auth_options = auth_options
        self.fhir_base_uri = options.fhir_base_uri

    @backoff.on_exception(
        backoff.expo, requests.exceptions.RequestException, max_time=180
    )
    def request(self, **kwargs):
        max_retries = 2
        for attempt in range(max_retries):
            try:
                response = self.api_service.oauth.request(**kwargs)
                if response.status_code == 401 or 'login' in response.text:
                    logging.info("Received 401 or login page, refreshing token...")
                    try:
                        self.api_service.refresh_token()
                        logging.info("Token refreshed successfully, retrying request...")
                    except Exception as refresh_error:
                        logging.error(f"Failed to refresh token: {refresh_error}")
                        if attempt < max_retries - 1:
                            logging.info(f"Retrying token refresh (attempt {attempt + 2}/{max_retries})...")
                            continue
                        raise
                    return self.api_service.oauth.request(**kwargs)
                return response
            except TokenExpiredError as e:
                logging.info(f"Token expired: {e}. Refreshing token and retrying...")
                try:
                    self.api_service.refresh_token()
                    logging.info("Token refreshed successfully after expiration, retrying request...")
                    # Retry the request after refreshing token
                    if attempt < max_retries - 1:
                        continue
                    return self.api_service.oauth.request(**kwargs)
                except Exception as refresh_error:
                    logging.error(f"Failed to refresh expired token: {refresh_error}")
                    if attempt < max_retries - 1:
                        logging.info(f"Retrying token refresh (attempt {attempt + 2}/{max_retries})...")
                        time.sleep(1)  # Brief pause before retry
                        continue
                    raise
        
        # If we get here, all retries failed
        raise RuntimeError("Failed to make request after multiple token refresh attempts")


class InternalAuthenticationService:

    def __init__(self, option: InternalAuthenticationOptions):
        self.options = option
        client = LegacyApplicationClient(client_id=self.options.client_id)
        oauth = OAuth2Session(client=client, auto_refresh_url=self.options.token_uri)
        self.client = client
        self.oauth = oauth

    def get_token(self):
        """
        Oauth 2 does not work without a ssl layer to test this locally see https://stackoverflow.com/a/27785830/14564571
        :return:
        """

        token = self.oauth.fetch_token(
            token_url=self.options.token_uri,
            client_id=self.options.client_id,
            client_secret=self.options.client_secret,
            username=self.options.user_username,
            password=self.options.user_password,
        )
        return token

    def refresh_token(
        self,
    ):
        try:
            logging.info("Attempting to refresh OAuth token (Internal Auth)...")
            result = self.get_token()
            logging.info("Token refresh successful (Internal Auth)")
            return result
        except Exception as e:
            logging.error(f"Token refresh failed (Internal Auth): {type(e).__name__}: {e}")
            raise

    def _is_refresh_required(self):
        # TODO some defensive programming would be nice.
        return time.time() > self.oauth.token.get("expires_at")

    def decode_token(self):
        # full_jwt = jwt.JWT(jwt=token, **kwargs)
        # full_jwt.token.objects["valid"] = True
        # return json.loads(full_jwt.token.payload.decode("utf-8"))
        pass


class ExternalAuthenticationService:

    def __init__(self, option: ExternalAuthenticationOptions):
        self.options = option
        client = LegacyApplicationClient(client_id=self.options.client_id)
        oauth = OAuth2Session(
            client=client,
            token={
                "access_token": self.options.access_token,
                "refresh_token": self.options.refresh_token,
                "token_type": "Bearer",
                "expires_in": 18000,
            },
            auto_refresh_url=self.options.token_uri,
        )
        self.client = client
        self.oauth = oauth

    def get_token(self):
        """
        Oauth 2 does not work without a ssl layer to test this locally see https://stackoverflow.com/a/27785830/14564571
        :return:
        """
        # return the current token, not if its expired or invalid raise an irrecoverable showstopper error.
        if self._is_refresh_required():
            # if expired
            self.oauth.refresh_token(
                token_url=self.options.token_uri,
                client_id=self.options.client_id,
                client_secret=self.options.client_secret,
            )

            token = self.oauth.fetch_token(
                token_url=self.options.token_uri,
                client_id=self.options.client_id,
                client_secret=self.options.client_secret,
            )
            return token
        else:
            return self.oauth.token

    def refresh_token(
        self,
    ):
        try:
            logging.info("Attempting to refresh OAuth token...")
            result = self.oauth.refresh_token(
                self.options.token_uri,
                client_id=self.options.client_id,
                client_secret=self.options.client_secret,
            )
            logging.info("Token refresh successful")
            return result
        except Exception as e:
            logging.error(f"Token refresh failed: {type(e).__name__}: {e}")
            logging.error(f"Token URI: {self.options.token_uri}")
            logging.error(f"Client ID: {self.options.client_id}")
            raise

    def _is_refresh_required(self):
        # TODO some defensive programming would be nice.
        at = self.oauth.token.get("access_token")
        try:
            decoded_at = self.decode_token(at)
            return time.time() > decoded_at.get("exp")
        except:
            return False

    def decode_token(self, token: str):
        # TODO - verify JWT
        _algorithms = "HS256"
        _do_verify = False
        cert_uri = f"{keycloak_url}/realms/fhir/protocol/openid-connect/certs"
        res = self.oauth.get(cert_uri).json().get("keys")
        # tired
        first_key = res[0]
        jwk = jwt.jwk_from_dict(first_key)
        _algorithms = first_key.get("alg")
        instance = jwt.JWT()
        return instance.decode(
            token, algorithms=_algorithms, key=jwk, do_verify=True, do_time_check=True
        )
