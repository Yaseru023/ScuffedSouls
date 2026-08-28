#version 150

uniform sampler2D DiffuseSampler;
uniform float desaturation;
uniform float blurRadius;
uniform vec2 OutSize;

in vec2 texCoord;
out vec4 fragColor;

void main() {
    vec2 pixel = blurRadius / max(OutSize, vec2(1.0));

    vec4 source = texture(DiffuseSampler, texCoord) * 0.28;
    source += texture(DiffuseSampler, texCoord + vec2(pixel.x, 0.0)) * 0.12;
    source += texture(DiffuseSampler, texCoord - vec2(pixel.x, 0.0)) * 0.12;
    source += texture(DiffuseSampler, texCoord + vec2(0.0, pixel.y)) * 0.12;
    source += texture(DiffuseSampler, texCoord - vec2(0.0, pixel.y)) * 0.12;
    source += texture(DiffuseSampler, texCoord + pixel) * 0.06;
    source += texture(DiffuseSampler, texCoord - pixel) * 0.06;
    source += texture(DiffuseSampler, texCoord + vec2(pixel.x, -pixel.y)) * 0.06;
    source += texture(DiffuseSampler, texCoord + vec2(-pixel.x, pixel.y)) * 0.06;

    float luminance = dot(source.rgb, vec3(0.2126, 0.7152, 0.0722));
    vec3 desaturated = mix(source.rgb, vec3(luminance), clamp(desaturation, 0.0, 1.0));
    fragColor = vec4(desaturated, source.a);
}
