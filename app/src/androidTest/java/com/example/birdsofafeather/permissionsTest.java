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
    public GrantPermissionRule backgroundPermission = GrantPermissionRule.grant(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
    @Rule
    public GrantPermissionRule coarsePermission = GrantPermissionRule.grant(Manifest.permission.ACCESS_COARSE_LOCATION);

    @Test
    public void backgroundTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getContext();
        assertEquals(ContextCompat.checkSelfPermission(appContext,"android.permission.ACCESS_BACKGROUND_LOCATION"), PackageManager.PERMISSION_GRANTED);
    }

    @Test
    public void coarseTest(){
        Context appContext = InstrumentationRegistry.getInstrumentation().getContext();
        assertEquals(ContextCompat.checkSelfPermission(appContext,"android.permission.ACCESS_COARSE_LOCATION"), PackageManager.PERMISSION_GRANTED);
    }

}
