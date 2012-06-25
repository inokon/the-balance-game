package com.thebalancegame.levels.arcademode;

import java.util.ArrayList;

import box2dLight.DirectionalLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.thebalancegame.graphics.ParticleEffectCustom;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.thebalancegame.GameScreen;
import com.thebalancegame.TheBalanceGame;
import com.thebalancegame.gameobjects.GameObject;

public class ArcadeLevel4 extends GameScreen {
	private int lives;
	private float counter;
	private float bombTime;
	private float nextTimeCheck;
	private ArrayList<GameObject> balls = new ArrayList<GameObject>();
	private ArrayList<GameObject> bombs = new ArrayList<GameObject>();
	private Body basketLeft;
	private Body basketRight;
	private Body basketLeftHandle;
	private Body basketRightHandle;
	private Sprite bgSprite;
	private Sprite bombSprite;
	private Sprite ballSprite;
	private TextureRegion region;
	private ParticleEffectCustom eff;
	private boolean basketVisible;
	private int i;

	public ArcadeLevel4(TheBalanceGame g) {
		super(g);
		LEVEL_WIDTH = 24;
		LEVEL_HEIGHT = 32;
	}
	
	@Override
	public void createWorld (World world) {
		lives = 3;
		bombTime = 0;
		counter = 0;
		gameTimeCheck = 0;
		bombs.clear();
		balls.clear();
		basketVisible = true;
		renderDebug = false;

		BodyDef bd = new BodyDef();
		bd.type = BodyType.KinematicBody;
		fd.friction = 0.0f;
		fd.restitution = 0.2f;
		bd.position.set(-3f, -0.2f);
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.2f, 4.5f);
		fd.shape = shape;
		basketLeft = world.createBody(bd); 
		basketLeft.createFixture(fd);
		bd.position.set(3f, -0.2f);
		basketRight = world.createBody(bd); 
		basketRight.createFixture(fd);
		shape.dispose();
		CircleShape shape2 = new CircleShape();
		shape2.setRadius(0.2f);
		bd.position.set(-3f, 4.3f);
		fd.shape = shape2;
		basketLeftHandle = world.createBody(bd); 
		basketLeftHandle.createFixture(fd);
		bd.position.set(3f, 4.3f);
		basketRightHandle = world.createBody(bd); 
		basketRightHandle.createFixture(fd);
		shape2.dispose();

		newPiece = createSimplePiece(rnd.nextFloat() * LEVEL_WIDTH * 1.7f - LEVEL_WIDTH * 0.8f, LEVEL_HEIGHT + 1, 5, 0.7f, 0.1f, 2, false);
		newPiece.body.setBullet(true);
		balls.add(new GameObject(newPiece, newPiece.body));
		
		/** SPRITES **/
		region = new TextureRegion(new Texture("data/sprites/Bar.png"), 154, 55, 93, 68);
		region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bgSprite = new Sprite(new Texture("data/sprites/bg.png"));
		bgSprite.setBounds(-WORLD_WIDTH/2, -1, WORLD_WIDTH, WORLD_HEIGHT + 1);
		bombSprite = new Sprite(new Texture("data/sprites/redball.png"));
		bombSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bombSprite.setOrigin(0.7f, 0.7f);
		ballSprite = new Sprite(new Texture("data/sprites/greenball.png"));
		ballSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		ballSprite.setOrigin(0.5f, 0.5f);
		
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
		particleEffect = new ParticleEffectCustom();
		particleEffect.load(Gdx.files.internal("data/particles/fire_attached"), Gdx.files.internal("data/particles"));
		particleEffects.add(particleEffect);
		particleEffect.setPosition(0, LEVEL_HEIGHT + 20);
		particleEffect.offset.y = 0.5f;
		particleEffect.offset.x = -0.5f;
		particleEffect.start();
		for (i=0; i<3; i++) {
			particleEffect = new ParticleEffectCustom();
			particleEffect.load(Gdx.files.internal("data/particles/green_smoke"), Gdx.files.internal("data/particles"));
			particleEffect.attachedBody = basketLeft;
			particleEffect.offset.x = 3;
			particleEffect.offset.y = 5;
			particleEffects.add(particleEffect);
		}
		particleEffect = new ParticleEffectCustom();
		particleEffect.load(Gdx.files.internal("data/particles/big_explosion"), Gdx.files.internal("data/particles"));
		particleEffects.add(particleEffect);
	}
	
	@Override
	public void handleGameMechanics(float deltaTime) {
		for (GameObject ball : balls) {
			if (!ball.used && ball.body.getPosition().y < 4 && ball.body.getPosition().x > basketLeft.getPosition().x && ball.body.getPosition().x < basketRight.getPosition().x) {
				ball.used = true;
				if (ball.body.getFixtureList().size() > 0)
					ball.body.getFixtureList().get(0).setSensor(true);
				ball.visible = false;
				for (i=2; i<5; i++) {
					eff = particleEffects.get(i);
					if (!eff.isStarted || eff.isComplete()) {
						eff.start();
						break;
					}
				}
			}
			else if (!ball.used && ball.body.getPosition().y < 4) {
				ball.used = true;
				if (ball.body.getFixtureList().size() > 0)
					ball.body.getFixtureList().get(0).setSensor(true);
			}
			else if (ball.visible && ball.body.getPosition().y < 0) {
				lives--;
				ball.visible = false;
			}
			else if (!ball.used && ball.body.getPosition().y > 4) {
				ball.body.setAwake(true);
			}
		}
		for (GameObject bomb : bombs) {
			if (!bomb.used && bomb.visible && bomb.body.getPosition().y < 4 && bomb.body.getPosition().x > basketLeft.getPosition().x && bomb.body.getPosition().x < basketRight.getPosition().x) {
				gameover = true;
				lives = 3;
				bomb.visible = false;
				eff = particleEffects.get(5);
				if (!eff.isStarted || eff.isComplete()) {
					eff.setPosition(basketLeft.getPosition().x + 3, basketLeft.getPosition().y + 2);
					eff.start();
				}
				basketVisible = false;
				return;
			}
			else if (!bomb.used && bomb.body.getPosition().y < 4) {
				bomb.used = true;
				if (bomb.body.getFixtureList().size() > 0)
					bomb.body.getFixtureList().get(0).setSensor(true);
			}
			else if (bomb.visible && bomb.body.getPosition().y < 0) {
				bomb.visible = false;
				if (bomb.attachedParticle != null) {
					bomb.attachedParticle.attachedBody = null;
					bomb.attachedParticle.setPosition(0, LEVEL_HEIGHT+20);
					bomb.attachedParticle = null;
				}
			}
			else if (!bomb.used && bomb.body.getPosition().y > 4) {
				bomb.body.setAwake(true);
				if (bomb.attachedParticle != null) {
					bomb.attachedParticle.offset.rotate((bomb.body.getAngle()));
				}
			}
		}
		if (lives < 0) {
			gameover = true;
			lives = 3;
			return;
		}
		nextTimeCheck = 0.9f + rnd.nextFloat()*3 - counter;
		if (nextTimeCheck < 0.25f)
			nextTimeCheck = 0.25f;
		if (gameTime >= 60) {
			winner = true;
			return;
		}
		else if (gameTimeCheck >= nextTimeCheck) {
			newPiece = createSimplePiece(rnd.nextFloat() * LEVEL_WIDTH * 1.7f - LEVEL_WIDTH * 0.8f, LEVEL_HEIGHT + 1, 5, 0.7f, 0.1f, 2, false);
			newPiece.body.setBullet(true);
			balls.add(new GameObject(newPiece, newPiece.body));
			gameTimeCheck = 0;
			counter += 0.01f;
		}
		else if (bombTime >= (2 + rnd.nextFloat()*5)) {
			newPiece = createSimplePiece(rnd.nextFloat() * LEVEL_WIDTH * 1.7f - LEVEL_WIDTH * 0.8f, LEVEL_HEIGHT + 1, 5, 0.7f, 0.1f, 2, false);
			newPiece.body.setBullet(true);
			bombs.add(new GameObject(newPiece, newPiece.body));
			eff = particleEffects.get(1);
			eff.attachedBody = bombs.get(bombs.size()-1).body;
			bombs.get(bombs.size()-1).attachedParticle = eff;
			bombs.get(bombs.size()-1).body.setAngularVelocity(5);
			bombTime = 0;
		}
		bombTime += deltaTime;
	}
	
	@Override
	public void handleBalanceInput(float x, float y, boolean isAccel) {
		if (isAccel) {
			tempAcceleratorY = y;
			tempAcceleratorY = tempAcceleratorY * ACCELERATOR_FILTER + lastAcceleratorY * (1 - ACCELERATOR_FILTER);
			tempFloat = tempAcceleratorY;
			if (tempFloat > 2.5f)
				tempFloat = 2.5f;
			else if (tempFloat < -2.5f)
				tempFloat = -2.5f;
			tempFloat *= LEVEL_WIDTH/2.857f;
			basketLeft.setTransform(tempFloat - 3f, basketLeft.getPosition().y, 0);
			basketRight.setTransform(tempFloat + 3f, basketRight.getPosition().y, 0);
			basketLeftHandle.setTransform(tempFloat - 3f, basketLeftHandle.getPosition().y, 0);
			basketRightHandle.setTransform(tempFloat + 3f, basketRightHandle.getPosition().y, 0);
			lastAcceleratorY = tempAcceleratorY;
				
		}
		else {
			basketLeft.setTransform(x - 3f, basketLeft.getPosition().y, 0);
			basketRight.setTransform(x + 3f, basketRight.getPosition().y, 0);
			basketLeftHandle.setTransform(x - 3f, basketLeftHandle.getPosition().y, 0);
			basketRightHandle.setTransform(x + 3f, basketRightHandle.getPosition().y, 0);
		}
	}
	
	public void handleLevelRendering(float deltaTime) {
		// Background
		batch.disableBlending();
		bgSprite.draw(batch);
		batch.enableBlending();
		for (GameObject ball : balls) {
			if (ball.visible) {
				ballSprite.setBounds(ball.body.getPosition().x - 0.5f, ball.body.getPosition().y - 0.5f, 1f, 1f);
				ballSprite.draw(batch);
			}
		}
		for (GameObject bomb : bombs) {
			if (bomb.visible) { 
				bombSprite.setRotation((float)Math.toDegrees(bomb.body.getAngle()));
				bombSprite.setBounds(bomb.body.getPosition().x - 0.7f, bomb.body.getPosition().y - 0.7f, 1.4f, 1.4f);
				bombSprite.draw(batch);
			}
		}
		if (basketVisible) {
			batch.draw(region, basketLeft.getPosition().x - 0.8f, -1, // the bottom left corner of the box, unrotated
				0, 0, // the rotation center relative to the bottom left corner of the box
				7.7f, 6, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				0); // the rotation angle
		}
	}
	
	@Override
	public void displayLevelUI() {
		font.draw(batch,  "LIVES: " + lives, 5, 40);
		font.draw(batch,  "LEVEL 4", Gdx.graphics.getWidth()/2, 20);
	}
}
