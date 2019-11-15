package singlejartest;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

public class Chart extends ApplicationFrame {

    public Chart( String applicationTitle , String chartTitle ) {
        super(applicationTitle);
        JFreeChart lineChart = ChartFactory.createLineChart(
                chartTitle,
                "Days","Balance",
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

        for (int strategy = 0; strategy < Data.getDailyBalance().size(); strategy++) {
            for (int day = 0; day < Data.getDailyBalance(strategy).size(); day++) {
                dataset.addValue(Data.getDailyBalance(strategy, day), "strategy_" + strategy, String.valueOf(day));
            }
        }
        return dataset;
    }
}