package com.thunderstorm.mhi;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONObject;

public class openhabTemplateGenerator {

    public static void openhabTemplateGenerator(ConcurrentHashMap<String,AirCon> airCons, String mqttIPString, String username, String password) {

        String[] templateFiles = { "openhabTemplates/template.items", "openhabTemplates/template.things",
                "openhabTemplates/template.sitemap","openhabTemplates/template.rules" };

        HashMap<String, String> keywords = new HashMap<String, String>();
   

        try {

            for (String airconIDKey : airCons.keySet()) {
                AirCon aircon = airCons.get(airconIDKey);

                if (aircon.name == null || aircon.name == "") {
                    keywords.put("$name", aircon.getAirConID());

                } else {
                    keywords.put("$name", aircon.getName());

                }
                keywords.put("$deviceID", aircon.getAirConID());
                keywords.put("$MQTTIP",mqttIPString);
                keywords.put("$username",username);
                keywords.put("$password",password);



                for (String filePath : templateFiles) {

                    String content = new String(Files.readAllBytes(Paths.get(filePath)));

                    for (String key : keywords.keySet()) {

                        content.replace(key, keywords.get(key));
                    }

                    // Save modified content back to the file
                    Files.write(Paths.get(filePath.replace("template.", aircon.getAirConID() + ".")),
                            content.getBytes());

                }

            }

            System.out.println("Keywords replaced successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
