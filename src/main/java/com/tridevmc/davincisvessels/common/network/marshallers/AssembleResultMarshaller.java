package com.tridevmc.davincisvessels.common.network.marshallers;

import com.tridevmc.davincisvessels.common.entity.ShipAssemblyInteractor;
import com.tridevmc.movingworld.common.chunk.assembly.AssembleResult;
import com.tridevmc.compound.network.marshallers.Marshaller;
import com.tridevmc.compound.network.marshallers.RegisteredMarshaller;
import io.netty.buffer.ByteBuf;

@RegisteredMarshaller(channel = "davincisvessels", acceptedTypes = {AssembleResult.class}, ids = {"ar", "assembleresult"})
public class AssembleResultMarshaller extends Marshaller<AssembleResult> {

    @Override
    public AssembleResult readFrom(ByteBuf in) {
        byte resultCode = in.readByte();
        AssembleResult result = new AssembleResult(AssembleResult.ResultType.fromByte(resultCode), in);
        result.assemblyInteractor = new ShipAssemblyInteractor().fromByteBuf(resultCode, in);

        return result;
    }

    @Override
    public void writeTo(ByteBuf out, AssembleResult assembleResult) {
        assembleResult.toByteBuf(out);
    }
}
