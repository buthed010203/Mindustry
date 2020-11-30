package mindustry.antigrief;

import arc.*;
import mindustry.antigrief.TileInfos.*;
import mindustry.game.*;
import mindustry.world.*;
import mindustry.gen.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.ConstructBlock.*;

import static mindustry.Vars.*;

public class BlockHandler{
    public BlockHandler() {
        register();
    }

    public void blockBuilt(Unit unit, Tile tile) {
        if (unit == null || !unit.isPlayer() || tile.block() == null) return;

        var player = new SemiPlayer(unit.getPlayer().name(), unit.getPlayer().id, null);
        var info = new TileInfo(tile.block(), tile.x, tile.y, tile.build.rotation, null, InteractionType.built, player);

        if (info.block instanceof ConstructBlock) {
            info.block = ((ConstructBuild)tile.build).cblock;
        }

        var lastInfo = antiGrief.tileInfos.getLast(tile);
        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction) return;

        antiGrief.tileInfos.add(info, tile);
    }

    public void blockDestroyed(Unit unit, Tile tile) {
        if (unit == null || !unit.isPlayer()) return;

        var player = new SemiPlayer(unit.getPlayer().name(), unit.getPlayer().id, null);
        var info = new TileInfo(tile.block(), tile.x, tile.y, tile.build.rotation, null, InteractionType.destroyed, player);

        if (info.block instanceof ConstructBlock) {
            info.block = ((ConstructBuild)tile.build).cblock;
        }

        var lastInfo = antiGrief.tileInfos.getLast(tile);
        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction) return;

        antiGrief.tileInfos.add(info, tile);
    }

    private void register() {
        Events.on(EventType.BlockBuildEndEvent.class, e -> {
            if (!e.breaking) {
                blockBuilt(e.unit, e.tile);
            } else {
                blockDestroyed(e.unit, e.tile);
            }
        });
    }
}
