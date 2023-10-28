package com.dk.cellsense;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrength;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private TelephonyManager telephonyManager;
    private TextView networkInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkInfoTextView = findViewById(R.id.networkInfoTextView);
        Button buttonShow = findViewById(R.id.yourButtonId);

        buttonShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                } else {
                    collectNetworkInfo();
                }
            }
        });
    }

    private void collectNetworkInfo() {
        telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);

        if (telephonyManager == null) {
            networkInfoTextView.setText("TelephonyManager not available on this device.");
            return;
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            networkInfoTextView.setText("Permission not granted for ACCESS_FINE_LOCATION.");
            return;
        }

        List<CellInfo> cellInfoList = telephonyManager.getAllCellInfo();

        if (cellInfoList.isEmpty()) {
            networkInfoTextView.setText("No cell information available.");
            return;
        }

        // Collect Nearest Base Station Information
        CellInfo cellInfo = cellInfoList.get(0);
        networkInfoTextView.setText("Nearest Base Station:\n");
        if (cellInfo instanceof CellInfoGsm) {
            CellInfoGsm cellInfoGsm = (CellInfoGsm) cellInfo;
            networkInfoTextView.append("GSM Cell ID: " + cellInfoGsm.getCellIdentity().getCid() + "\n");
        } else if (cellInfo instanceof CellInfoLte) {
            CellInfoLte cellInfoLte = (CellInfoLte) cellInfo;
            networkInfoTextView.append("LTE Cell ID: " + cellInfoLte.getCellIdentity().getCi() + "\n");
        }

        // Collect Signal Strength Information
        CellSignalStrength signalStrength = null;
        if (cellInfo instanceof CellInfoGsm) {
            signalStrength = ((CellInfoGsm) cellInfo).getCellSignalStrength();
            // Get GSM signal strength information
        } else if (cellInfo instanceof CellInfoLte) {
            signalStrength = ((CellInfoLte) cellInfo).getCellSignalStrength();
            // Get LTE signal strength information
        }

        if (signalStrength != null) {
            int strengthDbm = signalStrength.getDbm();
            networkInfoTextView.append("Signal Strength (dBm): " + strengthDbm + "\n");
        } else {
            networkInfoTextView.append("Signal Strength information not available.\n");
        }

        // Collect SIM Module Details
        networkInfoTextView.append("SIM Module Details:\n");
        networkInfoTextView.append("Operator Name: " + telephonyManager.getNetworkOperatorName() + "\n");
        networkInfoTextView.append("SIM Operator: " + telephonyManager.getSimOperatorName() + "\n");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                collectNetworkInfo();
            } else {
                networkInfoTextView.setText("Permission denied for ACCESS_FINE_LOCATION.");
            }
        }
    }
}
