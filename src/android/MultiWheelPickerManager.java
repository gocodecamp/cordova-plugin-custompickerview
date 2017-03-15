package com.bjzjns.cordovaplugin;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aigestudio.wheelpicker.WheelPicker;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * 说明
 * 传入的JsonArry数据约定格式
 * [{"dataArray":[{"name":"北京","objectArray":[{"name":"北京","objectArray":[{"name":"昌平"},{"name":"海淀"},{"name":"朝阳"}]}]},{"name":"天津","objectArray":[{"name":"天津","objectArray":[{"name":"aaa"},{"name":"bbb"},{"name":"ccc"}]}]}],"columnCount":3,"isLinkWork":true, "title":"hhhhh"}]
 * 或者
 * [{"dataArray":[[{'name' : '不喜欢/不想要','id': 1}, {'name' : '未按约定时间发货', 'id': 2},{'name' : '商品描述不符', 'id': 3},{'name' : '快递物流无跟踪记录', 'id': 4}],[{'name' : '不喜欢/不想要','id': 1}, {'name' : '未按约定时间发货', 'id': 2},{'name' : '商品描述不符', 'id': 3},{'name' : '快递物流无跟踪记录', 'id': 4}],[{'name' : '不喜欢/不想要','id': 1}, {'name' : '未按约定时间发货', 'id': 2},{'name' : '商品描述不符', 'id': 3},{'name' : '快递物流无跟踪记录', 'id': 4}]],"columnCount":3,"isLinkWork":false, "title": "hhhhh"}]
 */

public class MultiWheelPickerManager implements WheelPicker.OnItemSelectedListener, View.OnClickListener {

    public static final String TAG = MultiWheelPickerManager.class.getSimpleName();

    private CordovaPlugin mCordovaPlugin;
    private CallbackContext mCallbackContext;

    private FrameLayout mRootView;
    private View mMultiWheelPickerView;
    private View mTouchRl;
    private View mCancelTv;
    private View mTitleTv;
    private View mConfirmTv;
    private WheelPicker mWheelLeftWp;
    private WheelPicker mWheelCenterWp;
    private WheelPicker mWheelRightWp;

    private int mWheelPickerNumber = 0;
    private boolean linkage;

    private int mLeftPostion, mCenterPostion, mRightPostion;

    private ArrayList<String> mLeftList = new ArrayList<String>();
    private ArrayList<String> mCenterList = new ArrayList<String>();
    private ArrayList<String> mRightList = new ArrayList<String>();

    private Activity mActivity;
    private String mPackageName;
    private JSONArray mJsonArray;
    private String mTitle;

    private String mKeyColumnCount = "columnCount";
    private String mKeyIsLinkWork = "isLinkWork";
    private String mKeyDataArray = "dataArray";
    private String mKeyTitle = "title";
    private String mKeyObjectArray = "objectArray";

    public MultiWheelPickerManager(CordovaPlugin cordovaPlugin, CallbackContext callbackContext, JSONArray jsonArray) {
        mCordovaPlugin = cordovaPlugin;
        mCallbackContext = callbackContext;
        mActivity = cordovaPlugin.cordova.getActivity();
        mPackageName = mActivity.getPackageName();

        if (jsonArray.length() != 0) {
            initData(jsonArray);
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
            Log.i(TAG, "初始化数据:" + jsonObject.toString());
            mWheelPickerNumber = jsonObject.getInt(mKeyColumnCount);
            linkage = jsonObject.getBoolean(mKeyIsLinkWork);
            mTitle = jsonObject.getString(mKeyTitle);

            mJsonArray = jsonObject.getJSONArray(mKeyDataArray);

            switch (mWheelPickerNumber) {
                case 3:
                    if (linkage) {
                        getNameList(mRightList, mJsonArray.getJSONObject(0).getJSONArray(mKeyObjectArray).getJSONObject(0).getJSONArray(mKeyObjectArray));
                    } else {
                        getNameList(mRightList, mJsonArray.getJSONArray(2));
                    }

                case 2:
                    if (linkage) {
                        getNameList(mCenterList, mJsonArray.getJSONObject(0).getJSONArray(mKeyObjectArray));
                    } else {
                        getNameList(mCenterList, mJsonArray.getJSONArray(1));
                    }
                case 1:
                    if (linkage) {
                        getNameList(mLeftList, mJsonArray);
                    } else {
                        getNameList(mLeftList, mJsonArray.getJSONArray(0));
                    }
                default:
                    break;
            }

            initView();

        } catch (JSONException e) {
            e.printStackTrace();
            mCallbackContext.error("初始化数据解析异常");
        }
    }

    /**
     * 获取数据的名称显示集合
     *
     * @param arrayList
     * @param jsonArray
     */
    private void getNameList(ArrayList<String> arrayList, JSONArray jsonArray) {
        if (arrayList != null && jsonArray != null) {
            arrayList.clear();
            int length = jsonArray.length();
            try {
                for (int i = 0; i < length; i++) {
                    arrayList.add(jsonArray.getJSONObject(i).getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private int getViewIdentifier(String viewId) {
        return mActivity.getResources().getIdentifier(viewId, "id", mPackageName);
    }

    private int getLayoutIdentifier(String layoutId) {
        return mActivity.getResources().getIdentifier(layoutId, "layout", mPackageName);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        //UI 线程显示操作
        mCordovaPlugin.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mRootView = ((FrameLayout) (((Activity) mCordovaPlugin.webView.getContext()).getWindow().getDecorView().findViewById(android.R.id.content)));
                mMultiWheelPickerView = LayoutInflater.from(mCordovaPlugin.webView.getContext()).inflate(getLayoutIdentifier("layout_multi_wheel_picker"), null);

                mTouchRl = mMultiWheelPickerView.findViewById(getViewIdentifier("touch_rl"));
                mCancelTv = mMultiWheelPickerView.findViewById(getViewIdentifier("cancle_tv"));
                mTitleTv = mMultiWheelPickerView.findViewById(getViewIdentifier("title_tv"));
                mConfirmTv = mMultiWheelPickerView.findViewById(getViewIdentifier("confirm_tv"));

                mWheelLeftWp = (WheelPicker) mMultiWheelPickerView.findViewById(getViewIdentifier("wheel_left_wp"));
                mWheelCenterWp = (WheelPicker) mMultiWheelPickerView.findViewById(getViewIdentifier("wheel_center_wp"));
                mWheelRightWp = (WheelPicker) mMultiWheelPickerView.findViewById(getViewIdentifier("wheel_right_wp"));

                if (!TextUtils.isEmpty(mTitle)) {
                    ((TextView) mTitleTv).setText(mTitle);
                }

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
            JSONArray jsonArray = new JSONArray();
            try {
                switch (mWheelPickerNumber) {
                    case 1:
                        if (linkage) {
                            jsonArray.put(mJsonArray.getJSONObject(mLeftPostion));
                        } else {
                            jsonArray.put(mJsonArray.getJSONArray(0).getJSONObject(mLeftPostion));
                        }
                        break;
                    case 2:
                        if (linkage) {
                            jsonArray.put(mJsonArray.getJSONObject(mLeftPostion));
                            jsonArray.put(mJsonArray.getJSONObject(mLeftPostion).getJSONArray(mKeyObjectArray).getJSONObject(mCenterPostion));
                        } else {
                            jsonArray.put(mJsonArray.getJSONArray(0).getJSONObject(mLeftPostion));
                            jsonArray.put(mJsonArray.getJSONArray(1).getJSONObject(mCenterPostion));
                        }
                        break;
                    case 3:
                        if (linkage) {
                            jsonArray.put(mJsonArray.getJSONObject(mLeftPostion));
                            jsonArray.put(mJsonArray.getJSONObject(mLeftPostion).getJSONArray(mKeyObjectArray).getJSONObject(mCenterPostion));
                            if (mJsonArray.getJSONObject(mLeftPostion).getJSONArray(mKeyObjectArray).getJSONObject(mCenterPostion).getJSONArray(mKeyObjectArray).length() > 0) {
                                jsonArray.put(mJsonArray.getJSONObject(mLeftPostion).getJSONArray(mKeyObjectArray).getJSONObject(mCenterPostion).getJSONArray(mKeyObjectArray).getJSONObject(mRightPostion));
                            }
                        } else {
                            jsonArray.put(mJsonArray.getJSONArray(0).getJSONObject(mLeftPostion));
                            jsonArray.put(mJsonArray.getJSONArray(1).getJSONObject(mCenterPostion));
                            jsonArray.put(mJsonArray.getJSONArray(2).getJSONObject(mRightPostion));
                        }
                        break;
                }
                mCallbackContext.success(jsonArray.toString());
                Log.i(TAG, "返回的数据：" + jsonArray.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                mCallbackContext.error(e.getMessage());
            }
        }
    }

    @Override
    public void onItemSelected(WheelPicker picker, Object data, int position) {
        try {
            if (picker.getId() == getViewIdentifier("wheel_left_wp")) {
                mLeftPostion = position;
                //是否联动
                if (linkage) {
                    getNameList(mCenterList, mJsonArray.getJSONObject(mLeftPostion).getJSONArray(mKeyObjectArray));
                    mWheelCenterWp.setData(mCenterList);
                    mWheelCenterWp.setSelectedItemPosition(0);
                    getNameList(mRightList, mJsonArray.getJSONObject(mLeftPostion).getJSONArray(mKeyObjectArray).getJSONObject(0).getJSONArray(mKeyObjectArray));
                    mWheelRightWp.setData(mRightList);
                    mWheelRightWp.setSelectedItemPosition(0);
                }
            } else if (picker.getId() == getViewIdentifier("wheel_center_wp")) {
                mCenterPostion = position;
                if (linkage) {
                    getNameList(mRightList, mJsonArray.getJSONObject(mLeftPostion).getJSONArray(mKeyObjectArray).getJSONObject(mCenterPostion).getJSONArray(mKeyObjectArray));
                    mWheelRightWp.setData(mRightList);
                    mWheelRightWp.setSelectedItemPosition(0);
                }
            } else if (picker.getId() == getViewIdentifier("wheel_right_wp")) {
                mRightPostion = position;
            }
        } catch (JSONException e) {
            e.printStackTrace();
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
