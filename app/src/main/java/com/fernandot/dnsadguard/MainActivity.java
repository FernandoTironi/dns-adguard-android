package com.fernandot.dnsadguard;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {

    private View orbButton;
    private TextView statusText;
    private TextView statusDescription;
    private TextView statusChip;
    private TextView serverValue;
    private TextView modeValue;
    private View haloOuter;
    private View haloInner;
    private View orb;
    private ImageView statusIcon;
    private MaterialCardView permissionCard;
    private DnsManager dnsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        orbButton = findViewById(R.id.orb_button);
        statusText = findViewById(R.id.status_text);
        statusDescription = findViewById(R.id.status_description);
        statusChip = findViewById(R.id.status_chip);
        serverValue = findViewById(R.id.server_value);
        modeValue = findViewById(R.id.mode_value);
        haloOuter = findViewById(R.id.halo_outer);
        haloInner = findViewById(R.id.halo_inner);
        orb = findViewById(R.id.orb);
        statusIcon = findViewById(R.id.status_icon);
        permissionCard = findViewById(R.id.permission_card);

        dnsManager = new DnsManager(this);
        updateUI();
    }

    private void updateUI() {
        boolean hasPermission = dnsManager.checkPermission();
        boolean active = hasPermission && dnsManager.isAdGuardActive();

        orbButton.setOnClickListener(null);
        orbButton.setClickable(hasPermission);

        if (!hasPermission) {
            applyState(
                    R.drawable.halo_warning,
                    R.drawable.orb_warning,
                    R.drawable.ic_alert,
                    R.drawable.chip_warning,
                    R.color.amber_600,
                    R.string.status_permission,
                    R.string.status_title_permission,
                    R.string.status_description_permission,
                    R.string.value_server_empty,
                    R.string.value_mode_blocked
            );
            permissionCard.setVisibility(View.VISIBLE);
            return;
        }

        permissionCard.setVisibility(View.GONE);
        orbButton.setOnClickListener(v -> {
            if (dnsManager.isAdGuardActive()) dnsManager.disableDnsAdguard();
            else dnsManager.enableDnsAdguard();
            updateUI();
        });

        if (active) {
            applyState(
                    R.drawable.halo_active,
                    R.drawable.orb_active,
                    R.drawable.ic_shield,
                    R.drawable.chip_active,
                    R.color.brand_600,
                    R.string.status_active,
                    R.string.status_title_active,
                    R.string.status_description_active,
                    0,
                    R.string.value_mode_private
            );
            serverValue.setText(DnsManager.DNS_ADDRESS);
        } else {
            applyState(
                    R.drawable.halo_inactive,
                    R.drawable.orb_inactive,
                    R.drawable.ic_shield_off,
                    R.drawable.chip_inactive,
                    R.color.slate_500,
                    R.string.status_inactive,
                    R.string.status_title_inactive,
                    R.string.status_description_inactive,
                    R.string.value_server_empty,
                    R.string.value_mode_auto
            );
        }
    }

    private void applyState(int haloRes, int orbRes, int iconRes, int chipRes, int chipColorRes,
                            int chipTextRes, int titleRes, int descriptionRes,
                            int serverRes, int modeRes) {
        haloOuter.setBackgroundResource(haloRes);
        haloInner.setBackgroundResource(haloRes);
        orb.setBackgroundResource(orbRes);
        statusIcon.setImageResource(iconRes);
        statusChip.setBackgroundResource(chipRes);
        statusChip.setTextColor(getColor(chipColorRes));
        statusChip.setText(chipTextRes);
        statusText.setText(titleRes);
        statusDescription.setText(descriptionRes);
        if (serverRes != 0) serverValue.setText(serverRes);
        modeValue.setText(modeRes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
