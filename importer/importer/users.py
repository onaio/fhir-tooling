import json
import logging
import pathlib
import uuid

from importer.builder import get_base_url
from importer.config.settings import api_service
from importer.request import handle_request

dir_path = str(pathlib.Path(__file__).parent.resolve())
json_path = "/".join([dir_path, "../json_payloads/"])


def get_keycloak_url():
    return api_service.auth_options.keycloak_realm_uri


# This function builds the user payload and posts it to
# the keycloak api to create a new user
# it also adds the user to the provided keycloak group
# and sets the user password
def create_user(user):
    (
        firstName,
        lastName,
        username,
        email,
        userId,
        userType,
        _,
        keycloakGroupId,
        keycloakGroupName,
        appId,
        password,
    ) = user

    with open(json_path + "keycloak_user_payload.json") as json_file:
        payload_string = json_file.read()

    obj = json.loads(payload_string)
    obj["firstName"] = firstName
    obj["lastName"] = lastName
    obj["username"] = username
    obj["email"] = email
    obj["attributes"]["fhir_core_app_id"][0] = appId

    final_string = json.dumps(obj, ensure_ascii=False)
    logging.info("Creating user: " + username)
    _keycloak_url = get_keycloak_url()
    r = handle_request("POST", final_string, _keycloak_url + "/users")
    if r.status_code == 201:
        logging.info("User created successfully")
        new_user_location = r.headers["Location"]
        user_id = (new_user_location.split("/"))[-1]

        # add user to group
        payload = (
            '{"id": "' + keycloakGroupId + '", "name": "' + keycloakGroupName + '"}'
        )
        group_endpoint = user_id + "/groups/" + keycloakGroupId
        url = _keycloak_url + "/users/" + group_endpoint
        logging.info("Adding user to Keycloak group: " + keycloakGroupName)
        r = handle_request("PUT", payload, url)

        # set password
        payload = '{"temporary":false,"type":"password","value":"' + password + '"}'
        password_endpoint = user_id + "/reset-password"
        url = _keycloak_url + "/users/" + password_endpoint
        logging.info("Setting user password")
        r = handle_request("PUT", payload, url)

        return user_id, {}
    else:
        logging.info(r.text)
        obj = {"task": "Create user", "row": str(user), "error": r.text}
        return 0, obj


# This function build the FHIR resources related to a
# new user and posts them to the FHIR api for creation
def create_user_resources(user_id, user):
    logging.info("Creating user resources")
    (
        firstName,
        lastName,
        username,
        email,
        id,
        userType,
        enableUser,
        keycloakGroupId,
        keycloakGroupName,
        _,
        password,
    ) = user

    # generate uuids
    if len(str(id).strip()) == 0:
        practitioner_uuid = str(
            uuid.uuid5(
                uuid.NAMESPACE_DNS, username + keycloakGroupId + "practitioner_uuid"
            )
        )
    else:
        practitioner_uuid = id

    group_uuid = str(
        uuid.uuid5(uuid.NAMESPACE_DNS, username + keycloakGroupId + "group_uuid")
    )
    practitioner_role_uuid = str(
        uuid.uuid5(
            uuid.NAMESPACE_DNS, username + keycloakGroupId + "practitioner_role_uuid"
        )
    )

    # get payload and replace strings
    initial_string = """{"resourceType": "Bundle","type": "transaction","meta": {"lastUpdated": ""},"entry": """
    with open(json_path + "user_resources_payload.json") as json_file:
        payload_string = json_file.read()

    # replace the variables in payload
    ff = (
        payload_string.replace("$practitioner_uuid", practitioner_uuid)
        .replace("$keycloak_user_uuid", user_id)
        .replace("$firstName", firstName)
        .replace("$lastName", lastName)
        .replace("$email", email)
        .replace('"$enable_user"', enableUser)
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
    ff = json.dumps(obj, indent=2, ensure_ascii=False)

    payload = initial_string + ff + "}"
    return payload


def confirm_keycloak_user(user):
    # Confirm that the keycloak user details are as expected
    user_username = str(user[2]).strip()
    user_email = str(user[3]).strip()
    _keycloak_url = get_keycloak_url()
    response = handle_request(
        "GET", "", _keycloak_url + "/users?exact=true&username=" + user_username
    )
    logging.debug(response)

    try:
        json_response = json.loads(response[0])
    except Exception as e:
        logging.error(f"Invalid JSON response from Keycloak: {e}")
        return 0, {"task": "Confirm Keycloak user", "row": str(user), "error": "Invalid JSON response"}

    if not json_response:
        message = f"No Keycloak user found for username: {user_username}"
        logging.error(message)
        return 0, {"task": "Confirm Keycloak user", "row": str(user), "error": message}

    user_data = json_response[0]
    response_email = user_data.get("email", "")
    response_username = user_data.get("username")

    try:
        response_username = json_response[0]["username"]
    except IndexError:
        message = "Username not found! Skipping user: " + str(user[2])
        logging.error(message)
        obj = {"task": "Confirm Keycloak user", "row": str(user), "error": message}
        return 0, obj

    if response_username != user_username:
        message = "Username does not match! Skipping user: " + str(user[2])
        logging.error(message)
        obj = {"task": "Confirm Keycloak user", "row": str(user), "error": message}
        return 0, obj

    if len(response_email) > 0 and response_email != user_email:
        logging.error("Email does not match for user: " + str(user[2]))

    keycloak_id = user_data.get("id")
    logging.info("User confirmed with id: " + str(keycloak_id))
    return keycloak_id, {}


def confirm_practitioner(user, user_id):
    practitioner_uuid = str(user[4]).strip()
    base_url = get_base_url()
    if not practitioner_uuid:
        # If practitioner uuid not provided in csv, check if any practitioners exist linked to the keycloak user_id
        r = handle_request("GET", "", base_url + "/Practitioner?identifier=" + user_id)
        json_r = json.loads(r[0])
        counter = json_r["total"]
        if counter > 0:
            message = (
                "User "
                + str(user[2])
                + " is linked to "
                + str(counter)
                + " Practitioner(s)"
            )
            logging.info(message)
            obj = {"task": "Confirm Practitioner", "row": str(user), "error": message}
            return True, obj
        else:
            return False, {}

    r = handle_request("GET", "", base_url + "/Practitioner/" + practitioner_uuid)

    if r[1] == 404:
        logging.info("Practitioner does not exist, proceed to creation")
        return False, {}
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
                return True, {}
            else:
                message = (
                    "The Keycloak user and Practitioner are not linked as expected"
                )
                logging.error(message)
                obj = {
                    "task": "Confirm Practitioner",
                    "row": str(user),
                    "error": message,
                }
                return True, obj

        except Exception as err:
            message = (
                "Error occurred trying to find Practitioner. The error is: " + str(err)
            )
            logging.error(message)
            obj = {"task": "Confirm Practitioner", "row": str(user), "error": message}
            return True, obj


def create_roles(role_list):
    for role in role_list:
        current_role = str(role[0])
        _keycloak_url = get_keycloak_url()
        logging.debug("The current role is: " + current_role)

        # check if role already exists
        role_response = handle_request(
            "GET", "", _keycloak_url + "/roles/" + current_role
        )
        logging.debug(role_response)
        if current_role in role_response[0]:
            logging.error("A role already exists with the name " + current_role)
        else:
            role_payload = '{"name": "' + current_role + '"}'
            create_role = handle_request("POST", role_payload, _keycloak_url + "/roles")
            if create_role.status_code == 201:
                logging.info("Successfully created role: " + current_role)

        try:
            # check if role has composite roles
            if role[1]:
                logging.debug("Role has composite roles")
                # get roled id
                full_role = handle_request(
                    "GET", "", _keycloak_url + "/roles/" + current_role
                )
                json_resp = json.loads(full_role[0])
                role_id = json_resp["id"]
                logging.debug("roleId: " + str(role_id))

                # get all available roles
                available_roles = handle_request(
                    "GET",
                    "",
                    _keycloak_url
                    + "/roles-by-id/" + role_id
                    + "/composites",
                )
                json_roles = json.loads(available_roles[0])
                logging.debug("json_roles: " + str(json_roles))

                rolesMap = {}

                for jrole in json_roles:
                    rolesMap[str(jrole["name"])] = jrole

                associated_roles = str(role[2])
                associated_role_array = associated_roles.split("|")
                arr = []
                for arole in associated_role_array:
                    if arole not in rolesMap.keys():
                        role_payload = '{"name": "' + arole + '"}'
                        arr.append(role_payload)
                    else:
                        logging.info("Role " + arole + " exists")


                payload_arr = json.dumps(arr, ensure_ascii=False)
                logging.info("Payload array: " + payload_arr)
                handle_request(
                    "POST",
                    payload_arr,
                    _keycloak_url + "/roles-by-id/" + role_id + "/composites",
                )

        except IndexError:
            pass


def get_group_id(group):
    _keycloak_url = get_keycloak_url()
    # check if group exists
    all_groups = handle_request("GET", "", _keycloak_url + "/groups")
    json_groups = json.loads(all_groups[0])
    group_obj = {}

    for a_group in json_groups:
        group_obj[a_group["name"]] = a_group

    if group in group_obj.keys():
        gid = str(group_obj[group]["id"])
        logging.info("Group already exists with id : " + gid)
        return gid

    else:
        logging.info("Group does not exists, lets create it")
        # create the group
        create_group_payload = '{"name":"' + group + '"}'
        handle_request("POST", create_group_payload, _keycloak_url + "/groups")
        return get_group_id(group)


def assign_group_roles(role_list, group, roles_max):
    _keycloak_url = get_keycloak_url()
    group_id = get_group_id(group)
    logging.debug("The groupID is: " + group_id)

    # get available roles
    available_roles_for_group = handle_request(
        "GET",
        "",
        _keycloak_url
        + "/groups/" + group_id
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

    json_assign_payload = json.dumps(assign_payload, ensure_ascii=False)
    handle_request(
        "POST",
        json_assign_payload,
        _keycloak_url + "/groups/" + group_id + "/role-mappings/realm",
    )


def assign_default_groups_roles(roles_max):
    DEFAULT_GROUPS = {
        "ANDROID_PRACTITIONER": ["ANDROID_CLIENT"],
        "WEB_PRACTITIONER": ["WEB_CLIENT"],
    }
    for group_name, roles in DEFAULT_GROUPS.items():
        assign_group_roles(roles, group_name, roles_max)
