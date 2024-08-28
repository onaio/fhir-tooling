import logging

from importer.config.settings import api_service


# This function makes the request to the provided url
# to create resources
def post_request(request_type, payload, url, json_payload):
    logging.info("Posting request")
    logging.info("Request type: " + request_type)
    logging.info("Url: " + url)
    logging.debug("Payload: " + payload)

    return api_service.request(
        method=request_type, url=url, data=payload, json=json_payload
    )


def handle_request(request_type, payload, url, json_payload=None):
    try:
        response = post_request(request_type, payload, url, json_payload)
        if response.status_code == 200 or response.status_code == 201:
            logging.info("[" + str(response.status_code) + "]" + ": SUCCESS!")

        if request_type == "GET":
            return response.text, response.status_code
        else:
            return response
    except Exception as err:
        logging.error(err)
