import logging
import time

import requests
from oauthlib.oauth2 import LegacyApplicationClient
from requests_oauthlib import OAuth2Session

try:
    import config
except ModuleNotFoundError:
    logging.error("The config.py file is missing!")
    exit()

token_cache = {}


def fetch_new_token():
    # get client credentials from config file
    client_id = config.client_id
    client_secret = config.client_secret
    username = config.username
    password = config.password
    access_token_url = config.access_token_url

    oauth = OAuth2Session(client=LegacyApplicationClient(client_id=client_id))
    token = oauth.fetch_token(
        token_url=access_token_url,
        username=username,
        password=password,
        client_id=client_id,
        client_secret=client_secret,
    )
    access_token = token["access_token"]
    expires_at = token["expires_at"]
    return access_token, expires_at


def get_access_token():
    current_time = time.time()
    if "token" in token_cache and (current_time < token_cache["expires_at"]):
        return token_cache["token"]

    token, expires_at = fetch_new_token()
    token_cache["token"] = token
    token_cache["expires_at"] = expires_at
    return token


def handle_request(request_type: str, url: str, payload=None):
    logging.info(">> Handling a " + request_type + " request, to url " + url)
    access_token = "Bearer " + get_access_token()
    headers = {"Content-type": "application/json", "Authorization": access_token}

    request_methods = {
        "get": requests.get,
        "post": requests.post,
    }
    method = request_methods.get(request_type)
    if method is None:
        raise ValueError(f"Unsupported request type: {request_type}")

    try:
        if request_type == "get":
            response = method(url, headers=headers)
        else:
            response = method(url, headers=headers, json=payload)

        response.raise_for_status()
        return response

    except requests.RequestException as e:
        logging.error(str(e))
        return None
