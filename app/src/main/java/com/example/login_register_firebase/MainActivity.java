package com.example.login_register_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private EditText nameEditText,emailEditText,numberEditText,passwordEditText;
    private Button registerButton;
    private TextView loginText;
    private FirebaseAuth fauth;
    private FirebaseFirestore fstore;
    String name="";
    String email="";
    String password="";
    String number="";
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nameEditText=findViewById(R.id.nameEditText);
        emailEditText=findViewById(R.id.emailEditText);
        passwordEditText=findViewById(R.id.passwordEditText);
        numberEditText=findViewById(R.id.numberEditText);
        registerButton=findViewById(R.id.registerButton);
        loginText=findViewById(R.id.loginTextView);
        fauth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();

        if(fauth.getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this, UserActivity.class));
            finish();
        }

        registerButtonCLick();

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
            }
        });
    }

    public void registerButtonCLick(){

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=nameEditText.getText().toString();
                email=emailEditText.getText().toString();
                password=passwordEditText.getText().toString();
                number=numberEditText.getText().toString();
                if(name.equals("") || email.equals("") || password.equals("") || number.equals("")){
                    Toast.makeText(MainActivity.this,"Fill info in all the fields",Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                    fauth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(MainActivity.this,"Registered",Toast.LENGTH_SHORT).show();

                                userId=fauth.getUid();
                                DocumentReference documentReference=fstore.collection("users").document(userId);
                                Map<String,Object> data=new HashMap<>();
                                data.put("username",name);
                                data.put("useremail",email);
                                data.put("userphone",number);
                                documentReference.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                       Toast.makeText(MainActivity.this,"Data uploaded to cloud",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this,"Data wasn't uploaded to cloud",Toast.LENGTH_SHORT).show();
                                        Log.d("data error",e.getMessage());
                                    }
                                });
                                startActivity(new Intent(MainActivity.this,UserActivity.class));
                            }else{
                                Toast.makeText(MainActivity.this,"Not Registered",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}