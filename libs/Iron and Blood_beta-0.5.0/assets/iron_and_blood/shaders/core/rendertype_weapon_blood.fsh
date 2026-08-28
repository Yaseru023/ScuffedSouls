#version 150

#moj_import <fog.glsl>

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float FogStart;
uniform float FogEnd;
uniform vec4 FogColor;

in float vertexDistance;
in vec4 vertexColor;
in vec4 overlayColor;
in vec2 texCoord0;
in float stainAmount;

out vec4 fragColor;

float blood_hash(vec2 point) {
    return fract(sin(dot(point, vec2(127.1, 311.7))) * 43758.5453);
}

void main() {
    vec4 source = texture(Sampler0, texCoord0);
    if (source.a < 0.1 || stainAmount <= 0.001) {
        discard;
    }

    vec4 color = source * vertexColor * ColorModulator;
    color.rgb = mix(overlayColor.rgb, color.rgb, overlayColor.a);

    vec2 atlasSize = vec2(textureSize(Sampler0, 0));
    vec2 pixel = floor(texCoord0 * atlasSize);
    float fine = blood_hash(floor(pixel / 2.0));
    float blotch = blood_hash(floor((pixel + vec2(5.0, 11.0)) / 5.0));
    float streak = blood_hash(vec2(
            floor((pixel.x + pixel.y * 0.35) / 3.0),
            floor(pixel.y / 8.0)));
    // Weapon blood is deliberately sparse. Even a fully stained weapon keeps
    // roughly ninety percent of its authored material unobscured.
    float field = max(fine * 0.99, max(blotch, streak * 0.90));
    float threshold = 0.975 - stainAmount * 0.030;
    float mask = smoothstep(threshold - 0.018,
            threshold + 0.018, field);

    float shade = clamp(dot(color.rgb, vec3(0.30, 0.59, 0.11)),
            0.18, 1.0);
    vec3 dried = vec3(0.22, 0.003, 0.001) * shade;
    vec3 wet = vec3(0.58, 0.012, 0.005) * shade;
    vec3 blood = mix(dried, wet, stainAmount);
    // Preserve metal, wood and material shading instead of replacing it with
    // a solid red surface.
    color.rgb = mix(color.rgb, blood,
            mask * (0.12 + stainAmount * 0.34));
    color.a = source.a;
    fragColor = linear_fog(
            color, vertexDistance, FogStart, FogEnd, FogColor);
}
