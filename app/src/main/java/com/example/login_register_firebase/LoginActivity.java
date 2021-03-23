package com.example.login_register_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private EditText emailLogin,passwordLogin;
    private Button loginButton;
    private TextView registerText,forgotPassword;
    private String emailL;
    private String passwordL;
    private FirebaseAuth fauth1;
    String changePasswordEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailLogin=findViewById(R.id.emailLogin);
        passwordLogin=findViewById(R.id.passwordLogin);
        loginButton=findViewById(R.id.loginButton);
        registerText=findViewById(R.id.registerText);
        forgotPassword=findViewById(R.id.forgotPassword);
        fauth1=FirebaseAuth.getInstance();

        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText forgotPasswordTxt=new EditText(v.getContext());
                AlertDialog.Builder forgotPasswordDialog=new AlertDialog.Builder(v.getContext());
                forgotPasswordDialog.setTitle("Reset Password");
                forgotPasswordDialog.setMessage("Enter mail to send link:");
                forgotPasswordDialog.setView(forgotPasswordTxt);

                forgotPasswordDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        changePasswordEmail=forgotPasswordTxt.getText().toString();
                        fauth1.sendPasswordResetEmail(changePasswordEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(LoginActivity.this,"Link sent to respective mail",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(LoginActivity.this,"Not able to send link",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                forgotPasswordDialog.create().show();
            }
        });

        loginButtonClick();
    }

    private void loginButtonClick() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailL=emailLogin.getText().toString();
                passwordL=passwordLogin.getText().toString();
                if(emailL.equals("") || passwordL.equals("")){
                    Toast.makeText(LoginActivity.this,"Fill info in the fields",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    fauth1.signInWithEmailAndPassword(emailL,passwordL).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(LoginActivity.this,"Logged in successfully",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this,UserActivity.class));

                            }else{
                                Toast.makeText(LoginActivity.this,"Wrong credentials",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

    }
}