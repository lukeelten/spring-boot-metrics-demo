#!/bin/bash
URL="http://metrics-demo-demo.apps.dbag.cc-openshift.de"
COUNT=10

if [[ $? -gt 1 ]]; then
    COUNT="$1"
fi

for i in $(seq 1 10); do
    curl "$URL/message" -d "{\"message\": \"message$i\", \"sender\": \"sender$i\", \"recipient\": \"recipient$i\"}" -H "Content-Type: application/json"
    echo
done
