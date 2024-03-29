package singlejartest;

import com.dukascopy.api.Instrument;
import com.dukascopy.api.Period;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static singlejartest.WriteToFile.writeDown;
import static singlejartest.WriteToFile.writeDownUserInput;

public class GUI {
    private JPanel panel1;
    private JTextField startEquityTextField;
    private JButton startButton;
    private JProgressBar progressBar1;
    private JComboBox PeriodComboBox;
    private JComboBox ParameterIncreaseSizeComboBox;
    private JTextField dateToTextField;
    private JTextField InstrumentTextField;
    private JTextField dateFromTextField;
    private JTextField ma_1;
    private JTextField ma_2;
    private JTextArea ConsoleTextArea;

    public GUI() {
        JFrame f = new JFrame("SMATester");
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        $$$setupUI$$$();
        f.add(panel1);

        f.setSize(700, 1000);
        f.setVisible(true);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // load and save date from / to
                final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
                try {
                    Data.setDateFrom(dateFormat.parse(dateFromTextField.getText() + " 00:00:00"));
                    Data.setDateTo(dateFormat.parse(dateToTextField.getText() + " 00:00:00"));
                } catch (ParseException a) {
                    a.printStackTrace();
                }

                // load and save myInstrument
                Data.setInstrument(Instrument.valueOf(InstrumentTextField.getText()));

                // load and save opening deposit
                TestMainRepeater.setOpeningDeposit(Integer.parseInt(startEquityTextField.getText()));

                // load and save period
                switch (PeriodComboBox.getSelectedIndex()) {
                    case 0:
                        Data.setPeriod(Period.ONE_SEC);
                        break;
                    case 1:
                        Data.setPeriod(Period.TEN_SECS);
                        break;
                    case 2:
                        Data.setPeriod(Period.ONE_MIN);
                        break;
                    case 3:
                        Data.setPeriod(Period.FIVE_MINS);
                        break;
                    case 4:
                        Data.setPeriod(Period.TEN_MINS);
                        break;
                    case 5:
                        Data.setPeriod(Period.FIFTEEN_MINS);
                        break;
                    case 6:
                        Data.setPeriod(Period.THIRTY_MINS);
                        break;
                    case 7:
                        Data.setPeriod(Period.ONE_HOUR);
                        break;
                    case 8:
                        Data.setPeriod(Period.FOUR_HOURS);
                        break;
                    case 9:
                        Data.setPeriod(Period.DAILY);
                        break;
                    case 10:
                        Data.setPeriod(Period.WEEKLY);
                        break;
                    case 11:
                        Data.setPeriod(Period.MONTHLY);
                        break;
                }

                // save ma_1 and ma_2 period
                Data.setMa_1(Short.parseShort(ma_1.getText()));
                Data.setMa_2(Short.parseShort(ma_2.getText()));

                // save new dateFrom
                TestMainRepeater.setMaActual_1(Integer.parseInt(ma_1.getText()));

                // create new file clean old
                WriteToFile.startFile();
                writeDownUserInput();
                writeDown("smaTimePeriod\tFinal equity\tOrders", true);

                // clean console text area
                ConsoleTextArea.selectAll();
                ConsoleTextArea.replaceSelection("");
                ConsoleTextArea.append("" +
                        "Name\t" +
                        "Final Deposit\t" +
                        "Success rate\t" +
                        "Num Of Orders\t" +
                        "Avrg commission\t" +
                        "Avrg Duratin Of Order ");

                // get list of parameters
                TestMainRepeater.refreshListOfParameters();


                System.out.println("Date From: " + Data.getDateFrom());
                System.out.println("Date To: " + Data.getDateTo());
                System.out.println("MA 1: " + Data.getMa_1());
                System.out.println("MA 2: " + Data.getMa_2());
                System.out.println("Period: " + Data.getPeriod());
                System.out.println("Instrument: " + Data.getInstrument());
                System.out.println("Start Equity: " + TestMainRepeater.getOpeningDeposit());



                // get list of parameters
                TestMainRepeater.setListOfParameters(
                        SMACrossListGenerator.listOfParameters(
                                Data.getMa_1()/10, Data.getMa_2()/10));


                // start test
                try {
                    TestMainRepeater.startStrategy();
                } catch (Exception a) {

                }
            }
        });
    }

    public void printToConsoleTextArea(String string) {
        ConsoleTextArea.append(string);
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        panel1.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLoweredBevelBorder(), null));
        startEquityTextField = new JTextField();
        startEquityTextField.setText("50000");
        startEquityTextField.setToolTipText("1000 USD e.g.");
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(startEquityTextField, gbc);
        startButton = new JButton();
        startButton.setText("Start");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(startButton, gbc);
        final JLabel label1 = new JLabel();
        label1.setText("Date (MM/dd/yyyy)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel1.add(label1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Instrument");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel1.add(label2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Time Frame");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel1.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Start Equity");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel1.add(label4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("Časový úsek");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel1.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("USD");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label6, gbc);
        PeriodComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel1 = new DefaultComboBoxModel();
        defaultComboBoxModel1.addElement("1 Sec");
        defaultComboBoxModel1.addElement("10 Sec");
        defaultComboBoxModel1.addElement("1 Min");
        defaultComboBoxModel1.addElement("5 Min");
        defaultComboBoxModel1.addElement("10 Min");
        defaultComboBoxModel1.addElement("15 Min");
        defaultComboBoxModel1.addElement("30 Min");
        defaultComboBoxModel1.addElement("1 Hour");
        defaultComboBoxModel1.addElement("4 Hour");
        defaultComboBoxModel1.addElement("1 Day");
        defaultComboBoxModel1.addElement("1 Week");
        defaultComboBoxModel1.addElement("1 Month");
        PeriodComboBox.setModel(defaultComboBoxModel1);
        PeriodComboBox.setSelectedIndex(7);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(PeriodComboBox, gbc);
        ParameterIncreaseSizeComboBox = new JComboBox();
        final DefaultComboBoxModel defaultComboBoxModel2 = new DefaultComboBoxModel();
        defaultComboBoxModel2.addElement("1 (1, 2, 3, 4, etc.)");
        defaultComboBoxModel2.addElement("5 (5, 10, 15, 20, etc.)");
        defaultComboBoxModel2.addElement("10 (10, 20, 30, 40, etc.)");
        defaultComboBoxModel2.addElement("20 (20, 40, 60, 80, etc.)");
        ParameterIncreaseSizeComboBox.setModel(defaultComboBoxModel2);
        ParameterIncreaseSizeComboBox.setSelectedIndex(2);
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 6;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(ParameterIncreaseSizeComboBox, gbc);
        dateToTextField = new JTextField();
        dateToTextField.setText("01/02/2019");
        dateToTextField.setToolTipText("dd/MM/yyyy");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(dateToTextField, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("-");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel1.add(label7, gbc);
        dateFromTextField = new JTextField();
        dateFromTextField.setText("01/01/2019");
        dateFromTextField.setToolTipText("dd/MM/yyyy");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(dateFromTextField, gbc);
        progressBar1 = new JProgressBar();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 7;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(progressBar1, gbc);
        InstrumentTextField = new JTextField();
        InstrumentTextField.setText("EURUSD");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 6;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(InstrumentTextField, gbc);
        final JLabel label8 = new JLabel();
        label8.setText("SMA parameters (from - to)");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        panel1.add(label8, gbc);
        ma_1 = new JTextField();
        ma_1.setText("50");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(ma_1, gbc);
        ma_2 = new JTextField();
        ma_2.setText("150");
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 5;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(ma_2, gbc);
        final JLabel label9 = new JLabel();
        label9.setText("-");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 5;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        panel1.add(label9, gbc);
        final JScrollPane scrollPane1 = new JScrollPane();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 7;
        gbc.fill = GridBagConstraints.BOTH;
        panel1.add(scrollPane1, gbc);
        ConsoleTextArea = new JTextArea();
        ConsoleTextArea.setColumns(0);
        ConsoleTextArea.setDragEnabled(true);
        ConsoleTextArea.setEditable(true);
        ConsoleTextArea.setLineWrap(false);
        ConsoleTextArea.setRows(15);
        ConsoleTextArea.setText("");
        scrollPane1.setViewportView(ConsoleTextArea);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

}
