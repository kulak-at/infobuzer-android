package at.kulak.infobuzer.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by kulak on 4/4/14.
 */
public class Entry {
    JSONObject data = null;
    public Entry(JSONObject d) {
        data = d;
    }

    private String jsonConvert(String key) {
        return jsonConvert(key, "");
    }

    private String jsonConvert(String key, String deflt) {
        try {
            return data.getString(key);
        } catch(JSONException e) {
            return deflt;
        }
    }

    public Date getStartDate() throws Exception {
            long seconds = data.getLong("startDate");
            return new Date(seconds * 1000);
    }

    public String getShortTitle() {
        return jsonConvert("shortTitle");
    }

    public String getCategories() {
        try {
            JSONArray ar = data.getJSONArray("categories");
            String result = "";
            for(int i=0;i<(Math.min(ar.length(), 2));i++) {
                result += ar.getString(i);
            }
            return result;
        } catch(Exception e) {
            return "";
        }
    }

    public String getTitle() {
        return "TITLE";
    }

    public String getDescription() {
        return "";
    }

    public String getImageUrl() {
        return "";
    }

    public String getUrl() {
        return "";
    }

}
