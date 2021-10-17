package com.game.angrybirds;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.*;
import com.badlogic.gdx.graphics.g3d.environment.*;
import com.badlogic.gdx.graphics.g3d.model.*;
import com.badlogic.gdx.graphics.g3d.utils.*;
import com.badlogic.gdx.maps.objects.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.utils.*;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.*;
import com.badlogic.gdx.assets.*;
import com.badlogic.gdx.graphics.glutils.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.scenes.scene2d.utils.*;
import android.util.*;
import android.os.Build;
import java.util.*;

public class MyGdxGame implements ApplicationListener
{
	final static private int worldHeight = 64;
	final static private int worldEdgeLength = 128;
	
    public SpriteBatch batch;
	public Environment environment;
    public PerspectiveCamera cam;
	public ModelBatch modelBatch;
	public Model model;
	public Texture[] grass;
	public TextureRegion[] tre;
	public BitmapFont font;
	public Array<GameObject> instances = new Array<GameObject>();
	public boolean done = false;
	public int[][][] world = new int[worldEdgeLength][worldHeight][worldEdgeLength];
	private Vector3 tempPosition = new Vector3();
	private AssetManager assets;
	private ModelInstance sky;
	private Texture gui;
	private TextureRegion[] guir = new TextureRegion[12];
	private Stage stage;
	private ImageButton[] btn = new ImageButton[12];
	private ModelInstance pmi;
	private Vector3 playercoo = new Vector3();
	private Player mplayer;
	private static final long DOUBLE_TIME = 300;
	private static long lastClickTime = 0;
	private static long lastClickTime2 = 0;
	private static long lastClickTime3 = 0;
	private int movement;
	private Vector3 playerVel = new Vector3(0,0,0);
	private Vector3 tmp = new Vector3(0,0,0);
	private InputEvent touchup = new InputEvent();
	private InputEvent touchdown = new InputEvent();
	private boolean[] isUp = new boolean[12];
	private boolean isRun = false;
	private int[] pointers = new int[12];
	
	@Override
	public void create()
	{
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		stage = new Stage(new StretchViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
		
		touchup.setType(InputEvent.Type.touchUp);
		touchdown.setType(InputEvent.Type.touchDown);
		
		movement = Player.MOVE.STOP;
		batch = new SpriteBatch();
		font = new BitmapFont();
		
		float[][] pn = PerlinNoiseGenerator.generateSmoothNoise(PerlinNoiseGenerator.generateWhiteNoise(worldEdgeLength, worldEdgeLength), 4);
		
		for(int i=0;i<worldEdgeLength;i++) {
			for(int k=0;k<worldEdgeLength;k++) {
				for(int j=0;j<pn[i][k]*60+1;j++) {
				    world[i][j][k] = 1;
				}
		    }
		}
		
		int h = worldHeight;
		while(world[3][--h][3]!=1) {}
		
		playercoo.set(3.0f, (float)h+1.4f, 3.0f);
		
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 1.0f, 1.0f, 1.0f, 1.0f));
		//environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1.0f, -0.8f, - 0.2f));
		
		grass = new Texture[4];
		tre = new TextureRegion[4];
		
		modelBatch = new ModelBatch();
		cam = new PerspectiveCamera(60, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(playercoo.x, playercoo.y+0.7f, playercoo.z);
		cam.direction.set(1.0f, 0f, 1.0f);
		cam.near = 0.1f;
		cam.far = 300f;
		
		
		mplayer = new Player(cam);

		InputMultiplexer imp = new InputMultiplexer(stage, mplayer);
		
		Gdx.input.setInputProcessor(imp);
		
	    
		gui = new Texture(Gdx.files.internal("data/gui.png"));
		
		guir[4] = new TextureRegion(gui,0f,108f/256f,130f/256f,160f/256f);
        guir[5] = new TextureRegion(gui,218f/256f,64f/256f,236f/256f,100f/256f);
		TextureRegion btns[][] = guir[4].split(26,26);
		btns[0][4].setRegion(107f/256f,110f/256f,128f/256f,131f/256f);
		btns[1][4].setRegion(107f/256f,136f/256f,128f/256f,157f/256f);
		TextureRegion btns2[][] = guir[5].split(18,18);
		TextureRegionDrawable[][] trrd = new TextureRegionDrawable[2][6];
		for(int i=0;i<2;i++) {
			for(int j=0;j<5;j++) {
				trrd[i][j] = new TextureRegionDrawable(btns[i][j]);
				trrd[i][j].setMinWidth(180f);
				trrd[i][j].setMinHeight(180f);
			}
		}
		trrd[0][5] = new TextureRegionDrawable(btns2[0][0]);
		trrd[0][5].setMinWidth(150f);
		trrd[0][5].setMinHeight(150f);
		
		trrd[1][5] = new TextureRegionDrawable(btns2[1][0]);
		trrd[1][5].setMinWidth(150f);
		trrd[1][5].setMinHeight(150f);
		
		
		btn[0] = new ImageButton(trrd[0][0]);
		btn[0].setPosition(200,360);
		btn[0].setChecked(false);
		btn[0].setTouchable(Touchable.disabled);
		
		btn[1] = new ImageButton(trrd[0][1]);
		btn[1].setPosition(20,180);
		btn[1].setChecked(false);
		btn[1].setTouchable(Touchable.disabled);

		btn[2] = new ImageButton(trrd[0][2]);
		btn[2].setPosition(200,0);
		btn[2].setChecked(false);
		btn[2].setTouchable(Touchable.disabled);
		
		btn[3] = new ImageButton(trrd[0][3]);
		btn[3].setPosition(380,180);
		btn[3].setChecked(false);
		btn[3].setTouchable(Touchable.disabled);

		btn[4] = new ImageButton(trrd[0][4],trrd[0][4],trrd[1][4]);
		btn[4].setPosition(200,180);
		btn[4].setChecked(false);
		btn[4].setName("Jfalse");
		btn[4].setTouchable(Touchable.disabled);

		btn[5] = new ImageButton(trrd[1][0]);
		btn[5].setPosition(20,360);
		btn[5].setChecked(false);
		btn[5].setTouchable(Touchable.disabled);

		btn[6] = new ImageButton(trrd[1][1]);
		btn[6].setPosition(380,360);
		btn[6].setChecked(false);
		btn[6].setTouchable(Touchable.disabled);
		
		btn[7] = new ImageButton(trrd[1][2]);
		btn[7].setPosition(1900,270);
		btn[7].setChecked(false);
		btn[7].setTouchable(Touchable.disabled);
		
		btn[8] = new ImageButton(trrd[1][3]);
		btn[8].setPosition(1900,90);
		btn[8].setChecked(false);
		btn[8].setTouchable(Touchable.disabled);
		
		btn[9] = new ImageButton(trrd[1][5],trrd[1][5],trrd[0][5]);
		btn[9].setPosition(1915,220);
		btn[9].setChecked(false);
		btn[9].setTouchable(Touchable.disabled);

		btn[5].setVisible(false);
		btn[6].setVisible(false);
		btn[7].setVisible(false);
		btn[8].setVisible(false);
		
		for(int i=0; i<10; i++) {
		    stage.addActor(btn[i]);
	    }
		
		final Rectangle btnSneakReg = new Rectangle(1915,220,150,150);

				
		stage.addListener(new ClickListener() {
			@Override
			public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
				if(x<580f&&y<560f) {
					if(x>=200 && x<=380 && y>=360 && y<=540 && !btn[0].isChecked()) {
						long currentTimeMillis = System.currentTimeMillis();
						if (currentTimeMillis - lastClickTime < DOUBLE_TIME) {
							movement = Player.MOVE.RUN;
							isRun = true;
						}
						else {
							movement = Player.MOVE.FORWORD;
							isRun = false;
						}
						lastClickTime = currentTimeMillis;
						btn[5].setVisible(true);
						btn[6].setVisible(true);
						btn[0].setChecked(true);
						pointers[0] = pointer;

					}
					else if(x>=20 && x<=200 && y>=180 && y<=360 && !btn[1].isChecked()) {
						movement = Player.MOVE.LEFT;
						btn[5].setVisible(true);
						btn[1].setChecked(true);
						pointers[1] = pointer;
					}
					else if(x>=200 && x<=380 && y>=0 && y<=180 && !btn[2].isChecked()) {
						movement = Player.MOVE.BACK;
						btn[2].setChecked(true);
						pointers[2] = pointer;
					}
					else if(x>=380 && x<=560 && y>=180 && y<=360 && !btn[3].isChecked()) {
						movement = Player.MOVE.RIGHT;
						btn[6].setVisible(true);
						btn[3].setChecked(true);
						pointers[3] = pointer;
					}
					else if(x>=200 && x<=380 && y>=180 && y<=360) {
						long currentTimeMillis3 = System.currentTimeMillis();
						if (currentTimeMillis3 - lastClickTime3 < DOUBLE_TIME) {
							btn[4].toggle();
							if(btn[4].isChecked()) {
								btn[7].setVisible(true);
								btn[8].setVisible(true);
								btn[9].setVisible(false);
							}
							else {
								btn[7].setVisible(false);
								btn[8].setVisible(false);
								btn[9].setVisible(true);
							}
						}
						else if(!btn[4].isChecked()){
							movement = Player.MOVE.RISE;
						}
						lastClickTime3 = currentTimeMillis3;
						btn[4].setName("Jtrue");
						pointers[4] = pointer;
					}
					else if(x>=20 && x<=200 && y>=360 && y<=540 && !btn[5].isChecked() && btn[5].isVisible()) {
						movement = Player.MOVE.LEFTFRONT;
						btn[5].setVisible(true);
						btn[6].setVisible(true);
						btn[5].setChecked(true);
						pointers[5] = pointer;
					}
					else if(x>=380 && x<=560 && y>=360 && y<=540 && !btn[6].isChecked() && btn[6].isVisible()) {
						movement = Player.MOVE.RIGHTFRONT;
						btn[5].setVisible(true);
						btn[6].setVisible(true);
						btn[6].setChecked(true);
						pointers[6] = pointer;
					}
				
				}
				else if(x>=1900 && x<=2080 && y>=270 && y<=450 && !btn[7].isChecked() && btn[7].isVisible()) {
					movement = Player.MOVE.RISE;
					btn[7].setChecked(true);
					pointers[7] = pointer;
				}
				else if(x>=1900 && x<=2080 && y>=90 && y<=270 && !btn[8].isChecked() && btn[8].isVisible()) {
					movement = Player.MOVE.DOWN;
					btn[8].setChecked(true);
					pointers[8] = pointer;
				}
				else if(btnSneakReg.contains(x,y) && btn[9].isVisible()) {
					long currentTimeMillis2 = System.currentTimeMillis();
					if (currentTimeMillis2 - lastClickTime2 < DOUBLE_TIME) {
						movement = Player.MOVE.DOWN;
						btn[9].toggle();
					}
					else {
				
					}
					lastClickTime2 = currentTimeMillis2;
					pointers[9] = pointer;
				}
				else
					return false;
				return true;
			}
			
			@Override
			public void touchDragged(InputEvent event, float x, float y, int pointer) {
				if((x>=580f||y>=560f) && (x<1900 || x>2080 || y<90 || y>450) && !btnSneakReg.contains(x,y)) {
					stage.touchUp((int)x,Gdx.graphics.getHeight()-(int)y,pointer,0);
					mplayer.touchDown((int)x,Gdx.graphics.getHeight()-(int)y,pointer,0);
				    return;
				}
				if(x>=20 && x<=200 && y>=360 && y<=540 && !btn[5].isChecked() && btn[5].isVisible()) {
					movement = Player.MOVE.LEFTFRONT;
					btn[5].setVisible(true);
					btn[6].setVisible(true);
					btn[5].setChecked(true);
					pointers[5] = pointer;
				}
				else if(x>=380 && x<=560 && y>=360 && y<=540 && !btn[6].isChecked() && btn[6].isVisible()) {
					movement = Player.MOVE.RIGHTFRONT;
					btn[5].setVisible(true);
					btn[6].setVisible(true);
					btn[6].setChecked(true);
					pointers[6] = pointer;
				}
				else if((x<200 || x>380 || y<360 || y>540) && btn[0].isChecked() && pointer == pointers[0]){
					if(!(btn[5].isChecked() || btn[6].isChecked())) {
						btn[5].setVisible(false);
						btn[6].setVisible(false);
					}
					btn[0].setChecked(false);
				}
				else if((x<20 || x>200 || y<180 || y>360) && btn[1].isChecked() && pointer == pointers[1]) {
					if(!btn[0].isChecked() && !btn[5].isChecked()) {
					    btn[5].setVisible(false);
					}
					btn[1].setChecked(false);
				}
				else if((x<200 || x>380 || y<0 || y>180) && btn[2].isChecked() && pointer == pointers[2]) {
					btn[2].setChecked(false);
				}
				else if((x<380 || x>560 || y<180 || y>360) && btn[3].isChecked() && pointer == pointers[3]) {
					if(!btn[0].isChecked() && !btn[6].isChecked()) {
					    btn[6].setVisible(false);
					}
					btn[3].setChecked(false);
				}
				else if((x<201 || x>379 || y<181 || y>359) && btn[4].getName().equals("Jtrue") && pointer == pointers[4]) {
					btn[4].setName("Jfalse");
				}
				else if((x<20 || x>200 || y<360 || y>540) && btn[5].isChecked() && pointer == pointers[5]) {
					btn[5].setVisible(false);
					btn[6].setVisible(false);
					btn[5].setChecked(false);
				}
				else if((x<380 || x>560 || y<360 || y>540) && btn[6].isChecked() && pointer == pointers[6]) {
					btn[5].setVisible(false);
					btn[6].setVisible(false);
					btn[6].setChecked(false);
				}
				else if((x<1900 || x>2080 || y<270 || y>450) && btn[7].isChecked() && pointer == pointers[7]) {
					btn[7].setChecked(false);
				}
				else if((x<1900 || x>2080 || y<90 || y>270) && btn[8].isChecked() && pointer == pointers[8]) {
					btn[8].setChecked(false);
				}
				
				else if(x>=200 && x<=380 && y>=360 && y<=540 && !btn[0].isChecked()) {
					long currentTimeMillis = System.currentTimeMillis();
					if (currentTimeMillis - lastClickTime < DOUBLE_TIME) {
						movement = Player.MOVE.RUN;
						isRun = true;
					}
					else {
						movement = Player.MOVE.FORWORD;
						isRun = false;
					}
					lastClickTime = currentTimeMillis;
					btn[5].setVisible(true);
					btn[6].setVisible(true);
					btn[0].setChecked(true);
					pointers[0] = pointer;
				}
				else if(x>=20 && x<=200 && y>=180 && y<=360 && !btn[1].isChecked()) {
					movement = Player.MOVE.LEFT;
					btn[5].setVisible(true);
					btn[1].setChecked(true);
					pointers[1] = pointer;
				}
				else if(x>=200 && x<=380 && y>=0 && y<=180 && !btn[2].isChecked()) {
					movement = Player.MOVE.BACK;
					btn[2].setChecked(true);
					pointers[2] = pointer;
				}
				else if(x>=380 && x<=560 && y>=180 && y<=360 && !btn[3].isChecked()) {
					movement = Player.MOVE.RIGHT;
					btn[6].setVisible(true);
					btn[3].setChecked(true);
					pointers[3] = pointer;
				}
				else if(x>=201 && x<=379 && y>=181 && y<=359 && btn[4].getName().equals("Jfalse")) {
					long currentTimeMillis3 = System.currentTimeMillis();
					if (currentTimeMillis3 - lastClickTime3 < DOUBLE_TIME) {
						btn[4].toggle();
						if(btn[4].isChecked()) {
							btn[7].setVisible(true);
							btn[8].setVisible(true);
							btn[9].setVisible(false);
						}
						else {
							btn[7].setVisible(false);
							btn[8].setVisible(false);
							btn[9].setVisible(true);
						}
					}
					else if(!btn[4].isChecked()){
						movement = Player.MOVE.RISE;
					}
					lastClickTime3 = currentTimeMillis3;
					btn[4].setName("Jtrue");
					pointers[4] = pointer;
				}
				else if(x>=1900 && x<=2080 && y>=270 && y<=450 && !btn[7].isChecked() && btn[7].isVisible()) {
					movement = Player.MOVE.RISE;
					btn[7].setChecked(true);
					pointers[7] = pointer;
				}
				else if(x>=1900 && x<=2080 && y>=90 && y<=270 && !btn[8].isChecked() && btn[8].isVisible()) {
					movement = Player.MOVE.DOWN;
					btn[8].setChecked(true);
					pointers[8] = pointer;
				}
				else if(
				    !( (x>=200 && x<=380 && y>=360 && y<=540)
					|| (x>=20 && x<=200 && y>=180 && y<=360)
					|| (x>=200 && x<=380 && y>=0 && y<=180)
					|| (x>=380 && x<=560 && y>=180 && y<=360)
					|| (x>=201 && x<=379 && y>=181 && y<=359)
					|| (x>=20 && x<=200 && y>=360 && y<=540)
					|| (x>=380 && x<=560 && y>=360 && y<=540)
					|| (x>=1900 && x<=2080 && y>=270 && y<=450)
					|| (x>=1900 && x<=2080 && y>=90 && y<=270)
					)) {
					movement = Player.MOVE.STOP;
					btn[5].setVisible(false);
					btn[6].setVisible(false);
					btn[0].setChecked(false);
					btn[1].setChecked(false);
					btn[2].setChecked(false);
					btn[3].setChecked(false);
					btn[4].setName("Jfalse");
					btn[5].setChecked(false);
					btn[6].setChecked(false);
					btn[7].setChecked(false);
					btn[8].setChecked(false);
				}				
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				movement = Player.MOVE.STOP;
				if(pointer == pointers[0]) {
				    btn[0].setChecked(false);
					if(!btn[1].isChecked() && !btn[5].isChecked()) {
					    btn[5].setVisible(false);
					}
					if(!btn[3].isChecked() && !btn[6].isChecked()) {
					    btn[6].setVisible(false);
					}
				}
				if(pointer == pointers[1]) {
				    btn[1].setChecked(false);
					if(!btn[0].isChecked() && !btn[5].isChecked()) {
					    btn[5].setVisible(false);
					}
				}
				if(pointer == pointers[2]) {
				    btn[2].setChecked(false);
				}
				if(pointer == pointers[3]) {
				    btn[3].setChecked(false);
					if(!btn[0].isChecked() && !btn[6].isChecked()) {
					    btn[6].setVisible(false);
					}
				}
				if(pointer == pointers[4]) {
				    btn[4].setName("Jfalse");
				}
				if(pointer == pointers[5]) {
				    btn[5].setChecked(false);
				}
				if(pointer == pointers[6]) {
				    btn[6].setChecked(false);
				}
				if(pointer == pointers[7]) {
				    btn[7].setChecked(false);
				}
				if(pointer == pointers[8]) {
				    btn[8].setChecked(false);
				}
			}
		});
        
		assets = new AssetManager();
		assets.load("sky/skydome.g3db", Model.class);
		assets.load("data/grass.png", Texture.class);
		assets.load("data/dirt.png", Texture.class);
    }
	
	private void doneLoading() {
		grass[1] = assets.get("data/grass.png", Texture.class);
		grass[2] = assets.get("data/dirt.png", Texture.class);

		tre[1] = new TextureRegion(grass[1]);
		tre[2] = new TextureRegion(grass[2]);
		
		for(float x = 0f; x < worldEdgeLength; x+=16f) {
			for(float z = 0f; z < worldEdgeLength; z+=16f) {
				block(x, z);
				GameObject instance = new GameObject(model);
				instances.add(instance);
		    }
		}
		
		sky = new ModelInstance(assets.get("sky/skydome.g3db", Model.class));
		sky.transform.scl(1.5f);
		pmi = player();
		
		done = true;
	}

	@Override
	public void render()
	{
		if(!done && assets.update()) {
			try {
		        doneLoading();
			}
			catch(Exception e) {
				dispose();
			}
			
		}
		if(!done) return;
		cam.position.set(playercoo.x, playercoo.y+0.8f, playercoo.z);
		cam.update();
		
		mplayer.update();
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		Gdx.gl.glClearColor(186f/255f,235f/255f,252f/255f,1.0f);
		modelBatch.begin(cam);
		
		for (final GameObject instance : instances) {
			if (isVisible(cam, instance)) {
				modelBatch.render(instance, environment);
	            
		    }
			
		}
		
		if (sky != null)
			try {
			    sky.transform.setTranslation(playercoo);
				modelBatch.render(sky);
			}
			catch(Exception e) {
			}
			
		if(pmi != null) {
		    modelBatch.render(pmi);			
			
			if(btn[0].isChecked() && !isUp[0]) {
				isUp[0] = true;
				playerVel.add(0f, 0f, isRun?6f:3f);
			}
			else if(!btn[0].isChecked() && isUp[0]){
				isUp[0] = false;
				playerVel.add(0f, 0f, isRun?-6f:-3f);
				isRun = false;
			}
			
			if(btn[1].isChecked() && !isUp[1]) {
				isUp[1] = true;
				playerVel.add(3f, 0f, 0f);
			}
			else if(!btn[1].isChecked() && isUp[1]){
				isUp[1] = false;
				playerVel.add(-3f, 0f, 0f);
			}
			
			if(btn[2].isChecked() && !isUp[2]) {
				isUp[2] = true;
				playerVel.add(0f, 0f, -3f);
			}
			else if(!btn[2].isChecked() && isUp[2]){
				isUp[2] = false;
				playerVel.add(0f, 0f, 3f);
			}
			
			if(btn[3].isChecked() && !isUp[3]) {
				isUp[3] = true;
				playerVel.add(-3f, 0f, 0f);
			}
			else if(!btn[3].isChecked() && isUp[3]){
				isUp[3] = false;
				playerVel.add(3f, 0f, 0f);
			}
			
			if(btn[4].getName().equals("Jtrue") && !isUp[4]) {
				isUp[4] = true;
				playerVel.add(0f, 3f, 0f);
			}
			else if(btn[4].getName().equals("Jfalse") && isUp[4]){
				isUp[4] = false;
				playerVel.add(0f, -3f, 0f);
			}
			
			if(btn[5].isChecked() && !isUp[5]) {
				isUp[5] = true;
				playerVel.add(2.13f, 0f, 2.13f);
			}
			else if(!btn[5].isChecked() && isUp[5]){
				isUp[5] = false;
				playerVel.add(-2.13f, 0f, -2.13f);
			}
			
			if(btn[6].isChecked() && !isUp[6]) {
				isUp[6] = true;
				playerVel.add(-2.13f, 0f, 2.13f);
			}
			else if(!btn[6].isChecked() && isUp[6]){
				isUp[6] = false;
				playerVel.add(2.13f, 0f, -2.13f);
			}
			
			if(btn[7].isChecked() && !isUp[7]) {
				isUp[7] = true;
				playerVel.add(0f, 3f, 0f);
			}
			else if(!btn[7].isChecked() && isUp[7]){
				isUp[7] = false;
				playerVel.add(0f, -3f, 0f);
			}
			
			if(btn[8].isChecked() && !isUp[8]) {
				isUp[8] = true;
				playerVel.add(0f, -3f, 0f);
			}
			else if(!btn[8].isChecked() && isUp[8]){
				isUp[8] = false;
				playerVel.add(0f, 3f, 0f);
			}
						
			pmi.transform.setTranslation(playercoo.mulAdd(tmp.set(playerVel.z*mplayer.playerDirection.x+playerVel.x*mplayer.playerDirection.z, playerVel.y, playerVel.z*mplayer.playerDirection.z-playerVel.x*mplayer.playerDirection.x), Gdx.graphics.getDeltaTime()));			
		}
		modelBatch.end();
		stage.act();
		stage.draw();
	    batch.begin();
		font.setScale(3,3);
		font.draw(batch, "fps: " + Gdx.graphics.getFramesPerSecond() , 20, 50);
        font.draw(batch, "camdir: " + cam.direction.toString(), 20, 1000);
		font.draw(batch, "camup: " + cam.up.toString(), 20, 950);
		font.draw(batch, "campos: " + cam.position.toString(), 20, 900);
		font.draw(batch, "camview: H:" + cam.viewportHeight + "W: " + cam.viewportWidth + " CPU ABI: " + Build.CPU_ABI, 20, 850);
		font.draw(batch, "playerdir: " + mplayer.playerDirection.toString(), 20, 800);
		font.draw(batch, "playerpos: " + String.format("%.1f", playercoo.x)+","+String.format("%.1f", (playercoo.y-0.4f))+","+String.format("%.1f", playercoo.z)+"; velocity: "+String.format("%.1f", playerVel.len()), 20, 750);
		font.draw(batch, "playermov: " + movement + "; sneak: " + btn[9].isChecked(), 20, 700);
		batch.end();
	}

	@Override
	public void dispose()
	{
		modelBatch.dispose();
		model.dispose();
		instances.clear();
		stage.dispose();
		batch.dispose();
	}

	@Override
	public void resize(int width, int height)
	{
	}

	@Override
	public void pause()
	{
	}

	@Override
	public void resume()
	{
	}	
	
	protected boolean isVisible(final Camera cam, final GameObject instance) {
		instance.transform.getTranslation(tempPosition);
		tempPosition.add(instance.center);
		return cam.frustum.sphereInFrustum(tempPosition, instance.radius);
	}
	
	public void block(float cx, float cz) {
		int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();
		
		MeshPartBuilder box = modelBuilder.part("box", GL20.GL_TRIANGLES, attr, new Material(TextureAttribute.createDiffuse(tre[1].getTexture())));
	    
		for ( float x = 0f+cx; x < 16f+cx; x++) {
			for ( float y = 0f; y < worldHeight; y++) {
			    for ( float z = 0f+cz; z < 16f+cz; z++) {
					if(world[(int)x][(int)y][(int)z]!=0) {
						Vector3[] vertex = new Vector3[8];
						vertex[0] = new Vector3( 0.5f + x,  0.5f + y,  0.5f + z);
						vertex[1] = new Vector3( 0.5f + x,  0.5f + y, -0.5f + z);
						vertex[2] = new Vector3( 0.5f + x, -0.5f + y,  0.5f + z);
						vertex[3] = new Vector3( 0.5f + x, -0.5f + y, -0.5f + z);
						vertex[4] = new Vector3(-0.5f + x,  0.5f + y,  0.5f + z);
						vertex[5] = new Vector3(-0.5f + x,  0.5f + y, -0.5f + z);
						vertex[6] = new Vector3(-0.5f + x, -0.5f + y,  0.5f + z);
						vertex[7] = new Vector3(-0.5f + x, -0.5f + y, -0.5f + z);
						
		                if(z==0||world[(int)x][(int)y][((int)z-1)]==0) {
							box.setUVRange(0.5f,0f,1.0f,0.5f);
							box.rect(vertex[3], vertex[7], vertex[5], vertex[1], new Vector3(0f, 0f, -1f));
						}

						if(z==worldEdgeLength-1||world[(int)x][(int)y][(int)z+1]==0) {
							box.setUVRange(0.5f,0f,1.0f,0.5f);
							box.rect(vertex[6], vertex[2], vertex[0], vertex[4], new Vector3(0f, 0f, 1f));
						}
						
						if(y==0||world[(int)x][(int)y-1][(int)z]==0) {
							box.setUVRange(0f,0.5f,0.5f,1.0f);
							box.rect(vertex[7], vertex[3], vertex[2], vertex[6], new Vector3(0f, -1f, 0f));
						}
						
						if(y==worldHeight-1||world[(int)x][(int)y+1][(int)z]==0) {
							box.setUVRange(0f,0f,0.5f,0.5f);
							box.rect(vertex[4], vertex[0], vertex[1], vertex[5], new Vector3(0f, 1f, 0f));
						}

						if(x==0||world[(int)x-1][(int)y][(int)z]==0) {
							box.setUVRange(0.5f,0f,1.0f,0.5f);
							box.rect(vertex[7], vertex[6], vertex[4], vertex[5], new Vector3(-1f, 0f, 0f));
						}

						if(x==worldEdgeLength-1||world[(int)x+1][(int)y][(int)z]==0) {
							box.setUVRange(0.5f,0f,1.0f,0.5f);
							box.rect(vertex[2], vertex[3], vertex[1], vertex[0], new Vector3(1f, 0f, 0f));
						}
		            }
		        }
		    }
		}
	    model = modelBuilder.end();
	}
	
	public ModelInstance player() {
		int attr = VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates;
		ModelBuilder modelBuilder = new ModelBuilder();
		modelBuilder.begin();

		modelBuilder.part("player", GL20.GL_TRIANGLES, attr, new Material(ColorAttribute.createDiffuse(Color.BLUE)))
			.box(0.8f,1.8f,0.8f);

	    Model pmodel = modelBuilder.end();
		ModelInstance pmin = new ModelInstance(pmodel);
		pmin.transform.setToTranslation(playercoo);
		
		return pmin;
	}
	
	public static class GameObject extends ModelInstance {
		public final Vector3 center = new Vector3();
		public final Vector3 dimensions = new Vector3();
		public final float radius;
		
		private final static BoundingBox bounds = new BoundingBox();

		public GameObject(Model model) {
			super(model);
			calculateBoundingBox(bounds);
			center.set(bounds.getCenter());
			dimensions.set(bounds.getDimensions());
			radius = dimensions.len() / 2f;
		}
	}
}
