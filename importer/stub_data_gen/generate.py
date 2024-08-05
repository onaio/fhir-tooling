import click
import csv
import os
import random


def generate_random_data(count, type):
    data = []
    for i in range(count):
        if type == 'users':
            data.append({
                'id': f'user_{i}',
                'name': f'User {i}',
                'email': f'user{i}@example.com'
            })
        elif type == 'careteams':
            data.append({
                'id': f'team_{i}',
                'name': f'Care Team {i}'
            })
        elif type == 'locations':
            data.append({
                'id': f'loc_{i}',
                'name': f'Location {i}',
                'address': f'{i} Main St'
            })
        elif type == 'organizations':
            data.append({
                'id': f'org_{i}',
                'name': f'Organization {i}'
            })
        elif type == 'products':
            data.append({
                'id': f'prod_{i}',
                'name': f'Product {i}',
                'price': random.uniform(10, 1000)
            })
        elif type == 'inventories':
            data.append({
                'id': f'inv_{i}',
                'product_id': f'prod_{random.randint(0, count-1)}',
                'quantity': random.randint(0, 100)
            })
    return data


def write_csv(data, filename):
    if not data:
        return
    with open(filename, 'w', newline='') as csvfile:
        writer = csv.DictWriter(csvfile, fieldnames=data[0].keys())
        writer.writeheader()
        for row in data:
            writer.writerow(row)


@click.group()
def cli():
    pass


@click.group()
@click.option('--out-dir', default='./out', help='Output directory for generated files')
@click.option('--count', default=20, help='Count of records to generate')
@click.pass_context
def generate(ctx, out_dir, count):
    ctx.obj['OUT_DIR'] = out_dir
    ctx.obj['COUNT'] = count
    if not os.path.exists(out_dir):
        os.makedirs(out_dir)

    # Call each subcommand's logic
    users(out_dir)
    # careteams(out_dir)
    # locations(out_dir)
    # orgs_locs(out_dir, None, None)
    # users_orgs(out_dir)
    # products(out_dir)
    # inventories(out_dir)
    # organizations(out_dir)

    click.echo(f"All data types generated and saved in {out_dir}")


@click.command()
@click.pass_context
def users(ctx):
    out_dir = ctx.obj['OUT_DIR']
    data = generate_random_data(25, 'users')
    write_csv(data, os.path.join(out_dir, 'users.csv'))
    click.echo(f"User data generated and saved in {out_dir}")


@click.command()
@click.pass_context
def careteams(ctx):
    out_dir = ctx.obj['OUT_DIR']
    data = generate_random_data(25, 'careteams')
    write_csv(data, os.path.join(out_dir, 'careteams.csv'))
    click.echo(f"Care team data generated and saved in {out_dir}")


@click.command()
@click.pass_context
def locations(ctx):
    out_dir = ctx.obj['OUT_DIR']
    data = generate_random_data(25, 'locations')
    write_csv(data, os.path.join(out_dir, 'locations.csv'))
    click.echo(f"Location data generated and saved in {out_dir}")


@click.command()
@click.option('--orgs-csv', help='Organizations CSV file')
@click.option('--locs-csv', help='Locations CSV file')
@click.pass_context
def orgs_locs(ctx, orgs_csv, locs_csv):
    out_dir = ctx.obj['OUT_DIR']
    if not out_dir.startswith('./out') and (not orgs_csv or not locs_csv):
        click.echo("Error: --orgs-csv and --locs-csv are required when --out-dir is not './out'")
        return

    orgs_data = generate_random_data(10, 'organizations')
    locs_data = generate_random_data(20, 'locations')

    orgs_file = orgs_csv or os.path.join(out_dir, 'organizations.csv')
    locs_file = locs_csv or os.path.join(out_dir, 'locations.csv')

    write_csv(orgs_data, orgs_file)
    write_csv(locs_data, locs_file)
    click.echo(f"Organization and location data generated and saved in {out_dir}")


@click.command()
@click.pass_context
def users_orgs(ctx):
    out_dir = ctx.obj['OUT_DIR']
    users_data = generate_random_data(15, 'users')
    orgs_data = generate_random_data(5, 'organizations')

    write_csv(users_data, os.path.join(out_dir, 'users.csv'))
    write_csv(orgs_data, os.path.join(out_dir, 'organizations.csv'))
    click.echo(f"User and organization data generated and saved in {out_dir}")


@click.command()
@click.pass_context
def products(ctx):
    out_dir = ctx.obj['OUT_DIR']
    data = generate_random_data(25, 'products')
    write_csv(data, os.path.join(out_dir, 'products.csv'))
    click.echo(f"Product data generated and saved in {out_dir}")


@click.command()
@click.pass_context
def inventories(ctx):
    out_dir = ctx.obj['OUT_DIR']
    data = generate_random_data(25, 'inventories')
    write_csv(data, os.path.join(out_dir, 'inventories.csv'))
    click.echo(f"Inventory data generated and saved in {out_dir}")


@click.command()
@click.pass_context
def organizations(ctx):
    out_dir = ctx.obj['OUT_DIR']
    data = generate_random_data(25, 'organizations')
    write_csv(data, os.path.join(out_dir, 'organizations.csv'))
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