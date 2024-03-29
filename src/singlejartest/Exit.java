package singlejartest;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IIndicators.AppliedPrice;
import org.jfree.ui.RefineryUtilities;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class Exit implements IStrategy {
    private IEngine engine;
    private IConsole console;
    private IHistory history;
    private IContext context;
    private IChart chart;
    private static IIndicators indicators;
    private static IAccount account;
    private int counter = 0;
    private static int orderCounter = 0;
    private static OfferSide myOfferSide = OfferSide.BID;

    private static Instrument instrument = Instrument.EURUSD;
    private static Period period = Period.ONE_HOUR;
    private Filter indicatorFilter = Filter.ALL_FLATS;
    public double amount = 0.001;
    public int stopLossPips = 50;
    public int takeProfitPips = 50;
    public int breakEvenPips = 25;
    private int smaTimePeriod_1;
    private int smaTimePeriod_2;
    private String strategyName;

    public Exit(int smaTimePeriod_1, int smaTimePeriod_2, boolean GUITest){
        // smaTimePeriod1 must by smaller than 2
        if(smaTimePeriod_1 < smaTimePeriod_2){
            this.smaTimePeriod_1 = smaTimePeriod_1 * 10;
            this.smaTimePeriod_2 = smaTimePeriod_2 * 10;
        }else{
            this.smaTimePeriod_1 = smaTimePeriod_2 * 10;
            this.smaTimePeriod_2 = smaTimePeriod_1 * 10;
        }

        // create strategy name
        strategyName = smaTimePeriod_1*10 + "/" + smaTimePeriod_2*10;
        Data.addStrategyName(strategyName);

        //  for GUI testing DataCube is not available
        if (!GUITest) {
            period = Data.getPeriod();
            instrument = Data.getInstrument();
        }

        // reset orderCounter for new test
        orderCounter = 0;
    }

    @Override
    public void onStart(IContext context) throws JFException {
        this.engine = context.getEngine();
        this.console = context.getConsole();
        this.history = context.getHistory();
        this.context = context;
        this.account = context.getAccount();
        this.indicators = context.getIndicators();

        // check if chart is open if doesn't print error message if is open add indicators
        chart = context.getLastActiveChart();
        if (chart == null) {
            console.getErr().println("No chart opened!");
            return;
        }
        chart = context.getChart(instrument);
        chart.add(indicators.getIndicator("SMA"), new Object[]{smaTimePeriod_1});
        chart.add(indicators.getIndicator("SMA"), new Object[]{smaTimePeriod_2});
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        // filter right instrument and period
        if (!instrument.equals(instrument) || !period.equals(Exit.period))
            return;

        newOrderLogic(instrument);
        //Data.getOrders(0);
        //setBreakEvent();

        storeEquity(instrument);

    }

    @Override
    public void onStop() throws JFException {
        // close all orders
        for (IOrder order : engine.getOrders()) {
            engine.getOrder(order.getLabel()).close();
            //Data.addOrder(TestMainRepeater.getLoopCounter(), order);
        }

        // store final deposit
        Data.addFinalDeposit(account.getEquity());

        // show graph after last test
        if (SMACrossListGenerator.getFinalNumber()-1 == TestMainRepeater.getLoopCounter()) {

            /** get list of certain number of the best strategies */
            // create list of best results
            Map<Double, Integer> strategies = new HashMap<>();
            Data.getFinalDepositList();

            ArrayList<Double> list = (ArrayList<Double>)Data.getFinalDepositList();
            for(int i = 0; i < list.size(); i++){
                strategies.put(Data.getFinalDeposit(i), i);
            }


            // sort list
            SortedMap<Double, Integer> sortedCache = new TreeMap<>(Collections.reverseOrder());
            sortedCache.putAll(strategies);

            // store sorted list of best strategies to the Data
            ArrayList<Integer> list2 =  new ArrayList<>(sortedCache.values());
            for(int i = 0; i < sortedCache.size(); i++){
                Data.addBestResultsIndex(list2.get(i));
            }

            strategies.forEach((x,y) -> System.out.println(x + "  " + y));
            System.out.println("---------------------------------- SORTED ---------------------------------");
            //print sorted list
            ArrayList<Integer> list3 = (ArrayList<Integer>) Data.getBestResults();
            list3.forEach((x) -> System.out.println(x+1 + " " + Data.getFinalDeposit(x) ));







            // show equity graph
            Chart chart = new Chart();
            chart.pack();
            RefineryUtilities.centerFrameOnScreen(chart);
            chart.setVisible(true);
        }

        //get correct number of valid orders
        int numOfOrders = 0;
        int profitOrders = 0;
        int lossOrders = 0;
        double avrgCommission=0;
        for (IOrder order: Data.getOrders(TestMainRepeater.getLoopCounter())) {
            if (order.getState().name() == "CLOSED" || order.getState().name() == "FILLED"){
                numOfOrders++;
                avrgCommission += order.getCommissionInUSD();
            }
            if (order.getProfitLossInUSD() > 0)
                lossOrders++;
            if (order.getProfitLossInUSD() < 0)
                profitOrders++;
        }



        DecimalFormat df = new DecimalFormat("#.##");
        String successRate = df.format((double)profitOrders/((double)numOfOrders/100));
        avrgCommission = Double.parseDouble(df.format(avrgCommission/numOfOrders));

        // write down to file and gui console starting line
        WriteToFile.writeDown(String.valueOf("\n" + TestMainRepeater.getMaActual_1() + "\t\t" + getEquity()) + "\t\t" + orderCounter, true);
        TestMainRepeater.printToConsoleTextArea(
                "\n" + Data.getStrategyName(TestMainRepeater.getLoopCounter()) +
                        "\t" + getEquity() +
                        "\t" + successRate + "%" +
                        "\t" + numOfOrders +
                        "\t" + avrgCommission
        );
    }

    @Override
    public void onMessage(IMessage message) throws JFException {
        //if(message.getOrder() != null) printMe("order: " + message.getOrder().getLabel() + " || message content: " + message.getContent());
    }

    @Override
    public void onAccount(IAccount account) throws JFException {
    }

    @Override
    public void onTick(Instrument instrument, ITick tick) throws JFException {
        if (!instrument.equals(instrument)) {
            return;
        }
    }



    private double [] MA_1;
    private double [] MA_2;
    private IOrder order = null;
    boolean ma1IsBiggerNew;
    boolean ma1IsBiggerOld;
    /* creating new orders logic */
    public void newOrderLogic(Instrument instrument) throws JFException {
        IBar prevBar = history.getBar(instrument, period, OfferSide.BID, 1);
        MA_1 = indicators.sma(instrument, period, OfferSide.BID, AppliedPrice.CLOSE, smaTimePeriod_1,
                indicatorFilter, 3, prevBar.getTime(), 0);
        MA_2 = indicators.sma(instrument, period, OfferSide.BID, AppliedPrice.CLOSE, smaTimePeriod_2,
                indicatorFilter, 3, prevBar.getTime(), 0);

        // load new value
        if (MA_1[0] > MA_2[0]) {
            ma1IsBiggerNew = true;
        }else{
            ma1IsBiggerNew = false;
        }

        // load old value
        if (MA_1[2] > MA_2[2]) { ma1IsBiggerOld = true; }
        else{ ma1IsBiggerOld = false; }

        if(ma1IsBiggerNew != ma1IsBiggerOld){
            // SMA10 crossover SMA90 from UP to DOWN
            if (MA_1[0] < MA_2[0]) {
                if (engine.getOrders().size() > 0) {
                    for (IOrder orderInMarket : engine.getOrders()) {
                        //Data.addOrder(TestMainRepeater.getLoopCounter(), orderInMarket);
                        orderInMarket.close();
                    }
                }
                if ((order == null) || (order.isLong() && order.getState().equals(IOrder.State.CLOSED)) ) {
                    print("Create SELL");
                    submitOrder(OrderCommand.SELL);
                }
                ma1IsBiggerOld = false;
            }
            // SMA10 crossover SMA90 from DOWN to UP
            if (MA_1[0] > MA_2[0]) {
                if (engine.getOrders().size() > 0) {
                    for (IOrder orderInMarket : engine.getOrders()) {
                        //Data.addOrder(TestMainRepeater.getLoopCounter(), orderInMarket);
                        orderInMarket.close();
                    }
                }
                if ((order == null) || (order.isLong() && order.getState().equals(IOrder.State.CLOSED)) ) {
                    print("Create BUY");
                    submitOrder(OrderCommand.BUY);
                }
                ma1IsBiggerOld = true;
            }
        }
    }

    boolean dailyChecker = false;
    /* at 22 oclock store equity */
    public void storeEquity(Instrument instrument) throws JFException {
        // return hour
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("HH");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        long lastTickTime = history.getLastTick(instrument).getTime();
        String hour = sdf.format(lastTickTime);

        // at 22 hours store balance
        if (hour.equals("22") && dailyChecker == false){
            dailyChecker = true;
            //System.out.println("Balance storing");
            Data.addDailyEquity(TestMainRepeater.getLoopCounter(), getEquity());
        }else if (hour.equals("23")){
            dailyChecker = false;
        }
    }

    private void print(String toPrint){
        console.getOut().println(toPrint);
    }

    public static double getEquity() {
        return account.getEquity();
    }

    private void setBreakEvent(){
        try {
            for(IOrder order : engine.getOrders()){

                // if its in profit and new sl is bigger ther old one
                if(order.getProfitLossInUSD() > 0 && order.getOpenPrice() > order.getStopLossPrice()){

                    // and if is profit bigger than require value of break even
                    if( order.getProfitLossInPips() >= breakEvenPips){
                        //console.getOut().println("Order has profit of " + order.getProfitLossInPips() + " pips! Moving the stop loss to the open price." );

                        // set SL to the starting value
                        order.setStopLossPrice(order.getOpenPrice());

                        System.out.println("ORDER label : " + order.getLabel());
                        System.out.println("ORDER commision : " + order.getCommissionInUSD());
                        System.out.println("ORDER profit : " + order.getProfitLossInUSD());
                    }
                }
            }
        } catch (JFException e) {
            e.printStackTrace();
        }
    }

    /** @return price of certain number of pip */
    private double getPipPrice(int pips) {
        return pips * instrument.getPipValue();
    }

    private IOrder submitOrder(IEngine.OrderCommand orderCmd) throws JFException {

        double stopLossPrice, takeProfitPrice;

        // Calculating stop loss and take profit prices
        if (orderCmd == OrderCommand.BUY) {

            //console.getOut().println("BUY");
            stopLossPrice = history.getLastTick(instrument).getBid() - getPipPrice(stopLossPips);
            takeProfitPrice = history.getLastTick(instrument).getBid() + getPipPrice(takeProfitPips);
        } else {
            //console.getOut().println("SELL");
            stopLossPrice = history.getLastTick(instrument).getAsk() + getPipPrice(stopLossPips);
            takeProfitPrice = history.getLastTick(instrument).getAsk() - getPipPrice(takeProfitPips);
        }

        IOrder order = engine.submitOrder(getLabel(instrument), instrument, orderCmd, getAmount(), 0, 20, 0, 0);
        Data.addOrder(TestMainRepeater.getLoopCounter(), order);

        orderCounter++;

        // Submitting an order for the specified getInstrument at the current market price
        return order;
    }

    /** return amount of lots with a risk two percents */
    private double getAmount() throws JFException {

        // formula from internet
        IBar bar2 = history.getBar(instrument, period, myOfferSide, 1);
        double units = (((account.getEquity() * 0.01) / (1/ bar2.getClose())) / stopLossPips )  *(10000/1);
        console.getOut().println((units/100000));


        // mine formula show the same ?!?!?!
        IBar bar = history.getBar(instrument, period, myOfferSide, 1);
        double equityInUSD = getEquity() * bar.getClose();
        double lots = (equityInUSD * 0.01) / stopLossPips;
        // transfer microlots to lots
        lots /= 10;
        console.getOut().println(lots);
        return lots;
    }

    private String getLabel(Instrument instrument) {
        String label = instrument.name();
        label = label + (counter++);
        label = label.toUpperCase();
        return label;
    }
}