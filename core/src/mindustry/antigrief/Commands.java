package mindustry.antigrief;

import arc.*;
import arc.math.geom.*;
import arc.struct.*;
import arc.util.*;
import arc.util.CommandHandler.*;
import mindustry.core.*;
import mindustry.world.*;

import java.util.*;

import static mindustry.Vars.*;

public class Commands {
    private final String prefix = "/";
    private CommandHandler handler = new CommandHandler(prefix);

    public Commands() {
        this.register();
    }

    private void register() {
        handler.register("eval", "<JavaScript...>", "evaluates javascript code, same as running stuff in f8", args -> {
             Core.app.post(() -> {
                 var output = mods.getScripts().runConsole("var me = Vars.player;" + Seq.with(args).toString(" "));
                 if (output.length() >= maxTextLength) {
                     Log.info("eval: " + output);
                     player.sendMessage("[accent]Output exceeded max message size and was logged instead");
                 } else {
                    player.sendMessage("[lightgray]" + output);
                 }
             });
        });

        handler.register("clear", "Clears the chat", args -> {
           ui.chatfrag.clearMessages();
        });

        handler.register("info", "<on/off>", "Gets antigrief info for a tile", args -> {
            var infos = antiGrief.tileInfos.get(getCursorTile());
            if (infos == null || infos.size == 0) {
                player.sendMessage("No info found");
                return;
            }
            infos.forEach(info -> {
                player.sendMessage(info.interaction.name() + " by " + info.lastInteraction.name + ",[white] block was " + info.block.name);
            });
        });
    }

    private Tile getCursorTile() {
        Vec2 vec = Core.input.mouseWorld(Core.input.mouseX(), Core.input.mouseY());
        return world.tile(World.toTile(vec.x), World.toTile(vec.y));
    }

    public boolean run(String command) {
        return handler.handleMessage(command).type == ResponseType.valid;
    }
}
