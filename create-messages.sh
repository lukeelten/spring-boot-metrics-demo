#!/bin/bash
#URL="http://metrics-demo-demo.apps.dbag.cc-openshift.de"

URL="http://localhost:8080"
COUNT=10
SLEEP=0

if [[ $# -gt 0 ]]; then
    COUNT="$1"
fi

if [[ $# -gt 1 ]]; then
    SLEEP="$2"
fi

for i in $(seq 1 ${COUNT}); do
    curl "$URL/message" -d "{\"message\": \"message$i\", \"sender\": \"sender$i\", \"recipient\": \"recipient$i\"}" -H "Content-Type: application/json"
    echo
    sleep ${SLEEP}
done
