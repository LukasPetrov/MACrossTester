package singlejartest;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;

import java.util.*;

/**
 * Create "DataCube" which is 3D field of array lists
 */
public class Data {
    private static ArrayList<Object> firstLevel = new ArrayList<>();

    private static int StrategyNameIndex;
    private static int param1Index;
    private static int param2Index;
    private static int finalDepositIndex;
    private static int dayliBalanceIndex;
    private static int orderNameIndex;
    private static int orderSizeIndex;
    private static int orderCommisionIndex;

    private static int dateFromIndex;
    private static int dateToIndex;
    private static int inputMa_1Index;
    private static int inputMa_2Index;
    private static int inputInstrumentIndex;
    private static int inputPeriodIndex;
    private static int inputOpeningDepositIndex;
    private static int bestResultsIndex2;
    private static int bestResultsIndex;

    public static void createDataCube(){
        // int represents location at firstLevel ArrayList
        StrategyNameIndex = 0;      addData1D(new ArrayList<String>());                // 0 Strategy name
        param1Index = 1;            addData1D(new ArrayList<Integer>());               // 1 param 1
        param2Index = 2;            addData1D(new ArrayList<Integer>());               // 2 param 2
        finalDepositIndex = 3;      addData1D(new ArrayList<Double>());                // 3 final deposit
        dayliBalanceIndex = 4;      addData1D(new ArrayList<ArrayList<Double>>());     // 4 daily balance

        orderNameIndex = 5;         addData1D(new ArrayList<ArrayList<String>>());     // 5 order name
        orderSizeIndex = 6;         addData1D(new ArrayList<ArrayList<Double>>());     // 6 order size
        orderCommisionIndex = 7;    addData1D(new ArrayList<ArrayList<Double>>());     // 7 order commission

        dateFromIndex = 8;              addData1D(new Date());          // 8 date from
        dateToIndex = 9;                addData1D(new Date());          // 9 date to
        inputMa_1Index = 10;            addData1D(new short[0]);        // 10 ma_1 from user input
        inputMa_2Index = 11;            addData1D(new short[0]);        // 11 ma_2 from user input
        inputInstrumentIndex = 12;      addData1D(new Object());        // 12 instrument from user input
        inputPeriodIndex = 13;          addData1D(new Object());        // 13 period from user input
        inputOpeningDepositIndex = 14;  addData1D(new Object());        // 14 opening deposit from user input
        bestResultsIndex = 15;          addData1D(new ArrayList<Integer>());     // 15 list of the best strategies <finalDeposit, index>
    }

    public static void addStrategyName(String name){
        addData2D(StrategyNameIndex, name);
    }
    public static void addParam1(int param){
        addData2D(param1Index, param);
    }
    public static void addParam2(int param){
        addData2D(param2Index, param);
    }
    public static void addFinalDeposit(Double deposit){
        addData2D(finalDepositIndex, deposit);
    }
    public static void addDailyBalance(int strategyNumber,  Double balance){
        addData3D(dayliBalanceIndex, strategyNumber, balance);
    }
    public static void addOrderName(int strategyNumber, String orderName){
        addData3D(orderNameIndex, strategyNumber, orderName);
    }
    public static void addOrderSize(int strategyNumber, Double orderSize){
        addData3D(orderSizeIndex, strategyNumber, orderSize);
    }
    public static void addOrderCommission(int strategyNumber, Double orderCommission){
        addData3D(orderCommisionIndex, strategyNumber, orderCommission);
    }
    public static void addBestResultsIndex(int param){
        addData2D(bestResultsIndex, param);
    }

    public static void setDateFrom(Date dateFrom){
        setData1D(dateFromIndex, dateFrom);
    }
    public static void setDateTo(Date dateTo){
        setData1D(dateToIndex, dateTo);
    }
    public static void setMa_1(short ma_1){
        setData1D(inputMa_1Index, ma_1);
    }
    public static void setMa_2(short ma_2){
        setData1D(inputMa_2Index, ma_2);
    }
    public static void setInstrument(Instrument instrument){
        setData1D(inputInstrumentIndex, instrument);
    }
    public static void setPeriod(Period period){
        setData1D(inputPeriodIndex, period);
    }
    public static void setOpeningDeposit(int openingDeposit){
        setData1D(inputOpeningDepositIndex, openingDeposit);
    }


    public static String getStrategyName(int strategyIndex){
        return getData(StrategyNameIndex, strategyIndex);
    }
    public static Object getStrategyName(){
        return getData(StrategyNameIndex);
    }
    public static int getParam1(int strategyIndex){
        return getData(param1Index, strategyIndex);
    }
    public static int getParam2(int strategyIndex){
        return getData(param2Index, strategyIndex);
    }
    public static Double getFinalDeposit(int strategyIndex){
        return getData(finalDepositIndex, strategyIndex);
    }
    public static Object getFinalDepositList(){
        return getData(finalDepositIndex);
    }
    public static Double getDailyBalance(int strategyIndex, int dayIndex){
        return (Double) getData(dayliBalanceIndex, strategyIndex, dayIndex);
    }
    public static List getDailyBalance(int strategy){
        return (List) getData(dayliBalanceIndex, strategy);
    }
    public static List getDailyBalance(){
        return (List) getData(dayliBalanceIndex);
    }
    public static String getOrderName(int strategyIndex, int orderIndex){
        return getData(orderNameIndex, strategyIndex, orderIndex);
    }
    public static Double getOrderSize(int strategyIndex, int orderIndex){
        return getData(orderSizeIndex, strategyIndex, orderIndex);
    }
    public static Double getOrderCommission(int strategyIndex, int orderIndex){
        return getData(orderCommisionIndex, strategyIndex, orderIndex);
    }

    public static Date getDateFrom(){
        return (Date) getData(dateFromIndex);
    }
    public static Date  getDateTo(){
        return (Date) getData(dateToIndex);
    }
    public static short  getMa_1(){
        return (short) getData(inputMa_1Index);
    }
    public static short  getMa_2(){
        return (short) getData(inputMa_2Index);
    }
    public static Instrument  getInstrument(){
        return (Instrument) getData(inputInstrumentIndex);
    }
    public static Period  getPeriod(){
        return (Period) getData(inputPeriodIndex);
    }

    public static Object  getOpeningDeposit(){
        return  getData(inputOpeningDepositIndex);
    }

    public static int getBestResults(int strategyIndex){
        return getData(bestResultsIndex, strategyIndex);
    }
    public static Object getBestResults(){
        return getData(bestResultsIndex);
    }



    /**
     * Return the value from position x, y, z.
     *
     * @param  x  1st Dimension
     * @param  y  2nd Dimension
     * @param  z  3th Dimension
     */
    private static <T> T getData(int x, int y, int z){

        // load first level
        ArrayList<ArrayList<T>> secondLevel = (ArrayList<ArrayList<T>>) firstLevel.get(x);

        // load second list
        ArrayList<T> thirdLevel = secondLevel.get(y);

        // load item
        T value = thirdLevel.get(z);

        return value;
    }

    /**
     * Return the value from position x, y.
     *
     * @param  x  1st Dimension
     * @param  y  2nd Dimension
     */
    private static <T> T getData(int x, int y){

        // load first level
        ArrayList<T> secondLevel = (ArrayList<T>) firstLevel.get(x);

        // load second list
        T value = secondLevel.get(y);

        return value;
    }


    /**
     * Return Object from position x.
     *
     * @param  x  1st Dimension
     */
    private static Object  getData(int x){

        // load second value
        Object value = (Object) firstLevel.get(x);

        return value;
    }


    /**
     * Add the value to position x, y.
     *
     * @param  x  1st Dimension
     * @param  y  2nd Dimension
     * @param  value add this value
     */
    private static void addData3D(int x, int y, Object value){
        //try if list exists if not (error) create new and then add new value
        try{
            // load first level
            ArrayList<Object> secondLevel = (ArrayList<Object>) firstLevel.get(x);

            // load second list

            ArrayList<Object> thirdLevel = (ArrayList<Object>) secondLevel.get(y);
            thirdLevel.add(value);

            // overwrite original list
            secondLevel.set(y, thirdLevel);
            firstLevel.set(x, secondLevel);
        }catch(Exception e){
            // create new list to solve the error
            addData2D(x, new ArrayList<Integer>());

            // load first level
            ArrayList<Object> secondLevel = (ArrayList<Object>) firstLevel.get(x);

            // load second list

            ArrayList<Object> thirdLevel = (ArrayList<Object>) secondLevel.get(y);
            thirdLevel.add(value);

            // overwrite original list
            secondLevel.set(y, thirdLevel);
            firstLevel.set(x, secondLevel);
        }
    }


    /**
     * Add the value to position x.
     *
     * @param  x  1st Dimension
     * @param  value add this value
     */
    private static void addData2D(int x, Object value){

        // load first level
        ArrayList<Object> secondLevel = (ArrayList<Object>) firstLevel.get(x);

        // load second list
        secondLevel.add(value);

        // overwrite original list

        firstLevel.set(x, secondLevel);
    }

    /**
     * Add new list.
     *
     * @param  list add this list
     */
    private static void addData1D(Object list){
        firstLevel.add(list);
    }


    /**
     * Set/Replace list.
     *
     * @param  x 1st Dimension
     * @param  list add this list
     */
    private static void setData1D(int x, Object list){
        firstLevel.set(x, list);
    }
}
