package br.com.quartorodolfo.roomx.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.github.rtoshiro.util.format.SimpleMaskFormatter;
import com.github.rtoshiro.util.format.text.MaskTextWatcher;

import br.com.quartorodolfo.roomx.R;
import br.com.quartorodolfo.roomx.helper.Preferencias;

public class SettingActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private EditText txtIP;
    private EditText txtPorta;
    private Button btnConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        toolbar = (Toolbar) findViewById(R.id.tbConfiguracoes);
        toolbar.setTitle("Configurações");
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        txtIP = (EditText) findViewById(R.id.txtIPID);
        txtPorta = (EditText) findViewById(R.id.txtPortaID);
        btnConfig = (Button) findViewById(R.id.btnConfigID);

        //=================== Mascaras nos TextBox =======
        SimpleMaskFormatter simpleMaskFormaterIP = new SimpleMaskFormatter("NNN.NNN.NNN.NNN");
        MaskTextWatcher maskTextWatcherIP = new MaskTextWatcher(txtIP,simpleMaskFormaterIP);
        txtIP.addTextChangedListener(maskTextWatcherIP);

        btnConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtPorta.getText().toString().isEmpty() || txtIP.getText().toString().isEmpty()){
                    Toast.makeText(SettingActivity.this, "Preencha todos os campos!",Toast.LENGTH_SHORT).show();
                }
                else{
                    Preferencias preferencias = new Preferencias(SettingActivity.this);
                    preferencias.salvarDados(txtIP.getText().toString(),txtPorta.getText().toString());
                    finish();
                }
            }
        });


    }
}
