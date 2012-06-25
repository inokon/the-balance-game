package com.thebalancegame.graphics;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class ParticleEffectCustom extends ParticleEffect {
	public boolean isStarted;
	public Body attachedBody;
	public Vector2 offset = new Vector2();
	
	@Override
	public void start() {
		super.start();
		isStarted = true;
	}
	
	@Override
	public void update (float delta) {
		if (attachedBody != null) {
			setPosition(attachedBody.getPosition().x + offset.x, attachedBody.getPosition().y + offset.y);
		}
		super.update(delta);
	}
	
	@Override
	public boolean isComplete () {
		if (super.isComplete()) {
			isStarted = false;
			return true;
		}
		return false;
	}
}
