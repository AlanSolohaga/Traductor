package com.project.traductor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mannan.translateapi.Language;
import com.mannan.translateapi.TranslateAPI;

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int SPEECH_REQUEST_CODE = 0 ;
    ImageView grabar;
    TextView texto;
    TextView textoTraducido;
    Button btnTraducir;
    TextToSpeech mTTS;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        texto = findViewById(R.id.texto);
        textoTraducido = findViewById(R.id.textoTraducido);
        grabar = findViewById(R.id.grabar);

        grabar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grabarVoz();
            }
        });

        btnTraducir = findViewById(R.id.btnTraducir);
        btnTraducir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(texto.getText().toString().equals("")|| texto.getText().toString() == null){
                    Toast.makeText(MainActivity.this, "Ingrese algun texto", Toast.LENGTH_SHORT).show();
                }else{
                    traducir(texto.getText().toString());
                }
            }
        });
        mTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS){
                    int result = mTTS.setLanguage(Locale.ENGLISH);
                    if(result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS","Lenguaje no soported");
                    }else{
                        //button.setEnable(true);
                    }
                }else{
                    Log.e("TTS","Initialization failed");
                }
            }
        });
    }

    private void grabarVoz() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        // Start the activity, the intent will be populated with the speech text
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==SPEECH_REQUEST_CODE && resultCode== RESULT_OK){
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            Log.e("SPOKEN",spokenText);
            texto.setText(spokenText);
            traducir(spokenText);
        }
    }

    private void traducir(String texto) {
        TranslateAPI translate = new TranslateAPI(Language.AUTO_DETECT,Language.ENGLISH,texto);
        translate.setTranslateListener(new TranslateAPI.TranslateListener() {
            @Override
            public void onSuccess(String s) {

                textoTraducido.setText(s);
                hablar(s);
            }

            @Override
            public void onFailure(String s) {
                Toast.makeText(MainActivity.this, s, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void hablar(String text) {
        mTTS.setSpeechRate(1f);
        mTTS.speak(text,TextToSpeech.QUEUE_FLUSH,null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mTTS!=null){
            mTTS.stop();
            mTTS.shutdown();
        }
    }
}
