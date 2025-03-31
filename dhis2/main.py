import csv
import json
import requests
from configparser import ConfigParser
from requests.auth import HTTPBasicAuth

# Read the config file to get API credentials and endpoint URL
def read_config(config_file='config.ini'):
    config = ConfigParser()
    config.read(config_file)
    endpoint_url = config.get('API', 'endpoint_url')
    username = config.get('API', 'username')
    password = config.get('API', 'password')
    return endpoint_url, username, password


# # Function to read CSV and convert to valid JSON format
# def csv_to_json(csv_file):
#     data_list = []
#     with open(csv_file, mode='r') as file:
#         csv_reader = csv.reader(file)
#         headers = next(csv_reader)  # First row as headers
#         for row in csv_reader:
#             data_list.append(dict(zip(headers, row)))
#
#     # Convert list of dictionaries to a JSON string with double quotes
#     json_data = json.dumps(data_list, indent=4)
#     return json_data

# Read CSV and format it into JSON
def csv_to_json(csv_file):
    data_list = []
    with open(csv_file, mode='r') as file:
        csv_reader = csv.DictReader(file)
        for row in csv_reader:
            data_list.append(row)
            #json_data = json.dumps(data_list, indent=4)
    return data_list

# Post data to the endpoint using Basic Auth
def post_data(endpoint_url, username, password, data_entry):
    headers = {'Content-Type': 'application/json'}
    post_payload = json.dumps(data_entry)  # Convert each dictionary to JSON before sending
    print(f"This is the request body: {post_payload}")
    response = requests.post(endpoint_url, auth=HTTPBasicAuth(username, password), headers=headers, data=post_payload)
    return response

# Main function to execute the logic
def main():
    # Read config
    endpoint_url, username, password = read_config()

    # Convert CSV to JSON
    csv_file = 'data_elements.csv'  # Path to your CSV file
    json_data = csv_to_json(csv_file)
    print(json_data)
    # Post each row to the endpoint
    for entry in json_data:
        #print(f"This is a single entry: {entry}")
        response = post_data(endpoint_url, username, password, entry)
        if response.status_code == 201:
            print(f"Success: {entry}")
        else:
            print(f"Failed to post: {entry}. \n Fail Status Code: {response.status_code}")

if __name__ == "__main__":
    main()
