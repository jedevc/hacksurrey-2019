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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        KSIServiceCredentials credentials = new KSIServiceCredentials("ot.9HL3ao", "F8ByaJsVcq0c");

        HttpClientSettings clientSettings = new HttpClientSettings(
                "http://tryout.guardtime.net:8080/gt-signingservice",
                "https://tryout-extender.guardtime.net:8081/gt-extendingservice",
                "http://verify.guardtime.com/ksi-publications.bin",
                credentials);

        SimpleHttpClient simpleHttpClient = new SimpleHttpClient(clientSettings);
        try {
            KSI ksi = new KSIBuilder()
                    .setKsiProtocolSignerClient(simpleHttpClient)
                    .setKsiProtocolExtenderClient(simpleHttpClient)
                    .setKsiProtocolPublicationsFileClient(simpleHttpClient)
                    .setPublicationsFileTrustedCertSelector(new X509CertificateSubjectRdnSelector("E=publications@guardtime.com"))
                    .build();
        } catch(Exception e) {
            
        }

        DataHasher hasher = new DataHasher();

        // Read the input file to be signed
        FileInputStream inputStream;
        byte[] fileContent;
        try {
            inputStream = new FileInputStream("signme.txt"); // CHANGE TO ANDROID INPUT
            fileContent = Files.readAllBytes(Paths.get("signme.txt")); // CHANGE TO ANDROID
            System.out.printf("\n\nInput file: %s\n", new String(fileContent));
            hasher.addData(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
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

        if (requestCode == GET_FROM_STORAGE && resultCode == Activity.RESULT_OK) {
            Uri fileUri = data.getData();
            files.add(fileUri);
        }
    }
}
