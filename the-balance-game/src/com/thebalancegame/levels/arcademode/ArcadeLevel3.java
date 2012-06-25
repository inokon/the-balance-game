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
import com.thebalancegame.graphics.EffectLight;
import com.thebalancegame.graphics.ParticleEffectCustom;
import com.thebalancegame.utils.ColorUtil;
import com.thebalancegame.utils.MathUtil;

public class ArcadeLevel3 extends GameScreen {
	private int lives;
	private GameObject leftLever1;
	private GameObject leftLever2;
	private GameObject leftLeverWeight;
	private GameObject rightLever1;
	private GameObject rightLever2;
	private GameObject rightLeverWeight;
	private Sprite bgSprite;
	private Sprite mainBallSprite;
	private Sprite ballSprite;
	private TextureRegion region;
	private TextureRegion region2;
	private boolean tempBool;
	private boolean tempBool2;
	private ArrayList<ParticleEffectCustom> particleEffects2 = new ArrayList<ParticleEffectCustom>();
	private int i;

	public ArcadeLevel3(TheBalanceGame g) {
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
		renderDebug = false;
		
		createSecondaryBox(0, 5);
		
		// Left Lever
		newPiece = createSimplePiece(-19, 15, 7, 0.2f, 0, 0, false);
		leftLever1 = new GameObject(newPiece, newPiece.body);
		newPiece = createSimplePiece(-19, 16.8f, 7, 0, 0, 0, false);
		leftLever2 = new GameObject(newPiece, newPiece.body);
		newPiece = createSimplePiece(-19, 16.2f, 8, 0.3f, 0, 1, false);
		leftLeverWeight = new GameObject(newPiece, newPiece.body);
		
		// Right Lever
		newPiece = createSimplePiece(19, 15, 7, 0.2f, 0, 0, false);
		rightLever1 = new GameObject(newPiece, newPiece.body);
		newPiece = createSimplePiece(19, 16.8f, 7, 0, 0, 0, false);
		rightLever2 = new GameObject(newPiece, newPiece.body);
		newPiece = createSimplePiece(19, 16.2f, 8, 0.3f, 0, 1, false);
		rightLeverWeight = new GameObject(newPiece, newPiece.body);
		
		createSimpleEnemy(-2, 30, 10, 10);
		
		/** LIGHTS **/
		createNewLight(new PointLight(rayHandler, 32, ColorUtil.LIGHT_BLUE, 1, 0, 15));
		tempLight.attachToBody(enemies.get(0).body, 0, 0);
		tempLight.setXray(true);
		enemies.get(enemies.size()-1).attachedLight = tempLight;
		createNewLight(new PointLight(rayHandler, 32, Color.MAGENTA, 1.5f, 0, 15));
		tempLight.attachToBody(leftLeverWeight.body, 0, 0);
		tempLight.setXray(true);
		createNewLight(new PointLight(rayHandler, 32, Color.MAGENTA, 1.5f, 0, 15));
		tempLight.attachToBody(rightLeverWeight.body, 0, 0);
		tempLight.setXray(true);
		createNewLight(new DirectionalLight(rayHandler, 32, new Color(0, 0, 0.5f, 0.3f), -45));
		tempLight.setSoft(true);
		tempLight.setXray(true);
		
		/** SPRITES **/
		region = new TextureRegion(new Texture("data/sprites/Bar.png"), 0, 0, 512, 32);
		region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		region2 = new TextureRegion(new Texture("data/sprites/Bar.png"), 0, 35, 43, 43);
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
		for (i=0; i<6; i++) {
			particleEffect = new ParticleEffectCustom();
			particleEffect.load(Gdx.files.internal("data/particles/boiling"), Gdx.files.internal("data/particles"));
			particleEffects2.add(particleEffect);
		}
	}
	
	@Override
	public void handleGameMechanics(float deltaTime) {
		updateParticles(particleEffects2, deltaTime);
		leftLeverWeight.body.setAwake(true);
		rightLeverWeight.body.setAwake(true);
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
					tempBool2 = false;
					for (ParticleEffectCustom eff : particleEffects2) {
						if (!eff.isStarted || eff.isComplete()) {
							eff.setPosition(enemy.body.getPosition().x, enemy.body.getPosition().y);
							eff.start();
							tempBool2 = true;
							break;
						}
					}
					if (!tempBool2) {
						particleEffect = new ParticleEffectCustom();
						particleEffect.load(Gdx.files.internal("data/particles/boiling"), Gdx.files.internal("data/particles"));
						particleEffect.setPosition(enemy.body.getPosition().x, enemy.body.getPosition().y);
						particleEffect.start();
						particleEffects2.add(particleEffect);
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
		if (leftLeverWeight.body.getPosition().y < 10 || rightLeverWeight.body.getPosition().y < 10) {
			leftLeverWeight.body.getFixtureList().get(0).setSensor(true);
			rightLeverWeight.body.getFixtureList().get(0).setSensor(true);
			gameover = true;
			return;
		}
		if (gameTime >= 60) {
			winner = true;
			return;
		}
		else if (gameTimeCheck >= 10) {
			createSimpleEnemy(rnd.nextFloat() * LEVEL_WIDTH, 20 + rnd.nextFloat() * (LEVEL_HEIGHT - 25), 10 + enemies.size()*5, 10 + enemies.size()*5);
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
		if (MathUtil.isEqual(bodyA.getPosition(), leftLeverWeight.body.getPosition()) || MathUtil.isEqual(bodyA.getPosition(), rightLeverWeight.body.getPosition())
				|| MathUtil.isEqual(bodyB.getPosition(), leftLeverWeight.body.getPosition()) || MathUtil.isEqual(bodyB.getPosition(), rightLeverWeight.body.getPosition()))
			return;
		//synchronized (contactListenerVector) {
			for (GameObject enemy : enemies) {
				if (enemy.used)
					continue;
				if (MathUtil.isEqual(bodyB.getPosition(), enemy.body.getPosition())) {
					if (MathUtil.isEqual(bodyA.getPosition(), secondaryBox.body.getPosition())) {
						Vector2 contactListenerVector = new Vector2();
						contactListenerVector.set(enemy.body.getPosition());
						contactListenerVector.sub(secondaryBox.body.getPosition());
						enemy.body.setLinearVelocity(enemy.body.getLinearVelocity().rotate(-enemy.body.getLinearVelocity().angle() + contactListenerVector.angle())
								.mul(1 + Math.abs(contactListenerVector.angle())*0.0002f));
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
		//}
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
			secondaryBox.body.setTransform(tempFloat, secondaryBox.body.getPosition().y, 0);
			tempFloat *= 1.4f;
			leftLever1.body.setTransform(leftLever1.body.getPosition(), (float)Math.toRadians(tempFloat));
			leftLever2.body.setTransform(leftLever2.body.getPosition(), (float)Math.toRadians(tempFloat));
			rightLever1.body.setTransform(rightLever1.body.getPosition(), (float)Math.toRadians(tempFloat));
			rightLever2.body.setTransform(rightLever2.body.getPosition(), (float)Math.toRadians(tempFloat));
			lastAcceleratorY = tempAcceleratorY;
		}
		else {
			secondaryBox.body.setTransform(x, secondaryBox.body.getPosition().y, 0);
			leftLever1.body.setTransform(leftLever1.body.getPosition(), (float)Math.toRadians(x * 1.4f));
			leftLever2.body.setTransform(leftLever2.body.getPosition(), (float)Math.toRadians(x * 1.4f));
			rightLever1.body.setTransform(rightLever1.body.getPosition(), (float)Math.toRadians(x * 1.4f));
			rightLever2.body.setTransform(rightLever2.body.getPosition(), (float)Math.toRadians(x * 1.4f));
		}
	}
	
	public void handleLevelRendering(float deltaTime) {
		// Background
		batch.disableBlending();
		bgSprite.draw(batch);
		batch.enableBlending();
		// Boxes
		batch.draw(region, secondaryBox.body.getPosition().x - 3, secondaryBox.body.getPosition().y - 0.6f, // the bottom left corner of the box, unrotated
				3f, 0.2f, // the rotation center relative to the bottom left corner of the box
				6, 1.2f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				0); // the rotation angle
		batch.draw(region, leftLever1.body.getPosition().x - 3, leftLever1.body.getPosition().y - 0.6f, // the bottom left corner of the box, unrotated
				3f, 0.6f, // the rotation center relative to the bottom left corner of the box
				6, 1.2f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				(float)Math.toDegrees(leftLever1.body.getAngle())); // the rotation angle
		batch.draw(region, leftLever2.body.getPosition().x - 3, leftLever2.body.getPosition().y - 0.6f, // the bottom left corner of the box, unrotated
				3f, 0.6f, // the rotation center relative to the bottom left corner of the box
				6, 1.2f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				(float)Math.toDegrees(leftLever2.body.getAngle())); // the rotation angle
		batch.draw(region, rightLever1.body.getPosition().x - 3, rightLever1.body.getPosition().y - 0.6f, // the bottom left corner of the box, unrotated
				3f, 0.6f, // the rotation center relative to the bottom left corner of the box
				6, 1.2f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				(float)Math.toDegrees(rightLever1.body.getAngle())); // the rotation angle
		batch.draw(region, rightLever2.body.getPosition().x - 3, rightLever2.body.getPosition().y - 0.6f, // the bottom left corner of the box, unrotated
				3f, 0.6f, // the rotation center relative to the bottom left corner of the box
				6, 1.2f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				(float)Math.toDegrees(rightLever2.body.getAngle())); // the rotation angle
		batch.draw(region2, leftLeverWeight.body.getPosition().x - 0.5f, leftLeverWeight.body.getPosition().y - 0.4f, // the bottom left corner of the box, unrotated
				0.5f, 0.5f, // the rotation center relative to the bottom left corner of the box
				1, 1.2f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				(float)Math.toDegrees(leftLeverWeight.body.getAngle())); // the rotation angle
		batch.draw(region2, rightLeverWeight.body.getPosition().x - 0.5f, rightLeverWeight.body.getPosition().y - 0.4f, // the bottom left corner of the box, unrotated
				0.5f, 0.5f, // the rotation center relative to the bottom left corner of the box
				1, 1.2f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				(float)Math.toDegrees(rightLeverWeight.body.getAngle())); // the rotation angle
		// Balls
		for (GameObject enemy : enemies) {
			if (!enemy.visible)
				continue;
			ballSprite.setRotation((float)Math.toDegrees(enemy.body.getAngle()));
			ballSprite.setBounds(enemy.body.getPosition().x - 0.5f, enemy.body.getPosition().y - 0.5f, 1f, 1f);
			ballSprite.draw(batch);
		}
		renderParticles(particleEffects2);
	}
	
	@Override
	public void displayLevelUI() {
		font.draw(batch,  "LIVES: " + lives, 5, 40);
		font.draw(batch,  "LEVEL 3", Gdx.graphics.getWidth()/2, 20);
	}
	
	public void handleLevelDispose() {
		bgSprite.getTexture().dispose();
		mainBallSprite.getTexture().dispose();
		ballSprite.getTexture().dispose();
		region.getTexture().dispose();
		region2.getTexture().dispose();
		disposeParticles(particleEffects2);
	}

}
