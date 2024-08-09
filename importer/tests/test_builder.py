import json
import pathlib
import unittest

from jsonschema import validate
from mock import patch

from importer.builder import (build_assign_payload, build_org_affiliation,
                              build_payload, check_parent_admin_level,
                              extract_matches, extract_resources,
                              process_resources_list)
from importer.utils import read_csv

dir_path = str(pathlib.Path(__file__).parent.resolve())
csv_path = dir_path + "/../csv/"
json_path = dir_path + "/../json_payloads/"


class TestBuilder(unittest.TestCase):
    @patch("importer.builder.get_resource")
    def test_build_payload_organizations(self, mock_get_resource):
        mock_get_resource.return_value = "1"

        csv_file = csv_path + "organizations/organizations_full.csv"
        resource_list = read_csv(csv_file)
        json_file = json_path + "organizations_payload.json"
        payload = build_payload("organizations", resource_list, json_file)
        payload_obj = json.loads(payload)
        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 3)

        resource_schema = {
            "type": "object",
            "properties": {
                "resourceType": {"const": "Organization"},
                "id": {"const": "8342dd77-aecd-48ab-826b-75c7c33039ed"},
                "identifier": {"type": "array", "items": {"type": "object"}},
                "active": {"const": "true"},
                "name": {"const": "Health Organization"},
            },
            "required": ["resourceType", "id", "identifier", "active", "name"],
        }
        validate(payload_obj["entry"][2]["resource"], resource_schema)

        request_schema = {
            "type": "object",
            "properties": {
                "method": {"const": "PUT"},
                "url": {"const": "Organization/8342dd77-aecd-48ab-826b-75c7c33039ed"},
                "ifMatch": {"const": "1"},
            },
        }
        validate(payload_obj["entry"][2]["request"], request_schema)

        #  TestCase organizations_min.csv
        csv_file = csv_path + "organizations/organizations_min.csv"
        resource_list = read_csv(csv_file)
        payload = build_payload("organizations", resource_list, json_file)
        payload_obj = json.loads(payload)
        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 1)

        resource_schema = {
            "type": "object",
            "properties": {
                "resourceType": {"const": "Organization"},
                "id": {"const": "3da051e0-d743-5574-8f0e-6cb8798551f5"},
                "identifier": {"type": "array", "items": {"type": "object"}},
                "active": {"const": "true"},
                "name": {"const": "Min Organization"},
            },
            "required": ["id", "identifier", "active", "name"],
        }
        validate(payload_obj["entry"][0]["resource"], resource_schema)

        request_schema = {
            "type": "object",
            "properties": {
                "method": {"const": "PUT"},
                "url": {"const": "Organization/3da051e0-d743-5574-8f0e-6cb8798551f5"},
                "ifMatch": {"const": "1"},
            },
        }
        validate(payload_obj["entry"][0]["resource"], request_schema)

    @patch("importer.builder.check_parent_admin_level")
    @patch("importer.builder.get_resource")
    def test_build_payload_locations(
        self, mock_get_resource, mock_check_parent_admin_level
    ):
        mock_get_resource.return_value = "1"
        mock_check_parent_admin_level.return_value = "3"

        csv_file = csv_path + "locations/locations_full.csv"
        resource_list = read_csv(csv_file)
        json_file = json_path + "locations_payload.json"
        payload = build_payload(
            "locations",
            resource_list,
            json_file,
            None,
            "http://terminology.hl7.org/CodeSystem/location-type",
        )
        payload_obj = json.loads(payload)
        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 3)

        resource_schema = {
            "type": "object",
            "properties": {
                "resourceType": {"const": "Location"},
                "id": {"const": "ba787982-b973-4bd5-854e-eacbe161e297"},
                "identifier": {"type": "array", "items": {"type": "object"}},
                "status": {"const": "active"},
                "name": {"const": "City1"},
                "partOf": {"type": "object"},
                "type": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "coding": {
                                "type": "array",
                                "items": {
                                    "type": "object",
                                    "properties": {
                                        "system": {"type": "string"},
                                        "code": {"type": "string"},
                                        "display": {"type": "string"},
                                    },
                                },
                            }
                        },
                    },
                },
                "physicalType": {
                    "type": "object",
                    "properties": {
                        "coding": {
                            "type": "array",
                            "items": {
                                "type": "object",
                                "properties": {
                                    "system": {
                                        "const": "http://terminology.hl7.org/CodeSystem/location-physical-type"
                                    },
                                    "code": {"const": "wa"},
                                    "display": {"const": "ward"},
                                },
                            },
                        }
                    },
                },
                "position": {
                    "type": "object",
                    "properties": {
                        "longitude": {"const": 36.81},
                        "latitude": {"const": -1.28},
                    },
                },
            },
            "required": [
                "resourceType",
                "id",
                "identifier",
                "status",
                "name",
                "partOf",
                "type",
                "physicalType",
                "position",
            ],
        }
        validate(payload_obj["entry"][0]["resource"], resource_schema)

        request_schema = {
            "type": "object",
            "properties": {
                "method": {"const": "PUT"},
                "url": {"const": "Location/ba787982-b973-4bd5-854e-eacbe161e297"},
                "ifMatch": {"const": "1"},
            },
        }
        validate(payload_obj["entry"][0]["request"], request_schema)

        #  TestCase locations_min.csv
        csv_file = csv_path + "locations/locations_min.csv"
        resource_list = read_csv(csv_file)
        payload = build_payload(
            "locations",
            resource_list,
            json_file,
            None,
            "http://terminology.hl7.org/CodeSystem/location-type",
        )
        payload_obj = json.loads(payload)
        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 2)

        resource_schema = {
            "type": "object",
            "properties": {
                "resourceType": {"const": "Location"},
                "id": {"const": "c4336f73-4450-566b-b381-d07a6e857d72"},
                "identifier": {"type": "array", "items": {"type": "object"}},
                "status": {"const": "active"},
                "name": {"const": "City1"},
            },
            "required": ["id", "identifier", "status", "name"],
        }
        validate(payload_obj["entry"][0]["resource"], resource_schema)

        request_schema = {
            "type": "object",
            "properties": {
                "method": {"const": "PUT"},
                "url": {"const": "Location/c4336f73-4450-566b-b381-d07a6e857d72"},
                "ifMatch": {"const": "1"},
            },
        }
        validate(payload_obj["entry"][0]["resource"], request_schema)

    @patch("importer.builder.handle_request")
    @patch("importer.builder.get_base_url")
    def test_check_parent_admin_level(self, mock_get_base_url, mock_handle_request):
        mock_get_base_url.return_value = "https://example.smartregister.org/fhir"
        mocked_response_text = {
            "resourceType": "Location",
            "id": "18fcbc2e-4240-4a84-a270-7a444523d7b6",
            "identifier": [
                {"use": "official", "value": "18fcbc2e-4240-4a84-a270-7a444523d7b6"}
            ],
            "status": "active",
            "name": "test location-1",
            "type": [
                {
                    "coding": [
                        {
                            "system": "https://smartregister.org/codes/administrative-level",
                            "code": "2",
                            "display": "Level 2",
                        }
                    ]
                }
            ],
        }
        string_mocked_response_text = json.dumps(mocked_response_text)
        mock_handle_request.return_value = (string_mocked_response_text, 200)
        location_parent_id = "18fcbc2e-4240-4a84-a270-7a444523d7b6"
        admin_level = check_parent_admin_level(location_parent_id)
        self.assertEqual(admin_level, "3")

    @patch("importer.builder.get_resource")
    def test_build_payload_care_teams(self, mock_get_resource):
        mock_get_resource.return_value = "1"

        csv_file = csv_path + "careteams/careteam_full.csv"
        resource_list = read_csv(csv_file)
        payload = build_payload(
            "careTeams", resource_list, json_path + "careteams_payload.json"
        )
        payload_obj = json.loads(payload)
        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 1)

        resource_schema = {
            "type": "object",
            "properties": {
                "resourceType": {"const": "CareTeam"},
                "id": {"const": "b323df51-b515-46a0-b4e6-dd29d515ae5e"},
                "identifier": {"type": "array", "items": {"type": "object"}},
                "status": {"const": "active"},
                "name": {"const": "Knee care"},
                "participant": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "role": {
                                "type": "array",
                                "items": {
                                    "type": "object",
                                    "properties": {
                                        "coding": {
                                            "type": "array",
                                            "items": {
                                                "type": "object",
                                                "properties": {
                                                    "system": {
                                                        "const": "http://snomed.info/sct"
                                                    },
                                                    "code": {"const": "394730007"},
                                                    "display": {
                                                        "const": "Healthcare related organization"
                                                    },
                                                },
                                            },
                                        }
                                    },
                                },
                            },
                            "member": {
                                "type": "object",
                                "properties": {
                                    "reference": {"type": "string"},
                                    "display": {"type": "string"},
                                },
                            },
                        },
                        "anyOf": [
                            {"required": ["role", "member"]},
                            {"required": ["member"]},
                        ],
                    },
                },
                "managingOrganization": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "reference": {"type": "string"},
                            "display": {"type": "string"},
                        },
                    },
                },
            },
            "required": [
                "resourceType",
                "id",
                "identifier",
                "status",
                "name",
                "participant",
                "managingOrganization",
            ],
        }
        validate(payload_obj["entry"][0]["resource"], resource_schema)

        request_schema = {
            "type": "object",
            "properties": {
                "method": {"const": "PUT"},
                "url": {"const": "CareTeam/b323df51-b515-46a0-b4e6-dd29d515ae5e"},
                "ifMatch": {"const": "1"},
            },
        }
        validate(payload_obj["entry"][0]["request"], request_schema)

    @patch("importer.builder.save_image")
    @patch("importer.builder.get_resource")
    def test_build_payload_group(self, mock_get_resource, mock_save_image):
        mock_get_resource.return_value = "1"
        mock_save_image.return_value = "f374a23a-3c6a-4167-9970-b10c16a91bbd"

        csv_file = csv_path + "import/product.csv"
        resource_list = read_csv(csv_file)
        payload, list_resource = build_payload(
            "Group", resource_list, json_path + "product_group_payload.json", []
        )
        payload_obj = json.loads(payload)
        self.assertEqual(list_resource, ["Binary/f374a23a-3c6a-4167-9970-b10c16a91bbd"])

        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 2)

        resource_schema_0 = {
            "type": "object",
            "properties": {
                "resourceType": {"const": "Group"},
                "id": {"const": "1d86d0e2-bac8-4424-90ae-e2298900ac3c"},
                "identifier": {"type": "array", "items": {"type": "object"}},
                "active": {"const": "true"},
                "name": {"const": "thermometer"},
                "characteristic": {"type": "array", "minItems": 6, "maxItems": 6},
            },
            "required": ["resourceType", "id", "identifier", "active", "name"],
        }
        validate(payload_obj["entry"][0]["resource"], resource_schema_0)

        resource_schema_1 = {
            "type": "object",
            "properties": {
                "resourceType": {"const": "Group"},
                "id": {"const": "334ec316-b44b-5678-b110-4d7ad6b1972f"},
                "identifier": {"type": "array", "items": {"type": "object"}},
                "active": {"const": "true"},
                "name": {"const": "sterilizer"},
                "characteristic": {"type": "array", "minItems": 2, "maxItems": 2},
            },
            "required": ["resourceType", "id", "identifier", "active", "name"],
        }
        validate(payload_obj["entry"][1]["resource"], resource_schema_1)

        request_schema = {
            "type": "object",
            "properties": {
                "method": {"const": "PUT"},
                "url": {"const": "Group/1d86d0e2-bac8-4424-90ae-e2298900ac3c"},
                "ifMatch": {"const": "1"},
            },
        }
        validate(payload_obj["entry"][0]["request"], request_schema)

    def test_build_payload_group_reference_list(self):
        binary_resources = ["Binary/df620fe8-eeaa-47c6-809c-84252e22980a"]
        response_string = (
            '{"entry": [{"response": {"location": '
            '"Group/ce64e19d-6d8a-4ef0-8fc6-1da83783aea8/_history/1"}}, {"response": '
            '{"location": "Group/aedd3c1a-5de8-45d5-8b35-5c288ccbb761/_history/1"}}]}'
        )
        expected_resource_list = [
            "Binary/df620fe8-eeaa-47c6-809c-84252e22980a",
            "Group/ce64e19d-6d8a-4ef0-8fc6-1da83783aea8",
            "Group/aedd3c1a-5de8-45d5-8b35-5c288ccbb761",
        ]

        created_resources = extract_resources(binary_resources, response_string)
        self.assertEqual(created_resources, expected_resource_list)

        resource = [
            [
                "Supply Inventory List",
                "current",
                "create",
                "77dae131-fd5d-4585-95db-2dd2b569d7a1",
            ]
        ]
        result_payload = build_payload(
            "List", resource, json_path + "group_list_payload.json"
        )
        full_list_payload = process_resources_list(result_payload, created_resources)

        resource_schema = {
            "type": "object",
            "properties": {
                "resourceType": {"const": "List"},
                "id": {"const": "77dae131-fd5d-4585-95db-2dd2b569d7a1"},
                "identifier": {"type": "array", "items": {"type": "object"}},
                "status": {"const": "current"},
                "mode": {"const": "working"},
                "title": {"const": "Supply Inventory List"},
                "entry": {"type": "array", "minItems": 3, "maxItems": 3},
            },
            "required": [
                "resourceType",
                "id",
                "identifier",
                "status",
                "mode",
                "title",
                "entry",
            ],
        }
        validate(full_list_payload["entry"][0]["resource"], resource_schema)

    def test_extract_matches(self):
        csv_file = csv_path + "organizations/organizations_locations.csv"
        resource_list = read_csv(csv_file)
        resources = extract_matches(resource_list)
        expected_matches = {
            "a9137781-eb94-4d5f-8d39-471a92aec9f2": [
                "138396:World",
                "54876:Kenya",
                "105167:Nairobi",
            ],
            "8342dd77-aecd-48ab-826b-75c7c33039ed": ["138396:World"],
        }
        self.assertEqual(resources, expected_matches)

    def test_build_org_affiliation(self):
        csv_file = csv_path + "organizations/organizations_locations.csv"
        resource_list = read_csv(csv_file)
        resources = extract_matches(resource_list)
        payload = build_org_affiliation(resources, resource_list)
        payload_obj = json.loads(payload)
        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 2)

    def test_uuid_generated_for_locations_is_unique_and_repeatable(self):
        resources = [
            [
                "City1",
                "active",
                "create",
                "",
                "test location-1",
                "18fcbc2e-4240-4a84-a270-7a444523d7b6",
                "jurisdiction",
                "jdn",
                "3",
                "jurisdiction",
                "jdn",
                "36.81",
                "36.81",
            ],
            [
                "Building1",
                "active",
                "create",
                "",
                "test location-1",
                "18fcbc2e-4240-4a84-a270-7a444523d7b6",
                "building",
                "bu",
                "3",
                "building",
                "bu",
                "36.81",
                "36.81",
            ],
            [
                "City1",
                "active",
                "create",
                "",
                "test location-1",
                "18fcbc2e-4240-4a84-a270-7a444523d7b6",
                "jurisdiction",
                "jdn",
                "3",
                "jurisdiction",
                "jdn",
                "36.81",
                "36.81",
            ],
        ]

        payload = build_payload(
            "locations",
            resources,
            json_path + "locations_payload.json",
            None,
            "http://terminology.hl7.org/CodeSystem/location-type",
        )
        payload_obj = json.loads(payload)
        location1 = payload_obj["entry"][0]["resource"]["id"]
        location2 = payload_obj["entry"][1]["resource"]["id"]
        location3 = payload_obj["entry"][2]["resource"]["id"]

        self.assertNotEqual(location1, location2)
        self.assertEqual(location1, location3)

    def test_uuid_generated_in_build_org_affiliation_is_unique_and_repeatable(self):
        resource_list = [
            ["HealthyU", "a9137781-eb94-4d5f-8d39-471a92aec9f2", "World", "138396"],
            ["HealthyU", "a9137781-eb94-4d5f-8d39-471a92aec9f2", "Kenya", "54876"],
            ["HealthyU", "a9137781-eb94-4d5f-8d39-471a92aec9f2", "Nairobi", "105167"],
            ["One Org", "8342dd77-aecd-48ab-826b-75c7c33039ed", "World", "138396"],
        ]

        resources = extract_matches(resource_list)
        payload = build_org_affiliation(resources, resource_list)
        payload_obj = json.loads(payload)
        organization_affiliation1 = payload_obj["entry"][0]["resource"]["id"]
        organization_affiliation2 = payload_obj["entry"][1]["resource"]["id"]

        self.assertNotEqual(organization_affiliation1, organization_affiliation2)

        payload2 = build_org_affiliation(resources, resource_list)
        payload2_obj = json.loads(payload2)
        organization_affiliation3 = payload2_obj["entry"][0]["resource"]["id"]
        organization_affiliation4 = payload2_obj["entry"][1]["resource"]["id"]

        self.assertEqual(organization_affiliation1, organization_affiliation3)
        self.assertEqual(organization_affiliation2, organization_affiliation4)

    def test_update_resource_with_no_id_fails(self):
        resources = [
            [
                "City1",
                "active",
                "update",
                "",
                "test location-1",
                "18fcbc2e-4240-4a84-a270-7a444523d7b6",
                "site",
                "si",
                "ward",
                "wa",
            ]
        ]
        with self.assertRaises(ValueError) as raised_error:
            build_payload("locations", resources, json_path + "locations_payload.json")
        self.assertEqual(
            "The id is required to update a resource", str(raised_error.exception)
        )

    @patch("importer.builder.get_resource")
    def test_update_resource_with_non_existing_id_fails(self, mock_get_resource):
        mock_get_resource.return_value = "0"
        non_existing_id = "123"
        resources = [
            [
                "City1",
                "active",
                "update",
                non_existing_id,
                "test location-1",
                "18fcbc2e-4240-4a84-a270-7a444523d7b6",
                "site",
                "si",
                "ward",
                "wa",
            ]
        ]
        with self.assertRaises(ValueError) as raised_error:
            build_payload("locations", resources, json_path + "locations_payload.json")
        self.assertEqual(
            "Trying to update a Non-existent resource", str(raised_error.exception)
        )

    @patch("importer.builder.handle_request")
    @patch("importer.builder.get_base_url")
    def test_build_assign_payload_update_assigned_org(
        self, mock_get_base_url, mock_handle_request
    ):
        mock_get_base_url.return_value = "https://example.smartregister.org/fhir"
        mock_response_data = {
            "resourceType": "Bundle",
            "total": 1,
            "entry": [
                {
                    "resource": {
                        "resourceType": "PractitionerRole",
                        "id": "de43b370-a772-434e-87b3-49b93e65a399",
                        "meta": {"versionId": "2"},
                        "practitioner": {
                            "reference": "Practitioner/f5d49ba0-50d7-4491-bd6c-62e429707a03",
                            "display": "Jenn",
                        },
                        "organization": {
                            "reference": "Organization/8342dd77-aecd-48ab-826b-75c7c33039ed",
                            "display": "Health Organization",
                        },
                    }
                }
            ],
        }
        string_response = json.dumps(mock_response_data)
        mock_response = (string_response, 200)
        mock_handle_request.return_value = mock_response

        resource_list = [
            [
                "Jenn",
                "f5d49ba0-50d7-4491-bd6c-62e429707a03",
                "New Org",
                "98199caa-4455-4b2f-a5cf-cb9c89b6bbdc",
            ]
        ]
        payload = build_assign_payload(
            resource_list, "PractitionerRole", "practitioner=Practitioner/"
        )
        payload_obj = json.loads(payload)

        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 1)

        self.assertEqual(
            payload_obj["entry"][0]["resource"]["practitioner"],
            mock_response_data["entry"][0]["resource"]["practitioner"],
        )
        self.assertNotEqual(
            payload_obj["entry"][0]["resource"]["organization"],
            mock_response_data["entry"][0]["resource"]["organization"],
        )
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["organization"]["reference"],
            "Organization/98199caa-4455-4b2f-a5cf-cb9c89b6bbdc",
        )
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["organization"]["display"], "New Org"
        )

    @patch("importer.builder.handle_request")
    @patch("importer.builder.get_base_url")
    def test_build_assign_payload_create_org_assignment(
        self, mock_get_base_url, mock_handle_request
    ):
        mock_get_base_url.return_value = "https://example.smartregister.org/fhir"
        mock_response_data = {
            "resourceType": "Bundle",
            "total": 1,
            "entry": [
                {
                    "resource": {
                        "resourceType": "PractitionerRole",
                        "id": "de43b370-a772-434e-87b3-49b93e65a399",
                        "meta": {"versionId": "2"},
                        "practitioner": {
                            "reference": "Practitioner/f5d49ba0-50d7-4491-bd6c-62e429707a03",
                            "display": "Jenn",
                        },
                    }
                }
            ],
        }
        string_response = json.dumps(mock_response_data)
        mock_response = (string_response, 200)
        mock_handle_request.return_value = mock_response

        resource_list = [
            [
                "Jenn",
                "f5d49ba0-50d7-4491-bd6c-62e429707a03",
                "New Org",
                "98199caa-4455-4b2f-a5cf-cb9c89b6bbdc",
            ]
        ]
        payload = build_assign_payload(
            resource_list, "PractitionerRole", "practitioner=Practitioner/"
        )
        payload_obj = json.loads(payload)

        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 1)

        self.assertEqual(
            payload_obj["entry"][0]["resource"]["practitioner"],
            mock_response_data["entry"][0]["resource"]["practitioner"],
        )
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["organization"]["reference"],
            "Organization/98199caa-4455-4b2f-a5cf-cb9c89b6bbdc",
        )
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["organization"]["display"], "New Org"
        )

    @patch("importer.builder.handle_request")
    @patch("importer.builder.get_base_url")
    def test_build_assign_payload_create_new_practitioner_role(
        self, mock_get_base_url, mock_handle_request
    ):
        mock_get_base_url.return_value = "https://example.smartregister.org/fhir"
        mock_response_data = {"resourceType": "Bundle", "total": 0}
        string_response = json.dumps(mock_response_data)
        mock_response = (string_response, 200)
        mock_handle_request.return_value = mock_response

        resource_list = [
            [
                "Jenn",
                "f5d49ba0-50d7-4491-bd6c-62e429707a03",
                "New Org",
                "98199caa-4455-4b2f-a5cf-cb9c89b6bbdc",
            ]
        ]
        payload = build_assign_payload(
            resource_list, "PractitionerRole", "practitioner=Practitioner/"
        )
        payload_obj = json.loads(payload)

        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 1)

        self.assertEqual(
            payload_obj["entry"][0]["resource"]["practitioner"]["reference"],
            "Practitioner/f5d49ba0-50d7-4491-bd6c-62e429707a03",
        )
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["practitioner"]["display"], "Jenn"
        )
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["organization"]["reference"],
            "Organization/98199caa-4455-4b2f-a5cf-cb9c89b6bbdc",
        )
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["organization"]["display"], "New Org"
        )

    @patch("importer.builder.handle_request")
    @patch("importer.builder.get_base_url")
    def test_build_assign_payload_create_new_link_location_to_inventory_list(
        self, mock_get_base_url, mock_handle_request
    ):
        mock_get_base_url.return_value = "https://example.smartregister.org/fhir"
        mock_response_data = {"resourceType": "Bundle", "total": 0}
        string_response = json.dumps(mock_response_data)
        mock_response = (string_response, 200)
        mock_handle_request.return_value = mock_response

        resource_list = [
            [
                "Nairobi Inventory Items",
                "e62a049f-8d48-456c-a387-f52e72c39c74",
                "2024-06-01T10:40:10.111Z",
                "3af23539-850a-44ed-8fb1-d4999e2145ff",
            ]
        ]
        payload = build_assign_payload(resource_list, "List", "subject=List/")
        payload_obj = json.loads(payload)

        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 1)

        self.assertEqual(
            payload_obj["entry"][0]["resource"]["title"], "Nairobi Inventory Items"
        )
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["entry"][0]["item"]["reference"],
            "Group/e62a049f-8d48-456c-a387-f52e72c39c74",
        )
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["entry"][0]["date"],
            "2024-06-01T10:40:10.111Z",
        )
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["subject"]["reference"],
            "Location/3af23539-850a-44ed-8fb1-d4999e2145ff",
        )

    @patch("importer.builder.handle_request")
    @patch("importer.builder.get_base_url")
    def test_build_assign_payload_update_location_with_new_inventory(
        self, mock_get_base_url, mock_handle_request
    ):
        mock_get_base_url.return_value = "https://example.smartregister.org/fhir"
        mock_response_data = {
            "resourceType": "Bundle",
            "total": 1,
            "entry": [
                {
                    "resource": {
                        "resourceType": "List",
                        "id": "6d7d2e70-1c90-11db-861d-0242ac120002",
                        "meta": {"versionId": "2"},
                        "subject": {
                            "reference": "Location/46bb8a3f-cf50-4cc2-b421-fe4f77c3e75d"
                        },
                        "entry": [
                            {
                                "item": {
                                    "reference": "Group/f2734756-a6bb-4e90-bbc6-1c34f51d3d5c"
                                }
                            }
                        ],
                    }
                }
            ],
        }
        string_response = json.dumps(mock_response_data)
        mock_response = (string_response, 200)
        mock_handle_request.return_value = mock_response

        resource_list = [
            [
                "Nairobi Inventory Items",
                "e62a049f-8d48-456c-a387-f52e72c39c74",
                "2024-06-01T10:40:10.111Z",
                "3af23539-850a-44ed-8fb1-d4999e2145ff",
            ]
        ]

        payload = build_assign_payload(resource_list, "List", "subject=List/")
        payload_obj = json.loads(payload)

        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 1)

        self.assertEqual(
            payload_obj["entry"][0]["resource"]["entry"][0]["item"]["reference"],
            "Group/f2734756-a6bb-4e90-bbc6-1c34f51d3d5c",
        )
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["entry"][1]["item"]["reference"],
            "Group/e62a049f-8d48-456c-a387-f52e72c39c74",
        )

    @patch("importer.builder.handle_request")
    @patch("importer.builder.get_base_url")
    def test_build_assign_payload_create_new_link_location_to_inventory_list_with_multiples(
        self, mock_get_base_url, mock_handle_request
    ):
        mock_get_base_url.return_value = "https://example.smartregister.org/fhir"
        mock_response_data = {"resourceType": "Bundle", "total": 0}
        string_response = json.dumps(mock_response_data)
        mock_response = (string_response, 200)
        mock_handle_request.return_value = mock_response

        resource_list = [
            [
                "Nairobi Inventory Items",
                "e62a049f-8d48-456c-a387-f52e72c39c74",
                "2024-06-01T10:40:10.111Z",
                "3af23539-850a-44ed-8fb1-d4999e2145ff",
            ],
            [
                "Nairobi Inventory Items",
                "a36b595c-68a7-4244-91d5-c64be23b1ebd",
                "2024-06-05T30:30:30.264Z",
                "3af23539-850a-44ed-8fb1-d4999e2145ff",
            ],
            [
                "Mombasa Inventory Items",
                "c0666a5a-00f6-488c-9001-8630560b5810",
                "2024-06-06T55:23:19.492Z",
                "3cd687a4-a169-45b3-a939-0418470c29db",
            ],
        ]
        payload = build_assign_payload(resource_list, "List", "subject=List/")
        payload_obj = json.loads(payload)

        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 2)
        self.assertEqual(len(payload_obj["entry"][0]["resource"]["entry"]), 2)
        self.assertEqual(len(payload_obj["entry"][1]["resource"]["entry"]), 1)

        self.assertEqual(
            payload_obj["entry"][0]["resource"]["title"], "Nairobi Inventory Items"
        )
        self.assertEqual(
            payload_obj["entry"][1]["resource"]["title"], "Mombasa Inventory Items"
        )
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["entry"][0]["item"]["reference"],
            "Group/e62a049f-8d48-456c-a387-f52e72c39c74",
        )
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["entry"][1]["item"]["reference"],
            "Group/a36b595c-68a7-4244-91d5-c64be23b1ebd",
        )
        self.assertEqual(
            payload_obj["entry"][1]["resource"]["entry"][0]["item"]["reference"],
            "Group/c0666a5a-00f6-488c-9001-8630560b5810",
        )

    @patch("importer.builder.check_parent_admin_level")
    @patch("importer.builder.get_resource")
    def test_define_own_location_type_coding_system_url(
        self, mock_get_resource, mock_check_parent_admin_level
    ):
        mock_get_resource.return_value = "1"
        mock_check_parent_admin_level.return_value = "3"
        test_system_code = "http://terminology.hl7.org/CodeSystem/test_location-type"

        csv_file = csv_path + "locations/locations_full.csv"
        resource_list = read_csv(csv_file)
        payload = build_payload(
            "locations",
            resource_list,
            json_path + "locations_payload.json",
            None,
            test_system_code,
        )
        payload_obj = json.loads(payload)
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["type"][0]["coding"][0]["system"],
            test_system_code,
        )
