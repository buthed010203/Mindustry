package mindustry.antigrief;

import arc.*;
import arc.util.*;
import arc.struct.*;
import arc.util.CommandHandler.*;
import mindustry.core.GameState.*;
import mindustry.game.*;

import static mindustry.Vars.*;

public class Commands {
    private final String prefix = "/";
    private final CommandHandler handler = new CommandHandler(prefix);

    public boolean displayRemoved = false;

    public Commands() {
        this.register();
    }

    private void register() {
        handler.register("eval", "<JavaScript...>", "evaluates javascript code, same as running stuff in f8", args -> {
             Core.app.post(() -> {
                 var output = mods.getScripts().runConsole("var me = Vars.player;" + Seq.with(args).toString(" "));
                 AntiGrief.sendMessage("[lightgray]" + output);
             });
        });

        handler.register("clear", "Clears the chat", args -> {
           if (!headless) ui.chatfrag.clearMessages();
        });

        handler.register("info", "<x> <y>", "Gets antigrief info for a tile", args -> {
            if (!Strings.canParseInt(args[0])) {
                AntiGrief.sendMessage("[#f5b041]x[#f8c471] is not a number");
                return;
            }

            if (!Strings.canParseInt(args[1])) {
                AntiGrief.sendMessage("[#f5b041]y[#f8c471] is not a number");
                return;
            }

            int x = Strings.parseInt(args[0]);
            int y = Strings.parseInt(args[1]);

            var infos = antiGrief.tileInfos.get(x, y);
            if (infos.size == 0) {
                AntiGrief.sendMessage("[#f5b041]No info found");
                return;
            }
            AntiGrief.sendMessage("Found " + infos.size + " interaction" + (infos.size == 1 ? "" : "s") + ":");
            infos.forEach(info -> {
                AntiGrief.sendMessage(info.toString(true, false));
            });
        });

        handler.register("toggleremoved", "Displays removed blocks", args -> {
            toggleDisplayRemoved();
        });

//        handler.register("toggleRemoved", "Displays removed blocks", args -> {
//            toggleDisplayRemoved();
//        });

        Events.on(EventType.StateChangeEvent.class, e -> {
            if (!((e.from == State.playing && e.to == State.paused) || (e.from == State.paused && e.to == State.playing))) {
                displayRemoved = false;
            }
        });
    }

    public void toggleDisplayRemoved() {
        if (!state.isPlaying() && !state.isPaused()){
            displayRemoved = false;
            return;
        }

        AntiGrief.sendMessage("[#f8c471]Displaying of removed blocks is set to [#f5b041] " + !displayRemoved);
        displayRemoved = !displayRemoved;
        renderer.blocks.reRenderShadows();
    }

    public ResponseType run(String command) {
        return handler.handleMessage(command).type;
    }
}
