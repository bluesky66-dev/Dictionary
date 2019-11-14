package com.project.dictionary;

import android.content.Context;

import com.orhanobut.hawk.Hawk;

import java.util.ArrayList;

public class HawkUtil {

    private static final String KEY = "HAWK_KEY";

    private ArrayList<DictionaryData> orgData;

    boolean isEmpty() {
        return orgData.isEmpty();
    }

    HawkUtil(Context context) {
        Hawk.init(context).build();
        orgData = getAll();
        if (orgData == null) {
            orgData = new ArrayList<>();
            overwriteList();
        }
    }

    void add(DictionaryData data) {
        int size = orgData.size();
        for (int i = 0; i < size; i++) {
            if (data.equals(orgData.get(i))) {
                return;
            }
        }
        orgData.add(data);
    }

    boolean has(DictionaryData data) {
        int size = orgData.size();
        for (int i = 0; i < size; i++) {
            if (data.equals(orgData.get(i))) {
                return true;
            }
        }
        return false;
    }

    void remove(DictionaryData data) {
        int size = orgData.size();
        for (int i = 0; i < size; i++) {
            if (data.equals(orgData.get(i))) {
                orgData.remove(i);
                break;
            }
        }
    }

    void overwriteList() {
        Hawk.put(KEY, orgData);
    }

    ArrayList<DictionaryData> getAll() {
        return Hawk.get(KEY);
    }
}
