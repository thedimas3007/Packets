package thedimas.network.server.events;

import lombok.*;
import thedimas.network.enums.DcReason;
import thedimas.network.event.Event;
import thedimas.network.server.ServerClientHandler;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerClientDisconnectedEvent implements Event {
    private ServerClientHandler client;
    private DcReason reason;
}

