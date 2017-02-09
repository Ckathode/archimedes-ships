package io.github.elytra.davincisvessels.common.network.marshallers;

import io.github.elytra.concrete.Marshaller;
import io.github.elytra.davincisvessels.common.entity.ShipAssemblyInteractor;
import io.github.elytra.movingworld.common.chunk.assembly.AssembleResult;
import io.netty.buffer.ByteBuf;

public class AssembleResultMarshaller implements Marshaller<AssembleResult> {

    public static final String MARSHALLER_NAME = "io.github.elytra.davincisvessels.common.network.marshallers.AssembleResultMarshaller";
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
