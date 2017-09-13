package com.ancsdemo;

/**
 * Package com.ancsdemo.
 * Created by yaoh on 2017/09/13.
 * Company Beacool IT Ltd.
 * <p/>
 * Description:
 */

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * @author Javen
 *         数据库观察者
 */
public class SmsObserver extends ContentObserver {

    private static final String TAG = "SmsObserver";

    private ContentResolver mResolver;
    public SmsHandler smsHandler;

    public SmsObserver(ContentResolver mResolver, SmsHandler handler) {
        super(handler);
        this.mResolver = mResolver;
        this.smsHandler = handler;
    }

    @Override
    public void onChange(boolean selfChange) {
        Log.i(TAG, "SmsObserver---> 短信有改变");

        Cursor mCursor = mResolver.query(Uri.parse("content://sms/inbox"), new String[]{"_id",
                "address",
                "read",
                "body",
                "thread_id"}, "read=?", new String[]{"0"}, "date desc");

        if (mCursor == null) {
            return;
        } else {
            while (mCursor.moveToNext()) {
                SmsInfo _smsInfo = new SmsInfo();

                int _inIndex = mCursor.getColumnIndex("_id");
                if (_inIndex != -1) {
                    _smsInfo._id = mCursor.getString(_inIndex);
                }

                int thread_idIndex = mCursor.getColumnIndex("thread_id");
                if (thread_idIndex != -1) {
                    _smsInfo.thread_id = mCursor.getString(thread_idIndex);
                }

                int addressIndex = mCursor.getColumnIndex("address");
                if (addressIndex != -1) {
                    _smsInfo.smsAddress = mCursor.getString(addressIndex);
                }

                int bodyIndex = mCursor.getColumnIndex("body");
                if (bodyIndex != -1) {
                    _smsInfo.smsBody = mCursor.getString(bodyIndex);
                }

                int readIndex = mCursor.getColumnIndex("read");
                if (readIndex != -1) {
                    _smsInfo.read = mCursor.getString(readIndex);
                }


                // 根据你的拦截策略，判断是否不对短信进行操作;将短信设置为已读;将短信删除
                Log.e(TAG, "获取的短信内容为：" + _smsInfo.toString());

                Message msg = smsHandler.obtainMessage();
                _smsInfo.action = 0;             // 0不对短信进行操作;1将短信设置为已读;2将短信删除
                msg.obj = _smsInfo;
                smsHandler.sendMessage(msg);
            }
        }

        if (mCursor != null) {
            mCursor.close();
            mCursor = null;
        }
    }

    public static class SmsHandler extends Handler {
        private Context mcontext;

        public SmsHandler(Context context) {
            this.mcontext = context;
        }

        @Override
        public void handleMessage(Message msg) {
            SmsInfo smsInfo = (SmsInfo) msg.obj;

            if (smsInfo.action == 1) {
                ContentValues values = new ContentValues();
                values.put("read", "1");
                mcontext.getContentResolver().update(Uri.parse("content://sms/inbox"), values, "thread_id=?",
                        new String[]{smsInfo.thread_id});
            } else if (smsInfo.action == 2) {
                Uri mUri = Uri.parse("content://sms/");
                mcontext.getContentResolver().delete(mUri, "_id=?", new String[]{smsInfo._id});
            }
        }
    }

    class SmsInfo {
        public String _id = "";
        public String thread_id = "";
        public String smsAddress = "";
        public String smsBody = "";
        public String read = "";
        public int action = 0;// 1代表设置为已读，2表示删除短信

        @Override
        public String toString() {
            return "SmsInfo [_id=" + _id
                    + ", thread_id=" + thread_id
                    + ", smsAddress=" + smsAddress
                    + ", smsBody=" + smsBody
                    + ", read=" + read
                    + ", action=" + action + "]";
        }
    }
}