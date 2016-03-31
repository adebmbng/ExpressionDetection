package com.example.adebambang.expressionv4;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by Ade Bambang Kurnia on 3/29/2016.
 */
public class HowItWorks extends AppCompatActivity {

    private final static int SELECT_PHOTO=12345;
    private static final int REQUEST_EXTERNAL_STORAGE =1;
    Bitmap myGambar;
    SparseArray<Face> faces;

    private static final int RC_HANDLE_GMS = 9001;
    TextView gantiteks;
    ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.howitworks);
        gantiteks = (TextView) findViewById(R.id.text);
        iv = (ImageView) findViewById(R.id.gambar);
    }

    public void button1Clicked(View v){
        gantiteks.setText("Press Button 2 to Check the face");
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, SELECT_PHOTO);

    }

    public  void  button2Clicked(View v){
        iv.setImageBitmap(null);
        iv.setEnabled(false);
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        FaceDetector detector = new FaceDetector.Builder(getApplicationContext())
                .setTrackingEnabled(false)
                .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();

        Frame frame = new Frame.Builder().setBitmap(myGambar).build();
        faces = detector.detect(frame);

        gantiteks.setText("Total face detected: " + faces.size() + " Press Button 3 (We only pick 1 face)");
//        gantiteks.setText(""+faces.valueAt(0).getPosition().x+", "+faces.valueAt(0).getPosition().y);

        customView overlay = (customView) findViewById(R.id._customView);
        overlay.setContent(myGambar, faces);
//        iv.setImageBitmap(myGambar);
        detector.release();
    }

    public void button3Clicked(View v){
        Face face = faces.valueAt(0);
        int x;
        if (((int)face.getPosition().x)>myGambar.getWidth()){
            x=myGambar.getWidth();
        } else x = (int)face.getPosition().x;

        Bitmap muka = Bitmap.createBitmap(myGambar, x,
                (int)face.getPosition().y +5,
                (int)face.getWidth(),
                (int)face.getHeight());
        iv.setImageBitmap(muka);

        gantiteks.setText("We will detect the lips at Button 4");
    }

    public void button4Clicked(View v){
        iv.setImageBitmap(null);
        iv.setEnabled(false);
        customView overlay1 = (customView) findViewById(R.id._customView);
        overlay1.setContent(null,null);

        Face face = faces.valueAt(0);
        int bibir0 = (int) face.getLandmarks().get(0).getPosition().y;
        int bibir1 = (int) face.getLandmarks().get(1).getPosition().y;
        int bibir2 = (int) face.getLandmarks().get(2).getPosition().y;
        int bibir3 = (int) face.getLandmarks().get(3).getPosition().y;
        int bibir4 = (int) face.getLandmarks().get(4).getPosition().y;
        int bibir5 = (int) face.getLandmarks().get(5).getPosition().x;
        int bibir6 = (int) face.getLandmarks().get(6).getPosition().x;
        int bibir7 = (int) face.getLandmarks().get(7).getPosition().y;
        gantiteks.setText("" + bibir0 + " " +
                        bibir1 + " " +
                        bibir2 + " " +
                        bibir3 + " " +
                        bibir4 + " " +
                        bibir5 + " " +
                        bibir6 + " " +
                        bibir7 + " "
        );
        int w,h;

        if((bibir5-40)>myGambar.getWidth()){
            w=myGambar.getWidth();
        } else w = bibir5-40;

        if((bibir7-15)>myGambar.getHeight()){
            h = myGambar.getHeight();
        } else h = bibir7-15;
        Bitmap bibir = Bitmap.createBitmap(myGambar, w,
                h,
                bibir6-bibir5+85,
                25);

        iv.setImageBitmap(scaleDown(bibir,300,true));
        gantiteks.setText("Press Button 5 to get the expression");

    }

    public void button5Clicked(View v){
        Face face = faces.valueAt(0);
        gantiteks.setText("Probabilty angel of lips: "+face.getIsSmilingProbability());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode==SELECT_PHOTO && resultCode==RESULT_OK && data!=null){
            Uri pickedImg = data.getData();
            String[] path = {MediaStore.Images.Media.DATA};
            Cursor cs = getContentResolver().query(pickedImg,path,null,null,null,null);

            cs.moveToFirst();
            String ImgPath = cs.getString(cs.getColumnIndex(path[0]));
            BitmapFactory.Options option = new BitmapFactory.Options();

            option.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bmp = BitmapFactory.decodeFile(ImgPath, option);
            Bitmap chc1;
            chc1 = scaleDown(bmp,1024,true);
            myGambar = chc1;
            iv.setImageBitmap(myGambar);

            cs.close();
        }

    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}
