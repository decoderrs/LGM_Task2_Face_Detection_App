package com.example.facedetectionapp;

/*package whatever do not write package name here*/

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.ml.vision.label.FirebaseVisionImageLabel;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button cameraButton;
    ImageView imageView;

    public static final int REQUEST_IMAGE_CAPTURE = 121;
    FirebaseVisionImage image;
    FirebaseVisionFaceDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        FirebaseApp.initializeApp(this);
        imageView =findViewById(R.id.imageView);
        cameraButton = findViewById(R.id.camera_btn);


        cameraButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Making a new intent for choosing a image
                        Intent i = new Intent();
                        i.setType("image/*");
                        i.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(i, "chose image"),REQUEST_IMAGE_CAPTURE );
                    }
                });
    }

   @Override
    protected  void  onActivityResult(int requestCode,int resultCode,@Nullable Intent data){
        super.onActivityResult(requestCode,resultCode,data);

        if(requestCode ==REQUEST_IMAGE_CAPTURE){

            try {

               Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(),data.getData());
                Bitmap mutableBmp=bmp.copy(Bitmap.Config.ARGB_8888,true);

                Canvas canvas=new Canvas(mutableBmp);
                image=FirebaseVisionImage.fromFilePath(getApplicationContext(),data.getData());

                FirebaseVisionFaceDetectorOptions options=new FirebaseVisionFaceDetectorOptions.Builder()
                        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
                        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_CONTOURS)
                        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();

                detector=FirebaseVision.getInstance().getVisionFaceDetector(options);

                Task<List<FirebaseVisionFace>> result=
                        detector.detectInImage(image)
                                .addOnSuccessListener(
                                        new OnSuccessListener<List<FirebaseVisionFace>>() {

                                            @Override
                                            public void onSuccess(List<FirebaseVisionFace> Faces) {
                                                for(FirebaseVisionFace face:Faces){
                                                    Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_SHORT).show();
                                                    Rect bounds=face.getBoundingBox();

                                                    Paint p=new Paint();
                                                    p.setColor(Color.YELLOW);
                                                    p.setStyle(Paint.Style.STROKE);

                                                    canvas.drawRect(bounds,p);

                                                    float rotY=face.getHeadEulerAngleY();
                                                    float rotZ=face.getHeadEulerAngleZ();
                                                }
                                            }
                                        }
                                )
                                .addOnFailureListener(
                                        new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this,"Oops something went wrong",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
   }

}




