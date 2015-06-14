package tetris;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by toudi on 2015-06-14.
 */
public class Network {

    public Network(){
        makeRequest("DefaultSettingsRequest");
        for (int i =1;i<10;i++)
        makeRequest("LevelRequest "+i);
        makeRequest("HighScoreRequest");
    }

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

    private static void WriteNewLevel(String s) throws  IOException
    {
        PrintWriter pw=null;
        try{
            pw = new PrintWriter("level"+""+".eiti");
            pw.print(s);
        }catch (Exception e){}
        finally {
            if (pw != null)
                try {
                    pw.close();
                } catch (Exception e) {                }
        }
    }
    public static void makeRequest(String request) {
        try {
            Socket aSocket = new Socket("localhost", 8085);
            PrintWriter out = new PrintWriter(aSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(aSocket.getInputStream()));
            out.println(request);
            String line;
            String fullResponse="";
            do {
                line = in.readLine();
                if (line != null)
                    fullResponse += line.trim() + " ";
            }while (line!=null);
            String[] splitted = fullResponse.split(" ",2);
            String response = splitted[0];
            switch (response)
            {
                case ("DefaultSettingsConfig"):
                    WriteNewConfigFile(splitted[1]);
                    break;
                case ("Level"):
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
            System.exit(1);
        }
    }
}
