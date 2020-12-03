package mindustry.antigrief;

import arc.*;
import arc.util.*;
import arc.func.*;
import arc.struct.*;

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

    public void trace(Player player){
        trace(player, null);
    }

    public void trace(Player playerToTrace, Cons<TraceInfo> onTrace) {
        if(!player.admin || !antiGrief.autoTrace || playerToTrace.admin) {
            if (onTrace != null) onTrace.get(null);
            return;
        }

        if (infos.containsKey(playerToTrace.id)){
            onTrace.get(infos.get(playerToTrace.id));
            return;
        }

        if (!listeners.containsKey(playerToTrace.id)) {
            listeners.put(playerToTrace.id, onTrace);
        }

        Call.adminRequest(playerToTrace, AdminAction.trace);
        Log.info("[AntiGrief] Requested trace info for " + playerToTrace.name);
    }

    public boolean fire(Player player, TraceInfo info) {
        if (infos.containsKey(player.id)) {
            infos.remove(player.id);
        }
        infos.put(player.id, info);

        if (listeners.containsKey(player.id)) {
            if (listeners.get(player.id) != null) listeners.get(player.id).get(info);
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
