package com.thebalancegame.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MathUtil {
	public static boolean isEqual(Vector2 first, Vector2 sec) {
		if (Float.floatToIntBits(first.x) != Float.floatToIntBits(sec.x))
			return false;
		if (Float.floatToIntBits(first.y) != Float.floatToIntBits(sec.y))
			return false;
		return true;
	}

	public static boolean isEqual(Vector3 first, Vector3 sec) {
		if (Float.floatToIntBits(first.x) != Float.floatToIntBits(sec.x))
			return false;
		if (Float.floatToIntBits(first.y) != Float.floatToIntBits(sec.y))
			return false;
		if (Float.floatToIntBits(first.z) != Float.floatToIntBits(sec.z))
			return false;
		return true;
	}
}
