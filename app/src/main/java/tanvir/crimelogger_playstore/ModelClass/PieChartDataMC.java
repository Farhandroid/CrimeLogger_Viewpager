package tanvir.crimelogger_playstore.ModelClass;

/**
 * Created by USER on 20-Jan-18.
 */

public class PieChartDataMC {

    String crimeType;
    int crimeNumber;

    public PieChartDataMC(String crimeType, int crimeNumber) {
        this.crimeType = crimeType;
        this.crimeNumber = crimeNumber;
    }

    public String getCrimeType() {
        return crimeType;
    }

    public int getCrimeNumber() {
        return crimeNumber;
    }
}
