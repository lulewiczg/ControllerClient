package com.github.lulewiczg.controller.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.github.lulewiczg.controller.R;
import com.github.lulewiczg.controller.actions.impl.TextAction;
import com.github.lulewiczg.controller.client.Client;


public class TextFragment extends ActionFragment {

    private EditText textInput;

    public void sendText() {
        String string = textInput.getText().toString();
        TextAction textAction = new TextAction(string);
        Client.get().doActionFast(textAction);
        textInput.setText("");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        textInput = view.findViewById(R.id.text_send_input);
        View button = view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendText();
            }
        });
        return view;
    }
}
