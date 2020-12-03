package mindustry.antigrief;

import arc.*;
import arc.struct.*;
import arc.util.*;
import arc.util.CommandHandler.*;

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
                 AntiGrief.sendMessage("[lightgray]" + output);
             });
        });

        handler.register("clear", "Clears the chat", args -> {
           if (!headless) ui.chatfrag.clearMessages();
        });

//        handler.register("info", "Gets antigrief info for tile below the cursor", args -> {
//            var infos = antiGrief.tileInfos.get(antiGrief.getCursorTile());
//            if (infos.size == 0) {
//                player.sendMessage("No info found");
//                return;
//            }
//            infos.forEach(info -> {
//                player.sendMessage(info.interaction.name() + " by " + info.player.name + ",[white] block was " + info.block.name);
//            });
//        });
    }

    public boolean run(String command) {
        return handler.handleMessage(command).type == ResponseType.valid;
    }
}
