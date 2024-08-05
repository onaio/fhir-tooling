"""
class that returns generators for each of the different categories of data.
"""

class DataGen:
    def __init__(self):
        self._users = []

    def users(self):
        if len(self._users) > 0:
            return self._users
        # generate and return users information

    def users_organizations(self):
        if()