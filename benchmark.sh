#!/usr/bin/env bash

singleQuery=""
multipleQuery=""
database=""

chunking=100

query () {

    echo "$1"
    echo
    echo  " - chunking: $2"
    echo  " - repeat: $3x"
    echo  " - format: $4"
    echo  " - limits: $6"
#    echo  " - query: $5"

    echo
    java -cp inch-java-2.12.jar org.influxdb.tool.benchmark.QueryComparator -query "$5" -database ${database} -repeat $3 -hide true -chunking $2 -format $4 -limits "$6"
    echo
    java -cp inch-java-2.13.jar org.influxdb.tool.benchmark.QueryComparator -query "$5" -database ${database} -repeat $3 -hide true -chunking $2 -format $4 -limits "$6"
    echo
    echo "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - "
    echo

   return 0
}

echo "################################"
echo "#             JSON             #"
echo "################################"

query "Single property query" ${chunking} 1 "JSON" "${singleQuery}" "100,1000,10000"
query "Single property query" ${chunking} 10 "JSON" "${singleQuery}" "100,1000,10000"
query "Single property query" ${chunking} 50 "JSON" "${singleQuery}" "100,1000,10000"

query "Multiple property query" ${chunking} 1 "JSON" "${multipleQuery}" "100,1000,10000"
query "Multiple property query" ${chunking} 10 "JSON" "${multipleQuery}" "100,1000,10000"
query "Multiple property query" ${chunking} 50 "JSON" "${multipleQuery}" "100,1000,10000"

echo "################################"
echo "#           MSGPACK            #"
echo "################################"

query "Single property query" ${chunking} 1 "MSGPACK" "${singleQuery}" "100,1000,10000"
query "Single property query" ${chunking} 10 "MSGPACK" "${singleQuery}" "100,1000,10000"
query "Single property query" ${chunking} 50 "MSGPACK" "${singleQuery}" "100,1000,10000"

query "Multiple property query" ${chunking} 1 "MSGPACK" "${multipleQuery}" "100,1000,10000"
query "Multiple property query" ${chunking} 10 "MSGPACK" "${multipleQuery}" "100,1000,10000"
query "Multiple property query" ${chunking} 50 "MSGPACK" "${multipleQuery}" "100,1000,10000"

