#include <SPI.h>
#include <Ethernet.h>
#include <Thermistor.h>
#include <Ultrasonic.h>

byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
byte ip[] = { 192, 168, 100, 95 };
//EthernetClient client;

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

//Inicializa o sensor nos pinos definidos acima
#define pino_trigger 4
#define pino_echo 5
Ultrasonic ultrasonic(pino_trigger, pino_echo);

//Servidor Email
EthernetClient clientEmail;
byte serverEmail[] = {200,147,99,132}; // Colocar o ip do servidor SMTP 179.107.143.71
int portaEmail = 587;
bool enviaEmail = false;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(9600);
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
  
    Ultrasonico();
    delay(500);
}

void Ultrasonico(){
  //Le as informacoes do sensor, em cm e pol
  float cmMsec, inMsec;
  long microsec = ultrasonic.timing();
  cmMsec = ultrasonic.convert(microsec, Ultrasonic::CM);
  //Exibe informacoes no serial monitor
  //Serial.print("Distancia em cm: ");
  //Serial.print(cmMsec);
  //Serial.print("\n");
  if(cmMsec>9){
    if(!enviaEmail){
      if(EnviaEmail()){
        Serial.println(F("Email sent"));
      }
      else {
        Serial.println(F("Email failed"));
        delay(1000);
        enviaEmail = false;
      }
    }
  }
  else{
    enviaEmail = false;
  }
}

byte EnviaEmail() {
  byte thisByte = 0;
  byte respCode;
  Serial.println(F("conectando..."));
  if(clientEmail.connect(serverEmail,portaEmail)) {
  Serial.println(F("connected"));
  } else {
  Serial.println(F("connection failed"));
  return 0;
  }

  if(!eRcv()) return 0;

  Serial.println(F("Sending hello"));
  // replace 1.2.3.4 with your Arduino's ip
  
  clientEmail.println("HELO 192.168.100.95");
  if(!eRcv()) return 0;

  //clientEmail.println("STARTTLS oAuth2\r\n");


  Serial.println(F("Sending auth login"));
  clientEmail.println("auth login");
  if(!eRcv()) return 0;

  Serial.println("Sending User");
  // Change to your base64 encoded user
  clientEmail.println("xxxxxx"); 


  if(!eRcv()) return 0;

  Serial.println("Sending Password");
  // change to your base64 encoded password
  clientEmail.println("xxxxxx"); 


  if(!eRcv()) return 0;

  // change to your email address (sender)
  Serial.println(F("Sending From"));
  clientEmail.println("MAIL From: <xxxxxx@bol.com.br>");
  if(!eRcv()) return 0;

  // change to recipient address
  Serial.println(F("Sending To"));
  clientEmail.println("RCPT To: <xxxxxx@gmail.com>");
  if(!eRcv()) return 0;

  Serial.println(F("Sending DATA"));
  clientEmail.println("DATA");
  if(!eRcv()) return 0;

  Serial.println(F("Sending email"));

  // change to recipient address
  clientEmail.println("To: Rodolfo Souza <xxxxxx@gmail.com>");

  // change to your address
  clientEmail.println("From: Roomx AI <xxxxxx@bol.com.br>");

  clientEmail.println("Subject: RoomX"); 

  clientEmail.println("Ola, a central foi aberta!");

  clientEmail.println(".");

  if(!eRcv()) return 0;

  Serial.println(F("Sending QUIT"));
  clientEmail.println("QUIT");
  if(!eRcv()) return 0;

  clientEmail.stop();

  Serial.println(F("disconnected"));
  enviaEmail = true;
  return 1;
}

byte eRcv()
{
  byte respCode;
  byte thisByte;
  int loopCount = 0;

  while(!clientEmail.available()) {
    delay(1);
    loopCount++;

    // if nothing received for 10 seconds, timeout
    if(loopCount > 10000) {
        clientEmail.stop();
        Serial.println(F("\r\nTimeout"));
        return 0;
      }
    }

    respCode = clientEmail.peek();

    while(clientEmail.available())
    { 
      thisByte = clientEmail.read(); 
      Serial.write(thisByte);
    }

    if(respCode >= '4')
    {
      efail();
      return 0; 
   }

  return 1;
}

void efail()
{
  byte thisByte = 0;
  int loopCount = 0;

  clientEmail.println(F("QUIT"));

  while(!clientEmail.available()) {
    delay(1);
    loopCount++;

    // if nothing received for 10 seconds, timeout
    if(loopCount > 10000) {
        clientEmail.stop();
        Serial.println(F("\r\nTimeout"));
        return;
      }
  }

  while(clientEmail.available())
  { 
    thisByte = clientEmail.read(); 
    Serial.write(thisByte);
  }

  clientEmail.stop();

  Serial.println(F("disconnected"));
}
