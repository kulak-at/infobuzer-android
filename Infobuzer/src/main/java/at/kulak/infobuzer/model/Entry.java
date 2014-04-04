package at.kulak.infobuzer.model;


import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by kulak on 4/4/14.
 */
public class Entry implements Parcelable{
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
        return jsonConvert("title", "No title");
    }

    public String getDescription() {
        return jsonConvert("description", "No description");
    }

    public String getImageUrl() {
        return jsonConvert("image");
    }

    public String getUrl() {
        return jsonConvert("url");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Entry> CREATOR =
            new Creator<Entry>() {
                @Override
                public Entry createFromParcel(Parcel parcel) {
                    return new Entry(parcel);
                }

                @Override
                public Entry[] newArray(int size) {
                    return new Entry[size];
                }
            };

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(data.toString());
    }

    public Entry(Parcel parcel) {
        try {
            this.data = new JSONObject(parcel.readString());
        } catch(Exception e) {
            // sic
        }
    }
}
