#version 330 core

in vec2 v_TexCoords;
in vec4 v_Color;
in vec2 v_ScreenPos;

out vec4 fragColor;

uniform sampler2D u_Texture;
uniform float u_Range;
uniform vec2 u_TextureSize;

uniform int u_PaintType;
uniform vec4 u_PaintColor1, u_PaintColor2, u_GradientCoords;

float median(float r, float g, float b) {
    return max(min(r, g), min(max(r, g), b));
}

float screenRange() {
    vec2 unitRange = vec2(u_Range) / u_TextureSize;
    vec2 screenTexSize = vec2(1.0) / fwidth(v_TexCoords);
    return max(0.5 * dot(unitRange, screenTexSize), 1.0);
}

void main() {
    vec3 msd = texture(u_Texture, v_TexCoords).rgb;
    float sd = median(msd.r, msd.g, msd.b);

    float screenDistance = screenRange() * (sd - 0.5);
    float opacity = clamp(screenDistance + 0.5, 0.0, 1.0);

    vec4 baseColor;
    if (u_PaintType == 1) {
        vec2 start = u_GradientCoords.xy;
        vec2 end = u_GradientCoords.zw;
        vec2 v = end - start;

        float t = dot(v_ScreenPos - start, v) / dot(v, v);
        
        baseColor = mix(u_PaintColor1, u_PaintColor2, clamp(t, 0.0, 1.0));
    } else {
        baseColor = u_PaintColor1;
    }

    baseColor *= v_Color;

    if (opacity < 0.01) {
        discard;
    }

    fragColor = vec4(baseColor.rgb, baseColor.a * opacity);
}
