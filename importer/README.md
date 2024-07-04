# Setup Keycloak Roles

This script is used to setup keycloak roles and groups. It takes in a csv file with the following columns:

- **role**: The actual names of the roles you would like to create
- **composite**: A boolean value that tells if the role has composite roles or not
- **associated_roles**: Roles to be created/added to the main role as associated roles

### Options

- `setup` : (Required) This needs to be set to "roles" in order to initiate the setup process
- `csv_file` : (Required) The csv file with the list of roles
- `group` : (Not required) This is the actual group name. If not passed then the roles will just be created but not assigned to any group
- `roles_max` : (Not required) This is the maximum number of roles to pull from the api. The default is set to 500. If the number of roles in your setup is more than this you will need to change this value


### To run script
1. Create virtualenv
2. Install requirements.txt - `pip install -r requirements.txt`
3. Create a `config/config.py` file. The `config/sample_config.py` is an example  of what this should look like. Populate it with the right credentials, you can either provide an access token or client credentials. Ensure that the user whose details you provide in this config file has the necessary permissions/privilleges.
4. Run script - `python3 main.py --setup roles --csv_file csv/setup/roles.csv --group Supervisor`
5. If you are running the script without `https` setup e.g locally or a server without https setup, you will need to set the `OAUTHLIB_INSECURE_TRANSPORT` environment variable to 1. For example `export OAUTHLIB_INSECURE_TRANSPORT=1 && python3 main.py --setup roles --csv_file csv/setup/roles.csv --group OpenSRP_Provider --log_level debug`
6. You can turn on logging by passing a `--log_level` to the command line as `info`, `debug` or `error`. For example `python3 main.py --setup roles --csv_file csv/setup/roles.csv --group Supervisor --log_level debug`


# FHIR Resource CSV Importer

This script takes in a csv file with a list of resources, builds the payloads 
and then posts them to the API for creation

### To run script
1. Create virtualenv
2. Install requirements.txt - `pip install -r requirements.txt`
3. Create a `config.py` file. The `sample_config.py` is an example  of what this should look like. Populate it with the right credentials
4. Run script - `python3 main.py --csv_file csv/locations.csv --resource_type locations`
5. You can turn on logging by passing a `--log_level` to the command line as `info`, `debug` or `error`. For example `python3 main.py --csv_file csv/locations.csv --resource_type locations --log_level info`
6. There is a progress bar that shows the read_csv and build_payload progress as it is going on
7. You can get only the response from the api after the import is done by passing `--only_response true`


See example csvs in the csv folder

## To test

To run all tests
```console
$ pytest
```
To run specific tests
```console
$ pytest path/to/test_file.py::TestClass::test_function
```

To run tests and generate a coverage report
```console
$ pytest --junitxml=coverage.html --cov=importer --cov-report=html
```
The coverage report `coverage.html` will be at the working directory

## How to use it

### 1. Create locations in bulk
- Run `python3 main.py --csv_file csv/locations/locations_min.csv --resource_type locations --log_level info`
- See example csv [here](/importer/csv/locations/locations_min.csv)
- The first two columns __name__ and __status__ is the minimum required
- If the csv file has only the required columns, (e.g. [locations_min.csv](/importer/csv/locations/locations_min.csv)) the __id__ and __method__ are set to __generating a new unique_uuid__ and a default value __create__ method respectively
- [locations_full](/importer/csv/locations/locations_full.csv) shows more options available
- The third column is the request method, can be either create or update. Default is set to create
-  The fourth column is the id, which is required when updating
- The fifth and sixth columns are parentName and parentID,respectively 
- The seventh and eighth columns are the location's type and typeCode, respectively
- The ninth column is the administrative level, that shows the hierarchical level of the location. Root location would have a `level 0` and all child locations will have a level `parent_admin_level + 1`
- The tenth and eleventh columns are the location's physicalType and physicalTypeCode, respectively

### 2. Create users in bulk
- Run `python3 main.py --csv_file csv/users.csv --resource_type users --log_level info`
- See example csv [here](/importer/csv/users.csv)
- First four columns are firstName, lastName, Username and email. Username and email need to be unique
- The fifth column `id` is optional. If populated with a uuid, it will be used as the Practitioner uuid when creating the Practitioner resource. If left empty, a random uuid will be generated
- The sixth column is the `userType`, this needs to be set to either `Practitioner` or `Supervisor`
- The seventh column is `enableUser` which defaults to True if not set
- The eighth and ninth column are details about the users Keycloak Group and are required for proper assignment
- The last two columns are the `ApplicationID` and `password`

### 3. Create organizations in bulk
- Run `python3 main.py --csv_file csv/organizations/organizations_min.csv --resource_type organizations --log_level info`
- See example csv [here](/importer/csv/organizations/organizations_min.csv)
- The first  column __name__ is the only one required
- If the csv file has only the required column, (e.g. [organizations_min.csv](/importer/csv/organizations/organizations_min.csv)) the __id__ , __active__, and __method__ are set to __generating a new unique_uuid__ and the default values __create__ and  __true__ respectively
- [organizations_full](/importer/csv/organizations/organizations_full.csv) shows more options available
- The third column is the request method, can be either create or update. Default is set to create
- The fourth column is the id, which is required when updating
- The fifth columns in the identifier, in some cases this is different from the id

### 4. Create care teams in bulk
- Run `python3 main.py --csv_file csv/careteams/careteam_full.csv --resource_type careTeams --log_level info`
- See example csv [here](/importer/csv/careteams/careteam_full.csv)
- The first  column __name__ is the only one required
- The third column is the request method, can be either create or update. Default is set to create
- The fourth column is the id, which is required when updating
- The fifth columns is the identifier, in some cases this is different from the id
- The sixth column is the organizations. This is only useful when you want to assign organizations when creating and updating careTeams. The format expected is a string like `orgId1:orgName1|orgId2:orgName2|orgId3:orgNam3`
- The seventh column is the participants. This is only useful when you want to assign users when creating and updating careTeams. The format expected is a string like `userId1:fullName1|userId2:fullName2|userId3:fullName3`


### 5. Assign locations to parent locations
- Run `python3 main.py --csv_file csv/locations/locations_full.csv --resource_type locations --log_level info`
- See example csv [here](/importer/csv/locations/locations_full.csv)
- Adding the last two columns __parentID__ and __parentName__ will ensure the locations are assigned the right parent both during creation or updating

### 6. Assign organizations to locations
- Run `python3 main.py --csv_file csv/organizations/organizations_locations.csv --assign organizations-Locations --log_level info`
- See example csv [here](/importer/csv/organizations/organizations_locations.csv)

### 7. Assign users to organizations
- Run `python3 main.py --csv_file csv/practitioners/users_organizations.csv --assign users-organizations --log_level info`
- See example [here](/importer/csv/practitioners/users_organizations.csv)
- The first two columns are __name__ and __id__ of the practitioner, while the last two columns are the __name__ and __id__ of the organization

### 8. Delete duplicate Practitioners on HAPI
- Run `python3 main.py --csv_file csv/users.csv --setup clean_duplicates --cascade_delete true --log_level info`
- This should be used very carefully and in very special circumstances such as early stages of server setup. Avoid usage in active production environments as it will actually delete FHIR resources
- It is recommended to first run with cascade_delete set to false in order to see if there are any linked resources which will also be deleted. Also any resources that are actually deleted are only soft deleted and can be recovered
- For this to work you must provide Practitioner uuids in your users.csv file. This is what is used to determine which Practitioner to not delete
- The script will check to see if every user has a keycloak uuid that has a Practitioner uuid that matches the one provided in the csv file
- Note that if none of the Practitioner uuids match then all will be deleted
- Set `cascade_delete` to True or False if you would like to automatically delete any linked resources. If you set it to False, and there are any linked resources, then the resources will NOT be deleted

### 9. Export resources from API endpoint to CSV file
- Run `python3  main.py --export_resources True --parameter _lastUpdated --value gt2023-08-01 --limit 20 --resource_type Location --log_level info`
- `export_resources` can either be True or False, checks if it is True and exports the resources
- The `parameter` is used as a filter for the resources. The set default parameter is "_lastUpdated", other examples include, "name"
- The `value` is where you pass the actual parameter value to filter the resources. The set default value is "gt2023-01-01", other examples include, "Good Health Clinic 1"
- The `limit` is the number of resources exported at a time. The set default value is '1000'
- Specify the `resource_type` you want to export, different resource_types are exported to different csv_files
- The csv_file containing the exported resources is labelled using the current time, to know when the resources were exported for example, csv/exports/2024-02-21-12-21-export_Location.csv

### 10. Import products from openSRP 1
- Run `python3 main.py --csv_file csv/import/product.csv --setup products --log_level info`
- See example csv [here](/importer/csv/import/product.csv)
- This creates a Group resource for each product imported
- The first two columns __name__ and __active__ is the minimum required
- The last column __imageSourceUrl__ contains a url to the product image. If this source requires authentication, then you need to provide the `product_access_token` in the config file. The image is added as a binary resource and referenced in the product's Group resource

### 11. Import inventories from openSRP 1
- Run `python3 main.py --csv_file csv/import/inventory.csv --setup inventories --log_level info`
- See example csv [here](/importer/csv/import/inventory.csv)
- This creates a Group resource for each inventory imported
- The first two columns __name__ and __active__ is the minimum required
- Adding a value to the Location column will create a separate List resource (or update) that links the inventory to the provided location resource

### 12. Import JSON resources from file
- Run `python3 main.py --bulk_import True --json_file tests/fhir_sample.json --chunk_size 500000 --sync sort --resources_count 100 --log_level info`
- This takes in a file with a JSON array, reads the resources from the array in the file and posts them to the FHIR server
- `bulk_import` (Required) must be set to True
- `json_file` (Required) points to the file with the json array. The resources in the array need to be separated by a single comma (no spaces) and the **"id"** must always be the first attribute in the resource object. This is what the code uses to identify the beginning and end of resources
- `chunk_size` (Not required) is the number of characters to read from the JSON file at a time. The size of this file can potentially be very large, so we do not want to read it all at once, we read it in chunks. This number **MUST** be at least the size of the largest single resource in the array. The default is set to 1,000,000
- `sync` (Not required) defines the sync strategy. This can be either **direct** (which is the default) or **sort**
  - **Direct** will read the resources one chunk at a time, while building a payload and posting to the server before reading the next chunk. This works if you have referential integrity turned off in the FHIR server
  - **Sort** will read all the resources in the file first and sort them into different resource types. It will then build separate payloads for the different resource types and try to post them to the FHIR server in the order that the resources first appear in the JSON file. For example, if you want Patients to be synced first, then make sure that the first resource is a Patient resource
- `resources_count` (Not required) is the number of resources put in a bundle when posting the resources to the FHIR server. The default is set to 100
