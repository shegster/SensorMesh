/*PROGRAM FOR BMP280-SENSOR*/
/*TODO:
    -implement right MESH-credentials
    -implement sendMessage(root)
    -clean-up code
    */

/*Wiring: 
  VCC -> 3v3
  GND -> GND
  SCL -> 6
  SDA -> 7
*/

#include <Adafruit_Sensor.h>
#include <Adafruit_BMP280.h>
#include "painlessMesh.h"
#include <Arduino_JSON.h>
#include <Time.h>


// MESH Details
#define   MESH_PREFIX     "SensorMesh" //name for your MESH
#define   MESH_PASSWORD   "MESHpassword" //password for your MESH
#define   MESH_PORT       5555 //default port

#define SDA_GPIO 7
#define SCL_GPIO 6

const String type = "BMP280";

//BME object on the default I2C pins
Adafruit_BMP280 bmp;

//Number for this node
int nodeNumber = 2;
struct tm timeinfo;
time_t current_timestamp;
DynamicJsonDocument jsonReadings(1024);
unsigned long previousMillis = millis();

//String to send to other nodes with sensor readings
String readings;

Scheduler userScheduler; // to control your personal task
painlessMesh  mesh;

// User stub
void sendMessage() ; // Prototype so PlatformIO doesn't complain
void getReadings(); // Prototype for sending sensor readings

//Create tasks: to send messages and get readings;
Task taskSendMessage(TASK_SECOND * 1 , TASK_FOREVER, &sendMessage);

void getReadings() {

  initBMP();

  unsigned long currentMillis = millis();

  jsonReadings["name"] = "BMP280-Life";
  jsonReadings["node"] = nodeNumber;
  jsonReadings["id"] = mesh.getNodeId();
  jsonReadings["type"] = type;
  jsonReadings["timestamp"] = (currentMillis - previousMillis);
  jsonReadings["temp"] = bmp.readTemperature();
  jsonReadings["pres"] = bmp.readPressure()/100.0F; 
  
  //************************
    Serial.println("*****************");
    Serial.print("Node-ID: ");
    Serial.println(mesh.getNodeId());
    Serial.print("Timestamp: ");
    Serial.println((currentMillis - previousMillis)/1000);
    Serial.printf("Temperatur: ");
    Serial.println(bmp.readTemperature());
    Serial.printf("Luftdruck: ");
    Serial.println(bmp.readPressure());
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

//Init BMP280
void initBMP(){

  if(!bmp.begin(0x76) && !bmp.begin(0x77)){
    Serial.println("BMP280 nicht gefunden worden, bitte verdrahtung überprüfen!");
  };
}

void setup() {
  // put your setup code here, to run once:
  Wire.begin(SDA_GPIO,SCL_GPIO);
  Serial.begin(115200);
  delay(4000);

  initBMP();

  //mesh.setDebugMsgTypes( ERROR | MESH_STATUS | CONNECTION | SYNC | COMMUNICATION | GENERAL | MSG_TYPES | REMOTE ); // all types on
  mesh.setDebugMsgTypes( ERROR | STARTUP );  // set before init() so that you can see startup messages

  mesh.init( MESH_PREFIX, MESH_PASSWORD, &userScheduler, MESH_PORT, WIFI_AP_STA, 6);
  mesh.setContainsRoot(true);

  userScheduler.addTask(taskSendMessage);
  taskSendMessage.enable();
}

void loop() {
  // put your main code here, to run repeatedly:
  // it will run the user scheduler as well
  mesh.update();

}
