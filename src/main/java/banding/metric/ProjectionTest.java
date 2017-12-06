package banding.metric;

import banding.IntervalReader;
import banding.entity.Interval;
import banding.entity.Track;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;

import static banding.entity.Track.isPointInAnyIntervalOf;

public class ProjectionTest {

    public static int countProjection(Map<String, Track> referenceMap, Map<String, Track> queryMap) {
        int projectionCount = 0;

        for (Map.Entry<String, Track> entry: referenceMap.entrySet()) {
            projectionCount += countProjection(entry.getValue(), queryMap.get(entry.getKey()));
        }
        return projectionCount;
    }

    private static long countProjection(Track reference, Track query) {
        return countProjection(reference.getIntervals(), query.getIntervals());
    }

    public static long countProjection(Queue<Interval> referenceIntervals, Queue<Interval> queryIntervals) {
        return queryIntervals.stream()
                .map(Interval::middleOfInterval)
                .filter(x -> isPointInAnyIntervalOf(x, referenceIntervals))
                .count();
    }

    public static void main(String[] args) throws IOException {
        String ref = "/Users/alkli/Documents/Yandex.Disk/BioInstitute/banding/banding/src/main/resources/hgTables_ref";
        String query = "/Users/alkli/Documents/Yandex.Disk/BioInstitute/banding/banding/src/main/resources/hgTables_CpG";

        Queue<Interval> referenceIntervals = new IntervalReader(ref).read();
        Queue<Interval> queryIntervals = new IntervalReader(query).read();

        System.out.println(ProjectionTest.countProjection(queryIntervals, referenceIntervals));
    }
}
