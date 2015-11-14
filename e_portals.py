#!/usr/bin/env python
# -*- coding: utf-8 -*-

from itertools import takewhile

from  pymongo import MongoClient
client = MongoClient("cannobio")
coll = client.wikipedia.portals

coll.remove()

is_tab = '\t'.__eq__

def build_tree(lines):
  lines = iter(lines)
  stack = []
  for line in lines:
    indent = len(list(takewhile(is_tab, line)))
    stack[indent:] = [line.lstrip()]
    yield stack

with open("portals.data") as f:
  for node in build_tree(f.read().split('\n')):
    coll.insert({"key": node[-1].lower(), "tree": node, "repr": "/".join(node)})





# def _recurse_tree(parent, depth, source):
#     last_line = source.readline().rstrip()
#     while last_line:
#         tabs = last_line.count('\t')
#         if tabs < depth:
#             break
#         node = last_line.strip()
#         if tabs >= depth:
#             if parent is not None:
#                 print "%s: %s" %(parent, node)
#             last_line = _recurse_tree(node, tabs+1, source)
#     return last_line

# inFile = open("portals.data")
# _recurse_tree(None, 0, inFile)




# depth = 0
# root = { "txt": "root", "children": [] }
# parents = []
# node = root
# for line in f:
#     line = line.rstrip()
#     newDepth = len(line) - len(line.lstrip("\t")) + 1
#     print newDepth, line
#     # if the new depth is shallower than previous, we need to remove items from the list
#     if newDepth < depth:
#         parents = parents[:newDepth]
#     # if the new depth is deeper, we need to add our previous node
#     elif newDepth == depth + 1:
#         parents.append(node)
#     # levels skipped, not possible
#     elif newDepth > depth + 1:
#         raise Exception("Invalid file")
#     depth = newDepth

#     # create the new node
#     node = {"txt": line.strip(), "children":[]}
#     # add the new node into its parent's children
#     parents[-1]["children"].append(node)

# json_list = root["children"]
# print json_list