"""
preprocess a csv of location records, group them by their admin levels
"""


class LocationNode:
    def __init__(self, raw_record=None, parent_node=None, children=None):
        if children is None:
            children = []
        self.parent = parent_node
        self.children = children
        self.raw_record = raw_record

    @property
    def admin_level(self):
        try:
            return self.raw_record[8]
        except:
            return None

    @property
    def location_id(self):
        try:
            return self.raw_record[3]
        except:
            return None

    def __repr__(self):
        return f"<LocationNode: {self.location_id}; parent: {self.parent}; {self.raw_record} />"


def group_by_admin_level(csv_records):
    location_node_store = {}
    for record in csv_records:
        location_id, parent_id = record[3], record[5]
        this_record_node = location_node_store.get(location_id)
        if this_record_node:
            # assume tombstone
            this_record_node.raw_record = record
        else:
            this_record_node = LocationNode(record)
            location_node_store[location_id] = this_record_node
        # see if this parentNode exists in the nodeStore
        if parent_id:
            this_location_parent_node = location_node_store.get(parent_id)
            if this_location_parent_node is not None:
                pass
            else:
                # create a tombstone
                this_location_parent_node = LocationNode()
                location_node_store[parent_id] = this_location_parent_node
            this_location_parent_node.children.append(this_record_node)
            this_record_node.parent = this_location_parent_node
    return location_node_store


def get_node_children(parents):
    children = []
    for node in parents:
        children.extend(node.children)
    return children


def get_next_admin_level(node_map_store: dict):
    """generator function that yields the next group of admin level locations"""
    # start by getting the parent locations. i.e. locations that do not have parent
    parent_nodes = []
    for node in node_map_store.values():
        if node.parent is None and node.raw_record or node.parent and node.parent.raw_record is None:
            parent_nodes.append(node)
    yield parent_nodes

    fully_traversed = False
    while not fully_traversed:
        children_at_this_level = get_node_children(parent_nodes)
        if len(children_at_this_level) == 0:
            fully_traversed = True
        else:
            parent_nodes = children_at_this_level
            yield children_at_this_level


def process_locations(csv_records):
    nodes_map = group_by_admin_level(csv_records)
    for batch in get_next_admin_level(nodes_map):
        batch_raw_records = []
        for node in batch:
            batch_raw_records.append(node.raw_record)
        yield batch_raw_records

# TODO - validate based on adminlevel and the generated groupings based on the parentIds