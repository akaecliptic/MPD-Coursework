package org.me.gcu.equake.Architecture;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import androidx.lifecycle.ViewModel;

import org.me.gcu.equake.Adapter.EQUAKEParser;
import org.me.gcu.equake.Interface.RequestReady;
import org.me.gcu.equake.Model.EQUAKE;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Developed by: Michael A. F.
 */
public class EQUAKEViewModel extends ViewModel {

    private static final String URL = "http://quakes.bgs.ac.uk/feeds/MhSeismology.xml";

    private List<EQUAKE> working;
    private final Handler handler;
    private final HandlerThread handlerThread;

    private final OkHttpClient client;
    private final Request request;

    public EQUAKEViewModel() {
        super();
        request = new Request.Builder().url(URL).build();
        client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(5, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .build();

        handlerThread = new HandlerThread("EquakeHttpRequest");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        handler = new Handler(looper);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        handlerThread.quitSafely();
    }

    public List<EQUAKE> getWorking() {
        if (working == null){
            try {
                loadData();
                handlerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return working;
    }

    public void loadData(RequestReady requestReady) {
        handler.post(() -> {
            try (Response response = client.newCall(request).execute()) {
                EQUAKEParser parser = new EQUAKEParser();
                requestReady.onComplete(parser.parse(response.body().byteStream()));
            } catch (IOException | XmlPullParserException e) {
                System.out.println("I Crashed");
                e.printStackTrace();
            }
        });
    }

    public void loadData() {
        handler.post(() -> {
            try (Response response = client.newCall(request).execute()) {
                EQUAKEParser parser = new EQUAKEParser();
                working = parser.parse(response.body().byteStream());
            } catch (IOException | XmlPullParserException e) {
                System.out.println("I Crashed");
                e.printStackTrace();
            }
        });
    }
}
