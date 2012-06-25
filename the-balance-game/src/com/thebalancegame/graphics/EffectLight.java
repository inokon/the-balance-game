package com.thebalancegame.graphics;

import box2dLight.Light;

public class EffectLight {

	public Light light;
	public float tween;
	public float original;
	public float progress;
	public float speed;
	
	public EffectLight(Light light, float tween, float speed) {
		this.light = light;
		this.original = light.getDistance();
		this.tween = tween;
		this.progress = light.getDistance();
		this.speed = speed;
	}
}
