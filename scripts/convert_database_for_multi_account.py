#!/usr/bin/env python

import json


with open("/Users/arjun/IdeaProjects/group44/data/database.json", "r") as database_file:
    database = json.load(database_file)

next_account_id = 1
for account in database["accounts"]:
    account["@id"] = next_account_id
    next_account_id += 1

all_account_ids = list(range(1, next_account_id))
for poi in database["pois"]:
    poi["favoriteOf"] = all_account_ids if poi["favorite"] else []
    del poi["favorite"]

with open("/Users/arjun/IdeaProjects/group44/data/database.json", "w") as database_file:
    json.dump(database, database_file, indent=2, sort_keys=True)
    database_file.write("\n")
