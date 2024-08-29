import csv
import os
from typing import Literal

default_out_dir = os.path.normpath(os.path.join(os.path.realpath(__file__), "../../out"))

def write_csv(csv_path, data):
    [header, rows] = data
    with open(csv_path, mode='w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(header)
        writer.writerows(rows)
    print(f"CSV file '{csv_path}' with {len(data[1])} rows has been created.")

def read_full_csv(csv_path):
    with open(csv_path, "r") as file:
        reader = csv.reader(file)
        header = reader.__next__()
        rows = [row for row in reader]
        return [rows, header]

ResourceTypes = Literal["users", "careteams", "locations", "orgs_locs", "users_orgs", "products", "inventories", "organizations"]
def write_resource(resource_type: ResourceTypes, out_dir, data):
    if resource_type == "users":
        csv_path = os.path.join(out_dir, 'users.csv')
    elif resource_type == "careteams":
        csv_path = os.path.join(out_dir, 'careteams.csv')
    elif resource_type == "locations":
        csv_path = os.path.join(out_dir, 'locations.csv')
    elif resource_type == "orgs_locs":
        csv_path = os.path.join(out_dir, 'orgs_locs.csv')
    elif resource_type == "users_orgs":
        csv_path = os.path.join(out_dir, 'users_orgs.csv')
    elif resource_type == "products":
        csv_path = os.path.join(out_dir, 'products.csv')
    elif resource_type == "inventories":
        csv_path = os.path.join(out_dir, 'inventories.csv')
    elif resource_type == "organizations":
        csv_path = os.path.join(out_dir, 'organizations.csv')
    else:
        raise TypeError("resource type is unknown")
    write_csv(csv_path, data)
