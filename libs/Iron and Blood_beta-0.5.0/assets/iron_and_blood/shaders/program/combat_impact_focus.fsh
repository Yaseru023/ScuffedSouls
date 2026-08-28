#version 150

uniform sampler2D DiffuseSampler;
uniform float intensity;
uniform float spread;

in vec2 texCoord;
out vec4 fragColor;

vec2 safeCoord(vec2 value) {
    return clamp(value, vec2(0.001), vec2(0.999));
}

void main() {
    vec2 centerRay = texCoord - vec2(0.5);
    vec2 sampleStep = centerRay * spread;
    vec4 source = texture(DiffuseSampler, texCoord);
    vec4 focused = source * 0.34;
    focused += texture(DiffuseSampler, safeCoord(texCoord - sampleStep * 0.45)) * 0.22;
    focused += texture(DiffuseSampler, safeCoord(texCoord - sampleStep * 0.90)) * 0.18;
    focused += texture(DiffuseSampler, safeCoord(texCoord - sampleStep * 1.35)) * 0.14;
    focused += texture(DiffuseSampler, safeCoord(texCoord + sampleStep * 0.30)) * 0.12;

    float edgeWeight = smoothstep(0.08, 0.72, length(centerRay));
    float blendWeight = clamp(intensity * (0.30 + edgeWeight * 0.70), 0.0, 0.62);
    fragColor = vec4(mix(source.rgb, focused.rgb, blendWeight), source.a);
}
