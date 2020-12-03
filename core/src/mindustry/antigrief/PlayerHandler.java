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

            var trace = antiGrief.tracer.get(id);
            if(antiGrief.autoTrace && player.admin){
                if(trace == null){
                    antiGrief.tracer.trace(player, t -> {
                        if(t != null){
                            if (antiGrief.joinMessages) AntiGrief.sendMessage(players.get(id) + "[#f8c471] joined." + " [#f5b041]uuid:[] " + t.uuid + " [#f5b041]ip:[] " + t.ip);
                        }else{
                            if (antiGrief.joinMessages) AntiGrief.sendMessage(players.get(id) + "[#f8c471] joined." + " [#f5b041]id:[] " + id);
                        }
                    });
                }else{
                    if (antiGrief.joinMessages) AntiGrief.sendMessage(players.get(id) + "[#f8c471] joined." + " [#f5b041]uuid:[] " + trace.uuid + " [#f5b041]ip:[] " + trace.ip);
                }
            }else{
                if (antiGrief.joinMessages) AntiGrief.sendMessage(players.get(id) + "[#f8c471] joined." + " [#f5b041]id:[] " + id);
            }
        }
    }

    public void handleLeave(int id) {
        if (players.containsKey(id)) {
            var trace = antiGrief.tracer.get(id);
            if (antiGrief.leaveMessages) {
                AntiGrief.sendMessage(players.get(id) + "[#f8c471] left." + ((antiGrief.autoTrace && trace != null) ? " [#f5b041]uuid:[] " + trace.uuid + " [#f5b041]ip:[] " + trace.ip : " [#f5b041]id:[] " + id));
            }
            players.remove(id);
        }
    }
}
