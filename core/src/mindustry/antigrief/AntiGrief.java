package mindustry.antigrief;

import arc.*;
import arc.math.geom.*;
import mindustry.core.*;
import mindustry.world.*;

import static mindustry.Vars.world;

public class AntiGrief {
    public final Commands commands;
    public final BlockHandler blockHandler;
    public final TileInfos tileInfos = new TileInfos();
    public final TracePlayer tracer = new TracePlayer();

    private boolean autoTrace;
    private boolean joinMessages;
    private boolean leaveMessages;

    public int maxInfosPerTile = 50;
    public int maxInfoInHud = 5;
    public boolean showHud = true;

    public AntiGrief() {
        commands = new Commands();
        blockHandler = new BlockHandler();

        loadSettings();
    }

    public void saveSettings() {
        Core.settings.put("antigrief.autoTrace", autoTrace);
        Core.settings.put("antigrief.joinMessages", joinMessages);
        Core.settings.put("antigrief.leaveMessages", leaveMessages);

        Core.settings.put("antigrief.maxInfosPerTile", maxInfosPerTile);

        Core.settings.saveValues();
    }

    public void loadSettings() {
        autoTrace = Core.settings.getBool("antigrief.autoTrace", true);
        joinMessages = Core.settings.getBool("antigrief.joinMessages", false);
        leaveMessages = Core.settings.getBool("antigrief.leaveMessages", true);

        maxInfosPerTile = Core.settings.getInt("antigrief.maxInfosPerTile", 50);
    }

    public Tile getCursorTile() {
        Vec2 vec = Core.input.mouseWorld(Core.input.mouseX(), Core.input.mouseY());
        return world.tile(World.toTile(vec.x), World.toTile(vec.y));
    }
}
