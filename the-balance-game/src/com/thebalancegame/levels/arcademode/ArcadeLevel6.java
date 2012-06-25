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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.thebalancegame.GameScreen;
import com.thebalancegame.TheBalanceGame;
import com.thebalancegame.gameobjects.GameObject;

public class ArcadeLevel6 extends GameScreen {
	private int lives;
	private float counter;
	private Sprite bgSprite;
	private Sprite mainBallSprite;
	private TextureRegion region;
	private TextureRegion region2;
	private GameObject column;
	private GameObject tempArrow;
	private ArrayList<GameObject> arrows = new ArrayList<GameObject>();
	private ArrayList<GameObject> arrowsToDestroy = new ArrayList<GameObject>();
	private ArrayList<Float> arrowsHeights = new ArrayList<Float>();
	private ArrayList<ParticleEffectCustom> particleEffects2 = new ArrayList<ParticleEffectCustom>();
	private Vector2 force = new Vector2(0, 92000);
	private Vector2 doubleJumpForce = new Vector2(0, 85000);
	private Vector2 crouchForce = new Vector2(0, 150000);
	private boolean canDoubleJump = true;
	private long crouchTimeCheck;
	private long crouchTimeCheck2;
	private boolean isCrouching;
	private float currentRadius;
	private int difficulty = 3;
	private int i;

	public ArcadeLevel6(TheBalanceGame g) {
		super(g);
		LEVEL_WIDTH = 24;
		LEVEL_HEIGHT = 32;
	}
	
	@Override
	public void createWorld (World world) {
		lives = 3;
		gameTimeCheck = 0;
		counter = 0;
		arrows.clear();
		isCrouching = false;
		crouchTimeCheck = 0;
		canDoubleJump = true;
		difficulty = 3;
		renderDebug = false;
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(2, 8);
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		fd.shape = shape;
		fd.friction = 0.4f;
		fd.restitution = 0.0f;
		bd.position.set(0, 4);
		column = new GameObject(null, world.createBody(bd));
		column.body.createFixture(fd);
		shape.dispose();

		newPiece = getNewPieceInstanceFromTemplate(11);
		newPiece.setPhysics(0.3f, 0.0f, 4, false);
		newPiece.isBullet = true;
		createBodyAndFixture(newPiece, 0, 15);
		mainBall = new GameObject(newPiece, newPiece.body);
		
		arrowsHeights.add(13.5f);
		arrowsHeights.add(15.5f);
		arrowsHeights.add(18.0f);
		arrowsHeights.add(21.0f);
		//arrowsHeights.add(24.0f);
		
		/** LIGHTS **/
		createNewLight(new PointLight(rayHandler, 32, Color.RED, 4, 0, 15));
		tempLight.attachToBody(mainBall.body, 0, 0);
		tempLight.setXray(true);
		mainBall.attachedLight = tempLight;
		createNewLight(new DirectionalLight(rayHandler, 32, new Color(0, 0, 0.5f, 0.3f), -45));
		tempLight.setSoft(true);
		tempLight.setXray(true);
		
		/** SPRITES **/
		region = new TextureRegion(new Texture("data/sprites/Bar.png"), 82, 35, 75, 167);
		region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		region2 = new TextureRegion(new Texture("data/sprites/Bar.png"), 158, 35, 77, 19);
		region2.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bgSprite = new Sprite(new Texture("data/sprites/bg.png"));
		bgSprite.setBounds(-WORLD_WIDTH/2, -1, WORLD_WIDTH, WORLD_HEIGHT + 1);
		mainBallSprite = new Sprite(new Texture("data/sprites/mainball.png"));
		mainBallSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		mainBallSprite.setOrigin(2, 2);
		
		/** PARTICLES **/
		particleEffect = new ParticleEffectCustom();
		particleEffect.load(Gdx.files.internal("data/particles/falling_particles"), Gdx.files.internal("data/particles"));
		particleEffect.setPosition(0, - 15);
		particleEffect.start();
		particleEffects.add(particleEffect);
		
		for (i=0; i<10; i++) {
			particleEffect = new ParticleEffectCustom();
			particleEffect.load(Gdx.files.internal("data/particles/trail"), Gdx.files.internal("data/particles"));
			particleEffects2.add(particleEffect);
		}
	}
	
	@Override
	public void handleGameMechanics(float deltaTime) {
		updateParticles(particleEffects2, deltaTime);
		for (GameObject arrow : arrowsToDestroy) {
			arrows.remove(arrow);
			arrow.body.getFixtureList().get(0).setSensor(true);
		}
		arrowsToDestroy.clear();
		for (GameObject arrow : arrows) {
			if (arrow.visible && arrow.body.getPosition().x > WORLD_WIDTH/1.5 || arrow.body.getPosition().x < -WORLD_WIDTH/1.5) {
				if (arrow.attachedLight != null) {
					arrow.attachedLight.attachToBody(null, 0, 0);
					arrow.attachedLight.setActive(false);
					arrow.attachedLight = null;
				}
				if (arrow.attachedParticle != null) {
					arrow.attachedParticle.isStarted = false;
				}
				arrow.visible = false;
				arrowsToDestroy.add(arrow);
			}
		}
		if (mainBall.body.getPosition().y < 5) {
			gameover = true;
			return;
		}
		else if (mainBall.body.getPosition().y < 15)
			canDoubleJump = true;
		if (!isCrouching && crouchTimeCheck > 0 && (System.currentTimeMillis() - crouchTimeCheck) > 200) {
			isCrouching = true;
			startScaleTweeningAndReverse(mainBall, 0.5f, 0.3f, deltaTime);
			crouchTimeCheck2 = System.currentTimeMillis();
			crouchTimeCheck = -100;
		}
		else if ((System.currentTimeMillis() - crouchTimeCheck2) > 1000) {
			crouchTimeCheck2 = 0;
			isCrouching = false;
			mainBallSprite.setOrigin(2, 2);
		}
		if (lives < 0) {
			gameover = true;
			lives = 3;
			return;
		}
		if (gameTime >= 60) {
			winner = true;
			return;
		}
		else if (gameTimeCheck >= (1.2f + rnd.nextFloat()*2 - counter)) {
			for (i=0; i<rnd.nextInt(difficulty); i++) {
				if (rnd.nextInt(2) == 0) {
					newPiece = createSimplePiece(-LEVEL_WIDTH, arrowsHeights.get(rnd.nextInt(arrowsHeights.size())), 12, 0.7f, 0.5f, 0, false);
					newPiece.body.setLinearVelocity(25 /*- 10*i*/ + rnd.nextFloat()*10 * counter, 0);
				}
				else {
					newPiece = createSimplePiece(LEVEL_WIDTH, arrowsHeights.get(rnd.nextInt(arrowsHeights.size())), 12, 0.7f, 0.5f, 0, false);
					newPiece.body.setLinearVelocity(-25 /*+ 5*i*/ - rnd.nextFloat()*10 * counter, 0);
				}
				tempArrow = new GameObject(newPiece, newPiece.body);
				arrows.add(tempArrow);
				// attach particle
				for (ParticleEffectCustom eff : particleEffects2) {
					if (!eff.isStarted || eff.isComplete()) {
						eff.attachedBody = tempArrow.body;
						eff.setPosition(tempArrow.body.getPosition().x, tempArrow.body.getPosition().y);
						eff.start();
						tempArrow.attachedParticle = eff;
						break;
					}
				}
			}
			counter += 0.01f;
			gameTimeCheck = 0;
		}
		if (gameTime >= 40 && difficulty == 3)
			difficulty++;
	}
	
	@Override
	public void handleBodyDestroyed(Body body) {
		if (body == mainBall.body)
			gameover = true;
	}
	
	@Override
	public void handleBalanceInput(float x, float y, boolean isAccel) {
		mainBall.body.setAwake(true);
		if (isAccel) {
			tempAcceleratorY = y;
			tempAcceleratorY = tempAcceleratorY * ACCELERATOR_FILTER + lastAcceleratorY * (1 - ACCELERATOR_FILTER);
			tempFloat = tempAcceleratorY;
			if (tempFloat > 3.5f)
				tempFloat = 3.5f;
			else if (tempFloat < -3.5f)
				tempFloat = -3.5f;
			//(LEVEL_WIDTH/((LEVEL_WIDTH - 3.0f)/tempFloat)
			tempFloat *= LEVEL_WIDTH/4f;
			gravityVector.set(tempFloat/1.5f, GRAVITY_DEFAULT -(counter*10));
			world.setGravity(gravityVector);
			lastAcceleratorY = tempAcceleratorY;
				
		}
		else {
			gravityVector.set(x/1.5f, GRAVITY_DEFAULT -(counter*10));
			world.setGravity(gravityVector);
		}
	}
	
	@Override
	public void handleTouchInput(int type, float x, float y) {
		if (type == 0) {
			crouchTimeCheck = System.currentTimeMillis();
			if (!isScaleTweening && canDoubleJump && mainBall.body.getPosition().y > 15f) {
				if (isCrouching)
					mainBall.body.applyForceToCenter(crouchForce);
				else
					mainBall.body.applyForceToCenter(doubleJumpForce);
				canDoubleJump = false;
			}
		}
		else if (type == 1) {
			
		}
		else if (type == 2) {
			if (crouchTimeCheck >= 0) {
				if (Math.abs(mainBall.body.getLinearVelocity().y) < 5 && mainBall.body.getPosition().y < 15) {
					mainBall.body.applyForceToCenter(force);
				}
			}
			crouchTimeCheck = 0;
		}
	}
	
	public void handleLevelRendering(float deltaTime) {
		// Background
		batch.disableBlending();
		bgSprite.draw(batch);
		batch.enableBlending();
		// Column
		batch.draw(region, column.body.getPosition().x - 3f, column.body.getPosition().y - 8.5f, // the bottom left corner of the box, unrotated
				0, 0, // the rotation center relative to the bottom left corner of the box
				6f, 17.2f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				0); // the rotation angle
		// Arrows
		for (GameObject arrow : arrows) {
			if (arrow.visible) {
				batch.draw(region2, arrow.body.getPosition().x - 2.5f, arrow.body.getPosition().y - 0.5f, // the bottom left corner of the box, unrotated
						0, 0, // the rotation center relative to the bottom left corner of the box
						5f, 1f, // the width and height of the box
						1, 1, // the scale on the x- and y-axis
						0); // the rotation angle
			}
		}
		// Main ball
		if (isCrouching) {
			currentRadius = mainBall.body.getFixtureList().get(0).getShape().getRadius();
			mainBallSprite.setOrigin(currentRadius, currentRadius);
			//mainBallSprite.setRotation((float)Math.toDegrees(mainBall.body.getAngle()));
			mainBallSprite.setBounds(mainBall.body.getPosition().x - currentRadius, mainBall.body.getPosition().y - currentRadius, currentRadius*2, currentRadius*2);
			//System.out.println("step: " + bodyScaleTweening.getFixtureList().get(0).getShape().getRadius()*2);
		}
		else {
			//mainBallSprite.setRotation((float)Math.toDegrees(mainBall.body.getAngle()));
			mainBallSprite.setBounds(mainBall.body.getPosition().x - 2f, mainBall.body.getPosition().y - 2f, 4f, 4f);
		}
		mainBallSprite.draw(batch);
		renderParticles(particleEffects2);
	}
	
	@Override
	public void displayLevelUI() {
		//font.draw(batch,  "LIVES: " + lives, 5, 40);
		font.draw(batch,  "LEVEL 6", Gdx.graphics.getWidth()/2, 20);
	}
	
	public void handleLevelDispose() {
		arrowsToDestroy.clear();
		arrows.clear();
		bgSprite.getTexture().dispose();
		mainBallSprite.getTexture().dispose();
		region.getTexture().dispose();
		region2.getTexture().dispose();
		disposeParticles(particleEffects2);
	}
}
