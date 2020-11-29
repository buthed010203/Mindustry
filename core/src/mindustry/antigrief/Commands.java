package mindustry.antigrief;

import arc.*;
import arc.struct.*;
import arc.util.*;
import arc.util.CommandHandler.*;

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
    }

    public boolean run(String command) {
        return handler.handleMessage(command).type == ResponseType.valid;
    }
}
