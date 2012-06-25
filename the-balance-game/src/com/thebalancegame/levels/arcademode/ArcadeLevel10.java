package com.thebalancegame.levels.arcademode;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.thebalancegame.GameScreen;
import com.thebalancegame.TheBalanceGame;
import com.thebalancegame.gameobjects.GameObject;
import com.thebalancegame.gameobjects.Piece;

public class ArcadeLevel10 extends GameScreen {
	private ArrayList<GameObject> blackHoles = new ArrayList<GameObject>();
	private ArrayList<GameObject> planets = new ArrayList<GameObject>();
	private ArrayList<GameObject> whiteHoles = new ArrayList<GameObject>();
	private GameObject newObj;
	private GameObject mainBall;
	private int lives;
	private Body body;
	private Vector2 force = new Vector2();
	private Vector2 accelForce = new Vector2();
	private float tempAccelX;
	private float tempAccelY;
	private BodyDef bd = new BodyDef();
	private CircleShape shape;
	int i;
	int j;

	public ArcadeLevel10(TheBalanceGame g) {
		super(g);
		LEVEL_WIDTH = 24;
		LEVEL_HEIGHT = 350;
	}
	
	@Override
	public void createWorld (World world) {
		// Create ground cage
		{
			ChainShape shape = new ChainShape();
			shape.createLoop(new Vector2[] {new Vector2(LEVEL_WIDTH, 0), new Vector2(LEVEL_WIDTH, LEVEL_HEIGHT - 1), new Vector2(-LEVEL_WIDTH, LEVEL_HEIGHT - 1), new Vector2(-LEVEL_WIDTH, 0)});
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
		cameraMoving = true;
		planets.clear();
		blackHoles.clear();
		whiteHoles.clear();
		
		shape = new CircleShape();

		bd.type = BodyType.KinematicBody;
		fd.friction = 0.4f;
		fd.restitution = 0.0f;
		fd.isSensor = false;
		
		newPiece = createSimplePiece(0, 1, 4, 0.2f, 0.2f, 0, false);
		mainBall = new GameObject(newPiece, newPiece.body);
		
		/*float lastRadius = 0;
		for (i=-30; i<30; i+=lastRadius*3.5f) {
			for (j=0; j<35; j+=lastRadius*3.5f) {
				if (mainBall.body.getPosition().dst2(i, j) < 50)
					continue;
				shape.setRadius(3 + rnd.nextFloat() * 3);
				bd.position.set(i + rnd.nextFloat() * 2, j + rnd.nextFloat() * 2);
				fd.shape = shape;
				body = world.createBody(bd);
				body.createFixture(fd);
				newPiece = new Piece(bd.type);
				blackHoles.add(new GameObject(newPiece, body, shape.getRadius()));
				lastRadius = shape.getRadius();
				//System.out.println("radius: " + lastRadius);
			}
		}*/
		/* 			O			*/
		createNewHole(-15, 15, 15, 15, 5, 1, 5, blackHoles);
		/* 	O				 O	*/
		createNewHole(-11, 27, -4, 27, 4, 1, 3, blackHoles);
		createNewHole(11, 27, 4, 27, 4, 1, 3, blackHoles);
		/* o				  o	*/
		createNewHole(-19, 30, 5, blackHoles);
		createNewHole(19, 30, 5, blackHoles);
		/* 			O			*/
		createNewHole(0, 40, 0, 35, 5, 2, 2, whiteHoles);
		/* o	O		O	  o	*/
		createNewHole(-19, 50, 3.5f, blackHoles);
		createNewHole(19, 50, 3.5f, blackHoles);
		createNewHole(-6, 50, 6, blackHoles);
		createNewHole(6, 50, 6, blackHoles);
		/*  O	  O	 	O	 O	*/
		createNewHole(-18, 70, -18, 95, 5, 2, 8, blackHoles);
		createNewHole(-6, 70, -8, 95, 5, 2, 7, blackHoles);
		createNewHole(6, 70, 8, 95, 5, 2, 9, blackHoles);
		createNewHole(18, 70, 18, 95, 5, 2, 10, blackHoles);
		/* barrier */
		createNewHole(-21, 110, 3, blackHoles);
		createNewHole(-15, 110, 3, blackHoles);
		createNewHole(21, 110, 3, blackHoles);
		createNewHole(15, 110, 3, blackHoles);
		/* white snake */
		createNewHole(-8, 110, 4, whiteHoles);
		createNewHole(8, 110, 4, whiteHoles);
		createNewHole(-7, 117, 4, whiteHoles);
		createNewHole(8, 117, 4, whiteHoles);
		createNewHole(-6, 124, 4, whiteHoles);
		createNewHole(9, 124, 4, whiteHoles);
		createNewHole(-5, 131, 4, whiteHoles);
		createNewHole(10, 131, 4, whiteHoles);
		createNewHole(-4, 138, 4, whiteHoles);
		createNewHole(13, 137, 4, whiteHoles);
		createNewHole(-1, 144, 4, whiteHoles);
		createNewHole(5, 148, 4, whiteHoles);
		createNewHole(21, 138, 4, whiteHoles);
		/* slide show */
		createNewHole(21, 160, -21, 160, 5, 1, 15, blackHoles);
		createNewHole(-21, 170, 21, 170, 5, 1, 20, blackHoles);
		createNewHole(21, 180, -21, 180, 5, 1, 12, blackHoles);
		createNewHole(-21, 190, 21, 190, 5, 1, 17, blackHoles);
		createNewHole(15, 205, -15, 205, 4, 1, 10, blackHoles);
		/* huge */
		createNewHole(0, 250, 15, planets);
		
		shape.dispose();
	}
	
	@Override
	public void handleGameMechanics(float deltaTime) {
		for (GameObject hole : blackHoles) {
			if (hole.body.getPosition().dst2(mainBall.body.getPosition()) <= hole.radius*hole.radius*4f) {
				force.set(hole.body.getPosition());
				force.sub(mainBall.body.getPosition());
				force.mul(hole.radius*3);
				mainBall.body.applyForceToCenter(force);
			}
		}
		for (GameObject planet : planets) {
			if (planet.body.getPosition().dst2(mainBall.body.getPosition()) <= planet.radius*planet.radius*4f) {
				force.set(planet.body.getPosition());
				force.sub(mainBall.body.getPosition());
				force.mul(planet.radius*3);
				mainBall.body.applyForceToCenter(force);
			}
		}
		for (GameObject hole : whiteHoles) {
			if (hole.body.getPosition().dst2(mainBall.body.getPosition()) <= hole.radius*hole.radius*4f) {
				force.set(mainBall.body.getPosition());
				force.sub(hole.body.getPosition());
				//force.nor();
				force.mul(force.len2());
				mainBall.body.applyForceToCenter(force);
			}
		}
		if (lives < 0) {
			gameover = true;
			lives = 3;
			return;
		}
		if (gameTime >= 60 || mainBall.body.getPosition().y > LEVEL_HEIGHT-5) {
			winner = true;
			return;
		}
		else if (gameTimeCheck >= 5) {
			
			gameTimeCheck = 0;
		}
		handleObjectsMovement(blackHoles, deltaTime);
		handleObjectsMovement(whiteHoles, deltaTime);
	}
	
	@Override
	public void handleBalanceInput(float x, float y, boolean isAccel) {
		if (isAccel) {
			tempAcceleratorY = y;
			tempAcceleratorY = tempAcceleratorY * ACCELERATOR_FILTER + lastAcceleratorY * (1 - ACCELERATOR_FILTER);
			tempAccelY = tempAcceleratorY;
			tempAcceleratorX = x;
			tempAcceleratorX = tempAcceleratorX * ACCELERATOR_FILTER + lastAcceleratorX * (1 - ACCELERATOR_FILTER);
			tempAccelX = tempAcceleratorX;
			if (tempAccelY > 3f)
				tempAccelY = 3f;
			else if (tempAccelY < -3f)
				tempAccelY = -3f;
			if (tempAccelX > 3f)
				tempAccelX = 3f;
			else if (tempAccelX < -3f)
				tempAccelX = -3f;
			accelForce.x = tempAccelY * 300;
			accelForce.y = -tempAccelX * 300;
			mainBall.body.applyForceToCenter(accelForce);
			lastAcceleratorY = tempAcceleratorY;
			lastAcceleratorX = tempAcceleratorX;
		}
		else {
			accelForce.set(x, y);
			accelForce.sub(mainBall.body.getPosition());
			accelForce.mul(40);
			mainBall.body.applyForceToCenter(accelForce);
		}
	}
	
	private void createNewHole(float x, float y, float radius, ArrayList<GameObject> list) {
		createNewHole(x, y, 0, 0, radius, 0, 0, list);
	}
	
	private void createNewHole(float startX, float startY, float endX, float endY, float radius, int movingType, float speed, ArrayList<GameObject> list) {
		shape.setRadius(radius);
		bd.position.set(startX, startY);
		fd.shape = shape;
		body = world.createBody(bd);
		body.createFixture(fd);
		newPiece = new Piece(bd.type);
		newObj = new GameObject(newPiece, body);
		newObj.genericBoolean = true;
		newObj.radius = radius;
		newObj.genericInteger = movingType;
		if (movingType == 1) {
			if (startX < endX)
				newObj.genericFloat = 1f;
			else
				newObj.genericFloat = -1f;
		}
		else if (movingType == 2) {
			if (startY < endY)
				newObj.genericFloat = 1f;
			else
				newObj.genericFloat = -1f;
		}
		newObj.genericFloat2 = speed;
		newObj.endX = endX;
		newObj.endY = endY;
		list.add(newObj);
	}
	
	private void handleObjectsMovement(ArrayList<GameObject> list, float deltaTime) {
		for (GameObject obj : list) {
			if (obj.genericBoolean) {
				// movement on x axis
				if (obj.genericInteger == 1) {
					obj.body.setTransform(obj.body.getPosition().x + deltaTime*obj.genericFloat*obj.genericFloat2, obj.body.getPosition().y, 0);
					if (!obj.genericBoolean2 && Math.abs(obj.body.getPosition().x - obj.endX) < 0.5f) {
						obj.genericFloat2 *= -1;
						obj.genericBoolean2 = true;
					}
					if (obj.genericBoolean2) {
						if (Math.abs(obj.body.getPosition().x - obj.startX) < 0.5f) {
							obj.genericFloat2 *= -1;
							obj.genericBoolean2 = false;							
						}
					}
				}
				// movement on y axis
				else if (obj.genericInteger == 2) {
					obj.body.setTransform(obj.body.getPosition().x, obj.body.getPosition().y + deltaTime*obj.genericFloat*obj.genericFloat2, 0);
					if (!obj.genericBoolean2 && Math.abs(obj.body.getPosition().y - obj.endY) < 0.5f) {
						obj.genericFloat2 *= -1;
						obj.genericBoolean2 = true;
					}
					if (obj.genericBoolean2) {
						if (Math.abs(obj.body.getPosition().y - obj.startY) < 0.5f) {
							obj.genericFloat2 *= -1;
							obj.genericBoolean2 = false;							
						}
					}
				}
			}
		}
	}
	
	@Override
	public void handleContactFilter(Body bodyA, Body bodyB) {
		if (bodyA == mainBall.body) {
			for (GameObject obj: whiteHoles) {
				if (obj.body == bodyB)
					return;
			}
			for (GameObject obj: planets) {
				if (obj.body == bodyB)
					return;
			}
			gameover = true;
		}
	}
	
	@Override
	public void setCameraLimits() {
		if (LEVEL_WIDTH > WORLD_WIDTH/2) {
			CAMERA_RIGHT_LIMIT = LEVEL_WIDTH/3;
			CAMERA_LEFT_LIMIT = -LEVEL_WIDTH/3;
		}
		else {
			CAMERA_RIGHT_LIMIT = 0;
			CAMERA_LEFT_LIMIT = 0;
		}
    	CAMERA_UP_LIMIT = LEVEL_HEIGHT - 15;
    	CAMERA_DOWN_LIMIT = 15;
	}
	
	@Override
	public void handleCameraMovement(float deltaTime) {
		tmp.set(mainBall.body.getPosition().x, mainBall.body.getPosition().y);
		tmp.sub(camera.position.x, camera.position.y);
		tmp.mul(deltaTime * 10);
		updateCameraPosition(camera.position.x + tmp.x, camera.position.y + tmp.y);
	}
	
	@Override
	public void displayLevelUI() {
		//font.draw(batch,  "LIVES: " + lives, 5, 40);
		font.draw(batch,  "LEVEL 10", Gdx.graphics.getWidth()/2, 20);
		//font.drawMultiLine(batch, "accelX: " + Gdx.input.getAccelerometerX() + "\n" + "accelY: " + Gdx.input.getAccelerometerY() + "\n", 0, 50);
	}

}
