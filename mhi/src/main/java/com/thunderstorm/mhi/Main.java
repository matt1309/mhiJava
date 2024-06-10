package com.thunderstorm.mhi;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

AirCon aircon = new AirCon();
RacParser parser = new RacParser();


//some test examples i plan to run
aircon.sethostname("192.168.0.150");
aircon.setport("5443");

try{
aircon.parser.translateBytes(aircon, aircon.getAirconStats(false));
}catch (Exception e){

    System.out.println(e.toString());
}

//This would be the part adjusted by user on openhab side. this is turning on the unit. 
aircon.setOperation(true);

//tranlsating command into base64 for the unit
String command = parser.toBase64(aircon);

try{
//sending command to the aircon unit itself
String response = aircon.sendAircoCommand(aircon.getAircoId(), command);

aircon.parser.translateBytes(aircon, response);



}catch (Exception e){

    System.out.println(e.toString());
}



    }
}