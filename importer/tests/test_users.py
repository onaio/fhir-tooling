import json
import unittest

from jsonschema import validate
from mock import patch

from importer.users import (confirm_keycloak_user, confirm_practitioner,
                            create_user, create_user_resources)


class TestUsers(unittest.TestCase):
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

    @patch("importer.users.logging")
    @patch("importer.users.handle_request")
    @patch("importer.users.get_keycloak_url")
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

    @patch("importer.users.handle_request")
    @patch("importer.users.get_keycloak_url")
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
    @patch("importer.users.logging")
    @patch("importer.users.handle_request")
    @patch("importer.users.get_keycloak_url")
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
    @patch("importer.users.handle_request")
    @patch("importer.users.get_base_url")
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

    @patch("importer.users.logging")
    @patch("importer.users.handle_request")
    @patch("importer.users.get_base_url")
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
