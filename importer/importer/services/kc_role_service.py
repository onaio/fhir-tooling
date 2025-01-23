from importer.services.fhir_keycloak_api import FhirKeycloakApi


class KcRoleService:
    def __init__(self, api_service: FhirKeycloakApi, client_id = None):
        self.api_service = api_service
        self.client_id = client_id

    def format_role_payload(self, role_config):
        def add_if_not_None(store, key, value):
            if value is not None:
                store[key] = value
        role_payload = {}
        add_if_not_None(role_payload, "id", role_config.get("id"))
        add_if_not_None(role_payload, "name", role_config.get("name"))
        add_if_not_None(role_payload, "description", role_config.get("description"))
        add_if_not_None(role_payload, "composite", bool(role_config.get("composite")))
        add_if_not_None(role_payload, "clientRole", bool(self.client_id))
        add_if_not_None(role_payload, "containerId", self.client_id)
        composites_payload = {}
        add_if_not_None(composites_payload, "realm", role_config.get("composites", {}).get("realm"))
        add_if_not_None(composites_payload, "client", role_config.get("composites", {}).get("clientRoles"))
        add_if_not_None(role_payload, "composites", composites_payload)
        return role_payload

    def create_role_update_payload(self, role_config, live_role):
        def add_if_not_None(store, key, value):
            if value is not None:
                store[key] = value
        role_payload = {}
        add_if_not_None(role_payload, "id", live_role.get("id"))
        add_if_not_None(role_payload, "name", role_config.get("name"))
        add_if_not_None(role_payload, "description", role_config.get("description"))
        # if mode is exact:
            # update role in place as it is.
        # if mode is not exact :
            # if live_role is composite:
                # fetch composites, check which role_Config composites are not already added in live_role
                # add composites to role
            # if live_role is not composite
                # if role_Config is also not composite, update other details
                # if role_config is composite, then switch role to composite via create_role_payload
            # fetch composite roles for the live_role
        # if live_role is composite and role_config is not:
            # if mode is
        add_if_not_None(role_payload, "composite", bool(role_config.get("composite")))
        add_if_not_None(role_payload, "clientRole", bool(self.client_id))
        add_if_not_None(role_payload, "containerId", self.client_id)
        composites_payload = {}
        add_if_not_None(composites_payload, "realm", role_config.get("composites", {}).get("realm"))
        add_if_not_None(composites_payload, "client", role_config.get("composites", {}).get("clientRoles"))
        add_if_not_None(role_payload, "composites", composites_payload)
        return role_payload

    @property
    def get_role_uri(self):
        if self.client_id:
            return f"{self.api_service.auth_options.keycloak_realm_uri}/clients/{self.client_id}/roles"
        return f"{self.api_service.auth_options.keycloak_realm_uri}/roles"

    def get_delete_role_uri(self, role_config):
        role_id = role_config.get("id")
        role_name = role_config.get("name")
        if role_id:
            return f"{self.api_service.auth_options.keycloak_realm_uri}/roles_by_id/{role_id}"
        if not role_name:
            raise Exception("Cannot delete an unknown role, role missing both an id and name")
        if self.client_id:
            return f"{self.api_service.auth_options.keycloak_realm_uri}/clients/{self.client_id}/roles/{role_name}"
        return f"{self.api_service.auth_options.keycloak_realm_uri}/roles/{role_name}"

    def create_role(self, role_c):
        """Create new role and assign composites where required."""
        role_payload = self.format_role_payload(role_c)
        response = self.api_service.request(method="post", url=self.get_role_uri, json=role_payload)
        response.raise_for_status()
        return response.json()

    def update_role(self, role_config, live_role, mode):
        """
            Perform an update on a role. Superficially change attributes depending on mode;
            if mode is normal -> update name, description,
                -> then get roles composites and check negative intersection and push those roles.
            if mode is exact -> Update role full details -> delete it and call create realm role
            if mode is global and exact - no consequence here
        Args:
            role:

        Returns:
        """
        if mode == "exact":
            # just update it.
            payload = self.format_role_payload(role_config)
            response = self.api_service.request(method="put", json=payload)
            response.raise_for_status()
            return response.json()
        else:
            role_config_is_composite = bool(role_config.composites)
            if role_config_is_composite and live_role.composite:
                # get live_role composite roles. - TBC
                pass

    def delete_role(self, role):
        """
        Delete a role - consideration on the role.
        Args:
            role:

        """
        delete_role_uri = self.get_delete_role_uri(role)
        response = self.api_service.request(url=delete_role_uri, method="delete")
        response.raise_for_status()
        return response.json()

    def get_roles(self):
        """
        Gets all roles in the container , the client if client id is provided or the realm
        """
        response = self.api_service.request(url=self.get_role_uri, method="get")
        response.raise_for_status()
        data = response.json()
        return data