package com.thebalancegame;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import box2dLight.Light;
import box2dLight.RayHandler;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.thebalancegame.graphics.ParticleEffectCustom;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.QueryCallback;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.thebalancegame.gameobjects.GameObject;
import com.thebalancegame.gameobjects.ObjectInfo;
import com.thebalancegame.gameobjects.Piece;
import com.thebalancegame.gameobjects.Portal;
import com.thebalancegame.graphics.EffectLight;
import com.thebalancegame.graphics.Shockwave;
import com.thebalancegame.utils.ShaderUtil;

public class GameScreen implements Screen, InputProcessor, ContactListener {

	public GL20 gl = null;
	public RayHandler rayHandler;
	public static final int WORLD_WIDTH = 48;
	public static final int WORLD_HEIGHT = 32;
	public int LEVEL_WIDTH = 48;
	public int LEVEL_HEIGHT = 32;
	public int CAMERA_RIGHT_LIMIT;
	public int CAMERA_LEFT_LIMIT;
	public int CAMERA_UP_LIMIT;
	public int CAMERA_DOWN_LIMIT;
	public static final float GRAVITY_DEFAULT 			= -20.0f;
	public static final boolean IS_DESKTOP 				= true;
	public static final float FORCEMUL_DEFAULT 			= 860;
	public static final Vector2 ZERO_VECTOR				= new Vector2(0,0);
	public static final boolean ALLOW_DRAG				= false; 
	public static final float PORTAL_FORCE_OUT 			= 250;
	public static final float TICKS_PER_SECOND 			= 60;
	public static final float SKIP_TICKS 				= 1 / TICKS_PER_SECOND;
	public static float slowMotion		 				= 1;
	public static final int MAX_FRAMESKIP 				= 1;
	public static final float ACCELERATOR_FILTER		= 0.10f;
	
	public boolean drawBodies = true;
	public boolean drawJoints = false;
	public boolean drawAAAB = false;
	public float gravity;
	public Vector2 gravityVector = new Vector2();
	public float forceMul;
	public float dragForceMult = 1000.0f;
	public TheBalanceGame game;
	public Random rnd = new Random();
	
	public ArrayList<Piece> pieceTemplates = new ArrayList<Piece>();
	public ArrayList<Portal> portalIn = new ArrayList<Portal>();
	public ArrayList<Portal> portalOut = new ArrayList<Portal>();
	public ArrayList<GameObject> enemies = new ArrayList<GameObject>();
	public ArrayList<Light> lights = new ArrayList<Light>();
	public ArrayList<EffectLight> effectLights = new ArrayList<EffectLight>();
	public ArrayList<ParticleEffectCustom> particleEffects = new ArrayList<ParticleEffectCustom>();
	public Shockwave wave = new Shockwave();
	public OrthographicCamera camera;
	public Box2DDebugRenderer renderer;
	public SpriteBatch batch;
	public BitmapFont font;
    public FrameBuffer frameBuffer;
    public ShaderProgram shockwaveShader;
    public boolean shockwaveEnabled;
    public Texture screenTexture;
    public Mesh screenMesh;
	public World world;
	public Body groundBody;
	public MouseJoint mouseJoint;
	public GameObject mainBox;
	public GameObject secondaryBox;
	public GameObject mainBall;
	
	// General status variables
	public boolean stepped;
	public boolean isDragging;
	public float destroyOutOfWorldTimeCheck;
	public boolean gameover;
	public boolean winner;
	public boolean replay;
	public boolean triggerNextLevel;
	public boolean triggerPreviousLevel;
	public float timeStep;
	public float nextGameTick;
	public boolean allowOutOfWorldDestruction;
	public boolean allowPortalTransferForce;
	public int loops;
	public float interpolation;
	public float gameTime;
	public float gameTimeCheck;
	public float scaleTweening;
	public GameObject objScaleTweening;
	public boolean isScaleTweeningAndReverse;
	public float scaleTweeningFactor;
	public float scaleTweeningFactorStep;
	public float scaleTweeningTime;
	public float scaleTweeningTimeCheck;
	public boolean isScaleTweening;
	public boolean gameStarted;
	public boolean renderDebug = true;
	
	// Camera Status variables
	public float zoom = 1.0f;
	public long doubleTapWindow;
	public boolean cameraMoving;
	
	// Accelerometer
	public float lastAcceleratorX;
	public float tempAcceleratorX;
	public float lastAcceleratorY;
	public float tempAcceleratorY;
	
	// for pinch-to-zoom
	public int numberOfFingers = 0;
	public int fingerOnePointer;
	public int fingerTwoPointer;
	public float distance;
	public float factor;
	public float lastDistance = 0;
	public Vector3 fingerOne = new Vector3();
	public Vector3 fingerTwo = new Vector3();
	
	
	// Temp variables
	public Fixture tempFixture = null;
	public Vector2 forceDirection = new Vector2();
	public Piece newPiece;
	public Piece newPiece2;
	public Body hitBody = null;
	public BodyDef def = new BodyDef();
	public FixtureDef fd = new FixtureDef();
	public Body logicHitBody = null;
	public Body tempBody = null;
	public Body tempBody2 = null;
	public Vector2 tmp = new Vector2();
	public Vector2 target = new Vector2();
	public Vector3 testPoint = new Vector3();
	public Vector3 renderPos = new Vector3();
	public Vector3 renderSize = new Vector3();
	public Vector2 testPoint2D = new Vector2();
	public Iterator<Body> bodyIterator = null;
	public int tempInt;
	public float tempFloat;
	public float tempFloat2;
	public Vector2 contactListenerVector = new Vector2();
	public Light tempLight;
	public ParticleEffectCustom particleEffect;
	
	
	public GameScreen(TheBalanceGame g) {
    	this(g, GRAVITY_DEFAULT, FORCEMUL_DEFAULT);
    }
	
	public GameScreen(TheBalanceGame g, float gravity, float forceMul) {
    	this(g, gravity, forceMul, 1000.0f, true, false, false);
    }
	
	public GameScreen(TheBalanceGame g, float gravity, float forceMul, float dragForceMult, boolean drawBodies, boolean drawJoints, boolean drawAAAB) {
    	game = g;
    	this.gravity = gravity;
    	gravityVector.set(0, gravity);
    	this.forceMul = forceMul;
    	this.dragForceMult = dragForceMult;
    	this.drawBodies = drawBodies;
    	this.drawJoints = drawJoints;
    	this.drawAAAB = drawAAAB;
    }
	
	public void setCameraLimits() {
		if (LEVEL_WIDTH > WORLD_WIDTH/2) {
			CAMERA_RIGHT_LIMIT = LEVEL_WIDTH/3;
			CAMERA_LEFT_LIMIT = -LEVEL_WIDTH/3;
		}
		else {
			CAMERA_RIGHT_LIMIT = 0;
			CAMERA_LEFT_LIMIT = 0;
		}
    	CAMERA_UP_LIMIT = LEVEL_HEIGHT/2 - 1;
    	CAMERA_DOWN_LIMIT = LEVEL_HEIGHT/2 - 1;
	}

	public void createWorld (World world) {
		// This should be overriden by levels
	}
	
	public void createNewLight(Light light) {
		tempLight = light;
		lights.add(tempLight);
	}
	
	public void createPortalIn(float x, float y, int bodyAngle, int portalAngle) {
		createBodyAndFixture(getNewPieceInstanceFromTemplate(0).setSensor(true).setPortalIn(true).setAngle(bodyAngle), x, y);
		portalIn.add(new Portal(tempFixture, portalAngle, PORTAL_FORCE_OUT));
	}
	
	public void createPortalOut(float x, float y, int bodyAngle, int portalAngle) {
		createBodyAndFixture(getNewPieceInstanceFromTemplate(1).setSensor(true).setPortalOut(true).setAngle(bodyAngle), x, y);
		portalOut.add(new Portal(tempFixture, portalAngle, PORTAL_FORCE_OUT));
	}
	
	public void createSimpleEnemy(float x, float y, float minVelocity, float maxVelocity) {
		createSimpleEnemy(x, y, minVelocity, maxVelocity, false);
	}
	
	public void createSimpleEnemy(float x, float y, float minVelocity, float maxVelocity, boolean isPortalAllowed) {
		maxVelocity -= minVelocity;
		newPiece = getNewPieceInstanceFromTemplate(5);
		newPiece.setPhysics(0, 1, 0, false);
		newPiece.isPortalAllowed = isPortalAllowed;
		createBodyAndFixture(newPiece, x, y);
		tempFloat = rnd.nextBoolean() ? 1 : -1;
		newPiece.body.setLinearVelocity(tempFloat * (minVelocity + rnd.nextFloat() * maxVelocity * 0.8f), 5 + rnd.nextFloat() * maxVelocity);
		enemies.add(new GameObject(newPiece, newPiece.body));
		enemies.get(enemies.size()-1).genericFloat = minVelocity;
		enemies.get(enemies.size()-1).genericFloat2 = maxVelocity;
		enemies.get(enemies.size()-1).genericFloat3 = (float)Math.abs(newPiece.body.getLinearVelocity().x);
	}
	
	public Piece createSimplePiece(float x, float y, int index, float friction, float restitution, float gravityScale, boolean sensor) {
		newPiece = getNewPieceInstanceFromTemplate(index);
		newPiece.setPhysics(friction, restitution, gravityScale, sensor);
		createBodyAndFixture(newPiece, x, y);
		return newPiece;
	}
	
	public void createSimplePiece(float x, float y, int index) {
		createBodyAndFixture(getNewPieceInstanceFromTemplate(index), x, y);
	}
	
	public Piece createCompositePiece(float x, float y, float friction, float restitution, float gravityScale, boolean sensor, ArrayList<Shape> shapeList) {
		newPiece = getNewPieceInstanceFromTemplate(9);
		newPiece.setPhysics(friction, restitution, gravityScale, sensor);
		for (Shape s : shapeList) {
			newPiece.addShape(s);
		}
		createBodyAndFixture(newPiece, x, y);
		return newPiece;
	}
	
	public void createMainBall(float x, float y, float gravityScale) {
		newPiece = getNewPieceInstanceFromTemplate(4);
		newPiece.setPhysics(0.0f, 0.2f, gravityScale, false);
		newPiece.isBullet = true;
		createBodyAndFixture(newPiece, x, y);
		mainBall = new GameObject(newPiece, newPiece.body);
	}
	
	public void createMainBox(float x, float y) {
		newPiece = getNewPieceInstanceFromTemplate(6);
		newPiece.gravityScale = 0;
		newPiece.density = 1000.0f;
		createBodyAndFixture(newPiece, x, y);
		mainBox = new GameObject(newPiece, newPiece.body);
		mainBox.body.setTransform(mainBox.body.getPosition(), (float)Math.toRadians(-10));
	}
	
	public void createSecondaryBox(float x, float y) {
		newPiece = getNewPieceInstanceFromTemplate(7);
		newPiece.setPhysics(0, 1, 0, false);
		newPiece.density = 1000.0f;
		createBodyAndFixture(newPiece, x, y);
		secondaryBox = new GameObject(newPiece, newPiece.body);
	}
	
	public Body createBodyAndFixture(Piece piece, float x, float y) {
		piece.pos.x = x;
		piece.pos.y = y;
		def.position.x = x;
		def.position.y = y;
		def.type = piece.type;
		def.angle = piece.angle;
		def.gravityScale = piece.gravityScale;
		def.bullet = piece.isBullet;
		Body body = world.createBody(def);
		if (body.getType() == BodyType.StaticBody) {
			if (piece.shapes != null) {
				for (Shape shape : piece.shapes) {
					fd.shape = shape;
					tempFixture = body.createFixture(fd);
				}
			}
			else {
				fd.shape = piece.shape;
				tempFixture = body.createFixture(fd);
			}
		}
		else {
			if (piece.shapes != null) {
				for (Shape shape : piece.shapes) {
					tempFixture = body.createFixture(shape, piece.density);
					tempFixture.setFriction(piece.friction);
					tempFixture.setRestitution(piece.restitution);
				}
			}
			else {
				tempFixture = body.createFixture(piece.shape, piece.density);
				tempFixture.setFriction(piece.friction);
				tempFixture.setRestitution(piece.restitution);
			}
		}
		tempFixture.setSensor(piece.isSensor);
		piece.setBody(body);
		// introduce ObjectInfo as UserData
		if (body.getType() == BodyType.DynamicBody || body.getType() == BodyType.KinematicBody) {
			tempFixture.getBody().setUserData(new ObjectInfo(piece));
			if (piece.isPortalAllowed) {
				((ObjectInfo)tempFixture.getBody().getUserData()).isPortalAllowed = true;
			}
			if (piece.isMainChar) {
				((ObjectInfo)tempFixture.getBody().getUserData()).isMainChar = true;
			}
		}
		return body;
	}

	/** Create and save body templates **/
	public void setupPieces() {
		// Reallocate arrays
		pieceTemplates = new ArrayList<Piece>(60);
		portalIn = new ArrayList<Portal>(20);
		portalOut = new ArrayList<Portal>(20);
		/** Portal In vertical **/
		addNewPieceTemplate((new Piece(0.5f, 1.5f, 0, BodyType.StaticBody)).setSensor(true).setPortalIn(true)); // 0
		/** Portal Out vertical **/
		addNewPieceTemplate((new Piece(0.5f, 1.5f, 0, BodyType.StaticBody)).setSensor(true).setPortalOut(true)); // 1
		/** Portal In horizontal **/
		addNewPieceTemplate((new Piece(1.5f, 0.5f, 0, BodyType.StaticBody)).setSensor(true).setPortalIn(true)); // 2
		/** Portal Out horizontal **/
		addNewPieceTemplate((new Piece(1.5f, 0.5f, 0, BodyType.StaticBody)).setSensor(true).setPortalOut(true)); // 3
		/** Circle 1.0 DynamicBody **/
		addNewPieceTemplate(new Piece(1, BodyType.DynamicBody)); // 4
		/** Circle 0.2 DynamicBody **/
		addNewPieceTemplate(new Piece(0.5f, BodyType.DynamicBody)); // 5
		/** Large MAIN box **/
		addNewPieceTemplate(new Piece(15, 0.5f, 0, BodyType.KinematicBody)); // 6
		/** SECONDARY mini box **/
		addNewPieceTemplate(new Piece(3, 0.2f, 0, BodyType.KinematicBody)); // 7
		/** Mini Box **/
		addNewPieceTemplate(new Piece(0.5f, 0.5f, 0, BodyType.DynamicBody)); // 8
		/** Basket Piece **/
		addNewPieceTemplate(new Piece(5, BodyType.KinematicBody, true)); // 9
		/** Large Box **/
		addNewPieceTemplate(new Piece(1.2f, 1.2f, 0, BodyType.DynamicBody)); // 10
		/** Large Ball **/
		addNewPieceTemplate(new Piece(2, BodyType.DynamicBody)); // 11
		/** Arrow **/
		addNewPieceTemplate(new Piece(2f, 0.2f, 0, BodyType.KinematicBody)); // 12
		/** Ultra Large Box **/
		addNewPieceTemplate(new Piece(1.5f, 1.5f, 0, BodyType.DynamicBody)); // 13
		/** Little Box **/
		addNewPieceTemplate(new Piece(1.5f, 1.0f, 0, BodyType.KinematicBody)); // 14
		/** Large Planet **/
		addNewPieceTemplate(new Piece(30, BodyType.KinematicBody)); // 15
		/** Little Planet **/
		addNewPieceTemplate(new Piece(5, BodyType.KinematicBody)); // 16
	}
	
	public Piece getNewPieceInstanceFromTemplate(int templateIndex) {
		return (new Piece(pieceTemplates.get(templateIndex))).setIndex(templateIndex);
	}

	@Override
	public void show() {
    	this.allowOutOfWorldDestruction = true;
    	this.allowPortalTransferForce = true;
    	this.renderDebug = true;
		// Setup all the game elements once
		setupPieces();
		camera = new OrthographicCamera(WORLD_WIDTH, WORLD_HEIGHT);
		camera.position.set(0, (WORLD_HEIGHT/2)-1, 0);
		setCameraLimits();

		renderer = new Box2DDebugRenderer(drawBodies, drawJoints, drawAAAB, true);
		if (rayHandler == null && handleLightsRendering()) {
			RayHandler.setColorPrecisionMediump();
			rayHandler = new RayHandler(world);
			rayHandler.setBlur(false);
			rayHandler.setShadows(false);
			rayHandler.setCulling(true);
		}
		world = new World(new Vector2(0, gravity), true);
		world.setContactListener(this);
		BodyDef bodyDef = new BodyDef();
		groundBody = world.createBody(bodyDef);
		createWorld(world);
		gameStarted = true;

		batch = new SpriteBatch(1000);
		font = new BitmapFont();
		
		screenMesh = ShaderUtil.createFullScreenQuad();
		frameBuffer = new FrameBuffer(Format.RGB565, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
		shockwaveShader = new ShaderProgram(Gdx.files.internal("data/shaders/shockwave.vert").readString(), 
				Gdx.files.internal("data/shaders/shockwave.frag").readString());
		if (!shockwaveShader.isCompiled()) {
			Gdx.app.log("ShaderTest", "couldn't compile post shader: " + shockwaveShader.getLog());
		}
		
		Gdx.input.setInputProcessor(this);
		nextGameTick = 0;
		timeStep = 0;
		gameTime = 0;
		gameTimeCheck = 0;
    	this.isScaleTweening = false;
    	this.isScaleTweeningAndReverse = false;
	}

	@Override
	public void render(float deltaTime) {
		//long startTime = System.nanoTime();
		stepped = false;
		if (!gameover && !winner) {
			gameTime += deltaTime;
			gameTimeCheck += deltaTime;
		}
		else
			effectLights.clear();
		timeStep += deltaTime;
		loops = 0;
        while(timeStep > nextGameTick && loops < MAX_FRAMESKIP) {
			performLogic(SKIP_TICKS*slowMotion);
	    	world.step(SKIP_TICKS*slowMotion, 3, 3);
	    	updateParticles(particleEffects, SKIP_TICKS);
			stepped = true;
			
			if (cameraMoving) {
				handleCameraMovement(SKIP_TICKS*slowMotion);
			}
			if (effectLights.size() > 0) {
				handleLightEffects(SKIP_TICKS*slowMotion);
			}
			if (shockwaveEnabled)
			    wave.update(SKIP_TICKS);
			
            nextGameTick += SKIP_TICKS;
            loops++;
        }
		if (wave != null && wave.started && !wave.completed && stepped) {
			shockwaveEnabled = true;
			slowMotion = 0.5f;
			if (wave.shockwaveTime >= 0.8f)
				slowMotion += (wave.shockwaveTime/3);
			frameBuffer.begin();
		}
		//interpolation = (timeStep + SKIP_TICKS - nextGameTick) / SKIP_TICKS;
        
		//float updateTime = (System.nanoTime() - startTime) / 1000000000.0f;
		//startTime = System.nanoTime();
		if (gl == null)
			gl = Gdx.app.getGraphics().getGL20();
		
		gl.glClearColor(0, 0, 0, 1);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		camera.zoom = zoom;
		camera.update();
		//camera.apply(gl);

		//float renderTime = (System.nanoTime() - startTime) / 1000000000.0f;

		batch.setProjectionMatrix(camera.combined);
		batch.begin();
		handleLevelRendering(deltaTime);
		renderParticles(particleEffects);
		batch.end();

		if (shockwaveEnabled) {
			frameBuffer.end();
		    screenTexture = frameBuffer.getColorBufferTexture();
		    screenTexture.bind(0);
		    shockwaveShader.begin();
		    shockwaveShader.setUniformi("sceneTex", 0);
		    shockwaveShader.setUniformf("center", wave.shockwaveCenter.x, wave.shockwaveCenter.y);
		    shockwaveShader.setUniformf("time", wave.shockwaveTime);
		    shockwaveShader.setUniformf("shockParams", wave.shockwaveParams.x, wave.shockwaveParams.y, wave.shockwaveParams.z);
		    screenMesh.render(shockwaveShader, GL10.GL_TRIANGLE_FAN);
		    shockwaveShader.end();
			if (wave.completed) {
				shockwaveEnabled = false;
				slowMotion = 1f;
			}
		}
		
		/** FONTS **/
		batch.getProjectionMatrix().setToOrtho2D(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.begin();
		font.draw(batch,  "fps:" + Gdx.graphics.getFramesPerSecond(), 0, 15);
		/*font.draw(batch,  "ENTER -> restart", 670, 30);
		font.draw(batch,  "SPACE -> next", 670, 15);*/
		font.draw(batch,  "(" + (int)testPoint.x + "," + (int)testPoint.y + ")", 10, Gdx.graphics.getHeight());
		//font.drawMultiLine(batch, "accelX: " + Gdx.input.getAccelerometerX() + "\n" + "accelY: " + Gdx.input.getAccelerometerY() + "\n", 0, 50);
		font.draw(batch,  "REPLAY", Gdx.graphics.getWidth() - 60, Gdx.graphics.getHeight());
		font.draw(batch,  "NEXT", Gdx.graphics.getWidth() - 110, Gdx.graphics.getHeight());
		font.draw(batch,  "PREV", Gdx.graphics.getWidth() - 160, Gdx.graphics.getHeight());
		font.draw(batch,  "" + gameTime, Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight());
		if (winner) {
			font.draw(batch,  "YOU WIN!", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		}
		else if (gameover) {
			font.draw(batch,  "YOU LOSE!", Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight()/2);
		}
		displayLevelUI();
		batch.end();
		
		if (renderDebug)
			renderer.render(world, camera.combined);

		if (lights.size() > 0) {
			if (stepped)
				rayHandler.update();
			rayHandler.setCombinedMatrix(camera.combined);
			rayHandler.render();
		}
	}
	
	public void displayLevelUI() {
		// This should be overriden by levels
	}
	
	public void updateCameraPosition(float x, float y) {
		// limit the view on the level angles
		if (x > CAMERA_RIGHT_LIMIT)
			x = CAMERA_RIGHT_LIMIT;
		else if (x < CAMERA_LEFT_LIMIT)
			x = CAMERA_LEFT_LIMIT;
		if (y > CAMERA_UP_LIMIT)
			y = CAMERA_UP_LIMIT;
		else if (y < CAMERA_DOWN_LIMIT)
			y = CAMERA_DOWN_LIMIT;
		camera.position.set(x, y, 0);
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// Change Zoom
		if (keycode == Input.Keys.PLUS)
			zoom -= 0.2f;
		else if (keycode == Input.Keys.MINUS)
			zoom += 0.2f;
		
		else if (keycode == Input.Keys.ENTER)
			replay = true;
		
		else if (keycode == Input.Keys.SPACE)
			triggerNextLevel = true;
		
		// Exit
		else if (keycode == Input.Keys.ESCAPE)
			game.exit();
		
		
		// Shockwave
		else if (keycode == Input.Keys.S) {
			wave.setPosition(testPoint2D, camera);
			wave.start();
		}
		
		else if (keycode == Input.Keys.D) {
			if (slowMotion == 1)
				slowMotion = 0.5f;
			else
				slowMotion = 1;
		}
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public QueryCallback callback = new QueryCallback() {
		@Override
		public boolean reportFixture (Fixture fixture) {
			if (fixture.testPoint(testPoint.x, testPoint.y)) {
				hitBody = fixture.getBody();
				return false;
			} else
				return true;
		}
	};

	@Override
	public boolean touchDown(int x, int y, int pointer, int button) {
		// Replay button
		if (x > (Gdx.graphics.getWidth() - 60) && y < 20) {
			replay = true;
			return false;
		}
		// Next button
		if (x > (Gdx.graphics.getWidth() - 110) && x < (Gdx.graphics.getWidth() - 70) && y < 20)
			triggerNextLevel = true;
		// Previous button
		if (x > (Gdx.graphics.getWidth() - 170) && x < (Gdx.graphics.getWidth() - 120) && y < 20)
			triggerPreviousLevel = true;
		// for pinch-to-zoom
		numberOfFingers++;
		if (!IS_DESKTOP) {
			if (numberOfFingers == 1) {
				fingerOnePointer = pointer;
				fingerOne.set(x, y, 0);
			}
			else if (numberOfFingers == 2) {
				fingerTwoPointer = pointer;
				fingerTwo.set(x, y, 0);
				lastDistance = fingerOne.dst2(fingerTwo);
				return false;
			}
		}
		// translate the mouse coordinates to world coordinates
		camera.unproject(testPoint.set(x, y, 0));
		testPoint2D.x = testPoint.x;
		testPoint2D.y = testPoint.y;
		// Drag Mode
		if (ALLOW_DRAG) {
			hitBody = null;
			world.QueryAABB(callback, testPoint.x - 0.0001f, testPoint.y - 0.0001f, testPoint.x + 0.0001f, testPoint.y + 0.0001f);
			if (hitBody == groundBody) hitBody = null;
			// ignore kinematic bodies, they don't work with the mouse joint
			if (hitBody != null && (hitBody.getType() == BodyType.KinematicBody || hitBody.getType() == BodyType.StaticBody)) return false;
			if (hitBody != null) {
				MouseJointDef def = new MouseJointDef();
				def.bodyA = groundBody;
				def.bodyB = hitBody;
				def.collideConnected = true;
				def.target.set(testPoint.x, testPoint.y);
				def.maxForce = dragForceMult * hitBody.getMass();
	
				mouseJoint = (MouseJoint)world.createJoint(def);
				hitBody.setAwake(true);
				isDragging = true;
			}
		}
		/*if (!isDragging) {
			if ((System.currentTimeMillis() - doubleTapWindow) < 150) {
				cameraMoving = true;
			}
		}*/
		

		/*wave.setPosition(testPoint2D, camera);
		wave.start();*/

		if (gameStarted)
			handleTouchInput(0, testPoint2D.x, testPoint2D.y);
		return false;
	}
	
	@Override
	public boolean touchDragged(int x, int y, int pointer) {
		// for pinch-to-zoom
		if (!IS_DESKTOP) {
			if (numberOfFingers == 2) {
				if (pointer == fingerOnePointer)
				       fingerOne.set(x, y, 0);
				if (pointer == fingerTwoPointer)
				       fingerTwo.set(x, y, 0);
				distance = fingerOne.dst2(fingerTwo);
				//factor = distance / lastDistance / 4;
				if (lastDistance > distance)
					zoom += 0.01f;
				else if (lastDistance < distance)
					zoom -= 0.01f;
				lastDistance = distance;
				return false;
			}
		}
		// if a mouse joint exists we simply update
		// the target of the joint based on the new
		// mouse coordinates
		camera.unproject(testPoint.set(x, y, 0));
		testPoint2D.x = testPoint.x;
		testPoint2D.y = testPoint.y;
		if (mouseJoint != null) {
			mouseJoint.setTarget(target.set(testPoint.x, testPoint.y));
		}
		
		if (gameStarted)
			handleTouchInput(1, testPoint2D.x, testPoint2D.y);
		
		return false;
	}

	@Override
	public boolean touchUp(int x, int y, int pointer, int button) {
		camera.unproject(testPoint.set(x, y, 0));
		testPoint2D.x = testPoint.x;
		testPoint2D.y = testPoint.y;
		// if a mouse joint exists we simply destroy it
		if (mouseJoint != null) {
			world.destroyJoint(mouseJoint);
			mouseJoint = null;
		}
		hitBody = null;
		
		// for pinch-to-zoom     
		 numberOfFingers--;
		// just some error prevention... clamping number of fingers (ouch! :-)
		 if(numberOfFingers<0)
		        numberOfFingers = 0;
		lastDistance = 0;
		isDragging = false;
		//cameraMoving = false;
		doubleTapWindow = System.currentTimeMillis();
		
		if (gameStarted)
			handleTouchInput(2, testPoint2D.x, testPoint2D.y);
		return false;
	}

	@Override
	public boolean touchMoved(int x, int y) {
		camera.unproject(testPoint.set(x, y, 0));
		testPoint2D.x = testPoint.x;
		testPoint2D.y = testPoint.y;
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void dispose() {
		lights.clear();
		effectLights.clear();
		if (rayHandler != null) {
			rayHandler.removeAll();
			//rayHandler.dispose();
		}
		renderer.dispose();
		//world.dispose();
		pieceTemplates.clear();
		portalIn.clear();
		portalOut.clear();
		enemies.clear();
		//rayHandler = null;
		disposeParticles(particleEffects);
		shockwaveShader.dispose();
		handleLevelDispose();
	}

	@Override
	public void pause() {
		// some error prevention...
		 numberOfFingers = 0;
	}

	@Override
	public void hide() {
		dispose();
	}
	
	public Piece addNewPieceTemplate(Piece piece) {
		pieceTemplates.add(piece);
		return piece;
	}

	public void createMouseJoint() {
		MouseJointDef def = new MouseJointDef();
		def.bodyA = groundBody;
		def.bodyB = hitBody;
		def.collideConnected = true;
		def.target.set(testPoint.x, testPoint.y);
		def.maxForce = dragForceMult * hitBody.getMass();
		
		mouseJoint = (MouseJoint)world.createJoint(def);
		hitBody.setAwake(true);
	}
	
	public QueryCallback portalInCallback = new QueryCallback() {
		@Override
		public boolean reportFixture (Fixture fixture) {
			if (fixture.getBody().getType() != BodyType.StaticBody && !fixture.isSensor() && ((ObjectInfo)fixture.getBody().getUserData()).isPortalAllowed) {
				// Prevent portal looping
				if (!((ObjectInfo)fixture.getBody().getUserData()).hasTimePassed(300))
					return true;
				for (int i=0; i<portalIn.size(); i++) {
					if (portalIn.get(i).fixture.testPoint(fixture.getBody().getPosition().x, fixture.getBody().getPosition().y)) {
						logicHitBody = fixture.getBody();
						if (logicHitBody != null) {
							logicHitBody.setTransform(portalOut.get(i).getBody().getPosition(), 0);
							if (portalOut.get(i).normal != null) {
								// New velocity angle
								//System.out.println("vel: "+logicHitBody.getLinearVelocity().angle()+" norm: " + portalOut.get(i).normal.angle()+" angle: " + portalOut.get(i).angle);
								logicHitBody.setLinearVelocity(logicHitBody.getLinearVelocity().rotate(portalOut.get(i).angle - logicHitBody.getLinearVelocity().angle()));
								// Apply a little more linear force
								if (allowPortalTransferForce)
									logicHitBody.applyForceToCenter(portalOut.get(i).transferForce);
							}
							if (fixture.getBody().getUserData() != null)
								((ObjectInfo)fixture.getBody().getUserData()).updateTime();
							handlePortalCallbackRendering(portalIn.get(i).getBody().getPosition(), portalOut.get(i).getBody().getPosition());
						}
					}
				}
			}
			return true;
		}
	};
	
	
	public QueryCallback portalOutCallback = new QueryCallback() {
		@Override
		public boolean reportFixture (Fixture fixture) {
			if (fixture.getBody().getType() != BodyType.StaticBody && !fixture.isSensor() && ((ObjectInfo)fixture.getBody().getUserData()).isPortalAllowed) {
				// Prevent portal looping
				if (!((ObjectInfo)fixture.getBody().getUserData()).hasTimePassed(300))
					return true;
				for (int i=0; i<portalIn.size(); i++) {
					if (portalOut.get(i).fixture.testPoint(fixture.getBody().getPosition().x, fixture.getBody().getPosition().y)) {
						logicHitBody = fixture.getBody();
						if (logicHitBody != null) {
							logicHitBody.setTransform(portalIn.get(i).getBody().getPosition(), 0);
							if (portalIn.get(i).normal != null) {
								// New velocity angle
								logicHitBody.setLinearVelocity(logicHitBody.getLinearVelocity().rotate(portalIn.get(i).normal.angle() - logicHitBody.getLinearVelocity().angle()));
								// Apply a little more linear force
								if (allowPortalTransferForce)
									logicHitBody.applyForceToCenter(portalIn.get(i).transferForce);
							}
							if (fixture.getBody().getUserData() != null)
								((ObjectInfo)fixture.getBody().getUserData()).updateTime();
							handlePortalCallbackRendering(portalOut.get(i).getBody().getPosition(), portalIn.get(i).getBody().getPosition());
						}
					}
				}
			}
			return true;
		}
	};
	
	public void performLogic(float deltaTime) {
		/** BALANCE INPUTS **/
		if (Gdx.input.getAccelerometerX() != 0 || Gdx.input.getAccelerometerY() != 0)
			handleBalanceInput(Gdx.input.getAccelerometerX(), Gdx.input.getAccelerometerY(), true);
		else
			handleBalanceInput(testPoint2D.x, testPoint2D.y, false);
		/** GAME STATUSES **/
		if (replay) {
			replay = false;
			winner = false;
			gameover = false;
			triggerNextLevel = false;
			triggerPreviousLevel = false;
			gameTime = 0;
			gameTimeCheck = 0;
			gameStarted = false;
			game.resetScreen();
		}
		else if (triggerNextLevel) {
			triggerNextLevel = false;
			replay = false;
			winner = false;
			gameover = false;
			gameTime = 0;
			gameTimeCheck = 0;
			gameStarted = false;
			game.nextLevel();
		}
		else if (triggerPreviousLevel) {
			triggerNextLevel = false;
			triggerPreviousLevel = false;
			replay = false;
			winner = false;
			gameover = false;
			gameTime = 0;
			gameTimeCheck = 0;
			gameStarted = false;
			game.prevLevel();
		}
		if (winner || gameover) {
			return;
		}
		
		/** GAME MECHANICS**/
		if (gameStarted) {
			handleGameMechanics(deltaTime);
			if (isScaleTweening)
				updateScaleTweening(deltaTime);
		}
		
		/** PORTALS **/
		logicHitBody = null;
		if (portalIn.size() > 0 && portalOut.size() > 0 && portalIn.size() == portalOut.size()) {
			for (int i=0; i<portalIn.size(); i++) {
				world.QueryAABB(portalInCallback, portalIn.get(i).getX() - 0.5f, portalIn.get(i).getY() - 1.5f, portalIn.get(i).getX() + 0.5f, portalIn.get(i).getY() + 1.5f);
				world.QueryAABB(portalOutCallback, portalOut.get(i).getX() - 0.5f, portalOut.get(i).getY() - 1.5f, portalOut.get(i).getX() + 0.5f, portalOut.get(i).getY() + 1.5f);
			}
		}
		
		/** PHYSICS **/
		if (allowOutOfWorldDestruction) {
			destroyOutOfWorldTimeCheck += deltaTime;
			if (destroyOutOfWorldTimeCheck >= 1) {
				destroyOutOfWorldTimeCheck = 0;
				bodyIterator = world.getBodies();
				while (bodyIterator.hasNext()) {
					logicHitBody = bodyIterator.next();
					// DESTROY OUT OF WORLD OBJECT
					if ( logicHitBody != null && ((logicHitBody.getPosition().x > LEVEL_HEIGHT + 20) || (logicHitBody.getPosition().x < -LEVEL_HEIGHT -20)
							|| (logicHitBody.getPosition().y > LEVEL_HEIGHT*2 + 20) || (logicHitBody.getPosition().y < -20))) {
							handleBodyDestroyed(logicHitBody);
							world.destroyBody(logicHitBody);
					}
				}
			}
		}
	}
	
	public void startScaleTweeningAndReverse(GameObject obj, float tweenFactor, float time, float deltaTime) {
		isScaleTweening = true;
		isScaleTweeningAndReverse = true;
		objScaleTweening = obj;
		scaleTweeningTime = time;
		scaleTweeningTimeCheck = 0;
		scaleTweeningFactor = tweenFactor;
		scaleTweeningFactorStep = tweenFactor / (time / deltaTime);
	}
	
	public void startScaleTweening(Body body, float tweenFactor, float time, float deltaTime) {
		isScaleTweening = true;
		isScaleTweeningAndReverse = false;
		objScaleTweening.body = body;
		scaleTweeningTime = time;
		scaleTweeningTimeCheck = 0;
		scaleTweeningFactor = tweenFactor;
		scaleTweeningFactorStep = tweenFactor / (time / deltaTime);
	}
	
	public void updateScaleTweening(float deltaTime) {
		if (objScaleTweening == null || objScaleTweening.body == null || objScaleTweening.body.getFixtureList().size() == 0) {
			isScaleTweening = false;
			isScaleTweeningAndReverse = false;
			return;
		}
		scaleTweeningTimeCheck += deltaTime;
		if (isScaleTweeningAndReverse) {
			if (scaleTweeningTimeCheck >= scaleTweeningTime) {
				if (scaleTweeningTimeCheck >= scaleTweeningTime + 0.5f) {
					isScaleTweening = false;
					isScaleTweeningAndReverse = false;
					startScaleTweening(objScaleTweening.body, -scaleTweeningFactor, 0.2f, deltaTime);
				}
				return;
			}
		}
		else if (scaleTweeningTimeCheck >= scaleTweeningTime) {
			isScaleTweening = false;
			return;
		}
		objScaleTweening.body.setAwake(true);
		objScaleTweening.body.getFixtureList().get(0).getShape().setRadius(objScaleTweening.body.getFixtureList().get(0).getShape().getRadius() 
				- (objScaleTweening.body.getFixtureList().get(0).getShape().getRadius() * scaleTweeningFactorStep));
		if (objScaleTweening.attachedLight != null) {
			objScaleTweening.attachedLight.setDistance(objScaleTweening.attachedLight.getDistance() - (objScaleTweening.attachedLight.getDistance() * scaleTweeningFactorStep));
		}
	}
	
	public void handleBodyDestroyed(Body body) {
		// This should be overriden by levels
	}
	
	public void handleTouchInput(int type, float x, float y) {
		// This should be overriden by levels
	}
	
	public void handleBalanceInput(float x, float y, boolean isAccel) {
		// This should be overriden by levels
	}
	
	public void handleGameMechanics(float deltaTime) {
		// This should be overriden by levels
	}
	
	public void handleContactFilter(Body bodyA, Body bodyB) {
		// This should be overriden by levels
	}
	
	public void handleCameraMovement(float deltaTime) {
		// This should be overriden by levels
		/*tmp.set(testPoint2D);
		tmp.sub(camera.position.x, camera.position.y);
		tmp.mul(2*deltaTime);
		updateCameraPosition(camera.position.x + tmp.x, camera.position.y + tmp.y);*/
	}
	
	public void handleLevelRendering(float deltaTime) {
		// This should be overriden by levels
	}
	
	public boolean handleLightsRendering() {
		// This should be overriden by levels
		return true;
	}
	
	public void handleLevelDispose() {
		// This should be overriden by levels
	}
	
	public void handlePortalCallbackRendering(Vector2 startPos, Vector2 endPos) {
		// This should be overriden by levels
	}
	
	private void handleLightEffects(float deltaTime) {
		boolean temp = false;
		for (EffectLight effectLight : effectLights) {
			if (effectLight.tween == 0)
				continue;			
			else if (effectLight.tween > 0) {
				if (effectLight.progress < (effectLight.original + effectLight.tween)) {
					effectLight.progress += effectLight.tween * deltaTime * effectLight.speed;
					effectLight.light.setDistance(effectLight.progress);
				}
				else
					effectLight.tween *= -1;
				temp = true;
			}
			else if ( effectLight.progress > effectLight.original) {
				effectLight.progress += effectLight.tween * deltaTime * effectLight.speed;
				effectLight.light.setDistance(effectLight.progress);
				temp = true;
			}
			else {
				effectLight.tween = 0;
				effectLight.light.setDistance(effectLight.original);
			}
		}
		if (!temp) {
			effectLights.clear();
		}
	}
	
	public void updateParticles(ArrayList<ParticleEffectCustom> list, float deltaTime) {
		for (ParticleEffectCustom eff : list) {
			if (eff.isStarted && !eff.isComplete())
				eff.update(deltaTime);
		}
	}
	
	public void renderParticles(ArrayList<ParticleEffectCustom> list) {
		for (ParticleEffectCustom eff : list) {
			if (eff.isStarted && !eff.isComplete())
				eff.draw(batch);
		}
	}
	
	public void disposeParticles(ArrayList<ParticleEffectCustom> list) {
		for (ParticleEffectCustom eff : list) {
			eff.dispose();
		}
		list.clear();
	}

	@Override
	public void beginContact(Contact contact) {}
	@Override
	public void endContact(Contact contact) {}
	@Override
	public void preSolve(Contact contact, Manifold oldManifold) {}
	@Override
	public void postSolve(Contact contact, ContactImpulse impulse) {
		if (!gameover && !winner)
			handleContactFilter(contact.getFixtureA().getBody(), contact.getFixtureB().getBody());
	}
}
