package singlejartest;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WriteToFile {
    /**
     * write/overwrite text to the file "soubor.txt"
     * @param text will write to the file
     * @param append if it is true text will append
     * */
    public static void writeDown(String text, Boolean append){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("soubor.txt", append)))
        {
            bw.write(text);
            bw.flush();
        }
        catch (Exception e)
        {
            System.err.println("Do souboru se nepovedlo zapsat.");
        }
    }
    /**
     * write/overwrite text to the file "soubor.txt" and create new line
     * @param text will write to the file
     * @param append if it is true text will append
     * */
    public static void writeDownln(String text, Boolean append){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("soubor.txt", append)))
        {
            bw.write(text);
            bw.newLine();
            bw.flush();
        }
        catch (Exception e)
        {
            System.err.println("Do souboru se nepovedlo zapsat.");
        }
    }

    /**
     * overwrite whole file and write first line ("New test" + date) to the file "soubor.txt"
     * */
    public static void startFile(){
        // getting date and time
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();

        // owerwrite whole file and write down first line
        writeDown("New test " + dtf.format(now) + "\n\n", false);

    }

    /**
     * add last line ("getEquity :" + Equity) to the file "soubor.txt"
     * */
    public static void endFile(){
        writeDown("Final equity is : " + Exit.getEquity(), true);
    }

    public static void writeDownUserInput(){

        writeDownln("Date From:\t\t" + Data.getDateFrom(),true);
        writeDownln("Date To:\t\t" + Data.getDateTo(),true);
        writeDownln("Instrument:\t\t" + Data.getInstrument(),true);
        writeDownln("Opening deposit:\t\t" + Data.getOpeningDeposit(),true);
        writeDownln("Period:  \t\t" + Data.getPeriod(),true);
        writeDownln("Parameters From:\t" + Data.getMa_1(),true);
        writeDownln("Parameters To:\t\t" + Data.getMa_2() + "\n",true);
    }
}
