package mindustry.antigrief;

import arc.*;
import arc.util.*;
import arc.math.geom.*;

import mindustry.core.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class AntiGrief {
    public final Commands commands;
    public final BlockHandler blockHandler;
    public final PlayerHandler playerHandler;
    public final TileInfos tileInfos = new TileInfos();
    public final TracePlayer tracer = new TracePlayer();

    public boolean autoTrace;
    public boolean joinMessages;
    public boolean leaveMessages;

    public boolean displayFullSizeBlocks = false;

    public int maxInfosPerTile = 50;
    public int maxInfoInHud = 5;
    public boolean showHud = true;

    public AntiGrief() {
        commands = new Commands();
        blockHandler = new BlockHandler();
        playerHandler = new PlayerHandler();

        loadSettings();
    }

    public void saveSettings() {
        Core.settings.put("antigrief.autoTrace", autoTrace);
        Core.settings.put("antigrief.joinMessages", joinMessages);
        Core.settings.put("antigrief.leaveMessages", leaveMessages);
        Core.settings.put("antigrief.showHud", showHud);
        Core.settings.put("antigrief.displayFullSizeBlocks", displayFullSizeBlocks);

        Core.settings.put("antigrief.maxInfosPerTile", maxInfosPerTile);
        Core.settings.put("antigrief.maxInfoInHud", maxInfoInHud);

        Core.settings.saveValues();
    }

    public void loadSettings() {
        autoTrace = Core.settings.getBool("antigrief.autoTrace", true);
        joinMessages = Core.settings.getBool("antigrief.joinMessages", false);
        leaveMessages = Core.settings.getBool("antigrief.leaveMessages", true);
        showHud = Core.settings.getBool("antigrief.showHud", true);
        displayFullSizeBlocks = Core.settings.getBool("antigrief.displayFullSizeBlocks", false);

        maxInfosPerTile = Core.settings.getInt("antigrief.maxInfosPerTile", 50);
        maxInfoInHud = Core.settings.getInt("antigrief.maxInfoInHud", 5);
    }

    public static void sendMessage(String msg) {
        if (headless) {
            Log.info("[AntiGrief] " + msg);
        } else {
            ui.chatfrag.addMessage(msg, null);
        }
    }

    public static Tile getCursorTile() {
        Vec2 vec = Core.input.mouseWorld(Core.input.mouseX(), Core.input.mouseY());
        return world.tile(World.toTile(vec.x), World.toTile(vec.y));
    }

    public static String prettyTime(long millis) {
        long secs = millis / 1000;
        long mins = 0, hours = 0;
        var time = new StringBuilder();

        if (secs >= 60) {
            mins = (secs - (secs % 60)) / 60;
            secs = secs % 60;
        }

        if (mins >= 60) {
            hours = (mins - (mins % 60)) / 60;
            mins = mins % 60;
        }

        if (secs == 0 && mins == 0 && hours == 0) time.append(millis % 1000).append("ms");
        else if (mins == 0 && hours == 0) time.append(secs).append("s");
        else if (hours == 0) time.append(mins).append("m").append(secs).append("s");
        else time.append(hours).append("h").append(mins).append("m").append(secs).append("s");

        return time.toString();
    }
}
