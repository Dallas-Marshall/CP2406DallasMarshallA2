package rainfall;

import textio.TextIO;

import java.io.File;

public class Loader {

    public static Station load(String directoryName, String stationName) throws LoaderException {
        // Check valid input
        if (directoryName.strip().equals("")) {
            throw new LoaderException("empty directory name");
        } else if (stationName.strip().equals("")) {
            throw new LoaderException("empty station name");
        }

        // Create analysedCSVFile
        String pathToAnalysedCSVFile = String.format("%s/%s_analysed.csv", directoryName, stationName);
        File analysedCSVFile = new File(pathToAnalysedCSVFile);

        if (!analysedCSVFile.exists()) {

            // Create rawDataCSVFile
            String pathToRawDataCSVFile = String.format("%s/%s.csv", directoryName, stationName);
            File rawDataCSVFile = new File(pathToRawDataCSVFile);

            if (!rawDataCSVFile.exists()) throw new LoaderException("rainfall file not found");

            // Analyse rawDataCSVFile
            TextIO.readFile(pathToRawDataCSVFile);
            try {
                initialiseOutFile(pathToRawDataCSVFile);
                analyseDataset(pathToAnalysedCSVFile);
            } catch (AnalysisException error) { // error analysing raw data
                throw new LoaderException(error.getMessage());
            }
        }
        // load analysed file
        TextIO.readFile((pathToAnalysedCSVFile));
        Station station = loadStation();
        if (station == null) throw new LoaderException("empty analysedCSVFile");
        return station;
    } // end load


    private static Station loadStation() {
        return null; // TODO - Create Records and load into Station
    } // end loadStation


    private static void analyseDataset(String pathToAnalysedCSVFile) throws AnalysisException {
        // Set index of values
        final int INDEX_OF_YEAR = 2;
        final int INDEX_OF_MONTH = 3;
        final int INDEX_OF_DAY = 4;
        final int INDEX_OF_RAINFALL_MEASUREMENT = 5;

        // Check file is not empty and remove header
        if (readNextLine() == null) throw new AnalysisException("empty rawDataCSVFile");

        // Set write file and write header
        TextIO.writeFile(pathToAnalysedCSVFile);
        TextIO.putln("year,month,total,min,max");

        // Set tracking variables with sentinel values
        double monthlyRainfallTotal = 0.0;
        double monthlyRainfallMin = Double.POSITIVE_INFINITY;
        double monthlyRainfallMax = Double.NEGATIVE_INFINITY;
        int currentMonth = 1;
        int currentYear = 0;

        // Read first raw rainfall data line
        String[] rainfallRecord = readNextLine();
        while (rainfallRecord != null) {

            // Replace blank rainfall readings with 0.0
            if (rainfallRecord[INDEX_OF_RAINFALL_MEASUREMENT].equals("")) {
                rainfallRecord[INDEX_OF_RAINFALL_MEASUREMENT] = String.valueOf(0.0);
            }

            // Extract values
            int day = Integer.parseInt(rainfallRecord[INDEX_OF_DAY]);
            int year = Integer.parseInt(rainfallRecord[INDEX_OF_YEAR]);
            int month = Integer.parseInt(rainfallRecord[INDEX_OF_MONTH]);
            double rainfallMeasurement = Double.parseDouble(rainfallRecord[INDEX_OF_RAINFALL_MEASUREMENT]);

            // Update Sentinel Value
            if (currentYear == 0) currentYear = year;

            // Check Valid values
            if (day < 1 || day > 31) throw new AnalysisException("illegal day in rawDataCSVFile: " + day);
            if (month < 1 || month > 12) throw new AnalysisException("illegal month in rawDataCSVFile: " + month);
            if (year < 1000 || year > 9999) throw new AnalysisException("illegal year in rawDataCSVFile: " + year);

            if (month != currentMonth) {
                // Print to file
                printToFile(currentYear, currentMonth, monthlyRainfallTotal, monthlyRainfallMin, monthlyRainfallMax);

                // Reset tracking variables with sentinel values
                monthlyRainfallTotal = 0.0;
                monthlyRainfallMin = Double.POSITIVE_INFINITY;
                monthlyRainfallMax = Double.NEGATIVE_INFINITY;

                // Update new month/year
                currentMonth = month;
                currentYear = year;
            }

            // Update total, min & max
            monthlyRainfallTotal += rainfallMeasurement;
            if (rainfallMeasurement > monthlyRainfallMax) monthlyRainfallMax = rainfallMeasurement;
            if (rainfallMeasurement < monthlyRainfallMin) monthlyRainfallMin = rainfallMeasurement;

            // Read next raw rainfall data line
            rainfallRecord = readNextLine();
        }

        if (TextIO.eof())
            printToFile(currentYear, currentMonth, monthlyRainfallTotal, monthlyRainfallMin, monthlyRainfallMax);
    } // end analyseDataset


    public static class LoaderException extends Exception {
        public LoaderException(String message) {
            super(message);
        }
    } // end class LoaderException


    public static class AnalysisException extends Exception {
        public AnalysisException(String message) {
            super(message);
        }
    } // end class AnalysisException


    private static String[] readNextLine() {
        if (TextIO.eof()) {
            return null;
        } else {
            return TextIO.getln().split(",", -1);
        }
    } // end extractNextMeasurement


    private static void initialiseOutFile(String pathToRainfallData) {
        String fileName = pathToRainfallData.substring(pathToRainfallData.lastIndexOf("/") + 1, pathToRainfallData.lastIndexOf("."));
        String pathToOutFile = String.format("resources/%s_analysed.csv", fileName);
        TextIO.writeFile(pathToOutFile);
    } // end initialiseOutFile


    private static void printToFile(int year, int month, double rainfallTotal, double rainfallMin, double rainfallMax) {
        TextIO.putf("%d,%d,%1.2f,%1.2f,%1.2f\n", year, month, rainfallTotal, rainfallMin, rainfallMax);
    } // end printToFile

} // end class Loader
