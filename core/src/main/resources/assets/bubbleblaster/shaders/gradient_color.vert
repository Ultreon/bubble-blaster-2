#ifdef GL_ES
precision mediump float;
#endif

attribute vec4 a_position;
attribute vec4 a_color;

uniform mat4 u_projTrans;

varying vec4 v_color;
varying vec2 v_texCoord;

void main() {
    v_color = a_color;
    gl_Position = u_projTrans * a_position;
}