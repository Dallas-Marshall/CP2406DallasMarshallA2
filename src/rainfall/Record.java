package rainfall;

/**
 * An object of class Record represents a month's rainfall data.
 */
public class Record {
    private final int year;
    private final int month;
    private final double total;
    private final double min;
    private final double max;

    /**
     * Create a Record object and store values,
     * all values must be checked valid by program calling the constructor.
     *
     * @param year  The year the rainfall data was recorded.
     * @param month The month the rainfall data was recorded.
     * @param total The total rainfall in mm for the specified month.
     * @param min   The minimum rainfall recorded for the specified month.
     * @param max   The maximum rainfall recorded for the specified month.
     */
    public Record(int year, int month, double total, double min, double max) {
        this.year = year;
        this.month = month;
        this.total = total;
        this.min = min;
        this.max = max;
    } // end Record constructor

    /**
     * @return The value of year.
     */
    public int getYear() {
        return year;
    } // end getYear

    /**
     * @return The value of month.
     */
    public int getMonth() {
        return month;
    } // end getMonth

    /**
     * @return The value of total rainfall.
     */
    public double getTotal() {
        return total;
    } // end getTotal

    /**
     * @return The value of minimum rainfall.
     */
    public double getMin() {
        return min;
    } // end getMin

    /**
     * @return The value of maximum rainfall.
     */
    public double getMax() {
        return max;
    } // end getMax

    /**
     * @return A string representation of Record in CSV format.
     */
    public String toString() {
        return String.format("%d,%d,%1.2f,%1.2f,%1.2f", year, month, total, min, max);
    } // end toString

} // end class Record