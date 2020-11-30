package mindustry.antigrief;

import arc.*;
import arc.func.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.*;
import mindustry.gen.*;
import mindustry.net.Administration.*;
import mindustry.net.Packets.*;

import static mindustry.Vars.*;

public class TracePlayer{
    private final ObjectMap<Player, Cons<TraceInfo>> listeners = new ObjectMap<>();
    private final ObjectMap<Player, TraceInfo> infos = new ObjectMap<>();

    private String lastMap;

    public TracePlayer() {
        Events.on(EventType.WorldLoadEvent.class, e -> {
            if (state.map.name().equals(lastMap) && net.active()) return;
            infos.clear();
            listeners.clear();
            lastMap = state.map.name();
        });
    }

    public void trace(Player player, Cons<TraceInfo> onTrace) {
        Call.adminRequest(player, AdminAction.trace);
        Log.info("[AntiGrief] Requested trace info for " + player.name);
        if (onTrace != null) {
            if (infos.containsKey(player)) {
                onTrace.get(infos.get(player));
            } else {
                if (!listeners.containsKey(player)) {
                    listeners.put(player, onTrace);
                }
            }
        }
    }

    public boolean fire(Player player, TraceInfo info) {
        if (infos.containsKey(player)) {
            infos.remove(player);
        }
        infos.put(player, info);

        if (listeners.containsKey(player)) {
            listeners.get(player).get(info);
            listeners.remove(player);
            return true;
        }
        return false;
    }
}
