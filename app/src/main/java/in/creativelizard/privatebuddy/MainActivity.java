package in.creativelizard.privatebuddy;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imgView;
    private Pref _pref;
    private EditText etImagePath,etFileName;
    private Spinner spFiles;
    TextView tvFileCount;
    private ArrayList<String> fileList;
    private ArrayAdapter<String> arrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
    }

    private void initialize() {
        imgView = (ImageView)findViewById(R.id.imageView);
        etImagePath = (EditText)findViewById(R.id.etImagePath);
        etFileName = (EditText)findViewById(R.id.etName);
        tvFileCount = (TextView)findViewById(R.id.tvFileCount);
        spFiles = (Spinner)findViewById(R.id.spFiles);
        fileList = new ArrayList<>();
        tvFileCount.setText("No. Of Files saved : "+ String.valueOf(fileCount()));
        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fileList);
        spFiles.setAdapter(arrayAdapter);

        _pref = new Pref(this);
    }

    public void clkBrws(View view){
        Intent intent = new Intent();
// Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
// Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);



    }

    public void clkOpen(View v){
        loadImageFromStorage(etImagePath.getText().toString());
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("sid_img_private", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,etFileName.getText().toString() +".jpg");
        //_pref.setSession(ConstantClass.SAVED_PATH,directory.getPath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
    private void loadImageFromStorage(String path)
    {

        try {
            //etFileName.setText(_pref.getSession(ConstantClass.SAVED_PATH));
            File f=new File(path, (String) spFiles.getSelectedItem());
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            imgView.setImageBitmap(b);
        }

        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            Uri selectedImageUri = data.getData();
          //  String s = selectedImageUri.getPath();
            String s = getRealPathFromDocumentUri(this,selectedImageUri);
            Bitmap bitmap = BitmapFactory.decodeFile(s);
            saveToInternalStorage(bitmap);
        }
    }

    public static String getRealPathFromDocumentUri(Context context, Uri uri){
        String filePath = "";

        Pattern p = Pattern.compile("(\\d+)$");
        Matcher m = p.matcher(uri.toString());
        if (!m.find()) {
         //   Log.e(ImageConverter.class.getSimpleName(), "ID for requested image not found: " + uri.toString());
            return filePath;
        }
        String imgId = m.group();

        String[] column = { MediaStore.Images.Media.DATA };
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                column, sel, new String[]{ imgId }, null);

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();

        return filePath;
    }

    private int fileCount(){
        File file=new File(etImagePath.getText().toString());
        File[] list = file.listFiles();
        int count = 0;
        for (File f: list){
            String name = f.getName();
            fileList.add(name);
            if (name.endsWith(".jpg") || name.endsWith(".mp3") || name.endsWith(".some media extention"))
                count++;
        }
            return  count;
    }
}

