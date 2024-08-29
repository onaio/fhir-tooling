import json
from faker import Faker
import math
import os

from fixtures import unicef_sections, inventory_donors, location_physical_types, service_point_types

fake = Faker()


def generate_users(count: int):
    header = [
        "firstName", "lastName", "username", "email", "practitionerId",
        "userType", "enableUser", "keycloakGroupId", "keycloakGroupName",
        "appId", "password"
    ]

    # Function to generate random row data
    def generate_user_row(index: int):
        f_name = fake.first_name()
        l_name = fake.last_name()
        u_name = fake.user_name()
        email = fake.email()
        user_id = fake.uuid4()
        user_type = fake.random_element(["Practitioner", "Supervisor", ""])
        enable_user = fake.random_element(["true", "false"])
        group_ids = ""
        group_names = ""
        app_id = fake.random_element(["quest", "core", "anc", ""])
        password = fake.password()
        return [f_name, l_name, u_name, email, user_id, user_type, enable_user, group_ids, group_names, app_id,
                password]

    rows = []
    for cursor in range(1, count+1):
        rows.append(generate_user_row(cursor))
    return [header, rows]


def generate_organizations(count: int):
    header = [
        "orgName", "orgActive", "method", "orgId", "identifier"
    ]

    # Function to generate random row data
    def generate_row(idx: int):
        org_name = fake.company()
        active = fake.random_element(["true", "false"])
        method = "create"
        org_id = fake.uuid4()
        identifier = fake.uuid4()

        return [org_name, active, method, org_id, identifier]

    rows = []
    for cursor in range(1, count+1):
        rows.append(generate_row(cursor))
    return [header, rows]


def generate_locations(count: int):
    header = [
        "locationName", "locationStatus", "method", "locationId", "locationParentName", "locationParentId",
        "locationType", "locationTypeCode", "locationAdminLevel", "locationPhysicalType", "locationPhysicalTypeCode",
        "longitude", "latitude"
    ]

    # Function to generate random row data
    def generate_row(cursor: int, generated_locations):
        name = fake.city()
        parent_location = None
        if len(generated_locations):
            parent_location = fake.random_element(generated_locations)
        physical_type_code = fake.random_element(location_physical_types)
        type_code = fake.random_element(service_point_types)
        status = fake.random_element(["active", "inactive", "suspended"])
        method = "create"
        id = fake.uuid4()
        loc_type = type_code.get("display")
        loc_type_code = type_code.get("code")
        loc_admin_level = 1
        loc_physical_type = physical_type_code.get("display")
        loc_physical_type_code = physical_type_code.get("code")
        parent_loc_name = ""
        parent_loc_id = ""
        latitude = ""
        longitude = ""
        if parent_location:
            parent_admin_level = int(parent_location[8])
            parent_loc_name = parent_location[0]
            parent_loc_id = parent_location[3]
            loc_admin_level = str(parent_admin_level + 1)
        if int(loc_admin_level) > 3:
            latitude = fake.geo_coordinate()
            longitude = fake.geo_coordinate()
        return [name, status, method, id, parent_loc_name, parent_loc_id, loc_type, loc_type_code, loc_admin_level, loc_physical_type,
                loc_physical_type_code, longitude, latitude]

    rows = []
    for idx in range(1, count + 1):
        rows.append(generate_row(idx, rows))
    return [header, rows]


def generate_org_to_locations(organizations, locations, count: int):
    header = [
        "orgName", "orgId", "locationName", "locationId",
    ]

    # Function to generate random row data
    def generate_row(_: int):
        loc = fake.random_element(locations)
        org = fake.random_element(organizations)

        return [org[0], org[3], loc[0], loc[3]]

    rows = []
    for cursor in range(1, count+1):
        rows.append(generate_row(cursor))
    return [header, rows]


def generate_care_teams(organizations, practitioner_users, count: int):
    header = [
        "name", "status", "method", "id", "identifier", "organizations",
        "participants"
    ]

    # Function to generate random row data
    def generate_row(_):
        name = fake.name()
        status = fake.random_element(["active", "inactive"])
        method = "create"
        id = fake.uuid4()
        identifier = fake.uuid4()
        try:
            random_orgs = fake.random_sample(organizations, math.floor(fake.random.random() * 5))
            random_parts = fake.random_sample(practitioner_users, math.floor(fake.random.random() * 5))
        except IndexError:
            random_parts = []
            random_orgs = []

        assigned_orgs = "|".join([f"{org[3]}:{org[0]}" for org in random_orgs])
        assigned_parts = "|".join([f"{user[4]}:{user[0]} {user[1]}" for user in random_parts])


        return [name, status, method, id, identifier, assigned_orgs, assigned_parts]

    rows = []
    for cursor in range(1, count + 1):
        rows.append(generate_row(cursor))
    return [header, rows]

products_samples_lookup = os.path.normpath(os.path.join(os.path.realpath(__file__), "../product_samples.json"))
with open(products_samples_lookup, "r") as file:

    json_data = file.read()
    productSample = json.loads(json_data)


def generate_products(count):
    header = [
        "name", "active", "method", "id", "materialNumber", "previousId", "isAttractiveItem", "availability", "condition"
        , "appropriateUsage", "accountabilityPeriod", "imageSourceUrl"
    ]

    rows = []
    for i in range(1, count + 1):
        template = fake.random_element(productSample)
        identifier = fake.uuid4()
        name = template.get("productName")
        material_number = f"SKU00{i}"
        name = f"{material_number}-{name}"
        attractive_item = template.get("isAttractiveItem")
        availability = template.get("availability")
        condition = template.get("condition")
        appropriate_usage = template.get("appropriateUsage")
        accountability = template.get("accountabilityPeriod")
        image_source_url = ""
        status = fake.random_element(["true", "false"])
        rows.append(
            [name, status, "create", identifier, material_number, "", attractive_item, availability, condition, appropriate_usage,
             accountability, image_source_url])

    return [header, rows]


def generate_inventory(locations, products, count: int):
    header = [
        "name", "active", "method", "id", "poNumber", "serialNumber", "usualId", "actual", "productId", "deliveryDate"
        , "accountabilityDate", "quantity", "unicefSection", "donor", "location"
    ]
    rows = []
    for i in range(1, count + 1):
        location_record = fake.random_element(locations)
        product_record = fake.random_element(products)
        location_name = location_record[0]
        product_name = product_record[0]
        product_name = f"{location_name} - {product_name}"
        status = fake.random_element(["active", "inactive"])
        attractive_item = "true" if product_record[6] else "false"
        inventory_id = fake.uuid4()
        serial_number = ""
        po_umber = f"PONUM{i}"
        if attractive_item:
            serial_number = f"SKNO{i}"
        delivery_date = ""
        product_id = product_record[3]
        quantity = math.ceil(fake.random.random() * 50)
        unicef_section = fake.random_element(unicef_sections)
        donor = fake.random_element(inventory_donors).get("")
        location = location_record[3]

        to_append = [product_name, status, "create", inventory_id, po_umber, serial_number, "true", product_id, delivery_date, "", quantity, unicef_section, donor, location]
        rows.append(to_append)
    return [header, rows]


def generate_users_orgs(users, organizations, count: int):
    header = [
        "username", "userId", "orgName", "orgId"
    ]
    def generate_row():
        random_org = fake.random_element(organizations)
        random_user = fake.random_element(users)
        org_id = random_org[3]
        org_name = random_org[0]
        user_id = random_user[4]
        user_name = f"{random_user[0]} {random_user[1]}"
        return [user_name, user_id, org_name, org_id, ]

    rows = []
    for cursor in range(100):
        rows.append(generate_row())
    return [header, rows]
