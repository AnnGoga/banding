package banding.metric;

import banding.IntervalReader;
import banding.entity.Chromosome;
import banding.entity.Genome;
import banding.entity.Interval;
import banding.entity.Track;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertThat;

public class JaccardTestTest {

    @Test
    public void shouldComputeIntersectionOfIntervals() {
        Deque<Interval> reference = new ArrayDeque<>();
        reference.add(new Interval(5, 55));
        reference.add(new Interval(204, 255));

        Deque<Interval> query = new ArrayDeque<>();
        query.add(new Interval(5, 20));
        query.add(new Interval(27, 42));
        query.add(new Interval(47, 62));
        query.add(new Interval(197, 212));
        query.add(new Interval(219, 234));
        query.add(new Interval(242, 257));

        assertThat(JaccardTest.getIntersectionValue(reference, query), is(80L));
    }

    @Test
    public void printUnionAndIntersection() {
        Deque<Interval> reference = new ArrayDeque<>();
        reference.add(new Interval(5, 55));
        reference.add(new Interval(204, 255));

        Deque<Interval> query = new ArrayDeque<>();
        query.add(new Interval(5, 20));
        query.add(new Interval(27, 42));
        query.add(new Interval(47, 62));
        query.add(new Interval(197, 212));
        query.add(new Interval(219, 234));
        query.add(new Interval(242, 257));

        System.out.println("TrackUnion");
        Track.tracksUnion(query, reference)
                .forEach(x -> System.out.println(x.getStartIndex() + "\t" + x.getEndIndex() + "\t" + x.getLength()));

        reference = new ArrayDeque<>();
        reference.add(new Interval(5, 55));
        reference.add(new Interval(204, 255));

        query = new ArrayDeque<>();
        query.add(new Interval(5, 20));
        query.add(new Interval(27, 42));
        query.add(new Interval(47, 62));
        query.add(new Interval(197, 212));
        query.add(new Interval(219, 234));
        query.add(new Interval(242, 257));

        System.out.println("TrackIntersection");
        Track.trackIntersection(query, reference)
                .forEach(x -> System.out.println(x.getStartIndex() + "\t" + x.getEndIndex() + "\t" + x.getLength()));
    }

    @Test
    public void shouldComputeJaccardMetric() throws IOException {
        String reference = "/Users/alkli/Documents/Yandex.Disk/BioInstitute/banding/banding/src/test/resources/ref.txt";
        String query = "/Users/alkli/Documents/Yandex.Disk/BioInstitute/banding/banding/src/test/resources/query.txt";

        Deque<Interval> referenceIntervals = new IntervalReader(reference).read();
        Deque<Interval> queryIntervals = new IntervalReader(query).read();

        assertThat(JaccardTest.computeJaccardStatisticForChromosome(queryIntervals, referenceIntervals), is(closeTo(0.672, 0.001)));
    }

    @Test
    public void shouldComputeJaccardMetricOnGenome() throws IOException {
        String reference = "/Users/alkli/Documents/Yandex.Disk/BioInstitute/banding/banding/src/test/resources/ref.txt";
        String query = "/Users/alkli/Documents/Yandex.Disk/BioInstitute/banding/banding/src/test/resources/query.txt";

        Deque<Interval> referenceIntervals = new IntervalReader(reference).read();
        Deque<Interval> queryIntervals = new IntervalReader(query).read();

        Genome genomeReference = new Genome();
        long startIndexReference = referenceIntervals.getFirst().getStartIndex();
        long endIndexReference = referenceIntervals.getLast().getEndIndex();
        genomeReference.addChromosome(new Chromosome("chr1",
                new Track(referenceIntervals),
                startIndexReference,
                endIndexReference));

        Genome genomeQuery = new Genome();
        long startIndexQuery = queryIntervals.getFirst().getStartIndex();
        long endIndexQuery = queryIntervals.getLast().getEndIndex();
        genomeQuery.addChromosome(new Chromosome("chr1",
                new Track(queryIntervals),
                startIndexQuery,
                endIndexQuery));
        assertThat(JaccardTest.computeJaccardStatisticForGenome(genomeReference, genomeQuery), is(closeTo(0.672, 0.001)));
    }

    @Test
    public void shouldComputeUnionValueForChromosome() {
        Track referenceChr1 = new Track();
        referenceChr1.addInterval(new Interval(5, 55));
        Track referenceChr2 = new Track();
        referenceChr2.addInterval(new Interval(204, 255));
        HashMap<String, Track> referenceMap = new HashMap<>();
        referenceMap.put("Chr1", referenceChr1);
        referenceMap.put("Chr2", referenceChr2);

        Track queryChr1 = new Track();
        queryChr1.addInterval(new Interval(5, 20));
        queryChr1.addInterval(new Interval(27, 42));
        queryChr1.addInterval(new Interval(47, 62));
        Track queryChr2 = new Track();
        queryChr2.addInterval(new Interval(197, 212));
        queryChr2.addInterval(new Interval(219, 234));
        queryChr2.addInterval(new Interval(242, 257));
        HashMap<String, Track> queryMap = new HashMap<>();
        queryMap.put("Chr1", queryChr1);
        queryMap.put("Chr2", queryChr2);

        assertThat(JaccardTest.getUnionValueForTrackSet(referenceMap, queryMap), is(119L));
    }

    @Test
    public void shouldComputeIntersectionValueForChromosome() {
        Track referenceChr1 = new Track();
        referenceChr1.addInterval(new Interval(5, 55));
        Track referenceChr2 = new Track();
        referenceChr2.addInterval(new Interval(204, 255));
        HashMap<String, Track> referenceMap = new HashMap<>();
        referenceMap.put("Chr1", referenceChr1);
        referenceMap.put("Chr2", referenceChr2);

        Track queryChr1 = new Track();
        queryChr1.addInterval(new Interval(5, 20));
        queryChr1.addInterval(new Interval(27, 42));
        queryChr1.addInterval(new Interval(47, 62));
        Track queryChr2 = new Track();
        queryChr2.addInterval(new Interval(197, 212));
        queryChr2.addInterval(new Interval(219, 234));
        queryChr2.addInterval(new Interval(242, 257));
        HashMap<String, Track> queryMap = new HashMap<>();
        queryMap.put("Chr1", queryChr1);
        queryMap.put("Chr2", queryChr2);

        assertThat(JaccardTest.getIntersectionValueForTrackSet(referenceMap, queryMap), is(80L));
    }

    @Test
    public void shouldComputeJaccardMetricForChromosomes() {
        Track referenceChr1 = new Track();
        referenceChr1.addInterval(new Interval(5, 55));
        Track referenceChr2 = new Track();
        referenceChr2.addInterval(new Interval(204, 255));
        HashMap<String, Track> referenceMap = new HashMap<>();
        referenceMap.put("Chr1", referenceChr1);
        referenceMap.put("Chr2", referenceChr2);

        Track queryChr1 = new Track();
        queryChr1.addInterval(new Interval(5, 20));
        queryChr1.addInterval(new Interval(27, 42));
        queryChr1.addInterval(new Interval(47, 62));
        Track queryChr2 = new Track();
        queryChr2.addInterval(new Interval(197, 212));
        queryChr2.addInterval(new Interval(219, 234));
        queryChr2.addInterval(new Interval(242, 257));
        HashMap<String, Track> queryMap = new HashMap<>();
        queryMap.put("Chr1", queryChr1);
        queryMap.put("Chr2", queryChr2);

        assertThat(JaccardTest.computeJaccardStatisticForChromosomeSet(referenceMap, queryMap), is(closeTo(0.672, 0.001)));
    }

    @Test
    public void jaccardMetricShouldBeEqual1ForIdenticalTrack() {
        Deque<Interval> intervals = new ArrayDeque<>();
        intervals.add(new Interval(5, 20));
        intervals.add(new Interval(27, 42));
        intervals.add(new Interval(47, 62));
        intervals.add(new Interval(197, 212));
        intervals.add(new Interval(219, 234));
        intervals.add(new Interval(242, 257));
        Track track = new Track(intervals);

        double expectedValue = 1;
        double jaccardStatistic = JaccardTest.computeJaccardStatisticForChromosome(track, track);
        assertThat(jaccardStatistic, is(expectedValue));
    }

}