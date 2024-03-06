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
    build_assign_payload
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
                "handy",
            ],
            [
                "e2e-skate",
                "True",
                "update",
                "2d4feac9-9ab5-4585-9b33-e5abd14ceb0f",
                "58605ed8-7217-4bf3-8122-229b6f47fa64",
                "foolish",
            ],
        ]
        self.test_resource_type = "test_organization"
        self.test_fieldnames = ["name", "active", "method", "id", "identifier", "alias"]
        csv_file = write_csv(self.test_data, self.test_resource_type, self.test_fieldnames)
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
                "alias": {"const": ["Health Org"]},
            },
            "required": ["resourceType", "id", "identifier", "active", "name", "alias"],
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
                "name": {"const": "Min Organization"}
            },
            "required": [
                "id",
                "identifier",
                "active",
                "name"
            ],
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

    @patch("main.get_resource")
    def test_build_payload_locations(self, mock_get_resource):
        mock_get_resource.return_value = "1"

        csv_file = "csv/locations/locations_full.csv"
        resource_list = read_csv(csv_file)
        payload = build_payload(
            "locations", resource_list, "json_payloads/locations_payload.json"
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
                                        "system": {
                                            "const": "http://terminology.hl7.org/CodeSystem/location-type"
                                        },
                                        "code": {"const": "si"},
                                        "display": {"const": "site"},
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
            "locations", resource_list, "json_payloads/locations_payload.json"
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
                "name": {"const": "City1"}
            },
            "required": [
                "id",
                "identifier",
                "status",
                "name"
            ],
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
            },
            "required": ["resourceType", "id", "identifier", "status", "name"],
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

        #  TestCase careteam_min.csv
        csv_file = "csv/careteams/careteam_min.csv"
        resource_list = read_csv(csv_file)
        payload = build_payload(
            "careTeams", resource_list, "json_payloads/careteams_payload.json"
        )
        payload_obj = json.loads(payload)
        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 2)

        resource_schema = {
            "type": "object",
            "properties": {
                "resourceType": {"const": "CareTeam"},
                "id": {"const": "17150b86-00db-599b-9984-1e7aa08291bb"},
                "identifier": {"type": "array", "items": {"type": "object"}},
                "status": {"const": ""},
                "name": {"const": "Good careteam"}
            },
            "required": [
                "id",
                "identifier",
                "status",
                "name"
            ],
        }
        validate(payload_obj["entry"][0]["resource"], resource_schema)

        request_schema = {
            "type": "object",
            "properties": {
                "method": {"const": "PUT"},
                "url": {"const": "Location/17150b86-00db-599b-9984-1e7aa08291bb"},
                "ifMatch": {"const": "1"},
            },
        }
        validate(payload_obj["entry"][0]["resource"], request_schema)

    def test_extract_matches(self):
        csv_file = "csv/organizations/organization_locations.csv"
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
        csv_file = "csv/organizations/organization_locations.csv"
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
                "TRUE",
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
                "TRUE",
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
                "TRUE",
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
                "jurisdiction",
            ],
            [
                "Building1",
                "active",
                "create",
                "",
                "test location-1",
                "18fcbc2e-4240-4a84-a270-7a444523d7b6",
                "building",
                "building",
            ],
            [
                "City1",
                "active",
                "create",
                "",
                "test location-1",
                "18fcbc2e-4240-4a84-a270-7a444523d7b6",
                "jurisdiction",
                "jurisdiction",
            ],
        ]

        payload = build_payload(
            "locations", resources, "json_payloads/locations_payload.json"
        )
        payload_obj = json.loads(payload)
        location1 = payload_obj["entry"][0]["resource"]["id"]
        location2 = payload_obj["entry"][1]["resource"]["id"]
        location3 = payload_obj["entry"][2]["resource"]["id"]
        print(location1, location2, location3)

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
    def test_build_assign_payload_update_assigned_org(self, mock_get_base_url, mock_handle_request):
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
                            "display": "Jenn"
                        },
                        "organization": {
                            "reference": "Organization/8342dd77-aecd-48ab-826b-75c7c33039ed",
                            "display": "Health Organization"
                        }
                    }
                }
            ]
        }
        string_response = json.dumps(mock_response_data)
        mock_response = (string_response, 200)
        mock_handle_request.return_value = mock_response

        resource_list = [
            ["f5d49ba0-50d7-4491-bd6c-62e429707a03", "Jenn", "98199caa-4455-4b2f-a5cf-cb9c89b6bbdc", "New Org"]
        ]
        payload = build_assign_payload(resource_list, "PractitionerRole")
        payload_obj = json.loads(payload)

        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 1)

        self.assertEqual(
            payload_obj["entry"][0]["resource"]["practitioner"],
            mock_response_data["entry"][0]["resource"]["practitioner"])
        self.assertNotEqual(
            payload_obj["entry"][0]["resource"]["organization"],
            mock_response_data["entry"][0]["resource"]["organization"])
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["organization"]["reference"],
            "Organization/98199caa-4455-4b2f-a5cf-cb9c89b6bbdc")
        self.assertEqual(payload_obj["entry"][0]["resource"]["organization"]["display"], "New Org")

    @patch("main.handle_request")
    @patch("main.get_base_url")
    def test_build_assign_payload_create_org_assignment(self, mock_get_base_url, mock_handle_request):
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
                            "display": "Jenn"
                        }
                    }
                }
            ]
        }
        string_response = json.dumps(mock_response_data)
        mock_response = (string_response, 200)
        mock_handle_request.return_value = mock_response

        resource_list = [
            ["f5d49ba0-50d7-4491-bd6c-62e429707a03", "Jenn", "98199caa-4455-4b2f-a5cf-cb9c89b6bbdc", "New Org"]
        ]
        payload = build_assign_payload(resource_list, "PractitionerRole")
        payload_obj = json.loads(payload)

        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 1)

        self.assertEqual(
            payload_obj["entry"][0]["resource"]["practitioner"],
            mock_response_data["entry"][0]["resource"]["practitioner"])
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["organization"]["reference"],
            "Organization/98199caa-4455-4b2f-a5cf-cb9c89b6bbdc")
        self.assertEqual(payload_obj["entry"][0]["resource"]["organization"]["display"], "New Org")

    @patch("main.handle_request")
    @patch("main.get_base_url")
    def test_build_assign_payload_create_new_practitioner_role(self, mock_get_base_url, mock_handle_request):
        mock_get_base_url.return_value = "https://example.smartregister.org/fhir"
        mock_response_data = {
            "resourceType": "Bundle",
            "total": 0
        }
        string_response = json.dumps(mock_response_data)
        mock_response = (string_response, 200)
        mock_handle_request.return_value = mock_response

        resource_list = [
            ["f5d49ba0-50d7-4491-bd6c-62e429707a03", "Jenn", "98199caa-4455-4b2f-a5cf-cb9c89b6bbdc", "New Org"]
        ]
        payload = build_assign_payload(resource_list, "PractitionerRole")
        payload_obj = json.loads(payload)

        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj["entry"]), 1)

        self.assertEqual(
            payload_obj["entry"][0]["resource"]["practitioner"]["reference"],
            "Practitioner/f5d49ba0-50d7-4491-bd6c-62e429707a03")
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["practitioner"]["display"], "Jenn")
        self.assertEqual(
            payload_obj["entry"][0]["resource"]["organization"]["reference"],
            "Organization/98199caa-4455-4b2f-a5cf-cb9c89b6bbdc")
        self.assertEqual(payload_obj["entry"][0]["resource"]["organization"]["display"], "New Org")

if __name__ == "__main__":
    unittest.main()
