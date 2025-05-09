import base64
import csv
import json
import logging
import os
import pathlib
import uuid
from datetime import datetime

import click
import magic
import requests
from dateutil.relativedelta import relativedelta

from importer.config.settings import (api_service, fhir_base_url,
                                      product_access_token)
from importer.request import handle_request

dir_path = str(pathlib.Path(__file__).parent.resolve())
json_path = "/".join([dir_path, "../json_payloads/"])


def identify_coding_object_index(array, current_system):
    for index, value in enumerate(array):
        list_of_systems = value["coding"][0]["system"]
        if current_system in list_of_systems:
            return index
        else:
            return -1


def get_base_url():
    return api_service.fhir_base_uri


def check_parent_admin_level(location_parent_id):
    base_url = get_base_url()
    resource_url = "/".join([base_url, "Location", location_parent_id])

    response = handle_request("GET", "", resource_url)
    obj = json.loads(response[0])
    if "type" in obj:
        response_type = obj["type"]
        current_system = "administrative-level"
        if current_system:
            index = identify_coding_object_index(response_type, current_system)
            if index >= 0:
                code = obj["type"][index]["coding"][0]["code"]
                admin_level = str(int(code) + 1)
                return admin_level
        else:
            return None
    else:
        return None


def extract_matches(resource_list):
    team_map = {}
    with click.progressbar(
        resource_list, label="Progress::Extract matches "
    ) as extract_progress:
        for resource in extract_progress:
            group_name, group_id, item_name, item_id = resource
            if group_id.strip() and item_id.strip():
                if group_id not in team_map.keys():
                    team_map[group_id] = [item_id + ":" + item_name]
                else:
                    team_map[group_id].append(item_id + ":" + item_name)
            else:
                logging.error("Missing required id: Skipping " + str(resource))
    return team_map


# custom extras for organizations
def organization_extras(resource, payload_string):
    try:
        _, org_active, *_ = resource
    except ValueError:
        org_active = "true"

    try:
        payload_string = payload_string.replace("$active", org_active)
    except IndexError:
        payload_string = payload_string.replace("$active", "true")
    return payload_string


# custom extras for locations
def location_extras(resource, payload_string, location_coding_system):
    try:
        (
            locationName,
            *_,
            location_parent_name,
            location_parent_id,
            location_type,
            location_type_code,
            location_admin_level,
            location_physical_type,
            location_physical_type_code,
            longitude,
            latitude,
        ) = resource
    except ValueError:
        location_parent_name = "parentName"
        location_parent_id = "ParentId"
        location_type = "type"
        location_type_code = "typeCode"
        location_admin_level = "adminLevel"
        location_physical_type = "physicalType"
        location_physical_type_code = "physicalTypeCode"
        longitude = "longitude"
        latitude = "latitude"

    try:
        if location_parent_id and location_parent_id != "parentId":
            payload_string = payload_string.replace("$parentID", location_parent_id)
            if not location_parent_name or location_parent_name == "parentName":
                obj = json.loads(payload_string)
                del obj["resource"]["partOf"]["display"]
                payload_string = json.dumps(obj, indent=2, ensure_ascii=False)
            else:
                payload_string = payload_string.replace(
                    "$parentName", location_parent_name
                )
        else:
            obj = json.loads(payload_string)
            del obj["resource"]["partOf"]
            payload_string = json.dumps(obj, indent=2, ensure_ascii=False)
    except IndexError:
        obj = json.loads(payload_string)
        del obj["resource"]["partOf"]
        payload_string = json.dumps(obj, indent=2, ensure_ascii=False)

    try:
        payload_string = payload_string.replace("$t_system", location_coding_system)
        if location_type and location_type != "type":
            payload_string = payload_string.replace("$t_display", escape_quotes(location_type))
        if location_type_code and location_type_code != "typeCode":
            payload_string = payload_string.replace("$t_code", location_type_code)
        else:
            obj = json.loads(payload_string)
            payload_type = obj["resource"]["type"]
            current_system = "location-type"
            index = identify_coding_object_index(payload_type, current_system)
            if index >= 0:
                del obj["resource"]["type"][index]
                payload_string = json.dumps(obj, indent=2, ensure_ascii=False)
    except IndexError:
        obj = json.loads(payload_string)
        payload_type = obj["resource"]["type"]
        current_system = "location-type"
        index = identify_coding_object_index(payload_type, current_system)
        if index >= 0:
            del obj["resource"]["type"][index]
            payload_string = json.dumps(obj, indent=2, ensure_ascii=False)

    try:
        if location_admin_level and location_admin_level != "adminLevel":
            payload_string = payload_string.replace(
                "$adminLevelCode", location_admin_level
            )
        else:
            if location_admin_level in resource:
                admin_level = check_parent_admin_level(location_parent_id)
                if admin_level:
                    payload_string = payload_string.replace(
                        "$adminLevelCode", admin_level
                    )
                else:
                    obj = json.loads(payload_string)
                    obj_type = obj["resource"]["type"]
                    current_system = "administrative-level"
                    index = identify_coding_object_index(obj_type, current_system)
                    del obj["resource"]["type"][index]
                    payload_string = json.dumps(obj, indent=2, ensure_ascii=False)
            else:
                obj = json.loads(payload_string)
                obj_type = obj["resource"]["type"]
                current_system = "administrative-level"
                index = identify_coding_object_index(obj_type, current_system)
                del obj["resource"]["type"][index]
                payload_string = json.dumps(obj, indent=2, ensure_ascii=False)
    except IndexError:
        if location_admin_level in resource:
            admin_level = check_parent_admin_level(location_parent_id)
            if admin_level:
                payload_string = payload_string.replace("$adminLevelCode", admin_level)
            else:
                obj = json.loads(payload_string)
                obj_type = obj["resource"]["type"]
                current_system = "administrative-level"
                index = identify_coding_object_index(obj_type, current_system)
                del obj["resource"]["type"][index]
                payload_string = json.dumps(obj, indent=2, ensure_ascii=False)
        else:
            obj = json.loads(payload_string)
            obj_type = obj["resource"]["type"]
            current_system = "administrative-level"
            index = identify_coding_object_index(obj_type, current_system)
            del obj["resource"]["type"][index]
            payload_string = json.dumps(obj, indent=2, ensure_ascii=False)

    try:
        if location_physical_type and location_physical_type != "physicalType":
            payload_string = payload_string.replace(
                "$pt_display", location_physical_type
            )
        if (
            location_physical_type_code
            and location_physical_type_code != "physicalTypeCode"
        ):
            payload_string = payload_string.replace(
                "$pt_code", location_physical_type_code
            )
        else:
            obj = json.loads(payload_string)
            del obj["resource"]["physicalType"]
            # also remove from type[]
            payload_type = obj["resource"]["type"]
            current_system = "location-physical-type"
            index = identify_coding_object_index(payload_type, current_system)
            if index >= 0:
                del obj["resource"]["type"][index]
            payload_string = json.dumps(obj, indent=2, ensure_ascii=False)
    except IndexError:
        obj = json.loads(payload_string)
        del obj["resource"]["physicalType"]
        payload_type = obj["resource"]["type"]
        current_system = "location-physical-type"
        index = identify_coding_object_index(payload_type, current_system)
        if index >= 0:
            del obj["resource"]["type"][index]
        payload_string = json.dumps(obj, indent=2, ensure_ascii=False)

    # check if type is empty
    obj = json.loads(payload_string)
    _type = obj["resource"]["type"]
    if not _type:
        del obj["resource"]["type"]
        payload_string = json.dumps(obj, indent=2, ensure_ascii=False)

    try:
        if longitude and longitude != "longitude" and latitude and latitude != "latitude":
            payload_string = payload_string.replace('"$longitude"', longitude).replace(
                '"$latitude"', latitude
            )
        else:
            obj = json.loads(payload_string)
            del obj["resource"]["position"]
            payload_string = json.dumps(obj, indent=2, ensure_ascii=False)
    except IndexError:
        obj = json.loads(payload_string)
        del obj["resource"]["position"]
        payload_string = json.dumps(obj, indent=2, ensure_ascii=False)

    return trim_json_strings(payload_string)


# custom extras for careTeams
def care_team_extras(resource, payload_string, f_type):
    orgs_list = []
    participant_list = []
    elements = []
    elements2 = []

    try:
        *_, organizations, participants = resource
    except ValueError:
        organizations = "organizations"
        participants = "participants"

    if organizations and organizations != "organizations":
        elements = organizations.split("|")
    else:
        logging.info("No organizations")

    if participants and participants != "participants":
        elements2 = participants.split("|")
    else:
        logging.info("No participants")

    if "orgs" in f_type:
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

        if len(participant_list) > 0:
            obj = json.loads(payload_string)
            obj["resource"]["participant"] = participant_list
            obj["resource"]["managingOrganization"] = orgs_list
            payload_string = json.dumps(obj, ensure_ascii=False)

    if "users" in f_type:
        if len(elements2) > 0:
            elements = elements2
        for user in elements:
            y = {"member": {}}
            x = user.split(":")
            y["member"]["reference"] = "Practitioner/" + str(x[0])
            y["member"]["display"] = str(x[1])
            participant_list.append(y)

        if len(participant_list) > 0:
            obj = json.loads(payload_string)
            obj["resource"]["participant"] = participant_list
            payload_string = json.dumps(obj, ensure_ascii=False)

    return payload_string


def encode_image(image_file):
    with open(image_file, "rb") as image:
        image_b64_data = base64.b64encode(image.read())
    return image_b64_data


# This function takes in the source url of an image, downloads it, encodes it,
# and saves it as a Binary resource. It returns the id of the Binary resource if
# successful and 0 if failed
def save_image(image_source_url):
    try:
        headers = {"Authorization": "Bearer " + product_access_token}
    except AttributeError:
        headers = {}

    data = requests.get(url=image_source_url, headers=headers)
    if not os.path.exists("images"):
        os.makedirs("images")

    if data.status_code == 200:
        with open("images/image_file", "wb") as image_file:
            image_file.write(data.content)

        # get file type
        mime = magic.Magic(mime=True)
        file_type = mime.from_file("images/image_file")

        encoded_image = encode_image("images/image_file")
        resource_id = str(uuid.uuid5(uuid.NAMESPACE_DNS, image_source_url))
        payload = {
            "resourceType": "Bundle",
            "type": "transaction",
            "entry": [
                {
                    "request": {
                        "method": "PUT",
                        "url": "Binary/" + resource_id,
                        "ifMatch": "1",
                    },
                    "resource": {
                        "resourceType": "Binary",
                        "id": resource_id,
                        "contentType": file_type,
                        "data": str(encoded_image),
                    },
                }
            ],
        }
        payload_string = json.dumps(payload, indent=2, ensure_ascii=False)
        response = handle_request("POST", payload_string, get_base_url())
        if response.status_code == 200:
            logging.info(response.text)
            return resource_id
        else:
            logging.error("Error while creating Binary resource")
            logging.error(response.text)
            return 0
    else:
        logging.error("Error while attempting to retrieve image")
        logging.error(data)
        return 0


def get_product_accountability_period(product_id: str) -> int:
    product_endpoint = "/".join([fhir_base_url, "Group", product_id])
    response = handle_request("GET", "", product_endpoint)
    if response[1] != 200:
        logging.error(
            "Error while attempting to get the accountability period from product : "
            + product_id
        )
        logging.error(response[0])
        return -1

    json_product = json.loads(response[0])
    product_characteristics = json_product["characteristic"]
    for character in product_characteristics:
        if (
            character["code"]["coding"][0]["system"] == "http://smartregister.org/codes"
            and character["code"]["coding"][0]["code"] == "67869606"
        ):
            accountability_period = character["valueQuantity"]["value"]
            return accountability_period
    logging.error(
        "Accountability period was not found in the product characteristics : "
        + product_id
    )
    return -1


def calculate_date(delivery_date: str, product_accountability_period: int) -> str:
    delivery_datetime = datetime.strptime(delivery_date, "%Y-%m-%dT%H:%M:%S.%fZ")
    end_date = delivery_datetime + relativedelta(months=product_accountability_period)
    end_date_str = end_date.strftime("%Y-%m-%dT%H:%M:%S.")
    milliseconds = end_date.microsecond // 1000
    end_date_str += f"{milliseconds:03d}Z"
    return end_date_str


# custom extras for product import
def group_extras(resource, payload_string, group_type, created_resources):
    payload_obj = json.loads(payload_string)
    item_name = resource[0]
    del_indexes = []
    del_identifier_indexes = []

    GROUP_INDEX_MAPPING = {
        "product_official_id_index": 0,
        "product_secondary_id_index": 1,
        "product_is_attractive_index": 0,
        "product_is_available_index": 1,
        "product_condition_index": 2,
        "product_appropriate_usage_index": 3,
        "product_accountability_period_index": 4,
        "product_image_index": 5,
        "inventory_official_id_index": 0,
        "inventory_secondary_id_index": 1,
        "inventory_usual_id_index": 2,
        "inventory_member_index": 0,
        "inventory_quantity_index": 0,
        "inventory_unicef_section_index": 1,
        "inventory_donor_index": 2,
        "inventory_rel_location_index": 0,
        "inventory_location_index": 1
    }

    if group_type == "product":
        (
            _,
            active,
            *_,
            material_number,
            previous_id,
            is_attractive_item,
            availability,
            condition,
            appropriate_usage,
            accountability_period,
            image_source_url,
        ) = resource

        if active:
            payload_obj["resource"]["active"] = active
        else:
            del payload_obj["resource"]["active"]

        if material_number:
            payload_obj["resource"]["identifier"][
                GROUP_INDEX_MAPPING["product_official_id_index"]
            ]["value"] = material_number
        else:
            del_identifier_indexes.append(
                GROUP_INDEX_MAPPING["product_official_id_index"]
            )

        if previous_id:
            payload_obj["resource"]["identifier"][
                GROUP_INDEX_MAPPING["product_secondary_id_index"]
            ]["value"] = previous_id
        else:
            del_identifier_indexes.append(
                GROUP_INDEX_MAPPING["product_secondary_id_index"]
            )

        if is_attractive_item:
            payload_obj["resource"]["characteristic"][
                GROUP_INDEX_MAPPING["product_is_attractive_index"]
            ]["valueBoolean"] = is_attractive_item
        else:
            del_indexes.append(GROUP_INDEX_MAPPING["product_is_attractive_index"])

        if availability:
            payload_obj["resource"]["characteristic"][
                GROUP_INDEX_MAPPING["product_is_available_index"]
            ]["valueCodeableConcept"]["text"] = escape_quotes(availability)
        else:
            del_indexes.append(GROUP_INDEX_MAPPING["product_is_available_index"])

        if condition:
            payload_obj["resource"]["characteristic"][
                GROUP_INDEX_MAPPING["product_condition_index"]
            ]["valueCodeableConcept"]["text"] = escape_quotes(condition)
        else:
            del_indexes.append(GROUP_INDEX_MAPPING["product_condition_index"])

        if appropriate_usage:
            payload_obj["resource"]["characteristic"][
                GROUP_INDEX_MAPPING["product_appropriate_usage_index"]
            ]["valueCodeableConcept"]["text"] = escape_quotes(appropriate_usage)
        else:
            del_indexes.append(GROUP_INDEX_MAPPING["product_appropriate_usage_index"])

        if accountability_period:
            payload_obj["resource"]["characteristic"][
                GROUP_INDEX_MAPPING["product_accountability_period_index"]
            ]["valueQuantity"]["value"] = accountability_period
        else:
            del_indexes.append(
                GROUP_INDEX_MAPPING["product_accountability_period_index"]
            )

        if image_source_url:
            image_binary = save_image(image_source_url)
            if image_binary != 0:
                payload_obj["resource"]["characteristic"][
                    GROUP_INDEX_MAPPING["product_image_index"]
                ]["valueReference"]["reference"] = ("Binary/" + image_binary)
                created_resources.append("Binary/" + image_binary)
            else:
                logging.error(
                    "Unable to link the image Binary resource for product " + item_name
                )
                del_indexes.append(GROUP_INDEX_MAPPING["product_image_index"])
        else:
            del_indexes.append(GROUP_INDEX_MAPPING["product_image_index"])

    elif group_type == "inventory":
        (
            _,
            active,
            *_,
            po_number,
            serial_number,
            usual_id,
            actual,
            product_id,
            delivery_date,
            accountability_date,
            quantity,
            unicef_section,
            donor,
            location,
        ) = resource

        if active:
            payload_obj["resource"]["active"] = bool(active)
        else:
            del payload_obj["resource"]["active"]

        if serial_number:
            payload_obj["resource"]["identifier"][
                GROUP_INDEX_MAPPING["inventory_official_id_index"]
            ]["value"] = serial_number
        else:
            del_identifier_indexes.append(
                GROUP_INDEX_MAPPING["inventory_official_id_index"]
            )

        if po_number:
            payload_obj["resource"]["identifier"][
                GROUP_INDEX_MAPPING["inventory_secondary_id_index"]
            ]["value"] = po_number
        else:
            del_identifier_indexes.append(
                GROUP_INDEX_MAPPING["inventory_secondary_id_index"]
            )

        if usual_id:
            payload_obj["resource"]["identifier"][
                GROUP_INDEX_MAPPING["inventory_usual_id_index"]
            ]["value"] = usual_id
        else:
            del_identifier_indexes.append(
                GROUP_INDEX_MAPPING["inventory_usual_id_index"]
            )

        if actual:
            payload_obj["resource"]["actual"] = bool(actual)
        else:
            del payload_obj["resource"]["actual"]

        if product_id:
            payload_obj["resource"]["member"][
                GROUP_INDEX_MAPPING["inventory_member_index"]
            ]["entity"]["reference"] = ("Group/" + product_id)
        else:
            payload_obj["resource"]["member"][
                GROUP_INDEX_MAPPING["inventory_member_index"]
            ]["entity"]["reference"] = "Group/"

        if delivery_date:
            payload_obj["resource"]["member"][
                GROUP_INDEX_MAPPING["inventory_member_index"]
            ]["period"]["start"] = delivery_date
        else:
            payload_obj["resource"]["member"][
                GROUP_INDEX_MAPPING["inventory_member_index"]
            ]["period"]["start"] = ""

        if accountability_date:
            payload_obj["resource"]["member"][
                GROUP_INDEX_MAPPING["inventory_member_index"]
            ]["period"]["end"] = accountability_date
        else:
            product_accountability_period = get_product_accountability_period(
                product_id
            )
            if product_accountability_period != -1:
                accountability_date = calculate_date(
                    delivery_date, product_accountability_period
                )
                payload_obj["resource"]["member"][
                    GROUP_INDEX_MAPPING["inventory_member_index"]
                ]["period"]["end"] = accountability_date
            else:
                payload_obj["resource"]["member"][
                    GROUP_INDEX_MAPPING["inventory_member_index"]
                ]["period"]["end"] = ""

        if quantity:
            payload_obj["resource"]["characteristic"][
                GROUP_INDEX_MAPPING["inventory_quantity_index"]
            ]["valueQuantity"]["value"] = int(quantity)
        else:
            del_indexes.append(GROUP_INDEX_MAPPING["inventory_quantity_index"])

        if unicef_section:
            payload_obj["resource"]["characteristic"][
                GROUP_INDEX_MAPPING["inventory_unicef_section_index"]
            ]["valueCodeableConcept"]["text"] = unicef_section
            payload_obj["resource"]["characteristic"][
                GROUP_INDEX_MAPPING["inventory_unicef_section_index"]
            ]["valueCodeableConcept"]["coding"][0]["code"] = unicef_section
            payload_obj["resource"]["characteristic"][
                GROUP_INDEX_MAPPING["inventory_unicef_section_index"]
            ]["valueCodeableConcept"]["coding"][0]["display"] = unicef_section
        else:
            del_indexes.append(GROUP_INDEX_MAPPING["inventory_unicef_section_index"])

        if donor:
            payload_obj["resource"]["characteristic"][
                GROUP_INDEX_MAPPING["inventory_donor_index"]
            ]["valueCodeableConcept"]["text"] = donor
            payload_obj["resource"]["characteristic"][
                GROUP_INDEX_MAPPING["inventory_donor_index"]
            ]["valueCodeableConcept"]["coding"][0]["code"] = donor
            payload_obj["resource"]["characteristic"][
                GROUP_INDEX_MAPPING["inventory_donor_index"]
            ]["valueCodeableConcept"]["coding"][0]["display"] = donor
        else:
            del_indexes.append(GROUP_INDEX_MAPPING["inventory_donor_index"])

        if location:
            payload_obj["resource"]["meta"]["tag"][GROUP_INDEX_MAPPING["inventory_rel_location_index"]
            ]["code"] = location
        else:
            del_indexes.append(GROUP_INDEX_MAPPING["inventory_rel_location_index"])

        if location:
            payload_obj["resource"]["meta"]["tag"][GROUP_INDEX_MAPPING["inventory_location_index"]
            ]["code"] = location
        else:
            del_indexes.append(GROUP_INDEX_MAPPING["inventory_location_index"])

    else:
        logging.info("Group type not defined")

    for x in reversed(del_indexes):
        del payload_obj["resource"]["characteristic"][x]
    for x in reversed(del_identifier_indexes):
        del payload_obj["resource"]["identifier"][x]

    payload_string = json.dumps(payload_obj, indent=2, ensure_ascii=False)
    return payload_string, created_resources


# This function is used to Capitalize the 'resource_type'
# and remove the 's' at the end, a version suitable with the API
def get_valid_resource_type(resource_type):
    logging.debug("Modify the string resource_type")
    modified_resource_type = resource_type[0].upper() + resource_type[1:-1]
    return modified_resource_type


# This function gets the current resource version from the API
def get_resource(resource_id, resource_type):
    if resource_type not in ["List", "Group", "Encounter", "Location"]:
        resource_type = get_valid_resource_type(resource_type)
    resource_url = "/".join([fhir_base_url, resource_type, resource_id])
    response = handle_request("GET", "", resource_url)
    return json.loads(response[0])["meta"]["versionId"] if response[1] == 200 else "0"


def check_for_nulls(resource: list) -> list:
    for index, value in enumerate(resource):
        if len(value.strip()) < 1:
            resource[index] = None
        else:
            resource[index] = value.strip()
    return resource


# This function builds a json payload
# which is posted to the api to create resources
def build_payload(
    resource_type,
    resources,
    resource_payload_file,
    created_resources=None,
    location_coding_system=None,
):
    logging.info("Building request payload")
    initial_string = """{"resourceType": "Bundle","type": "transaction","entry": ["""
    final_string = group_type = ""

    with open(resource_payload_file) as json_file:
        payload_string = json_file.read()

    with click.progressbar(
        resources, label="Progress::Building payload "
    ) as build_payload_progress:
        for resource in build_payload_progress:
            logging.info("\t")

            resource = check_for_nulls(resource)

            try:
                name, status, method, _id, *_ = resource
            except ValueError:
                name = resource[0]
                status = "" if len(resource) == 1 else resource[1]
                method = "create"
                _id = str(uuid.uuid5(uuid.NAMESPACE_DNS, name))

            if method == "create":
                version = "1"
                if _id:
                    unique_uuid = identifier_uuid = _id
                else:
                    unique_uuid = identifier_uuid = str(
                        uuid.uuid5(uuid.NAMESPACE_DNS, name)
                    )

            if method == "update":
                if _id:
                    version = get_resource(_id, resource_type)

                    if version != "0":
                        unique_uuid = identifier_uuid = _id
                    else:
                        logging.info("Failed to get resource!")
                        raise ValueError("Trying to update a Non-existent resource")
                else:
                    logging.info("The id is required!")
                    raise ValueError("The id is required to update a resource")

            # ps = payload_string
            ps = (
                payload_string.replace("$name", escape_quotes(name))
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
                ps = location_extras(resource, ps, location_coding_system)
            elif resource_type == "careTeams":
                ps = care_team_extras(resource, ps, "orgs & users")
            elif resource_type == "Group":
                if "inventory" in resource_payload_file:
                    group_type = "inventory"
                elif "product" in resource_payload_file:
                    group_type = "product"
                else:
                    logging.error("Undefined group type")
                ps, created_resources = group_extras(
                    resource, ps, group_type, created_resources
                )

            final_string = final_string + ps + ","

    final_string = initial_string + final_string.rstrip(",") + "]}"

    if "$" in final_string:
      logging.warning("Unresolved placeholders found in payload!")

    try:
        json.loads(final_string)
    except json.JSONDecodeError as e:
        logging.error("Invalid JSON generated: " + str(e))
        raise

    if group_type == "product":
     return final_string, created_resources

    return final_string


def get_org_name(key, resource_list):
    org_name = ""
    for x in resource_list:
        if x[1] == key:
            org_name = x[0]
    return org_name


def build_org_affiliation(resources, resource_list):
    fp = """{"resourceType": "Bundle","type": "transaction","entry": [ """

    with open(json_path + "organization_affiliation_payload.json") as json_file:
        payload_string = json_file.read()

    with click.progressbar(
        resources, label="Progress::Build payload "
    ) as build_progress:
        for key in build_progress:
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
            rp = json.dumps(obj, ensure_ascii=False)

            fp = fp + rp + ","

    fp = fp[:-1] + " ]}"
    return fp


def check_resource(subject, entries, resource_type, url_filter):
    if subject not in entries.keys():
        base_url = get_base_url()
        check_url = (
            base_url + "/" + resource_type + "/_search?_count=1&" + url_filter + subject
        )
        response = handle_request("GET", "", check_url)
        json_response = json.loads(response[0])

        entries[subject] = json_response
    return entries


def update_practitioner_role(resource, organization_id, organization_name):
    try:
        resource["organization"]["reference"] = "Organization/" + organization_id
        resource["organization"]["display"] = organization_name
    except KeyError:
        org = {
            "organization": {
                "reference": "Organization/" + organization_id,
                "display": organization_name,
            }
        }
        resource.update(org)
    return resource


def update_list(resource,location_id, inventory_id, supply_date):
    with open(json_path + "inventory_location_list_payload.json") as json_file:
        payload_string = json_file.read()

        payload_string = payload_string.replace("$supply_date", supply_date).replace(
            "$inventory_id", inventory_id).replace("$location_id", location_id)
        json_payload = json.loads(payload_string)

        try:
            entries = resource["entry"]
            if inventory_id not in str(entries):
                entry = json_payload["entry"][0]
                entries.append(entry)

        except KeyError:
            entry = {"entry": json_payload["entry"]}
            resource.update(entry)

        try:
            if location_id not in str(entries):
                meta = json_payload["meta"]
                resource["meta"].update(meta)

        except KeyError:
            meta = {"meta": json_payload["meta"]}
            resource.update(meta)
    return resource


def create_new_practitioner_role(
    new_id, practitioner_name, practitioner_id, organization_name, organization_id
):
    with open(json_path + "practitioner_organization_payload.json") as json_file:
        payload_string = json_file.read()

    payload_string = (
        payload_string.replace("$id", new_id)
        .replace("$practitioner_id", practitioner_id)
        .replace("$practitioner_name", practitioner_name)
        .replace("$organization_id", organization_id)
        .replace("$organization_name", organization_name)
    )
    resource = json.loads(payload_string)
    return resource


def create_new_list(new_id, location_id, inventory_id, title, supply_date):
    with open(json_path + "inventory_location_list_payload.json") as json_file:
        payload_string = json_file.read()

    payload_string = (
        payload_string.replace("$id", new_id)
        .replace("$title", title)
        .replace("$location_id", location_id)
        .replace("$supply_date", supply_date)
        .replace("$inventory_id", inventory_id)
    )
    resource = json.loads(payload_string)
    return resource


def build_assign_payload(rows, resource_type, url_filter):
    bundle = {"resourceType": "Bundle", "type": "transaction", "entry": []}

    subject_id = item_id = organization_name = practitioner_name = inventory_name = (
        supply_date
    ) = resource_id = version = ""
    entries = {}
    resource = {}
    results = {}

    for row in rows:
        if resource_type == "List":
            # inventory_name, inventory_id, supply_date, location_id
            inventory_name, item_id, supply_date, subject_id = row
        if resource_type == "PractitionerRole":
            # practitioner_name, practitioner_id, organization_name, organization_id
            practitioner_name, subject_id, organization_name, item_id = row

        get_content = check_resource(subject_id, entries, resource_type, url_filter)
        json_response = get_content[subject_id]

        if json_response["total"] == 1:
            logging.info("Updating existing resource")
            resource = json_response["entry"][0]["resource"]

            if resource_type == "PractitionerRole":
                resource = update_practitioner_role(
                    resource, item_id, organization_name
                )
            if resource_type == "List":
                resource = update_list(resource, subject_id, item_id, supply_date)

            if "meta" in resource:
                version = resource["meta"]["versionId"]
                resource_id = resource["id"]

        elif json_response["total"] == 0:
            logging.info("Creating a new resource")
            resource_id = str(uuid.uuid5(uuid.NAMESPACE_DNS, subject_id + item_id))

            if resource_type == "PractitionerRole":
                resource = create_new_practitioner_role(
                    resource_id,
                    practitioner_name,
                    subject_id,
                    organization_name,
                    item_id,
                )
            if resource_type == "List":
                resource = create_new_list(
                    resource_id, subject_id, item_id, inventory_name, supply_date
                )
            version = "1"

            try:
                resource["entry"] = (
                    entries[subject_id]["resource"]["resource"]["entry"]
                    + resource["entry"]
                )
            except KeyError:
                logging.debug("No existing entries")

        else:
            raise ValueError("The number of references should only be 0 or 1")

        payload = {
            "request": {
                "method": "PUT",
                "url": resource_type + "/" + resource_id,
                "ifMatch": version,
            },
            "resource": resource,
        }
        entries[subject_id]["resource"] = payload
        results[subject_id] = payload

    final_entries = []
    for entry in results:
        final_entries.append(results[entry])

    bundle["entry"] = final_entries
    return json.dumps(bundle, indent=2, ensure_ascii=False)


def build_group_list_resource(
    list_resource_id: str, csv_file: str, full_list_created_resources: list, title: str
):
    if not list_resource_id:
        list_resource_id = str(uuid.uuid5(uuid.NAMESPACE_DNS, csv_file))
    current_version = get_resource(list_resource_id, "List")
    method = "create" if current_version == str(0) else "update"
    resource = [[title, "current", method, list_resource_id]]

    if method == "create":
        result_payload = build_payload(
            "List", resource, "json_payloads/group_list_payload.json"
        )
        return process_resources_list(result_payload, full_list_created_resources)
    if method == "update":
        resource_url = "/".join([get_base_url(), "List", list_resource_id])
        response = handle_request("GET", "", resource_url)
        payload = {
            "resourceType": "Bundle",
            "type": "transaction",
            "entry": [
                {
                    "request": {
                        "method": "PUT",
                        "url": "List/" + list_resource_id,
                        "ifMatch": current_version
                    },
                    "resource": json.loads(response[0])
                }
            ]
        }
        resource_payload = json.dumps(payload, indent=2, ensure_ascii=False)
        return process_resources_list(resource_payload, full_list_created_resources)


# This function takes a 'created_resources' array and a response string
# It converts the response string to a json object, then loops through the entry array
# extracting all the referenced resources and adds them to the created_resources array
# then returns it
def extract_resources(created_resources, response_string):
    json_response = json.loads(response_string)
    entry = json_response["entry"]
    for item in entry:
        resource = item["response"]["location"]
        index = resource.find("/", resource.find("/") + 1)
        created_resources.append(resource[0:index])
    return created_resources


# This function takes a List resource payload and a list of resources
# It adds the resources into the List resource's entry array
# then returns the full resource payload
def process_resources_list(payload, resources_list):
    entry = []
    json_payload = json.loads(payload)

    try:
        entries = json_payload["entry"][0]["resource"]["entry"]
    except KeyError:
        entries = []
        json_payload["entry"][0]["resource"]["entry"] = entries  # Ensure key exists

    if len(entries) > 0:
        entry = entries

    for resource in resources_list:
        if resource not in str(entries):
            item = {"item": {"reference": resource}}
            entry.append(item)

    json_payload["entry"][0]["resource"]["entry"] = entry
    return json_payload


def link_to_location(resource_list):
    arr = []
    with click.progressbar(
        resource_list, label="Progress::Linking inventory to location"
    ) as link_locations_progress:
        for resource in link_locations_progress:
            try:
                if resource[14]:
                    # name, inventory_id, supply_date, location_id
                    resource_link = [
                        resource[0],
                        resource[3],
                        resource[9],
                        resource[14],
                    ]
                    arr.append(resource_link)
            except IndexError:
                logging.info("No location provided for " + resource[0])

        if len(arr) > 0:
            return build_assign_payload(arr, "List", "subject=Location/")
        else:
            return ""


def count_records(csv_filepath):
    with open(csv_filepath, newline="") as csvfile:
        reader = csv.reader(csvfile)
        return sum(1 for _ in reader) - 1


def process_response(response):
    json_response = json.loads(response)
    issues = json_response["issue"]
    return issues

def escape_quotes(value):
    if isinstance(value, str):
        return value.replace('"', '\\"')
    return value

def trim_json_strings(obj):
    if isinstance(obj, dict):
        return {k: trim_json_strings(v) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [trim_json_strings(i) for i in obj]
    elif isinstance(obj, str):
        return obj.strip()
    else:
        return obj

def build_report(csv_file, response, error_details, fail_count, fail_all):
    # Get total number of records
    total_records = count_records(csv_file)
    issues = []

    # Get status code
    if hasattr(response, "status_code") and response.status_code > 201:
        status = "Failed"
        processed_records = 0

        if response.text:
            issues = process_response(response.text)
            for issue in issues:
                del issue["code"]
    else:
        if fail_count > 0:
            status = "Failed"
            if fail_all:
                processed_records = total_records
            else:
                processed_records = total_records - fail_count
        else:
            status = "Completed"
            processed_records = total_records

    report = {
        "status": status,
        "totalRecords": total_records,
        "processedRecords": processed_records,
    }
    if len(issues) > 0:
        report["failedRecords"] = len(issues)

    all_errors = issues + error_details

    if len(all_errors) > 0:
        report["errorDetails"] = all_errors

    string_report = json.dumps(report, indent=2, ensure_ascii=False)
    logging.info("============================================================")
    logging.info("============================================================")
    logging.info(string_report)
    logging.info("============================================================")
    logging.info("============================================================")


def build_single_resource(
    resource_type, resource_id, practitioner_id, period_start, location_id, form_encounter,
    visit_encounter, subject, value_string="", note="",
):
    template_map = {
        "visit": "visit_encounter_payload.json",
        "flag": "flag_payload.json",
        "encounter": "flag_encounter_payload.json",
        "observation": "flag_observation_payload.json",
    }
    json_template = next(
        (template for key, template in template_map.items() if key in resource_type),
        None,
    )

    boolean_code = boolean_value = ""
    if "product" in resource_type:
        code = "PRODCHECK"
        display = text = "Product Check"
        c_code = "issue_details"
        c_display = c_text = value_string
    elif "service_point_check" in resource_type:
        code = "SPCHECK"
        display = text = "Service Point Check"
        c_code = "34657579"
        c_display = c_text = "Service Point Good Order Check"
        boolean_code = "373067005"
        boolean_value = "No (qualifier value)"
    elif "consult_beneficiaries" in resource_type:
        code = "CNBEN"
        display = text = "Consult Beneficiaries Visit"
        c_code = "77223346"
        c_display = c_text = "Consult Beneficiaries"
        boolean_code = "373066001"
        boolean_value = "Yes (qualifier value)"
    elif "warehouse_check" in resource_type:
        code = "WHCHECK"
        display = text = "Warehouse check Visit"
        c_code = "68561322"
        c_display = c_text = "Required action"
        boolean_code = "373066001"
        boolean_value = "Yes (qualifier value)"
    else:
        code = display = text = c_code = c_display = c_text = ""

    with open(json_path + json_template) as json_file:
        resource_payload = json_file.read()

    visit_encounter_vars = {
        "$id": resource_id,
        "$version": "1",
        "$category_code": code,
        "$category_display": display,
        "$category_text": text,
        "$code_code": c_code,
        "$code_display": c_display,
        "$code_text": c_text,
        "$practitioner_id": practitioner_id,
        "$start": period_start.replace(" ", "T"),
        "$end": period_start.replace(" ", "T"),
        "$subject": subject,
        "$location": location_id,
        "$form_encounter": form_encounter,
        "$visit_encounter": visit_encounter,
        "$value_string": json.dumps(value_string)[1:-1],
        "$boolean_code": boolean_code,
        "$boolean_value": boolean_value,
        "$note": json.dumps(note)[1:-1],
    }
    for var, value in visit_encounter_vars.items():
        resource_payload = resource_payload.replace(var, value)

    obj = json.loads(resource_payload)
    if "product_observation" in resource_type:
        del obj["resource"]["valueCodeableConcept"]
        del obj["resource"]["note"]
    if (
        "service_point_check_encounter" in resource_type
        or "consult_beneficiaries_encounter" in resource_type
    ):
        del obj["resource"]["subject"]
    if (
        "service_point_check_observation" in resource_type
        or "consult_beneficiaries_observation" in resource_type
    ):
        del obj["resource"]["focus"]
        del obj["resource"]["valueString"]
    resource_payload = json.dumps(obj, indent=4)

    return resource_payload


def build_resources(
        resource_type, encounter_id, flag_id, observation_id, practitioner_id, period, location,
        visit_encounter, subject, value_string="", note="",
        ):
    encounter = build_single_resource(
        resource_type + "_encounter", encounter_id, practitioner_id, period, location, "",
        visit_encounter, subject,
    )
    flag = build_single_resource(
        resource_type + "_flag", flag_id, practitioner_id, period, location, encounter_id,
        "", subject,
    )
    observation = build_single_resource(
        resource_type + "_observation", observation_id, practitioner_id, period, location, encounter_id,
        "", subject, value_string, note,
    )

    resources = encounter + "," + flag + "," + observation + ","
    return resources

def check_location(location_id, locations_list):
    if location_id in locations_list:
        return locations_list[location_id]

    check = get_resource(location_id, "Location")
    if check != "0":
        locations_list[location_id] = True
        return True
    else:
        locations_list[location_id] = False
        logging.info("-- Skipping location, it does NOT EXIST " + location_id)
        return False


def build_flag_payload(resources, practitioner_id, visit_encounter):
    initial_string = """{"resourceType": "Bundle","type": "transaction","entry": [ """
    final_string = ""
    locations = {}
    for resource in resources:
        flag_id = str(uuid.uuid5(uuid.NAMESPACE_DNS, "flag" + resource[2]))
        observation_id = str(uuid.uuid5(uuid.NAMESPACE_DNS, "observation" + resource[2]))

        sub_list = []
        valid_location = check_location(resource[4], locations)
        if valid_location:
            if resource[3] == "service_point":
                if "no" in resource[15]:
                    note = (
                        resource[16].replace('"', "").replace("[", "").replace("]", "")
                    )
                    sub_list = build_resources(
                        "service_point_check", resource[2], flag_id, observation_id, practitioner_id,
                        resource[1], resource[4], visit_encounter, "Location/" + resource[4], "", note,
                    )
                if "yes" in resource[17]:
                    note = (
                        resource[18].replace('"', "").replace("[", "").replace("]", "")
                    )
                    sub_list = build_resources(
                        "consult_beneficiaries", resource[2], flag_id, observation_id, practitioner_id,
                        resource[1], resource[4], visit_encounter, "Location/" + resource[4], "", note,
                    )
                if "yes" in resource[19]:
                    note = (
                        resource[20].replace('"', "").replace("[", "").replace("]", "")
                    )
                    sub_list = build_resources(
                        "warehouse", resource[2], flag_id, observation_id, practitioner_id, resource[1],
                        resource[4], visit_encounter, "Location/" + resource[4], "", note,
                    )

            elif resource[3] == "product":
                if resource[8]:
                    product_info = [
                        resource[8], resource[9], resource[10], resource[11], resource[12], resource[13], resource[14],
                    ]
                    value_string = " | ".join(filter(None, product_info))
                    value_string = value_string.replace('"', "")
                    if resource[6]:
                        sub_list = build_resources(
                            "product", resource[2], flag_id, observation_id, practitioner_id,
                            resource[1], resource[4], visit_encounter, "Group/" + resource[6], value_string,
                        )
                    else:
                        logging.info("-- Missing Group, skipping resource: " + str(resource))
            else:
                logging.info("-- This entityType is not supported")

            if len(sub_list) < 1:
                sub_list = ""

            final_string = final_string + sub_list
    final_string = initial_string + final_string[:-1] + " ] } "
    return final_string
