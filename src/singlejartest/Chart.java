package singlejartest;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Chart extends ApplicationFrame {

    public Chart( ) {
        super("MATester");
        JFreeChart lineChart = ChartFactory.createLineChart(
                "strategies",
                "Days","Balance",
                createDataset2(),
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

        for (int strategy = 0; strategy < Data.getDailyBalance().size(); strategy++) {
            for (int day = 0; day < Data.getDailyBalance(strategy).size(); day++) {
                dataset.addValue(Data.getDailyBalance(strategy, day), "strategy_" + strategy, String.valueOf(day));
            }
        }
        return dataset;
    }
    // show 15 max strategies
    private DefaultCategoryDataset createDataset2( ) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

        try {
            for (int strategy = 0; strategy < 15; strategy++) {
                for (int day = 0; day < Data.getDailyBalance(Data.getBestResults(strategy)).size(); day++) {
                    dataset.addValue(Data.getDailyBalance(Data.getBestResults(strategy), day), "strategy_" + Data.getBestResults(strategy), String.valueOf(day));
                }
            }
        }catch(Exception e){

        }

        return dataset;
    }
}