package com.fernandot.dnsadguard;

import android.content.Context;
import android.content.pm.PackageManager;
import android.provider.Settings;

public class DnsManager {
    public static final String DNS_ADDRESS = "dns.adguard.com";
    private static final String DNS_MODE_HOSTNAME = "hostname";
    private static final String DNS_MODE_AUTO = "opportunistic";
    private static final String KEY_DNS_MODE = "private_dns_mode";
    private static final String KEY_DNS_SPECIFIER = "private_dns_specifier";
    private final Context context;

    public DnsManager(Context context) {
        this.context = context;
    }

    public boolean isAdGuardActive() {
        String mode = Settings.Global.getString(context.getContentResolver(), KEY_DNS_MODE);
        String address = Settings.Global.getString(context.getContentResolver(), KEY_DNS_SPECIFIER);
        return DNS_MODE_HOSTNAME.equals(mode) && DNS_ADDRESS.equals(address);
    }

    public void enableDnsAdguard() {
        if (!checkPermission()) {
            throw new SecurityException("WRITE_SECURE_SETTINGS permission not granted");
        }
        boolean modeSet = Settings.Global.putString(context.getContentResolver(), KEY_DNS_MODE, DNS_MODE_HOSTNAME);
        boolean specifierSet = Settings.Global.putString(context.getContentResolver(), KEY_DNS_SPECIFIER, DNS_ADDRESS);
        if (!modeSet || !specifierSet) {
            throw new IllegalStateException("Failed to write private DNS settings");
        }
    }

    public void disableDnsAdguard() {
        if (!checkPermission()) {
            throw new SecurityException("WRITE_SECURE_SETTINGS permission not granted");
        }
        boolean modeSet = Settings.Global.putString(context.getContentResolver(), KEY_DNS_MODE, DNS_MODE_AUTO);
        boolean specifierCleared = Settings.Global.putString(context.getContentResolver(), KEY_DNS_SPECIFIER, "");
        if (!modeSet || !specifierCleared) {
            throw new IllegalStateException("Failed to reset private DNS settings");
        }
    }

    public boolean checkPermission() {
        return context.checkSelfPermission(android.Manifest.permission.WRITE_SECURE_SETTINGS)
                == PackageManager.PERMISSION_GRANTED;
    }
}