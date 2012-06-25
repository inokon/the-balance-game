package com.thebalancegame.levels.arcademode;

import java.util.ArrayList;

import box2dLight.DirectionalLight;
import box2dLight.Light;
import box2dLight.PointLight;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.thebalancegame.GameScreen;
import com.thebalancegame.TheBalanceGame;
import com.thebalancegame.gameobjects.GameObject;
import com.thebalancegame.gameobjects.Portal;
import com.thebalancegame.graphics.EffectLight;
import com.thebalancegame.graphics.ParticleEffectCustom;
import com.thebalancegame.utils.ColorUtil;
import com.thebalancegame.utils.MathUtil;

public class ArcadeLevel2 extends GameScreen {
	private int lives;
	private Sprite bgSprite;
	private Sprite mainBallSprite;
	private Sprite ballSprite;
	private TextureRegion region;
	private TextureRegion region2;
	private boolean tempBool;
	private boolean tempBool2;
	private boolean tempBool3;
	private ArrayList<ParticleEffectCustom> particleEffects2 = new ArrayList<ParticleEffectCustom>();
	private ArrayList<ParticleEffectCustom> particleEffects3 = new ArrayList<ParticleEffectCustom>();
	private int i;
	private Vector2 lastStartPos = new Vector2();
	private long lastPortalWarpRendering;

	public ArcadeLevel2(TheBalanceGame g) {
		super(g);
		LEVEL_WIDTH = 24;
		LEVEL_HEIGHT = 32;
	}
	
	@Override
	public void createWorld (World world) {
		// Create ground cage
		{
			ChainShape shape = new ChainShape();
			shape.createChain(new Vector2[] {new Vector2(LEVEL_WIDTH, 0), new Vector2(LEVEL_WIDTH, LEVEL_HEIGHT - 1), new Vector2(-LEVEL_WIDTH, LEVEL_HEIGHT - 1), new Vector2(-LEVEL_WIDTH, 0)});
			fd.shape = shape;
			fd.friction = 0.0f;
			fd.restitution = 1.0f;
			BodyDef bd = new BodyDef();
			Body cage = world.createBody(bd);
			cage.createFixture(fd);
			// dispose shape
			shape.dispose();
		}
		lives = 3;
		gameTimeCheck = 0;
		renderDebug = false;
		allowPortalTransferForce = false;
		
		createMainBox(0, 1);
		createSecondaryBox(0, 10);
		createSimpleEnemy(-2, 30, 10, 10, true);
		createMainBall(0, 5, 1);
		createPortalIn(0.5f, 20, 0, -20);
		createPortalOut(LEVEL_WIDTH -0.5f, LEVEL_HEIGHT - 2.5f, 0, 130);
		createPortalIn(-0.5f, 20, 0, 200);
		createPortalOut(-LEVEL_WIDTH +0.5f, LEVEL_HEIGHT - 2.5f, 0, 50);
		
		/** LIGHTS **/
		createNewLight(new PointLight(rayHandler, 32, Color.RED, 2, 0, 15));
		tempLight.attachToBody(mainBall.body, 0, 0);
		tempLight.setXray(true);
		createNewLight(new PointLight(rayHandler, 32, ColorUtil.LIGHT_BLUE, 1, 0, 15));
		tempLight.attachToBody(enemies.get(0).body, 0, 0);
		tempLight.setXray(true);
		enemies.get(enemies.size()-1).attachedLight = tempLight;
		for (Portal portal : portalIn) {
			createNewLight(new PointLight(rayHandler, 8, ColorUtil.LIGHT_MAGENTA, 1, 0, 15));
			tempLight.attachToBody(portal.getBody(), 0, 0);
			tempLight.setXray(true);
		}
		for (Portal portal : portalOut) {
			createNewLight(new PointLight(rayHandler, 8, ColorUtil.LIGHT_MAGENTA, 1, 0, 15));
			tempLight.attachToBody(portal.getBody(), 0, 0);
			tempLight.setXray(true);
		}
		createNewLight(new DirectionalLight(rayHandler, 32, new Color(0, 0, 0.5f, 0.3f), -45));
		tempLight.setSoft(true);
		tempLight.setXray(true);
		
		/** SPRITES **/
		region = new TextureRegion(new Texture("data/sprites/Bar.png"), 0, 0, 512, 32);
		region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		region2 = new TextureRegion(new Texture("data/sprites/Bar.png"), 45, 35, 35, 70);
		region2.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bgSprite = new Sprite(new Texture("data/sprites/bg.png"));
		bgSprite.setBounds(-WORLD_WIDTH/2, -1, WORLD_WIDTH, WORLD_HEIGHT + 1);
		mainBallSprite = new Sprite(new Texture("data/sprites/mainball.png"));
		mainBallSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		mainBallSprite.setOrigin(1, 1);
		ballSprite = new Sprite(new Texture("data/sprites/ball.png"));
		ballSprite.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		ballSprite.setOrigin(0.5f, 0.5f);
		
		/** PARTICLES **/
		particleEffect = new ParticleEffectCustom();
		particleEffect.load(Gdx.files.internal("data/particles/falling_particles"), Gdx.files.internal("data/particles"));
		particleEffect.setPosition(0, - 15);
		particleEffect.start();
		particleEffects.add(particleEffect);
		for (i=0; i<15; i++) {
			particleEffect = new ParticleEffectCustom();
			particleEffect.load(Gdx.files.internal("data/particles/blue_sparks"), Gdx.files.internal("data/particles"));
			particleEffects.add(particleEffect);
		}
		for (i=0; i<10; i++) {
			particleEffect = new ParticleEffectCustom();
			particleEffect.load(Gdx.files.internal("data/particles/portal_warp"), Gdx.files.internal("data/particles"));
			particleEffects2.add(particleEffect);
		}
		for (i=0; i<6; i++) {
			particleEffect = new ParticleEffectCustom();
			particleEffect.load(Gdx.files.internal("data/particles/boiling"), Gdx.files.internal("data/particles"));
			particleEffects3.add(particleEffect);
		}
	}
	
	@Override
	public void handleGameMechanics(float deltaTime) {
		updateParticles(particleEffects2, deltaTime);
		updateParticles(particleEffects3, deltaTime);
		if (secondaryBox != null) {
			for (GameObject enemy : enemies) {
				if (!enemy.used && enemy.body.getPosition().y < secondaryBox.body.getPosition().y) {
					enemy.body.getFixtureList().get(0).setSensor(true);
					enemy.used = true;
					enemy.visible = false;
					lives--;
					enemy.attachedLight.attachToBody(null, 0, 0);
					enemy.attachedLight.setActive(false);
					enemy.attachedLight = null;
					// trigger boiling particles
					tempBool3 = false;
					for (ParticleEffectCustom eff : particleEffects3) {
						if (!eff.isStarted || eff.isComplete()) {
							eff.setPosition(enemy.body.getPosition().x, enemy.body.getPosition().y);
							eff.start();
							tempBool3 = true;
							break;
						}
					}
					if (!tempBool3) {
						particleEffect = new ParticleEffectCustom();
						particleEffect.load(Gdx.files.internal("data/particles/boiling"), Gdx.files.internal("data/particles"));
						particleEffect.setPosition(enemy.body.getPosition().x, enemy.body.getPosition().y);
						particleEffect.start();
						particleEffects3.add(particleEffect);
					}
				}
				else if (enemy.body.getPosition().y < -5) {
					enemy.body.setLinearVelocity(0, 0);
				}
				else if (!enemy.used) {
					if (enemy.body.getLinearVelocity().x >= 0 && enemy.body.getLinearVelocity().x < enemy.genericFloat)
						enemy.body.setLinearVelocity(enemy.genericFloat, enemy.body.getLinearVelocity().y);
					else if (enemy.body.getLinearVelocity().x < 0 && enemy.body.getLinearVelocity().x > -enemy.genericFloat)
							enemy.body.setLinearVelocity(-enemy.genericFloat, enemy.body.getLinearVelocity().y);
					if (enemy.body.getLinearVelocity().y >= 0 && enemy.body.getLinearVelocity().y < 5)
						enemy.body.setLinearVelocity(enemy.body.getLinearVelocity().x, 5);
					else if (enemy.body.getLinearVelocity().y < 0 && enemy.body.getLinearVelocity().y > -5)
						enemy.body.setLinearVelocity(enemy.body.getLinearVelocity().x, -5);
				}
			}
		}
		if (lives < 0) {
			gameover = true;
			lives = 3;
			return;
		}
		if (mainBall != null) {
			mainBall.body.setAwake(true);
			if (mainBall.body.getPosition().y < -2) {
				gameover = true;
				return;
			}
		}
		if (gameTime >= 60) {
			winner = true;
			return;
		}
		else if (gameTimeCheck >= 10) {
			createSimpleEnemy(rnd.nextFloat() * LEVEL_WIDTH, 20 + rnd.nextFloat() * (LEVEL_HEIGHT - 25), 10 + enemies.size()*5, 10 + enemies.size()*5, true);
			wave.setPosition(enemies.get(enemies.size()-1).body.getPosition(), camera);
			wave.start();
			createNewLight(new PointLight(rayHandler, 32, ColorUtil.LIGHT_BLUE, 1, 0, 15));
			tempLight.attachToBody(enemies.get(enemies.size()-1).body, 0, 0);
			tempLight.setXray(true);
			enemies.get(enemies.size()-1).attachedLight = tempLight;
			gameTimeCheck = 0;
		}
	}
	
	@Override
	public void handleContactFilter(Body bodyA, Body bodyB) {
		if (mainBox == null || secondaryBox == null)
			return;
		if (MathUtil.isEqual(bodyA.getPosition(), mainBox.body.getPosition()) || MathUtil.isEqual(bodyA.getPosition(), mainBall.body.getPosition())) {
			return;
		}
		// handle contact
		for (GameObject enemy : enemies) {
			if (enemy.used)
				continue;
			if (MathUtil.isEqual(bodyB.getPosition(), enemy.body.getPosition())) {
				if (MathUtil.isEqual(bodyA.getPosition(), secondaryBox.body.getPosition())) {
					//synchronized (contactListenerVector) {
					Vector2 contactListenerVector = new Vector2();
						contactListenerVector.set(enemy.body.getPosition());
						contactListenerVector.sub(secondaryBox.body.getPosition());
						enemy.body.setLinearVelocity(enemy.body.getLinearVelocity().rotate(-enemy.body.getLinearVelocity().angle() + contactListenerVector.angle())
								.mul(1 + Math.abs(contactListenerVector.angle())*0.0002f));
					//}
				}
				for (Light light : lights) {
					if (!light.isActive())
						continue;
					if (light.getBody() != null && MathUtil.isEqual(light.getBody().getPosition(), enemy.body.getPosition())) {
						boolean temp = false;
						for (EffectLight eff : effectLights) {
							if (eff.light.hashCode() == light.hashCode())
								temp = true;
						}
						if (!temp)
							effectLights.add(new EffectLight(light, 2f, 10));
						break;
					}
				}
				if (System.currentTimeMillis() - enemy.timer > 100) {
					enemy.timer = System.currentTimeMillis();
					// particles
					tempBool = false;
					for (ParticleEffectCustom eff : particleEffects) {
						if (!eff.isStarted || eff.isComplete()) {
							eff.setPosition(bodyB.getPosition().x, bodyB.getPosition().y);
							eff.start();
							tempBool = true;
							break;
						}
					}
					if (!tempBool) {
						particleEffect = new ParticleEffectCustom();
						particleEffect.load(Gdx.files.internal("data/particles/blue_sparks"), Gdx.files.internal("data/particles"));
						particleEffect.setPosition(bodyB.getPosition().x, bodyB.getPosition().y);
						particleEffect.start();
						particleEffects.add(particleEffect);
					}
				}
				return;
			}
		}
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
			//(LEVEL_WIDTH/((LEVEL_WIDTH - 3.0f)/tempFloat)
			tempFloat *= LEVEL_WIDTH/2.857f;
			if (secondaryBox != null) {
				secondaryBox.body.setTransform(tempFloat, secondaryBox.body.getPosition().y, 0);
			}
			tempFloat *= 1.4f;
			if (mainBox != null) {
				mainBox.body.setTransform(mainBox.body.getPosition(), (float)Math.toRadians(tempFloat));
			}
			lastAcceleratorY = tempAcceleratorY;
		}
		else {
			if (secondaryBox != null)
				secondaryBox.body.setTransform(x, secondaryBox.body.getPosition().y, 0);
			if (mainBox != null)
				mainBox.body.setTransform(mainBox.body.getPosition(), (float)Math.toRadians(x * 1.4f));
		}
	}
	
	@Override
	public void handleLevelRendering(float deltaTime) {
		// Background
		batch.disableBlending();
		bgSprite.draw(batch);
		batch.enableBlending();
		// Main Box
		batch.draw(region, mainBox.body.getPosition().x - 15.25f, mainBox.body.getPosition().y - 0.6f, // the bottom left corner of the box, unrotated
				15f, 0.5f, // the rotation center relative to the bottom left corner of the box
				30.5f, 1.4f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				(float)Math.toDegrees(mainBox.body.getAngle())); // the rotation angle
		// Second box
		batch.draw(region, secondaryBox.body.getPosition().x - 3, secondaryBox.body.getPosition().y - 0.6f, // the bottom left corner of the box, unrotated
				3f, 0.2f, // the rotation center relative to the bottom left corner of the box
				6, 1.2f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				0); // the rotation angle
		// Main ball
		//mainBallSprite.setRotation((float)Math.toDegrees(mainBall.body.getAngle()));
		mainBallSprite.setBounds(mainBall.body.getPosition().x - 1f, mainBall.body.getPosition().y - 1f, 2f, 2f);
		mainBallSprite.draw(batch);
		// Balls
		for (GameObject enemy : enemies) {
			if (!enemy.visible)
				continue;
			ballSprite.setRotation((float)Math.toDegrees(enemy.body.getAngle()));
			ballSprite.setBounds(enemy.body.getPosition().x - 0.5f, enemy.body.getPosition().y - 0.5f, 1f, 1f);
			ballSprite.draw(batch);
		}
		// Portals
		for (Portal portal : portalIn) {
			batch.draw(region2, portal.getBody().getPosition().x - 0.6f, portal.getBody().getPosition().y - 1.7f, // the bottom left corner of the box, unrotated
					0, 0, // the rotation center relative to the bottom left corner of the box
					1.2f, 3.5f, // the width and height of the box
					1, 1, // the scale on the x- and y-axis
					0); // the rotation angle
		}
		for (Portal portal : portalOut) {
			batch.draw(region2, portal.getBody().getPosition().x - 0.6f, portal.getBody().getPosition().y - 1.7f, // the bottom left corner of the box, unrotated
					0, 0, // the rotation center relative to the bottom left corner of the box
					1.2f, 3.5f, // the width and height of the box
					1, 1, // the scale on the x- and y-axis
					0); // the rotation angle
		}
		renderParticles(particleEffects2);
		renderParticles(particleEffects3);
	}
	
	@Override
	public void handlePortalCallbackRendering(Vector2 startPos, Vector2 endPos) {
		if (System.currentTimeMillis() - lastPortalWarpRendering < 100 && (MathUtil.isEqual(startPos, lastStartPos) || MathUtil.isEqual(endPos, lastStartPos)))
			return;
		// particles
		tempBool2 = false;
		for (ParticleEffectCustom eff : particleEffects2) {
			if (!eff.isStarted || eff.isComplete()) {
				eff.setPosition(startPos.x, startPos.y);
				eff.start();
				tempBool2 = true;
				break;
			}
		}
		if (!tempBool2) {
			particleEffect = new ParticleEffectCustom();
			particleEffect.load(Gdx.files.internal("data/particles/portal_warp"), Gdx.files.internal("data/particles"));
			particleEffect.setPosition(startPos.x, startPos.y);
			particleEffect.start();
			particleEffects2.add(particleEffect);
		}
		tempBool2 = false;
		for (ParticleEffectCustom eff : particleEffects2) {
			if (!eff.isStarted || eff.isComplete()) {
				eff.setPosition(endPos.x, endPos.y);
				eff.start();
				tempBool2 = true;
				break;
			}
		}
		if (!tempBool2) {
			particleEffect = new ParticleEffectCustom();
			particleEffect.load(Gdx.files.internal("data/particles/portal_warp"), Gdx.files.internal("data/particles"));
			particleEffect.setPosition(endPos.x, endPos.y);
			particleEffect.start();
			particleEffects2.add(particleEffect);
		}
		lastStartPos.set(startPos);
		lastPortalWarpRendering = System.currentTimeMillis();
	}
	
	@Override
	public void displayLevelUI() {
		font.draw(batch,  "LIVES: " + lives, 5, 40);
		font.draw(batch,  "LEVEL 2", Gdx.graphics.getWidth()/2, 20);
		//System.out.println("size: " + particleEffects.size() + " size2: " + particleEffects2.size());
	}
	
	public void handleLevelDispose() {
		disposeParticles(particleEffects2);
		disposeParticles(particleEffects3);
		bgSprite.getTexture().dispose();
		mainBallSprite.getTexture().dispose();
		ballSprite.getTexture().dispose();
		region.getTexture().dispose();
		region2.getTexture().dispose();
	}

}
