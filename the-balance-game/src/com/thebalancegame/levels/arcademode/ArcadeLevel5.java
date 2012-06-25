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
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.thebalancegame.GameScreen;
import com.thebalancegame.TheBalanceGame;
import com.thebalancegame.gameobjects.GameObject;

public class ArcadeLevel5 extends GameScreen {
	private int lives;
	private float counter;
	private ArrayList<GameObject> balls = new ArrayList<GameObject>();
	private ArrayList<Vector2> baskets = new ArrayList<Vector2>();
	private int[] basketIndex;
	private TextureRegion region;
	private Sprite redBall;
	private Sprite greenBall;
	private Sprite blueBall;
	private Sprite violetBall;
	private Sprite bgSprite;
	private Sprite sprite;
	private ParticleEffectCustom eff;
	int i;

	public ArcadeLevel5(TheBalanceGame g) {
		super(g);
		LEVEL_WIDTH = 24;
		LEVEL_HEIGHT = 32;
	}
	
	@Override
	public void createWorld (World world) {
		lives = 3;
		gameTimeCheck = 0;
		counter = 0;
		renderDebug = false;
		balls.clear();
		baskets.clear();
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.2f, 4);
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody;
		fd.shape = shape;
		fd.friction = 0.2f;
		fd.restitution = 0.0f;
		
		bd.position.set(-21, 0);
		world.createBody(bd).createFixture(fd);
		bd.position.set(-17, 0);
		world.createBody(bd).createFixture(fd);
		baskets.add(new Vector2(-19, 3));

		bd.position.set(-8.5f, 0);
		world.createBody(bd).createFixture(fd);
		bd.position.set(-4.5f, 0);
		world.createBody(bd).createFixture(fd);
		baskets.add(new Vector2(-6.5f, 3));

		bd.position.set(4.5f, 0);
		world.createBody(bd).createFixture(fd);
		bd.position.set(8.5f, 0);
		world.createBody(bd).createFixture(fd);
		baskets.add(new Vector2(6.5f, 3));

		bd.position.set(17, 0);
		world.createBody(bd).createFixture(fd);
		bd.position.set(21, 0);
		world.createBody(bd).createFixture(fd);
		baskets.add(new Vector2(19, 3));

		shape.dispose();
		
		basketIndex = new int[4];
		basketIndex[0] = 0;
		basketIndex[1] = 1;
		basketIndex[2] = 2;
		basketIndex[3] = 3;
		
		newPiece = createSimplePiece(rnd.nextFloat() * -LEVEL_WIDTH/2 + rnd.nextFloat() * LEVEL_WIDTH/2, LEVEL_HEIGHT + 1, 4, 0.7f, 0.1f, 1, false);
		balls.add(new GameObject(newPiece, newPiece.body));
		balls.get(balls.size()-1).genericInteger = basketIndex[rnd.nextInt(4)];
		
		/** SPRITES **/
		region = new TextureRegion(new Texture("data/sprites/Bar.png"), 370, 35, 110, 150);
		region.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		bgSprite = new Sprite(new Texture("data/sprites/bg.png"));
		bgSprite.setBounds(-WORLD_WIDTH/2, -1, WORLD_WIDTH, WORLD_HEIGHT + 1);
		redBall = new Sprite(new Texture("data/sprites/redball.png"));
		redBall.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		greenBall = new Sprite(new Texture("data/sprites/greenball.png"));
		greenBall.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		blueBall = new Sprite(new Texture("data/sprites/blueball.png"));
		blueBall.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		violetBall = new Sprite(new Texture("data/sprites/violetball.png"));
		violetBall.getTexture().setFilter(TextureFilter.Linear, TextureFilter.Linear);
		
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
		particleEffect.load(Gdx.files.internal("data/particles/gold_sparks"), Gdx.files.internal("data/particles"));
		particleEffects.add(particleEffect);
		particleEffect = new ParticleEffectCustom();
		particleEffect.load(Gdx.files.internal("data/particles/red_fuse"), Gdx.files.internal("data/particles"));
		particleEffects.add(particleEffect);
	}
	
	@Override
	public void handleGameMechanics(float deltaTime) {
		for (GameObject ball : balls) {
			if (!ball.used && (ball.body.getPosition().x < (-LEVEL_WIDTH-10) || ball.body.getPosition().x > (LEVEL_WIDTH+10))) {
				lives--;
				ball.used = true;
			}
			else if (!ball.used && ball.body.getPosition().y < 3) {
				for (i=0; i<baskets.size(); i++) {
					if (Math.abs(baskets.get(i).x - ball.body.getPosition().x) <= 1.5f) {
						if (ball.genericInteger == basketIndex[i]) {
							ball.used = true;
							eff = particleEffects.get(1);
							if (!eff.isStarted || eff.isComplete()) {
								eff.setPosition(baskets.get(i).x, baskets.get(i).y - 2);
								eff.start();
							}
							break;
						}
						else {
							eff = particleEffects.get(2);
							if (!eff.isStarted || eff.isComplete()) {
								eff.setPosition(baskets.get(i).x, baskets.get(i).y - 2);
								eff.start();
							}
						}
					}
				}
				if (!ball.used) {
					lives--;
					ball.used = true;
				}
			}
			else if (ball.visible && ball.body.getPosition().y < -5) {
				ball.visible = false;
				ball.body.getFixtureList().get(0).setSensor(true);
			}
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
		else if (gameTimeCheck >= (4 + rnd.nextFloat()*3 - counter)) {
			newPiece = createSimplePiece(rnd.nextFloat() * -LEVEL_WIDTH/2 + rnd.nextFloat() * LEVEL_WIDTH/2, LEVEL_HEIGHT + 1, 4, 0.7f, 0.1f, 1, false);
			balls.add(new GameObject(newPiece, newPiece.body));
			balls.get(balls.size()-1).genericInteger = basketIndex[rnd.nextInt(4)];
			counter += 0.01f;
			gameTimeCheck = 0;
		}
	}
	
	@Override
	public void handleBalanceInput(float x, float y, boolean isAccel) {
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
			gravityVector.set(tempFloat * 2f, -5f -(counter*40));
			world.setGravity(gravityVector);
			lastAcceleratorY = tempAcceleratorY;
				
		}
		else {
			gravityVector.set(x * 2f, -5f -(counter*40));
			world.setGravity(gravityVector);
		}
	}
	
	public void handleLevelRendering(float deltaTime) {
		// Background
		batch.disableBlending();
		bgSprite.draw(batch);
		batch.enableBlending();
		for (GameObject ball : balls) {
			if (!ball.visible)
				continue;
			// Different colors
			switch (ball.genericInteger) {
				case 0:
					sprite = redBall;
					break;
				case 1:
					sprite = greenBall;
					break;
				case 2:
					sprite = blueBall;
					break;
				case 3:
					sprite = violetBall;
					break;
			}
			sprite.setBounds(ball.body.getPosition().x - 1f, ball.body.getPosition().y - 1f, 2f, 2f);
			sprite.draw(batch);
		}
		for (Vector2 basket : baskets) {
			batch.draw(region, basket.x - 3.1f, basket.y - 4.8f, // the bottom left corner of the box, unrotated
				0, 0, // the rotation center relative to the bottom left corner of the box
				6f, 6.1f, // the width and height of the box
				1, 1, // the scale on the x- and y-axis
				0); // the rotation angle
		}
		redBall.setBounds(baskets.get(0).x - 1f, baskets.get(0).y - 3f, 2f, 2f);
		redBall.draw(batch);
		greenBall.setBounds(baskets.get(1).x - 1f, baskets.get(1).y - 3f, 2f, 2f);
		greenBall.draw(batch);
		blueBall.setBounds(baskets.get(2).x - 1f, baskets.get(2).y - 3f, 2f, 2f);
		blueBall.draw(batch);
		violetBall.setBounds(baskets.get(3).x - 1f, baskets.get(3).y - 3f, 2f, 2f);
		violetBall.draw(batch);
	}
	
	@Override
	public void displayLevelUI() {
		font.draw(batch,  "LIVES: " + lives, 5, 40);
		font.draw(batch,  "LEVEL 5", Gdx.graphics.getWidth()/2, 20);
	}
}
