import json
import sys

from importer.config.settings import api_service


class RoleAction:
    def __init__(self, container, name, exact):
        self.container = container
        self.name = name

    def update_state(self):
        """ Fetch the liveRole and offlineRole """
        self.live_role = None
        self.offline_role = None
        pass

    def check_update_action(self):
        """Decide on how the action that should be taken"""
        """
        check if this needs to be removed under exact.
        check differences, if name or description need to change
        check composition status, is there a way to verify that role getting assigned is valid.
            for exact 
                realm roles:
                    caveat: all realm roles being assigned should be present in the json.
                    generate final state of role and push it
                for client roles, we get all the roles belonging to that client and check.
            for nonExact:
                realm:
                    check still
                    
                    
        RealmRoleController
        ClientRoleController
        """


class RealmRoleController:
    def __init__(self):
        pass

    @staticmethod
    def initialize():
        """ Fetch all realm roles This is uses factory singleton pattern so we create stuff from this function """
        pass

    def get_role_by_id(self, id):
        """
        Gets a role from the internal role store by id, set touched to true
        Args:
            id:

        Returns:

        """
        pass

    def get_role_by_name(self, id):
        """
        Gets a role from the internal role store by name, set touched to true
        Args:
            id:

        Returns:

        """
        pass

    def get_non_touched_roles(self):
        """

        Returns:

        """


class ClientRoleController:
    def __init__(self, client_name):
        pass

    @staticmethod
    def initialize(client_name):
        """ Fetch all realm roles This is uses factory singleton pattern so we create stuff from this function """
        pass

    def get_role_by_id(self, id):
        """
        Gets a role from the internal role store by id, set touched to true
        Args:
            id:

        Returns:

        """
        pass

    def get_role_by_name(self, id):
        """
        Gets a role from the internal role store by name, set touched to true
        Args:
            id:

        Returns:

        """
        pass

    def get_non_touched_roles(self):
        """

        Returns:

        """


def parse_kc_config_rep_config(file_url):
    try:
        with open(file_url, "r") as file:
            contents = file.read()
            return json.loads(contents)
    except FileNotFoundError:
        print(f"Error: The file '{file_url}' was not found.")
        sys.exit(1)
    except json.JSONDecodeError:
        print("Error: Failed to parse the JSON content.")
        sys.exit(1)
    except Exception as e:
        print(f"Error: Unexpected error {e}")
        sys.exit(1)


class RealmRoleFetcher():
    def __init__(self):
        self.realm_roles_by_id = None
        self.realm_roles_by_name = None

    def fetch_live_roles(self):
        all_roles = []
        first = 0
        page_size = 100

        while True:
            # TODO - if any request fails this could lead to negative side effects.
            roles_url = f"{api_service.auth_options.keycloak_realm_uri}/roles?first={first}&max={page_size}"
            roles_page = api_service.request(url=roles_url)
            all_roles.extend(roles_page)

            if len(roles_page) < page_size:
                break
            else:
                first += page_size
        realm_role_by_id = {}
        realm_role_by_name = {}
        for role in all_roles:
            live_role_rep = {
                "resource": role,
                "meta": {
                    "touched": False
                }
            }
            realm_role_by_name[role.get("name")] = live_role_rep
            realm_role_by_id[role.get("id")] = live_role_rep
        self.realm_roles_by_id = realm_role_by_id
        self.realm_roles_by_name = realm_role_by_name

    def get_live_role(self, config_role_id, config_role_name):
        # what would it mean to get by id and miss, then fetch by name and get entry, means the user is trying to force
        # creation of a new role with an existing role name
        if self.realm_roles_by_id is None:
            self.fetch_live_roles()
        if config_role_id is not None:
            return self.realm_roles_by_id[config_role_id]
        elif config_role_name is not None:
            return self.realm_roles_by_name[config_role_name]
        return

    def update_touched_live_role(self, live_role_id):
        pass

def main():
    # deal with realm roles first
    kc_config = parse_kc_config_rep_config()
    role_config = kc_config.get("roles", {})
    realm_roles = role_config.get("realmRoles", [])
    client_roles = role_config.get("clientRoles", {})

    # realm_roles_controller = RealmRoleController.initialize()
    # we start by going over the config's realm roles.
    realm_role_fetcher = RealmRoleFetcher()
    for config_role in realm_roles:
        # source of truth precedence starts with id and then name
        role_id = config_role.get("id")
        role_name = config_role.get("name")

        live_role = realm_role_fetcher.get_live_role(role_id, role_name)
        if live_role is None:
            # we request post the config_role as a new role.
            pass
        else:
            # TODO - we are making changes even when we possibly should not.
            live_role["name"] = config_role.get("name")
            live_role["description"] = config_role.get("description")
            # TODO - we now then request edit the live_role
        # we now check composting realm roles.
        if mode == "normal":
            # we just add missing roles. - need to get the roles by both their ids and names.





        if role_id:
            role = realm_roles_controller.get_role_by_id(role_id)
        elif role is None and role_name:
            role = realm_roles_controller.get_role_by_name(role_name)

        # if not role; then we create new role - post new role. as well as check composites
        # we can send the role as a single request that includes the composeites too.
        RoleService.create_realm_role(role) # - this also adds composite
        continue
        # now the role exists, we need to update it
        RoleService.update_realm_role(role)

    # after processing roles and the mode is global exact. we delete non-touched roles
    for role in realm_roles_controller.get_non_touched_roles():
        if mode === "greedyExact":
            # delete role
            pass


"""
* need to sort the config_roles in dependency order.
* * TODO - prevent requiring to create a role whose composite is a config_role that has not been created yet.
* * TODO - condition above could also occur that the config_role is a composite as well. (for now error out.)
For config_role in configs
    define live_role
    fetch live_role using first id, then name
    if live_role name not config_role name: then live_role.name = config_role.name.
    do same for description: live_role.description = config_role.description.
    # we check composited realm roles.
        live_roles_composites = Fetch live_roles composite_roles by its id to include composited live roles too
        # we check any differences in config_roles_composites and this.(FAFO: If you update a role composite does it stick.)
        # to see what roles should be removed:
            if mode is at least exact :
                Request delete all composited realm roles.
                Request Add config_role_composites to live_role id.
            if mode is normal:
                # only roles that should be added.
                Generate a union of cofig_role_composites with live_role_composites
                Request push all such roles to live_role id.
            if mode is greedyExact(out of scope):
                # we will Request delete existing composited realm roles that are not part of config_role_composite.
    # we go to composited client roles:
        # we should check if there are composited client roles that should be removed 
        # if there are client roles that should be newly composited.     
        
"""