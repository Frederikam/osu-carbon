package com.frederikam.osucarbon;

import com.frederikam.osucarbon.osuapi.OsuAPI;
import com.frederikam.osucarbon.io.Line;
import com.frederikam.osucarbon.io.LineSender;
import com.frederikam.osucarbon.osuapi.User;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class OsuCarbon {

    public static String osuApiKey = "";
    public static String carbonPath = "carbon.osu";
    public static String mode = "osu";
    public static Pattern chartDataScrapeRegex = Pattern.compile("(\"allRankHistories.+),\"allScores\"");
    public static String carbonHost = "";
    public static int carbonPort = 2003;
    public static OsuAPI api;
    public static JSONArray usersToMonitor;
    public static final OsuCarbon instance = new OsuCarbon();

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        //Load configuration
        InputStream is = new FileInputStream(new File("./config.json"));
        Scanner scanner = new Scanner(is);
        JSONObject config = new JSONObject(scanner.useDelimiter("\\A").next());
        scanner.close();

        osuApiKey = config.getString("osuApiKey");
        carbonPath = config.getString("carbonPath");
        mode = config.getString("mode");
        carbonHost = config.getString("carbonHost");
        carbonPort = config.getInt("carbonPort");
        usersToMonitor = config.getJSONArray("users");

        //Handle command line arguments
        boolean doDailyStatistics = true;

        api = new OsuAPI(osuApiKey);

        int i = 0;
        for (String arg : Arrays.asList(args)) {
            if (arg.equals("-s")) {
                doDailyStatistics = false;
                try {
                    api.scrapeAndReportChartData(args[i + 1], mode);
                } catch (UnirestException ex) {
                    Logger.getLogger(OsuCarbon.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            i++;
        }

        if (doDailyStatistics) {
            while (true) {
                long t = System.currentTimeMillis() + 630000;//t is offset by 11 minutes
                long currentDay = t / 86400000;
                long nextScheduledReport = (currentDay + 1) * 86400000 - 600000;//10 minutes before midnight
                long diff = nextScheduledReport - System.currentTimeMillis();
                
                System.out.println("Next report in " + diff / 60000  + " minutes.");

                //Wait for the remaining time
                synchronized (instance) {
                    instance.wait(diff);
                }
                uploadDailyStats();
            }
        }
    }

    private static void uploadDailyStats(){
        ArrayList<Line> lines = new ArrayList<>();
        
        for(Object obj : usersToMonitor){
            String userIdentifier = (String) obj;
            User user = api.getUser(userIdentifier);
            
            lines.add(new Line(carbonPath+".rank."+userIdentifier, user.rank));
            lines.add(new Line(carbonPath+".pp."+userIdentifier, user.pp));
            lines.add(new Line(carbonPath+".accuracy."+userIdentifier, user.accuracy));
        }
        
        LineSender.submitData(lines);
    }
}
