#!/bin/bash
#
# docker-entrypoint for docker-solr

set -e

if [[ "$VERBOSE" = "yes" ]]; then
    set -x
fi

spark-submit --class HiveSparkApp app/spark1_2.11-0.1.jar && spark-submit --class HbaseApp --repositories http://repo.hortonworks.com/content/groups/public/ --packages com.hortonworks:shc-core:1.1.1-2.1-s_2.11  app/spark1_2.11-0.1.jar


