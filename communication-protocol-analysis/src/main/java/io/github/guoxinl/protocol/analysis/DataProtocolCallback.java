package io.github.guoxinl.protocol.analysis;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.github.guoxinl.protocol.analysis.model.entity.DataProtocol;
import io.github.guoxinl.protocol.analysis.model.entity.ProtocolEntity;
import io.github.guoxinl.protocol.analysis.utils.ClassUtils;
import lombok.NoArgsConstructor;

import java.util.Objects;


/**
 * Created by guoxin on 18-2-25.
 */
@NoArgsConstructor
public class DataProtocolCallback implements Callback<ByteBuf, ByteBuf> {

    @Override
    public ByteBuf call(ByteBuf byteBuf) {
        DataProtocol dataProtocol         = DataProtocol.analysis(byteBuf);
        ProtocolEntity protocolEntity     = dataProtocol.protocolEntity();
        Class        callback             = dataProtocol.getCallback();
        Object       resultProtocolEntity = ClassUtils.methodInvoke(callback, "call", Object.class, protocolEntity);
        if (Objects.isNull(resultProtocolEntity)) {
            return null;
        }
        DataProtocol result = DataProtocol.convert((ProtocolEntity) resultProtocolEntity);
        ByteBuf      buffer = Unpooled.buffer();
        result.serialization(buffer);
        return buffer;
    }

}
