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
            if (net.active()) Groups.player.each(p -> handleJoin(p.id));
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

            if (antiGrief.joinMessages){
                var trace = antiGrief.tracer.get(id);
                if(antiGrief.autoTrace && player.admin){
                    if(trace == null){
                        antiGrief.tracer.trace(player, t -> {
                            if(t != null){
                                AntiGrief.sendMessage(players.get(id) + "[accent] joined." + " uuid: " + t.uuid + " ip: " + t.ip);
                            }else{
                                AntiGrief.sendMessage(players.get(id) + "[accent] joined." + " id: " + id);
                            }
                        });
                    }else{
                        AntiGrief.sendMessage(players.get(id) + "[accent] joined." + " uuid: " + trace.uuid + " ip: " + trace.ip);
                    }
                }else{
                    AntiGrief.sendMessage(players.get(id) + "[accent] joined." + " id: " + id);
                }
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
