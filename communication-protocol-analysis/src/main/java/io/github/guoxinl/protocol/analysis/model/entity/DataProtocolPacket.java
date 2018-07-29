package io.github.guoxinl.protocol.analysis.model.entity;

import io.github.guoxinl.protocol.analysis.model.exception.ProtocolConfigException;
import io.netty.buffer.ByteBuf;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import io.github.guoxinl.protocol.analysis.conf.cache.TypeCache;
import io.github.guoxinl.protocol.analysis.conf.cache.TypeIndexCache;
import io.github.guoxinl.protocol.analysis.conf.convert.TypeConvert;
import io.github.guoxinl.protocol.analysis.model.anno.CodeIndex;
import io.github.guoxinl.protocol.analysis.model.anno.TypeIndex;
import io.github.guoxinl.protocol.analysis.model.exception.ProtocolException;
import io.github.guoxinl.protocol.analysis.model.exception.TypeCacheNotFoundException;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Objects;

/**
 * 协议：数据段
 * <p>
 * Created by guoxin on 18-2-25.
 */
@Slf4j
@Getter
@Setter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
class DataProtocolPacket implements Serializable, ProtocolSerialization {
    /**
     * 字段
     */
    private DataProtocolIndexCode         code;
    /**
     * 类型
     */
    private DataProtocolIndexType         type;
    /**
     * 元素集合
     */
    private DataProtocolPacketElementList elements;

    /**
     * 解析数据
     *
     * @param byteBuf 字节流
     */
    DataProtocolPacket(ByteBuf byteBuf) {
        {
            int codeIndex = byteBuf.readUnsignedByte();
            this.code = DataProtocolIndexCode.create(codeIndex);
            log.debug("codeIndex readerIndex:{}", byteBuf.readerIndex());
        }
        {
            int       typeIndex = byteBuf.readUnsignedByte();
            log.debug("typeIndex readerIndex:{}", byteBuf.readerIndex());

            TypeCache typeCache = TypeIndexCache.getInstance().get(typeIndex);
            if (Objects.isNull(typeCache)) {
                throw new TypeCacheNotFoundException("typeIndex " + typeIndex + ", TypeConvert Not found!");
            }
            Class<? extends TypeConvert> typeConvert = typeCache.getTypeConvert();
            this.type = DataProtocolIndexType.create(typeIndex, typeConvert);
        }
        {
            this.elements = new DataProtocolPacketElementList(byteBuf, this.type.getType(), this.code);
        }
    }

    DataProtocolPacket(Field declaredField, ProtocolEntity protocolEntity) {
        CodeIndex codeIndexAnnotation = declaredField.getAnnotation(CodeIndex.class);
        if (Objects.isNull(codeIndexAnnotation)) {
            throw new ProtocolConfigException("字段" + declaredField.getName() + "请使用 @CodeIndex 注解对协议对象进行标注");
        }
        this.code = DataProtocolIndexCode.create(codeIndexAnnotation.index(), codeIndexAnnotation.description());

        TypeIndex typeIndexAnnotation = declaredField.getAnnotation(TypeIndex.class);
        if (Objects.isNull(typeIndexAnnotation)) {
            throw new ProtocolConfigException("字段" + declaredField.getName() + "请使用 @TypeIndex 注解对协议对象进行标注");
        }

        int                          typeIndex   = TypeConvert.getTypeIndex(typeIndexAnnotation.convert());
        TypeCache                    typeCache   = TypeIndexCache.getInstance().get(typeIndex);
        if (Objects.isNull(typeCache)) {
            throw new ProtocolConfigException("TypeIndex为 " + typeIndex + "TypeConvert未注册");
        }
        Class<? extends TypeConvert> typeConvert = typeCache.getTypeConvert();

        this.type = DataProtocolIndexType.create(typeIndex, typeConvert);
        if (Objects.nonNull(protocolEntity)) {
            this.elements = new DataProtocolPacketElementList(declaredField, protocolEntity, typeConvert);
        }
    }

    @Override
    public void serialization(ByteBuf byteBuf) {
        {
            byteBuf.writeByte(this.code.getIndex());
            log.debug("code writerIndex: {}", byteBuf.writerIndex());
        }
        {
            byteBuf.writeByte(this.type.getIndex());
            log.debug("type writerIndex: {}", byteBuf.writerIndex());
        }
        {
            elements.serialization(byteBuf);
            log.debug("elements writerIndex: {}", byteBuf.writerIndex());
        }
    }

    void protocolEntity(Object instance, short codeIndex, Field declaredField) {
        if (codeIndex == this.getCode().getIndex()) {
            declaredField.setAccessible(true);
            try {
                declaredField.set(instance, /*packet.getData()*/null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                // 如果这个对象正在执行Java语言访问控制，并且底层子弹不可访问会出现此错误
                throw new ProtocolException("如果这个对象正在执行Java语言访问控制 ，并且底层子弹不可访问会出现此错误", e);
            }
        }
    }
}
