package org.leavesmc.leaves.bytebuf.packet;

import org.leavesmc.leaves.bytebuf.Bytebuf;
import org.leavesmc.leaves.bytebuf.PacketType;

@Deprecated
public record Packet(PacketType type, Bytebuf bytebuf) {
}
