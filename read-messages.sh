#!/bin/bash
URL="http://metrics-demo-demo.apps.dbag.cc-openshift.de"
COUNT=10
SLEEP=0

if [[ $# -gt 0 ]]; then
    COUNT="$1"
fi

if [[ $# -gt 1 ]]; then
    SLEEP="$2"
fi

for i in $(seq 1 ${COUNT}); do
    curl "$URL/message/$i"
    echo
    sleep ${SLEEP}
done
