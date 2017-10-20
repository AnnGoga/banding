import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayDeque;
import java.util.Queue;

public class IntervalReader {

    private final String path;

    public IntervalReader(String path) {
        this.path = path;
    }

    public Queue<Interval> read() throws IOException {
        Queue<Interval> intervals = new ArrayDeque<>();

        Reader in = new FileReader(path);
        Iterable<CSVRecord> records = CSVFormat.RFC4180
                .withDelimiter('\t')
                .withFirstRecordAsHeader()
                .parse(in);

        for (CSVRecord record : records) {
            Interval interval = new Interval(record.get("chrom"),
                    Integer.valueOf(record.get("chromStart")),
                    Integer.valueOf(record.get("chromEnd")));
            intervals.add(interval);
        }

        return intervals;
    }

    public static void main(String[] args) throws IOException {
        String path = "/Users/alkli/Documents/Yandex.Disk/BioInstitute/banding/banding/src/test/resources/ref.txt";
        Queue<Interval> intervals = new IntervalReader(path).read();
        for (Interval interval: intervals) {
            System.out.println(interval);
        }
    }
}
