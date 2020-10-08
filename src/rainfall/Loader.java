package rainfall;

import textio.TextIO;

public class Loader {

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
                analyseDataset(pathToRainfallData);
            } catch (AnalysisException error) { // error analysing raw data
                throw new LoaderException(error.getMessage());
            }

            TextIO.readFile((pathToAnalysedFile)); // load analysed file
        }
        // TODO - return analysed file as station object
        return null;
    } // end load

    private static void analyseDataset(String pathToRainfallData) throws AnalysisException {
        //TODO - Analyse File and save to file
    } // end analyseDataset

    public static class LoaderException extends Exception {
        public LoaderException(String message) {
            super(message);
        }
    } // end class LoaderException

    public static class AnalysisException extends Exception {
        public AnalysisException(String message) { super(message); }
    } // end class AnalysisException

} // end class Loader
