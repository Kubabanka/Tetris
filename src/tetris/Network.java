package tetris;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

/**
 * Created by Tomasz Duda and Jakub Bańka on 2015-06-14.
 */

/**
 * Komunikacja sieciowa.
 */
public class Network {

    /**
     * Konstruktor tej klasy.
     */
    public Network(){
        makeRequest("DefaultSettingsRequest");
        for (int i =1;i<10;i++)
        {makeRequest("LevelRequest "+i);}
        makeRequest("HighScoresRequest");

        JOptionPane.showMessageDialog(null, "Pobrano wszystkie pliki!", "Sukces!", JOptionPane.PLAIN_MESSAGE);

    }

    /**
     * Metoda służąca do tworzenia nowego pliku konfiguracyjnego.
     * @param s dane potrzebne do stworzenia nowego pliku konfiguracyjnego
     */
    private static void WriteNewConfigFile(String s){
        PrintWriter pw=null;
        try{
            pw = new PrintWriter("config.properties");
            String [] splitted = s.split(" ");
            pw.println("lineScore="+splitted[0]);
            pw.println("blockScore="+splitted[1]);
            pw.println("width="+splitted[2]);
            pw.println("height="+splitted[3]);
            pw.println("scale="+splitted[4]);
            pw.println("playerName="+splitted[5]);
            pw.println("penalty="+splitted[6]);
            pw.println("levelScore="+splitted[7]);
            pw.println("speed="+splitted[8]);
            pw.println("maxPowerUp="+splitted[9]);
        }catch (Exception e){}
        finally {
            if (pw!=null)
                try{pw.close();}
                catch(Exception e){}
        }
    }

    /**
     * Metoda służąca do tworzenia nowego pliku najlepszych wyników.
     * @param s dane potrzebne do jego stworzenia
     * @throws IOException
     */
    private static void  WriteNewHighScore(String s) throws  IOException
    {
        PrintWriter pw=null;
        try{
            pw = new PrintWriter("high_scores.eiti");
            pw.print(s);
        }catch (Exception e){}
        finally {
            if (pw!=null)
                try{pw.close();}
                catch(Exception e){}
        }
    }

    /**
     * Metoda służąca do parsowania poziomów gry
     * @param s dane na temat poziomu gry
     * @throws IOException
     */
    private static void WriteNewLevel(String s) throws  IOException
    {
        String [] k = s.split(" ",2);

        PrintWriter pw=null;
        try{
            pw = new PrintWriter("levels/level"+k[0]+".eiti");
            pw.print(k[1]);
        }catch (Exception e){}
        finally {
            if (pw != null)
                try {
                    pw.close();
                } catch (Exception e) {                }
        }
    }

    /**
     * Metoda służaca do komunikacji z serwerem. Otwiera ona i zamyka połączenie z nim
     * @param request określa typ żądania
     */
    public static void makeRequest(String request) {
            try {
            Socket aSocket = new Socket("localhost", 8087);
            PrintWriter out = new PrintWriter(aSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
            out.println(request);
            String line;
            String fullResponse="";
            do {
                line = in.readLine();
                if (line != null)
                    fullResponse += line.trim() + "\r\n";
            }while (line!=null);
            String[] splitted = fullResponse.split(" ",2);
            String response = splitted[0];
            switch (response)
            {
                case ("DefaultSettingsConfig"):
                    WriteNewConfigFile(splitted[1]);
                    break;
                case "Level":
                    WriteNewLevel(splitted[1]);
                    break;
                case ("HighScoresReply"):
                    WriteNewHighScore(splitted[1]);
                    break;
                case ("NewScoreReply"):
                    if (splitted[1].equals("1"))
                    System.out.println("You did it!");
                    break;
            }
            aSocket.close();
            }catch (UnknownHostException e){
            System.out.println("Unknown host");
            System.exit(1);
        }catch (Exception e){
            System.out.println("Error occurred");
                JOptionPane.showMessageDialog(null,"Nie uruchomiłeś serwera!","Błąd!",JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }

    public static void main(String[] args) {
        Network n = new Network();
    }
}
