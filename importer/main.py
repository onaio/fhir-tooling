import json
import logging
import pathlib
from datetime import datetime

import click

from importer.builder import (build_assign_payload, build_group_list_resource,
                              build_org_affiliation, build_payload,
                              build_report, extract_matches, extract_resources,
                              link_to_location)
from importer.config.settings import fhir_base_url
from importer.request import handle_request
from importer.users import (assign_default_groups_roles, assign_group_roles,
                            confirm_keycloak_user, confirm_practitioner,
                            create_roles, create_user, create_user_resources)
from importer.utils import (build_mapped_payloads, clean_duplicates,
                            export_resources_to_csv, read_csv,
                            read_file_in_chunks)

dir_path = str(pathlib.Path(__file__).parent.resolve())


@click.command()
@click.option("--csv_file", required=False)
@click.option("--json_file", required=False)
@click.option("--resource_type", required=False)
@click.option("--assign", required=False)
@click.option("--setup", required=False)
@click.option("--group", required=False)
@click.option("--roles_max", required=False, default=500)
@click.option("--default_groups", required=False, default=True)
@click.option("--cascade_delete", required=False, default=False)
@click.option("--report_response", required=False)
@click.option("--export_resources", required=False)
@click.option("--parameter", required=False, default="_lastUpdated")
@click.option("--value", required=False, default="gt2023-01-01")
@click.option("--limit", required=False, default=1000)
@click.option("--bulk_import", required=False, default=False)
@click.option("--chunk_size", required=False, default=1000000)
@click.option("--resources_count", required=False, default=100)
@click.option("--list_resource_id", required=False)
@click.option(
    "--log_level", type=click.Choice(["DEBUG", "INFO", "ERROR"], case_sensitive=False)
)
@click.option(
    "--sync",
    type=click.Choice(["DIRECT", "SORT"], case_sensitive=False),
    required=False,
    default="DIRECT",
)
@click.option(
    "--location_type_coding_system",
    required=False,
    default="http://terminology.hl7.org/CodeSystem/location-type",
)
def main(
    csv_file,
    json_file,
    resource_type,
    assign,
    setup,
    group,
    roles_max,
    default_groups,
    cascade_delete,
    report_response,
    log_level,
    export_resources,
    parameter,
    value,
    limit,
    bulk_import,
    chunk_size,
    resources_count,
    list_resource_id,
    sync,
    location_type_coding_system,
):
    if log_level == "DEBUG":
        logging.basicConfig(
            filename="importer.log", encoding="utf-8", level=logging.DEBUG
        )
    elif log_level == "INFO":
        logging.basicConfig(
            filename="importer.log", encoding="utf-8", level=logging.INFO
        )
    elif log_level == "ERROR":
        logging.basicConfig(
            filename="importer.log", encoding="utf-8", level=logging.ERROR
        )
    logging.getLogger().addHandler(logging.StreamHandler())

    start_time = datetime.now()
    logging.info("Start time: " + start_time.strftime("%H:%M:%S"))
    issues = []
    fail_count = 0
    fail_all = False

    if export_resources == "True":
        logging.info("Starting export...")
        logging.info("Exporting " + resource_type)
        export_resources_to_csv(resource_type, parameter, value, limit)
        exit()

    if bulk_import:
        logging.info("Starting bulk import...")
        resource_mapping = read_file_in_chunks(json_file, chunk_size, sync)
        if sync.lower() == "sort":
            build_mapped_payloads(resource_mapping, json_file, resources_count)
        end_time = datetime.now()
        logging.info("End time: " + end_time.strftime("%H:%M:%S"))
        total_time = end_time - start_time
        logging.info("Total time: " + str(total_time.total_seconds()) + " seconds")
        exit()

    final_response = ""

    logging.info("Starting csv import...")
    json_path = "/".join([dir_path, "json_payloads/"])
    resource_list = read_csv(csv_file)

    if resource_list:
        if resource_type == "users":
            logging.info("Processing users")
            with click.progressbar(
                resource_list, label="Progress:Processing users "
            ) as process_user_progress:
                for user in process_user_progress:
                    user_id, create_issue = create_user(user)
                    if user_id == 0:
                        fail_count = fail_count + 1
                        if create_issue:
                            issues.append(create_issue)
                        # user was not created above, check if it already exists
                        user_id, confirm_issue = confirm_keycloak_user(user)
                        if confirm_issue:
                            issues.append(confirm_issue)
                    if user_id != 0:
                        # user_id has been retrieved
                        # check practitioner
                        practitioner_exists, practitioner_issue = confirm_practitioner(
                            user, user_id
                        )
                        if practitioner_issue:
                            issues.append(practitioner_issue)
                        if not practitioner_exists:
                            payload = create_user_resources(user_id, user)
                            final_response = handle_request(
                                "POST", payload, fhir_base_url
                            )
                            if final_response.status_code > 201:
                                issues.append(final_response.text)
                    logging.info("Processing complete!")
        elif resource_type == "locations":
            logging.info("Processing locations")
            json_payload = build_payload(
                "locations",
                resource_list,
                "json_payloads/locations_payload.json",
                None,
                location_type_coding_system,
            )
            final_response = handle_request("POST", json_payload, fhir_base_url)
            logging.info("Processing complete!")
        elif resource_type == "organizations":
            logging.info("Processing organizations")
            json_payload = build_payload(
                "organizations",
                resource_list,
                json_path + "organizations_payload.json",
            )
            final_response = handle_request("POST", json_payload, fhir_base_url)
            logging.info("Processing complete!")
        elif resource_type == "careTeams":
            logging.info("Processing CareTeams")
            json_payload = build_payload(
                "careTeams", resource_list, json_path + "careteams_payload.json"
            )
            final_response = handle_request("POST", json_payload, fhir_base_url)
            logging.info("Processing complete!")
        elif assign == "organizations-Locations":
            logging.info("Assigning Organizations to Locations")
            matches = extract_matches(resource_list)
            json_payload = build_org_affiliation(matches, resource_list)
            final_response = handle_request("POST", json_payload, fhir_base_url)
            logging.info("Processing complete!")
        elif assign == "users-organizations":
            logging.info("Assigning practitioner to Organization")
            json_payload = build_assign_payload(
                resource_list, "PractitionerRole", "practitioner=Practitioner/"
            )
            final_response = handle_request("POST", json_payload, fhir_base_url)
            logging.info("Processing complete!")
        elif setup == "roles":
            logging.info("Setting up keycloak roles")
            create_roles(resource_list)
            if group:
                assign_group_roles(resource_list, group, roles_max)
            logging.info("Processing complete")
            if default_groups:
                assign_default_groups_roles(roles_max)
        elif setup == "clean_duplicates":
            logging.info(
                "You are about to clean/delete Practitioner resources on the HAPI server"
            )
            click.confirm("Do you want to continue?", abort=True)
            clean_duplicates(resource_list, cascade_delete)
            logging.info("Processing complete!")
        elif setup == "products":
            logging.info("Importing products as FHIR Group resources")
            json_payload, created_resources = build_payload(
                "Group", resource_list, json_path + "product_group_payload.json", []
            )
            product_creation_response = handle_request(
                "POST", json_payload, fhir_base_url
            )
            if product_creation_response.status_code == 200:
                full_list_created_resources = extract_resources(
                    created_resources, product_creation_response.text
                )
                list_payload = build_group_list_resource(
                    list_resource_id,
                    csv_file,
                    full_list_created_resources,
                    "Supply Inventory List",
                )
                final_response = handle_request("POST", "", fhir_base_url, list_payload)
                logging.info("Processing complete!")
            else:
                fail_count = fail_count + 1
                fail_all = True
                json_response = json.loads(product_creation_response.text)
                for _ in json_response["issue"]:
                    del _["code"]
                    issues.append(_)
                logging.error(json_response)
        elif setup == "inventories":
            logging.info("Importing inventories as FHIR Group resources")
            json_payload = build_payload(
                "Group", resource_list, json_path + "inventory_group_payload.json"
            )
            inventory_creation_response = handle_request(
                "POST", json_payload, fhir_base_url
            )
            groups_created = []
            if inventory_creation_response.status_code == 200:
                groups_created = extract_resources(
                    groups_created, inventory_creation_response.text
                )
            else:
                fail_count = fail_count + 1
                fail_all = True
                json_response = json.loads(inventory_creation_response.text)
                for _ in json_response["issue"]:
                    del _["code"]
                    issues.append(_)
                logging.error(json_response)
            logging.info(inventory_creation_response.text)
            logging.info("GROUPS: " + str(groups_created))

            lists_created = []
            link_payload = link_to_location(resource_list)
            if len(link_payload) > 0:
                link_response = handle_request("POST", link_payload, fhir_base_url)
                if link_response.status_code == 200 or link_response.status_code == 201:
                    lists_created = extract_resources(lists_created, link_response.text)
                else:
                    fail_count = fail_count + 1
                    fail_all = True
                    json_response = json.loads(link_response.text)
                    for _ in json_response["issue"]:
                        del _["code"]
                        issues.append(_)
                logging.info(link_response.text)
                logging.info("LISTS: " + str(lists_created))

            full_list_created_resources = groups_created + lists_created
            logging.info("FULL LIST: " + str(full_list_created_resources))
            if len(full_list_created_resources) > 0:
                list_payload = build_group_list_resource(
                    list_resource_id,
                    csv_file,
                    full_list_created_resources,
                    "Supply Chain commodities",
                )
                final_response = handle_request("POST", "", fhir_base_url, list_payload)
                logging.info(final_response.text)
                logging.info("Processing complete!")
        else:
            message = "Unsupported request!"
            fail_all = True
            issues.append({"Error": message})
            logging.error("Unsupported request!")
    else:
        logging.error("Empty csv file!")

    if final_response and final_response.text:
        logging.info('{ "final-response": ' + final_response.text + "}")

    end_time = datetime.now()
    logging.info("End time: " + end_time.strftime("%H:%M:%S"))
    total_time = end_time - start_time
    logging.info("Total time: " + str(total_time.total_seconds()) + " seconds")

    if report_response:
        build_report(csv_file, final_response, issues, fail_count, fail_all)


if __name__ == "__main__":
    main()
