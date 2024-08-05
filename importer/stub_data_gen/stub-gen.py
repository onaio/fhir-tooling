import csv
import uuid
from faker import Faker

# Initialize Faker
fake = Faker()

def get_users_csv_data():
    header = [
        "firstName", "lastName", "username", "email", "userId",
        "userType", "enableUser", "keycloakGroupId", "keycloakGroupName",
        "appId", "password"
    ]
    # Function to generate random row data
    def generate_user_row():
        f_name = fake.first_name()
        l_name = fake.last_name()
        u_name = fake.user_name()
        email = fake.email()
        user_id = ""
        user_type = fake.random_element(["Practitioner", "Supervisor", ""])
        enable_user = fake.random_element(["true", "false", ""])
        group_ids = ""
        group_names = ""
        app_id = "quest"
        password = fake.password()
        return [f_name, l_name, u_name, email, user_id, user_type, enable_user, group_ids, group_names, app_id,
                password]

    rows = []
    for _ in range(100):
        rows.append(generate_user_row())
    return [header, rows]

def get_orgs_csv_data():
    header = [
        "orgName", "orgActive", "method", "orgId", "identifier"
    ]

    # Function to generate random row data
    def generate_row():
        org_name = fake.name()
        active = fake.random_element(["true", "false"])
        method = "create"
        id = fake.uuid4()
        identifier = fake.uuid4()

        return [org_name, active, method, id, identifier]

    # Generate 100 rows of data
    rows = []
    for _ in range(100):
        rows.append(generate_row())
    return [header, rows]

def get_locations_csv_data():
    header = [
        "locationName", "locationStatus", "method", "locationId", "locationParentName", "locationParentId",
        "locationType", "locationTypeCode", "locationAdminLevel", "locationPhysicalType", "locationPhysicalTypeCode",
        "longitude", "latitude"
    ]

    # Function to generate random row data
    def generate_row():
        (latitude, longitude, name, _, __) = fake.location_on_land()
        status = fake.random_element(["active", "inactive", "suspended"])
        method = "create"
        id = fake.uuid4()
        locType = "jdn"
        locTypeCode = "jurisdiction"
        locAdminLevel = "1"
        locPhysicalType = "jdn"
        locPhysicalTypeCode = "jurisdiction"
        identifier = fake.uuid4()

        return [name,status, method, id, "", "", locType, locTypeCode, locAdminLevel, locPhysicalType,
                locPhysicalTypeCode, "", ""]

    # Generate 100 rows of data
    rows = []
    for _ in range(100):
        rows.append(generate_row())
    return [header, rows]

def get_org_to_location_csv_data(locData, orgData):
    header = [
        "orgName", "orgId", "locationName", "locationId",
    ]

    # Function to generate random row data
    def generate_row():
        loc = fake.random_element(locData)
        org = fake.random_element(orgData)

        return [org[0], org[3], loc[0], loc[3]]

    # Generate 100 rows of data
    rows = []
    for _ in range(100):
        rows.append(generate_row())
    return [header, rows]


def generateCareTeam(organizations, practitioners):
    header = [
        "name", "status", "method", "id", "identifier", "organizations",
        "participants"
    ]

    # Function to generate random row data
    def generate_row():
        name = fake.name()
        status = fake.random_element(["active", "inactive"])
        method = "create"
        id = fake.uuid4()
        identifier = fake.uuid4()


        return [name, status, method, id, "", "", locType, locTypeCode, locAdminLevel, locPhysicalType,
                locPhysicalTypeCode, "", ""]

    # Generate 100 rows of data
    rows = []
    for _ in range(100):
        rows.append(generate_row())
    return [header, rows]


def generate_products():
    pass

def generate_inventory():
    pass


def generate_users_orgs():
    pass

def write_csv(csv_path, data):
    [header, rows] = data
    with open(csv_path, mode='w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(header)
        writer.writerows(rows)
    print(f"CSV file '{csv_path}' with 100 rows has been created.")

# def main():
#     orgs_data = get_orgs_csv_data()
#     locs_data = get_locations_csv_data()
#     org_to_loc_data = get_org_to_location_csv_data(orgs_data, locs_data)
#     write_pairing = [(orgs_data, "./localCsvs/organizations.csv"), (locs_data, "./localCsvs/locations.csv"),
#                      (org_to_loc_data, "./localCsvs/orgToLocation.csv")]
#     for write in write_pairing:
#         (data, path) = write
#         write_csv(path, data)
#
# main()

