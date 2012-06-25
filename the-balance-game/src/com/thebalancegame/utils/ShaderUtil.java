package com.thebalancegame.utils;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;

public class ShaderUtil {

	public static Mesh createFullScreenQuad() {
		float[] verts = new float[16];// VERT_SIZE
		int i = 0;
		verts[i++] = -1; // x1
		verts[i++] = -1; // y1

		verts[i++] = 0f; // u1
		verts[i++] = 0f; // v1

		verts[i++] = 1f; // x2
		verts[i++] = -1; // y2

		verts[i++] = 1f; // u2
		verts[i++] = 0f; // v2

		verts[i++] = 1f; // x3
		verts[i++] = 1f; // y2

		verts[i++] = 1f; // u3
		verts[i++] = 1f; // v3

		verts[i++] = -1; // x4
		verts[i++] = 1f; // y4

		verts[i++] = 0f; // u4
		verts[i++] = 1f; // v4

		Mesh tmpMesh = new Mesh(false, 4, 0, new VertexAttribute(
				Usage.Position, 2, "a_position"), new VertexAttribute(
				Usage.TextureCoordinates, 2, "a_texCoord0"));

		tmpMesh.setVertices(verts);
		return tmpMesh;
	}
}
