package com.bjzjns.cordovaplugin;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONArray;

import java.util.ArrayList;

public class MultiWheelPickerModel implements Parcelable {

    /**
     * dataArray : [{"name":"北京","objectArray":[{"name":"北京","objectArray":[{"name":"昌平"},{"name":"海淀"},{"name":"朝阳"}]}]},{"name":"天津","objectArray":[{"name":"天津","objectArray":[{"name":"aaa"},{"name":"bbb"},{"name":"ccc"}]}]}]
     * columnCount : 3
     * isLinkWork : true
     */

    public int columnCount;  //轮选器的个数
    public boolean isLinkWork; //是否联动
    public ArrayList<WheelPickerModel> dataArray;  //单个轮选器的数据集合


    /**
     * 根据轮选器的位置和个数以及联动属性获取Json数组
     *
     * @param leftPostion
     * @param centerPositon
     * @param rightPosition
     * @return
     */
    public JSONArray getJosnArry(int leftPostion, int centerPositon, int rightPosition) {

        JSONArray jsonArray = new JSONArray();
        switch (columnCount) {
            case 3:
                if (isLinkWork) {
                    jsonArray.put(dataArray.get(leftPostion).getJsonObjcet());
                    jsonArray.put(dataArray.get(leftPostion).objectArray.get(centerPositon).getJsonObjcet());
                    jsonArray.put(dataArray.get(leftPostion).objectArray.get(centerPositon).objectArray.get(rightPosition).getJsonObjcet());
                } else {
                    jsonArray.put(dataArray.get(leftPostion).getJsonObjcet());
                    jsonArray.put(dataArray.get(0).objectArray.get(centerPositon).getJsonObjcet());
                    jsonArray.put(dataArray.get(0).objectArray.get(0).objectArray.get(rightPosition).getJsonObjcet());
                }

                break;
            case 2:
                if (isLinkWork) {
                    jsonArray.put(dataArray.get(leftPostion).getJsonObjcet());
                    jsonArray.put(dataArray.get(leftPostion).objectArray.get(centerPositon).getJsonObjcet());
                } else {
                    jsonArray.put(dataArray.get(leftPostion).getJsonObjcet());
                    jsonArray.put(dataArray.get(0).objectArray.get(centerPositon).getJsonObjcet());
                }
                break;
            case 1:
                jsonArray.put(dataArray.get(leftPostion).getJsonObjcet());
                break;
        }

        return jsonArray;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.columnCount);
        dest.writeByte(this.isLinkWork ? (byte) 1 : (byte) 0);
        dest.writeTypedList(this.dataArray);
    }

    public MultiWheelPickerModel() {
    }

    protected MultiWheelPickerModel(Parcel in) {
        this.columnCount = in.readInt();
        this.isLinkWork = in.readByte() != 0;
        this.dataArray = in.createTypedArrayList(WheelPickerModel.CREATOR);
    }

    public static final Parcelable.Creator<MultiWheelPickerModel> CREATOR = new Parcelable.Creator<MultiWheelPickerModel>() {
        @Override
        public MultiWheelPickerModel createFromParcel(Parcel source) {
            return new MultiWheelPickerModel(source);
        }

        @Override
        public MultiWheelPickerModel[] newArray(int size) {
            return new MultiWheelPickerModel[size];
        }
    };

    @Override
    public String toString() {
        return "MultiWheelPickerModel{" +
                "columnCount=" + columnCount +
                ", isLinkWork=" + isLinkWork +
                ", dataArray=" + dataArray +
                '}';
    }
}
