package br.com.quartorodolfo.roomx.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.NetPermission;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

import br.com.quartorodolfo.roomx.R;
import br.com.quartorodolfo.roomx.helper.Preferencias;

public class MainActivity extends AppCompatActivity {
    private ImageButton btnLampQtRodolfo;
    private ImageButton btnSetting;
    private String ipArduino;
    private String portaArduino;
    private Preferencias preferencias;
    private ImageButton btnGaragem;
    private ImageButton btnTomada;
    private String codigoLamp;
    private TextView txtTemperatura;
    private char[] vet;
    private ImageButton btnTemp;
    private AlertDialog.Builder dialog;

    private PrintWriter out;
    private BufferedReader in;
    private String sResult;
    private boolean mRun = true;
    final Handler handler = new Handler(Looper.getMainLooper());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLampQtRodolfo = (ImageButton) findViewById(R.id.btnLampQtRodolfoID);
        btnGaragem = (ImageButton) findViewById(R.id.btnGaragemID);
        btnSetting = (ImageButton) findViewById(R.id.btnSettingID);
        btnTomada = (ImageButton) findViewById(R.id.btnTomadaID);
        btnTemp = (ImageButton) findViewById(R.id.btnTempID);


        preferencias = new Preferencias(MainActivity.this);
        ipArduino = preferencias.getIP();
        portaArduino = preferencias.getPORTA();

//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                leTemp();
//            }
//        };
//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(timerTask, 0,1000);

        btnTomada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipArduino = preferencias.getIP();
                portaArduino = preferencias.getPORTA();
                if(ipArduino == null || portaArduino == null){
                    Toast.makeText(MainActivity.this,"Conexão não configurada!",Toast.LENGTH_SHORT).show();
                }
                else{
                    acendeLampada("C");
                }
            }
        });

        btnGaragem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipArduino = preferencias.getIP();
                portaArduino = preferencias.getPORTA();
                if(ipArduino == null || portaArduino == null){
                    Toast.makeText(MainActivity.this,"Conexão não configurada!",Toast.LENGTH_SHORT).show();
                }
                else{
                    acendeLampada("B");
                }
            }
        });

        btnLampQtRodolfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ipArduino = preferencias.getIP();
                portaArduino = preferencias.getPORTA();
                if(ipArduino == null || portaArduino == null){
                    Toast.makeText(MainActivity.this,"Conexão não configurada!",Toast.LENGTH_SHORT).show();
                }
                else{
                    acendeLampada("A");
                }
            }
        });

        btnSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        btnTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        ipArduino = preferencias.getIP();
                        portaArduino = preferencias.getPORTA();
                        if(ipArduino == null || portaArduino == null){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this,"Conexão não configurada!",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else{
                            leTemp();
                        }
                    }
                };
                Timer timer = new Timer();
                timer.schedule(timerTask,500);
            }
        });
    }

    private void leTemp(){
        Socket soc =null;
        mRun = true;
        try {
            soc = new Socket(ipArduino, Integer.parseInt(portaArduino));
            //dados enviados para o servidor
            out = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(soc.getOutputStream())), true);

            in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

            out.println("T");
            out.flush();

            while (mRun)
            {
                vet = new char[6];
                in.read(vet);
                sResult = Character.toString(vet[0]) + Character.toString(vet[1]) +Character.toString(vet[2]);

                if (sResult != null)
                {

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            //escreveTemp(sResult);
                            dialog = new AlertDialog.Builder(MainActivity.this);
                            dialog.setTitle("Temperatura");
                            dialog.setMessage(sResult + "°C");
                            dialog.setCancelable(false);
                            dialog.setIcon(R.drawable.ic_action_temperature);
                            dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dialog.create();
                            dialog.show();
                        }
                    });
                    mRun = false;
                }
            }
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }finally{
            try{
                if(soc!=null){
                    soc.close();
                }
            }catch (IOException e)
            {
                //aTcp.onExceptionOcorred(e.getMessage());
            }
        }

    }

    private void escreveTemp(String texto){
        txtTemperatura.setText(texto);
    }

    private void acendeLampada(String cod) {
        codigoLamp = cod;
        Thread t = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    Socket soc1 = new Socket(ipArduino, Integer.parseInt(portaArduino));
                    //dados enviados para o servidor
                    BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(soc1.getOutputStream()));
                    bw.write(codigoLamp);
                    bw.flush();
                    bw.close();
                } catch (UnknownHostException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        });
        t.start();
        t.interrupt();
    }
}

