package at.kulak.infobuzer.model;


import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by kulak on 4/4/14.
 */
public class Entry implements Parcelable{
    //JSONObject data = null;

    private String shortTitle;
    private String title;
    private String description;
    private String image;
    private String url;
    private String[] categories;
    long startDate;

    public Entry() {
    }

    public Date getStartDate() {
            return new Date(startDate * 1000);
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public String getCategoriesString(int limit) {
        String result = "";
        String separator = "";
        for(int i=0;i<Math.min(categories.length, limit); i++) {
            result += separator + categories[i];
            separator = ", ";
        }
        return result;
    }

    public String getCategoriesString() {
        return getCategoriesString(2);
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUrl() {
        return image;
    }

    public String getUrl() {
        return url;
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
    public void writeToParcel(Parcel parcel, int j) {
        parcel.writeString(shortTitle);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(image);
        parcel.writeString(url);

        ArrayList<String> list = new ArrayList<String>();
        for(int i=0;i<categories.length;i++)
            list.add(categories[i]);

        parcel.writeStringList(list);
        parcel.writeLong(startDate);
    }

    public Entry(Parcel parcel) {
        shortTitle = parcel.readString();
        title = parcel.readString();
        description = parcel.readString();
        image = parcel.readString();
        url = parcel.readString();
        ArrayList<String> list = new ArrayList<String>();
        parcel.readStringList(list);
        categories = new String[list.size()];

        for(int i=0;i<list.size();i++) {
            categories[i] = list.get(i);
        }

        startDate = parcel.readLong();

    }
}
