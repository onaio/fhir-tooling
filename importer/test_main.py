import json
import unittest
from jsonschema import validate
from mock import patch
from main import (
    read_csv,
    write_csv,
    build_payload,
    build_org_affiliation,
    extract_matches,
    create_user_resources,
    export_resources_to_csv,
    build_assign_payload,
    create_user,
    confirm_keycloak_user,
    confirm_practitioner,
    check_parent_admin_level,
    split_chunk,
    read_file_in_chunks,
    extract_resources,
    process_resources_list,
)


class TestMain(unittest.TestCase):
    def test_read_csv(self):
        csv_file = "csv/users.csv"
        records = read_csv(csv_file)
        self.assertIsInstance(records, list)
        self.assertEqual(len(records), 3)

    def test_write_csv(self):
        self.test_data = [
            [
                "e2e-mom",
                "True",
                "update",
                "caffe509-ae56-4d42-945e-7b4c161723d1",
                "d93ae7c3-73c0-43d1-9046-425a3466ecec",
            ],
            [
                "e2e-skate",
                "True",
                "update",
                "2d4feac9-9ab5-4585-9b33-e5abd14ceb0f",
                "58605ed8-7217-4bf3-8122-229b6f47fa64",
            ],
        ]
        self.test_resource_type = "test_organization"
        self.test_fieldnames = ["name", "active", "method", "id", "identifier"]
        csv_file = write_csv(
            self.test_data, self.test_resource_type, self.test_fieldnames
        )
        csv_content = read_csv(csv_file)
        self.assertEqual(csv_content, self.test_data)

    @patch("main.get_resource")
    def test_build_payload_organizations(self, mock_get_resource):
        mock_get_resource.return_value = "1"

        csv_file = "csv/organizations/organizations_full.csv"
        resource_list = read_csv(csv_file)
        payload = build_payload(
            "organizations", resource_list, "json_payloads/organizations_payload.json"
        )
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
        csv_file = "csv/organizations/organizations_min.csv"
        resource_list = read_csv(csv_file)
        payload = build_payload(
            "organizations", resource_list, "json_payloads/organizations_payload.json"
        )
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

    @patch("main.check_parent_admin_level")
    @patch("main.get_resource")
    def test_build_payload_locations(
        self, mock_get_resource, mock_check_parent_admin_level
    ):
        mock_get_resource.return_value = "1"
        mock_check_parent_admin_level.return_value = "3"

        csv_file = "csv/locations/locations_full.csv"
        resource_list = read_csv(csv_file)
        payload = build_payload(
            "locations", resource_list, "json_payloads/locations_payload.json",
            None, "http://terminology.hl7.org/CodeSystem/location-type"
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
        csv_file = "csv/locations/locations_min.csv"
        resource_list = read_csv(csv_file)
        payload = build_payload(
            "locations", resource_list, "json_payloads/locations_payload.json",
            None, "http://terminology.hl7.org/CodeSystem/location-type"
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

    @patch("main.handle_request")
    @patch("main.get_base_url")
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
        locationParentId = "18fcbc2e-4240-4a84-a270-7a444523d7b6"
        admin_level = check_parent_admin_level(locationParentId)
        self.assertEqual(admin_level, "3")

    @patch("main.get_resource")
    def test_build_payload_care_teams(self, mock_get_resource):
        mock_get_resource.return_value = "1"

        csv_file = "csv/careteams/careteam_full.csv"
        resource_list = read_csv(csv_file)
        payload = build_payload(
            "careTeams", resource_list, "json_payloads/careteams_payload.json"
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

    @patch("main.save_image")
    @patch("main.get_resource")
    def test_build_payload_group(self, mock_get_resource, mock_save_image):
        mock_get_resource.return_value = "1"
        mock_save_image.return_value = "f374a23a-3c6a-4167-9970-b10c16a91bbd"

        csv_file = "csv/import/product.csv"
        resource_list = read_csv(csv_file)
        payload, list_resource = build_payload(
            "Group", resource_list, "json_payloads/product_group_payload.json", []
        )
        payload_obj = json.loads(payload)
        self.assertEqual(list_resource, ['Binary/f374a23a-3c6a-4167-9970-b10c16a91bbd'])

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
        binary_resources = ['Binary/df620fe8-eeaa-47c6-809c-84252e22980a']
        response_string = ('{"entry": [{"response": {"location": '
                           '"Group/ce64e19d-6d8a-4ef0-8fc6-1da83783aea8/_history/1"}}, {"response": '
                           '{"location": "Group/aedd3c1a-5de8-45d5-8b35-5c288ccbb761/_history/1"}}]}')
        expected_resource_list = ['Binary/df620fe8-eeaa-47c6-809c-84252e22980a',
                                  'Group/ce64e19d-6d8a-4ef0-8fc6-1da83783aea8',
                                  'Group/aedd3c1a-5de8-45d5-8b35-5c288ccbb761']

        created_resources = extract_resources(binary_resources, response_string)
        self.assertEqual(created_resources, expected_resource_list)

        resource = [["Supply Inventory List", "current", "create", "77dae131-fd5d-4585-95db-2dd2b569d7a1"]]
        result_payload = build_payload(
            "List", resource, "json_payloads/product_list_payload.json")
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
            "required": ["resourceType", "id", "identifier", "status", "mode", "title", "entry"],
        }
        validate(full_list_payload["entry"][0]["resource"], resource_schema)

    def test_extract_matches(self):
        csv_file = "csv/organizations/organizations_locations.csv"
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
        csv_file = "csv/organizations/organizations_locations.csv"
        resource_list = read_csv(csv_file)
        resources = extract_matches(resource_list)
        payload = build_org_affiliation(resources, resource_list)
        payload_obj = json.loads(payload)
        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 2)

    def test_uuid_generated_in_creating_user_resources_is_unique_and_repeatable(self):
        users = [
            [
                "Jane",
                "Doe",
                "Janey",
                "jdoe@example.com",
                "",
                "Practitioner",
                "true",
                "a715b562-27f2-432a-b1ba-e57db35e0f93",
                "test",
                "demo",
                "pa$$word",
            ],
            [
                "John",
                "Doe",
                "Janey",
                "jodoe@example.com",
                "",
                "Practitioner",
                "true",
                "a715b562-27f2-432a-b1ba-e57db35e0f93",
                "test",
                "demo",
                "pa$$word",
            ],
            [
                "Janice",
                "Doe",
                "Jenn",
                "jendoe@example.com",
                "99d54e3c-c26f-4500-a7f9-3f4cb788673f",
                "Supervisor",
                "false",
                "a715b562-27f2-432a-b1ba-e57db35e0f93",
                "test",
                "demo",
                "pa$$word",
            ],
        ]

        users_uuids = {}
        for user_id, user in enumerate(users):
            payload = create_user_resources(user[4], user)
            payload_obj = json.loads(payload)
            practitioner_uuid = payload_obj["entry"][0]["resource"]["id"]
            group_uuid = payload_obj["entry"][1]["resource"]["id"]
            practitioner_role_uuid = payload_obj["entry"][2]["resource"]["id"]
            users_uuids[user_id] = [
                practitioner_uuid,
                group_uuid,
                practitioner_role_uuid,
            ]

        # practitioner_uuid
        self.assertEqual(users_uuids[0][0], users_uuids[1][0])
        self.assertNotEqual(users_uuids[1][0], users_uuids[2][0])

        # group_uuid
        self.assertEqual(users_uuids[0][1], users_uuids[1][1])
        self.assertNotEqual(users_uuids[1][1], users_uuids[2][1])

        # practitioner_role_uuid
        self.assertEqual(users_uuids[0][2], users_uuids[1][2])
        self.assertNotEqual(users_uuids[1][2], users_uuids[2][2])

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
            "locations", resources, "json_payloads/locations_payload.json",
            None, "http://terminology.hl7.org/CodeSystem/location-type"
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
            build_payload(
                "locations", resources, "json_payloads/locations_payload.json"
            )
        self.assertEqual(
            "The id is required to update a resource", str(raised_error.exception)
        )

    @patch("main.get_resource")
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
            build_payload(
                "locations", resources, "json_payloads/locations_payload.json"
            )
        self.assertEqual(
            "Trying to update a Non-existent resource", str(raised_error.exception)
        )

    @patch("main.write_csv")
    @patch("main.handle_request")
    @patch("main.get_base_url")
    def test_export_resource_to_csv(
        self, mock_get_base_url, mock_handle_request, mock_write_csv
    ):
        mock_get_base_url.return_value = "https://example.smartregister.org/fhir"
        mock_response_data = {
            "entry": [
                {
                    "resource": {
                        "name": "City1",
                        "status": "active",
                        "id": "ba787982-b973-4bd5-854e-eacbe161e297",
                        "identifier": [
                            {"value": "ba787 982-b973-4bd5-854e-eacbe161e297"}
                        ],
                        "partOf": {
                            "display": "test location-1",
                            "reference": "Location/18fcbc2e-4240-4a84-a270"
                            "-7a444523d7b6",
                        },
                        "type": [
                            {"coding": [{"display": "Jurisdiction", "code": "jdn"}]}
                        ],
                        "physicalType": {
                            "coding": [{"display": "Jurisdiction", "code": "jdn"}]
                        },
                    }
                }
            ]
        }
        string_response = json.dumps(mock_response_data)
        mock_response = (string_response, 200)
        mock_handle_request.return_value = mock_response
        test_data = [
            [
                "City1",
                "active",
                "update",
                "ba787982-b973-4bd5-854e-eacbe161e297",
                "ba787 982-b973-4bd5-854e-eacbe161e297",
                "test location-1",
                "18fcbc2e-4240-4a84-a270-7a444523d7b6",
                "Jurisdiction",
                "jdn",
                "Jurisdiction",
                "jdn",
            ]
        ]
        test_elements = [
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
        resource_type = "Location"
        export_resources_to_csv("Location", "_lastUpdated", "gt2023-08-01", 1)
        mock_write_csv.assert_called_once_with(test_data, resource_type, test_elements)

    @patch("main.handle_request")
    @patch("main.get_base_url")
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

    @patch("main.handle_request")
    @patch("main.get_base_url")
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

    @patch("main.handle_request")
    @patch("main.get_base_url")
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

    @patch("main.handle_request")
    @patch("main.get_base_url")
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

    @patch("main.handle_request")
    @patch("main.get_base_url")
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

    @patch("main.handle_request")
    @patch("main.get_base_url")
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

    @patch("main.logging")
    @patch("main.handle_request")
    @patch("main.get_keycloak_url")
    def test_create_user(
        self, mock_get_keycloak_url, mock_handle_request, mock_logging
    ):
        mock_get_keycloak_url.return_value = (
            "https://keycloak.smartregister.org/auth/admin/realms/example-realm"
        )
        mock_handle_request.return_value.status_code = 201
        mock_handle_request.return_value.headers = {
            "Location": "https://keycloak.smartregister.org/auth/admin/realms"
            "/example-realm/users/6cd50351-3ddb-4296-b1db"
            "-aac2273e35f3"
        }
        mocked_user_data = (
            "Jenn",
            "Doe",
            "Jenny",
            "jeendoe@example.com",
            "431cb523-253f-4c44-9ded-af42c55c0bbb",
            "Supervisor",
            "TRUE",
            "a715b562-27f2-432a-b1ba-e57db35e0f93",
            "test",
            "demo",
            "pa$$word",
        )
        user_id = create_user(mocked_user_data)

        self.assertEqual(user_id, "6cd50351-3ddb-4296-b1db-aac2273e35f3")
        mock_logging.info.assert_called_with("Setting user password")

    @patch("main.handle_request")
    @patch("main.get_keycloak_url")
    def test_create_user_already_exists(
        self, mock_get_keycloak_url, mock_handle_request
    ):
        mock_get_keycloak_url.return_value = (
            "https://keycloak.smartregister.org/auth/admin/realms/example-realm"
        )
        mock_handle_request.return_value.status_code = 409
        mocked_user_data = (
            "Jenn",
            "Doe",
            "Jenn",
            "jendoe@example.com",
            " 99d54e3c-c26f-4500-a7f9-3f4cb788673f",
            "Supervisor",
            "false",
            "a715b562-27f2-432a-b1ba-e57db35e0f93",
            "test",
            "demo",
            "pa$$word",
        )
        user_id = create_user(mocked_user_data)
        self.assertEqual(user_id, 0)

    # Test the confirm_keycloak function
    @patch("main.logging")
    @patch("main.handle_request")
    @patch("main.get_keycloak_url")
    def test_confirm_keycloak_user(
        self, mock_get_keycloak_url, mock_handle_request, mock_logging
    ):
        mock_get_keycloak_url.return_value = (
            "https://keycloak.smartregister.org/auth/admin/realms/example-realm"
        )
        mocked_user_data = (
            "Jenn",
            "Doe",
            "Jenny",
            "jeendoe@example.com",
            "431cb523-253f-4c44-9ded-af42c55c0bbb",
            "Supervisor",
            "TRUE",
            "a715b562-27f2-432a-b1ba-e57db35e0f93",
            "test",
            "demo",
            "pa$$word",
        )
        user_id = create_user(mocked_user_data)
        self.assertEqual(user_id, 0)

        mock_response = (
            '[{"id":"6cd50351-3ddb-4296-b1db-aac2273e35f3","createdTimestamp":1710151827166,'
            '"username":"Jenny","enabled":true,"totp":false,"emailVerified":false,"firstName":"Jenn",'
            '"lastName":"Doe","email":"jeendoe@example.com","attributes":{"fhir_core_app_id":["demo"]},'
            '"disableableCredentialTypes":[],"requiredActions":[],"notBefore":0,"access":{'
            '"manageGroupMembership":true,"view":true,"mapRoles":true,"impersonate":true,'
            '"manage":true}}]',
            200,
        )
        mock_handle_request.return_value = mock_response
        mock_json_response = json.loads(mock_response[0])
        keycloak_id = confirm_keycloak_user(mocked_user_data)

        self.assertEqual(mock_json_response[0]["username"], "Jenny")
        self.assertEqual(mock_json_response[0]["email"], "jeendoe@example.com")
        mock_logging.info.assert_called_with("User confirmed with id: " + keycloak_id)

    # Test confirm_practitioner function
    @patch("main.handle_request")
    @patch("main.get_base_url")
    def test_confirm_practitioner_if_practitioner_uuid_not_provided(
        self, mock_get_base_url, mock_handle_request
    ):
        mock_get_base_url.return_value = "https://example.smartregister.org/fhir"
        mocked_user = (
            "Jenn",
            "Doe",
            "Jenny",
            "jeendoe@example.com",
            "",
            "Supervisor",
            "TRUE",
            "a715b562-27f2-432a-b1ba-e57db35e0f93",
            "test",
            "demo",
            "pa$$word",
        )
        mocked_response_data = {
            "resourceType": "Bundle",
            "type": "searchset",
            "total": 1,
        }
        string_response = json.dumps(mocked_response_data)
        mock_response = (string_response, 200)
        mock_handle_request.return_value = mock_response
        practitioner_exists = confirm_practitioner(
            mocked_user, "431cb523-253f-4c44-9ded-af42c55c0bbb"
        )
        self.assertTrue(
            practitioner_exists, "Practitioner exist, linked to the provided user"
        )

    @patch("main.logging")
    @patch("main.handle_request")
    @patch("main.get_base_url")
    def test_confirm_practitioner_linked_keycloak_user_and_practitioner(
        self, mock_get_base_url, mock_handle_request, mock_logging
    ):
        mock_get_base_url.return_value = "https://example.smartregister.org/fhir"
        mocked_user = (
            "Jenn",
            "Doe",
            "Jenny",
            "jeendoe@example.com",
            "6cd50351-3ddb-4296-b1db-aac2273e35f3",
            "Supervisor",
            "TRUE",
            "a715b562-27f2-432a-b1ba-e57db35e0f93",
            "test",
            "demo",
            "pa$$word",
        )
        mocked_response_data = {
            "resourceType": "Practitioner",
            "identifier": [
                {"use": "official", "value": "431cb523-253f-4c44-9ded-af42c55c0bbb"},
                {"use": "secondary", "value": "6cd50351-3ddb-4296-b1db-aac2273e35f3"},
            ],
        }
        string_response = json.dumps(mocked_response_data)
        mock_response = (string_response, 200)
        mock_handle_request.return_value = mock_response
        practitioner_exists = confirm_practitioner(
            mocked_user, "6cd50351-3ddb-4296-b1db-aac2273e35f3"
        )
        self.assertTrue(practitioner_exists)
        self.assertEqual(
            mocked_response_data["identifier"][1]["value"],
            "6cd50351-3ddb-4296-b1db-aac2273e35f3",
        )
        mock_logging.info.assert_called_with(
            "The Keycloak user and Practitioner are linked as expected"
        )

    # Test create_user_resources function
    def test_create_user_resources(self):
        user = (
            "Jenn",
            "Doe",
            "Jenn",
            "jendoe@example.com",
            "99d54e3c-c26f-4500-a7f9-3f4cb788673f",
            "Supervisor",
            "false",
            "a715b562-27f2-432a-b1ba-e57db35e0f93",
            "test",
            "demo",
            "pa$$word",
        )
        user_id = "99d54e3c-c26f-4500-a7f9-3f4cb788673f"
        payload = create_user_resources(user_id, user)
        payload_obj = json.loads(payload)
        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 3)

        resource_schema = {
            "type": "object",
            "properties": {
                "resourceType": {"const": "Practitioner"},
                "id": {"const": "99d54e3c-c26f-4500-a7f9-3f4cb788673f"},
                "identifier": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "use": {
                                "type": "string",
                                "enum": ["official", "secondary"],
                            },
                            "type": {
                                "type": "object",
                                "properties": {
                                    "coding": {
                                        "type": "array",
                                        "items": {
                                            "type": "object",
                                            "properties": {
                                                "system": {
                                                    "const": "http://hl7.org/fhir/identifier-type"
                                                },
                                                "code": {"const": "KUID"},
                                                "display": {
                                                    "const": "Keycloak user ID"
                                                },
                                            },
                                        },
                                    },
                                    "text": {"const": "Keycloak user ID"},
                                },
                            },
                            "value": {"const": "99d54e3c-c26f-4500-a7f9-3f4cb788673f"},
                        },
                    },
                },
                "name": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "use": {"const": "official"},
                            "family": {"const": "Doe"},
                            "given": {"type": "array", "items": {"type": "string"}},
                        },
                    },
                },
            },
            "required": ["resourceType", "id", "identifier", "name"],
        }
        validate(payload_obj["entry"][0]["resource"], resource_schema)

        request_schema = {
            "type": "object",
            "properties": {
                "method": {"const": "PUT"},
                "url": {"const": "Practitioner/99d54e3c-c26f-4500-a7f9-3f4cb788673f"},
                "ifMatch": {"const": "1"},
            },
        }
        validate(payload_obj["entry"][0]["request"], request_schema)

        resource_schema = {
            "type": "object",
            "properties": {
                "resourceType": {"const": "Group"},
                "id": {"const": "0de5f541-65ca-5504-ad6b-9b386e5f8810"},
                "identifier": {"type": "array", "items": {"type": "object"}},
                "name": {"const": "Jenn Doe"},
                "member": {
                    "type": "array",
                    "items": {
                        "type": "object",
                        "properties": {
                            "entity": {
                                "type": "object",
                                "properties": {
                                    "reference": {
                                        "const": "Practitioner/99d54e3c-c26f-4500-a7f9-3f4cb788673f"
                                    }
                                },
                            }
                        },
                    },
                },
            },
            "required": ["resourceType", "id", "identifier", "name", "member"],
        }
        validate(payload_obj["entry"][1]["resource"], resource_schema)

        request_schema = {
            "type": "object",
            "properties": {
                "method": {"const": "PUT"},
                "url": {"const": "Group/0de5f541-65ca-5504-ad6b-9b386e5f8810"},
                "ifMatch": {"const": "1"},
            },
        }
        validate(payload_obj["entry"][1]["request"], request_schema)

        resource_schema = {
            "type": "object",
            "properties": {
                "resourceType": {"const": "PractitionerRole"},
                "id": {"const": "f08e0373-932e-5bcb-bdf2-0c28a3c8fdd3"},
                "identifier": {"type": "array", "items": {"type": "object"}},
                "practitioner": {
                    "type": "object",
                    "properties": {
                        "reference": {
                            "const": "Practitioner/99d54e3c-c26f-4500-a7f9-3f4cb788673f"
                        },
                        "display": {"const": "Jenn Doe"},
                    },
                },
                "code": {
                    "type": "object",
                    "properties": {
                        "coding": {
                            "type": "array",
                            "items": {
                                "type": "object",
                                "properties": {
                                    "system": {"const": "http://snomed.info/sct"},
                                    "code": {"const": "236321002"},
                                    "display": {"const": "Supervisor (occupation)"},
                                },
                            },
                        }
                    },
                },
            },
            "required": ["resourceType", "id", "identifier", "practitioner", "code"],
        }
        validate(payload_obj["entry"][2]["resource"], resource_schema)

        request_schema = {
            "type": "object",
            "properties": {
                "method": {"const": "PUT"},
                "url": {
                    "const": "PractitionerRole/f08e0373-932e-5bcb-bdf2-0c28a3c8fdd3"
                },
                "ifMatch": {"const": "1"},
            },
        }
        validate(payload_obj["entry"][2]["request"], request_schema)

    @patch("main.set_resource_list")
    def test_split_chunk_direct_sync_first_chunk_less_than_size(
        self, mock_set_resource_list
    ):
        chunk = '[{"id": "10", "resourceType": "Patient"}'
        next_left_over = split_chunk(chunk, "", 50, {}, "direct")
        chunk_list = '[{"id": "10", "resourceType": "Patient"}]'
        self.assertEqual(next_left_over, "-")
        mock_set_resource_list.assert_called_once_with(chunk_list)

    @patch("main.set_resource_list")
    def test_split_chunk_direct_sync_middle_chunk_less_than_size(
        self, mock_set_resource_list
    ):
        chunk = ' "resourceType": "Patient"}'
        left_over_chunk = '{"id": "10",'
        next_left_over = split_chunk(chunk, left_over_chunk, 50, {}, "direct")
        chunk_list = '[{"id": "10", "resourceType": "Patient"}]'
        self.assertEqual(next_left_over, "-")
        mock_set_resource_list.assert_called_once_with(chunk_list)

    @patch("main.set_resource_list")
    def test_split_chunk_direct_sync_last_chunk_less_than_size(
        self, mock_set_resource_list
    ):
        left_over_chunk = '{"id": "10", "resourceType": "Patient"}]'
        next_left_over = split_chunk("", left_over_chunk, 50, {}, "direct")
        chunk_list = '[{"id": "10", "resourceType": "Patient"}]'
        self.assertEqual(next_left_over, "-")
        mock_set_resource_list.assert_called_once_with(chunk_list)

    @patch("main.set_resource_list")
    def test_split_chunk_direct_sync_first_chunk_greater_than_size(
        self, mock_set_resource_list
    ):
        chunk = '[{"id": "10", "resourceType": "Patient"},{"id": "11", "resourceType":'
        next_left_over = split_chunk(chunk, "", 40, {}, "direct")
        chunk_list = '[{"id": "10", "resourceType": "Patient"}]'
        self.assertEqual(next_left_over, '{"id": "11", "resourceType":')
        mock_set_resource_list.assert_called_once_with(chunk_list)

    @patch("main.set_resource_list")
    def test_split_chunk_direct_sync_middle_chunk_greater_than_size(
        self, mock_set_resource_list
    ):
        chunk = ': "Task"},{"id": "10", "resourceType": "Patient"},{"id": "11", "resourceType":'
        left_over_chunk = '{"id": "09", "resourceType"'
        next_left_over = split_chunk(chunk, left_over_chunk, 80, {}, "direct")
        chunk_list = '[{"id": "09", "resourceType": "Task"},{"id": "10", "resourceType": "Patient"}]'
        self.assertEqual(next_left_over, '{"id": "11", "resourceType":')
        mock_set_resource_list.assert_called_once_with(chunk_list)

    @patch("main.set_resource_list")
    def test_split_chunk_direct_sync_last_chunk_greater_than_size(
        self, mock_set_resource_list
    ):
        left_over_chunk = '{"id": "10", "resourceType": "Patient"},{"id": "11", "resourceType": "Task"}]'
        next_left_over = split_chunk("", left_over_chunk, 43, {}, "direct")
        chunk_list = '[{"id": "10", "resourceType": "Patient"},{"id": "11", "resourceType": "Task"}]'
        self.assertEqual(next_left_over, "")
        mock_set_resource_list.assert_called_once_with(chunk_list)

    @patch("main.set_resource_list")
    @patch("main.build_resource_type_map")
    def test_split_chunk_sort_sync_first_chunk_less_than_size(
        self, mock_build_resource_type_map, mock_set_resource_list
    ):
        chunk = '[{"id": "10", "resourceType": "Patient"},{"id": "11"'
        next_left_over = split_chunk(chunk, "", 50, {}, "sort")
        chunk_list = '[{"id": "10", "resourceType": "Patient"}]'
        self.assertEqual(next_left_over, '{"id": "11"')
        mock_set_resource_list.assert_not_called()
        mock_build_resource_type_map.assert_called_once_with(chunk_list, {}, 0)

    def test_build_resource_type_map(self):
        json_file = "tests/json/sample.json"
        mapping = read_file_in_chunks(json_file, 300, "sort")
        mapped_resources = {
            "Patient": [0],
            "Practitioner": [1, 5],
            "Location": [2, 4],
            "Observation": [3],
        }
        self.assertIsInstance(mapping, dict)
        self.assertEqual(mapping, mapped_resources)

    @patch("main.check_parent_admin_level")
    @patch("main.get_resource")
    def test_define_own_location_type_coding_system_url(self, mock_get_resource, mock_check_parent_admin_level):
        mock_get_resource.return_value = "1"
        mock_check_parent_admin_level.return_value = "3"
        test_system_code = "http://terminology.hl7.org/CodeSystem/test_location-type"

        csv_file = "csv/locations/locations_full.csv"
        resource_list = read_csv(csv_file)
        payload = build_payload(
            "locations", resource_list, "json_payloads/locations_payload.json",
            None, test_system_code
        )
        payload_obj = json.loads(payload)
        self.assertEqual(payload_obj["entry"][0]["resource"]["type"][0]["coding"][0]["system"], test_system_code)


if __name__ == "__main__":
    unittest.main()
