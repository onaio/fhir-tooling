import json
import unittest
import uuid

import pytest

from main import (
    read_csv,
    build_payload,
    build_org_affiliation,
    extract_matches,
    create_user_resources
)


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
            "organizations",
            resource_list,
            "json_payloads/organizations_payload.json"
        )
        payload_obj = json.loads(payload)
        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj['entry']), 3)

    def test_build_org_affiliation(self):
        csv_file = "csv/organizations/organization_locations.csv"
        resource_list = read_csv(csv_file)
        resources = extract_matches(resource_list)
        payload = build_org_affiliation(resources, resource_list)
        payload_obj = json.loads(payload)
        self.assertIsInstance(payload_obj, dict)
        self.assertEqual(payload_obj["resourceType"], "Bundle")
        self.assertEqual(len(payload_obj['entry']), 2)

    def test_uuid_generated_in_creating_user_resources_is_unique_and_repeatable(self):
        users = [['Jane', 'Doe', 'Janey', 'jdoe@example.com', '', 'Practitioner', 'TRUE',
                  'a715b562-27f2-432a-b1ba-e57db35e0f93', 'test', 'demo', 'pa$$word'],
                 ['John', 'Doe', 'Janey', 'jodoe@example.com', '', 'Practitioner', 'TRUE',
                  'a715b562-27f2-432a-b1ba-e57db35e0f93', 'test', 'demo', 'pa$$word'],
                 ['Janice', 'Doe', 'Jenn', 'jendoe@example.com', '99d54e3c-c26f-4500-a7f9-3f4cb788673f', 'Supervisor',
                  'TRUE', 'a715b562-27f2-432a-b1ba-e57db35e0f93', 'test', 'demo', 'pa$$word']]

        users_uuids = {}
        for user_id, user in enumerate(users):
            payload = create_user_resources(user[4], user)
            payload_obj = json.loads(payload)
            practitioner_uuid = payload_obj["entry"][0]["resource"]["id"]
            group_uuid = payload_obj["entry"][1]["resource"]["id"]
            practitioner_role_uuid = payload_obj["entry"][2]["resource"]["id"]
            users_uuids[user_id] = [practitioner_uuid, group_uuid, practitioner_role_uuid]

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
        resources = [['location1', 'active', 'create', '1', '', 'test location-1', '18fcbc2e-4240-4a84-a270-7a444523d7b6', 'jurisdiction', 'jurisdiction'],
                     ['location2', 'active', 'create', '1', '', 'test location-1', '18fcbc2e-4240-4a84-a270-7a444523d7b6', 'building', 'building'],
                     ['location2', 'active', 'create', '1', '', 'test location-1', '18fcbc2e-4240-4a84-a270-7a444523d7b6', 'jurisdiction', 'jurisdiction']]

        payload = build_payload("locations", resources, "json_payloads/locations_payload.json")
        payload_obj = json.loads(payload)
        location1 = payload_obj["entry"][0]["resource"]["id"]
        location2 = payload_obj["entry"][1]["resource"]["id"]
        location3 = payload_obj["entry"][2]["resource"]["id"]

        self.assertNotEqual(location1, location2)
        self.assertEqual(location2, location3)

    def test_uuid_generated_in_build_org_affiliation_is_unique_and_repeatable(self):
        resource_list = [['HealthyU', 'a9137781-eb94-4d5f-8d39-471a92aec9f2', 'World', '138396'],
                         ['HealthyU', 'a9137781-eb94-4d5f-8d39-471a92aec9f2', 'Kenya', '54876'],
                         ['HealthyU', 'a9137781-eb94-4d5f-8d39-471a92aec9f2', 'Nairobi', '105167'],
                         ['One Org', '8342dd77-aecd-48ab-826b-75c7c33039ed', 'World', '138396']]

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


if __name__ == "__main__":
    unittest.main()
