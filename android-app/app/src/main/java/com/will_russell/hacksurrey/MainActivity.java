package com.will_russell.hacksurrey;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    static final int GET_FROM_STORAGE = 1;
    static final String SERVER_URL = "https://38d2ee85.ngrok.io";
    ArrayList<Uri> files = new ArrayList<>();
    String postResponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void makeRequest(byte[] data) {
        HttpURLConnection client = null;
        try {
            URL url = new URL(SERVER_URL + "/create");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setDoOutput(true);
            client.setUseCaches(false);
            OutputStream outputStream  = client.getOutputStream();
            String file_start = "file=";
            byte[] bytes;
            bytes = file_start.getBytes();
            outputStream.write(bytes);
            outputStream.write(data);
            outputStream.flush();
            outputStream.close();

            int responseCode = client.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                postResponse = "";
                BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
                while ((line = br.readLine()) != null) {
                    postResponse += line;
                }
                System.out.println(responseCode);
                System.out.println(postResponse);
                TextView tv = (TextView) findViewById(R.id.output_box);
                tv.setText(postResponse);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.disconnect();
            }
        }
    }

    public void uploadButtonOnClick(View v) {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            System.out.println("Permission denied");
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GET_FROM_STORAGE);
            }
        } else {
            System.out.println("Success");
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            startActivityForResult(intent, GET_FROM_STORAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GET_FROM_STORAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri fileUri = data.getData();
            files.add(fileUri);

            // Read the input file to be signed
            int SDK_INT = android.os.Build.VERSION.SDK_INT;
            if (SDK_INT > 8) {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                        .permitAll().build();
                StrictMode.setThreadPolicy(policy);
                byte[] fileContent;
                try {

                    InputStream stream = getContentResolver().openInputStream(fileUri);
                    fileContent = getByte(stream);
                    System.out.printf("\n\nInput file: %s\n", new String(fileContent));
                    makeRequest(fileContent);
                    //hasher.addData(stream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[] getByte(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, length);
        }
        return byteBuffer.toByteArray();
    }
}