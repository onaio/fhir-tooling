import csv
import uuid
from faker import Faker

# Initialize Faker
fake = Faker()

# Template data (header and sample row)
header = [
    "orgName", "orgActive", "method", "orgId", "identifier"
]

# Function to generate random row data
def generate_random_row():
    org_name = fake.name()
    active = fake.random_element(["true", "false", ""])
    method = "create"
    id = fake.uuid4()
    identifier = fake.uuid4()

    return [org_name, active, method, id, identifier]

# Generate 100 rows of data
rows = []
for _ in range(100):
    rows.append(generate_random_row())

# Write to CSV file
filename = f"localCsvs/orgs.csv"
with open(filename, mode='w', newline='') as file:
    writer = csv.writer(file)
    writer.writerow(header)
    writer.writerows(rows)

print(f"CSV file '{filename}' with 100 rows has been created.")
