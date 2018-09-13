package org.influxdb.tool.benchmark;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.tool.Utils;

/**
 * @author hoan.le [at] bonitoo.io
 */
public class QueryComparator {

    private InfluxDB influxDB;

    public static void main(String[] args) throws InterruptedException, IOException {

        String singleQuery = "...";

        String multipleQuery = "...";

        QueryComparator queryComparator = new QueryComparator();
        queryComparator.influxDB = Utils.connectToInfluxDB();

        // single field
        for (boolean chunking : new boolean[]{false, true}) {
            for (String order : new String[]{"ASC", "DESC"}) {
                for (Integer limit : new Integer[]{100, 1000, 10000}) {

                    String statement = String.format(singleQuery, order);

                    queryComparator.query("single property", statement, limit, order, chunking);
                }
            }
        }
        System.out.println();
        {
            // multiple field
            for (boolean chunking : new boolean[]{false, true}) {
                for (String order : new String[]{"ASC", "DESC"}) {
                    for (Integer limit : new Integer[]{100, 500, 1000, 5000, 10000, 50000}) {

                        String statement = String.format(multipleQuery, order);

                        queryComparator.query("multiple property", statement, limit, order, chunking);
                    }
                }
            }
        }

        queryComparator.influxDB.close();
        queryComparator.influxDB = null;
    }

    private void query(String name, String statement, Integer limit, String order, boolean chunking) throws InterruptedException {

        long now = System.currentTimeMillis();

        Query query = new Query(statement + " LIMIT " + limit, "thingworx_pp");
        if (chunking) {
            influxDB.query(query);
        } else {
            CountDownLatch countDownLatch = new CountDownLatch(1);

//            influxDB.query(query, 100,
//                    new java.util.function.BiConsumer<InfluxDB.Cancellable, QueryResult>() {
//                        @Override
//                        public void accept(InfluxDB.Cancellable cancellable, QueryResult queryResult) {
//
//                        }
//                    }, new Runnable() {
//                        @Override
//                        public void run() {
//                            countDownLatch.countDown();
//                        }
//                    });

            influxDB.query(query, 100,
                    new java.util.function.Consumer<QueryResult>() {
                        @Override
                        public void accept(QueryResult queryResult) {
                            if (queryResult.getError() != null && queryResult.getError().equals("DONE")) {
                                countDownLatch.countDown();
                            }
                        }
                    });
            countDownLatch.await(5, TimeUnit.SECONDS);
        }

        long elapsed = System.currentTimeMillis() - now;
        System.out.println(name + String.format(" [chunking = %s, %s, %s]", chunking, order, limit) + ", elapsed = " + elapsed);
    }
}
