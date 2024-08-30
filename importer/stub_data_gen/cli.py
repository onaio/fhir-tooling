import click
import os

from utils import default_out_dir
from utils import write_resource, read_full_csv
from stub_gen import generate_users, generate_products, generate_care_teams, generate_locations, \
    generate_org_to_locations, generate_users_orgs, generate_inventory, generate_organizations


@click.group()
def cli():
    pass


@click.group(invoke_without_command=True)
@click.option('--out-dir', default=default_out_dir, help='Output directory for generated files')
@click.option('--count', default=20, help='Count of records to generate')
@click.pass_context
def generate(ctx, out_dir, count):
    ctx.obj = {'OUT_DIR': out_dir, 'COUNT': count}
    if not os.path.exists(out_dir):
        os.makedirs(out_dir)

    if ctx.invoked_subcommand is None:
        # import ipdb; ipdb.set_trace()
        # Call each subcommand's logic
        user_data = generate_users(count)
        write_resource("users", out_dir, user_data)

        location_data = generate_locations(count)
        write_resource("locations", out_dir, location_data)

        products_data = generate_products(count)
        write_resource("products", out_dir, products_data)

        organization_data = generate_organizations(count)
        write_resource("organizations", out_dir, organization_data)

        care_team_data = generate_care_teams(organization_data[1], user_data[1], count)
        write_resource("careteams", out_dir, care_team_data)

        inventory_data = generate_inventory(location_data[1], products_data[1], count)
        write_resource("inventories", out_dir, inventory_data)

        user_orgs_data = generate_users_orgs(user_data[1], organization_data[1], count)
        write_resource("users_orgs", out_dir, user_orgs_data)

        orgs_locs_data = generate_org_to_locations(organization_data[1], location_data[1], count)
        write_resource("orgs_locs", out_dir, orgs_locs_data)

        click.echo(f"All data types generated and saved in {out_dir}")


@click.command()
@click.pass_context
def users(ctx):
    out_dir = ctx.obj['OUT_DIR']
    count = ctx.obj["COUNT"]
    data = generate_users(count)
    write_resource("users", out_dir, data)
    click.echo(f"User data generated and saved in {out_dir}")


@click.command()
@click.option('--orgs-csv', help='Organizations CSV file')
@click.option('--users-csv', help='Locations CSV file')
@click.pass_context
def careteams(ctx, orgs_csv, users_csv):
    out_dir = ctx.obj['OUT_DIR']
    count = ctx.obj["COUNT"]
    organizations_data = []
    locations_data = []
    if orgs_csv:
        [_, organizations_data] = read_full_csv(orgs_csv)
    if users_csv:
        [_, locations_data] = read_full_csv(users_csv)
    data = generate_care_teams( organizations_data, locations_data, count)
    write_resource("careteams", out_dir, data)
    click.echo(f"Care team data generated and saved in {out_dir}")


@click.command()
@click.pass_context
def locations(ctx):
    out_dir = ctx.obj['OUT_DIR']
    count = ctx.obj["COUNT"]
    data = generate_locations(count)
    write_resource("locations", out_dir, data)
    click.echo(f"Location data generated and saved in {out_dir}")


@click.command()
@click.option('--orgs-csv', help='Organizations CSV file', required=True)
@click.option('--locs-csv', help='Locations CSV file', required=True)
@click.pass_context
def orgs_locs(ctx, orgs_csv, locs_csv):
    out_dir = ctx.obj['OUT_DIR']
    count = ctx.obj['COUNT']
    # Idea: assume orgs and locations files exist in the out folder then we can use them as source
    # if not out_dir.startswith('./out') and (not orgs_csv or not locs_csv):
    #     click.echo("Error: --orgs-csv and --locs-csv are required when --out-dir is not './out'")
    #     return
    [_, organizations] = read_full_csv(orgs_csv)
    [_, locations] = read_full_csv(locs_csv)

    data = generate_org_to_locations(organizations, locations, count)
    write_resource("orgs_locs", out_dir, data)
    click.echo(f"Organization and location data generated and saved in {out_dir}")


@click.command()
@click.option('--orgs-csv', help='Organizations CSV file', required=True)
@click.option('--users-csv', help='users CSV file', required=True)
@click.pass_context
def users_orgs(ctx, orgs_csv, users_csv):
    out_dir = ctx.obj['OUT_DIR']
    count = ctx.obj['COUNT']
    [_, organizations] = read_full_csv(orgs_csv)
    [_, users] = read_full_csv(users_csv)
    data = generate_users_orgs(users, organizations, count)
    write_resource("users_orgs", out_dir, data)
    click.echo(f"User and organization data generated and saved in {out_dir}")


@click.command()
@click.pass_context
def products(ctx):
    out_dir = ctx.obj['OUT_DIR']
    count = ctx.obj["COUNT"]
    data = generate_products(count)
    write_resource("products", out_dir, data)
    click.echo(f"Product data generated and saved in {out_dir}")


@click.command()
@click.option('--locs-csv', help='Organizations CSV file', required=True)
@click.option('--prods-csv', help='users CSV file', required=True)
@click.pass_context
def inventories(ctx, locs_csv, prods_csv):
    out_dir = ctx.obj['OUT_DIR']
    count = ctx.obj["COUNT"]
    [_, locations] = read_full_csv(locs_csv)
    [_, products] = read_full_csv(prods_csv)
    data = generate_inventory(locations, products, count)
    write_resource("inventories", out_dir, data)
    click.echo(f"Inventory data generated and saved in {out_dir}")


@click.command()
@click.pass_context
def organizations(ctx):
    out_dir = ctx.obj['OUT_DIR']
    count = ctx.obj['COUNT']
    data = generate_organizations(count)
    write_resource("organizations", out_dir, data)
    click.echo(f"Organization data generated and saved in {out_dir}")


cli.add_command(generate)

generate.add_command(users)
generate.add_command(careteams)
generate.add_command(locations)
generate.add_command(orgs_locs)
generate.add_command(users_orgs)
generate.add_command(products)
generate.add_command(inventories)
generate.add_command(organizations)

if __name__ == '__main__':
    cli()