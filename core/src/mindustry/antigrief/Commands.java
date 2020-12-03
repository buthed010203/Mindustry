package mindustry.antigrief;

import arc.*;
import arc.util.*;
import arc.struct.*;
import arc.util.CommandHandler.*;

import static mindustry.Vars.*;

public class Commands {
    private final String prefix = "/";
    private final CommandHandler handler = new CommandHandler(prefix);

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
                AntiGrief.sendMessage("x is not a number");
                return;
            }

            if (!Strings.canParseInt(args[1])) {
                AntiGrief.sendMessage("y is not a number");
                return;
            }

            int x = Strings.parseInt(args[0]);
            int y = Strings.parseInt(args[1]);

            var infos = antiGrief.tileInfos.get(x, y);
            if (infos.size == 0) {
                AntiGrief.sendMessage("No info found");
                return;
            }

            AntiGrief.sendMessage("Found " + infos.size + " interactions:");
            infos.forEach(info -> {
                AntiGrief.sendMessage(info.toString(true));
            });
        });
    }

    public ResponseType run(String command) {
        return handler.handleMessage(command).type;
    }
}
