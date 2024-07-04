import csv
import uuid
from faker import Faker

# Initialize Faker
fake = Faker()

# Template data (header and sample row)
header = [
    "firstName", "lastName", "username", "email", "userId",
    "userType", "enableUser", "keycloakGroupId", "keycloakGroupName",
    "appId", "password"
]

# Function to generate random row data
def generate_random_row():
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
    return [f_name, l_name, u_name, email, user_id, user_type, enable_user, group_ids, group_names, app_id, password]

# Generate 100 rows of data
rows = []
for _ in range(100):
    rows.append(generate_random_row())

# Write to CSV file
filename = f"./users.csv"
with open(filename, mode='w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(header)
    writer.writerows(rows)

print(f"CSV file '{filename}' with 100 rows has been created.")
