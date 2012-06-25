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

public class ArcadeLevel8 extends GameScreen {
	private int lives;
	private ArrayList<GameObject> boxes = new ArrayList<GameObject>();
	private ArrayList<GameObject> boxesToDestroy = new ArrayList<GameObject>();
	private ArrayList<GameObject> balls = new ArrayList<GameObject>();
	private Sprite bgSprite;
	private Sprite ballSprite;
	private TextureRegion region;
	private TextureRegion region2;
	private boolean tempBool;
	private boolean tempBool2;
	private ArrayList<ParticleEffectCustom> particleEffects2 = new ArrayList<ParticleEffectCustom>();
	private Body cageBody;
	int i;
	float j;

	public ArcadeLevel8(TheBalanceGame g) {
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
			cageBody = world.createBody(bd);
			cageBody.createFixture(fd);
			// dispose shape
			shape.dispose();
		}
		lives = 2;
		boxes.clear();
		balls.clear();
		boxesToDestroy.clear();
		renderDebug = false;
		
		createSecondaryBox(0, 5);
		createSimpleEnemy(-10, 15, 20, 20);
		balls.add(enemies.get(enemies.size()-1));
		createSimpleEnemy(0, 20, 24, 24);
		balls.add(enemies.get(enemies.size()-1));
		createSimpleEnemy(10, 25, 27, 27);
		balls.add(enemies.get(enemies.size()-1));
		
		generateBoxes();
		
		/*balls.get(0).body.setTransform(0, LEVEL_HEIGHT-1, 0);
		balls.get(0).body.setLinearVelocity(-10, 0);*/
		
		/** LIGHTS **/
		for (GameObject ball : balls) {
			createNewLight(new PointLight(rayHandler, 32, ColorUtil.LIGHT_BLUE, 1, 0, 15));
			tempLight.attachToBody(ball.body, 0, 0);
			tempLight.setXray(true);
			ball.attachedLight = tempLight;
		}
		createNewLight(new DirectionalLight(rayHandler, 32, new Color(0, 0, 0.5f, 0.3f), -45));
		tempLight.setSoft(true);
		tempLight.setXray(true);
		
		/** SPRITES **/
		region = new TextureRegion(new Texture("data/sprites/Bar.png"), 0, 0, 512, 32);
		region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		region2 = new TextureRegion(new Texture("data/sprites/Bar.png"), 245, 35, 95, 60);
		region2.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bgSprite = new Sprite(new Texture("data/sprites/bg.png"));
		bgSprite.setBounds(-WORLD_WIDTH/2, -1, WORLD_WIDTH, WORLD_HEIGHT + 1);
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
		for (i=0; i<3; i++) {
			particleEffect = new ParticleEffectCustom();
			particleEffect.load(Gdx.files.internal("data/particles/boiling"), Gdx.files.internal("data/particles"));
			particleEffects2.add(particleEffect);
		}
	}
	
	@Override
	public void handleGameMechanics(float deltaTime) {
		updateParticles(particleEffects2, deltaTime);
		for (GameObject box : boxesToDestroy) {
			box.used = true;
			//world.destroyBody(box.body);
			box.body.getFixtureList().get(0).setSensor(true);
			/*if (box.attachedLight != null) {
				box.attachedLight.attachToBody(null, 0, 0);
				box.attachedLight.setActive(false);
				box.attachedLight = null;
			}*/
			//box.body.setTransform(box.body.getPosition().x, -10, 0);
		}
		boxesToDestroy.clear();
		for (GameObject obj : balls) {
			if (!obj.used && obj.body != null && obj.body.getPosition().y < secondaryBox.body.getPosition().y) {
				lives--;
				obj.used = true;
				obj.visible = false;
				obj.body.getFixtureList().get(0).setSensor(true);
				obj.attachedLight.attachToBody(null, 0, 0);
				obj.attachedLight.setActive(false);
				obj.attachedLight = null;
				// trigger boiling particles
				tempBool2 = false;
				for (ParticleEffectCustom eff : particleEffects2) {
					if (!eff.isStarted || eff.isComplete()) {
						eff.setPosition(obj.body.getPosition().x, obj.body.getPosition().y);
						eff.start();
						tempBool2 = true;
						break;
					}
				}
				if (!tempBool2) {
					particleEffect = new ParticleEffectCustom();
					particleEffect.load(Gdx.files.internal("data/particles/boiling"), Gdx.files.internal("data/particles"));
					particleEffect.setPosition(obj.body.getPosition().x, obj.body.getPosition().y);
					particleEffect.start();
					particleEffects2.add(particleEffect);
				}
			}
			else if (obj.body.getPosition().y < -5) {
				obj.body.setLinearVelocity(0, 0);
			}
			else if (!obj.used) {
				if (obj.body.getLinearVelocity().x >= 0 && obj.body.getLinearVelocity().x < obj.genericFloat)
					obj.body.setLinearVelocity(obj.genericFloat, obj.body.getLinearVelocity().y);
				else if (obj.body.getLinearVelocity().x < 0 && obj.body.getLinearVelocity().x > -obj.genericFloat)
					obj.body.setLinearVelocity(-obj.genericFloat, obj.body.getLinearVelocity().y);
				else if (obj.body.getLinearVelocity().x >= 0 && obj.body.getLinearVelocity().x > obj.genericFloat3)
					obj.body.setLinearVelocity(obj.genericFloat3, obj.body.getLinearVelocity().y);
				else if (obj.body.getLinearVelocity().x < 0 && obj.body.getLinearVelocity().x < -obj.genericFloat3)
					obj.body.setLinearVelocity(-obj.genericFloat3, obj.body.getLinearVelocity().y);
				
				if (obj.body.getLinearVelocity().y >= 0 && obj.body.getLinearVelocity().y < 5)
					obj.body.setLinearVelocity(obj.body.getLinearVelocity().x, 5);
				else if (obj.body.getLinearVelocity().y < 0 && obj.body.getLinearVelocity().y > -5)
					obj.body.setLinearVelocity(obj.body.getLinearVelocity().x, -5);
			}
		}
		for (GameObject box : boxes) {
			if (!box.used && box.body != null && box.body.getPosition().y < 5) {
				lives = -1;
				break;
			}
		}
		if (lives < 0) {
			gameover = true;
			lives = 2;
			return;
		}
		if (gameTime >= 60) {
			winner = true;
			return;
		}
		else if (boxes.size() > 0 && boxes.get(boxes.size()-1).body.getPosition().y < (LEVEL_HEIGHT-2.5f)) {
			generateBoxes();
			gameTimeCheck = 0;
		}
	}
	
	@Override
	public void handleContactFilter(Body bodyA, Body bodyB) {
		for (GameObject enemy : balls) {
			if (enemy.used)
				continue;
			if (MathUtil.isEqual(bodyB.getPosition(), enemy.body.getPosition())) {
				//synchronized (contactListenerVector) {
					if (MathUtil.isEqual(bodyA.getPosition(), secondaryBox.body.getPosition())) {
						Vector2 contactListenerVector = new Vector2();
						contactListenerVector.set(enemy.body.getPosition());
						contactListenerVector.sub(secondaryBox.body.getPosition());
						enemy.body.setLinearVelocity(enemy.body.getLinearVelocity().rotate(-enemy.body.getLinearVelocity().angle() + contactListenerVector.angle())
								/*.mul(1 + Math.abs(contactListenerVector.angle())*0.0002f)*/);
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
					break;
				//}
			}
		}
		if (!MathUtil.isEqual(bodyA.getPosition(), secondaryBox.body.getPosition()) && !MathUtil.isEqual(bodyA.getPosition(), cageBody.getPosition())) {
			for (GameObject box : boxes) {
				if (MathUtil.isEqual(box.body.getPosition(), bodyA.getPosition())) {
					box.used = true;
					boxesToDestroy.add(box);
					break;
				}
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
			secondaryBox.body.setTransform(tempFloat, secondaryBox.body.getPosition().y, 0);
			lastAcceleratorY = tempAcceleratorY;
		}
		else {
			secondaryBox.body.setTransform(x, secondaryBox.body.getPosition().y, 0);
		}
	}
	
	public void handleLevelRendering(float deltaTime) {
		// Background
		batch.disableBlending();
		bgSprite.draw(batch);
		batch.enableBlending();
		// Bar
		batch.draw(region, secondaryBox.body.getPosition().x - 3, secondaryBox.body.getPosition().y - 0.6f, // the bottom left corner of the box, unrotated
				3f, 0.2f, // the rotation center relative to the bottom left corner of the box
				6, 1.2f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				0); // the rotation angle
		// Balls
		for (GameObject enemy : enemies) {
			if (!enemy.visible)
				continue;
			ballSprite.setRotation((float)Math.toDegrees(enemy.body.getAngle()));
			ballSprite.setBounds(enemy.body.getPosition().x - 0.5f, enemy.body.getPosition().y - 0.5f, 1f, 1f);
			ballSprite.draw(batch);
		}
		// Boxes
		for (GameObject box : boxes) {
			if (box.used)
				continue;
			batch.draw(region2, box.body.getPosition().x - 1.8f, box.body.getPosition().y - 1.2f, // the bottom left corner of the box, unrotated
					0, 0, // the rotation center relative to the bottom left corner of the box
					3.5f, 2.4f, // the width and height of the box
					1, 1, // the scale on the x- and y-axis
					0); // the rotation angle
		}
		renderParticles(particleEffects2);
	}
	
	@Override
	public void displayLevelUI() {
		font.draw(batch,  "LIVES: " + lives, 5, 40);
		font.draw(batch,  "LEVEL 8", Gdx.graphics.getWidth()/2, 20);
	}
	
	public void handleLevelDispose() {
		bgSprite.getTexture().dispose();
		ballSprite.getTexture().dispose();
		region.getTexture().dispose();
		region2.getTexture().dispose();
		disposeParticles(particleEffects2);
	}
	
	private void generateBoxes() {
		for (i=-20; i<21; i+=4) {
			newPiece = createSimplePiece(i, LEVEL_HEIGHT, 14, 0, 0, 0, false);
			newPiece.body.setLinearVelocity(0, -0.5f);
			boxes.add(new GameObject(newPiece, newPiece.body));
			/*createNewLight(new PointLight(rayHandler, 16, Color.MAGENTA, 2f, 0, 15));
			tempLight.attachToBody(boxes.get(boxes.size()-1).body, 0, 0);
			tempLight.setXray(true);
			boxes.get(boxes.size()-1).attachedLight = tempLight;*/
		}
	}

}
