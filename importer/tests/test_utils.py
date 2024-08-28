import json
import pathlib
import unittest

from mock import patch

from importer.utils import (export_resources_to_csv, read_csv,
                            read_file_in_chunks, split_chunk, write_csv)

dir_path = str(pathlib.Path(__file__).parent.resolve())


class TestUtils(unittest.TestCase):
    def test_read_csv(self):
        csv_file = dir_path + "/../csv/users.csv"
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

    @patch("importer.utils.write_csv")
    @patch("importer.utils.handle_request")
    @patch("importer.utils.get_base_url")
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

    @patch("importer.utils.set_resource_list")
    def test_split_chunk_direct_sync_first_chunk_less_than_size(
        self, mock_set_resource_list
    ):
        chunk = '[{"id": "10", "resourceType": "Patient"}'
        next_left_over = split_chunk(chunk, "", 50, {}, "direct")
        chunk_list = '[{"id": "10", "resourceType": "Patient"}]'
        self.assertEqual(next_left_over, "-")
        mock_set_resource_list.assert_called_once_with(chunk_list)

    @patch("importer.utils.set_resource_list")
    def test_split_chunk_direct_sync_middle_chunk_less_than_size(
        self, mock_set_resource_list
    ):
        chunk = ' "resourceType": "Patient"}'
        left_over_chunk = '{"id": "10",'
        next_left_over = split_chunk(chunk, left_over_chunk, 50, {}, "direct")
        chunk_list = '[{"id": "10", "resourceType": "Patient"}]'
        self.assertEqual(next_left_over, "-")
        mock_set_resource_list.assert_called_once_with(chunk_list)

    @patch("importer.utils.set_resource_list")
    def test_split_chunk_direct_sync_last_chunk_less_than_size(
        self, mock_set_resource_list
    ):
        left_over_chunk = '{"id": "10", "resourceType": "Patient"}]'
        next_left_over = split_chunk("", left_over_chunk, 50, {}, "direct")
        chunk_list = '[{"id": "10", "resourceType": "Patient"}]'
        self.assertEqual(next_left_over, "-")
        mock_set_resource_list.assert_called_once_with(chunk_list)

    @patch("importer.utils.set_resource_list")
    def test_split_chunk_direct_sync_first_chunk_greater_than_size(
        self, mock_set_resource_list
    ):
        chunk = '[{"id": "10", "resourceType": "Patient"},{"id": "11", "resourceType":'
        next_left_over = split_chunk(chunk, "", 40, {}, "direct")
        chunk_list = '[{"id": "10", "resourceType": "Patient"}]'
        self.assertEqual(next_left_over, '{"id": "11", "resourceType":')
        mock_set_resource_list.assert_called_once_with(chunk_list)

    @patch("importer.utils.set_resource_list")
    def test_split_chunk_direct_sync_middle_chunk_greater_than_size(
        self, mock_set_resource_list
    ):
        chunk = ': "Task"},{"id": "10", "resourceType": "Patient"},{"id": "11", "resourceType":'
        left_over_chunk = '{"id": "09", "resourceType"'
        next_left_over = split_chunk(chunk, left_over_chunk, 80, {}, "direct")
        chunk_list = '[{"id": "09", "resourceType": "Task"},{"id": "10", "resourceType": "Patient"}]'
        self.assertEqual(next_left_over, '{"id": "11", "resourceType":')
        mock_set_resource_list.assert_called_once_with(chunk_list)

    @patch("importer.utils.set_resource_list")
    def test_split_chunk_direct_sync_last_chunk_greater_than_size(
        self, mock_set_resource_list
    ):
        left_over_chunk = '{"id": "10", "resourceType": "Patient"},{"id": "11", "resourceType": "Task"}]'
        next_left_over = split_chunk("", left_over_chunk, 43, {}, "direct")
        chunk_list = '[{"id": "10", "resourceType": "Patient"},{"id": "11", "resourceType": "Task"}]'
        self.assertEqual(next_left_over, "")
        mock_set_resource_list.assert_called_once_with(chunk_list)

    @patch("importer.utils.set_resource_list")
    @patch("importer.utils.build_resource_type_map")
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
        json_file = dir_path + "/json/sample.json"
        mapping = read_file_in_chunks(json_file, 300, "sort")
        mapped_resources = {
            "Patient": [0],
            "Practitioner": [1, 5],
            "Location": [2, 4],
            "Observation": [3],
        }
        self.assertIsInstance(mapping, dict)
        self.assertEqual(mapping, mapped_resources)
