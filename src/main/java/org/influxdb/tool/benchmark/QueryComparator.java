package org.influxdb.tool.benchmark;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.influxdb.tool.Utils;

/**
 * @author hoan.le [at] bonitoo.io
 */
public class QueryComparator {

    public static void main(String[] args) throws InterruptedException, IOException {

        String statement = null;
        String database = null;
        List<Integer> limits = new ArrayList<>();
        InfluxDB.ResponseFormat format = InfluxDB.ResponseFormat.JSON;
        int chunking = -1;
        int repeat = 1;

        // parse command line
        {
            Options options = new Options();

            options.addOption(Option.builder("help").desc("Print this help").hasArg(false).build());
            options.addOption(Option.builder("query").desc("Query to test - String").hasArg().build());
            options.addOption(Option.builder("database").desc("Database to use").hasArg().build());
            options.addOption(Option.builder("limits").desc("Limits to test String (default \"100,1000,10000\")").hasArg().build());
            options.addOption(Option.builder("format").desc("InfluxDB response format (default \"JSON\"; JSON, MSGPACK)").hasArg().build());
            options.addOption(Option.builder("chunking").desc("Size chunk - int (default 100, -1 to disable)").hasArg().build());
            options.addOption(Option.builder("repeat").desc("Repeat query - int (default 1)").hasArg().build());

            CommandLineParser parser = new DefaultParser();
            try {
                // parse the command line arguments
                CommandLine line = parser.parse(options, args);
                if (line.hasOption("help")) {
                    HelpFormatter formatter = new HelpFormatter();
                    formatter.printHelp("java -cp target/inch-java-1.0-jar-with-dependencies.jar org.influxdb.tool.benchmark.QueryComparator", options, true);
                    return;
                }

                statement = line.getOptionValue("query");

                database = line.getOptionValue("database");

                limits = Stream.of(line.getOptionValue("limits", "100,1000,10000")
                        .split(","))
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());

                format = InfluxDB.ResponseFormat.valueOf(line.getOptionValue("format", "JSON"));

                chunking = Integer.parseInt(line.getOptionValue("chunking", "100"));

                repeat = Integer.parseInt(line.getOptionValue("repeat", "1"));
            } catch (ParseException exp) {
                System.err.println("Parsing failed.  Reason: " + exp.getMessage());
            }
        }

        InfluxDB influxDB = Utils.connectToInfluxDB(format);


        String leftAlignFormat = "| %-35s | %-8d | %-9d |%n";

        System.out.println();
        System.out.println("Query: " + statement);
        System.out.println("Format: " + format);
        System.out.println("Repeat: " + repeat);
        System.out.println();
        System.out.format("+-------------------------------------+----------+-----------+%n");
        System.out.format("| Settings                            | Time ASC | Time DESC |%n");
        System.out.format("+-------------------------------------+----------+-----------+%n");

        for (Integer limit : limits) {

            List<Long> elepsedTimes = new ArrayList<>();
            for (String order : new String[]{"ASC", "DESC"}) {
                Query query = new Query(String.format(statement + " LIMIT " + limit, order), database);
                long now = System.currentTimeMillis();

                for (int i = 1; i <= repeat; i++) {

                    if (chunking <= 0) {
                        influxDB.query(query);
                    } else {
                        CountDownLatch countDownLatch = new CountDownLatch(1);

//            influxDB.query(query, chunking,
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

                        influxDB.query(query, chunking,
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
                }
                long elapsed = (System.currentTimeMillis() - now) / repeat;
                elepsedTimes.add(elapsed);
            }
            System.out.format(leftAlignFormat, String.format("[chunking = %s, limit = %s]", chunking, limit), elepsedTimes.get(0), elepsedTimes.get(1));
        }

        System.out.format("+-------------------------------------+----------+-----------+%n");

        influxDB.close();
        System.exit(0);
    }
}
