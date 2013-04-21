package fr.spaceapps.starlighttlse;

import rajawali.Camera;
import rajawali.RajawaliActivity;
import rajawali.math.Quaternion;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class Visualisation extends RajawaliActivity  implements OnTouchListener{

	private Renderer mRenderer;

	private boolean currentlyZooming;

	private float mPreviousDistance;

	private float mPreviousX;

	private float mPreviousY;
	private final float TRACKBALL_SCALE_FACTOR = 36.0f;
	private final float TOUCH_SCALE_FACTOR = 180f / 320;
	
	private final static String TAG = "Visualisation";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRenderer = new Renderer(this);
        mRenderer.setSurfaceView(mSurfaceView);
        mSurfaceView.setOnTouchListener(this);
        super.setRenderer(mRenderer);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.visualisation, menu);
		return true;
	}

	@Override
	public boolean onTouch(View v, MotionEvent e) {
		float x = e.getX();
		float y = e.getY();
		switch (e.getAction()) {
		case MotionEvent.ACTION_MOVE:
			currentlyZooming = e.getPointerCount()>1;

			Camera cam = mRenderer.getCamera();
			
			if(currentlyZooming){
				if(e.getPointerCount()<2){
					currentlyZooming = false;
				}else{
					float x2 = e.getX(1);
					float y2 = e.getY(1);
					float newDistance = (float) Math.sqrt((x-x2)*(x-x2)+(y-y2)*(y-y2));
					if(mPreviousDistance <=0){
						mPreviousDistance = newDistance;
					}
					
					int zoomMethod = 0;
					float zoomRatio = 1.0f;
					switch (zoomMethod) {
					case 0:
						// zoom additionnel sur la distance
						zoomRatio = newDistance-mPreviousDistance;
						mPreviousDistance = newDistance;
						
						//mRenderer.mDistance += zoomRatio/30.0f;
						//Log.i("TouchSurfaceView", "dist = "+mRenderer.mDistance+ " |event_dist : "+mPreviousDistance+ " ratio -= "+zoomRatio/36.0f);
						break;
					case 1:
						// zoom proportionnel sur la distance
						zoomRatio = newDistance/mPreviousDistance;
						mPreviousDistance = newDistance;
						//mRenderer.mDistance /= zoomRatio/2.0f;
						//Log.i("TouchSurfaceView", "dist = "+mRenderer.mDistance+ " |event_dist : "+mPreviousDistance+ " ratio *= "+zoomRatio/2.0f);
						break;
					default:
						break;
					}
					Log.d(TAG, "Zoom Ratio = " + zoomRatio);
					cam.setZ(cam.getZ() - zoomRatio/10);//TODO juste pour commencer, faut pas bouger que le Z au final
//					if(mRenderer.mDistance > -10){
//						mRenderer.mDistance = -10.0f;
//					}
					
					
				}
			}else{
				Log.i("onTouchEvent"," not zooming");
				//dx and dy according to the screen referential, they need to be translated to dx, dy and dz in the referential of the world
				float dx = x - mPreviousX;
				float dy = y - mPreviousY;
				
				
				cam.setRotX(cam.getRotX() + dx * TOUCH_SCALE_FACTOR);
				cam.setRotY(cam.getRotY() + dy * TOUCH_SCALE_FACTOR);
				
				Log.d(TAG, "dx = "+dx+" , dy = "+dy);
			}
			//requestRender();
			break;
		case MotionEvent.ACTION_POINTER_2_DOWN:
			currentlyZooming = true;
			float x2 = e.getX(1);
			float y2 = e.getY(1);
			mPreviousDistance = (float) Math.sqrt((x-x2)*(x-x2)+(y-y2)*(y-y2));
			break;
		case MotionEvent.ACTION_POINTER_2_UP:
			currentlyZooming =  false;
			break;
		}

		mPreviousX = x;
		mPreviousY = y;
//		if(mRenderer.mDistance > -10){
//			mRenderer.mDistance = -10.0f;
//		}
		
		return true;
	}
	
	

}
