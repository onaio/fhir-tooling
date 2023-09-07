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
                logging.info("[" + str(r.status_code) + "]" + ": SUCCESS!")
            else:
                logging.error("[" + str(r.status_code) + "]" + r.text)
            return r
        elif request_type == "PUT":
            r = requests.put(url, data=payload, headers=headers)
            if r.status_code == 200 or r.status_code == 201:
                logging.info("[" + str(r.status_code) + "]" + ": SUCCESS!")
            else:
                logging.error("[" + str(r.status_code) + "]" + r.text)
            return r
        elif request_type == "GET":
            r = requests.get(url, headers=headers)
            if r.status_code == 200 or r.status_code == 201:
                logging.info("[" + str(r.status_code) + "]" + ": SUCCESS!")
            else:
                logging.error("[" + str(r.status_code) + "]" + r.text)
            return r.text
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
    obj["attributes"]["fhir_core_app_id"][0] = user[9]

    final_string = json.dumps(obj)
    logging.info("Creating user: " + user[2])
    r = post_request("POST", final_string, config.keycloak_url)

    if r.status_code == 201:
        logging.info("User created successfully")
        new_user_location = r.headers["Location"]
        user_id = (new_user_location.split("/"))[-1]

        # add user to group
        payload = '{"id": "' + user[7] + '", "name": "' + user[8] + '"}'
        group_endpoint = "/" + user_id + "/groups/" + user[7]
        url = config.keycloak_url + group_endpoint
        logging.info("Adding user to Keycloak group: " + user[8])
        r = post_request("PUT", payload, url)

        # set password
        payload = '{"temporary":false,"type":"password","value":"' + user[10] + '"}'
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
    if (len(str(user[4]).strip()) == 0):
        practitioner_uuid = str(uuid.uuid4())
    else:
        practitioner_uuid = user[4]

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

    obj = json.loads(ff)
    if user[5] == "Supervisor":
        obj[2]["resource"]["code"] = {
            "coding": [
                {
                    "system": "http://snomed.info/sct",
                    "code": "236321002",
                    "display": "Supervisor (occupation)",
                }
            ]
        }
    elif user[5] == "Practitioner":
        obj[2]["resource"]["code"] = {
            "coding": [
                {
                    "system": "http://snomed.info/sct",
                    "code": "405623001",
                    "display": "Assigned practitioner",
                }
            ]
        }
    else:
        del obj[2]["resource"]["code"]
    ff = json.dumps(obj, indent=4)

    payload = initial_string + ff + "}"
    post_request("POST", payload, config.fhir_base_url)


# custom extras for organizations
def organization_extras(resource, payload_string):
    try:
        if resource[6]:
            payload_string = payload_string.replace("$alias", resource[6])
    except IndexError:
        obj = json.loads(payload_string)
        del obj["resource"]["alias"]
        payload_string = json.dumps(obj, indent=4)

    try:
        payload_string = payload_string.replace("$active", resource[1])
    except IndexError:
        payload_string = payload_string.replace("$active", "true")
    return payload_string


# custom extras for locations
def location_extras(resource, payload_string):
    try:
        if resource[5]:
            payload_string = payload_string.replace("$parentName", resource[5]).replace(
                "$parentID", resource[6]
            )
        else:
            obj = json.loads(payload_string)
            del obj["resource"]["partOf"]
            payload_string = json.dumps(obj, indent=4)
    except IndexError:
        obj = json.loads(payload_string)
        del obj["resource"]["partOf"]
        payload_string = json.dumps(obj, indent=4)

    try:
        if resource[7] == "building":
            payload_string = payload_string.replace("$pt_code", "bu").replace(
                "$pt_display", "Building"
            )
        elif resource[7] == "jurisdiction":
            payload_string = payload_string.replace("$pt_code", "jdn").replace(
                "$pt_display", "Jurisdiction"
            )
        else:
            logging.error("Unsupported location type provided for " + resource[0])
            obj = json.loads(payload_string)
            del obj["resource"]["physicalType"]
            payload_string = json.dumps(obj, indent=4)
    except IndexError:
        obj = json.loads(payload_string)
        del obj["resource"]["physicalType"]
        payload_string = json.dumps(obj, indent=4)

    return payload_string


# custom extras for careTeams
def care_team_extras(
    resource, payload_string, load_type, c_participants, c_orgs, ftype
):
    orgs_list = []
    participant_list = []
    elements = []
    elements2 = []

    if load_type == "min":
        try:
            if resource[6]:
                elements = resource[6].split("|")
        except IndexError:
            pass
        try:
            if resource[7]:
                elements2 = resource[7].split("|")
        except IndexError:
            pass
    elif load_type == "full":
        elements = resource
    else:
        logging.error("Unsupported load type")

    if "orgs" in ftype:
        for org in elements:
            y = {}
            x = org.split(":")
            y["reference"] = "Organization/" + str(x[0])
            y["display"] = str(x[1])
            orgs_list.append(y)

            z = {
                "role": [
                    {
                        "coding": [
                            {
                                "system": "http://snomed.info/sct",
                                "code": "394730007",
                                "display": "Healthcare related organization",
                            }
                        ]
                    }
                ],
                "member": {},
            }
            z["member"]["reference"] = "Organization/" + str(x[0])
            z["member"]["display"] = str(x[1])
            participant_list.append(z)

        if len(c_participants) > 0:
            participant_list = c_participants + participant_list
        if len(c_orgs) > 0:
            orgs_list = c_orgs + orgs_list

        if len(participant_list) > 0:
            obj = json.loads(payload_string)
            obj["resource"]["participant"] = participant_list
            obj["resource"]["managingOrganization"] = orgs_list
            payload_string = json.dumps(obj)

    if "users" in ftype:
        if len(elements2) > 0:
            elements = elements2
        for user in elements:
            y = {"member": {}}
            x = user.split(":")
            y["member"]["reference"] = "Practitioner/" + str(x[0])
            y["member"]["display"] = str(x[1])
            participant_list.append(y)

        if len(c_participants) > 0:
            participant_list = c_participants + participant_list

        if len(participant_list) > 0:
            obj = json.loads(payload_string)
            obj["resource"]["participant"] = participant_list
            payload_string = json.dumps(obj)

    return payload_string


def extract_matches(resource_list):
    teamMap = {}
    for resource in resource_list:
        if resource[1] not in teamMap.keys():
            teamMap[resource[1]] = [resource[3] + ":" + resource[2]]
        else:
            teamMap[resource[1]].append(resource[3] + ":" + resource[2])
    return teamMap


def fetch_and_build(extracted_matches, ftype):
    fp = """{"resourceType": "Bundle","type": "transaction","entry": [ """

    for key in extracted_matches:
        # hit api to get current payload
        endpoint = config.fhir_base_url + "/CareTeam/" + key
        fetch_payload = post_request("GET", "", endpoint)

        obj = json.loads(fetch_payload)
        current_version = obj["meta"]["versionId"]

        # build participants and managing orgs
        full_payload = {
            "request": {
                "method": "PUT",
                "url": "CareTeam/$unique_uuid",
                "ifMatch": "$version",
            },
            "resource": {},
        }
        full_payload["request"]["url"] = "CareTeam/" + str(key)
        full_payload["request"]["ifMatch"] = current_version
        full_payload["resource"] = obj
        del obj["meta"]

        try:
            curr_participants = full_payload["resource"]["participant"]
        except KeyError:
            curr_participants = {}

        try:
            curr_orgs = full_payload["resource"]["managingOrganization"]
        except KeyError:
            curr_orgs = {}

        payload_string = json.dumps(full_payload, indent=4)
        payload_string = care_team_extras(
            extracted_matches[key],
            payload_string,
            "full",
            curr_participants,
            curr_orgs,
            ftype,
        )
        fp = fp + payload_string + ","

    fp = fp[:-1] + " ] } "
    return fp


def get_org_name(key, resource_list):
    for x in resource_list:
        if x[1] == key:
            org_name = x[0]

    return org_name


def build_org_affiliation(resources, resource_list):
    fp = """{"resourceType": "Bundle","type": "transaction","entry": [ """

    with open("json_payloads/organization_affiliation_payload.json") as json_file:
        payload_string = json_file.read()

    for key in resources:
        rp = ""
        unique_uuid = str(uuid.uuid4())
        org_name = get_org_name(key, resource_list)

        rp = (
            payload_string.replace("$unique_uuid", unique_uuid)
            .replace("$identifier_uuid", unique_uuid)
            .replace("$version", "1")
            .replace("$orgID", key)
            .replace("$orgName", org_name)
        )

        locations = []
        for x in resources[key]:
            y = {}
            z = x.split(":")
            y["reference"] = "Location/" + str(z[0])
            y["display"] = str(z[1])
            locations.append(y)

        obj = json.loads(rp)
        obj["resource"]["location"] = locations
        rp = json.dumps(obj)

        fp = fp + rp + ","

    fp = fp[:-1] + " ] } "
    return fp


# This function builds a json payload
# which is posted to the api to create resources
def build_payload(resource_type, resources, resource_payload_file):
    logging.info("Building request payload")
    initial_string = """{"resourceType": "Bundle","type": "transaction","entry": [ """
    final_string = " "
    with open(resource_payload_file) as json_file:
        payload_string = json_file.read()

    for resource in resources:
        try:
            if resource[2] == "update":
                # use the provided id
                unique_uuid = resource[4]
                identifier_uuid = resource[4] if resource[5] == "" else resource[5]
            else:
                # generate a new uuid
                unique_uuid = str(uuid.uuid4())
                identifier_uuid = unique_uuid
        except IndexError:
            # default if method is not provided
            unique_uuid = str(uuid.uuid4())
            identifier_uuid = unique_uuid

        # ps = payload_string
        ps = (
            payload_string.replace("$name", resource[0])
            .replace("$unique_uuid", unique_uuid)
            .replace("$identifier_uuid", identifier_uuid)
        )

        try:
            ps = ps.replace("$status", resource[1])
        except IndexError:
            ps = ps.replace("$status", "active")

        try:
            ps = ps.replace("$version", resource[3])
        except IndexError:
            ps = ps.replace("$version", "1")

        if resource_type == "organizations":
            ps = organization_extras(resource, ps)
        elif resource_type == "locations":
            ps = location_extras(resource, ps)
        elif resource_type == "careTeams":
            ps = care_team_extras(resource, ps, "min", [], [], "orgs & users")

        final_string = final_string + ps + ","

    final_string = initial_string + final_string[:-1] + " ] } "
    return final_string


@click.command()
@click.option("--csv_file", required=True)
@click.option("--resource_type", required=False)
@click.option("--assign", required=False)
@click.option(
    "--log_level", type=click.Choice(["DEBUG", "INFO", "ERROR"], case_sensitive=False)
)
def main(csv_file, resource_type, assign, log_level):
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
                "locations", resource_list, "json_payloads/locations_payload.json"
            )
            post_request("POST", json_payload, config.fhir_base_url)
            logging.info("Processing complete!")
        elif resource_type == "organizations":
            logging.info("Processing organizations")
            json_payload = build_payload(
                "organizations",
                resource_list,
                "json_payloads/organizations_payload.json",
            )
            post_request("POST", json_payload, config.fhir_base_url)
            logging.info("Processing complete!")
        elif resource_type == "careTeams":
            logging.info("Processing CareTeams")
            json_payload = build_payload(
                "careTeams", resource_list, "json_payloads/careteams_payload.json"
            )
            post_request("POST", json_payload, config.fhir_base_url)
            logging.info("Processing complete!")
        elif assign == "organization-Location":
            logging.info("Assigning Organizations to Locations")
            matches = extract_matches(resource_list)
            json_payload = build_org_affiliation(matches, resource_list)
            post_request("POST", json_payload, config.fhir_base_url)
            logging.info("Processing complete!")
        elif assign == "careTeam-Organization":
            logging.info("Assigning CareTeam to Organization")
            matches = extract_matches(resource_list)
            json_payload = fetch_and_build(matches, "orgs")
            post_request("POST", json_payload, config.fhir_base_url)
            logging.info("Processing complete!")
        elif assign == "user-careTeam":
            logging.info("Assing users to careTeam")
            matches = extract_matches(resource_list)
            json_payload = fetch_and_build(matches, "users")
            post_request("POST", json_payload, config.fhir_base_url)
            logging.info("Processing complete!")
        else:
            logging.error("Unsupported resource type!")
    else:
        logging.error("Empty csv file!")


if __name__ == "__main__":
    main()
