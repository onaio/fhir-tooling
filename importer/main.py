import csv
import json
import uuid
import click
import requests
import logging
from oauthlib.oauth2 import LegacyApplicationClient
from requests_oauthlib import OAuth2Session

try:
    import config
except ModuleNotFoundError:
    logging.error("The config.py file is missing!")
    exit()


# This function takes in a csv file
# reads it and returns a list of strings/lines
# It ignores the first line (assumes headers)
def read_csv(csv_file):
    logging.info("Reading csv file")
    with open(csv_file, mode="r") as file:
        records = csv.reader(file, delimiter=",")
        try:
            next(records)
            all_records = []

            for record in records:
                all_records.append(record)

            logging.info("Returning records from csv file")
            return all_records

        except StopIteration:
            logging.error("Stop iteration on empty file")


# This function makes the request to the provided url
# to create resources
def post_request(request_type, payload, url):
    logging.info("Posting request-----------------")
    logging.info("Request type: " + request_type)
    logging.info("Url: " + url)
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
                logging.info(str(r.status_code) + ": SUCCESS!")
            return r
        elif request_type == "PUT":
            r = requests.put(url, data=payload, headers=headers)
            if r.status_code == 200 or r.status_code == 201:
                logging.info(str(r.status_code) + ": SUCCESS!")
            return r
        else:
            logging.error("Unsupported request type!")
    except Exception as err:
        logging.error(err)
        raise


# This function builds the user payload and posts it to
# the keycloak api to create a new user
# it also adds the user to the provided keycloak group
# and sets the user password
def create_user(user):
    with open("json_payloads/keycloak_user_payload.json") as json_file:
        payload_string = json_file.read()

    obj = json.loads(payload_string)
    obj["firstName"] = user[0]
    obj["lastName"] = user[1]
    obj["username"] = user[2]
    obj["email"] = user[3]
    obj["attributes"]["fhir_core_app_id"][0] = user[8]

    final_string = json.dumps(obj)
    logging.info("Creating user: " + user[2])
    r = post_request("POST", final_string, config.keycloak_url)

    if r.status_code == 201:
        logging.info("User created successfully")
        new_user_location = r.headers["Location"]
        user_id = (new_user_location.split("/"))[-1]

        # add user to group
        payload = '{"id": "' + user[6] + '", "name": "' + user[7] + '"}'
        group_endpoint = "/" + user_id + "/groups/" + user[6]
        url = config.keycloak_url + group_endpoint
        logging.info("Adding user to Keycloak group: " + user[7])
        r = post_request("PUT", payload, url)

        # set password
        payload = '{"temporary":false,"type":"password","value":"' + user[9] + '"}'
        password_endpoint = "/" + user_id + "/reset-password"
        url = config.keycloak_url + password_endpoint
        logging.info("Setting user password")
        r = post_request("PUT", payload, url)

        return user_id
    elif r.status_code == 409:
        logging.error(r.text)
        return 0
    else:
        logging.error(str(r.status_code) + ":" + r.text)
        return 0


# This function build the FHIR resources related to a
# new user and posts them to the FHIR api for creation
def create_user_resources(user_id, user):
    logging.info("Creating user resources")
    # generate uuids
    practitioner_uuid = str(uuid.uuid4())
    group_uuid = str(uuid.uuid4())
    practitioner_role_uuid = str(uuid.uuid4())

    # get payload and replace strings
    initial_string = """{"resourceType": "Bundle","type": "transaction","meta": {"lastUpdated": ""},"entry": """
    with open("json_payloads/user_resources_payload.json") as json_file:
        payload_string = json_file.read()

    # replace the variables in payload
    ff = (
        payload_string.replace("$practitioner_uuid", practitioner_uuid)
        .replace("$keycloak_user_uuid", user_id)
        .replace("$firstName", user[0])
        .replace("$lastName", user[1])
        .replace("$email", user[3])
        .replace("$group_uuid", group_uuid)
        .replace("$practitioner_role_uuid", practitioner_role_uuid)
    )

    payload = initial_string + ff + "}"
    post_request("POST", payload, config.fhir_base_url)


# This function builds a json payload
# which is posted to the api to create resources
def build_payload(resources, resource_payload_file):
    logging.info("Building request payload")
    initial_string = """{"resourceType": "Bundle","type": "transaction","meta": {"lastUpdated": ""},"entry": [ """
    final_string = " "
    with open(resource_payload_file) as json_file:
        payload_string = json_file.read()

    for resource in resources:
        unique_uuid = str(uuid.uuid4())
        ff = (
            payload_string.replace("$status", resource[1])
            .replace("$name", resource[0])
            .replace("$location_uuid", unique_uuid)
        )
        final_string = final_string + ff + ","

    final_string = initial_string + final_string[:-1] + " ] } "
    return final_string


@click.command()
@click.option("--csv_file", required=True)
@click.option("--resource_type", required=True)
@click.option(
    "--log_level", type=click.Choice(["DEBUG", "INFO", "ERROR"], case_sensitive=False)
)
def main(csv_file, resource_type, log_level):
    if log_level == "DEBUG":
        logging.basicConfig(level=logging.DEBUG)
    elif log_level == "INFO":
        logging.basicConfig(level=logging.INFO)
    elif log_level == "ERROR":
        logging.basicConfig(level=logging.ERROR)

    logging.info("Starting csv import...")
    resource_list = read_csv(csv_file)
    if resource_list:
        if resource_type == "users":
            logging.info("Processing users")
            for user in resource_list:
                user_id = create_user(user)
                if user_id != 0:
                    create_user_resources(user_id, user)
                    logging.info("Processing complete!")
        elif resource_type == "locations":
            logging.info("Processing locations")
            json_payload = build_payload(
                resource_list, "json_payloads/locations_payload.json"
            )
            post_request("POST", json_payload, config.fhir_base_url)
            logging.info("Processing complete!")
        else:
            logging.error("Unsupported resource type!")
    else:
        logging.error("Empty csv file!")


if __name__ == "__main__":
    main()
