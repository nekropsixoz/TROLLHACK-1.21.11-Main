#version 330 core

in vec4 v_Color;
in vec2 v_Size;
in vec4 v_Radius;
in vec2 v_LocalPos;

out vec4 fragColor;

float roundedBoxSDF(vec2 p, vec2 size, vec4 r) {
    float radius = (p.x > 0.0) ? ((p.y > 0.0) ? r.z : r.y) : ((p.y > 0.0) ? r.w : r.x);
    vec2 q = abs(p) - size + radius;

    return min(max(q.x, q.y), 0.0) + length(max(q, 0.0)) - radius;
}

void main() {
    vec2 halfSize = v_Size * 0.5;
    float dist = roundedBoxSDF(v_LocalPos, halfSize, v_Radius);

    float currentRadius = (v_LocalPos.x > 0.0) ? (v_LocalPos.y > 0.0 ? v_Radius.z : v_Radius.y) : (v_LocalPos.y > 0.0 ? v_Radius.w : v_Radius.x);
    vec2 cornerOffset = abs(v_LocalPos) - (halfSize - currentRadius);
    float distFromCorner = length(max(cornerOffset, 0.0));

    float paddingFactor = smoothstep(currentRadius * 0.5, currentRadius * 1.5, distFromCorner);
    float padding = mix(0.5 * fwidth(dist), 0.0, paddingFactor);

    dist -= padding;

    float smoothing = fwidth(dist);
    float alpha = 1.0 - smoothstep(-smoothing, smoothing, dist);

    if (alpha <= 0.0) {
        discard;
    }

    vec4 finalColor = v_Color;

    fragColor = vec4(finalColor.rgb, finalColor.a * alpha);
}
