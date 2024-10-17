# usage

Ofcourse you need to prepare your python environment and install the package dependencies

```commandline
python3 -m venv myenv
source myenv/bin/activate
pip instal -r ../requirements.txt
```

**To generate data for all resources**

```commandline
python faker-cli generate
```

**Generate data for a single resource**

```commandline
python faker-cli generate users --count=100
```

**Generate single resource that requires pre-generated data like assignments**

e.g. When assigning users to organizations, provide individual csvs containing the users and organizations data

```commandline
python faker-cli generate users-orgs --orgs-csv=<orgs-csv-path> --users-csv=<users-csv-path>
```

**Learn more**

```commandline
python faker-cli generate --help
```