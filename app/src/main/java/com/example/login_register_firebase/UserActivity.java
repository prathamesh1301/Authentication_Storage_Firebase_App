package com.example.login_register_firebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class UserActivity extends AppCompatActivity {
    private ImageView user_image,logoutImage,userImageIcon,resetPasswordImage,emailchangeImage,phoneNumberImage;
    private TextView user_name,user_email,user_phone;
    private FirebaseAuth fauth_user;
    private FirebaseFirestore fstoreL;
    private String userIdL;
    private FirebaseUser fuser;
    private StorageReference storageReference;
    String newPhone;
    private String UserIDU;
    String passwordChange;
    String emailChange;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        user_email=findViewById(R.id.user_email);
        user_name=findViewById(R.id.user_name);
        user_image=findViewById(R.id.user_image);
        user_phone=findViewById(R.id.user_phone);
        logoutImage=findViewById(R.id.logoutImage);
        resetPasswordImage=findViewById(R.id.resetPasswordImage);
        userImageIcon=findViewById(R.id.userImageIcon);
        emailchangeImage=findViewById(R.id.emailchangeImage);
        phoneNumberImage=findViewById(R.id.phoneNumberImage);
        fauth_user=FirebaseAuth.getInstance();
        fstoreL=FirebaseFirestore.getInstance();
        fuser=fauth_user.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference();

        StorageReference fref=storageReference.child("users/"+fauth_user.getUid()+"/profile.jpg");
        fref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(user_image);
            }
        });

        getData();

        resetPasswordImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText resetPasswordEditTxt=new EditText(v.getContext());
                AlertDialog.Builder passwordDialog=new AlertDialog.Builder(v.getContext());
                passwordDialog.setTitle("Reset Password");
                passwordDialog.setMessage("Enter new password:");
                passwordDialog.setView(resetPasswordEditTxt);
                passwordDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        passwordChange=resetPasswordEditTxt.getText().toString();
                        fuser.updatePassword(passwordChange).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UserActivity.this,"Password changed successfully",Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserActivity.this,"Password did not change",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
            passwordDialog.create().show();
            }
        });

        logoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fauth_user.signOut();
                Intent i=new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(i);

            }
        });

        emailchangeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText changeEmailTxt=new EditText(v.getContext());
                AlertDialog.Builder emailDialog=new AlertDialog.Builder(v.getContext());
                emailDialog.setTitle("Change Email");
                emailDialog.setMessage("Change Email to:");
                emailDialog.setView(changeEmailTxt);
                emailDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        emailChange=changeEmailTxt.getText().toString();
                        fuser.updateEmail(emailChange).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(UserActivity.this,"Email changed successfully",Toast.LENGTH_SHORT).show();
                                user_email.setText(emailChange);
                                UserIDU=fauth_user.getUid();
                                DocumentReference documentReference=fstoreL.collection("users").document(UserIDU);
                                Map<String,Object> dataUser=new HashMap<>();
                                dataUser.put("username",user_name.getText().toString());
                                dataUser.put("useremail",user_email.getText().toString());
                                dataUser.put("userphone",user_phone.getText().toString());
                                documentReference.update(dataUser).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(UserActivity.this,"Data updated in cloud",Toast.LENGTH_SHORT).show();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(UserActivity.this,"Data was not updated in cloud",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(UserActivity.this,"Email was not changed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                emailDialog.create().show();
            }
        });

        phoneNumberImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText phoneNumberTxt=new EditText(v.getContext());
                AlertDialog.Builder phoneDialog=new AlertDialog.Builder(v.getContext());
                phoneDialog.setTitle("Change Phone Number");
                phoneDialog.setMessage("Enter new phone number:");
                phoneDialog.setView(phoneNumberTxt);
                phoneDialog.setPositiveButton("Change", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newPhone=phoneNumberTxt.getText().toString();
                        UserIDU=fauth_user.getUid();
                        DocumentReference documentReference=fstoreL.collection("users").document(UserIDU);
                        Map<String,Object> phoneupdateData=new HashMap<>();
                        phoneupdateData.put("username",user_name.getText().toString());
                        phoneupdateData.put("useremail",user_email.getText().toString());
                        phoneupdateData.put("userphone",newPhone);
                        documentReference.update(phoneupdateData).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    user_phone.setText(newPhone);
                                    Toast.makeText(UserActivity.this,"Data updated",Toast.LENGTH_SHORT).show();
                                }else{
                                    Toast.makeText(UserActivity.this,"Error while changing number",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                phoneDialog.create().show();
            }
        });

        userImageIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGallery,100);

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri imageUri;
        if(requestCode==100){
            if(resultCode== Activity.RESULT_OK){
                imageUri=data.getData();
                //user_image.setImageURI(imageUri);
                uploadDataToFirebase(imageUri);
            }
        }
    }
    private void uploadDataToFirebase(Uri imageUri){
        StorageReference fref=storageReference.child("users/"+fauth_user.getUid()+"/profile.jpg");
        fref.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    //Toast.makeText(UserActivity.this,"Image uploaded to cloud",Toast.LENGTH_SHORT).show();
                    Picasso.get().load(imageUri).into(user_image);
                }else{
                    Toast.makeText(UserActivity.this,"Error while uploading image to cloud",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getData() {
        userIdL=fauth_user.getUid();
        DocumentReference documentReference=fstoreL.collection("users").document(userIdL);
        documentReference.addSnapshotListener(UserActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error!=null){
                    Toast.makeText(UserActivity.this,"Error while receiving data from cloud",Toast.LENGTH_SHORT).show();
                }else{
                    user_name.setText(value.getString("username"));
                    user_email.setText(value.getString("useremail"));
                    user_phone.setText(value.getString("userphone"));
                }
            }
        });

    }
}