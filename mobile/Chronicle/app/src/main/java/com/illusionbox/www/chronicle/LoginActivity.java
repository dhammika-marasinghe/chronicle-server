package com.illusionbox.www.chronicle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {

    private Button btn_login;
    private EditText txt_username;
    private Context context;
    private Intent intent;
    View.OnClickListener login = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (txt_username.getText().toString() != null && !txt_username.getText().toString().equalsIgnoreCase("")) {
                ChroniclePreferences.setPreference(context, "username", txt_username.getText().toString());
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                CommonDialogUtils.getAlertDialogWithOneButton(context, "Please enter your username", "Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                }).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login = (Button) findViewById(R.id.btn_login);
        txt_username = (EditText) findViewById(R.id.login_txt);

        context = this;

        intent = new Intent(context, MainActivity.class);
        if (ChroniclePreferences.getPreference(context, "username") != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }

        btn_login.setOnClickListener(login);
    }
}
