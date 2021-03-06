package com.example.birdsofafeather;

import static org.junit.Assert.assertEquals;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.GrantPermissionRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class permissionsTest {
    @Rule
    public GrantPermissionRule scanPermission = GrantPermissionRule.grant(Manifest.permission.BLUETOOTH_SCAN);
    @Rule
    public GrantPermissionRule adPermission = GrantPermissionRule.grant(Manifest.permission.BLUETOOTH_ADVERTISE);
    @Rule
    public GrantPermissionRule connectPermission = GrantPermissionRule.grant(Manifest.permission.BLUETOOTH_CONNECT);

    @Test
    public void scanTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getContext();
        assertEquals(ContextCompat.checkSelfPermission(appContext,"android.permission.BLUETOOTH_SCAN"), PackageManager.PERMISSION_GRANTED);
    }

    @Test
    public void adTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getContext();
        assertEquals(ContextCompat.checkSelfPermission(appContext,"android.permission.BLUETOOTH_ADVERTISE"), PackageManager.PERMISSION_GRANTED);
    }

    @Test
    public void connectTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getContext();
        assertEquals(ContextCompat.checkSelfPermission(appContext,"android.permission.BLUETOOTH_CONNECT"), PackageManager.PERMISSION_GRANTED);
    }

}