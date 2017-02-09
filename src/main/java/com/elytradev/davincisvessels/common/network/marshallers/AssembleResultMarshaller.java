package com.elytradev.davincisvessels.common.network.marshallers;

import io.github.elytra.concrete.Marshaller;
import com.elytradev.davincisvessels.common.entity.ShipAssemblyInteractor;
import com.elytradev.movingworld.common.chunk.assembly.AssembleResult;
import io.netty.buffer.ByteBuf;

public class AssembleResultMarshaller implements Marshaller<AssembleResult> {

    public static final String MARSHALLER_NAME = "com.elytradev.davincisvessels.common.network.marshallers.AssembleResultMarshaller";
    public static final AssembleResultMarshaller INSTANCE = new AssembleResultMarshaller();

    @Override
    public AssembleResult unmarshal(ByteBuf in) {
        byte resultCode = in.readByte();
        AssembleResult result = new AssembleResult(AssembleResult.ResultType.fromByte(resultCode), in);
        result.assemblyInteractor = new ShipAssemblyInteractor().fromByteBuf(resultCode, in);

        return result;
    }

    @Override
    public void marshal(ByteBuf out, AssembleResult assembleResult) {
        out = (assembleResult.toByteBuf(out));
    }
}
