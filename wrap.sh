#!/bin/bash
set -euo pipefail

opsem_file="examples/ray.opsem"
head_file="examples/template-head.tex"
foot_file="examples/template-foot.tex"
insert_file="build/tmp/insert_file.tex"
temp_file="build/tmp/temp_file.tex"

./gradlew install

./build/install/opsem/bin/opsem "$opsem_file" > "$insert_file"
cat "$head_file" "$insert_file" "$foot_file" > "$temp_file"

pdflatex "$temp_file"
