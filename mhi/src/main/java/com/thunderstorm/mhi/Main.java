package com.thunderstorm.mhi;

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");

AirCon aircon = new AirCon();



//some test examples i plan to run
aircon.sethostname("192.168.0.150");
aircon.setport("5443");


//initial getting of aircon data. 

try{
aircon.parser.translateBytes(aircon.getAirconStats(false));
}catch (Exception e){

    System.out.println(e.toString());
}


//editing the operation data to on. 
aircon.setOperation(true);

//tranlsating command into base64 for the unit
String command = aircon.parser.toBase64();

try{
//sending command to the aircon unit itself
String response = aircon.sendAircoCommand(aircon.getAircoId(), command);

aircon.parser.translateBytes(response);



}catch (Exception e){

    System.out.println(e.toString());
}



    }
}