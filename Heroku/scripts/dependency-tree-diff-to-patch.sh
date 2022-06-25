#!/usr/bin/env bash
# ./dependency-tree-diff-to-patch.sh diff.txt sha-before sha-after > diff.patch
echo "Index: Dependencies"
echo "==================================================================="
echo "./dependency-tree-diff.jar prev/$1 curr/$1"
echo "--- a/prev/$1	(revision $2)"
echo "+++ b/curr/$1	(revision $3)"
echo "@@ -1,$(cat "$1" | wc -l) +1,$(cat "$1" | wc -l) @@"
sed -re 's/^$|^([^+-])/ \0/g' "$1"
