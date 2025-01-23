from importer.services.fhir_keycloak_api import FhirKeycloakApi


class KcRoleService:
    def __init__(self, api_service: FhirKeycloakApi):
        self.api_service = api_service

    @property
    def get_groups_uri(self):
        return f"{self.api_service.auth_options.keycloak_realm_uri}/groups"

    def get_delete_group_uri(self, group_id):
        return f"{self.api_service.auth_options.keycloak_realm_uri}/groups/{group_id}"

    def delete_group(self, group_id):
        delete_group_uri = self.get_delete_group_uri(group_id)
        response = self.api_service.request(url=delete_group_uri, method="delete")
        response.raise_for_status()
        return response.json()

    def create_group(self, group_Config):
        # create a group, if group has sub-groups also create it. do we do this recursively.
        pass