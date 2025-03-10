#ifdef GL_ES
precision mediump float;
#endif

varying vec4 v_color;

uniform vec4 u_color1;  // First color
uniform vec4 u_color2;  // Second color
uniform float u_time;   // Time for scrolling
uniform vec2 u_speed;   // Scrolling speed (x, y)

void main() {
    float gradientFactor = fract(gl_FragCoord.x * u_speed.x + u_time * gl_FragCoord.y * u_speed.y);
    vec4 gradientColor = mix(u_color1, u_color2, gradientFactor);
    gl_FragColor = gradientColor * v_color;
}