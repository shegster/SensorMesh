/*PROGRAM FOR CCS811-SENSOR*/


/* Wiring:
  3v3 -> 3v3
  GND -> GND
  SDA -> 8
  SCL -> 9
  WAKE -> GND
  */



#include "painlessMesh.h"
#include <Arduino_JSON.h>
#include <Time.h>
#include <Wire.h>
#include "Adafruit_CCS811.h"

#define CCS811_ADDR 0x5A

Adafruit_CCS811 ccs;

// MESH Details
#define   MESH_PREFIX     "SensorMesh" //name for your MESH
#define   MESH_PASSWORD   "MESHpassword" //password for your MESH
#define   MESH_PORT       5555 //default port

//Number for this node
int nodeNumber = 3;
struct tm timeinfo;
time_t current_timestamp;
DynamicJsonDocument jsonReadings(1024);
unsigned long previousMillis = millis();
const String type = "CCS811";

//String to send to other nodes with sensor readings
String readings;

Scheduler userScheduler; // to control your personal task
painlessMesh  mesh;

void sendMessage() ; // Prototype so PlatformIO doesn't complain
void getReadings(); // Prototype for sending sensor readings

//Create tasks: to send messages and get readings;
Task taskSendMessage(TASK_SECOND * 1 , TASK_FOREVER, &sendMessage);

void getReadings() {

  unsigned long currentMillis = millis();

  if(ccs.readData()){
    Serial.println("ERROR!");
  }
  jsonReadings["name"] = "CCS811-Life";
  jsonReadings["node"] = nodeNumber;
  jsonReadings["id"] = mesh.getNodeId();
  jsonReadings["type"] = type;
  jsonReadings["timestamp"] = (currentMillis - previousMillis);
  jsonReadings["eco2"] = ccs.geteCO2();
  jsonReadings["tvoc"] = ccs.getTVOC(); 
  
  //************************
    Serial.println("*****************");
    Serial.print("Chip-ID: ");
    Serial.println(mesh.getNodeId());
    Serial.print("Timestamp: ");
    Serial.println((currentMillis - previousMillis)/1000);
    Serial.printf("Konzentration CO2: ");
    Serial.println(ccs.geteCO2());
    Serial.printf("Konzentration TVOC: ");
    Serial.println(ccs.getTVOC());
    Serial.println("*****************");
  //************************

}

void sendMessage () {
  getReadings();
  String msg;
  serializeJson(jsonReadings,msg);
  msg.concat("\n");
  Serial.println(mesh.sendBroadcast(msg) ? "Nachricht versendet" : "Nachricht konnte nicht gesendet werden");
}

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  delay(4000);

  if(!ccs.begin(CCS811_ADDR)) {
    Serial.println("Failed to start CCS811 sensor! Please check your wiring.");
    //while(1);
  } else{
    // Wait for the sensor to be ready
    while(!ccs.available());
    ccs.setDriveMode(CCS811_DRIVE_MODE_1SEC);
    Serial.println("CCS811 is ready.");
  }

  //mesh.setDebugMsgTypes( ERROR | MESH_STATUS | CONNECTION | SYNC | COMMUNICATION | GENERAL | MSG_TYPES | REMOTE ); // all types on
  mesh.setDebugMsgTypes( ERROR | STARTUP );  // set before init() so that you can see startup messages

  mesh.init( MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT , WIFI_AP_STA, 6);
  mesh.setContainsRoot(true);

  userScheduler.addTask(taskSendMessage);
  taskSendMessage.enable();

}

void loop() {
  // put your main code here, to run repeatedly:
  // it will run the user scheduler as well
  mesh.update();

}
