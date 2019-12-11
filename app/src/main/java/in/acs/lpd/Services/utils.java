package in.acs.lpd.Services;

import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class utils {
    public static HashMap<String, String> groupMembersData = new HashMap<>();

    public static HashMap<String, String> editTextValues = new HashMap<>();

    public ServiceHelper getBaseClassService(String url, String header) {
        return new RetroHelper().getAdapter(url, header).create(ServiceHelper.class);
    }

    public static HashMap<String, JsonObject> saveGSData = new LinkedHashMap<>();
    public  static String workForVillages;
}

