package com.hck.imagemap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.yoojia.imagemap.ImageMap1;
import net.yoojia.imagemap.core.LineShape;
import net.yoojia.imagemap.core.PrruNumShape;
import net.yoojia.imagemap.core.PrruShape;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.hck.imagemap.config.GlobalConfig;
import com.hck.imagemap.entity.Floor;
import com.hck.imagemap.utils.Loction;
import com.hck.imagemap.utils.UpLoad;

public class PrruInfoAcitivity extends Activity
{

    private ImageMap1 map;
    private RelativeLayout layout;
    private RelativeLayout layoutTemp;
    private Bundle bn;
    private Bitmap bitmap;
    private boolean isBack = false;
    private TextView tv_prruNum;
    private RequestQueue mRequestQueue;
    private UpLoad load;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_prru_info_acitivity);
        isBack = false;
        initView();
        getPrruSubscribe();
        getPrruInfo();
        requestLocationTask();
    }

    private void initView()
    {
        load = new UpLoad(this);
        layout = (RelativeLayout) findViewById(R.id.rl_prru_map);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutTemp = (RelativeLayout) LayoutInflater.from(this).inflate(
                R.layout.image_map_item, null);
        map = (ImageMap1) layoutTemp.findViewById(R.id.imagemap);
        layoutTemp.setLayoutParams(lp);
        layout.addView(layoutTemp);
        tv_prruNum = (TextView) findViewById(R.id.tv_prruNum);
        mRequestQueue = Volley.newRequestQueue(this);
        Intent in = this.getIntent();
        bn = in.getExtras();
        try
        {
            bitmap = BitmapFactory.decodeFile(Environment
                    .getExternalStorageDirectory() + bn.getString("path"));
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        if(bitmap == null){
            finish();
            return;
        }
        map.setMapBitmap(bitmap);
    }

    List<double[]> list = new ArrayList<double[]>();
    private ArrayList<String> neCodeArray = new ArrayList<String>();
    private ArrayList<PointF> locationArray = new ArrayList<PointF>();

    private void getPrruInfo()
    {
        Map<String, String> params = new HashMap<String, String>();
        params.put("ip", load.getLocaIpOrMac());
        params.put("isPush", "2");
        JsonObjectRequest newMissRequest = new JsonObjectRequest(
                Request.Method.POST, "http://" + GlobalConfig.server_ip
                        + "/sva/api/getPrruInfo?floorNo="
                        + bn.getInt("floorNo"), new JSONObject(params),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject jsonobj)
                    {
                        Log.d("getPrruInfo",
                                "getPrruInfo:" + jsonobj.toString());
                        System.out.println("getPrruInfo:" + jsonobj.toString());
                        JSONArray array = null;
                        try
                        {
                            array = jsonobj.getJSONArray("data");
                        } catch (Exception e)
                        {
                            Log.e("error", e + "");
                        }
                        locationArray.clear();
                        neCodeArray.clear();
                        tv_prruNum.setText(getString(R.string.prruNum)
                                + array.length());
                        for (int i = 0; i < array.length(); i++)
                        {
                            try
                            {
                                JSONObject o = array.getJSONObject(i);
                                double xSpot = o.getDouble("x");
                                double ySpot = o.getDouble("y");
                                String neCode = o.getString("neCode");
                                neCodeArray.add(neCode);
                                Loction loction = new Loction();
                                double xx = loction
                                        .location(xSpot, ySpot, (Floor) bn
                                                .getSerializable("currFloor"))[0];
                                double yy = loction
                                        .location(xSpot, ySpot, (Floor) bn
                                                .getSerializable("currFloor"))[1];
                                locationArray.add(new PointF((float) xx,
                                        (float) yy));
                                PrruShape prruShape = new PrruShape("prru" + i,
                                        Color.BLACK, null,
                                        PrruInfoAcitivity.this);
                                prruShape.setValues(String.format(
                                        "%.5f:%.5f:30", xx, yy));
                                map.addShape(prruShape, false);

                            } catch (Exception e)
                            {
                                Log.e("error", e + "");
                            }
                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Log.e("getPrruInfo", "getPrruInfo:" + error);
                        Toast.makeText(PrruInfoAcitivity.this,
                                getString(R.string.prruinfo_norespond),
                                Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders()
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        mRequestQueue.add(newMissRequest);
    }

    private int wrongTag = 0;

    private void getPrruSignal()
    {
        Map<String, String> params = new HashMap<String, String>();
        JsonObjectRequest newMissRequest = new JsonObjectRequest(
                Request.Method.POST, "http://" + GlobalConfig.server_ip
                        + "/sva/api/getPrruSignal", new JSONObject(params),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject jsonobj)
                    {
                        Log.d("getPrruSignal",
                                "getPrruSignal:" + jsonobj.toString());
                        System.out.println("getPrruSignal:"
                                + jsonobj.toString());
                        JSONArray array = null;
                        try
                        {
                            array = jsonobj.getJSONArray("data");
                        } catch (Exception e)
                        {
                            Log.e("error", e + "");
                        }
                        wrongTag = 0;
                        for (int i = 0; i < array.length(); i++)
                        {
                            try
                            {
                                JSONObject o = array.getJSONObject(i);
                                double rsrp = o.getDouble("rsrp");
                                String gpp = o.getString("gpp");
                                for (int j = 0; j < neCodeArray.size(); j++)
                                {
                                    if (neCodeArray.get(j).equals(gpp))
                                    {
                                        LineShape lineShape = new LineShape(
                                                "lines" + i, Color.RED);
                                        lineShape.setValues(String.format(
                                                "%.5f:%.5f:%.5f:%.5f",
                                                locationArray.get(j).x + 110,
                                                locationArray.get(j).y + 100,
                                                locationArray.get(j).x + 110,
                                                locationArray.get(j).y + 100
                                                        - rsrp));
                                        map.addShape(lineShape, false);
                                        PrruNumShape cs = new PrruNumShape("P"
                                                + i, Color.BLUE, (rsrp) + "");
                                        cs.setValues(String.format(
                                                "%.5f:%.5f:20",
                                                locationArray.get(j).x + 110,
                                                locationArray.get(j).y + 20));
                                        map.addShape(cs, false);
                                        break;
                                    }
                                }
                            } catch (Exception e)
                            {
                                Log.e("error", e + "");
                            }

                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        wrongTag++;
                        Log.e("getPrruInfo", "getPrruInfo:" + error);
                        if (wrongTag == 1)
                        {
                            Toast.makeText(PrruInfoAcitivity.this,
                                    getString(R.string.prruinfo_norespond),
                                    Toast.LENGTH_SHORT).show();
                        } else if (wrongTag == 10)
                        {
                            Toast.makeText(PrruInfoAcitivity.this,
                                    getString(R.string.prruinfo_norespond),
                                    Toast.LENGTH_SHORT).show();
                        } else if (wrongTag == 20)
                        {
                            Toast.makeText(PrruInfoAcitivity.this,
                                    getString(R.string.prruinfo_norespond),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders()
            {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json; charset=UTF-8");
                return headers;
            }
        };
        mRequestQueue.add(newMissRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.prru_info_acitivity, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        bitmap.recycle();
        finish();
    }

    private void getPrruSubscribe()
    {
        Map<String, String> params = new HashMap<String, String>();
        JsonObjectRequest newMissRequest = new JsonObjectRequest(
                Request.Method.POST,
                "http://" + GlobalConfig.server_ip
                        + "/sva/api/subscribePrru?storeId="
                        + bn.getInt("placeId") + "&ip=" + load.getLocaIpOrMac(),
                new JSONObject(params), new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject jsonobj)
                    {
                        System.out.println("subscribePrru:"
                                + jsonobj.toString());
                        try
                        {
                        } catch (Exception e)
                        {
                            Log.e("error", e + "");
                            return;
                        }
                    }
                }, new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        String wrong = getString(R.string.prru_fail).toString();
                        Toast.makeText(PrruInfoAcitivity.this, wrong,
                                Toast.LENGTH_LONG).show();
                    }
                });
        mRequestQueue.add(newMissRequest);
    }

    private void requestLocationTask()
    { // 延迟1s运行
        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                if (!isBack)
                {
                    getPrruSignal();
                    requestLocationTask();
                }
            }
        }, 2000);
    }

    @Override
    protected void onStop()
    {
        isBack = true;
        super.onStop();
    }

    public void setting_back(View v)
    {
        bitmap.recycle();
        finish();
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mRequestQueue.cancelAll(this);
        isBack = true;
        if(bitmap != null)
        {
            bitmap.recycle(); 
        }
       // bitmap.recycle();
    }

}
