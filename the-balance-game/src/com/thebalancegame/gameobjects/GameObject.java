package com.thebalancegame.gameobjects;

import box2dLight.Light;

import com.thebalancegame.graphics.ParticleEffectCustom;
import com.badlogic.gdx.physics.box2d.Body;

public class GameObject {
	public Piece pieceInfo;
	public Body body;
	public Light attachedLight;
	public ParticleEffectCustom attachedParticle;
	// Used for movements interpolation
	public int genericInteger;
	public int genericInteger2;
	public float genericFloat;
	public float genericFloat2;
	public float genericFloat3;
	public long genericLong;
	public long genericLong2;
	public boolean genericBoolean;
	public boolean genericBoolean2;
	public float radius;
	public float startX;
	public float startY;
	public float endX;
	public float endY;

	public boolean used;
	public boolean visible = true;
	public long timer;
	
	public GameObject (Piece pieceInfo, Body body) {
		this.pieceInfo = pieceInfo;
		this.body = body;
		this.startX = body.getPosition().x;
		this.startY = body.getPosition().y;
	}
	
	public GameObject (Piece pieceInfo, Body body, int genericInt) {
		this.pieceInfo = pieceInfo;
		this.body = body;
		this.genericInteger = genericInt;
		this.startX = body.getPosition().x;
		this.startY = body.getPosition().y;
	}
	
	public GameObject (Piece pieceInfo, Body body, float genericFloat) {
		this.pieceInfo = pieceInfo;
		this.body = body;
		this.genericFloat = genericFloat;
		this.startX = body.getPosition().x;
		this.startY = body.getPosition().y;
	}
	
	public GameObject (Piece pieceInfo, Body body, long genericLong) {
		this.pieceInfo = pieceInfo;
		this.body = body;
		this.genericLong = genericLong;
		this.startX = body.getPosition().x;
		this.startY = body.getPosition().y;
	}
	
	public GameObject (Piece pieceInfo, Body body, boolean genericBoolean) {
		this.pieceInfo = pieceInfo;
		this.body = body;
		this.genericBoolean = genericBoolean;
		this.startX = body.getPosition().x;
		this.startY = body.getPosition().y;
	}
}
