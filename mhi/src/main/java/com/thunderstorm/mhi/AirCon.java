package com.thunderstorm.mhi;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


//import java.util.HashMap;
//import java.util.Map;
//import java.time.LocalDateTime;
//import java.time.Duration;

import org.apache.http.impl.client.CloseableHttpClient;
//import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
//import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import org.json.JSONObject;




public class AirCon {

    private final Object lock = new Object();

   // private List<String> commands = Arrays.asList("Operation", "OperationMode", "AirFlow", "WindDirectionUD", "WindDirectionLR", "PresetTemp", "Entrust");
 public RacParser parser = new RacParser(); 
    
public static Float[] outdoorTempList = {-50.0f, -50.0f, -50.0f, -50.0f, -50.0f, -48.9f, -46.0f, -44.0f, -42.0f, -41.0f, -39.0f, -38.0f, -37.0f, -36.0f, -35.0f, -34.0f, -33.0f, -32.0f, -31.0f, -30.0f, -29.0f, -28.5f, -28.0f, -27.0f, -26.0f, -25.5f, -25.0f, -24.0f, -23.5f, -23.0f, -22.5f, -22.0f, -21.5f, -21.0f, -20.5f, -20.0f, -19.5f, -19.0f, -18.5f, -18.0f, -17.5f, -17.0f, -16.5f, -16.0f, -15.5f, -15.0f, -14.6f, -14.3f, -14.0f, -13.5f, -13.0f, -12.6f, -12.3f, -12.0f, -11.5f, -11.0f, -10.6f, -10.3f, -10.0f, -9.6f, -9.3f, -9.0f, -8.6f, -8.3f, -8.0f, -7.6f, -7.3f, -7.0f, -6.6f, -6.3f, -6.0f, -5.6f, -5.3f, -5.0f, -4.6f, -4.3f, -4.0f, -3.7f, -3.5f, -3.2f, -3.0f, -2.6f, -2.3f, -2.0f, -1.7f, -1.5f, -1.2f, -1.0f, -0.6f, -0.3f, 0.0f, 0.2f, 0.5f, 0.7f, 1.0f, 1.3f, 1.6f, 2.0f, 2.2f, 2.5f, 2.7f, 3.0f, 3.2f, 3.5f, 3.7f, 4.0f, 4.2f, 4.5f, 4.7f, 5.0f, 5.2f, 5.5f, 5.7f, 6.0f, 6.2f, 6.5f, 6.7f, 7.0f, 7.2f, 7.5f, 7.7f, 8.0f, 8.2f, 8.5f, 8.7f, 9.0f, 9.2f, 9.5f, 9.7f, 10.0f, 10.2f, 10.5f, 10.7f, 11.0f, 11.2f, 11.5f, 11.7f, 12.0f, 12.2f, 12.5f, 12.7f, 13.0f, 13.2f, 13.5f, 13.7f, 14.0f, 14.2f, 14.4f, 14.6f, 14.8f, 15.0f, 15.2f, 15.5f, 15.7f, 16.0f, 16.2f, 16.5f, 16.7f, 17.0f, 17.2f, 17.5f, 17.7f, 18.0f, 18.2f, 18.5f, 18.7f, 19.0f, 19.2f, 19.4f, 19.6f, 19.8f, 20.0f, 20.2f, 20.5f, 20.7f, 21.0f, 21.2f, 21.5f, 21.7f, 22.0f, 22.2f, 22.5f, 22.7f, 23.0f, 23.2f, 23.5f, 23.7f, 24.0f, 24.2f, 24.5f, 24.7f, 25.0f, 25.2f, 25.5f, 25.7f, 26.0f, 26.2f, 26.5f, 26.7f, 27.0f, 27.2f, 27.5f, 27.7f, 28.0f, 28.2f, 28.5f, 28.7f, 29.0f, 29.2f, 29.5f, 29.7f, 30.0f, 30.2f, 30.5f, 30.7f, 31.0f, 31.3f, 31.6f, 32.0f, 32.2f, 32.5f, 32.7f, 33.0f, 33.2f, 33.5f, 33.7f, 34.0f, 34.3f, 34.6f, 35.0f, 35.2f, 35.5f, 35.7f, 36.0f, 36.3f, 36.6f, 37.0f, 37.2f, 37.5f, 37.7f, 38.0f, 38.3f, 38.6f, 39.0f, 39.3f, 39.6f, 40.0f, 40.3f, 40.6f, 41.0f, 41.3f, 41.6f, 42.0f, 42.3f, 42.6f, 43.0f};
public static Float[] indoorTempList = {-30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -30.0f, -29.0f, -28.0f, -27.0f, -26.0f, -25.0f, -24.0f, -23.0f, -22.5f, -22.0f, -21.0f, -20.0f, -19.5f, -19.0f, -18.0f, -17.5f, -17.0f, -16.5f, -16.0f, -15.0f, -14.5f, -14.0f, -13.5f, -13.0f, -12.5f, -12.0f, -11.5f, -11.0f, -10.5f, -10.0f, -9.5f, -9.0f, -8.6f, -8.3f, -8.0f, -7.5f, -7.0f, -6.5f, -6.0f, -5.6f, -5.3f, -5.0f, -4.5f, -4.0f, -3.6f, -3.3f, -3.0f, -2.6f, -2.3f, -2.0f, -1.6f, -1.3f, -1.0f, -0.5f, 0.0f, 0.3f, 0.6f, 1.0f, 1.3f, 1.6f, 2.0f, 2.3f, 2.6f, 3.0f, 3.2f, 3.5f, 3.7f, 4.0f, 4.3f, 4.6f, 5.0f, 5.3f, 5.6f, 6.0f, 6.3f, 6.6f, 7.0f, 7.2f, 7.5f, 7.7f, 8.0f, 8.3f, 8.6f, 9.0f, 9.2f, 9.5f, 9.7f, 10.0f, 10.3f, 10.6f, 11.0f, 11.2f, 11.5f, 11.7f, 12.0f, 12.3f, 12.6f, 13.0f, 13.2f, 13.5f, 13.7f, 14.0f, 14.2f, 14.5f, 14.7f, 15.0f, 15.3f, 15.6f, 16.0f, 16.2f, 16.5f, 16.7f, 17.0f, 17.2f, 17.5f, 17.7f, 18.0f, 18.2f, 18.5f, 18.7f, 19.0f, 19.2f, 19.5f, 19.7f, 20.0f, 20.2f, 20.5f, 20.7f, 21.0f, 21.2f, 21.5f, 21.7f, 22.0f, 22.2f, 22.5f, 22.7f, 23.0f, 23.2f, 23.5f, 23.7f, 24.0f, 24.2f, 24.5f, 24.7f, 25.0f, 25.2f, 25.5f, 25.7f, 26.0f, 26.2f, 26.5f, 26.7f, 27.0f, 27.2f, 27.5f, 27.7f, 28.0f, 28.2f, 28.5f, 28.7f, 29.0f, 29.2f, 29.5f, 29.7f, 30.0f, 30.2f, 30.5f, 30.7f, 31.0f, 31.3f, 31.6f, 32.0f, 32.2f, 32.5f, 32.7f, 33.0f, 33.2f, 33.5f, 33.7f, 34.0f, 34.2f, 34.5f, 34.7f, 35.0f, 35.3f, 35.6f, 36.0f, 36.2f, 36.5f, 36.7f, 37.0f, 37.2f, 37.5f, 37.7f, 38.0f, 38.3f, 38.6f, 39.0f, 39.2f, 39.5f, 39.7f, 40.0f, 40.3f, 40.6f, 41.0f, 41.2f, 41.5f, 41.7f, 42.0f, 42.3f, 42.6f, 43.0f, 43.2f, 43.5f, 43.7f, 44.0f, 44.3f, 44.6f, 45.0f, 45.3f, 45.6f, 46.0f, 46.2f, 46.5f, 46.7f, 47.0f, 47.3f, 47.6f, 48.0f, 48.3f, 48.6f, 49.0f, 49.3f, 49.6f, 50.0f, 50.3f, 50.6f, 51.0f, 51.3f, 51.6f, 52.0f};




    private String hostname;
    private String port="5443";
    private String DeviceID;
    private String OperatorID;
    private String AirConID;

    boolean status;
String firmware;
int connectedAccounts;
    
    private boolean outdoorTemperature;
    private Boolean Operation;
    private int OperationMode;
    private int AirFlow;
    private int WindDirectionUD;
    private int WindDirectionLR;
    private float PresetTemp;
    private boolean Entrust;
    private int ModelNr;
    private boolean Vacant;
    private boolean CoolHotJudge;

    private float indoorTemp;
    private float outdoorTemp;
    private float electric;
    private String errorCode;

    private boolean selfCleanOperation = false;
    private boolean selfCleanReset = false;

    private LocalDateTime nextRequestAfter;
    private long minrefreshRate = 1L;



    public String gethostname() {
        synchronized (lock) {
            return hostname;
        }
    }

    public void sethostname(String hostname) {
        synchronized (lock) {
            this.hostname = hostname;
        }
    }

    public String getport() {
        synchronized (lock) {
            return port;
        }
    }

    public void setport(String port) {
        synchronized (lock) {
            this.port = port;
        }
    }


    public String getDeviceID() {
        synchronized (lock) {
            return DeviceID;
        }
    }

    public void setDeviceID(String DeviceID) {
        synchronized (lock) {
            this.DeviceID = DeviceID;
        }
    }

    public String getOperatorID() {
        synchronized (lock) {
            return OperatorID;
        }
    }

    public void setOperatorID(String OperatorID) {
        synchronized (lock) {
            this.OperatorID = OperatorID;
        }
    }

    public String getAirConID() {
        synchronized (lock) {
            return AirConID;
        }
    }

    public void setAirConID(String AirConID) {
        synchronized (lock) {
            this.AirConID = AirConID;
        }
    }





    public boolean getstatus() {
        synchronized (lock) {
            return status;
        }
    }

    public void setstatus(boolean status) {
        synchronized (lock) {
            this.status = status;
        }
    }



    public String getfirmware() {
        synchronized (lock) {
            return firmware;
        }
    }

    public void setfirmware(String firmware) {
        synchronized (lock) {
            this.firmware = firmware;
        }
    }

    public int getconnectedAccounts() {
        synchronized (lock) {
            return connectedAccounts;
        }
    }

    public void setconnectedAccounts(int connectedAccounts) {
        synchronized (lock) {
            this.connectedAccounts = connectedAccounts;
        }
    }









    public boolean getOutdoorTemperature() {
        synchronized (lock) {
            return outdoorTemperature;
        }
    }

    public void setOutdoorTemperature(boolean outdoorTemperature) {
        synchronized (lock) {
            this.outdoorTemperature = outdoorTemperature;
        }
    }

    public Boolean getOperation() {
        synchronized (lock) {
            return Operation;
        }
    }

    public void setOperation(Boolean operation) {
        synchronized (lock) {
            Operation = operation;
        }
    }

    public int getOperationMode() {
        synchronized (lock) {
            return OperationMode;
        }
    }

    public void setOperationMode(int operationMode) {
        synchronized (lock) {
            OperationMode = operationMode;
        }
    }

    public int getAirFlow() {
        synchronized (lock) {
            return AirFlow;
        }
    }

    public void setAirFlow(int airFlow) {
        synchronized (lock) {
            AirFlow = airFlow;
        }
    }

    public int getWindDirectionUD() {
        synchronized (lock) {
            return WindDirectionUD;
        }
    }

    public void setWindDirectionUD(int windDirectionUD) {
        synchronized (lock) {
            WindDirectionUD = windDirectionUD;
        }
    }

    public int getWindDirectionLR() {
        synchronized (lock) {
            return WindDirectionLR;
        }
    }

    public void setWindDirectionLR(int windDirectionLR) {
        synchronized (lock) {
            WindDirectionLR = windDirectionLR;
        }
    }

    public float getPresetTemp() {
        synchronized (lock) {
            return PresetTemp;
        }
    }

    public void setPresetTemp(float presetTemp) {
        synchronized (lock) {
            PresetTemp = presetTemp;
        }
    }

    public boolean getEntrust() {
        synchronized (lock) {
            return Entrust;
        }
    }

    public void setEntrust(boolean entrust) {
        synchronized (lock) {
            Entrust = entrust;
        }
    }

    public int getModelNr() {
        synchronized (lock) {
            return ModelNr;
        }
    }

    public void setModelNr(int modelNr) {
        synchronized (lock) {
            ModelNr = modelNr;
        }
    }

    public boolean getVacant() {
        synchronized (lock) {
            return Vacant;
        }
    }

    public void setVacant(boolean vacant) {
        synchronized (lock) {
            Vacant = vacant;
        }
    }

    public boolean getCoolHotJudge() {
        synchronized (lock) {
            return CoolHotJudge;
        }
    }

    public void setCoolHotJudge(boolean coolHotJudge) {
        synchronized (lock) {
            CoolHotJudge = coolHotJudge;
        }
    }

    public float getIndoorTemp() {
        synchronized (lock) {
            return indoorTemp;
        }
    }

    public void setIndoorTemp(float indoorTemp) {
        synchronized (lock) {
            this.indoorTemp = indoorTemp;
        }
    }

    public float getOutdoorTemp() {
        synchronized (lock) {
            return outdoorTemp;
        }
    }

    public void setOutdoorTemp(float outdoorTemp) {
        synchronized (lock) {
            this.outdoorTemp = outdoorTemp;
        }
    }

    public float getElectric() {
        synchronized (lock) {
            return electric;
        }
    }

    public void setElectric(float electric) {
        synchronized (lock) {
            this.electric = electric;
        }
    }

    public String getErrorCode() {
        synchronized (lock) {
            return errorCode;
        }
    }

    public void setErrorCode(String errorCode) {
        synchronized (lock) {
            this.errorCode = errorCode;
        }
    }

    public boolean isSelfCleanOperation() {
        synchronized (lock) {
            return selfCleanOperation;
        }
    }

    public void setSelfCleanOperation(boolean selfCleanOperation) {
        synchronized (lock) {
            this.selfCleanOperation = selfCleanOperation;
        }
    }

    public boolean isSelfCleanReset() {
        synchronized (lock) {
            return selfCleanReset;
        }
    }

    public void setSelfCleanReset(boolean selfCleanReset) {
        synchronized (lock) {
            this.selfCleanReset = selfCleanReset;
        }
    }

    public LocalDateTime isnextRequestAfter() {
        synchronized (lock) {
            return nextRequestAfter;
        }
    }

    public void setSelfCleanReset(LocalDateTime nextRequestAfter) {
        synchronized (lock) {
            this.nextRequestAfter = nextRequestAfter;
        }
    }


    public long isminrefreshRate() {
        synchronized (lock) {
            return minrefreshRate;
        }
    }

    public void setSelfCleanReset(long minrefreshRate) {
        synchronized (lock) {
            this.minrefreshRate = minrefreshRate;
        }
    }





    private JSONObject post(String command, Map<String, Object> contents) throws Exception {
        String url = "http://" + hostname + ":" + port + "/beaver/command/" + command;
        JSONObject data = new JSONObject();
        data.put("apiVer", "1.0");
        data.put("command", command);
        data.put("deviceId", DeviceID);
        data.put("operatorId", OperatorID);
        data.put("timestamp", System.currentTimeMillis() / 1000);
        if (contents != null) {
            data.put("contents", new JSONObject(contents));
        }
    
        synchronized (lock) {
            CloseableHttpClient httpClient = null;
            JSONObject jsonResponse = null;
            try {
                long waitTime = Duration.between(LocalDateTime.now(), nextRequestAfter).getSeconds();
                if (waitTime > 0) {
                    Thread.sleep(waitTime * 1000);
                }
    
                httpClient = HttpClientBuilder.create().build();
                HttpPost request = new HttpPost(url);
                request.setHeader("Content-Type", "application/json");
                request.setEntity(new StringEntity(data.toString()));
    
                CloseableHttpResponse response = httpClient.execute(request);
    
                String responseString = EntityUtils.toString(response.getEntity());
                jsonResponse = new JSONObject(responseString);
    
                nextRequestAfter = LocalDateTime.now().plusSeconds(minrefreshRate);
            } catch (Exception e) {
                System.out.println(e.toString());
            } finally {
                if (httpClient != null) {
                    httpClient.close();
                }
            }
            return jsonResponse; // Return jsonResponse outside the try-catch block
        }
    }

    public String getInfo() throws Exception {
        JSONObject result = post("getDeviceInfo", null);
        return result.getString("contents");
    }

    public String getAircoId() throws Exception {
        return new JSONObject(getInfo()).getString("airconId");
    }

    public String updateAccountInfo(String aircoId, String timeZone) throws Exception {
        Map<String, Object> contents = new HashMap<>();
        contents.put("accountId", OperatorID);
        contents.put("airconId", AirConID);
        contents.put("remote", 0);
        contents.put("timezone", timeZone);
        JSONObject result = post("updateAccountInfo", contents);
        return result.getString("contents");
    }

    public String delAccountInfo(String aircoId) throws Exception {
        Map<String, Object> contents = new HashMap<>();
        contents.put("accountId", OperatorID);
        contents.put("airconId", AirConID);
        JSONObject result = post("deleteAccountInfo", contents);
        return result.getString("contents");
    }

    public String getAirconStats(boolean raw) throws Exception {
        JSONObject result = post("getAirconStat", null);
        return raw ? result.toString() : result.getString("contents");
    }

    public String sendAircoCommand(String aircoId, String command) throws Exception {
        Map<String, Object> contents = new HashMap<>();
        contents.put("airconId", AirConID);
        contents.put("airconStat", command);
        JSONObject result = post("setAirconStat", contents);
        return result.getJSONObject("contents").getString("airconStat");
    }



}
