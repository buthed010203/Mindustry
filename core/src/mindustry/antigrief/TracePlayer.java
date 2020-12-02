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
    private final ObjectMap<Integer, Cons<TraceInfo>> listeners = new ObjectMap<>();
    private final ObjectMap<Integer, TraceInfo> infos = new ObjectMap<>();

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
            if (infos.containsKey(player.id)) {
                onTrace.get(infos.get(player.id));
            } else {
                if (!listeners.containsKey(player.id)) {
                    listeners.put(player.id, onTrace);
                }
            }
        }
    }

    public boolean fire(Player player, TraceInfo info) {
        if (infos.containsKey(player.id)) {
            infos.remove(player.id);
        }
        infos.put(player.id, info);

        if (listeners.containsKey(player.id)) {
            listeners.get(player.id).get(info);
            listeners.remove(player.id);
            return true;
        }
        return false;
    }

    public TraceInfo get(int id) {
        if (infos.containsKey(id)) return infos.get(id);
        return null;
    }

    public TraceInfo get(Player p) {
        return get(p.id);
    }
}
