package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Main {

    private final Object lock = new Object();
    Double[][] distanceTable;

    public static void main(String[] args) throws IOException {
        System.out.println(String.format("CORE: %d", Runtime.getRuntime().availableProcessors()));
        String file = args[0];
        Main main = new Main();
        System.out.println("#####################");
        main.original(file);
        System.out.println("#####################");
        main.parallel(file);
    }

    public static void print2D(Double mat[][]) {
        for (Double[] doubles : mat)
            System.out.println(Arrays.toString(doubles));
    }

    void add(Double[] data, int index) {
        synchronized (lock) {
            distanceTable[index - 1] = data;
        }
    }

    public void parallel(String file) throws IOException {
        Euclidian euclidian = new Euclidian();
        long start = System.currentTimeMillis();

        Stream<String> Matrix = Files.lines(Paths.get(file)).parallel();

        System.out.println(String.format("PAR read: %d ms", System.currentTimeMillis() - start));

        Double[][] data = Matrix
                .map(row ->
                        Arrays.stream(row.split(","))
                                .map(Double::parseDouble)
                                .toArray(Double[]::new))
                .toArray(Double[][]::new);

        distanceTable = new Double[data.length - 2][];

        System.out.println(String.format("PAR  map: %d ms", System.currentTimeMillis() - start));

        IntStream
                .range(1, data.length - 1)
                .parallel()
                .forEach(i -> {
                    add(euclidian.euclidian(Arrays.copyOf(data, i + 1)), i);
                });

        System.out.println(String.format("PAR time: %d ms", System.currentTimeMillis() - start));

        System.out.println("####PAR");
        print2D(distanceTable);
    }

    public void original(String file) throws IOException {
        long start = System.currentTimeMillis();

        Stream<String> Matrix = Files.lines(Paths.get(file)).parallel();

        System.out.println(String.format("ORI read: %d ms", System.currentTimeMillis() - start));

        String[][] DataSet = Matrix.map(mapping -> mapping.split(",")).toArray(String[][]::new);

        System.out.println(String.format("ORI  map: %d ms", System.currentTimeMillis() - start));

        Double[][] distanceTable = new Double[DataSet.length - 1][];

        /* START WANT TO REPLACE THIS MATRIX CALCULATION WITH PARALLEL STREAM RATHER THAN USE TRADITIONAL ARRAY ARITHMETICS START  */

        for (int i = 0; i < distanceTable.length - 1; ++i) {
            distanceTable[i] = new Double[i + 1];
            for (int j = 0; j <= i; ++j) {
                double distance = 0.0;
                for (int k = 0; k < DataSet[i + 1].length; ++k) {
                    double difference = Double.parseDouble(DataSet[j][k]) - Double.parseDouble(DataSet[i + 1][k]);
                    distance += difference * difference;
                }
                distanceTable[i][j] = distance;
            }
        }

        System.out.println(String.format("ORI time: %d ms", System.currentTimeMillis() - start));
        print2D(distanceTable);
    }
}

