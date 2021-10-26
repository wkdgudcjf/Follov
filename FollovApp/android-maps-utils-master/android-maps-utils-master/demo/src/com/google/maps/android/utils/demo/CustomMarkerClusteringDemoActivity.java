package com.google.maps.android.utils.demo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.google.maps.android.utils.demo.model.MyPhoto;
import com.google.maps.android.utils.demo.model.Person;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

/**
 * Demonstrates heavy customisation of the look of rendered clusters.
 */
public class CustomMarkerClusteringDemoActivity extends FragmentActivity implements ClusterManager.OnClusterClickListener<MyPhoto>, ClusterManager.OnClusterInfoWindowClickListener<MyPhoto>, ClusterManager.OnClusterItemClickListener<MyPhoto>, ClusterManager.OnClusterItemInfoWindowClickListener<MyPhoto> {
    private ClusterManager<MyPhoto> mClusterManager;
    private GoogleMap mMap;
    private Random mRandom = new Random(2014);
	ImageLoaderConfiguration config;
	DisplayImageOptions options;

    /**
     * Draws profile photos inside markers (using IconGenerator).
     * When there are multiple people in the cluster, draw multiple photos (using MultiDrawable).
     */
	@Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map);
        mMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap();
       config = new ImageLoaderConfiguration.Builder(getApplicationContext())
       .memoryCacheExtraOptions(100, 100) // default = device screen dimensions
       .discCacheExtraOptions(100, 100, CompressFormat.JPEG, 75, null)
        .threadPoolSize(3) // defaultk
        .threadPriority(Thread.NORM_PRIORITY - 1) // default
        .tasksProcessingOrder(QueueProcessingType.FIFO) // default
        .denyCacheImageMultipleSizesInMemory()
        .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
        .writeDebugLogs()
        .build();
		ImageLoader.getInstance().init(config);
		//
		options = new DisplayImageOptions.Builder()
        .cacheInMemory(true)
		.cacheOnDisc(true)
		.build();
        startDemo();
    }
    private class PhotoRenderer extends DefaultClusterRenderer<MyPhoto>
    {
        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
        private final ImageView mClusterImageView;
        private final int mDimension;
        private final int padding;
        public PhotoRenderer() {
            super(getApplicationContext(), mMap, mClusterManager);

            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
            mClusterIconGenerator.setContentView(multiProfile);
            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
            padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
        }

        @Override
        protected void onBeforeClusterItemRendered(MyPhoto photo, MarkerOptions markerOptions) {
            // Draw a single person.
            // Set the info window to show their name.
       // 	ImageView mImageView = new ImageView(getApplicationContext());
        //	mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
        //	mImageView.setPadding(padding, padding, padding, padding);
        //    mIconGenerator.setContentView(mImageView);
            String url = "http://211.189.20.150:8080/FollovServer/upload/"+photo.getCouple_id()+"/"+photo.getDate()+"/"+photo.getDate_code()+"/"+photo.getLoc_no()+"/"+photo.getName();
            Log.i("하나씩", "하나씩 : " + url);
        //    ImageLoader.getInstance().displayImage(url, mImageView,options);
         //   Bitmap icon = mIconGenerator.makeIcon();
            ImageSize targetSize = new ImageSize(120, 80); 
            ImageLoader imageLoader = ImageLoader.getInstance();
            Bitmap icon = imageLoader.loadImageSync(url,targetSize,options);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));

        }

        @Override
        protected void onBeforeClusterRendered(Cluster<MyPhoto> cluster, MarkerOptions markerOptions) {
            // Draw multiple people.
            // Note: this method runs on the UI thread. Don't spend too much time in here (like in this example).
         //   List<Drawable> profilePhotos = new ArrayList<Drawable>(Math.min(4, cluster.getSize()));
          //  int width = mDimension;
          //  int height = mDimension;

          //  for (MyPhoto p : cluster.getItems()) {
          //      Draw 4 at most.
          //        if (profilePhotos.size() == 4) break;
          //     Drawable drawable = getResources().getDrawable(p.profilePhoto);
          //      drawable.setBounds(0, 0, width, height);
          //      profilePhotos.add(drawable);
          //  }
          //  MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
          //  multiDrawable.setBounds(0, 0, width, height);
            MyPhoto photo = cluster.getItems().iterator().next();
            String url = "http://211.189.20.150:8080/FollovServer/upload/"+photo.getCouple_id()+"/"+photo.getDate()+"/"+photo.getDate_code()+"/"+photo.getLoc_no()+"/"+photo.getName();
            Log.i("하나씩", "하나씩 두개: " + url);
            ImageLoader.getInstance().displayImage(url, mClusterImageView,options);
        //    mClusterImageView.setImageDrawable(multiDrawable);
            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster cluster) {
            // Always render clusters.
            return cluster.getSize() > 1;
        }
    }

    @Override
    public boolean onClusterClick(Cluster<MyPhoto> cluster) {
        // Show a toast with some info when the cluster is clicked.
    //    String firstName = cluster.getItems().iterator().next().name;
    //    Toast.makeText(this, cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();
        return true;
    }

    @Override
    public void onClusterInfoWindowClick(Cluster<MyPhoto> cluster) {
        // Does nothing, but you could go to a list of the users.
    }

    @Override
    public boolean onClusterItemClick(MyPhoto item) {
        // Does nothing, but you could go into the user's profile page, for example.
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(MyPhoto item) {
        // Does nothing, but you could go into the user's profile page, for example.
    }

    protected void startDemo() {
    //	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 9.5f));

        mClusterManager = new ClusterManager<MyPhoto>(this, mMap);
        mClusterManager.setRenderer(new PhotoRenderer());
        mMap.setOnCameraChangeListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterInfoWindowClickListener(this);
        mClusterManager.setOnClusterItemClickListener(this);
        mClusterManager.setOnClusterItemInfoWindowClickListener(this);

        addItems();
        mClusterManager.cluster();
    }

    private void addItems() {
    	// mClusterManager.addItem(new MyPhoto(1,1,"test.png","00-00-00","test",position()));
     	
         
     	mClusterManager.addItem(new MyPhoto(3,32,"20140426_073623.jpg","14-04-26","wkd_kgffy1",position()));

    //     mClusterManager.addItem(new MyPhoto(3,32,"20140426_073625.jpg","14-04-26","wkd_kgffy1",position()));

  //       mClusterManager.addItem(new MyPhoto(3,32,"20140426_073627.jpg","14-04-26","wkd_kgffy1",position()));

//         mClusterManager.addItem(new MyPhoto(3,15,"20140426_073053.jpg","14-04-26","wkd_kgffy1",position()));
         
 
    }

    private LatLng position() {
        return new LatLng(random(51.6723432, 51.38494009999999), random(0.148271, -0.3514683));
    }

    private double random(double min, double max) {
        return mRandom.nextDouble() * (max - min) + min;
    }
}
