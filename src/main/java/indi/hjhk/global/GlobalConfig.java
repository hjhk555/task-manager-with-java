package indi.hjhk.global;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GlobalConfig {
    private final String configFilePath;

    private final HashMap<String, String> configMap = new HashMap<>();

    public GlobalConfig(String configFilePath) {
        this.configFilePath = configFilePath;
    }

    public void read(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(configFilePath));
            String line;

            while ((line = reader.readLine()) != null){
                int indexEq = line.indexOf('=');
                if (indexEq == -1) continue;
                configMap.put(line.substring(0, indexEq), line.substring(indexEq+1));
            }

            reader.close();
        } catch (FileNotFoundException e) {
            return;
        } catch (IOException e) {
            e.printStackTrace();
            configMap.clear();
            return;
        }
    }

    public void write(){
        try{
            FileWriter writer = new FileWriter(configFilePath);
            for (Map.Entry<String, String> entry : configMap.entrySet()){
                writer.write(entry.getKey()+"="+entry.getValue()+"\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    public int getIntOrDefault(String key, int def){
        String strVal = configMap.get(key);
        if (strVal == null){
            configMap.put(key, String.valueOf(def));
            return def;
        }
        try {
            return Integer.parseInt(strVal);
        }catch (NumberFormatException e){
            configMap.put(key, String.valueOf(def));
            return def;
        }
    }

    public long getLongOrDefault(String key, long def){
        String strVal = configMap.get(key);
        if (strVal == null){
            configMap.put(key, String.valueOf(def));
            return def;
        }
        try {
            return Long.parseLong(strVal);
        }catch (NumberFormatException e){
            configMap.put(key, String.valueOf(def));
            return def;
        }
    }

    public String getStringOrDefault(String key, String def){
        String strVal = configMap.get(key);
        if (strVal == null){
            configMap.put(key, String.valueOf(def));
            return def;
        }
        return strVal;
    }

    public void setInt(String key, int val){
        configMap.put(key, String.valueOf(val));
    }

    public void setString(String key, String val){
        configMap.put(key, val);
    }
}
