package com.bjzjns.cordovaplugin;

import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * This class echoes a string called from JavaScript.
 */
public class MultiWheelPickerPlugin extends CordovaPlugin {

    private static final String TAG = MultiWheelPickerPlugin.class.getSimpleName();

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("showPickerView")) {
            this.showPickerView(args, callbackContext);
            return true;
        } else if (action.equals("showAddressPickerView")) {
            this.showAddressPickerView(callbackContext);
            return true;
        }
        return false;
    }

    /**
     * 显示轮选器
     *
     * @param jsonArray       显示数据
     * @param callbackContext 回调
     */
    private void showPickerView(JSONArray jsonArray, CallbackContext callbackContext) {
        if (jsonArray != null && jsonArray.length() > 0) {
            new MultiWheelPickerManager(MultiWheelPickerPlugin.this, callbackContext, jsonArray);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    /**
     * 地址的三级联动
     *
     * @param callbackContext 回调
     */
    private void showAddressPickerView(CallbackContext callbackContext) {
        try {
            InputStream is = null;
            is = cordova.getActivity().getResources().getAssets().open("www/addr.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String text = new String(buffer);
            Log.e(TAG, "数据:" + text);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("columnCount", 3);
            jsonObject.put("isLinkWork", true);
            jsonObject.put("title", "选择地区");
            jsonObject.put("dataArray", new JSONObject(text).getJSONArray("dataArray"));
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(jsonObject);
            new MultiWheelPickerManager(MultiWheelPickerPlugin.this, callbackContext, jsonArray);
        } catch (Exception e) {
            e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }
}
