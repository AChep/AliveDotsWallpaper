precision mediump float;

attribute vec4 a_Position;
attribute float a_TimeShift;
attribute float a_HighColor;

uniform float u_PointSize;
uniform float u_Time;
uniform vec4  u_Color;

varying vec4 v_Color;

void main() {
	float timeMask = abs(fract(u_Time + a_TimeShift) * 2.0 - 1.0);
	float positionMask =  (1.3 - abs(a_Position.y - 0.3))  * (1.0 - abs(a_Position.x) * 0.7);
	v_Color = u_Color * timeMask * a_HighColor * positionMask;
	vec4 position = a_Position;
	position.x = position.x * timeMask;
	position.y = position.y * sqrt(1.0 - timeMask * 0.2);
	gl_Position = position;
	gl_PointSize = u_PointSize * (abs(position.x) + 0.11);
}