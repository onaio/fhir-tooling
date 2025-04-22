import logging
import json
import traceback

import requests

from importer.config.settings import api_service


# This function makes the request to the provided url
# to create resources
def post_request(request_type, payload, url, json_payload,headers=None):
    logging.info("Posting request")
    logging.info("Request type: " + request_type)
    logging.info("Url: " + url)
    #logging.debug("Payload: " + payload)

    request_kwargs = {
        "method": request_type,
        "url": url,
        "data": payload,
        "json": json_payload,
        "headers": headers
    }

    logging.debug(f"Request kwargs: {request_kwargs}")
    return api_service.request(**request_kwargs)


def handle_request(request_type, payload, url, json_payload=None, is_update_list=False):
    try:
        headers = {"Content-Type": "application/json", "Accept": "application/json"}

        # Conditionally add X-Meta-Snapshot-Mode: TAG for update_list
        if is_update_list:
            headers["X-Meta-Snapshot-Mode"] = "TAG"

        logging.info(f"Sending {request_type} request to {url}")
        logging.debug(f"Headers: {headers}")
        logging.info(f"Payload: {payload}")

        response = post_request(request_type, payload.encode("utf-8"), url, json_payload, headers)

        if response is None:
            logging.error("No response received from API.")
            return None

        if response.status_code in [200, 201]:
            logging.info(f"[{response.status_code}]: SUCCESS!")

        if request_type == "GET":
            return response.text, response.status_code
        else:
            return response
    except requests.exceptions.RequestException as req_err:
        logging.error(f"Request failed: {req_err}")
        logging.error(traceback.format_exc())  # Print full stack trace
        return None
    except Exception as err:
        logging.error(f"Unexpected error in handle_request: {err}")
        logging.error(traceback.format_exc())  # Print full stack trace
        return None
