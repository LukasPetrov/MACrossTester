package singlejartest;

import com.dukascopy.api.*;
import com.dukascopy.api.IEngine.OrderCommand;
import com.dukascopy.api.IIndicators.AppliedPrice;
import org.jfree.ui.RefineryUtilities;
import java.text.SimpleDateFormat;
import java.util.*;

//import static singlejartest.TestMainRepeater.smaTimePeriod;

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
    private static Instrument myInstrument = Instrument.EURUSD;
    private static OfferSide myOfferSide = OfferSide.BID;
    private static Period myPeriod = Period.ONE_HOUR;
    private Filter indicatorFilter = Filter.ALL_FLATS;
    private boolean oldTrend;
    private boolean newTrend;
    public double amount = 0.001;
    public int stopLossPips = 50;
    public int takeProfitPips = 50;
    public int breakEvenPips = 5;
    double[] sma = new double[3];

    // index for storing equity hiwtory, it starts by 1 because in 0 is strategy number
    public static int equityIndex = 1;

    //public static int smaTimePeriod = 50;

    public Exit(int smaTimePeriod){
        TestMainRepeater.setSmaTimePeriod(smaTimePeriod);
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

        chart = context.getLastActiveChart();
        if (chart == null) {
            console.getErr().println("No chart opened!");
            return;
        }
        chart = context.getChart(myInstrument);
        chart.add(indicators.getIndicator("SMA"), new Object[]{TestMainRepeater.getSmaTimePeriod() });
    }

    @Override
    public void onBar(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        // filter right instrument and period
        if (!instrument.equals(myInstrument) || !period.equals(myPeriod))
            return;

        /*
        System.out.println("LOTS   : " + getAmount());
        System.out.println("EQUITY : " + getEquity());
        System.out.println("ORDERS : " + orderCounter);
        System.out.println("COMMISION : " + submitOrder().getCommissionInUSD());
         */

        newOrder(instrument, period, askBar, bidBar);

        setBreakEvent();

        storeEquity(instrument);
    }

    @Override
    public void onStop() throws JFException {
        // show equity graph
        Chart chart = new Chart("SMATester", "Course of strategies");
        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );

        WriteToFile.writeDown(String.valueOf("\n" + TestMainRepeater.getSmaTimePeriod()  + "\t\t" +getEquity()) + "\t\t" + orderCounter,true);
        TestMainRepeater.printToConsoleTextArea("\n" + TestMainRepeater.getSmaTimePeriod()  + "\t\t" +getEquity() + "\t\t" + orderCounter);
        equityIndex = 1;
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
    }

    /* creating new orders logic */
    public void newOrder(Instrument instrument, Period period, IBar askBar, IBar bidBar) throws JFException {
        // get 2 last indicator values to detect the trend change
        IBar prevBar = history.getBar(instrument, myPeriod, myOfferSide, 1);
        sma[2] = sma[1];
        sma[1] = sma[0];
        sma[0] = indicators.sma(instrument, myPeriod, myOfferSide, AppliedPrice.CLOSE, TestMainRepeater.getSmaTimePeriod() , indicatorFilter, 1, prevBar.getTime(), 0)[0];
        //sma[0] = indicators.sma(instrument, period, myOfferSide, AppliedPrice.CLOSE, smaTimePeriod,0);

        // detect trend
        if (sma[0] < sma[1] && sma[1] < sma[2]) { // downtrend
            oldTrend = newTrend;
            newTrend = false;
        } else if (sma[0] > sma[1] && sma[1]+(sma[1]-sma[2])*2 > sma[2]){ // uptrend
            oldTrend = newTrend;
            newTrend = true;
        }

        // if new trend is starting create order
        if (oldTrend != newTrend){
            if (newTrend == true){
                submitOrder(OrderCommand.BUY);
                //System.out.println("ORDER : " + submitOrder(OrderCommand.BUY).getCommissionInUSD());
            } else {
                submitOrder(OrderCommand.SELL);
                //System.out.println("ORDER : " + submitOrder(OrderCommand.SELL).getCommissionInUSD());
            }
        }
    }

    /* at 22 oclock store equity */
    public void storeEquity(Instrument instrument) throws JFException {
        // return hour
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("HH");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        long lastTickTime = history.getLastTick(instrument).getTime();
        String hour = sdf.format(lastTickTime);

        if (hour.equals("22") ){
            TestMainRepeater.getEquitiesStorage().get(TestMainRepeater.getLoopCount()).add(account.getEquity());
            equityIndex++;
        }
    }

    private void printMe(String toPrint){
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

    private double getPipPrice(int pips) {
        return pips * this.myInstrument.getPipValue();
    }

    private IOrder submitOrder(IEngine.OrderCommand orderCmd) throws JFException {

        double stopLossPrice, takeProfitPrice;

        // Calculating stop loss and take profit prices
        if (orderCmd == OrderCommand.BUY) {

            //console.getOut().println("BUY");
            stopLossPrice = history.getLastTick(myInstrument).getBid() - getPipPrice(stopLossPips);
            takeProfitPrice = history.getLastTick(myInstrument).getBid() + getPipPrice(takeProfitPips);
        } else {
            //console.getOut().println("SELL");
            stopLossPrice = history.getLastTick(myInstrument).getAsk() + getPipPrice(stopLossPips);
            takeProfitPrice = history.getLastTick(myInstrument).getAsk() - getPipPrice(takeProfitPips);
        }

        orderCounter++;

        // Submitting an order for the specified myInstrument at the current market price
        return engine.submitOrder(getLabel(myInstrument), myInstrument, orderCmd, getAmount(), 0, 20, stopLossPrice, takeProfitPrice);
    }

    /** return amount of lots with a risk two percents */
    private double getAmount() throws JFException {

        // formula from internet
        IBar bar2 = history.getBar(myInstrument, myPeriod, myOfferSide, 1);
        double units = (((account.getEquity() * 0.01) / (1/ bar2.getClose())) / stopLossPips )  *(10000/1);
        console.getOut().println((units/100000));


        // mine formula show the same ?!?!?!
        IBar bar = history.getBar(myInstrument, myPeriod, myOfferSide, 1);
        double equityInUSD = getEquity() * bar.getClose();
        double lots = (equityInUSD * 0.01) / stopLossPips;
        // transfer microlots to lots
        lots /= 10;
        console.getOut().println(lots);
        return lots;
    }

    protected String getLabel(Instrument instrument) {
        String label = instrument.name();
        label = label + (counter++);
        label = label.toUpperCase();
        return label;
    }

    public static IIndicators getIndicators() {
        return indicators;
    }

    public static Instrument getMyInstrument() {
        return myInstrument;
    }

    public static OfferSide getMyOfferSide() {
        return myOfferSide;
    }

    public static Period getMyPeriod() {
        return myPeriod;
    }

    public Filter getIndicatorFilter() {
        return indicatorFilter;
    }

    public static void setMyInstrument(Instrument myInstrument) {
        Exit.myInstrument = myInstrument;
    }

    public static void setMyPeriod(Period myPeriod) {
        Exit.myPeriod = myPeriod;
    }

}