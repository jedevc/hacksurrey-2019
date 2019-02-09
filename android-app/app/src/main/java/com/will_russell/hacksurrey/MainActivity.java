package com.will_russell.hacksurrey;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.guardtime.ksi.KSI;
import com.guardtime.ksi.KSIBuilder;
import com.guardtime.ksi.exceptions.KSIException;
import com.guardtime.ksi.hashing.DataHash;
import com.guardtime.ksi.hashing.DataHasher;
import com.guardtime.ksi.service.client.KSIServiceCredentials;
import com.guardtime.ksi.service.client.http.HttpClientSettings;
import com.guardtime.ksi.service.http.simple.SimpleHttpClient;
import com.guardtime.ksi.trust.X509CertificateSubjectRdnSelector;
import com.guardtime.ksi.unisignature.KSISignature;
import com.guardtime.ksi.unisignature.verifier.policies.KeyBasedVerificationPolicy;
import com.guardtime.ksi.unisignature.verifier.policies.Policy;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final int GET_FROM_STORAGE = 1;
    ArrayList<Uri> files = new ArrayList<>();

    KSIServiceCredentials credentials;
    HttpClientSettings clientSettings;
    SimpleHttpClient simpleHttpClient;
    DataHasher hasher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        credentials = new KSIServiceCredentials("ot.9HL3ao", "F8ByaJsVcq0c");

        clientSettings = new HttpClientSettings(
                "http://tryout.guardtime.net:8080/gt-signingservice",
                "https://tryout-extender.guardtime.net:8081/gt-extendingservice",
                "http://verify.guardtime.com/ksi-publications.bin",
                credentials);

        simpleHttpClient = new SimpleHttpClient(clientSettings);
        try {
            KSI ksi = new KSIBuilder()
                    .setKsiProtocolSignerClient(simpleHttpClient)
                    .setKsiProtocolExtenderClient(simpleHttpClient)
                    .setKsiProtocolPublicationsFileClient(simpleHttpClient)
                    .setPublicationsFileTrustedCertSelector(new X509CertificateSubjectRdnSelector("E=publications@guardtime.com"))
                    .build();
        } catch(Exception e) {

        }

        hasher = new DataHasher();
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

        if (requestCode == GET_FROM_STORAGE && resultCode == Activity.RESULT_OK) {
            Uri fileUri = data.getData();
            files.add(fileUri);

            // Read the input file to be signed
            byte[] fileContent;
            try {

                InputStream stream = getContentResolver().openInputStream(fileUri);
                fileContent = getBytes(stream);
                System.out.printf("\n\nInput file: %s\n", new String(fileContent));
                hasher.addData(stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length = 0;
        while ((length = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, length);
        }
        return byteBuffer.toByteArray();
    }
}
