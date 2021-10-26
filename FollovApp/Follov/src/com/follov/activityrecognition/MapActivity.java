package com.follov.activityrecognition;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoViewAttacher;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.follov.FollovApplication;
import com.follov.R;
import com.follov.activityrecognition.Constants.Extra;
import com.follov.db.DBPool;
import com.follov.db.vo.Loc_photo_tb_VO;
import com.follov.db.vo.Loc_route_tb_VO;
import com.follov.pref.FollovPref;
import com.follov.user.MyPhoto;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;




public class MapActivity extends FragmentActivity implements ClusterManager.OnClusterClickListener<MyPhoto>, ClusterManager.OnClusterInfoWindowClickListener<MyPhoto>, ClusterManager.OnClusterItemClickListener<MyPhoto>, ClusterManager.OnClusterItemInfoWindowClickListener<MyPhoto> {

	public static final String TAG = MapActivity.class.getSimpleName();

	private static final int BOUNDARY_PADDING = 110;

	GoogleMap mGoogleMap;
	//ArrayList<LatLng> locList = null;
	ArrayList<Loc_route_tb_VO> locRouteList = null;
	ArrayList<Loc_photo_tb_VO> photoList = null;
	LatLngBounds bounds = null;
	private ClusterManager<MyPhoto> mClusterManager;
	DisplayImageOptions options;
	PhotoViewAttacher mAttacher;
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_fragment);

		mGoogleMap = ((SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map)).getMap(); 
//		mGoogleMap.setOnInfoWindowClickListener(infoCListener2);
		loadLocationFromDB();
//		mGoogleMap.setInfoWindowAdapter(new InfoWindow(MapActivity.this));
//		setBoundary();
//
//		mGoogleMap.setOnCameraChangeListener(new OnCameraChangeListener() {
//
//			@Override
//			public void onCameraChange(CameraPosition arg0) {
//				
//				if(mGoogleMap == null || bounds == null) {
//					Log.e("kim", "mGoogleMap null or bounds == null");
//					return;
//				}
//				
//				CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, BOUNDARY_PADDING);
//				//mGoogleMap.moveCamera(cu);
//				mGoogleMap.animateCamera(cu);
//
//				mGoogleMap.setOnCameraChangeListener(null);
//			}
//		});
		options = new DisplayImageOptions.Builder()
		.showImageOnLoading(R.drawable.ic_stub)
		.showImageForEmptyUri(R.drawable.ic_empty)
		.showImageOnFail(R.drawable.ic_error)
        .cacheInMemory(true)
		.cacheOnDisc(true)
		.build();
		drawPolyLineBetweenLocations();
		
		//test//
		 FollovApplication app = (FollovApplication) getApplication();
		 app.testmapActivity = this;
		//test
		 
		startImage();
	}
	 private class PhotoRenderer extends DefaultClusterRenderer<MyPhoto>
	    {
	        private final IconGenerator mIconGenerator = new IconGenerator(getApplicationContext());
	        private final IconGenerator mClusterIconGenerator = new IconGenerator(getApplicationContext());
	        private final ImageView mClusterImageView;
	        private ImageView mImageView;
	        private final int mDimension;
	        private final int padding;
	        private int cnt=0;
//	        ImageSize targetSize = new ImageSize(120, 80); 
//	        Thread mainThread = null;
	        public PhotoRenderer()
	        {
	            super(getApplicationContext(), mGoogleMap, mClusterManager);
	            View multiProfile = getLayoutInflater().inflate(R.layout.multi_profile, null);
	            mClusterIconGenerator.setContentView(multiProfile);
	            mClusterImageView = (ImageView) multiProfile.findViewById(R.id.image);
	            mDimension = (int) getResources().getDimension(R.dimen.custom_profile_image);
	            padding = (int) getResources().getDimension(R.dimen.custom_profile_padding);
	            mImageView = new ImageView(getApplicationContext());
	            mImageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
	            mImageView.setPadding(padding, padding, padding, padding);
	        //    mainThread = Thread.currentThread();
	            mIconGenerator.setContentView(mImageView);
	        }
	       
	        
	        ImageLoader imageLoader = ImageLoader.getInstance();
	 
			class ViewHolder 
			{
				ImageView imageView;
				ProgressBar progressBar;
			}
		    @Override
	        protected void onBeforeClusterItemRendered(MyPhoto photo, final MarkerOptions markerOptions) 
	        {
	            // Draw a single person.
	            // Set the info window to show their name.
	            String url = "http://211.189.20.150:8080/FollovServer/upload/"+photo.getCouple_id()+"/"+photo.getName();
	            Log.i("하나씩", "하나씩 : " + url);
	            final ViewHolder holder;
				View view = null;
				view = getLayoutInflater().inflate(R.layout.item_grid_image,null, false);
					holder = new ViewHolder();
					assert view != null;
					holder.imageView = (ImageView) view.findViewById(R.id.image);
					holder.imageView.setLayoutParams(new ViewGroup.LayoutParams(mDimension, mDimension));
					holder.imageView.setPadding(padding, padding, padding, padding);
					holder.progressBar = (ProgressBar) view.findViewById(R.id.progress);
					view.setTag(holder);
			

				imageLoader.displayImage(url, holder.imageView, options, new SimpleImageLoadingListener()
				{
											 @Override
											 public void onLoadingStarted(String imageUri, View view) 
											 {
												 holder.progressBar.setProgress(0);
												 holder.progressBar.setVisibility(View.VISIBLE);
											 }

											 @Override
											 public void onLoadingFailed(String imageUri, View view,
													 FailReason failReason) 
											 {
												 holder.progressBar.setVisibility(View.GONE);
											 }

											 @Override
											 public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
												 Log.i("하나씩", "이미지 로딩완료"+imageUri);
											     markerOptions.icon(BitmapDescriptorFactory.fromBitmap(loadedImage));
												 holder.progressBar.setVisibility(View.GONE);
											 }
										 }, new ImageLoadingProgressListener() {
											 @Override
											 public void onProgressUpdate(String imageUri, View view, int current,
													 int total) {
												 holder.progressBar.setProgress(Math.round(100.0f * current / total));
											 }
										 }
				);
	            
//	           imageLoader.displayImage(url, mImageView, options, new ImageLoadingListener()
//	            {
//	                @Override
//	                public void onLoadingStarted(String imageUri, View view) {
//	                	 Log.i("하나씩", "이미지 로딩시작"+imageUri);
//	                     
//	                }
//	                @Override
//	                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//	                    
//	                }
//	                @Override
//	                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//	                	Log.i("하나씩", "이미지 로딩완료"+imageUri);
//	                    Bitmap icon = mIconGenerator.makeIcon();
//	                    markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
////	               	  synchronized(mainThread){
////	      	            	mainThread.notify();
////	      	           }
//	                }
//	                @Override
//	                public void onLoadingCancelled(String imageUri, View view) {
//	                   
//	                }
//	            },
//	            new ImageLoadingProgressListener()
//	            {
//	                @Override
//	                public void onProgressUpdate(String imageUri, View view, int current, int total) 
//	                {
//	                   
//	                }
//	            });
//	            synchronized(mainThread)
//	            {
//	           	  try
//	 				{
//	 					mainThread.wait();
//	 				} catch (InterruptedException e)
//	 				{
//	 					// TODO Auto-generated catch block
//	 					e.printStackTrace();
//	 				}
//	            }
	            Log.i("하나씩", "이 이미지 로딩 메소드 끝남"+url);
//	            Bitmap icon = mIconGenerator.makeIcon();
//	            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
	           
	    //    ImageLoader.getInstance().displayImage(url, mImageView,options);
	    //      Bitmap icon = mIconGenerator.makeIcon();
//	            ImageLoader imageLoader = ImageLoader.getInstance();
//	            Bitmap icon = imageLoader.loadImageSync(url,targetSize,options);
//	            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
	            	
	          
//		            new MyThread(url,markerOptions,mainThread).start();
////		            try
////					{
////		            	Thread.sleep(20);
////					} catch (InterruptedException e)
////					{
////						// TODO Auto-generated catch block
////						e.printStackTrace();
////					}
//////		            
//		            try
//					{
//						mainThread.wait();
//					} catch (InterruptedException e)
//					{
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
	           }
	            
	            //메인왜이트.
//	        class MyThread extends Thread
//	        {
//	        	MarkerOptions mo;
//	        	String url;
//	        	Thread mainThread;
//	        	MyThread(String url,MarkerOptions mo,Thread mainThread)
//	        	{
//	        		this.mo = mo;
//	        		this.url = url;
//	        		this.mainThread = mainThread;
//	        	}
//	        	public void run()
//	        	{
//		            ImageLoader imageLoader = ImageLoader.getInstance();
//	                Bitmap icon = imageLoader.loadImageSync(url,targetSize,options);
//	               	mo.icon(BitmapDescriptorFactory.fromBitmap(icon));
//		        //   Message msg = Message.obtain();
//		        //   msg.obj = mainThread;
//		            synchronized(mainThread){
//		            	mainThread.notify();
//		           }
//		          //  handler.sendMessage(msg);
//	        	}
//	        }
	        @Override
	        protected void onBeforeClusterRendered(Cluster<MyPhoto> cluster, MarkerOptions markerOptions) 
	        {
	            // Draw multiple people.i
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
	          //MultiDrawable multiDrawable = new MultiDrawable(profilePhotos);
	          //multiDrawable.setBounds(0, 0, width, height);
	            MyPhoto photo = cluster.getItems().iterator().next();
	            String url = "http://211.189.20.150:8080/FollovServer/upload/"+photo.getCouple_id()+"/"+photo.getName();
	            Log.i("하나씩", "하나씩 두개: " + url);
	            ImageLoader.getInstance().displayImage(url, mClusterImageView,options);
	        //    mClusterImageView.setImageDrawable(multiDrawable);
	            Bitmap icon = mClusterIconGenerator.makeIcon(String.valueOf(cluster.getSize()));
	            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
	            if(cnt==0)
	            {
	            	Log.i("여기오냐?", "여기오냐 : "+cnt);
	            	LatLng ll = new LatLng(locRouteList.get(0).getLatitude(),locRouteList.get(0).getLongitude());
	            	CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(ll,21);
	            	mGoogleMap.animateCamera(cu);
	            	
//	            	Handler handler = new Handler() {
//	        			public void handleMessage(android.os.Message msg) {
//	        				if (true)
//	        				{
//	        					ProgressBarThread progressBar = new ProgressBarThread(FollovJoinMapActivity.this);
//	        					
//	        					progressBar.start();						
//	        					for(int i=0;i<500000000;i++)
//	        					{
//	        						;
//	        					}
//	        					progressBar.stopProgressBar();
//	        				}
//	        			};
//	        		};
//	        		handler.sendEmptyMessageDelayed(0, 500);
	            	ProgressBarThread progressBar = new ProgressBarThread(MapActivity.this);
					progressBar.start();						
					for(int i=0;i<500000000;i++)
					{
						;
					}
					progressBar.stopProgressBar();
	            	CameraUpdate cu2 = CameraUpdateFactory.newLatLngZoom(ll,2);
	            	mGoogleMap.animateCamera(cu2);
	            }
	            cnt++;
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
	    	Intent intent = new Intent(this, ImageGridActivity.class);
	    	int cnt = cluster.getSize();
	    	String temp[] = new String[cnt];
	    	int i=0;
	    	for (MyPhoto photo : cluster.getItems())
	    	{
	    		String url = "http://211.189.20.150:8080/FollovServer/upload/"+photo.getCouple_id()+"/"+photo.getName();
	    		temp[i++] = url;
	    	}
	    	intent.putExtra(Extra.IMAGES, temp);
			startActivity(intent);
	        return false;
	    }

	    @Override
	    public void onClusterInfoWindowClick(Cluster<MyPhoto> cluster) {
	        // Does nothing, but you could go to a list of the users.
	    }

	    @Override
	    public boolean onClusterItemClick(MyPhoto photo) {
	    	 String url = "http://211.189.20.150:8080/FollovServer/upload/"+photo.getCouple_id()+"/"+photo.getName();
	    	 Intent intent = new Intent(this, ImageGridActivity.class);
	    	 String temp[] = new String[1];
	    	 temp[0]=url;
	 		 intent.putExtra(Extra.IMAGES, temp);
	 		 startActivity(intent);
	    	 //mAttacher = new PhotoViewAttacher(imageView);
	        // Does nothing, but you could go into the user's profile page, for example.
	        return false;
	    }

	    @Override
	    public void onClusterItemInfoWindowClick(MyPhoto item) {
	        // Does nothing, but you could go into the user's profile page, for example.
	    }

	    protected void startImage() {
	    //	mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(51.503186, -0.126446), 9.5f));
	        mClusterManager = new ClusterManager<MyPhoto>(this, mGoogleMap);
	        mClusterManager.setRenderer(new PhotoRenderer());
	        mGoogleMap.setOnCameraChangeListener(mClusterManager);
	        mGoogleMap.setOnMarkerClickListener(mClusterManager);
	        mGoogleMap.setOnInfoWindowClickListener(mClusterManager);
	        mClusterManager.setOnClusterClickListener(this);
	        mClusterManager.setOnClusterInfoWindowClickListener(this);
	        mClusterManager.setOnClusterItemClickListener(this);
	        mClusterManager.setOnClusterItemInfoWindowClickListener(this);
	        addItems();
	        mClusterManager.cluster();
	    }
	    private void addItems()
	    {
	    	if(locRouteList == null || !(locRouteList.size() > 0)) return;
			MainActivity.PRINT_LOG(TAG, "locList size : "+locRouteList.size());
			if(photoList!=null)
			{
				MainActivity.PRINT_LOG(TAG, "PhotoList size : "+photoList.size());
			}
			for(Loc_route_tb_VO point : locRouteList)
			{
				if(point.getIsSpecial().equals("y"))
				{
					if(!checkPhotoExist(point.getLoc_no()))
					{
						mGoogleMap.addMarker(
							new MarkerOptions()
							.position(new LatLng(point.getLatitude(), point.getLongitude()))).showInfoWindow();
					}
					else
					{
						ArrayList<Loc_photo_tb_VO> list = getPhoto(point.getLoc_no());
						if(list==null||list.size()==0)
						{
							;
						}
						else
						{
							String couple_id = FollovPref.getString("coupleid", MapActivity.this);
							for(int i=0;i<list.size();i++)
							{
								Loc_photo_tb_VO lp = list.get(i);
								Log.i("여기다", couple_id+"/"+lp.getName());
								mClusterManager.addItem(new MyPhoto(couple_id,lp.getName()
									,new LatLng(point.getLatitude(),point.getLongitude())));
							}
						}
						// 사진불러오기.
					}
				}
			}
	    }

		private boolean checkPhotoExist(int loc_no)
		{
			if(photoList==null)
			{
				return false;
			}
			for(int i=0;i<photoList.size();i++)
			{
				Loc_photo_tb_VO photo = photoList.get(i);
				if(photo.getLoc_no()==loc_no&&photo.getIs_img_uploaded().equals("y"))
				{
					return true;
				}
			}
			return false;
		}
		private ArrayList<Loc_photo_tb_VO> getPhoto(int loc_no)
		{
			if(photoList==null)
			{
				return null;
			}
			ArrayList<Loc_photo_tb_VO> list = new ArrayList<Loc_photo_tb_VO>(); 
			for(int i=0;i<photoList.size();i++)
			{
				if(photoList.get(i).getLoc_no()==loc_no)
				{
					list.add(photoList.get(i));
				}
			}
			return list;
		}

//	private OnInfoWindowClickListener infoCListener2= new OnInfoWindowClickListener() //마커 클릭시 이미지 갤러리 보여주기.
//	{
//		@Override
//		public void onInfoWindowClick(Marker marker) {
//		//click한 infowindow의 marker가 전달 된다.
//		}
//	};
	private void loadLocationFromDB(){
		DBPool dbPool = DBPool.getInstance(getApplicationContext());
		int code = dbPool.recentDateCode();
		locRouteList = dbPool.getLocationRoutes(code);
		photoList = dbPool.getLocationPhotos(code);
		//locList = dbPool.getLocationPoints(dbPool.recentDateCode());
	}

	private void setBoundary(){
		
		LatLngBounds.Builder builder = new LatLngBounds.Builder();
		
		if(locRouteList == null || !(locRouteList.size() > 0)) return;
		
		for(Loc_route_tb_VO point : locRouteList){
			
			builder.include(new LatLng(point.getLatitude(), point.getLongitude()));
		}
		bounds = builder.build();
		
	}
	
	private ArrayList<LatLng> converToLatLngList(ArrayList<Loc_route_tb_VO> locrouteList){
		ArrayList<LatLng> locList = new ArrayList<LatLng>();
		
		if(locrouteList == null || !(locRouteList.size() > 0)) return null;
		
		for(Loc_route_tb_VO point : locrouteList){
			locList.add(new LatLng(point.getLatitude(), point.getLongitude()));
		}
		
		return locList;
	}
	
	private void drawPolyLineBetweenLocations(){
		
		if(locRouteList == null || !(locRouteList.size() > 0)) return;
		
		ArrayList<LatLng> locList = converToLatLngList(locRouteList);
		
		if(locList == null) return;
		
		
		PolylineOptions rect = new PolylineOptions();
		rect.addAll(locList).geodesic(true);

		Polyline polyline = mGoogleMap.addPolyline(rect);
	}

	
	public void testAddMarker(Location my, double loversLat, double loversLng, String time, String loversTime){
		
		MarkerOptions myOption = new MarkerOptions();
		myOption.position(new LatLng(my.getLatitude(), my.getLongitude()));
		myOption.title("����"+time);
		
		mGoogleMap.addMarker(myOption).showInfoWindow();
		
		MarkerOptions loversOption = new MarkerOptions();
		loversOption.position(new LatLng(loversLat, loversLng));
		loversOption.title("����"+loversTime);
		
		mGoogleMap.addMarker(loversOption).showInfoWindow();
		
	}
	


}
