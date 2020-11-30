package mindustry.antigrief;

import arc.func.*;
import arc.struct.*;
import mindustry.gen.*;
import mindustry.net.Administration.*;
import mindustry.net.Packets.*;

public class TracePlayer{
    private final ObjectMap<Player, Cons<TraceInfo>> listeners = new ObjectMap<>();
    private final ObjectMap<Player, TraceInfo> infos = new ObjectMap<>();

    public void trace(Player player, Cons<TraceInfo> onTrace) {
        Call.adminRequest(player, AdminAction.trace);
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
