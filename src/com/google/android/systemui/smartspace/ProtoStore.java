package com.google.android.systemui.smartspace;

import android.content.Context;
import android.util.Log;

import com.android.systemui.smartspace.nano.SmartspaceProto;
import com.google.protobuf.nano.MessageNano;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ProtoStore {
    private static final String TAG = "ProtoStore";

    public final Context mContext;

    public ProtoStore(Context context) {
        mContext = context.getApplicationContext();
    }

    public void store(SmartspaceProto.CardWrapper cardWrapper, String str) {
        try {
            FileOutputStream openFileOutput = mContext.openFileOutput(str, 0);
            if (cardWrapper != null) {
                openFileOutput.write(MessageNano.toByteArray(cardWrapper));
            } else {
                Log.d(TAG, "deleting " + str);
                mContext.deleteFile(str);
            }
            if (openFileOutput != null) {
                openFileOutput.close();
            }
        } catch (FileNotFoundException e) {
            Log.d(TAG, "file does not exist");
        } catch (Exception ex) {
            Log.e(TAG, "unable to write file", ex);
        }
    }

    public <T extends MessageNano> boolean load(String str, T t) {
        File fileStreamPath = mContext.getFileStreamPath(str);
        try {
            FileInputStream fileInputStream = new FileInputStream(fileStreamPath);
            int length = (int) fileStreamPath.length();
            byte[] bArr = new byte[length];
            fileInputStream.read(bArr, 0, length);
            MessageNano.mergeFrom(t, bArr);
            fileInputStream.close();
            return true;
        } catch (FileNotFoundException e) {
            Log.d(TAG, "no cached data");
            return false;
        } catch (Exception ex) {
            Log.e(TAG, "unable to load data", ex);
            return false;
        }
    }
}
