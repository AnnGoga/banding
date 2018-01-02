package banding.experiment.runner;

import banding.entity.Genome;
import banding.entity.Track;
import banding.generator.RandomTrackGenerator;
import banding.metric.JaccardTest;
import banding.metric.ProjectionTest;
import banding.report.Report;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.spark.api.java.JavaDoubleRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.stat.Statistics;
import org.apache.spark.mllib.stat.test.KolmogorovSmirnovTestResult;
import org.apache.spark.sql.SparkSession;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JaccardTestExperimentRunner {

    public static Report getReportForJaccardTest(SparkSession spark, Genome reference, Genome query, int numberOfExperiments) throws IOException {

        Report report = new Report();

        double queryJaccardTest = JaccardTest.computeJaccardStatisticForGenome(reference, query);

        List<Double> jaccardTestExperiments = generateRandomChromosomeSetsAndComputeJaccardTest(reference, query, numberOfExperiments);
        Double mean = jaccardTestExperiments.stream().collect(Collectors.averagingDouble(Double::valueOf));
        Double sumDev = jaccardTestExperiments.stream()
                .map(x -> ((double) x - mean) * ((double) x - mean))
                .collect(Collectors.summingDouble(Double::valueOf));
        double sd = Math.sqrt(sumDev / numberOfExperiments);

        JavaDoubleRDD rdd = getSparkContext(spark).parallelize(jaccardTestExperiments).mapToDouble(Double::doubleValue); //.mapToDouble(Double::valueOf);
        KolmogorovSmirnovTestResult result = Statistics.kolmogorovSmirnovTest(rdd, "norm", mean, sd);

        TTest tTest = new TTest();
        double tTestPValue = tTest.tTest(queryJaccardTest, getDoubleArray(jaccardTestExperiments));

        return report;
    }

    private static List<Double> generateRandomChromosomeSetsAndComputeJaccardTest(Genome referenceMap, Genome queryMap, int numberOfExperiments) {
        int capacity = numberOfExperiments;
        List<Double> stats = IntStream.range(0, capacity).boxed()
                .parallel()
                .map(x -> getJaccardTestForRandomChromosome(referenceMap, queryMap))
                .collect(Collectors.toList());
        return stats;
    }

    private static double getJaccardTestForRandomChromosome(Genome reference, Genome query) {
        Genome randomGenome = generateRandomGenomeByReferenceLike(reference, query);
        return JaccardTest.computeJaccardStatisticForGenome(reference, randomGenome);
    }

    private static Genome generateRandomGenomeByReferenceLike(Genome reference, Genome query) {
        Map<String, Track> referenceMap = new HashMap<>();
        reference.getChromosomes().stream()
                .forEach(x -> referenceMap.put(x.getName(), x.getTrack()));

        Map<String, Track> queryMap = new HashMap<>();
        query.getChromosomes().stream()
                .forEach(x -> queryMap.put(x.getName(), x.getTrack()));

        return RandomTrackGenerator.generateGenomeByReferenceLike(reference, query);
    }


    private static double[] getDoubleArray(List<? extends Number> list) {
        double[] arr = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            arr[i] = list.get(i).doubleValue();
        }
        return arr;
    }

    private static JavaSparkContext getSparkContext(SparkSession spark) {
        return JavaSparkContext.fromSparkContext(spark.sparkContext());
    }
}