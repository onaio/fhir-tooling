import csv
import json
import logging
import os
import uuid
from datetime import datetime

import click

from importer.builder import get_base_url
from importer.config.settings import fhir_base_url, keycloak_url
from importer.request import post_request


# This function takes in a csv file
# reads it and returns a list of strings/lines
# It ignores the first line (assumes headers)
def read_csv(csv_file: str):
    logging.info("Reading csv file")
    with open(csv_file, mode="r") as file:
        records = csv.reader(file, delimiter=",")
        try:
            next(records)
            all_records = []

            with click.progressbar(
                records, label="Progress::Reading csv "
            ) as read_csv_progress:
                for record in read_csv_progress:
                    all_records.append(record)

            logging.info("Returning records from csv file")
            return all_records

        except StopIteration:
            logging.error("Stop iteration on empty file")


# Create a csv file and initialize the CSV writer
def write_csv(data, resource_type, fieldnames):
    logging.info("Writing to csv file")
    path = "csv/exports"
    if not os.path.exists(path):
        os.makedirs(path)

    current_time = datetime.now().strftime("%Y-%m-%d-%H-%M")
    csv_file = f"{path}/{current_time}-export_{resource_type}.csv"
    with open(csv_file, "w", newline="") as file:
        csv_writer = csv.writer(file)
        csv_writer.writerow(fieldnames)
        with click.progressbar(
            data, label="Progress:: Writing csv"
        ) as write_csv_progress:
            for row in write_csv_progress:
                csv_writer.writerow(row)
    return csv_file


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


# This function exports resources from the API to a csv file
def export_resources_to_csv(resource_type, parameter, value, limit):
    base_url = get_base_url()
    resource_url = "/".join([str(base_url), resource_type])
    if len(parameter) > 0:
        resource_url = (
            resource_url + "?" + parameter + "=" + value + "&_count=" + str(limit)
        )
    response = handle_request("GET", "", resource_url)
    if response[1] == 200:
        resources = json.loads(response[0])
        data = []
        try:
            if resources["entry"]:
                if resource_type == "Location":
                    elements = [
                        "name",
                        "status",
                        "method",
                        "id",
                        "identifier",
                        "parentName",
                        "parentID",
                        "type",
                        "typeCode",
                        "physicalType",
                        "physicalTypeCode",
                    ]
                elif resource_type == "Organization":
                    elements = ["name", "active", "method", "id", "identifier"]
                elif resource_type == "CareTeam":
                    elements = [
                        "name",
                        "status",
                        "method",
                        "id",
                        "identifier",
                        "organizations",
                        "participants",
                    ]
                else:
                    elements = []
                with click.progressbar(
                    resources["entry"], label="Progress:: Extracting resource"
                ) as extract_resources_progress:
                    for x in extract_resources_progress:
                        rl = []
                        orgs_list = []
                        participants_list = []
                        for element in elements:
                            try:
                                if element == "method":
                                    value = "update"
                                elif element == "active":
                                    value = x["resource"]["active"]
                                elif element == "identifier":
                                    value = x["resource"]["identifier"][0]["value"]
                                elif element == "organizations":
                                    organizations = x["resource"][
                                        "managingOrganization"
                                    ]
                                    for index, value in enumerate(organizations):
                                        reference = x["resource"][
                                            "managingOrganization"
                                        ][index]["reference"]
                                        new_reference = reference.split("/", 1)[1]
                                        display = x["resource"]["managingOrganization"][
                                            index
                                        ]["display"]
                                        organization = ":".join(
                                            [new_reference, display]
                                        )
                                        orgs_list.append(organization)
                                    string = "|".join(map(str, orgs_list))
                                    value = string
                                elif element == "participants":
                                    participants = x["resource"]["participant"]
                                    for index, value in enumerate(participants):
                                        reference = x["resource"]["participant"][index][
                                            "member"
                                        ]["reference"]
                                        new_reference = reference.split("/", 1)[1]
                                        display = x["resource"]["participant"][index][
                                            "member"
                                        ]["display"]
                                        participant = ":".join([new_reference, display])
                                        participants_list.append(participant)
                                    string = "|".join(map(str, participants_list))
                                    value = string
                                elif element == "parentName":
                                    value = x["resource"]["partOf"]["display"]
                                elif element == "parentID":
                                    reference = x["resource"]["partOf"]["reference"]
                                    value = reference.split("/", 1)[1]
                                elif element == "type":
                                    value = x["resource"]["type"][0]["coding"][0][
                                        "display"
                                    ]
                                elif element == "typeCode":
                                    value = x["resource"]["type"][0]["coding"][0][
                                        "code"
                                    ]
                                elif element == "physicalType":
                                    value = x["resource"]["physicalType"]["coding"][0][
                                        "display"
                                    ]
                                elif element == "physicalTypeCode":
                                    value = x["resource"]["physicalType"]["coding"][0][
                                        "code"
                                    ]
                                else:
                                    value = x["resource"][element]
                            except KeyError:
                                value = ""
                            rl.append(value)
                        data.append(rl)
                write_csv(data, resource_type, elements)
                logging.info("Successfully written to csv")
            else:
                logging.info("No entry found")
        except KeyError:
            logging.info("No Resources Found")
    else:
        logging.error(
            f"Failed to retrieve resource. Status code: {response[1]} response: {response[0]}"
        )


def build_mapped_payloads(resource_mapping, json_file, resources_count):
    with open(json_file, "r") as file:
        data_dict = json.load(file)
        with click.progressbar(
            resource_mapping, label="Progress::Setting up ... "
        ) as resource_mapping_progress:
            for resource_type in resource_mapping_progress:
                index_positions = resource_mapping[resource_type]
                resource_list = [data_dict[i] for i in index_positions]
                set_resource_list(None, resource_list, resource_type, resources_count)


def build_resource_type_map(resources: str, mapping: dict, index_tracker: int = 0):
    resource_list = json.loads(resources)
    for index, resource in enumerate(resource_list):
        resource_type = resource["resourceType"]
        if resource_type in mapping.keys():
            mapping[resource_type].append(index + index_tracker)
        else:
            mapping[resource_type] = [index + index_tracker]

    global import_counter
    import_counter = len(resource_list) + import_counter


def split_chunk(
    chunk: str,
    left_over_chunk: str,
    size: int,
    mapping: dict = None,
    sync: str = None,
    import_counter: int = 0,
):
    if len(chunk) + len(left_over_chunk) < int(size):
        # load can fit in one chunk, so remove closing bracket
        last_bracket = chunk.rfind("}")
        current_chunk = chunk[: int(last_bracket)]
        next_left_over_chunk = "-"
        if len(chunk.strip()) == 0:
            last_bracket = left_over_chunk.rfind("}")
            left_over_chunk = left_over_chunk[: int(last_bracket)]
    else:
        # load can't fit, so split on last full resource
        split_index = chunk.rfind(
            '},{"id"'
        )  # Assumption that this string will find the last full resource
        current_chunk = chunk[:split_index]
        next_left_over_chunk = chunk[int(split_index) + 2 :]
        if len(chunk.strip()) == 0:
            last_bracket = left_over_chunk.rfind("}")
            left_over_chunk = left_over_chunk[: int(last_bracket)]

    if len(left_over_chunk.strip()) == 0:
        current_chunk = current_chunk[1:]

    chunk_list = "[" + left_over_chunk + current_chunk + "}]"

    if sync.lower() == "direct":
        set_resource_list(chunk_list)
    if sync.lower() == "sort":
        build_resource_type_map(chunk_list, mapping, import_counter)
    return next_left_over_chunk


def read_file_in_chunks(json_file: str, chunk_size: int, sync: str):
    logging.info("Reading file in chunks ...")
    incomplete_load = ""
    mapping = {}
    global import_counter
    import_counter = 0
    with open(json_file, "r") as file:
        while True:
            chunk = file.read(chunk_size)
            if not chunk:
                break
            incomplete_load = split_chunk(
                chunk, incomplete_load, chunk_size, mapping, sync, import_counter
            )
    return mapping


def set_resource_list(
    objs: str = None,
    json_list: list = None,
    resource_type: str = None,
    number_of_resources: int = 100,
):
    if objs:
        resources_array = json.loads(objs)
        process_chunk(resources_array, resource_type)
    if json_list:
        if len(json_list) > number_of_resources:
            for i in range(0, len(json_list), number_of_resources):
                sub_list = json_list[i : i + number_of_resources]
                process_chunk(sub_list, resource_type)
        else:
            process_chunk(json_list, resource_type)


def process_chunk(resources_array: list, resource_type: str):
    new_arr = []
    with click.progressbar(
        resources_array, label="Progress::Processing chunks ... "
    ) as resources_array_progress:
        for resource in resources_array_progress:
            if not resource_type:
                resource_type = resource["resourceType"]
            try:
                resource_id = resource["id"]
            except KeyError:
                if "identifier" in resource:
                    resource_identifier = resource["identifier"][0]["value"]
                    resource_id = str(
                        uuid.uuid5(uuid.NAMESPACE_DNS, resource_identifier)
                    )
                else:
                    resource_id = str(uuid.uuid4())

        item = {"resource": resource, "request": {}}
        item["request"]["method"] = "PUT"
        item["request"]["url"] = "/".join([resource_type, resource_id])
        new_arr.append(item)

    json_payload = {"resourceType": "Bundle", "type": "transaction", "entry": new_arr}

    r = handle_request("POST", "", fhir_base_url, json_payload)
    logging.info(r.text)
    # TODO handle failures


def clean_duplicates(users, cascade_delete):
    for user in users:
        # get keycloak user uuid
        username = str(user[2].strip())
        user_details = handle_request(
            "GET", "", keycloak_url + "/users?exact=true&username=" + username
        )
        obj = json.loads(user_details[0])
        keycloak_uuid = obj[0]["id"]

        # get Practitioner(s)
        r = handle_request(
            "GET",
            "",
            fhir_base_url + "/Practitioner?identifier=" + keycloak_uuid,
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


def delete_resource(resource_type, resource_id, cascade):
    if cascade:
        cascade = "?_cascade=delete"
    else:
        cascade = ""

    resource_url = "/".join([fhir_base_url, resource_type, resource_id + cascade])
    r = handle_request("DELETE", "", resource_url)
    logging.info(r.text)
