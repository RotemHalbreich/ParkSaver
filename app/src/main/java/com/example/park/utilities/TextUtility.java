package com.example.park.utilities;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.EditText;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TextUtility {

    public static boolean isValid(EditText... etText) {
        for (EditText editText : etText) {
            if (editText.getText().toString().trim().length() <= 0) {
                return false;
            }
        }
        return true;
    }

    public static <T> String toJson(T src) {
        return new Gson().toJson(src);
    }

    public static <T> T fromJson(String src, Class<T> tClass) {
        if (TextUtils.isEmpty(src)) {
            return null;
        }
        return new Gson().fromJson(src, TypeToken.of(tClass).getType());
    }

    public static String getCurrentDate() {
        Calendar cal = Calendar.getInstance();
        Date currentLocalTime = cal.getTime();
        SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
        return date.format(currentLocalTime);
    }

    public static byte[] getBytes(InputStream inputStream) {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        try {
            int len = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }
        } catch (Exception e) {

        }
        return byteBuffer.toByteArray();
    }

    public static boolean isFromHourToHourValid(String from, String to) {
        int f = getNumberFromHour(from);
        int t = getNumberFromHour(to);
        return t > f;
    }

    public static int getNumberFromHour(String hour) {
        return Integer.parseInt(hour.replace(":", ""));
    }

    // 1-3     2-5
    public static boolean isOveerLapping(int x1, int x2, int y1, int y2) {
        return Math.max(x1, y1) < Math.min(x2, y2);
    }

    public static int getNumberOfHours(String from, String to) {
        int f = getNumberFromHour(from);
        int t = getNumberFromHour(to);
        return (t - f) / 100;
    }


    public static boolean checkOverlapping(String fromHour, String toHour, List<String> notAvliableList) {
        int f = getNumberFromHour(fromHour);
        int t = getNumberFromHour(toHour);
        if (notAvliableList == null) {
            return true;
        }
        for (String s : notAvliableList) {
            String arr[] = s.split("-");
            int f2 = getNumberFromHour(arr[0]);
            int t2 = getNumberFromHour(arr[1]);
            if (isOveerLapping(f, t, f2, t2)) {
                return false;
            }
        }
        return true;

    }
}
