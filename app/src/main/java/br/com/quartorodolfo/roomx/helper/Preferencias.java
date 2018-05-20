package br.com.quartorodolfo.roomx.helper;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rod_s on 31/03/2018.
 */
public class Preferencias {
    private Context contexto;
    private SharedPreferences preferences;
    private final String NOME_ARQUIVO = "roomx.preferencias";
    private final int MODE = 0;
    private SharedPreferences.Editor editor;

    private final String CHAVE_IP = "ipArduino";
    private final String CHAVE_PORTA = "portaArduino";

    public Preferencias(Context contextoParametro) {
        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO,MODE);
        editor = preferences.edit();
    }

    public void salvarDados(String ipDoArduino, String nomeDoUsuario){
        editor.putString(CHAVE_IP,ipDoArduino);
        editor.putString(CHAVE_PORTA,nomeDoUsuario);
        editor.commit();
    }

    public String getIP() {
        return preferences.getString(CHAVE_IP, null);
    }

    public String getPORTA() {
        return preferences.getString(CHAVE_PORTA, null);
    }
}
