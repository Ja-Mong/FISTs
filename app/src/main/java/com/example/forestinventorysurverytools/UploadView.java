package com.example.forestinventorysurverytools;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
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
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.forestinventorysurverytools.sever.NetConnect;
import com.example.forestinventorysurverytools.sever.NetService;
import com.example.forestinventorysurverytools.sever.treeDTO;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

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

    //base Server url
    public String baseurl;
    //WebServer Upload
    NetConnect mNetConnect = new NetConnect();
    NetService mNetService;


    public ArrayList<String> img_path_arr;
    public ArrayList<String> json_path_arr;
    public ArrayList<treeDTO> tArray;

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
    }

    View.OnClickListener return_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    View.OnClickListener sel_img_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            Uri uri = Uri.parse(dirPath);
            intent.setDataAndType(uri,"image/*");
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(intent,1);
            startActivityForResult(
                    Intent.createChooser(intent,"선택")
                    ,1);
        }
    };

    View.OnClickListener sel_json_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            Uri uri = Uri.parse(dirPath);
            //intent.setDataAndType(uri,"application/json");
            intent.setDataAndType(uri,"application/octet-stream");
//            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
//            setResult(RESULT_OK,intent);
            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(intent,1);
            startActivityForResult(
                    Intent.createChooser(intent,"선택")
                    ,1);
        }
    };

    View.OnClickListener upload_img_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            // textview에서 \n 파싱 후 path arr에 집어넣기
            String rawdata = mView_img.getText().toString();
            String [] split = rawdata.split("\n");
            for(String tmp : split){
                Log.d("tag",tmp);
            }
            // 각 img path에서 이미지 데이터 가져오기

            // 이미지 하나씩 전송

            // array 초기화
            img_path_arr.clear();
            tArray.clear();

            //textview 초기화
            mView_img.setText("");
        }
    };


    View.OnClickListener upload_json_Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mNetConnect.buildNetworkService(baseurl);
            mNetService = mNetConnect.getNetService();

            // textview에서 \n 파싱 후 path arr에 집어넣기
            String rawdata = mView_json.getText().toString();
            String [] split = rawdata.split("\n");
            for(String tmp : split){
                Log.d("tag",tmp);
                json_path_arr.add(tmp);
            }

            // 각 json path에서 데이터 불러오기
            //https://medium.com/@nayantala259/android-how-to-read-and-write-parse-data-from-json-file-226f821e957a

            for(String tmp: json_path_arr){
                JsonParser jsonParser = new JsonParser();
                try{
                    JsonElement element = jsonParser.parse(new FileReader(tmp));
                    JsonObject jsonObject = element.getAsJsonObject();
                    JsonArray jsonArray = (JsonArray)jsonObject.get("nameValuePairs");
                }catch (IOException e){
                    e.printStackTrace();
                }

            }



            // json array 내 요소 -> treeDTO araaylist로 변경

            // post_tree_dto 사용해서 하나씩 전송
            try{
                for (int i=0; i<tArray.size();i++){
                    Call<String> post_tree_dto = mNetService.post_tree_dto(tArray.get(i));
                    post_tree_dto.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            Toast.makeText(getApplicationContext(), "업로드 성공", Toast.LENGTH_SHORT).show();
                            Log.d("tag", "비동기, 업로드 성공");
                        }

                        @Override
                        public void onFailure(Call<String> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "업로드 실패1" , Toast.LENGTH_SHORT).show();
                            Log.d("tag", "실패!, 상태 코드: " + t.getMessage());
                            Log.d("tag",t.getStackTrace().toString());
                        }
                    });
                }
            }catch (Exception e){
                Toast.makeText(getApplicationContext(), "업로드 실패2", Toast.LENGTH_SHORT).show();
                Log.d("tag", "업로드 실패" + e.getMessage());
            }


            //tArray, path array 초기화
            json_path_arr.clear();
            tArray.clear();
            //textview 초기화
            mView_json.setText("");
        }
    };
    
    public String realpath = "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode != 1 || resultCode != RESULT_OK){
            return;
        }

        Uri selected_file = data.getData();
        String filename = selected_file.getLastPathSegment();
        Log.d("tag",filename);

        String p = Environment.getExternalStorageDirectory().getAbsolutePath();
        String [] split = filename.split(":");

        if ("primary".equals(split[0])){
            realpath = p + "/" + split[1];
        }
        Log.d("tag", realpath);
        Toast.makeText(getApplicationContext(), realpath, Toast.LENGTH_SHORT).show();

        String [] split2 = split[1].split("/");

        if (split2[1].startsWith("FistJSON")){
            Log.d("tag", "json 파일");
            String view_data = mView_json.getText().toString();
            view_data += realpath+"\n";
            mView_json.setText(view_data);
            //arr에 add만 하면 에러발생
        }else{
            Log.d("tag", "img 파일");
            String view_data = mView_img.getText().toString();
            view_data += realpath+"\n";
            mView_img.setText(view_data);
        }

    }


}
