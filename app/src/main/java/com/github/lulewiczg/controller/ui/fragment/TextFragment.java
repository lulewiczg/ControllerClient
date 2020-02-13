package com.github.lulewiczg.controller.ui.fragment;

import android.os.Bundle;
import androidx.annotation.NonNull;
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
        Client.get().doActionFast(textAction, getActivity());
        textInput.setText("");
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_text, container, false);
        textInput = view.findViewById(R.id.text_send_input);
        View button = view.findViewById(R.id.button);
        button.setOnClickListener(v -> sendText());
        return view;
    }
}
