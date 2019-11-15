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

    // index for storing equity hiwtory, it starts by 1 because in 0 is strategy number
    public static int equityIndex = 1;

    //public static int smaTimePeriod = 50;

    public Exit(int smaTimePeriod_1, int smaTimePeriod_2){
        this.smaTimePeriod_1 = smaTimePeriod_1 * 10;
        this.smaTimePeriod_2 = smaTimePeriod_2 * 10;

        //TestMainRepeater.setMaActual_1(smaTimePeriod_1);

        period = Data.getPeriod();
        instrument = Data.getInstrument();

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

        /*
        System.out.println("LOTS   : " + getAmount());
        System.out.println("EQUITY : " + getEquity());
        System.out.println("ORDERS : " + orderCounter);
        System.out.println("COMMISION : " + submitOrder().getCommissionInUSD());
         */



        newOrderLogic(instrument);

        setBreakEvent();

        storeEquity(instrument);





    }

    @Override
    public void onStop() throws JFException {
        // close all orders
        for (IOrder order : engine.getOrders()) {
            engine.getOrder(order.getLabel()).close();
        }

        // show equity graph
        Chart chart = new Chart("SMATester", "Course of strategies");
        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );

        WriteToFile.writeDown(String.valueOf("\n" + TestMainRepeater.getMaActual_1()  + "\t\t" +getEquity()) + "\t\t" + orderCounter,true);
        TestMainRepeater.printToConsoleTextArea(
                "\n" + TestMainRepeater.getMaActual_1()*10 +
                        "\t" + TestMainRepeater.getMaActual_2()*10 +
                        "\t" +getEquity() +
                        "\t" + orderCounter);
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
        if (!instrument.equals(instrument)) {
            return;
        }



    }



    private double [] filteredMA_1;
    private double [] filteredMA_2;
    private IOrder order = null;

    /* creating new orders logic */
    public void newOrderLogic(Instrument instrument) throws JFException {
        IBar prevBar = history.getBar(instrument, period, OfferSide.BID, 1);
        filteredMA_1 = indicators.sma(instrument, period, OfferSide.BID, AppliedPrice.CLOSE, smaTimePeriod_1,
                indicatorFilter, 2, prevBar.getTime(), 0);
        filteredMA_2 = indicators.sma(instrument, period, OfferSide.BID, AppliedPrice.CLOSE, smaTimePeriod_2,
                indicatorFilter, 2, prevBar.getTime(), 0);

        // SMA10 crossover SMA90 from UP to DOWN
        if ((filteredMA_2[1] < filteredMA_2[0]) && (filteredMA_2[1] < filteredMA_1[1]) && (filteredMA_2[0] >= filteredMA_1[0])) {
            if (engine.getOrders().size() > 0) {
                for (IOrder orderInMarket : engine.getOrders()) {
                    if (orderInMarket.isLong()) {
                        print("Closing Long position");
                        orderInMarket.close();
                    }
                }
            }
            if ((order == null) || (order.isLong() && order.getState().equals(IOrder.State.CLOSED)) ) {
                print("Create SELL");
                submitOrder(OrderCommand.SELL);
            }
        }
        // SMA10 crossover SMA90 from DOWN to UP
        if ((filteredMA_2[1] > filteredMA_2[0]) && (filteredMA_2[1] > filteredMA_1[1]) && (filteredMA_2[0] <= filteredMA_1[0])) {
            if (engine.getOrders().size() > 0) {
                for (IOrder orderInMarket : engine.getOrders()) {
                    if (!orderInMarket.isLong()) {
                        print("Closing Short position");
                        orderInMarket.close();
                    }
                }
            }
            if ((order == null) || (!order.isLong() && order.getState().equals(IOrder.State.CLOSED)) ) {
                submitOrder(OrderCommand.BUY);
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



        if (hour.equals("22") && dailyChecker == false){
            dailyChecker = true;
            System.out.println("Balance storing");
            TestMainRepeater.getEquitiesStorage().get(TestMainRepeater.getLoopCount()).add(account.getEquity());
            equityIndex++;
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

        orderCounter++;

        // Submitting an order for the specified getInstrument at the current market price
        return engine.submitOrder(getLabel(instrument), instrument, orderCmd, getAmount(), 0, 20, stopLossPrice, takeProfitPrice);
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

    protected String getLabel(Instrument instrument) {
        String label = instrument.name();
        label = label + (counter++);
        label = label.toUpperCase();
        return label;
    }


}