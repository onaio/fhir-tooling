import csv
import json
import uuid
import click
import config
import requests
from oauthlib.oauth2 import LegacyApplicationClient
from requests_oauthlib import OAuth2Session


# This function takes in a csv file
# reads it and returns a list of strings/lines
# It ignores the first line (assumes headers)
def read_csv(csv_file):
    with open(csv_file, mode="r") as file:
        records = csv.reader(file, delimiter=",")
        next(records)
        all_records = []

        for record in records:
            all_records.append(record)

    return all_records


# This function makes the request to the provided url
# to create resources
def post_request(request_type, payload, url):
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
            return r
        elif request_type == "PUT":
            r = requests.put(url, data=payload, headers=headers)
            return r
        else:
            print("ERROR: Unsupported request type!")
    except:
        print("ERROR: Request failed!")


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
    r = post_request("POST", final_string, config.keycloak_url)

    if r.status_code == 201:
        new_user_location = r.headers["Location"]
        user_id = (new_user_location.split("/"))[-1]

        # add user to group
        payload = '{"id": "' + user[6] + '", "name": "' + user[7] + '"}'
        group_endpoint = "/" + user_id + "/groups/" + user[6]
        url = config.keycloak_url + group_endpoint
        r = post_request("PUT", payload, url)

        # set password
        payload = '{"temporary":false,"type":"password","value":"' + user[9] + '"}'
        password_endpoint = "/" + user_id + "/reset-password"
        url = config.keycloak_url + password_endpoint
        r = post_request("PUT", payload, url)

        return user_id
    elif r.status_code == 409:
        print("ERROR: User " + user[0] + " " + user[1] + " already exists!")
        return 0
    else:
        print("ERROR: User creation failed!")
        return 0


# This function build the FHIR resources related to a
# new user and posts them to the FHIR api for creation
def create_user_resources(user_id, user):
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
@click.option("--csv_file")
@click.option("--resource_type")
def main(csv_file, resource_type):
    resource_list = read_csv(csv_file)
    if resource_list:
        if resource_type == "users":
            for user in resource_list:
                user_id = create_user(user)
                if user_id != 0:
                    create_user_resources(user_id, user)
                    print("Process complete!")
        elif resource_type == "locations":
            json_payload = build_payload(
                resource_list, "json_payloads/locations_payload.json"
            )
            post_request("POST", json_payload, config.fhir_base_url)
            print("Process complete!")
        else:
            print("ERROR: Unsupported resource type!")
    else:
        print("ERROR: Your csv file is empty!")


if __name__ == "__main__":
    main()
