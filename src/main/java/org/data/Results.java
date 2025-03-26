package org.data;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.stat.correlation.Covariance;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.distribution.TDistribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Results {
    private final ArrayList<String> columns = new ArrayList<>();
    private HashMap<String, ArrayList<Double>> data;
    private final HashMap<String, Double> numberOfElements = new HashMap<>();
    private final HashMap<String, Double> max = new HashMap<>();
    private final HashMap<String, Double> min = new HashMap<>();
    private final HashMap<String, Double> distance = new HashMap<>();
    private final HashMap<String, Double> arithmeticMean = new HashMap<>();
    private final HashMap<String, Double> geometricMean = new HashMap<>();
    private final HashMap<String, Double> variance = new HashMap<>();
    private final HashMap<String, Double> standardDeviation = new HashMap<>();
    private final HashMap<String, Double> varianceCoefficient = new HashMap<>();
    private final HashMap<String, Double> covarianceCoefficient = new HashMap<>();
    private final HashMap<String, Double[]> confidenceInterval = new HashMap<>();

    public void calculate(HashMap<String, ArrayList<Double>> data) {
        this.data = data;
        this.calculate();
        this.calculateCovariance();
    }


    private void calculate() {
        for (String key : this.data.keySet()) {
            var selection = this.data.get(key);
            double[] arr = new double[selection.size()];
            Arrays.setAll(arr, selection::get);
            var stats = new DescriptiveStatistics(arr);

            this.columns.add(key);

            this.numberOfElements.put(key, (double) selection.size());
            this.max.put(key, stats.getMax());
            this.min.put(key, stats.getMin());
            this.distance.put(key, stats.getMax() - stats.getMin());

            this.arithmeticMean.put(key, stats.getMean());
            this.geometricMean.put(key, stats.getGeometricMean());

            this.variance.put(key, stats.getVariance());
            this.standardDeviation.put(key, stats.getStandardDeviation());
            this.varianceCoefficient.put(key, stats.getStandardDeviation() / stats.getMean());

            NormalDistribution distribution = new NormalDistribution(stats.getMean(), stats.getStandardDeviation());
            TDistribution tDistribution = new TDistribution(selection.size() - 1);
            double confidenceLevel = tDistribution.inverseCumulativeProbability((1 - 0.95) / 2);
            System.out.println(confidenceLevel);
//            double confidenceLevel = distribution.inverseCumulativeProbability(0.95);
            double temp = confidenceLevel * stats.getStandardDeviation() / Math.sqrt(selection.size());
            this.confidenceInterval.put(key, new Double[]{stats.getMean() - temp, stats.getMean() + temp});
        }
    }

    private void calculateCovariance() {
        for (String key1 : this.data.keySet()) {
            for (String key2 : this.data.keySet()) {
                ArrayList<Double> selection1 = this.data.get(key1), selection2 = this.data.get(key2);;
                double[] arr1 = new double[selection1.size()], arr2 = new double[selection2.size()];
                Arrays.setAll(arr1, selection1::get);
                Arrays.setAll(arr2, selection2::get);
                this.covarianceCoefficient.put(key1 + key2, new Covariance().covariance(arr1, arr2));
            }

        }
    }

    public HashMap<String, HashMap<String, Double>> export() {
        HashMap<String, HashMap<String, Double>> results = new HashMap<>();
        results.put("Number Of Elements", this.numberOfElements);
        results.put("Max", this.max);
        results.put("Min", this.min);
        results.put("Distance", this.distance);
        results.put("Arithmetic Mean", this.arithmeticMean);
        results.put("Geometric Mean", this.geometricMean);
        results.put("Variance", this.variance);
        results.put("Estimate Of Standard Deviation", this.standardDeviation);
        results.put("Coefficient Of Variation", this.varianceCoefficient);

        return results;
    }

    public HashMap<String, Double> exportCovariance() {
        return this.covarianceCoefficient;
    }

    public HashMap<String, Double[]> exportInterval() {
        return this.confidenceInterval;
    }

    public ArrayList<String> getColumns() {
        return this.columns;
    }
}
