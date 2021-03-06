package io.github.guoxinl.protocol.analysis.conf.convert;

import io.github.guoxinl.protocol.analysis.model.anno.Typed;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * C Signed int to Java {@link Integer}
 * <p>
 * Create by guoxin on 2018/7/9
 */
@Typed(index = 2, numberOfBytes = Integer.SIZE / TypeConvert.BIT, description = "{@link Integer} C Signed int to Java Integer")
public class SignedInt2integerTypeConvert implements TypeConvert<Integer> {

    @Override
    public ByteBuf encode(Integer integer) {
        ByteBuf byteBuf = Unpooled.copyInt(integer);
        return byteBuf;
    }

    @Override
    public Integer decode(ByteBuf byteBuf) {
        return byteBuf.getInt(0);
    }

}
