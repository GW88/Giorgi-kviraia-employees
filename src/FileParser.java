
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class FileParser {

    // map containing text file data
    private final Map<Long, Set<EmpData>> map;
    //previous read character
    private char prevChar;
    // reader for the input
    private Reader reader;
    //delimiter  to seperate columns in file
    private char delimiter;
    // flag that indicates end of file
    private boolean eof;
    // list for date formats
    private static List<SimpleDateFormat> date_pattern = new ArrayList<>();

    // supported date formats.
    static {
        date_pattern.add(new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z"));
        date_pattern.add(new SimpleDateFormat("EEE MMM d ''yy"));
        date_pattern.add(new SimpleDateFormat("h:mm a"));
        date_pattern.add(new SimpleDateFormat("hh 'o''clock' a  zzzz"));
        date_pattern.add(new SimpleDateFormat("K:mm a  z"));
        date_pattern.add(new SimpleDateFormat("yyyyy.MMMMM.dd GGG hh:mm aaa"));
        date_pattern.add(new SimpleDateFormat("EEE d MMM yyyy HH:mm:ss Z"));
        date_pattern.add(new SimpleDateFormat("yyMMddHHmmssZ"));
        date_pattern.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
        date_pattern.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
        date_pattern.add(new SimpleDateFormat("YYYY-'W'ww-u"));
        date_pattern.add(new SimpleDateFormat("EEE dd MMM yyyy HH:mm:ss z"));
        date_pattern.add(new SimpleDateFormat("EEE dd MMM yyyy HH:mm zzzz"));
        date_pattern.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ"));
        date_pattern.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSzzzz"));
        date_pattern.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sszzzz"));
        date_pattern.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss z"));
        date_pattern.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssz"));
        date_pattern.add(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss"));
        date_pattern.add(new SimpleDateFormat("yyyy-MM-dd'T'HHmmss.SSSz"));
        date_pattern.add(new SimpleDateFormat("yyyy-MM-dd"));
        date_pattern.add(new SimpleDateFormat("yyyyMMdd"));
        date_pattern.add(new SimpleDateFormat("dd/MM/yy"));
        date_pattern.add(new SimpleDateFormat("dd/MM/yyyy"));
        date_pattern.add(new SimpleDateFormat("dd/MM/yyyy"));
        date_pattern.add(new SimpleDateFormat("MMM d YYYY HH:mm:ss"));
    }


    /**
     * Constructor a FileParser obejct from Reader
     *
     * @param reader A Reader Object
     */
    FileParser(Reader reader) {
        this(reader, ',');
    }

    /**
     * Construct a FileParser object from Reader and delimiter.
     *
     * @param reader    A Reader Object
     * @param delimiter a char seperatijng columns.
     *                  Note: delimiter can not be same delimiter that contains a format string.
     *                  for example dot (.) or (/) forward slash
     */
    FileParser(Reader reader, char delimiter) {
        map = new HashMap<>();
        this.reader = reader.markSupported() ? reader : new BufferedReader(reader);
        this.prevChar = 0;
        this.delimiter = delimiter;
        this.eof = false;

        // bind data
        init();
        try {
            this.reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void init() {

        Object key;
        int column = 0;
        Object[] obj = new Object[4];

        for (; ; ) {
            key = nextValue();
            if (key == null)
                return;
            obj[column] = key;
            column++;

            if (column == 4) {

                column = 0;
                Date end = null;
                Date start = null;
                long empID = (long) obj[0];
                long projectID = (long) obj[1];
                if (obj[3] instanceof Date)
                    end = (Date) obj[3];
                if (obj[2] instanceof Date)
                    start = (Date) obj[2];


                // save the employee ID and days worked.
                EmpData emp = new EmpData(empID, projectID, start, end);

                if (this.map.containsKey(projectID)) {
                    this.map.get(projectID).add(emp);
                } else {
                    Set<EmpData> val = new LinkedHashSet<>();
                    val.add(emp);
                    this.map.put((Long) obj[1], val);
                }

            }

        }
    }


    /**
     * Get the next character in the stream.
     *
     * @return The next character, or 0 if past the end of the source string.
     * @throws RuntimeException if there is an error while reading stream.
     */
    private char next() {
        int c;

        try {
            c = this.reader.read();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        if (c <= 0) {
            this.eof = true;
            return 0;
        }
        this.prevChar = (char) c;
        return this.prevChar;
    }


    /**
     * get the next char, skipping delimiter and new line.
     *
     * @return A character.
     */
    private char nextClean() {
        for (; ; ) {
            char c = this.next();
            if (c != this.delimiter && c != '\r' && c != '\n') {
                return c;
            }
        }
    }


    /**
     * get the next value, value can be number or date
     *
     * @return An object
     */
    private Object nextValue() {
        char c = this.nextClean();
        String string;

        if (this.eof)
            return null;

        StringBuilder sb = new StringBuilder();
        while (c != this.delimiter && c != '\r' && c != '\n' && c != 0) {
            sb.append(c);
            c = this.next();
        }

        // remove leading and trailing spaces
        string = sb.toString().replaceAll("^\\s+", "");
        string = string.replaceAll("\\s+$", "");

        return toObject(string);
    }


    /**
     * convert give string to date object using SimpleDateFormat.
     *
     * @param val A string to be converted
     * @return A Date object or null.
     */
    private static Date toDate(final String val) {

        for (SimpleDateFormat pattern : date_pattern) {
            try {
                pattern.setLenient(false);
                return new Date(pattern.parse(val).getTime());
            } catch (ParseException ignore) {
                // ignore exception
            }
        }
        return null;
    }


    /**
     * convert string to number or date
     *
     * @param val A string to be converted
     * @return a Number or Date Object,  value  == null then return current date.
     */
    private static Object toObject(final String val) {

        if (val.equals(""))
            return val;
        if ("null".equalsIgnoreCase(val))
            return new Date();

        if (isNumber(val))
            return Long.valueOf(val);

        return toDate(val.trim());
    }

    /**
     * check if given string is number
     *
     * @param val a string to test
     * @return A boolean true or false.
     */
    private static boolean isNumber(final String val) {
        try {
            Long.parseLong(val);
        } catch (NumberFormatException ignore) {
            return false;
        }
        return true;
    }


    /**
     * method will finds the pair of employees that have worked as a team for the longest time at the same projects.
     */
    Map<Long, List<Vector<Long>>> getData() {

        if (this.map.isEmpty())
            return new LinkedHashMap<>();
        Map<Long, List<Vector<Long>>> longListMap = new LinkedHashMap<>();
        for (Map.Entry<Long, Set<EmpData>> data : this.map.entrySet()) {

            if (data.getValue().size() > 1) {

                List<EmpData> empData = new ArrayList<>(data.getValue());
                Vector<Long> longVector;
                for (int i = 0; i < empData.size(); i++) {

                    for (int c = i + 1; c < empData.size(); c++) {
                        longVector = new Vector<>();
                        longVector.add(empData.get(i).getEmpId());
                        longVector.add(empData.get(c).getEmpId());
                        longVector.add(data.getKey());
                        long diff = calculate_diff(empData.get(i), empData.get(c));
                        longVector.add(diff);
                        if (diff != -1) {
                            if (longListMap.containsKey(data.getKey())) {
                                List<Vector<Long>> v = longListMap.get(data.getKey());
                                Vector<Long> l = v.get(0);
                                if (l.get(3) < diff) {
                                    l.clear();
                                    l.addAll(longVector);
                                }

                            } else {
                                List<Vector<Long>> lo = new ArrayList<>();
                                lo.add(longVector);
                                longListMap.put(data.getKey(), lo);
                            }
                        }

                    }
                }

            }
        }

        return longListMap;
    }


    /**
     * calculate days worked together between 2 employees.
     *
     * @param empData1 Employee object
     * @param empData2 Employee object
     * @return calculated number of days worked together
     */
    private long calculate_diff(EmpData empData1, EmpData empData2) {
        long worked_together = -1;
        Date dFrom = empData1.getDateFrom();
        Date dTo = empData1.getDateTo();
        Date dFrom1 = empData2.getDateFrom();
        Date dTo1 = empData2.getDateTo();

        boolean overLaps = (dFrom.getTime() <= dTo1.getTime()) && (dTo.getTime() >= dFrom1.getTime());

        if (overLaps) {

            Date start = dFrom.getTime() > dFrom1.getTime() ? dFrom : dFrom1;
            Date end = dTo.getTime() < dTo1.getTime() ? dTo : dTo1;

            worked_together = TimeUnit.DAYS.convert(end.getTime() - start.getTime(), TimeUnit.MILLISECONDS);


        }

        return worked_together;
    }


    /**
     * Get the map containing data from file
     *
     * @return An map object
     */
    public Map<Long, Set<EmpData>> getMap() {
        return map;
    }


    @Override
    public String toString() {
        return "FileParser{" +
                "map=" + map +
                '}';
    }
}
