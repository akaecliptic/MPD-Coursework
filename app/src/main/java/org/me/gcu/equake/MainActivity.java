package org.me.gcu.equake;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import org.me.gcu.equake.Adapter.EQUAKEParser;
import org.me.gcu.equake.Architecture.EQUAKEViewModel;
import org.me.gcu.equake.Model.EQUAKE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Developed by: Michael A. F.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EQUAKEViewModel model = new ViewModelProvider(this).get(EQUAKEViewModel.class);
        model.loadData();
    }
}