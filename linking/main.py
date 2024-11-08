import json
import logging
from datetime import datetime

import click
import request

try:
    import config
except ModuleNotFoundError:
    logging.error("The config.py file is missing!")
    exit()

# Constants
LINK_TYPE_REPLACES = "replaces"
LINK_TYPE_REPLACED_BY = "replaced-by"


def make_request(
    request_type: str,
    resource_type: str,
    identifier: str = None,
    search_filter: str = None,
    payload=None,
):

    url = config.fhir_base_url
    if resource_type:
        url += f"/{resource_type}"
    if identifier:
        url += f"/{identifier}"
    elif search_filter:
        url += f"?{search_filter}"

    response = request.handle_request(request_type, url, payload)
    if response.status_code > 201:
        logging.error(response.status_code + " : " + response.text)
        return None
    return response


def has_existing_link(patient):
    if "link" in patient:
        link_type = patient["link"][0]["type"]
        link_reference = patient["link"][0]["other"]["reference"]
        return link_type, link_reference
    return None, None


def create_link(patient, id_reference, status, link_type):
    logging.info("Adding a " + link_type + " link...")
    link_reference = "Patient/" + id_reference
    patient["active"] = status
    if "link" not in patient:
        patient["link"] = []
    patient["link"].append({"other": {"reference": link_reference}, "type": link_type})


def link_records(patients):
    logging.info("Checking for links...")
    patient0, patient1 = patients

    link0, link_reference0 = has_existing_link(patient0)
    link1, link_reference1 = has_existing_link(patient1)

    if link0 in patient0:
        logging.info("Patient0 already has a " + link0 + " link")
    if link1 in patient1:
        logging.info("Patient1 already has a " + link1 + " link")

    # Both have links
    if link0 and link1:
        if (patient0["id"] in link_reference1) and (patient1["id"] in link_reference0):
            logging.info("This duplicate match is already linked")
            return False, patients
        logging.error(
            "Cannot link! Both of these records are already linked separately"
        )
        return False, patients

    # Link0 has link, link1 does not
    if link0:
        if link0 == LINK_TYPE_REPLACES:
            create_link(patient1, patient0["id"], "false", LINK_TYPE_REPLACED_BY)
            create_link(patient0, patient1["id"], "true", LINK_TYPE_REPLACES)
            return True, patients
        if link0 == LINK_TYPE_REPLACED_BY:
            logging.error("Can't link a new patient to a duplicate")
            return False, patients

    # Link1 has link, link0 does not
    if link1:
        if link1 == LINK_TYPE_REPLACES:
            create_link(patient0, patient1["id"], "false", LINK_TYPE_REPLACED_BY)
            create_link(patient1, patient0["id"], "true", LINK_TYPE_REPLACES)
            return True, patients
        if link1 == LINK_TYPE_REPLACED_BY:
            logging.error("Can't link a new patient to a duplicate")
            return False, patients

    # If none is linked
    logging.info("None is linked, proceed to compare lastUpdated date")
    patients = link_with_last_updated(patients)
    return True, patients


def link_with_last_updated(patients):
    patient0 = patients[0]
    patient1 = patients[1]
    date_format = "%Y-%m-%dT%H:%M:%S.%f%z"
    try:
        date0 = datetime.strptime(patients[0]["meta"]["lastUpdated"], date_format)
        date1 = datetime.strptime(patients[1]["meta"]["lastUpdated"], date_format)

        if date0 >= date1:
            create_link(patient0, patient1["id"], "true", LINK_TYPE_REPLACES)
            create_link(patient1, patient0["id"], "false", LINK_TYPE_REPLACED_BY)
        else:
            create_link(patient1, patient0["id"], "true", LINK_TYPE_REPLACES)
            create_link(patient0, patient1["id"], "false", LINK_TYPE_REPLACED_BY)
        return patients

    except ValueError as e:
        logging.error("Error parsing dates: " + str(e))
        return None


def build_bundle(resources, resource_type):
    logging.info("Building bundle payload")
    records = []
    for resource in resources:
        version = resource["meta"]["versionId"]
        record = {
            "request": {
                "method": "PUT",
                "url": resource_type + "/" + resource["id"],
                "ifMatch": version,
            },
            "resource": resource,
        }
        records.append(record)

    payload = {"resourceType": "Bundle", "type": "transaction", "entry": records}
    return payload


def assign_patients(duplicate_patients):
    if duplicate_patients[0]["link"][0]["type"] == LINK_TYPE_REPLACES:
        return duplicate_patients[0]["id"], duplicate_patients[1]["id"]
    return duplicate_patients[1]["id"], duplicate_patients[0]["id"]


def get_care_plan_details(patient_id):
    cp_details = {"patient_id": patient_id}
    search_filter = "subject=Patient/" + patient_id
    care_plan_response = make_request("get", "CarePlan", "", search_filter)
    json_response = json.loads(care_plan_response.text)
    if json_response["total"] == 1:
        cp_details["id"] = json_response["entry"][0]["resource"]["id"]
        cp_details["status"] = json_response["entry"][0]["resource"]["status"]
        cp_details["plan_definition"] = json_response["entry"][0]["resource"][
            "instantiatesCanonical"
        ][0]
        cp_details["tasks"] = json_response["entry"][0]["resource"]["activity"][0][
            "outcomeReference"
        ]
    # TODO handle 0 or multiple CarePlans
    return cp_details


def update_care_plans(primary, duplicate):
    resources = []
    # Primary
    primary_response = make_request("get", "CarePlan", primary)
    if primary_response is not None:
        primary_json = json.loads(primary_response.text)
        if "replaces" not in primary_json:
            primary_json["replaces"] = []
        primary_json["replaces"].append({"reference": "CarePlan/" + duplicate})
        resources.append(primary_json)

    # Duplicate
    duplicate_response = make_request("get", "CarePlan", duplicate)
    if duplicate_response is not None:
        duplicate_json = json.loads(duplicate_response.text)
        duplicate_json["status"] = "revoked"
        resources.append(duplicate_json)
        # TODO if CarePlans are more than 1
    post_bundle(resources, "CarePlan")


def handle_other_care_plan(task, primary_tasks, primary_patient_id):
    logging.info("... Handling other CarePlan")
    task_identifier = task["identifier"]
    duplicate_identifier = ""
    for x in task_identifier:
        if x["use"] == "secondary":
            duplicate_identifier = x["value"]

    matching_task = {}
    primary_identifier = ""
    if len(duplicate_identifier) > 0:
        for p_task in primary_tasks:
            if len(p_task["resource"]["identifier"]) > 1:
                for y in p_task["resource"]["identifier"]:
                    if y["use"] == "secondary":
                        primary_identifier = y["value"]
                        matching_task = p_task["resource"]

    if len(primary_identifier) > 0 and primary_identifier == duplicate_identifier:
        task["for"]["reference"] = "Patient/" + primary_patient_id
        if "basedOn" in matching_task:
            task["basedOn"][0]["reference"] = str(matching_task["basedOn"][0]["reference"])
            matching_task["status"] = "cancelled"

    tasks = [task, matching_task]
    payload = build_bundle(tasks, "Task")
    logging.info(payload)
    bundle_response = make_request("post", "", "", "", payload)
    if bundle_response is not None:
        logging.info(bundle_response.text)


def get_resource(patient_id, resource_type):
    tasks = []
    search_params = "patient=Patient/" + patient_id + "&_count=1000"
    response = make_request("get", resource_type, "", search_params)
    if response is not None:
        json_response = json.loads(response.text)
        if "entry" in json_response:
            tasks = json_response["entry"]
    return tasks


def post_bundle(bundle, resource_type):
    bundle_payload = build_bundle(bundle, resource_type)
    bundle_response = make_request("post", "", "", "", bundle_payload)
    if bundle_response is not None:
        logging.info(bundle_response.text)


@click.command()
@click.option("--patient_ids", required=True)
@click.option("--merge", required=False, default=True)
def main(patient_ids, merge):
    logging.basicConfig(
        level=logging.INFO, format="%(asctime)s - %(levelname)s - %(message)s"
    )
    duplicate_patients = []
    for pid in patient_ids.split("|"):
        logging.info("Processing patient id: " + pid)
        patient_response = make_request("get", "Patient", pid)
        if patient_response is not None:
            patient_obj = json.loads(patient_response.text)
            duplicate_patients.append(patient_obj)

    link, linked_patients = link_records(duplicate_patients)
    if link:
        link_payload = build_bundle(linked_patients, "Patient")
        logging.info(link_payload)
        bundle_response = make_request("post", "", "", "", link_payload)
        if bundle_response is not None:
            logging.info(bundle_response.text)

    if merge:
        primary, duplicate = assign_patients(duplicate_patients)

        # CarePlans
        primary_care_plan = get_care_plan_details(primary)
        duplicate_care_plan = get_care_plan_details(duplicate)
        update_care_plans(primary_care_plan["id"], duplicate_care_plan["id"])

        # Tasks
        primary_tasks = get_resource(primary, "Task")
        duplicate_tasks = get_resource(duplicate, "Task")

        completed_tasks = []
        for task in duplicate_tasks:
            if task["resource"]["status"] == "completed":
                completed_tasks.append(task["resource"])

        # update completed duplicate tasks
        for task in completed_tasks:
            task["for"]["reference"] = "Patient/" + primary
            if duplicate_care_plan["id"] in task["basedOn"][0]["reference"]:
                task["basedOn"][0]["reference"] = "CarePlan/" + primary_care_plan["id"]
            else:
                handle_other_care_plan(task, primary_tasks, primary)
        if len(completed_tasks) > 0:
            post_bundle(completed_tasks, "Task")

        # cancel matching primary tasks
        completed_tasks_ids = [resource["id"] for resource in completed_tasks]
        if (
            primary_care_plan["plan_definition"]
            == duplicate_care_plan["plan_definition"]
        ):
            counter = 0
            primary_task_ids_to_update = []
            for _index in duplicate_care_plan["tasks"]:
                _id = _index["reference"][5:]
                if _id in str(completed_tasks_ids):
                    task_reference = primary_care_plan["tasks"][counter]["reference"]
                    primary_task_ids_to_update.append(task_reference[5:])
                counter += 1

            cancelled_tasks = []
            for task in primary_tasks:
                if task["resource"]["id"] in str(primary_task_ids_to_update):
                    logging.info("cancelling task: " + task["resource"]["id"])
                    task["resource"]["status"] = "cancelled"
                    cancelled_tasks.append(task["resource"])
            if len(cancelled_tasks) > 0:
                post_bundle(cancelled_tasks, "Task")

        # TODO map tasks if planDefinition is different

        # Observations
        duplicate_observations = get_resource(duplicate, "Observation")
        updated_bundle = []
        for resource in duplicate_observations:
            resource["resource"]["subject"]["reference"] = "Patient/" + primary
            updated_bundle.append(resource["resource"])
        post_bundle(updated_bundle, "Observation")

        # Conditions
        duplicate_conditions = get_resource(duplicate, "Condition")
        updated_bundle = []
        logging.info(duplicate_conditions)
        for resource in duplicate_conditions:
            resource["subject"]["reference"] = "Patient/" + primary
            updated_bundle.append(resource["resource"])
        post_bundle(updated_bundle, "Condition")

        # Encounters
        duplicate_encounters = get_resource(duplicate, "Encounter")
        updated_bundle = []
        logging.info(duplicate_encounters)
        for resource in duplicate_encounters:
            resource["subject"]["reference"] = "Patient/" + primary
            updated_bundle.append(resource["resource"])
        post_bundle(updated_bundle, "Encounter")

        # Immunizations
        duplicate_immunizations = get_resource(duplicate, "Immunization")
        updated_bundle = []
        logging.info(duplicate_immunizations)
        for resource in duplicate_immunizations:
            resource["patient"]["reference"] = "Patient/" + primary
            updated_bundle.append(resource["resource"])
        post_bundle(updated_bundle, "Immunization")

        # RelatedPersons
        duplicate_related_persons = get_resource(duplicate, "RelatedPerson")
        updated_bundle = []
        logging.info(duplicate_related_persons)
        for resource in duplicate_related_persons:
            resource["patient"]["reference"] = "Patient/" + primary
            updated_bundle.append(resource["resource"])
        post_bundle(updated_bundle, "RelatedPerson")

        # Consent
        duplicate_consent = get_resource(duplicate, "Consent")
        updated_bundle = []
        logging.info(duplicate_consent)
        for resource in duplicate_consent:
            resource["patient"]["reference"] = "Patient/" + primary
            updated_bundle.append(resource["resource"])
        post_bundle(updated_bundle, "Consent")


if __name__ == "__main__":
    main()
