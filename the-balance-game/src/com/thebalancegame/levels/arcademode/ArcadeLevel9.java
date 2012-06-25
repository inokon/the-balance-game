package com.thebalancegame.levels.arcademode;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.thebalancegame.GameScreen;
import com.thebalancegame.TheBalanceGame;
import com.thebalancegame.gameobjects.GameObject;
import com.thebalancegame.gameobjects.Piece;

public class ArcadeLevel9 extends GameScreen {
	private static final int radiusStep = 30;
	private float planetRadianVelocity;
	private float wheelRadius;
	private float wheelCenterY;
	private float nextRadianStep;
	private int lives;
	private Vector2 force = new Vector2(0, 165000);
	private Vector2 doubleJumpForce = new Vector2(0, 130000);
	private GameObject wheel;
	private GameObject mainBall;
	private ArrayList<Body> obstacles = new ArrayList<Body>();
	private boolean canDoubleJump;
	private Body body;
	private int i;

	public ArcadeLevel9(TheBalanceGame g) {
		super(g);
		LEVEL_WIDTH = 24;
		LEVEL_HEIGHT = 32;
	}
	
	@Override
	public void createWorld (World world) {
		lives = 3;
		obstacles.clear();
		canDoubleJump = false;
		allowOutOfWorldDestruction = false;
		planetRadianVelocity = -20;

		Piece planet = createSimplePiece(0, -15, 15, 0, 0, 0, false);
		planet.body.setType(BodyType.KinematicBody);
		wheel = new GameObject(planet, planet.body);
		
		PolygonShape shape = new PolygonShape();
		BodyDef bd = new BodyDef();
		wheelRadius = planet.body.getFixtureList().get(0).getShape().getRadius();
		wheelCenterY = planet.body.getPosition().y;

		fd.friction = 0.4f;
		fd.restitution = 1.0f;
		fd.density = 1000;
		fd.isSensor = false;
		bd.type = BodyType.KinematicBody;
		bd.gravityScale = 0;
		
		float lastShapeWidth = 0;
		float lastShapeHeight = 0;
		for (i=80; i>=-240; i-= (radiusStep - rnd.nextInt(5) + (int)lastShapeWidth*2 + (int)lastShapeHeight/2f)) {
			lastShapeWidth = 0.2f + rnd.nextFloat()*2f;
			lastShapeHeight = 2 + rnd.nextFloat()*4f + rnd.nextFloat()*3f;
			shape.setAsBox(lastShapeHeight, lastShapeWidth);
			fd.shape = shape;
			bd.position.set(wheelRadius * (float)Math.cos(Math.toRadians(i)), wheelCenterY + wheelRadius * (float)Math.sin(Math.toRadians(i)));
			bd.angle = (float)Math.toRadians(i);
			body = world.createBody(bd);
			body.createFixture(fd);
			obstacles.add(body);
		}
		
		RevoluteJointDef rj = new RevoluteJointDef();
		rj.enableMotor = true;
		rj.motorSpeed = 0.5f;
		rj.maxMotorTorque = 100000000;
		
		Piece ball = createSimplePiece(0, 23, 11, 0, 0, 6, false);
		ball.body.setType(BodyType.DynamicBody);
		Piece ballSustain = createSimplePiece(0, 23, 5, 0, 0, 1, true);
		ballSustain.body.setType(BodyType.DynamicBody);
		mainBall = new GameObject(ball, ball.body);
		rj.initialize(ball.body, ballSustain.body, ballSustain.body.getPosition());
		rj.motorSpeed = -100;
		rj.maxMotorTorque = 100000000;
		world.createJoint(rj);
	}
	
	@Override
	public void handleGameMechanics(float deltaTime) {
		if (mainBall.body.getLinearVelocity().x < 0.5f)
			mainBall.body.setLinearVelocity(0, mainBall.body.getLinearVelocity().y);
		if (mainBall.body.getPosition().y < 0) {
			gameover = true;
			lives = 3;
			return;
		}
		nextRadianStep = (float)Math.toRadians(planetRadianVelocity * deltaTime);
		wheel.body.setTransform(wheel.body.getPosition(), wheel.body.getAngle() + nextRadianStep);
		for (Body obstacle : obstacles) {
			obstacle.setTransform(wheelRadius * (float)Math.cos(obstacle.getAngle() + nextRadianStep), 
					wheelCenterY + wheelRadius * (float)Math.sin(obstacle.getAngle() + nextRadianStep), 
					obstacle.getAngle() + nextRadianStep);
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
		else if (gameTimeCheck >= 5) {
			planetRadianVelocity -= 1f;
			gameTimeCheck = 0;
		}
	}
	
	@Override
	public void handleTouchInput(int type, float x, float y) {
		if (type == 0) {
			//System.out.println("y: " + mainBall.body.getPosition().y);
			if (canDoubleJump && mainBall.body.getPosition().y >= 21.5) {
				canDoubleJump = false;
				mainBall.body.applyForceToCenter(doubleJumpForce);
			}
			else if (mainBall.body.getPosition().y < 17.5f && mainBall.body.getPosition().y >= 16) {
				mainBall.body.applyForceToCenter(force);
				canDoubleJump = true;
			}
			
		}
		else if (type == 1) {
			
		}
		else if (type == 2) {
			
		}
	}
	
	@Override
	public void displayLevelUI() {
		//font.draw(batch,  "LIVES: " + lives, 5, 40);
		font.draw(batch,  "LEVEL 9", Gdx.graphics.getWidth()/2, 20);
	}

}
