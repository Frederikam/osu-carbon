package com.frederikam.osucarbon.osuapi;

import com.frederikam.osucarbon.OsuCarbon;
import com.frederikam.osucarbon.io.Line;
import com.frederikam.osucarbon.io.LineSender;
import static com.frederikam.osucarbon.OsuCarbon.chartDataScrapeRegex;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import org.json.JSONArray;
import org.json.JSONObject;

public class OsuAPI {

    public final String apiToken;

    public OsuAPI(String apiToken) {
        this.apiToken = apiToken;
    }
    
    public User getUser(String idOrUsername){
        try {
            String response = Unirest.get("https://osu.ppy.sh/api/get_user?k="+apiToken+"&u="+idOrUsername).asString().getBody();
            
            //remove outer brackets due to Unirest bug
            response = response.substring(1, response.length());
            JSONObject json = new JSONObject(response);
            User user = new User();
            
            user.id = Integer.valueOf(json.getString("user_id"));
            user.username = json.getString("username");
            user.rank = Integer.valueOf(json.getString("pp_rank"));
            user.pp = Float.valueOf(json.getString("pp_raw"));
            user.accuracy = Double.valueOf(json.getString("accuracy"))/100;
            
            return user;
        } catch (UnirestException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void scrapeAndReportChartData(String user, String mode) throws UnirestException {
        String rawData = Unirest.get("https://new.ppy.sh/u/" + user).asString().getBody();
        Matcher matcher = chartDataScrapeRegex.matcher(rawData);
        matcher.find();
        String match = matcher.group(1);

        /*
        Example data from regex:
        http://hs.frederikam.com/awovi.json
         */
        JSONObject chartData = new JSONObject("{" + match + "}");
        JSONArray chartDataArray = chartData.getJSONObject("allRankHistories").getJSONObject("data").getJSONObject(mode).getJSONObject("data").getJSONArray("data");
        int totalPastDays = chartDataArray.length() - 1;
        int i = 0;//i == 0 is the oldest record. i == length - 1 is the record for today
        ArrayList<Line> lines = new ArrayList<>();

        for (Object v : chartDataArray) {
            long rank = (long) (int) v;
            if(rank != 0){
                long time = System.currentTimeMillis() / 1000 - ((totalPastDays - i) * 86400);
                lines.add(new Line(OsuCarbon.carbonPath + ".rank." + user, rank, time));
            }
            i++;
        }
        LineSender.submitData(lines);
    }

}
