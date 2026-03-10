#version 330 core

layout (location = 0) in vec2 a_Pos;
layout (location = 1) in vec2 a_TexCoords;
layout (location = 2) in vec4 a_Color;

uniform mat4 u_Projection;

out vec2 v_TexCoords;
out vec4 v_Color;
out vec2 v_ScreenPos;

void main()
{
    v_TexCoords = a_TexCoords;
    v_Color = a_Color;
    v_ScreenPos = a_Pos.xy;
    gl_Position = u_Projection * vec4(a_Pos, 0.0, 1.0);
}
