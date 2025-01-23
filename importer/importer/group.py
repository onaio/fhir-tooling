"""
Group merger
"""
import json
import sys
from importer.config.settings import api_service


def fetch_groups(parent_id = None):
    url_params_args = "briefRepresentation=false"
    url = f"{api_service.auth_options.keycloak_realm_uri}/groups?{url_params_args}"
    if parent_id:
        url = f"{api_service.auth_options.keycloak_realm_uri}/groups/{parent_id}/children?{url_params_args}"
    try:
        response = api_service.request(url=url)
        return response.json()
    except Exception:
        # should we add to warnings? probably not since if mode is greedyExact that could have
        # problematic side effects.
        sys.exit(1)

def groups_fetcher_controller():
    """
    Recursively fetch all groups in the server, return them as a map of id to the respective group.
    Returns: a flat map of all groups that exists on the server.
    """
    groups_by_name = {}
    groups_by_id = {}
    groups = fetch_groups()
    groups_at_level = groups
    while len(groups_at_level):
        next_level_groups = []
        for group in groups_at_level:
            # append to groups map
            internal_group_rep = {
                "resource": group,
                "meta": {
                    "touched": False
                }
            }
            # important to maintain reference across both maps
            groups_by_name[group.get("name")] = internal_group_rep
            groups_by_id[group.get("id")] = internal_group_rep
            if group.get("subgroupsCount"):
                this_groups_children = fetch_groups((group.get("id")))
                next_level_groups.extend(this_groups_children)
        groups_at_level = next_level_groups

    return groups_by_name, groups_by_id

def process_groups_from_file(config_groups, mode, groups_by_name, groups_by_id):
    # we first go through config_groups, mark those that we visit as touched
    # If mode is greedyExact then we go through the non visited and delete them.
    for config_group in config_groups:
        config_group_id = config_group.get("id")
        config_group_name = config_group.get("name")
        live_group = None
        if config_group_id:
            live_group = groups_by_id.get(config_group_id)
        else:
            live_group = groups_by_name.get(config_group_name)
        # if config_group_id is different but name is similar with an existing live_group, let the api error out.
        # TODO - top level config groups could also be subgroups. How should this behave if the parent id is not found?
        group_merge_processing(config_group, config_group_parent = None, live_group_parent = None, mode="normal", groups_by_id=groups_by_id, groups_by_name=groups_by_name)

def group_merge_processing(config_group, config_group_parent = None, live_group_parent = None, mode="normal", groups_by_name = {}, groups_by_id = {}):
    """Does the actual group referencing and queues request operations."""
    config_group_id = config_group.get("id")
    config_group_name = config_group.get("name")
    live_group = None
    if config_group_id:
        live_group = groups_by_id.get(config_group_id)
    else:
        live_group = groups_by_name.get(config_group_name)

    # TODO - 4 and 3, changes to a group on the sub grouping hierarchy.

    # Are we creating a new resource or editing an existing record
    method = "POST" # initiate assumption that we are creating a new record
    if live_group:
        method = "PUT"
        # we check if we need a name change.
        if live_group.get("name") != config_group.get("name"):
            live_group["name"] = config_group.get("name")
        # we check realm roles
        if mode == "normal":
            # create union of realm roles
            config_group_realm_roles = config_group.get("realmRoles", [])
            live_group_realm_roles = live_group.get("realmRoles", [])
            final_realm_roles = list(set(config_group_realm_roles).union(set(live_group_realm_roles)))
            live_group["realmRoles"] = final_realm_roles
        elif mode == "exact" or mode == "greedyExact":
            live_group["realmRoles"] = config_group.get("realmRoles", [])
        # we now check client Roles
        if mode == "normal":
            config_group_client_roles = config_group.get("clientRoles", {})
            live_group_client_roles = live_group.get("clientRoles", {})
            if live_group.get("clientRoles") is None:
                live_group["clientRoles"] = {}
            for key in set(config_group_client_roles.keys()).union(live_group_client_roles.keys()):
                live_group["clientRoles"][key] = config_group_client_roles.get(key, []) + live_group_client_roles.get(key, [])
        elif mode == "exact" or mode == "greedyExact":
            # TODO - do we need to be worried that the realm roles and client roles might not exist.
            live_group["clientRoles"] = config_group.get("clientRoles")
        # we now push it to the api.
        group_update_url = f"{api_service.auth_options.keycloak_realm_uri}/groups/{live_group.id}"
        api_service.request(url=group_update_url, method=method, json=json.dumps(live_group))
    else:
        # we create the group
        group_create_url = f"{api_service.auth_options.keycloak_realm_uri}/groups"
        api_service.request(url=group_create_url, method=method, json=json.dumps(live_group))
    # TODO - update roles via role-mapping since just creating the group does associate the respective role mapping.
    # we now process if config_group has subgroups
    config_group_sub_groups = config_group.get("subGroups")
    if len(config_group_sub_groups):
        for config_sub_group in config_group_sub_groups:
            # process merge the sub_groups, TODO - consideration for stack overflow errors for deeply nested subgrouping
            group_merge_processing(config_sub_group, config_group_parent=config_group, mode=mode, groups_by_name=groups_by_name, groups_by_id=groups_by_id)
