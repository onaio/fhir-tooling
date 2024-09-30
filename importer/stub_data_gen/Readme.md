# usage

**To generate data for all resources**

```commandline
python cli.py generate
```

**Generate data for a single resource**

```commandline
python cli.py generate users --count=100
```

**Generate single resource that requires pre-generated data like assignments**

e.g. When assigning users to organizations, provide individual csvs containing the users and organizations data

```commandline
python cli.py generate users-orgs --orgs-csv=<orgs-csv-path> --users-csv=<users-csv-path>
```

**Learn more**

```commandline
python cli.py generate --help
```