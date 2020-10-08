package rainfall;

import textio.TextIO;

public class Loader {
    private static final int INDEX_OF_YEAR = 2;
    private static final int INDEX_OF_MONTH = 3;
    private static final int INDEX_OF_DAY = 4;
    private static final int INDEX_OF_RAINFALL_MEASUREMENT = 5;

    public static Station load(String directoryName, String stationName) throws LoaderException {
        if (directoryName.equals("")) {
            throw new LoaderException("empty directory name");
        } else if (stationName.equals("")) {
            throw new LoaderException("empty station name");
        }

        // load analysed file
        String pathToAnalysedFile = String.format("%s/%s_analysed.csv", directoryName, stationName);
        try {
            TextIO.readFile(pathToAnalysedFile);
        } catch (IllegalArgumentException e) { // analysed file does not exist

            // create analysed file
            String pathToRainfallData = String.format("%s/%s.csv", directoryName, stationName);
            try {
                TextIO.readFile(pathToRainfallData);
                initialiseOutFile(pathToRainfallData);
                analyseDataset(pathToAnalysedFile);
            } catch (AnalysisException error) { // error analysing raw data
                throw new LoaderException(error.getMessage());
            }

            TextIO.readFile((pathToAnalysedFile)); // load analysed file
        }
        // TODO - return analysed file as station object
        return null;
    } // end load


    private static void analyseDataset(String pathToAnalysedFile) throws AnalysisException {
        if (readNextLine() == null) throw new AnalysisException("Empty rainfall data file");

        TextIO.writeFile(pathToAnalysedFile);
        // Set tracking variables with sentinel values
        double monthlyRainfallTotal = 0.0;
        double monthlyRainfallMin = Double.POSITIVE_INFINITY;
        double monthlyRainfallMax = Double.NEGATIVE_INFINITY;
        int currentMonth = 1;
        int currentYear = 0;

        readNextLine(); // Remove header record

        // get first raw rainfall data line
        String[] rainfallRecord = readNextLine();
        while (rainfallRecord != null) {

            // Replace blank rainfall readings with 0.0
            if (rainfallRecord[INDEX_OF_RAINFALL_MEASUREMENT].equals("")) {
                rainfallRecord[INDEX_OF_RAINFALL_MEASUREMENT] = String.valueOf(0.0);
            }

            // Extract values
            int year = Integer.parseInt(rainfallRecord[INDEX_OF_YEAR]);
            int month = Integer.parseInt(rainfallRecord[INDEX_OF_MONTH]);
            double rainfallMeasurement = Double.parseDouble(rainfallRecord[INDEX_OF_RAINFALL_MEASUREMENT]);

            if (currentYear == 0) currentYear = year;




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
        TextIO.putln("year,month,total,min,max");
    } // end initialiseOutFile


    private static void printToFile(int year, int month, double rainfallTotal, double rainfallMin, double rainfallMax) {
        TextIO.putf("%d,%d,%1.2f,%1.2f,%1.2f\n", year, month, rainfallTotal, rainfallMin, rainfallMax);
    } // end printToFile

} // end class Loader
