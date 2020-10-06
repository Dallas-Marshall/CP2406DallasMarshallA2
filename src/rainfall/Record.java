package rainfall;

/**
 * A Record contains the analysed rainfall data specific to a single month in a
 * specific year that comes from a particular rainfall station
 */
public class Record {
    private int year;
    private int month;
    private double total;
    private double min;
    private double max;

    public Record(int year, int month, double total, double min, double max) {
        this.year = year;
        this.month = month;
        this.total = total;
        this.min = min;
        this.max = max;
    } // end Record constructor

    public int getYear() {
        return year;
    } // end getYear

    public int getMonth() {
        return month;
    } // end getMonth

    public double getTotal() {
        return total;
    } // end getTotal

    public double getMin() {
        return min;
    } // end getMin

    public double getMax() {
        return max;
    } // end getMax

} // end class Record