package com.game.angrybirds;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import android.util.*;

public class Player extends InputAdapter
{
	protected final Camera camera;
	protected float degreesPerPixel = 0.0938f;
	protected final Vector3 tmp = new Vector3();
	protected final Vector3 tmp2 = new Vector3();
	public final Vector3 playerDirection = new Vector3(1,0,1);
	protected int startX, startY, pointer;
	protected boolean isFirstTouch = true;

	public static final class MOVE {
		public static final int STOP = 0;
		public static final int FORWORD = 1;
		public static final int BACK = 2;
		public static final int LEFT = 3;
		public static final int RIGHT = 4;
		public static final int RUN = 5;
		public static final int RISE = 6;
		public static final int DOWN = 7;
		public static final int LEFTFRONT = 8;
		public static final int RIGHTFRONT = 9;
		public static final int JUMP = 10;
	}
	
	public Player(Camera camera) {
		this.camera = camera;
		tmp2.set(camera.direction);
		playerDirection.set(camera.direction.x, 0f, camera.direction.z).nor();
	}

	public void setDegreesPerPixel(float degreesPerPixel) {
		this.degreesPerPixel = degreesPerPixel;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		if(isFirstTouch) {
			Log.v("Player", "touchDown:x:"+screenX+"; y:"+screenY+"; p:"+pointer+"; b:"+button);
			startX = screenX;
			startY = screenY;
		    this.pointer = pointer;
			isFirstTouch = false;
		}
		return super.touchDown(screenX, screenY, pointer, button);
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(pointer == this.pointer) {
			Log.v("Player", "touchDragged:x:"+screenX+"; y:"+screenY+"; p:"+pointer);
		    float deltaX = (startX-screenX)*degreesPerPixel;
		    //-Gdx.input.getDeltaX() * degreesPerPixel;
		    float deltaY = (startY-screenY)*degreesPerPixel*1.75f;
		    //-Gdx.input.getDeltaY() * degreesPerPixel*1.75f;
	    	Log.v("delta", "x: "+deltaX+"y: "+deltaY);
		    //Log.v("deltaO", "sx: "+((screenX-scc.x)*degreesPerPixel)+"sy: "+((scc.y-screenY)*degreesPerPixel*1.75f));
		    
		    startX = screenX;
		    startY = screenY;
		    Log.v("player", "pointer: "+pointer);
		    //Log.v("playerclass", "befo");
		    playerDirection.rotate(camera.up, deltaX);
		    //Log.v("playerclass", "ed");
            
		    camera.direction.rotate(camera.up, deltaX);
		    tmp.set(playerDirection).crs(camera.up).nor();
		    tmp2.set(camera.direction);
		    tmp2.rotate(tmp, deltaY);
		    if((tmp2.dot(playerDirection)>0.025f||deltaY<0)&&(tmp2.dot(playerDirection)>0.025f||deltaY>0)) {
		    	camera.direction.rotate(tmp, deltaY);
		    }
		}
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		Log.v("Player", "touchUp:x:"+screenX+"; y:"+screenY+"; p:"+pointer+"; b:"+button);
		if(pointer == this.pointer)
		    isFirstTouch = true;
		return super.touchUp(screenX, screenY, pointer, button);
	}
	

	public void update() {
		update(Gdx.graphics.getDeltaTime());
	}

	private void update(float deltaTime) {
		//update(Gdx.graphics.getDeltaTime());
	}
}
