package com.bjzjns.cordovaplugin;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class WheelPickerModel implements Parcelable {
    public String name;  //名称
    public ArrayList<WheelPickerModel> objectArray; //数据集合

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeList(this.objectArray);
    }

    protected WheelPickerModel(Parcel in) {
        this.name = in.readString();
        this.objectArray = new ArrayList<WheelPickerModel>();
        in.readList(this.objectArray, WheelPickerModel.class.getClassLoader());
    }

    public static final Parcelable.Creator<WheelPickerModel> CREATOR = new Parcelable.Creator<WheelPickerModel>() {
        @Override
        public WheelPickerModel createFromParcel(Parcel source) {
            return new WheelPickerModel(source);
        }

        @Override
        public WheelPickerModel[] newArray(int size) {
            return new WheelPickerModel[size];
        }

    };


    public JSONObject getJsonObjcet() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("objectArray", objectArray);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public String toString() {
        return "{" +
                "name:'" + name + '\'' +
                ", objectArray:" + objectArray +
                '}';
    }

}
