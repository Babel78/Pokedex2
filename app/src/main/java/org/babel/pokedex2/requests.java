package org.babel.pokedex2;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Axel on 3/06/2018.
 */




public class requests extends AsyncTask<Void, Void, Void> {
    private TextToSpeech toSpeech;
    private int result;
    private JSONObject o;
    private String TAG = "POKEDEX";
    @SuppressLint("StaticFieldLeak")
    private
    Context ctx;
    requests(Context c){
        this.ctx = c;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
       toSpeech= new TextToSpeech(this.ctx, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                Log.i(TAG, "Status: " + status);
                if(status== TextToSpeech.SUCCESS){
                    result= toSpeech.setLanguage(Locale.getDefault());
                }
                else{
                    Toast.makeText(ctx,"GG",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    @Override
    protected Void doInBackground(Void... voids) {
        RequestQueue queue = Volley.newRequestQueue(ctx);
        String url = "http://192.168.1.10:8080/predecir";
        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    o = new JSONObject(response);
                    //Log.d(TAG,"Nombre: "+o.getString("name")+", Apellidos: "+ o.getString("lastname"));
                    toSpeech.speak(o.getString("pokemon")+" "+o.getString("descripcion"),TextToSpeech.QUEUE_FLUSH,null);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "onErrorResponse: "+error );
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                Bitmap imageBitmap = MainActivity.b;
                String image = "";
                if(imageBitmap!=null){
                    image = getStringImage(imageBitmap);
                }
                params.put("image",image);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String,String> params  = new HashMap<>();
                params.put("Content-Type","application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Log.i(TAG, "Termino");
    }

    private String getStringImage(Bitmap bip){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bip.compress(Bitmap.CompressFormat.JPEG,60,baos);
        byte [] b = baos.toByteArray();
        String temp = Base64.encodeToString(b,Base64.DEFAULT);
        return temp;
    }

}
