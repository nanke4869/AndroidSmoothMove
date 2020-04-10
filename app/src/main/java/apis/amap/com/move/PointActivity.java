package apis.amap.com.move;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PointActivity extends AppCompatActivity{
    private Button getPoint;
    private Button trans;
    String key = "Trace";
    public static double[] coords = new double[50];
    public static int k=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);
        getPoint= (Button) findViewById(R.id.getPoint);
        trans=(Button) findViewById(R.id.trans);
        getPoint.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                SendByHttpClient(key);
            }
        });

        trans.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent=new Intent(PointActivity.this, MainActivity.class);
                PointActivity.this.startActivity(intent);
            }
        });

    }

    public void SendByHttpClient(final String key){
        new Thread(new Runnable(){
            @Override
            public void run(){
                try {
                    HttpClient httpclient=new DefaultHttpClient();
                    HttpPost httpPost=new HttpPost("http://192.168.31.223:8080/HttpTrace/Search");
                    List<NameValuePair> params=new ArrayList<NameValuePair>();//用来存放post请求的参数，前面一个键，后面一个值
                    params.add(new BasicNameValuePair("search",key));
                    //UrlEncodedFormEntity这个类是用来把输入数据编码成合适的内容
                    //两个键值对，被UrlEncodedFormEntity实例编码后变为如下内容：param1=value1&param2=value2
                    final UrlEncodedFormEntity entity=new UrlEncodedFormEntity(params,"utf-8");
                    httpPost.setEntity(entity);//带上参数
                    HttpResponse httpResponse= httpclient.execute(httpPost);//响应结果
                    if(httpResponse.getStatusLine().getStatusCode()==200)
                    {
                        HttpEntity entity1=httpResponse.getEntity();
                        String response= EntityUtils.toString(entity1, "utf-8");
                        Message message=new Message();
                        message.what=0;

                        message.obj=response;
                        handler.sendMessage(message);
                    }
                    else{
                        Toast.makeText(PointActivity.this, "连接超时", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    String response = (String) msg.obj;
                    try {
                        getPoint(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(PointActivity.this, "异常111111", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }

        }
    };


    public static void getPoint(String jsonString) throws JSONException {

        try{
            JSONObject jsonObject = new JSONObject(jsonString);
            //JSONObject mapsObject = jsonObject.getJSONObject("maps");
            JSONArray mapsArray = jsonObject.getJSONArray("maps");
            for(int i=0; i<mapsArray.length();i++){
                JSONObject  temp = mapsArray.getJSONObject(i);
                // 查看Map中的键值对的key值
//                Iterator<String> iterator = temp.keys();
//                while(iterator.hasNext()){
//                    String json_key = iterator.next();
//                    String object =temp.get(json_key).toString();
//                    coords[k++]=Double.parseDouble(object);
//                }
                String lati = temp.getString("latitude");
                coords[k++]=Double.valueOf(lati).doubleValue();
                String longi = temp.getString("longitude");
                coords[k++]=Double.valueOf(longi).doubleValue();
            }
        }catch (Exception e) {
            // TODO: handle exception
           //Toast.makeText(PointActivity.this, "异常22222", Toast.LENGTH_SHORT).show();
        }
    }

    public static double[] GetPoint(){
        return coords;
    }

    public static int GetPointLen(){
        return k;
    }

    public static void main(String[] args){
        for(double i : coords)
            System.out.println(i);
    }
}
