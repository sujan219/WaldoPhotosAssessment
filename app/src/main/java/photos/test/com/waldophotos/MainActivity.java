package photos.test.com.waldophotos;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import photos.test.com.service.PhotoLoadListener;
import photos.test.com.service.PhotoLoader;
import photos.test.com.service.PhotoLoaderFactory;
import photos.test.com.service.RestClient;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private PhotoLoader photoLoader;
    private RecyclerViewAdapter recyclerViewAdapter;

    private static final int PERMISSION_REQUEST_ID = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermissions();
    }

    private void checkPermissions(){
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_ID);
        } else {
            setContent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_ID){
            setContent();
        }
    }

    private void setContent() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        photoLoader = PhotoLoaderFactory.getPhotoLoader(this);
        photoLoader.loadMore(new PhotoLoadListener() {
            @Override
            public void loaded() {
                recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, photoLoader, recyclerView);
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.setOnLoadMoreListener(new RecyclerViewAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        progressBar.setVisibility(View.VISIBLE);
                        photoLoader.loadMore(new PhotoLoadListener() {
                            @Override
                            public void loaded() {
                                recyclerViewAdapter.setLoaded();
                                recyclerViewAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }
                });
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}