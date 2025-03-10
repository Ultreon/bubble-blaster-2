#ifdef GL_ES
#define LOWP lowp
#define HIGHP highp
precision mediump float;
#else
#define LOWP
#define HIGHP
#endif

varying vec2 v_texCoords;
varying LOWP vec4 v_color;

uniform sampler2D u_texture;
uniform sampler2D u_overlayTexture;
uniform LOWP vec4 u_color1;
uniform LOWP vec4 u_color2;
uniform HIGHP float u_time;
uniform float u_speed;
uniform vec2 u_resolution;

void main() {
    // Convert fragment position to screen-space UV (0,1)
    vec2 screenUV = gl_FragCoord.xy / u_resolution;

    // Apply scrolling effect to the X coordinate
    float v = screenUV.x;
    float scrollX = mod(v / 2.0 + u_time / u_resolution.x * u_speed * 50.0, 2.0);
    if (scrollX > 1.0) {
        scrollX = 1.0 - (scrollX - 1.0);
    }

    vec4 gradientColor = mix(u_color1, u_color2, scrollX);

    gl_FragColor = vec4(gradientColor.rgb, 1.0) * texture2D(u_texture, v_texCoords) * v_color * texture2D(u_overlayTexture, v_texCoords);
}