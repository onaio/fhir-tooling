import csv
import json
import uuid
import click
import requests
import logging
import backoff
from datetime import datetime
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
@backoff.on_exception(backoff.expo, requests.exceptions.RequestException, max_time=180)
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

    if request_type == "POST":
        return requests.post(url, data=payload, headers=headers)
    elif request_type == "PUT":
        return requests.put(url, data=payload, headers=headers)
    elif request_type == "GET":
        return requests.get(url, headers=headers)
    elif request_type == "DELETE":
        return requests.delete(url, headers=headers)
    else:
        logging.error("Unsupported request type!")


def handle_request(request_type, payload, url):
    try:
        response = post_request(request_type, payload, url)
        if response.status_code == 200 or response.status_code == 201:
            logging.info("[" + str(response.status_code) + "]" + ": SUCCESS!")

        if request_type == "GET":
            return response.text, response.status_code
        else:
            return response
    except Exception as err:
        logging.error(err)


# This function builds the user payload and posts it to
# the keycloak api to create a new user
# it also adds the user to the provided keycloak group
# and sets the user password
def create_user(user):
    (firstName, lastName, username, email, id, userType, _, keycloakGroupID,
     keycloakGroupName,  applicationID, password) = user

    with open("json_payloads/keycloak_user_payload.json") as json_file:
        payload_string = json_file.read()

    obj = json.loads(payload_string)
    obj["firstName"] = firstName
    obj["lastName"] = lastName
    obj["username"] = username
    obj["email"] = email
    obj["attributes"]["fhir_core_app_id"][0] = applicationID

    final_string = json.dumps(obj)
    logging.info("Creating user: " + username)
    r = handle_request("POST", final_string, config.keycloak_url + "/users")

    if r.status_code == 201:
        logging.info("User created successfully")
        new_user_location = r.headers["Location"]
        user_id = (new_user_location.split("/"))[-1]

        # add user to group
        payload = '{"id": "' + keycloakGroupID + '", "name": "' + keycloakGroupName + '"}'
        group_endpoint = user_id + "/groups/" + keycloakGroupID
        url = config.keycloak_url + "/users/" + group_endpoint
        logging.info("Adding user to Keycloak group: " + keycloakGroupName)
        r = handle_request("PUT", payload, url)

        # set password
        payload = '{"temporary":false,"type":"password","value":"' + password + '"}'
        password_endpoint = user_id + "/reset-password"
        url = config.keycloak_url + "/users/" + password_endpoint
        logging.info("Setting user password")
        r = handle_request("PUT", payload, url)

        return user_id
    else:
        return 0


# This function build the FHIR resources related to a
# new user and posts them to the FHIR api for creation
def create_user_resources(user_id, user):
    logging.info("Creating user resources")
    (firstName, lastName, username, email, id, userType,
     _, keycloakGroupID, keycloakGroupName, _, password) = user

    # generate uuids
    if len(str(id).strip()) == 0:
        practitioner_uuid = str(
            uuid.uuid5(
                uuid.NAMESPACE_DNS, username + keycloakGroupID + "practitioner_uuid"
            )
        )
    else:
        practitioner_uuid = id

    group_uuid = str(
        uuid.uuid5(uuid.NAMESPACE_DNS, username + keycloakGroupID + "group_uuid")
    )
    practitioner_role_uuid = str(
        uuid.uuid5(
            uuid.NAMESPACE_DNS, username + keycloakGroupID + "practitioner_role_uuid"
        )
    )

    # get payload and replace strings
    initial_string = """{"resourceType": "Bundle","type": "transaction","meta": {"lastUpdated": ""},"entry": """
    with open("json_payloads/user_resources_payload.json") as json_file:
        payload_string = json_file.read()

    # replace the variables in payload
    ff = (
        payload_string.replace("$practitioner_uuid", practitioner_uuid)
        .replace("$keycloak_user_uuid", user_id)
        .replace("$firstName", firstName)
        .replace("$lastName", lastName)
        .replace("$email", email)
        .replace("$group_uuid", group_uuid)
        .replace("$practitioner_role_uuid", practitioner_role_uuid)
    )

    obj = json.loads(ff)
    if userType.strip() == "Supervisor":
        obj[2]["resource"]["code"] = {
            "coding": [
                {
                    "system": "http://snomed.info/sct",
                    "code": "236321002",
                    "display": "Supervisor (occupation)",
                }
            ]
        }
    elif userType.strip() == "Practitioner":
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
    return payload


# custom extras for organizations
def organization_extras(resource, payload_string):
    _, active, *_, alias = resource
    try:
        if alias:
            payload_string = payload_string.replace("$alias", alias)
        else:
            obj = json.loads(payload_string)
            del obj["resource"]["alias"]
            payload_string = json.dumps(obj, indent=4)
    except IndexError:
        obj = json.loads(payload_string)
        del obj["resource"]["alias"]
        payload_string = json.dumps(obj, indent=4)

    try:
        payload_string = payload_string.replace("$active", active)
    except IndexError:
        payload_string = payload_string.replace("$active", "true")
    return payload_string


# custom extras for locations
def location_extras(resource, payload_string):
    name, *_, parentName, parentID, type, typeCode, physicalType, physicalTypeCode = resource
    try:
        if parentName:
            payload_string = payload_string.replace("$parentName", parentName).replace(
                "$parentID", parentID
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
        if len(type.strip()) > 0:
            payload_string = payload_string.replace("$t_display", type)
        if len(typeCode.strip()) > 0:
            payload_string = payload_string.replace("$t_code", typeCode)
        else:
            obj = json.loads(payload_string)
            del obj["resource"]["type"]
            payload_string = json.dumps(obj, indent=4)
    except IndexError:
        obj = json.loads(payload_string)
        del obj["resource"]["type"]
        payload_string = json.dumps(obj, indent=4)

    try:
        if len(physicalType.strip()) > 0:
            payload_string = payload_string.replace("$pt_display", physicalType)
        if len(physicalTypeCode.strip()) > 0:
            payload_string = payload_string.replace("$pt_code", physicalTypeCode)
        else:
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

    *_, organizations, participants = resource
    if load_type == "min":
        try:
            if organizations:
                elements = organizations.split("|")
        except IndexError:
            pass
        try:
            if participants:
                elements2 = participants.split("|")
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
        group_name, group_id, item_name, item_id = resource
        if group_id.strip() and item_id.strip():
            if group_id not in teamMap.keys():
                teamMap[group_id] = [item_id + ":" + item_name]
            else:
                teamMap[group_id].append(item_id + ":" + item_name)
        else:
            logging.error("Missing required id: Skipping " + str(resource))
    return teamMap


def fetch_and_build(extracted_matches, ftype):
    fp = """{"resourceType": "Bundle","type": "transaction","entry": [ """

    for key in extracted_matches:
        # hit api to get current payload
        endpoint = config.fhir_base_url + "/CareTeam/" + key
        fetch_payload = handle_request("GET", "", endpoint)

        obj = json.loads(fetch_payload[0])
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
        unique_uuid = str(uuid.uuid5(uuid.NAMESPACE_DNS, key))
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


# This function is used to Capitalize the 'resource_type'
# and remove the 's' at the end, a version suitable with the API
def get_valid_resource_type(resource_type):
    logging.debug("Modify the string resource_type")
    modified_resource_type = resource_type[0].upper() + resource_type[1:-1]
    return modified_resource_type


# This function gets the current resource version from the API
def get_resource(resource_id, resource_type):
    resource_type = get_valid_resource_type(resource_type)
    resource_url = "/".join([config.fhir_base_url, resource_type, resource_id])
    response = handle_request("GET", "", resource_url)
    return json.loads(response[0])["meta"]["versionId"] if response[1] == 200 else "0"


# This function builds a json payload
# which is posted to the api to create resources
def build_payload(resource_type, resources, resource_payload_file):
    logging.info("Building request payload")
    initial_string = """{"resourceType": "Bundle","type": "transaction","entry": [ """
    final_string = " "
    with open(resource_payload_file) as json_file:
        payload_string = json_file.read()

    for resource in resources:
        name, status, method, id, *_ = resource
        try:
            if method == "create":
                version = "1"
                if len(id.strip()) > 0:
                    # use the provided id
                    unique_uuid = id.strip()
                    identifier_uuid = id.strip()
                else:
                    # generate a new uuid
                    unique_uuid = str(uuid.uuid5(uuid.NAMESPACE_DNS, name))
                    identifier_uuid = unique_uuid
        except IndexError:
            # default if method is not provided
            unique_uuid = str(uuid.uuid5(uuid.NAMESPACE_DNS, name))
            identifier_uuid = unique_uuid
            version = "1"

        try:
            if method == "update":
                if len(id.strip()) > 0:
                    version = get_resource(id, resource_type)
                    if version != "0":
                        # use the provided id
                        unique_uuid = id.strip()
                        identifier_uuid = id.strip()
                    else:
                        logging.info("Failed to get resource!")
                        raise ValueError("Trying to update a Non-existent resource")
                else:
                    logging.info("The id is required!")
                    raise ValueError("The id is required to update a resource")
        except IndexError:
            raise ValueError("The id is required to update a resource")

        # ps = payload_string
        ps = (
            payload_string.replace("$name", name)
            .replace("$unique_uuid", unique_uuid)
            .replace("$identifier_uuid", identifier_uuid)
            .replace("$version", version)
        )

        try:
            ps = ps.replace("$status", status)
        except IndexError:
            ps = ps.replace("$status", "active")

        if resource_type == "organizations":
            ps = organization_extras(resource, ps)
        elif resource_type == "locations":
            ps = location_extras(resource, ps)
        elif resource_type == "careTeams":
            ps = care_team_extras(resource, ps, "min", [], [], "orgs & users")

        final_string = final_string + ps + ","

    final_string = initial_string + final_string[:-1] + " ] } "
    return final_string


def confirm_keycloak_user(user):
    # Confirm that the keycloak user details are as expected
    user_username = str(user[2]).strip()
    user_email = str(user[3]).strip()
    response = handle_request(
        "GET", "", config.keycloak_url + "/users?exact=true&username=" + user_username
    )
    logging.debug(response)
    json_response = json.loads(response[0])

    try:
        response_email = json_response[0]["email"]
    except IndexError:
        response_email = ""

    try:
        response_username = json_response[0]["username"]
    except IndexError:
        logging.error("Skipping user: " + str(user))
        logging.error("Username not found!")
        return 0

    if response_username != user_username:
        logging.error("Skipping user: " + str(user))
        logging.error("Username does not match")
        return 0

    if len(response_email) > 0 and response_email != user_email:
        logging.error("Email does not match for user: " + str(user))

    keycloak_id = json_response[0]["id"]
    logging.info("User confirmed with id: " + keycloak_id)
    return keycloak_id


def confirm_practitioner(user, user_id):
    practitioner_uuid = str(user[4]).strip()

    if not practitioner_uuid:
        # If practitioner uuid not provided in csv, check if any practitioners exist linked to the keycloak user_id
        r = handle_request(
            "GET", "", config.fhir_base_url + "/Practitioner?identifier=" + user_id
        )
        json_r = json.loads(r[0])
        counter = json_r["total"]
        if counter > 0:
            logging.info(
                str(counter) + " Practitioner(s) exist, linked to the provided user"
            )
            return True
        else:
            return False

    r = handle_request(
        "GET", "", config.fhir_base_url + "/Practitioner/" + practitioner_uuid
    )

    if r[1] == 404:
        logging.info("Practitioner does not exist, proceed to creation")
        return False
    else:
        try:
            json_r = json.loads(r[0])
            identifiers = json_r["identifier"]
            keycloak_id = 0
            for id in identifiers:
                if id["use"] == "secondary":
                    keycloak_id = id["value"]

            if str(keycloak_id) == user_id:
                logging.info(
                    "The Keycloak user and Practitioner are linked as expected"
                )
                return True
            else:
                logging.error(
                    "The Keycloak user and Practitioner are not linked as exppected"
                )
                return True

        except Exception as err:
            logging.error("Error occured trying to find Practitioner: " + str(err))
            return True


def create_roles(role_list, roles_max):
    for role in role_list:
        current_role = str(role[0])
        logging.debug("The current role is: " + current_role)

        # check if role already exists
        role_response = handle_request(
            "GET", "", config.keycloak_url + "/roles/" + current_role
        )
        logging.debug(role_response)
        if current_role in role_response[0]:
            logging.error("A role already exists with the name " + current_role)
        else:
            role_payload = '{"name": "' + current_role + '"}'
            create_role = handle_request(
                "POST", role_payload, config.keycloak_url + "/roles"
            )
            if create_role.status_code == 201:
                logging.info("Successfully created role: " + current_role)

        try:
            # check if role has composite roles
            if role[1]:
                logging.debug("Role has composite roles")
                # get roled id
                full_role = handle_request(
                    "GET", "", config.keycloak_url + "/roles/" + current_role
                )
                json_resp = json.loads(full_role[0])
                role_id = json_resp["id"]
                logging.debug("roleId: " + str(role_id))

                # get all available roles
                available_roles = handle_request(
                    "GET",
                    "",
                    config.keycloak_url
                    + "/admin-ui-available-roles/roles/"
                    + role_id
                    + "?first=0&max="
                    + str(roles_max)
                    + "&search=",
                )
                json_roles = json.loads(available_roles[0])
                logging.debug("json_roles: " + str(json_roles))

                rolesMap = {}

                for jrole in json_roles:
                    # remove client and clientId, then rename role to name
                    # to build correct payload
                    del jrole["client"]
                    del jrole["clientId"]
                    jrole["name"] = jrole["role"]
                    del jrole["role"]
                    rolesMap[str(jrole["name"])] = jrole

                associated_roles = str(role[2])
                logging.debug("Associated roles: " + associated_roles)
                associated_role_array = associated_roles.split("|")
                arr = []
                for arole in associated_role_array:
                    if arole in rolesMap.keys():
                        arr.append(rolesMap[arole])
                    else:
                        logging.error("Role " + arole + "does not exist")

                payload_arr = json.dumps(arr)
                handle_request(
                    "POST",
                    payload_arr,
                    config.keycloak_url + "/roles-by-id/" + role_id + "/composites",
                )

        except IndexError:
            pass


def get_group_id(group):
    # check if group exists
    all_groups = handle_request("GET", "", config.keycloak_url + "/groups")
    json_groups = json.loads(all_groups[0])
    group_obj = {}

    for agroup in json_groups:
        group_obj[agroup["name"]] = agroup

    if group in group_obj.keys():
        gid = str(group_obj[group]["id"])
        logging.info("Group already exists with id : " + gid)
        return gid

    else:
        logging.info("Group does not exists, lets create it")
        # create the group
        create_group_payload = '{"name":"' + group + '"}'
        handle_request("POST", create_group_payload, config.keycloak_url + "/groups")
        return get_group_id(group)


def assign_group_roles(role_list, group, roles_max):
    group_id = get_group_id(group)
    logging.debug("The groupID is: " + group_id)

    # get available roles
    available_roles_for_group = handle_request(
        "GET",
        "",
        config.keycloak_url
        + "/groups/"
        + group_id
        + "/role-mappings/realm/available?first=0&max="
        + str(roles_max),
    )
    json_roles = json.loads(available_roles_for_group[0])
    role_obj = {}

    for j in json_roles:
        role_obj[j["name"]] = j

    assign_payload = []
    for r in role_list:
        if r[0] in role_obj.keys():
            assign_payload.append(role_obj[r[0]])

    json_assign_payload = json.dumps(assign_payload)
    handle_request(
        "POST",
        json_assign_payload,
        config.keycloak_url + "/groups/" + group_id + "/role-mappings/realm",
    )


def delete_resource(resource_type, resource_id, cascade):
    if cascade:
        cascade = "?_cascade=delete"
    else:
        cascade = ""

    resource_url = "/".join(
        [config.fhir_base_url, resource_type, resource_id + cascade]
    )
    r = handle_request("DELETE", "", resource_url)
    logging.info(r.text)


def clean_duplicates(users, cascade_delete):
    for user in users:
        # get keycloak user uuid
        username = str(user[2].strip())
        user_details = handle_request(
            "GET", "", config.keycloak_url + "/users?exact=true&username=" + username
        )
        obj = json.loads(user_details[0])
        keycloak_uuid = obj[0]["id"]

        # get Practitioner(s)
        r = handle_request(
            "GET",
            "",
            config.fhir_base_url + "/Practitioner?identifier=" + keycloak_uuid,
        )
        practitioner_details = json.loads(r[0])
        count = practitioner_details["total"]

        try:
            practitioner_uuid_provided = str(user[4].strip())
        except IndexError:
            practitioner_uuid_provided = None

        if practitioner_uuid_provided:
            if count == 1:
                practitioner_uuid_returned = practitioner_details["entry"][0][
                    "resource"
                ]["id"]
                # confirm the uuid matches the one provided in csv
                if practitioner_uuid_returned == practitioner_uuid_provided:
                    logging.info("User " + username + " ok!")
                else:
                    logging.error(
                        "User "
                        + username
                        + "has 1 Practitioner but it does not match the provided uuid"
                    )
            elif count > 1:
                for x in practitioner_details["entry"]:
                    p_uuid = x["resource"]["id"]
                    if practitioner_uuid_provided == p_uuid:
                        # This is the correct resource, so skip it
                        continue
                    else:
                        logging.info(
                            "Deleting practitioner resource with uuid: " + str(p_uuid)
                        )
                        delete_resource("Practitioner", p_uuid, cascade_delete)
            else:
                # count is less than 1
                logging.info("No Practitioners found")


@click.command()
@click.option("--csv_file", required=True)
@click.option("--resource_type", required=False)
@click.option("--assign", required=False)
@click.option("--setup", required=False)
@click.option("--group", required=False)
@click.option("--roles_max", required=False, default=500)
@click.option("--cascade_delete", required=False, default=False)
@click.option(
    "--log_level", type=click.Choice(["DEBUG", "INFO", "ERROR"], case_sensitive=False)
)
def main(
    csv_file, resource_type, assign, setup, group, roles_max, cascade_delete, log_level
):
    if log_level == "DEBUG":
        logging.basicConfig(level=logging.DEBUG)
    elif log_level == "INFO":
        logging.basicConfig(level=logging.INFO)
    elif log_level == "ERROR":
        logging.basicConfig(level=logging.ERROR)

    start_time = datetime.now()
    logging.info("Start time: " + start_time.strftime("%H:%M:%S"))

    logging.info("Starting csv import...")
    resource_list = read_csv(csv_file)
    if resource_list:
        if resource_type == "users":
            logging.info("Processing users")
            for user in resource_list:
                user_id = create_user(user)
                if user_id == 0:
                    # user was not created above, check if it already exists
                    user_id = confirm_keycloak_user(user)
                if user_id != 0:
                    # user_id has been retrieved
                    # check practitioner
                    practitioner_exists = confirm_practitioner(user, user_id)
                    if not practitioner_exists:
                        payload = create_user_resources(user_id, user)
                        handle_request("POST", payload, config.fhir_base_url)
                logging.info("Processing complete!")
        elif resource_type == "locations":
            logging.info("Processing locations")
            json_payload = build_payload(
                "locations", resource_list, "json_payloads/locations_payload.json"
            )
            handle_request("POST", json_payload, config.fhir_base_url)
            logging.info("Processing complete!")
        elif resource_type == "organizations":
            logging.info("Processing organizations")
            json_payload = build_payload(
                "organizations",
                resource_list,
                "json_payloads/organizations_payload.json",
            )
            handle_request("POST", json_payload, config.fhir_base_url)
            logging.info("Processing complete!")
        elif resource_type == "careTeams":
            logging.info("Processing CareTeams")
            json_payload = build_payload(
                "careTeams", resource_list, "json_payloads/careteams_payload.json"
            )
            handle_request("POST", json_payload, config.fhir_base_url)
            logging.info("Processing complete!")
        elif assign == "organization-Location":
            logging.info("Assigning Organizations to Locations")
            matches = extract_matches(resource_list)
            json_payload = build_org_affiliation(matches, resource_list)
            handle_request("POST", json_payload, config.fhir_base_url)
            logging.info("Processing complete!")
        elif assign == "careTeam-Organization":
            logging.info("Assigning CareTeam to Organization")
            matches = extract_matches(resource_list)
            json_payload = fetch_and_build(matches, "orgs")
            handle_request("POST", json_payload, config.fhir_base_url)
            logging.info("Processing complete!")
        elif assign == "user-careTeam":
            logging.info("Assigning users to careTeam")
            matches = extract_matches(resource_list)
            json_payload = fetch_and_build(matches, "users")
            handle_request("POST", json_payload, config.fhir_base_url)
            logging.info("Processing complete!")
        elif setup == "roles":
            logging.info("Setting up keycloak roles")
            create_roles(resource_list, roles_max)
            if group:
                assign_group_roles(resource_list, group, roles_max)
            logging.info("Processing complete")
        elif setup == "clean_duplicates":
            logging.info("=========================================")
            logging.info(
                "You are about to clean/delete Practitioner resources on the HAPI server"
            )
            click.confirm("Do you want to continue?", abort=True)
            clean_duplicates(resource_list, cascade_delete)
            logging.info("Processing complete!")
        else:
            logging.error("Unsupported request!")
    else:
        logging.error("Empty csv file!")

    end_time = datetime.now()
    logging.info("End time: " + end_time.strftime("%H:%M:%S"))
    total_time = end_time - start_time
    logging.info("Total time: " + str(total_time.total_seconds()) + " seconds")

if __name__ == "__main__":
    main()
