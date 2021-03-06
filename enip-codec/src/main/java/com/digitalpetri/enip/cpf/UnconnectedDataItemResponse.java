package com.digitalpetri.enip.cpf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;

public final class UnconnectedDataItemResponse extends CpfItem {

    public static final int TYPE_ID = 0xB2;

    private final ByteBuf data;

    public UnconnectedDataItemResponse(ByteBuf data) {
        super(TYPE_ID);

        this.data = data;
    }

    public ByteBuf getData() {
        return data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnconnectedDataItemResponse that = (UnconnectedDataItemResponse) o;

        return ByteBufUtil.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return ByteBufUtil.hashCode(data);
    }

    public static ByteBuf encode(UnconnectedDataItemResponse item, ByteBuf buffer) {
        buffer.writeShort(item.getTypeId());

        // Length placeholder...
        int lengthStartIndex = buffer.writerIndex();
        buffer.writeShort(0);

        // Encode the encapsulated data...
        int dataStartIndex = buffer.writerIndex();
        buffer.writeBytes(item.getData());
        item.getData().release();

        // Go back and update the length.
        int bytesWritten = buffer.writerIndex() - dataStartIndex;
        buffer.markWriterIndex();
        buffer.writerIndex(lengthStartIndex);
        buffer.writeShort(bytesWritten);
        buffer.resetWriterIndex();

        return buffer;
    }

    public static UnconnectedDataItemResponse decode(ByteBuf buffer) {
        int typeId = buffer.readUnsignedShort();
        int length = buffer.readUnsignedShort();

        assert (typeId == TYPE_ID);

        ByteBuf data = buffer.readSlice(length).retain();

        return new UnconnectedDataItemResponse(data);
    }

}
