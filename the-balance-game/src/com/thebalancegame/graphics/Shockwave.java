package com.thebalancegame.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Shockwave {

	public boolean started;
	public boolean completed;
    public Vector3 shockwaveCenter = new Vector3();
    public Vector3 shockwaveParams = new Vector3();
    public float shockwaveTime;
    
    public Shockwave() {
    	shockwaveParams.set(10.0f, 0.8f, 0.05f);
    }
    
    public Shockwave(float param1, float param2, float param3) {
    	shockwaveParams.set(param1, param2, param3);
    }
    
    public void start() {
    	this.started = true;
    	this.completed = false;
    }
    
    public void setPosition(Vector2 vect, OrthographicCamera camera) {
    	setPosition(vect.x, vect.y, camera);
    }
    
    public void setPosition(float x, float y, OrthographicCamera camera) {
    	shockwaveCenter.set(x, y, 0);
		camera.project(shockwaveCenter);
		shockwaveCenter.set(shockwaveCenter.x / (float)Gdx.graphics.getWidth(), shockwaveCenter.y / (float)Gdx.graphics.getHeight(), 0);
    }
    
    public void update(float deltaTime) {
		shockwaveTime += deltaTime;
		/*shockwaveParams.y -= shockwaveTime*0.0007f;
		if (shockwaveParams.y < 0)
			shockwaveParams.y = 0;*/
		if (shockwaveTime > 1f) {
			shockwaveTime = 0;
			//shockwaveParams.set(10.0f, 0.8f, 0.1f);
			shockwaveParams.y = 0.8f;
			completed = true;
		}
    }
}
