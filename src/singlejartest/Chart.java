package singlejartest;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.ArrayList;

public class Chart extends ApplicationFrame {

    public Chart( String applicationTitle , String chartTitle ) {
        super(applicationTitle);
        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle,
                "Days","Equity",
                createDataset(),
                PlotOrientation.VERTICAL,
                true,true,false);

        ChartPanel chartPanel = new ChartPanel( lineChart );
        chartPanel.setPreferredSize( new java.awt.Dimension( 560 , 367 ) );
        chartPanel.setHorizontalAxisTrace(false);
        chartPanel.setVerticalAxisTrace(true);
        setContentPane( chartPanel );
    }

    private DefaultCategoryDataset createDataset( ) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

        ArrayList<ArrayList<Double>> equityList = TestMainRepeater.getEquitiesStorage();

        for (int row = 0; row < equityList.size(); row++) {
            for (int col = 0; col < equityList.get(row).size(); col++) {
                dataset.addValue(equityList.get(row).get(col),
                        TestMainRepeater.getListOfParameters().get(0).get(col)*10 + "/" + TestMainRepeater.getListOfParameters().get(1).get(col)*10+row,
                        Integer.toString(col));



                //dataset.addValue(equityList.get(row).get(col), "sma_"+TestMainRepeater.getMaActual_1(), Integer.toString(col));
                //dataset.addValue(equityList[row][col+1], "strategy_"+row, Integer.toString(col));
                //System.out.print(equityList[row][col] + " ");

            }
            System.out.println();
        }

        return dataset;
    }

    private DefaultCategoryDataset createDataset2( ) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
        dataset.addValue( 15 , "schools" , "1" );
        dataset.addValue( 30 , "schools" , "2" );
        dataset.addValue( 60 , "schools" ,  "3" );
        dataset.addValue( 120 , "schools" , "4" );
        dataset.addValue( 240 , "schools" , "5" );
        dataset.addValue( 300 , "schools" , "6" );

        dataset.addValue( 15 , "schools" , "7" );
        dataset.addValue( 30 , "schools" , "8" );
        dataset.addValue( 60 , "schools" ,  "9" );
        dataset.addValue( 120 , "schools" , "10" );
        dataset.addValue( 240 , "schools" , "11" );
        dataset.addValue( 300 , "schools" , "12" );

        dataset.addValue( 15 , "schools" , "13" );
        dataset.addValue( 30 , "schools" , "14" );
        dataset.addValue( 60 , "schools" ,  "15" );
        dataset.addValue( 120 , "schools" , "16" );
        dataset.addValue( 240 , "schools" , "17" );
        dataset.addValue( 300 , "schools" , "18" );

        dataset.addValue( 15 , "schools" , "19" );
        dataset.addValue( 30 , "schools" , "20" );
        dataset.addValue( 60 , "schools" ,  "21" );
        dataset.addValue( 120 , "schools" , "22" );
        dataset.addValue( 240 , "schools" , "23" );
        dataset.addValue( 300 , "schools" , "24" );

        dataset.addValue( 15 , "schools" , "197F0" );
        dataset.addValue( 30 , "schools" , "198F0" );
        dataset.addValue( 60 , "schools" ,  "19F90" );
        dataset.addValue( 120 , "schools" , "20F00" );
        dataset.addValue( 240 , "schools" , "20F40" );
        dataset.addValue( 300 , "schools" , "20F50" );
        return dataset;
    }

    public static void main( String[ ] args ) {
        Chart chart = new Chart(
                "School Vs Years" ,
                "Numer of Schools vs years");

        chart.pack( );
        RefineryUtilities.centerFrameOnScreen( chart );
        chart.setVisible( true );
    }
}