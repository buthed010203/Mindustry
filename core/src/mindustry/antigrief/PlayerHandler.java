package mindustry.antigrief;

import arc.*;
import mindustry.game.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class PlayerHandler{
    public PlayerHandler() {
        register();
    }

    public void handleJoin(Player p) {
        player.sendMessage(p.name + "[white] has joined");
    }

    public void handlerLeave(Player p) {
        player.sendMessage(p.name + "[white] has left");
    }

    private void register() {
        Events.on(EventType.PlayerJoin.class, e -> {
            handleJoin(e.player);
        });

        Events.on(EventType.PlayerLeave.class, e -> {
            handlerLeave(e.player);
        });
    }
}
