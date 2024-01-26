import json
import click
import requests
import logging
import time
from oauthlib.oauth2 import LegacyApplicationClient
from requests_oauthlib import OAuth2Session

try:
    import config
except ModuleNotFoundError:
    logging.error("The config.py file is missing!")
    exit()


# This function makes the request to the provided url
def handle_request(request_type, payload, url):
    logging.debug("Posting request")
    logging.debug("Request type: " + request_type)
    logging.debug("Url: " + url)
    logging.debug("Payload: " + payload)

    # get credentials from config file
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

    access_token = "Bearer " + token["access_token"]
    headers = {"Content-type": "application/json", "Authorization": access_token}

    try:
        if request_type == "POST":
            r = requests.post(url, data=payload, headers=headers)
            if r.status_code == 200 or r.status_code == 201:
                logging.debug("[" + str(r.status_code) + "]" + ": SUCCESS!")
            else:
                logging.error("[" + str(r.status_code) + "]" + r.text)
            return r
        elif request_type == "GET":
            r = requests.get(url, headers=headers)
            if r.status_code == 200 or r.status_code == 201:
                logging.debug("[" + str(r.status_code) + "]" + ": SUCCESS!")
            else:
                logging.error("[" + str(r.status_code) + "]" + r.text)
            return r.text
        elif request_type == "DELETE":
            r = requests.delete(url, headers=headers)
            if r.status_code == 200 or r.status_code == 201:
                logging.debug("[" + str(r.status_code) + "]" + ": SUCCESS!")
            else:
                logging.error("[" + str(r.status_code) + "]" + r.text)
            return r.text
        else:
            logging.error("Unsupported request type!")
    except Exception as err:
        logging.error(err)
        raise


def build_payload(resource_ids, resource_type):
    logging.info("Building payload")
    full_payload = """ {"resourceType": "Bundle", "type": "transaction", "entry": [$myResources]} """
    resource_list = ""
    for resource_id in resource_ids:
        item = """ {"request": {"method": "DELETE", "url": "$resource_type/$id"} } """
        item = item.replace("$id", resource_id).replace("$resource_type", resource_type)
        resource_list = resource_list + item + ","

    full_payload = full_payload.replace("$myResources", resource_list[:-1])
    return full_payload


def delete_resources(resource_url, resource_type):
    # Get the resources from the url
    logging.info("Getting resources")
    response = handle_request("GET", "", resource_url)

    try:
        json_response = json.loads(response)
        resources = json_response["entry"]
    except KeyError:
        resources = ""

    resource_ids = []

    if len(resources) > 1:
        for resource in resources:
            resource_id = resource["resource"]["id"]
            resource_ids.append(resource_id)

        del_payload = build_payload(resource_ids, resource_type)
        handle_request("POST", del_payload, config.fhir_base_url)
        logging.info(str(len(resource_ids)) + " resources deleted")
        time.sleep(20)
        delete_resources(resource_url, resource_type)

    else:
        # confirm the count and that we are done
        logging.info("Checking count")
        count_url = resource_url + "&_summary=count"
        count_response = handle_request("GET", "", count_url)
        json_count = json.loads(count_response)
        count = json_count["total"]
        if count == 0:
            logging.info("All resourcess successfully deleted")
        else:
            time.sleep(20)
            logging.info("Call delete_resources again")
            delete_resources(resource_url, resource_type)


def expunge_resources(expunge_url):
    full_payload = """ { "resourceType": "Parameters", "parameter": [{ "name": "expungeDeletedResources", "valueBoolean": true }]} """
    response = handle_request("POST", full_payload, expunge_url)
    json_response = json.loads(response.text)
    counter = json_response["parameter"][0]["valueInteger"]
    if counter > 0:
        expunge_resources(expunge_url)
    else:
        logging.info("All deleted records expunged")


@click.command()
@click.option("--resource_type", required=True)
@click.option("--parameter", required=True)
@click.option("--value", required=True)
@click.option("--batch_size", default=1000)
@click.option("--expunge", default=False)
@click.option("--cascade", default=False)
@click.option(
    "--log_level", type=click.Choice(["DEBUG", "INFO", "ERROR"], case_sensitive=False)
)
def main(resource_type, parameter, value, batch_size, expunge, cascade, log_level):
    if log_level == "DEBUG":
        logging.basicConfig(level=logging.DEBUG)
    elif log_level == "INFO":
        logging.basicConfig(level=logging.INFO)
    elif log_level == "ERROR":
        logging.basicConfig(level=logging.ERROR)

    logging.info("Starting up the cleaner...")

    # build url
    resource_url = (
        config.fhir_base_url
        + "/"
        + resource_type
        + "?"
        + parameter
        + "="
        + value
        + "&_count="
        + str(batch_size)
    )
    delete_resources(resource_url, resource_type)
    if expunge:
        expunge_url = config.fhir_base_url + "/" + resource_type + "/$expunge"
        expunge_resources(expunge_url)
    if cascade:
        resource_url = resource_url + "&_cascade=delete"
        handle_request("DELETE", "", resource_url)


if __name__ == "__main__":
    main()
