package org.example;

public class Euclidian {

    public Double[] euclidian(Double[][] data) {

//        for (Double[] doubles : data)
//            System.out.println(Arrays.toString(doubles));

        Double[] result = new Double[data.length - 1];
        for (int i = 0; i < result.length; i++) {
            result[i] =
                    Math.pow(data[i][0] - data[data.length - 1][0], 2) +
                            Math.pow(data[i][1] - data[data.length - 1][1], 2);
//            System.out.println(String.format("%d: %.0f", i, result[i]));
        }

        return result;
    }
}
