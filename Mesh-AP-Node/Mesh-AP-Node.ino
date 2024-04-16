#include "painlessMesh.h"
#include <Arduino_JSON.h>

#define MESH_PREFIX "SensorMesh"
#define MESH_PASSWORD "MESHpassword"
#define MESH_PORT 5555

const String type = "ROOT";
painlessMesh mesh;
DynamicJsonDocument jsonReadings(1024);
Scheduler meshUpdateScheduler;

void receivedCallback(uint32_t from, String &msg){
  Serial.println(msg.c_str());
}

void meshChangedCallback(){
  jsonReadings["type"] = type;
  jsonReadings["name"] = MESH_PREFIX;
  JsonArray nodeList = jsonReadings["message"].to<JsonArray>();

  auto nodes = mesh.getNodeList();
  nodeList.clear();

  for(auto nodeId : nodes){
    nodeList.add(nodeId);
  }

  String msg;
  serializeJson(jsonReadings, msg);
  msg.concat("\n");

  Serial.println(msg);
}

Task taskSendMeshUpdate(TASK_SECOND, TASK_FOREVER, &meshChangedCallback);

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  mesh.setDebugMsgTypes( ERROR | STARTUP | CONNECTION);

  mesh.init( MESH_PREFIX, MESH_PASSWORD, MESH_PORT, WIFI_AP, 6);

  //mesh.initOTAReceive("bridge");

  //mesh.stationManual(STATION_SSID, STATION_PASSWORD, STATION_PORT, station_ip);

  mesh.setRoot(true);
  mesh.setContainsRoot(true);

  mesh.onReceive(&receivedCallback);
  mesh.onChangedConnections(&meshChangedCallback);

  meshUpdateScheduler.addTask(taskSendMeshUpdate);
  taskSendMeshUpdate.enable();

}

void loop() {
  // put your main code here, to run repeatedly:
  mesh.update();
  meshUpdateScheduler.execute();
}
