package com.bjzjns.cordovaplugin;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import com.aigestudio.wheelpicker.WheelPicker;
import com.google.gson.Gson;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 说明
 * 传入的JsonArry数据约定格式
 * [{"dataArray":[{"name":"北京","objectArray":[{"name":"北京","objectArray":[{"name":"昌平"},{"name":"海淀"},{"name":"朝阳"}]}]},{"name":"天津","objectArray":[{"name":"天津","objectArray":[{"name":"aaa"},{"name":"bbb"},{"name":"ccc"}]}]}],"columnCount":3,"isLinkWork":true}]
 */

public class MultiWheelPickerManager implements WheelPicker.OnItemSelectedListener, View.OnClickListener {

    public static final String TAG = MultiWheelPickerManager.class.getSimpleName();

    private CordovaPlugin mCordovaPlugin;
    private CallbackContext mCallbackContext;

    private FrameLayout mRootView;
    private View mMultiWheelPickerView;
    private View mTouchRl;
    private View mCancelTv;
    private View mConfirmTv;
    private WheelPicker mWheelLeftWp;
    private WheelPicker mWheelCenterWp;
    private WheelPicker mWheelRightWp;

    private int mWheelPickerNumber = 0;
    private boolean linkage;

    private MultiWheelPickerModel mMultiWheelPickerModel;
    private WheelPickerModel mWheelPickerModel;

    private int mLeftPostion, mCenterPostion, mRightPostion;

    private ArrayList<String> mLeftList = new ArrayList<String>();
    private ArrayList<String> mCenterList = new ArrayList<String>();
    private ArrayList<String> mRightList = new ArrayList<String>();

    private Activity mActivity;
    private String mAPackageName;

    public MultiWheelPickerManager(CordovaPlugin cordovaPlugin, CallbackContext callbackContext, JSONArray jsonArray) {
        mCordovaPlugin = cordovaPlugin;
        mCallbackContext = callbackContext;
        mActivity = cordovaPlugin.cordova.getActivity();
        mAPackageName = mActivity.getPackageName();

        if (jsonArray.length() != 0) {
            initData(jsonArray);
            initView(mCordovaPlugin);
        } else {
            if (callbackContext != null) {
                callbackContext.error("请传入约定的数据格式");
            }
        }
    }

    /**
     * 初始化数据
     *
     * @param jsonArray
     */
    private void initData(JSONArray jsonArray) {

        try {
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            mMultiWheelPickerModel = new Gson().fromJson(jsonObject.toString(), MultiWheelPickerModel.class);
            mWheelPickerNumber = mMultiWheelPickerModel.columnCount;
            linkage = mMultiWheelPickerModel.isLinkWork;

            getStringList(mLeftList, mMultiWheelPickerModel.dataArray);
            getStringList(mCenterList, mMultiWheelPickerModel.dataArray.get(0).objectArray);
            getStringList(mRightList, mMultiWheelPickerModel.dataArray.get(0).objectArray.get(0).objectArray);

            Log.i(TAG, mMultiWheelPickerModel.toString());

        } catch (JSONException e) {
            e.printStackTrace();
            mCallbackContext.error("JSONException");
        }

    }

    /**
     * 获取数据的名称显示集合
     *
     * @param arrayList
     * @param wheelPickerModelArrayList
     */
    private void getStringList(ArrayList<String> arrayList, ArrayList<WheelPickerModel> wheelPickerModelArrayList) {

        if (arrayList != null && wheelPickerModelArrayList != null) {
            arrayList.clear();
            int length = wheelPickerModelArrayList.size();
            for (int i = 0; i < length; i++) {
                arrayList.add(wheelPickerModelArrayList.get(i).name);
            }
        }
    }


    private int getViewIdentifier(String viewId) {
        return mActivity.getResources().getIdentifier(viewId, "id", mAPackageName);
    }

    private int getLayoutIdentifier(String layoutId) {
        return mActivity.getResources().getIdentifier(layoutId, "layout", mAPackageName);
    }

    /**
     * 初始化视图
     *
     * @param cordovaPlugin
     */
    private void initView(final CordovaPlugin cordovaPlugin) {
        //UI 线程显示操作
        cordovaPlugin.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRootView = ((FrameLayout) (((Activity) cordovaPlugin.webView.getContext()).getWindow().getDecorView().findViewById(android.R.id.content)));
                mMultiWheelPickerView = LayoutInflater.from(cordovaPlugin.webView.getContext()).inflate(getLayoutIdentifier("layout_multi_wheel_picker"), null);

                mTouchRl = mMultiWheelPickerView.findViewById(getViewIdentifier("touch_rl"));
                mCancelTv = mMultiWheelPickerView.findViewById(getViewIdentifier("cancle_tv"));
                mConfirmTv = mMultiWheelPickerView.findViewById(getViewIdentifier("confirm_tv"));

                mWheelLeftWp = (WheelPicker) mMultiWheelPickerView.findViewById(getViewIdentifier("wheel_left_wp"));
                mWheelCenterWp = (WheelPicker) mMultiWheelPickerView.findViewById(getViewIdentifier("wheel_center_wp"));
                mWheelRightWp = (WheelPicker) mMultiWheelPickerView.findViewById(getViewIdentifier("wheel_right_wp"));

                mTouchRl.setOnClickListener(MultiWheelPickerManager.this);
                mCancelTv.setOnClickListener(MultiWheelPickerManager.this);
                mConfirmTv.setOnClickListener(MultiWheelPickerManager.this);
                mWheelLeftWp.setOnItemSelectedListener(MultiWheelPickerManager.this);
                mWheelCenterWp.setOnItemSelectedListener(MultiWheelPickerManager.this);
                mWheelRightWp.setOnItemSelectedListener(MultiWheelPickerManager.this);

                showWheelPickerNumber(mWheelPickerNumber);
                setShowDataByNumber(mWheelPickerNumber);

                mRootView.addView(mMultiWheelPickerView);
            }
        });
    }


    /**
     * 根据数量设置轮选器的显示个数
     *
     * @param number
     */
    private void showWheelPickerNumber(int number) {
        if (number == 1) {
            mWheelRightWp.setVisibility(View.GONE);
            mWheelCenterWp.setVisibility(View.GONE);
        } else if (number == 2) {
            mWheelRightWp.setVisibility(View.GONE);
        } else if (number == 3) {
        }
    }

    /**
     * 根据轮选器的个数设置显示数据
     *
     * @param number
     */
    private void setShowDataByNumber(int number) {
        if (number == 1) {
            mWheelLeftWp.setData(mLeftList);
        } else if (number == 2) {
            mWheelLeftWp.setData(mLeftList);
            mWheelCenterWp.setData(mCenterList);
        } else if (number == 3) {
            mWheelLeftWp.setData(mLeftList);
            mWheelCenterWp.setData(mCenterList);
            mWheelRightWp.setData(mRightList);
        }
    }

    /**
     * 显示方法
     */
    public void show() {
        if (mCordovaPlugin != null && mRootView != null && mMultiWheelPickerView != null) {
            mCordovaPlugin.cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRootView.addView(mMultiWheelPickerView);
                }
            });
        }
    }

    /**
     * 关闭显示的方法
     */
    public void dismiss() {
        if (mCordovaPlugin != null && mRootView != null && mMultiWheelPickerView != null) {
            mCordovaPlugin.cordova.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mRootView.removeView(mMultiWheelPickerView);
                }
            });
        }
    }


    /**
     * 确认后的回调数据设置
     */
    public void confirm() {
        if (mCallbackContext != null) {
            //根据默认或设置的轮子个数获 选中的数据  拼接数据 设置回调函数

            dismiss();
            mCallbackContext.success(mMultiWheelPickerModel.getJosnArry(mLeftPostion, mCenterPostion, mRightPostion));
            Log.i(TAG, "返回的数据：" + mMultiWheelPickerModel.getJosnArry(mLeftPostion, mCenterPostion, mRightPostion).toString());
        }
    }

    @Override

    public void onItemSelected(WheelPicker picker, Object data, int position) {

        if (picker.getId() == getViewIdentifier("wheel_left_wp")) {
            mLeftPostion = position;
            //是否联动
            if (linkage) {
                getStringList(mCenterList, mMultiWheelPickerModel.dataArray.get(mLeftPostion).objectArray);
                mWheelCenterWp.setData(mCenterList);
                mWheelCenterWp.setSelectedItemPosition(0);
                getStringList(mRightList, mMultiWheelPickerModel.dataArray.get(mLeftPostion).objectArray.get(0).objectArray);
                mWheelRightWp.setData(mRightList);
                mWheelRightWp.setSelectedItemPosition(0);
            }
        } else if (picker.getId() == getViewIdentifier("wheel_center_wp")) {
            mCenterPostion = position;
            if (linkage) {
                getStringList(mRightList, mMultiWheelPickerModel.dataArray.get(mLeftPostion).objectArray.get(mCenterPostion).objectArray);
                mWheelRightWp.setData(mRightList);
                mWheelRightWp.setSelectedItemPosition(0);
            }
        } else if (picker.getId() == getViewIdentifier("wheel_right_wp")) {
            mRightPostion = position;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == getViewIdentifier("cancle_tv") || view.getId() == getViewIdentifier("touch_rl")) {
            dismiss();
        } else if (view.getId() == getViewIdentifier("confirm_tv")) {
            confirm();
        }
    }

}
