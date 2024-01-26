import json
import unittest
from unittest.mock import patch
from main import handle_request, build_payload


class TestMainFunctions(unittest.TestCase):

    @patch('main.config')
    @patch('main.requests.post')
    @patch('main.OAuth2Session')
    def test_handle_request_successful(self, oauth_session, mock_post,
                                       mock_config):
        oauth_session.fetch_token.return_value = "token"
        mock_config.client_id = 1
        mock_config.client_secret = "client_secret"
        mock_config.username = "username"
        mock_config.password = "password"
        mock_config.access_token_url = "access_token_url"

        mock_response = mock_post.return_value
        mock_response.status_code = 200
        mock_response.text = "Success"
        result = handle_request("POST", "payload", "url")
        self.assertEqual(result.status_code, 200)

    def test_build_payload(self):
        resource_ids = ["1"]
        resource_type = "ResourceType"
        payload = build_payload(resource_ids, resource_type)

        self.assertTrue(
            '{"resourceType": "Bundle", "type": "transaction", "entry": ['
            in payload)
        payload = json.loads(payload)
        self.assertDictEqual(payload['entry'][0]['request'],
                             {'method': 'DELETE', 'url': 'ResourceType/1'})


if __name__ == '__main__':
    unittest.main()
