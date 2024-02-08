import json
import unittest
from jsonschema import validate
from main import read_csv, build_payload, build_org_affiliation, extract_matches


class TestMain(unittest.TestCase):
    def test_read_csv(self):
        csv_file = "csv/users.csv"
        records = read_csv(csv_file)
        self.assertIsInstance(records, list)
        self.assertEqual(len(records), 3)

    def test_build_payload_organizations(self):
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

    def test_build_payload_locations(self):
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

    def test_build_payload_care_teams(self):
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


if __name__ == "__main__":
    unittest.main()
