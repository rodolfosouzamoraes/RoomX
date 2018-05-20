#include <SPI.h>
#include <Ethernet.h>
#include <Thermistor.h>

byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
byte ip[] = { 192, 168, 1, 100 };

Thermistor temp(0);

EthernetServer server(5560);
char msg;
//Porta ligada ao pino IN1 do modulo
int porta_rele1 = 23;
int porta_rele2 = 24;
int porta_rele3 = 25;
bool Ativa_Lamp1 = true;
bool Ativa_Lamp2 = true;
bool Ativa_Tomada1 = true;

void setup() {
  // put your setup code here, to run once:
  Ethernet.begin(mac, ip);
  server.begin();
  pinMode(porta_rele1, OUTPUT); 
  pinMode(porta_rele2, OUTPUT); 
  pinMode(porta_rele3, OUTPUT); 
  digitalWrite(porta_rele1, HIGH);
  digitalWrite(porta_rele2, HIGH);
  digitalWrite(porta_rele3, HIGH);
  
}

void loop() {
  // put your main code here, to run repeatedly:
  EthernetClient client = server.available();
   if(client.connected()){
    msg = client.read();
      switch(msg){
       case 'A' :
        if(Ativa_Lamp1 == true)
        {  
          digitalWrite(porta_rele1, LOW); 
          Ativa_Lamp1 = false;
        }
        else
        {
          digitalWrite(porta_rele1, HIGH);
          Ativa_Lamp1 = true;
        }
       break;
       case 'B' :
        if(Ativa_Lamp2 == true)
        {  
          digitalWrite(porta_rele2, LOW); 
          Ativa_Lamp2 = false;
        }
        else
        {
          digitalWrite(porta_rele2, HIGH);
          Ativa_Lamp2 = true;
        }
       break;
       case 'C' :
        if(Ativa_Tomada1 == true)
        {  
          digitalWrite(porta_rele3, LOW); 
          Ativa_Tomada1 = false;
        }
        else
        {
          digitalWrite(porta_rele3, HIGH);
          Ativa_Tomada1 = true;
        }
       break;
       }
       float temperature = temp.getTemp();
         char convertido[6]="";
         dtostrf(temperature, 6,2, convertido);
         client.write(convertido);
    }
    
    //delay(1000);
}
