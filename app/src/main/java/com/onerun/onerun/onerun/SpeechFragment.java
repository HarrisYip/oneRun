package com.onerun.onerun.onerun;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Locale;

/**
 * Created by Terrence on 2/24/2015.
 */
public class SpeechFragment extends Fragment implements TextToSpeech.OnInitListener{
    private TextToSpeech tts;
    private int My_DATA_CHECK_CODE = 0;

    public static SpeechFragment newInstance(int sectionNumber) {
        SpeechFragment frag = new SpeechFragment();
        Bundle args = new Bundle();
        args.putInt("SPEECH_SECTION_NUMBER", sectionNumber);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.speech_fragment,
                container, false);

        //Button speaker  = (Button)getView().findViewById(R.id.speak);
        //speaker.setOnClickListener(this);
        //speaker.setEnabled(false);

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, My_DATA_CHECK_CODE);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == My_DATA_CHECK_CODE) {
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                tts = new TextToSpeech(getActivity(), this);
            } else {
                Intent installTTSIntent = new Intent();
                installTTSIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installTTSIntent);
            }
        }
    }

    //@Override
    public void speakText(View v) {
        EditText myText = (EditText) v.findViewById(R.id.enter);
        String myStr = myText.getText().toString();
        speak(myStr);
    }

    public void speak(String sentence) {
        //tts.speak(sentence, tts.QUEUE_FLUSH, null, null);
        tts.speak(sentence, tts.QUEUE_FLUSH, null);
    }

    @Override
    public void onInit(int status) {
        if(status == TextToSpeech.SUCCESS) {
            if(tts.isLanguageAvailable(Locale.CANADA) == TextToSpeech.LANG_AVAILABLE)
                tts.setLanguage(Locale.CANADA);
        }
    }
}
