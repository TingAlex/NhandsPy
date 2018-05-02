package com.tingalex.picsdemo.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.tingalex.picsdemo.R;
import com.tingalex.picsdemo.db.Good;

import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class ShareActivity extends AppCompatActivity {

    public static final int CHOOSE_PHOTO = 2;
    private ImageView picture;
    private ImageView pictureFromWeb;
    private List<String> picurls;
    private Good good;
    private EditText titleText;
    private EditText descriptionText;
    private Boolean containPackageCost;
    private RadioGroup PackageCostGroup;
    private RadioGroup CategoryGroup;
    private String category;
    private EditText priceText;
    private Button chooseFromAlbumButton;
    private Button shareButton;
    private String imagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bmob.initialize(this, "195f864122ce10a6d3197a984d4c6370");
        setContentView(R.layout.activity_share);
        //清空上一次给的图片地址
        imagePath = "";
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("tempImagePath", imagePath);
        editor.apply();
        //是否包邮
        containPackageCost = true;
        PackageCostGroup = findViewById(R.id.PackageCost);
        PackageCostGroup.setOnCheckedChangeListener(listenContainChange);
        //商品类别
        category = "daily";
        CategoryGroup = findViewById(R.id.category);
        CategoryGroup.setOnCheckedChangeListener(listenCategoryChange);
        //
        picture = findViewById(R.id.picture);
        pictureFromWeb = findViewById(R.id.pictureFromWeb);
        //
        titleText = findViewById(R.id.titleText);
        descriptionText = findViewById(R.id.descriptionText);
        priceText = findViewById(R.id.priceText);
        //
        chooseFromAlbumButton = findViewById(R.id.choose_from_album);
        chooseFromAlbumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(ShareActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ShareActivity.this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    }, 1);
                } else {
                    openAlbum();
                }
            }
        });
        //
        shareButton = findViewById(R.id.shareButton);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //try to upload a picture
                final String[] pics = new String[1];
                //在这里手动改一下地址测试下：
                SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
                String imagePath = preferences.getString("tempImagePath", "");
                Log.i("bmob", "handleImageOnKitKat: after click imagePath " + imagePath);
                pics[0] = imagePath;
                BmobFile.uploadBatch(pics, new UploadBatchListener() {

                    @Override
                    public void onSuccess(List<BmobFile> files, List<String> urls) {
                        //1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
                        //2、urls-上传文件的完整url地址

                        if (urls.size() == pics.length) {//如果数量相等，则代表文件全部上传完成
                            //do something
                            picurls = urls;
                            //****在这里才可以去创建goods！
                            createGood(picurls);
                        }

                    }

                    @Override
                    public void onError(int statuscode, String errormsg) {
                        Log.d("test", "onError: " + "错误码" + statuscode + ",错误描述：" + errormsg);
                    }

                    @Override
                    public void onProgress(int curIndex, int curPercent, int total, int totalPercent) {
                        //1、curIndex--表示当前第几个文件正在上传
                        //2、curPercent--表示当前上传文件的进度值（百分比）
                        //3、total--表示总的上传文件数
                        //4、totalPercent--表示总的上传进度（百分比）
                    }
                });
            }
        });

        //TODO: get user pic and upload and get web url.


    }

    private RadioGroup.OnCheckedChangeListener listenCategoryChange = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int id = radioGroup.getCheckedRadioButtonId();
            switch (id) {
                case R.id.food:
                    category = "food";
                    break;
                case R.id.daily:
                    category = "daily";
                    break;
                case R.id.tech:
                    category = "tech";
                    break;
            }
        }
    };
    private RadioGroup.OnCheckedChangeListener listenContainChange = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int id = radioGroup.getCheckedRadioButtonId();
            switch (id) {
                case R.id.yesPackageCost:
                    containPackageCost = true;
                    break;
                case R.id.noPackageCost:
                    containPackageCost = false;
                    break;
            }
        }
    };

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    if (Build.VERSION.SDK_INT >= 19) {
                        handleImageOnKitKat(data);
                    } else {
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(this, uri)) {
            String docId = DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            imagePath = uri.getPath();
        }

        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("tempImagePath", imagePath);
        editor.apply();
        displayImage(imagePath);
        Log.i("bmob", "handleImageOnKitKat: imagePath " + imagePath);
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            picture.setImageBitmap(bitmap);

        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    public List<String> savePics() {
        return null;
    }

    public String createGood(List<String> picurls) {
        good = new Good();
        good.setUid();
        good.setTitle(titleText.getText().toString());
        good.setDescription(descriptionText.getText().toString());
        good.setPrice(Double.valueOf(priceText.getText().toString()));
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
//        String userUid = preferences.getString("uid", "");
        String userEmail = preferences.getString("email", "");
        good.setBelongto(userEmail);
        good.setCategroy(category);
        good.setTradeState("onSell");
        good.setPicurls(picurls);
        good.setContainPackageCost(containPackageCost);
//        Glide.with(this).load(picurls.get(0)).into(pictureFromWeb);
        good.save(new SaveListener<String>() {
            @Override
            public void done(String objectId, BmobException e) {
                if (e == null) {
                    Log.d("test", "done: add successful " + objectId);
                    finish();
                } else {
                    Log.d("test", "done: add failed " + e.getMessage());
                }
            }
        });
        return good.getUid();
    }
}
