package com.thebalancegame.levels.arcademode;

import java.util.ArrayList;

import box2dLight.DirectionalLight;
import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.thebalancegame.graphics.ParticleEffectCustom;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.MouseJoint;
import com.badlogic.gdx.physics.box2d.joints.MouseJointDef;
import com.thebalancegame.GameScreen;
import com.thebalancegame.TheBalanceGame;
import com.thebalancegame.gameobjects.GameObject;

public class ArcadeLevel7 extends GameScreen {
	private int lives;
	private ArrayList<GameObject> boxes = new ArrayList<GameObject>();
	private ArrayList<GameObject> balls = new ArrayList<GameObject>();
	private float force = 20000;
	private GameObject mainBox;
	private long winnerCheck;
	private boolean showWinnerTimer;
	private Body bodyToDestroy;
	private Sprite bgSprite;
	private Sprite ballSprite;
	private TextureRegion boxRegion;
	private TextureRegion sustainRegion;
	private TextureRegion sustainRegion2;
	private TextureRegion lineRegion;

	public ArcadeLevel7(TheBalanceGame g) {
		super(g);
		LEVEL_WIDTH = 24;
		LEVEL_HEIGHT = 32;
	}
	
	@Override
	public void createWorld (World world) {
		lives = 3;
		gameTimeCheck = 0;
		boxes.clear();
		balls.clear();
		winnerCheck = 0;
		showWinnerTimer = false;
		bodyToDestroy = null;
		renderDebug = false;

		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(15, 0.5f);
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		fd.shape = shape;
		fd.friction = 0.4f;
		fd.restitution = 0.0f;
		bd.position.set(0, 2);
		world.createBody(bd).createFixture(fd);
		shape.setAsBox(30, 0.1f);
		bd.position.set(0, 25);
		fd.isSensor = true;
		world.createBody(bd).createFixture(fd);
		shape.setAsBox(5, 0.4f);
		bd.position.set(-23.55f, 18);
		fd.isSensor = false;
		bd.angle = (float)Math.toRadians(0);
		world.createBody(bd).createFixture(fd);
		shape.setAsBox(0.4f, 0.6f);
		bd.position.set(-19, 19.2f); 
		world.createBody(bd).createFixture(fd);
		shape.dispose();
		
		newPiece = createSimplePiece(-26, 20.5f, 10, 0.2f, 0.1f, 1, false);
		boxes.add(new GameObject(newPiece, newPiece.body));
		mainBox = boxes.get(boxes.size()-1);
		mainBox.body.applyForceToCenter(force, 0);
		
		newPiece = createSimplePiece(-12, 3, 11, 0.98f, 0.1f, 1, false);
		//newPiece.body.getFixtureList().get(0).setDensity(1000);
		balls.add(new GameObject(newPiece, newPiece.body));
		newPiece = createSimplePiece(-8, 3, 11, 0.98f, 0.1f, 1, false);
		balls.add(new GameObject(newPiece, newPiece.body));
		newPiece = createSimplePiece(-4, 3, 11, 0.98f, 0.1f, 1, false);
		balls.add(new GameObject(newPiece, newPiece.body));
		newPiece = createSimplePiece(0, 3, 11, 0.98f, 0.1f, 1, false);
		balls.add(new GameObject(newPiece, newPiece.body));
		newPiece = createSimplePiece(4, 3, 11, 0.98f, 0.1f, 1, false);
		balls.add(new GameObject(newPiece, newPiece.body));
		newPiece = createSimplePiece(8, 3, 11, 0.98f, 0.1f, 1, false);
		balls.add(new GameObject(newPiece, newPiece.body));
		newPiece = createSimplePiece(12, 3, 11, 0.98f, 0.1f, 1, false);
		balls.add(new GameObject(newPiece, newPiece.body));
		
		/** SPRITES **/
		boxRegion = new TextureRegion(new Texture("data/sprites/Bar.png"), 0, 105, 85, 77);
		boxRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		sustainRegion = new TextureRegion(new Texture("data/sprites/Bar.png"), 155, 123, 116, 44);
		sustainRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		sustainRegion2 = new TextureRegion(new Texture("data/sprites/Bar.png"), 0, 201, 512, 29);
		sustainRegion2.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		lineRegion = new TextureRegion(new Texture("data/sprites/Bar.png"), 155, 173, 215, 16);
		lineRegion.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bgSprite = new Sprite(new Texture("data/sprites/bg.png"));
		bgSprite.setBounds(-WORLD_WIDTH/2, -1, WORLD_WIDTH, WORLD_HEIGHT + 1);
		ballSprite = new Sprite(new Texture("data/sprites/darkblueball2.png"));
		ballSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		ballSprite.setOrigin(2.2f, 2.2f);
		
		/** LIGHTS **/
		createNewLight(new DirectionalLight(rayHandler, 32, new Color(0, 0, 0.5f, 0.3f), -45));
		tempLight.setSoft(true);
		tempLight.setXray(true);
		
		/** PARTICLES **/
		particleEffect = new ParticleEffectCustom();
		particleEffect.load(Gdx.files.internal("data/particles/falling_particles"), Gdx.files.internal("data/particles"));
		particleEffect.setPosition(0, - 15);
		particleEffect.start();
		particleEffects.add(particleEffect);
	}
	
	@Override
	public void handleGameMechanics(float deltaTime) {
		for (GameObject box : boxes) {
			if (!box.used && box.body.getPosition().y < 0) {
				lives--;
				if (box.body == mainBox.body)
					bodyToDestroy = box.body;
				box.used = true;
			}
			else if (!box.used && (box.body.getPosition().y > LEVEL_HEIGHT || box.body.getPosition().x > LEVEL_WIDTH + 20 || box.body.getPosition().x < -LEVEL_WIDTH - 20)) {
				lives--;
				box.used = true;
				bodyToDestroy = box.body;
			}
			else if (box.visible && box.body.getPosition().y < -5) {
				box.visible = false;
			}
		}
		for (GameObject ball : balls) {
			if (!ball.used && ball.body.getPosition().y < 0) {
				gameover = true;
				lives = 3;
				showWinnerTimer = false;
				ball.used = true;
			}
			else if (ball.visible && ball.body.getPosition().y < -5) {
				ball.visible = false;
			}
		}
		if (bodyToDestroy != null) {
			if (bodyToDestroy == mainBox.body) { 
				newPiece = createSimplePiece(-26, 20.5f, 10, 0.2f, 0.1f, 1, false);
				boxes.add(new GameObject(newPiece, newPiece.body));
				mainBox = boxes.get(boxes.size()-1);
				mainBox.body.applyForceToCenter(force, 0);
			}
			world.destroyBody(bodyToDestroy);
			bodyToDestroy = null;
		}
		if (!isDragging && mainBox.body.getPosition().x > -19) {
			winnerCheck = System.currentTimeMillis();
			showWinnerTimer = true;
			newPiece = createSimplePiece(-26, 20.5f, 10, 0.2f, 0.1f, 1, false);
			boxes.add(new GameObject(newPiece, newPiece.body));
			mainBox = boxes.get(boxes.size()-1);
			mainBox.body.applyForceToCenter(force, 0);
		}
		if (lives < 0) {
			gameover = true;
			lives = 3;
			showWinnerTimer = false;
			return;
		}
		if (System.currentTimeMillis() - winnerCheck > 2000) {
			showWinnerTimer = false;
			for (GameObject box : boxes) {
				if (box.body.getLinearVelocity().len2() > 25)
					continue;
				if (!box.used && box.body != mainBox.body && box.body.getPosition().y >= 23.5f) {
					showWinnerTimer = false;
					winner = true;
					return;
				}
			}
		}
		if (gameTime >= 60) {
			gameover = true;
			showWinnerTimer = false;
			return;
		}
		else if (gameTimeCheck >= 30) {
			gameTimeCheck = 0;
		}
	}
	
	@Override
	public void handleBodyDestroyed(Body body) {
		
	}
	
	@Override
	public void handleTouchInput(int type, float x, float y) {
		if (type == 0) {
			// Drag Mode
			hitBody = null;
			world.QueryAABB(callback, testPoint.x - 0.0001f, testPoint.y - 0.0001f, testPoint.x + 0.0001f, testPoint.y + 0.0001f);
			if (hitBody == groundBody) hitBody = null;
			if (hitBody != null && (hitBody.getType() == BodyType.KinematicBody || hitBody.getType() == BodyType.StaticBody || hitBody != mainBox.body)) return;
			if (hitBody != null) {
				MouseJointDef def = new MouseJointDef();
				def.bodyA = groundBody;
				def.bodyB = hitBody;
				def.collideConnected = true;
				def.target.set(hitBody.getPosition().x, hitBody.getPosition().y + 6);
				def.maxForce = dragForceMult * hitBody.getMass();
	
				mouseJoint = (MouseJoint)world.createJoint(def);
				hitBody.setAwake(true);
				isDragging = true;
			}
		}
		else if (type == 1) {
			if (mouseJoint != null) {
				if (testPoint.y < 13.7f)
					testPoint.y = 13.7f;
				mouseJoint.setTarget(target.set(testPoint.x, testPoint.y));
				mouseJoint.getBodyB().setLinearVelocity(0, 0);
				mouseJoint.getBodyB().setTransform(testPoint.x, testPoint.y - 6, 0);
			}
		}
		else if (type == 2) {
			if (mainBox != null && mainBox.body.getPosition().x > -19) {
				winnerCheck = System.currentTimeMillis();
				showWinnerTimer = true;
				newPiece = createSimplePiece(-26, 20.5f, 10, 0.2f, 0.1f, 1, false);
				boxes.add(new GameObject(newPiece, newPiece.body));
				mainBox = boxes.get(boxes.size()-1);
				mainBox.body.applyForceToCenter(force, 0);
			}
		}
	}
	
	public void handleLevelRendering(float deltaTime) {
		// Background
		batch.disableBlending();
		bgSprite.draw(batch);
		batch.enableBlending();
		for (GameObject ball : balls) {
			if (ball.visible) {
				ballSprite.setRotation((float)Math.toDegrees(ball.body.getAngle()));
				ballSprite.setBounds(ball.body.getPosition().x - 2.2f, ball.body.getPosition().y - 2.2f, 4.4f, 4.4f);
				ballSprite.draw(batch);
			}
		}

		for (GameObject box : boxes) {
			if (box.visible) {
				batch.draw(boxRegion, box.body.getPosition().x - 1.5f, box.body.getPosition().y - 1.3f, // the bottom left corner of the box, unrotated
					1.5f, 1.3f, // the rotation center relative to the bottom left corner of the box
					3f, 2.6f, // the width and height of the box
					1, 1, // the scale on the x- and y-axis
					(float)Math.toDegrees(box.body.getAngle())); // the rotation angle
			}
		}
		batch.draw(sustainRegion, -26.2f, 17.2f, // the bottom left corner of the box, unrotated
			0, 0, // the rotation center relative to the bottom left corner of the box
			8f, 2.9f, // the width and height of the box
			1, 1, // the scale on the x- and y-axis
			0); // the rotation angle
		batch.draw(sustainRegion2, -15.8f, 1.3f, // the bottom left corner of the box, unrotated
				0, 0, // the rotation center relative to the bottom left corner of the box
				31.5f, 1.5f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				0); // the rotation angle
		batch.draw(lineRegion, -LEVEL_WIDTH - 10, 24.7f, // the bottom left corner of the box, unrotated
				0, 0, // the rotation center relative to the bottom left corner of the box
				LEVEL_WIDTH*2 + 20, 0.5f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				0); // the rotation angle
	}
	
	@Override
	public void displayLevelUI() {
		font.draw(batch,  "LIVES: " + lives, 5, 40);
		font.draw(batch,  "LEVEL 7", Gdx.graphics.getWidth()/2, 20);
		if (showWinnerTimer)
			font.draw(batch,  "Winner Check in: " + ((float)(2.0f - ((System.currentTimeMillis() - winnerCheck)/1000.0f))), Gdx.graphics.getWidth()/2 - 20, Gdx.graphics.getHeight() - 20);
	}
}
