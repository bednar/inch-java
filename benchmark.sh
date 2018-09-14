#!/usr/bin/env bash

singleQuery=""
multipleQuery=""
database=""

echo "###################################################################################"
echo
echo "Single property query, repeat 1x, JSON"
echo "--------------------------------------"
echo
java -cp inch-java-2.12.jar org.influxdb.tool.benchmark.QueryComparator -query "${singleQuery}" -database ${database} -repeat 1 -hide true
echo 
java -cp inch-java-2.13.jar org.influxdb.tool.benchmark.QueryComparator -query "${singleQuery}" -database ${database} -repeat 1 -hide true
echo
echo "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo
echo "Single property query, repeat 10x, JSON"
echo "--------------------------------------"
echo
java -cp inch-java-2.12.jar org.influxdb.tool.benchmark.QueryComparator -query "${singleQuery}" -database ${database} -repeat 10 -hide true
echo
java -cp inch-java-2.13.jar org.influxdb.tool.benchmark.QueryComparator -query "${singleQuery}" -database ${database} -repeat 10 -hide true
echo
echo "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo
echo "Single property query, repeat 50x, JSON"
echo "--------------------------------------"
echo
java -cp inch-java-2.12.jar org.influxdb.tool.benchmark.QueryComparator -query "${singleQuery}" -database ${database} -repeat 50 -hide true
echo
java -cp inch-java-2.13.jar org.influxdb.tool.benchmark.QueryComparator -query "${singleQuery}" -database ${database} -repeat 50 -hide true
echo
echo "###################################################################################"
echo
echo "Multiple property query, repeat 1x, JSON"
echo "--------------------------------------"
echo
java -cp inch-java-2.12.jar org.influxdb.tool.benchmark.QueryComparator -query "${multipleQuery}" -database ${database} -repeat 1 -hide true -limits "100,500,1000,5000,10000,50000"
echo
java -cp inch-java-2.13.jar org.influxdb.tool.benchmark.QueryComparator -query "${multipleQuery}" -database ${database} -repeat 1 -hide true -limits "100,500,1000,5000,10000,50000"
echo
echo "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo
echo "Multiple property query, repeat 10x, JSON"
echo "--------------------------------------"
echo
java -cp inch-java-2.12.jar org.influxdb.tool.benchmark.QueryComparator -query "${multipleQuery}" -database ${database} -repeat 10 -hide true -limits "100,500,1000,5000,10000,50000"
echo
java -cp inch-java-2.13.jar org.influxdb.tool.benchmark.QueryComparator -query "${multipleQuery}" -database ${database} -repeat 10 -hide true -limits "100,500,1000,5000,10000,50000"
echo
echo "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -"
echo
echo "Multiple property query, repeat 50x, JSON"
echo "--------------------------------------"
echo
java -cp inch-java-2.12.jar org.influxdb.tool.benchmark.QueryComparator -query "${multipleQuery}" -database ${database} -repeat 50 -hide true -limits "100,500,1000,5000,10000,50000"
echo
java -cp inch-java-2.13.jar org.influxdb.tool.benchmark.QueryComparator -query "${multipleQuery}" -database ${database} -repeat 50 -hide true -limits "100,500,1000,5000,10000,50000"
echo


