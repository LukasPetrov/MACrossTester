package singlejartest;


import com.dukascopy.api.Instrument;
import com.dukascopy.api.LoadingProgressListener;
import com.dukascopy.api.system.ISystemListener;
import com.dukascopy.api.system.ITesterClient;
import com.dukascopy.api.system.TesterFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

public class TestMainRepeater {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static GUI gui;
    private static long strategyId;
    private static ITesterClient client;
    private static Date dateFrom = null;
    private static Date dateTo = null;
    private static String jnlpUrl = "http://platform.dukascopy.com/demo/jforex.jnlp";
    private static String userName = "DEMO3TiMJp";
    private static String password = "TiMJp";
    private static String reportsFileLocation = "C:\\Users\\lukas\\Desktop\\Exit\\Reports\\report.html";
    private static int loopCount = 0;
    private static int openingDeposit = 1000;
    private static int smaTimePeriodFrom = 50;
    private static int smaTimePeriodTo = 100;
    private static int smaTimePeriod = smaTimePeriodFrom;
    private static int parameterIncreaseSize = 10;
    public static ArrayList<ArrayList<Double>> equitiesStorage = new ArrayList<>();


    public static void main(String[] args) {
        // start dialog and get parameters for test
        gui = new GUI();
    }

    public static void startStrategy() throws Exception {
        client = TesterFactory.getDefaultInstance();

        setSystemListener();
        tryToConnect();
        subscribeToInstruments();
        client.setInitialDeposit(Exit.getMyInstrument().getSecondaryJFCurrency(), openingDeposit);

        loadData();

        //LOGGER.info("Starting strategy");
        // run strategy
        client.startStrategy(new Exit(smaTimePeriod), getLoadingProgressListener());
    }

    private static void setSystemListener() {
        client.setSystemListener(new ISystemListener() {
            @Override
            public void onStart(long processId) {
                //LOGGER.info("Strategy started: " + processId);
                strategyId = processId;
            }

            @Override
            public void onStop(long processId) {
                if (smaTimePeriod <= smaTimePeriodTo - parameterIncreaseSize) {
                    //LOGGER.info("Strategy started: " + processId);
                    strategyId = processId;

                    // loop counter
                    TestMainRepeater.loopCount ++;

                    // creating report
                    File reportFile = new File(reportsFileLocation);
                    try {
                        client.createReport(processId, reportFile);
                    } catch (Exception e) {
                        LOGGER.error(e.getMessage(), e);
                    }

                    //start strategy again
                    if (client.getStartedStrategies().size() == 0) {
                        try {
                            // each new start increases the value smaTimePeriod by parameterIncreaseSize
                            smaTimePeriod += parameterIncreaseSize;

                            startStrategy();
                        } catch (Exception a) {

                        }
                    }
                }
            }

            @Override
            public void onConnect() {
                LOGGER.info("Connected");
            }

            @Override
            public void onDisconnect() {
                //tester doesn't disconnect
            }
        });
    }

    private static void tryToConnect() throws Exception {
        LOGGER.info("Connecting...");
        //connect to the server using jnlp, user name and password
        //connection is needed for data downloading
        client.connect(jnlpUrl, userName, password);

        //wait for it to connect
        int i = 10; //wait max ten seconds
        while (i > 0 && !client.isConnected()) {
            Thread.sleep(1000);
            i--;
        }
        if (!client.isConnected()) {
            LOGGER.error("Failed to connect Dukascopy servers");
            System.exit(1);
        }
    }

    private static void subscribeToInstruments() {
        //set instruments that will be used in testing
        Set<Instrument> instruments = new HashSet<>();
        instruments.add(Exit.getMyInstrument());
        LOGGER.info("Subscribing instruments...");
        client.setSubscribedInstruments(instruments);
    }

    private static void loadData() throws InterruptedException, java.util.concurrent.ExecutionException {
        //load data
        LOGGER.info("Downloading data");
        Future<?> future = client.downloadData(null);
        client.setDataInterval(ITesterClient.DataLoadingMethod.DIFFERENT_PRICE_TICKS, dateFrom.getTime(), dateTo.getTime());
        //wait for downloading to complete
        future.get();
    }

    public static LoadingProgressListener getLoadingProgressListener() {
        return new LoadingProgressListener() {
            @Override
            public void dataLoaded(long startTime, long endTime, long currentTime, String information) {
                LOGGER.info(information);
            }

            @Override
            public void loadingFinished(boolean allDataLoaded, long startTime, long endTime, long currentTime) {
            }

            @Override
            public boolean stopJob() {
                return false;
            }
        };
    }

    public static void printToConsoleTextArea(String string){
        gui.printToConsoleTextArea(string);
    }


    public static int getOpeningDeposit() {
        return openingDeposit;
    }

    public static int getSmaTimePeriodFrom() {
        return smaTimePeriodFrom;
    }

    public static int getSmaTimePeriodTo() {
        return smaTimePeriodTo;
    }

    public static void setOpeningDeposit(int openingDeposit) {
        TestMainRepeater.openingDeposit = openingDeposit;
    }

    public static void setSmaTimePeriodFrom(int smaTimePeriodFrom) {
        TestMainRepeater.smaTimePeriodFrom = smaTimePeriodFrom;
    }

    public static void setSmaTimePeriodTo(int smaTimePeriodTo) {
        TestMainRepeater.smaTimePeriodTo = smaTimePeriodTo;
    }

    public static Date getDateFrom() {
        return dateFrom;
    }

    public static Date getDateTo() {
        return dateTo;
    }

    public static int getLoopCount() {
        return loopCount;
    }

    public static void setDateFrom(Date dateFrom) {
        TestMainRepeater.dateFrom = dateFrom;
    }

    public static void setDateTo(Date dateTo) {
        TestMainRepeater.dateTo = dateTo;
    }

    public static int getSmaTimePeriod() {
        return smaTimePeriod;
    }

    public static int getParameterIncreaseSize() {
        return parameterIncreaseSize;
    }

    public static void setSmaTimePeriod(int smaTimePeriod) {
        TestMainRepeater.smaTimePeriod = smaTimePeriod;
    }

    public static void setParameterIncreaseSize(int parameterIncreaseSize) {
        TestMainRepeater.parameterIncreaseSize = parameterIncreaseSize;
    }

    public static ArrayList<ArrayList<Double>> getEquitiesStorage() {
        return equitiesStorage;
    }

}

