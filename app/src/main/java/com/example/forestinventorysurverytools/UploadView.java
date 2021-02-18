package com.example.forestinventorysurverytools;

import android.content.ClipData;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.forestinventorysurverytools.sever.FileUtil;
import com.example.forestinventorysurverytools.sever.NetConnect;
import com.example.forestinventorysurverytools.sever.NetService;
import com.example.forestinventorysurverytools.sever.TreeDTO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import lombok.SneakyThrows;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UploadView extends AppCompatActivity {

    Button mSel_img_btn;
    Button mUpload_img_btn;
    Button mSel_json_btn;
    Button mUpload_json_btn;
    Button mReturn;

    TextView mView_img;
    TextView mView_json;

    String imgText;
    String jsonText;


    //base Server url
    public String baseurl;
    //WebServer Upload
    NetConnect mNetConnect = new NetConnect();
    NetService mNetService;


    String mbPart=null;
    ArrayList<MultipartBody.Part> blist = new ArrayList<MultipartBody.Part>();


    public String UserID;

    public String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FIST";

    Gson gson = new Gson();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Intent upViewIntent = getIntent();
        UserID = upViewIntent.getStringExtra("UserID");
        baseurl = upViewIntent.getStringExtra("baseurl");

        mReturn = (Button)findViewById(R.id.btn_return);
        mSel_img_btn = (Button)findViewById(R.id.Btn_sel_img);
        mUpload_img_btn=(Button)findViewById(R.id.Btn_upload_img);
        mSel_json_btn = (Button)findViewById(R.id.Btn_sel_json);
        mUpload_json_btn = (Button)findViewById(R.id.Btn_upload_json);
        mView_img = (TextView)findViewById(R.id.tv_selected_img);
        mView_json = (TextView)findViewById(R.id.tv_selected_json);
        mSel_img_btn.setOnClickListener(sel_img_Listener);
        mSel_json_btn.setOnClickListener(sel_json_Listener);
        mUpload_img_btn.setOnClickListener(upload_img_Listener);
        mUpload_json_btn.setOnClickListener(upload_json_Listener);
        mReturn.setOnClickListener(return_Listener);

        imgText = mView_img.getText().toString();
        jsonText = mView_json.getText().toString();
    }

    //메인으로돌아가기
    View.OnClickListener return_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };


    // 이미지 선택
    View.OnClickListener sel_img_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            Uri uri = Uri.parse(dirPath);
            intent.setDataAndType(uri,"image/*");
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(intent,1);
            startActivityForResult(
                    Intent.createChooser(intent,"선택")
                    ,1);
        }
    };

    //JSON파일 선택
    View.OnClickListener sel_json_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            Uri uri = Uri.parse(dirPath);
            intent.setDataAndType(uri,"application/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(intent,1);
            startActivityForResult(
                    Intent.createChooser(intent,"선택")
                    ,2);
        }
    };


    // jpg나 json 선택 완료 직후의 작업
    @SneakyThrows
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode==1){
                String fName= "";
                blist.clear();
                ClipData clipData = data.getClipData();
                //이미지 URI 를 이용하여 이미지뷰에 순서대로 세팅한다.
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        Uri urione = clipData.getItemAt(i).getUri();
                        String ss= FileUtil.getPath(urione,getApplicationContext());
                        Log.d("tag", "@@"+ss);
                        File file = new File(FileUtil.getPath(urione,getApplicationContext()));
                        if(i==0)
                            fName = file.getName();

                        // 멀티파트바디 파트타입으로 변환
                        RequestBody requestFile =RequestBody.create(MediaType.parse(getContentResolver().getType(urione)),file );
                        MultipartBody.Part body = MultipartBody.Part.createFormData("files", file.getName(), requestFile);
                        blist.add(body);


                    }
                    if(blist.size()>0)
                        mView_img.setText(imgText+"\n선택된 파일 : "+fName+"\n 외 "+blist.size()+"개가 선택");
                    else
                        mView_img.setText(imgText+"\n선택된 파일이 없습니다.");
                }
            }else{
                mbPart=null;
                Uri uri = data.getData();
                File file = new File(FileUtil.getPath(uri,getApplicationContext()));


                JSONParser parser = new JSONParser();
                Object obj = parser.parse(new FileReader(file));
                JSONArray jsonArray = (JSONArray) obj;
                mbPart = jsonArray.toJSONString();

                if(file!=null && mbPart!=null)
                    mView_json.setText(jsonText+"\n 선택된 파일 : "+file.getName());
                else
                    mView_json.setText(jsonText+"\n선택된 파일이 없습니다.");
            }
        }
    }



    //이미지 업로드
    View.OnClickListener upload_img_Listener = new View.OnClickListener() {
        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onClick(View v) {
            mView_img.setText(imgText+"\n 업로드 중...");
            try {
                Log.d("tag", "업로드 수행-----------------");
                mNetConnect.buildNetworkService(baseurl);
                mNetService = mNetConnect.getNetService();
                Call<String> imgFile = mNetService.imgFile(blist);
                Log.d("tag","##"+blist.get(0).body().getClass().getName()+" "
                        +blist.get(0).body().getClass().getCanonicalName()+
                        " "+blist.get(0).body().getClass().getSimpleName()
                +" "+blist.get(0).body().getClass().getTypeName());
                imgFile.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(getApplicationContext(), "업로드 성공", Toast.LENGTH_SHORT).show();

                        String urJson = response.body();
                        mView_img.setText(imgText+"\n 업로드 완료");
                        Log.d("tag", "비동기, 업로드 성공 " + urJson);
                    }

                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "업로드 실패1", Toast.LENGTH_SHORT).show();
                        Log.d("tag", "실패!, 상태 코드: " + t.getMessage());
                        mView_img.setText(imgText+"\n 업로드 실패");
                        t.getStackTrace().toString();
                    }
                });

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "업로드 실패2", Toast.LENGTH_SHORT).show();
                Log.d("tag", "업로드 실패 에러:" + e.getMessage());
            }
        }
    };

    // JSON업로드
    View.OnClickListener upload_json_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mView_json.setText(jsonText+"\n 업로드 중...");
            try {
                Log.d("tag", "업로드 수행-----------------");
                mNetConnect.buildNetworkService(baseurl);
                mNetService = mNetConnect.getNetService();
                Call<String> jsonFile = mNetService.jsonFile(mbPart);
                jsonFile.enqueue(new Callback<String>() {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        Toast.makeText(getApplicationContext(), "업로드 성공", Toast.LENGTH_SHORT).show();
                        mView_json.setText(jsonText+"\n 업로드 완료");
                        String urJson = response.body();
                        Log.d("tag", "비동기, 업로드 성공 " + urJson);
                    }
                    @Override
                    public void onFailure(Call<String> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "업로드 실패1", Toast.LENGTH_SHORT).show();
                        Log.d("tag", "실패!, 상태 코드: " + t.getMessage()+" "+t.toString());
                        t.getStackTrace().toString();
                        mView_json.setText(jsonText+"\n 업로드 실패");
                    }
                });

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "업로드 실패2", Toast.LENGTH_SHORT).show();
                Log.d("tag", "업로드 실패 에러:" + e.getMessage());
            }
        }
    };




}
