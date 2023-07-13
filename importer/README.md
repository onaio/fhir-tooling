## Resource Importer

This script takes in a csv file with a list of resources, builds the payloads 
and then posts them to the API for creation

To run script
1. Create virtualenv
2. Install requirements.txt - `pip install -r requirements.txt`
3. Create a `config.py` file. The `sample_config.py` is an example  of what this should look like. Populate it with the right credentials
4. Run script - `python3 main.py --csv_file csv/locations.csv --resource_type locations`

See example csvs in the csv folder