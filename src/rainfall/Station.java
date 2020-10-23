package rainfall;

import java.util.*;

/**
 * An object of class Station represents monthly rainfall data of a station,
 * where each Record is a month's rainfall statistics.
 */
public class Station {

    private final List<Record> rainfallRecords; // List to store Records

    /**
     * Constructor. Create a Station object and initialise ArrayList.
     */
    public Station() {
        rainfallRecords = new ArrayList<>();
    } // end Station constructor

    /**
     * Method to add Record to Station object,
     * all values must be checked valid by program calling the method.
     *
     * @param record The Record object to be added to Station.
     */
    public void addRecord(Record record) {
        rainfallRecords.add(record);
    } // end addRecord

    /**
     * Method to find and return specific Record Object.
     *
     * @param year  The year of the specific Record object.
     * @param month The month of the specific Record object.
     * @return The specific Record object if it exists in Station,
     * otherwise returns null.
     */
    public Record getRecord(int year, int month) {
        for (Record record : rainfallRecords) {
            if (record.getYear() == year && record.getMonth() == month) {
                return record;
            }
        }
        return null;
    } // end getRecord

    /**
     * Method to find and return Record object at position i in Station.
     *
     * @param i The index of the requested Record object.
     * @return The Record object at position i in Station.
     * @throws ArrayIndexOutOfBoundsException for values of i, that are out of bounds.
     */
    public Record getRecord(int i) {
        if (i >= rainfallRecords.size()) {
            throw new ArrayIndexOutOfBoundsException("Index %i, is Invalid");
        } else {
            return rainfallRecords.get(i);
        }
    } // end getRecord

    /**
     * Method finds the number of Records in Station object.
     *
     * @return Number of Records in Station object.
     */
    public int getNumberOfRecords() {
        return rainfallRecords.size();
    } // end numberOfRecords

    /**
     * Method finds the highest monthly rainfall from Station object.
     *
     * @return The highest monthly rainfall value.
     */
    public double getMaxRainfallValue() {
        double maxRainfall = Double.NEGATIVE_INFINITY;
        for (Record record : rainfallRecords) {
            if (record.getTotal() > maxRainfall) maxRainfall = record.getTotal();
        }
        return maxRainfall;
    } // end getMaxRainfallValue

} // end class Station
