package photos.test.com.waldophotos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import photos.test.com.service.PhotoLoadListener;
import photos.test.com.service.PhotoLoader;
import photos.test.com.service.PhotoLoaderFactory;

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

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
        ImageLoader.getInstance().init(config);
        setContent();
    }

    private void setContent() {
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        photoLoader = PhotoLoaderFactory.createPhotoLoader(this);
        photoLoader.loadMore(new PhotoLoadListener() {
            @Override
            public void loaded(int lastSize) {
                recyclerViewAdapter = new RecyclerViewAdapter(MainActivity.this, photoLoader, recyclerView);
                recyclerView.setAdapter(recyclerViewAdapter);
                recyclerViewAdapter.setOnLoadMoreListener(new RecyclerViewAdapter.OnLoadMoreListener() {
                    @Override
                    public void onLoadMore() {
                        progressBar.setVisibility(View.VISIBLE);
                        photoLoader.loadMore(new PhotoLoadListener() {
                            @Override
                            public void loaded(int lastSize) {
                                recyclerViewAdapter.setLoaded();
                                recyclerViewAdapter.notifyItemInserted(lastSize);
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void loadFailed() {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(MainActivity.this, "Cannot load photos at this time", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void loadFailed() {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(MainActivity.this, "Cannot load photos at this time", Toast.LENGTH_SHORT).show();
            }
        });
    }
}