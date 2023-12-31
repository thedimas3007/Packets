package thedimas.network.packet;

import lombok.*;
import org.jetbrains.annotations.Nullable;
import thedimas.network.enums.ServerAction;

import java.io.Serializable;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerActionPacket<T extends Serializable> implements Packet {
    ServerAction action;
    @Nullable T payload;
}
