package com.bjzjns.cordovaplugin;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * This class echoes a string called from JavaScript.
 */
public class customPickerView extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("showPickerView")) {
            this.showPickerView(args, callbackContext);
            return true;
        }
        return false;
    }

    private void showPickerView(JSONArray jsonArray, CallbackContext callbackContext) {
        if (jsonArray != null && jsonArray.length() > 0) {
            MultiWheelPickerManager multiWheelPickerManager = new MultiWheelPickerManager(customPickerView.this, callbackContext, jsonArray);
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }
}
