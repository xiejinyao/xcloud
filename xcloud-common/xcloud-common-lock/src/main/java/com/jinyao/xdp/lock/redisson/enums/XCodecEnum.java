package com.jinyao.xdp.lock.redisson.enums;

/**
 * Redisson 广泛使用数据序列化来编组和解组通过 Redis 服务器的网络链接接收或发送的字节。许多流行的编解码器可供使用,如下枚举值参考自：
 * <p>
 * https://github.com/redisson/redisson/wiki/4.-data-serialization
 */
public enum XCodecEnum {
    /**
     * org.redisson.codec.MarshallingCodec
     * <p>
     * JBoss Marshalling binary codec
     * Default codec
     */
    MARSHALLING_CODEC,
    /**
     * org.redisson.codec.JsonJacksonCodec
     * <p>
     * Jackson JSON codec.
     * Stores type information in @class field
     * (Android compatible)
     */
    JSON_JACKSON_CODEC,
    /**
     * org.redisson.codec.Kryo5Codec
     * <p>
     * Kryo 5 binary codec
     * (Android compatible)
     */
    KRYO5_CODEC,
    /**
     * org.redisson.codec.AvroJacksonCodec
     * <p>
     * Avro binary json codec
     */
    AVRO_JACKSON_CODEC,
    /**
     * org.redisson.codec.SmileJacksonCodec
     * <p>
     * Smile binary json codec
     */
    SMILE_JACKSON_CODEC,
    /**
     * org.redisson.codec.CborJacksonCodec
     * <p>
     * CBOR binary json codec
     */
    CBOR_JACKSON_CODEC,
    /**
     * org.redisson.codec.MsgPackJacksonCodec
     * <p>
     * MsgPack binary json codec
     */
    MSG_PACK_JACKSON_CODEC,
    /**
     * org.redisson.codec.IonJacksonCodec
     * <p>
     * Amazon Ion codec
     */
    ION_JACKSON_CODEC,
    /**
     * org.redisson.codec.SerializationCodec
     * <p>
     * JDK Serialization binary codec
     * (Android compatible)
     */
    SERIALIZATION_CODEC,
    /**
     * org.redisson.codec.LZ4Codec
     * <p>
     * LZ4 compression codec.
     * Uses MarshallingCodec for serialization by default
     */
    L_Z_4_CODEC,
    /**
     * org.redisson.codec.SnappyCodecV2
     * <p>
     * Snappy compression codec based on snappy-java project.
     * Uses MarshallingCodec for serialization by default
     */
    SNAPPY_CODEC_V2,
    /**
     * org.redisson.client.codec.StringCodec
     * <p>
     * String codec
     */
    STRING_CODEC,
    /**
     * org.redisson.client.codec.LongCodec
     * <p>
     * Long codec
     */
    LONG_CODEC,
    /**
     * org.redisson.client.codec.ByteArrayCodec
     * <p>
     * Byte array codec
     */
    BYTE_ARRAY_CODEC;
}
