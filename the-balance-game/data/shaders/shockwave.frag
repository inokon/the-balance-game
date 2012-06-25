#ifdef GL_ES
precision lowp float;
#endif

uniform sampler2D sceneTex; // 0
uniform vec2 center; // Mouse position
uniform float time; // effect elapsed time
uniform vec3 shockParams; // 10.0, 0.8, 0.1

varying vec2 v_texCoords;

vec2 diffraction(vec2 uv){   
   vec2 differ = uv - center;
   float dist2 = dot(differ, differ);   
   vec2 diffUV = differ * inversesqrt(dist2);
   float distance = sqrt(dist2);
   
   float diff = (distance - time);
   
   vec2 texCoords = uv +  diffUV * diff;
   
   return (abs(diff) > shockParams.z) ? uv : texCoords;
}

void main()
{
	gl_FragColor = texture2D(sceneTex, diffraction(v_texCoords.xy));
}