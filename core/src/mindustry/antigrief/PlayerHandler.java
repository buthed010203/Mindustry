package mindustry.antigrief;

import arc.*;
import arc.struct.*;
import mindustry.game.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class PlayerHandler{
    private final ObjectMap<Integer, String> players = new ObjectMap<>();
    private String lastMap;

    public PlayerHandler() {
        Events.on(EventType.WorldLoadEvent.class, e -> {
            if (state.map.name().equals(lastMap) && net.active()) return;
            lastMap = state.map.name();
            players.clear();
        });
    }

    public void handleJoin(int id) {
        if (players.containsKey(id)) return;
        var player = Groups.player.getByID(id);
        if (player != null) {
            players.put(id, player.name);

            if(antiGrief.tracer.get(id) == null) {
                antiGrief.tracer.trace(player, trace -> {
                    if (trace != null && antiGrief.joinMessages) {
                        AntiGrief.sendMessage(players.get(id) + "[accent] left." + " uuid: " + trace.uuid + " ip: " + trace.ip);
                    }
                });
            }

            if (antiGrief.joinMessages && !antiGrief.autoTrace && !player.admin) {
                AntiGrief.sendMessage(players.get(id) + "[accent] left." + " id: " + id);
            }
        }
    }

    public void handleLeave(int id) {
        if (players.containsKey(id)) {
            var trace = antiGrief.tracer.get(id);
            if (antiGrief.leaveMessages) {
                AntiGrief.sendMessage(players.get(id) + "[accent] left." + ((antiGrief.autoTrace && trace != null) ? " uuid: " + trace.uuid + " ip: " + trace.ip : " id: " + id));
            }
            players.remove(id);
        }
    }
}
