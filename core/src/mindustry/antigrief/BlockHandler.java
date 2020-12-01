package mindustry.antigrief;

import arc.*;
import mindustry.antigrief.TileInfos.*;
import mindustry.game.*;
import mindustry.world.*;
import mindustry.gen.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.ConstructBlock.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.power.*;

import static mindustry.Vars.*;

public class BlockHandler{
    public BlockHandler() {
        register();
    }

    public void blockBuilt(Unit unit, Tile tile) {
        blockBuilt(unit, tile, false);
    }

    public void blockBuilt(Unit unit, Tile tile, boolean rotated) {
        if (unit == null || !unit.isPlayer() || tile.block() == null) return;
        var player = new SemiPlayer(unit.getPlayer().name(), unit.getPlayer().id, null);
        var info = new TileInfo(tile.block(), tile.x, tile.y, tile.build.rotation, null, rotated ? InteractionType.rotated : InteractionType.built, player);
        var lastInfo = antiGrief.tileInfos.getLast(tile);

        if (info.block instanceof ConstructBlock) {
            info.block = ((ConstructBuild)tile.build).cblock;
        }

        if (lastInfo != null && lastInfo.block == info.block) {
            if (lastInfo.rotation != info.rotation) info.interaction = InteractionType.rotated;
        }

        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction && lastInfo.player.id == info.player.id && lastInfo.rotation == info.rotation) return;
        antiGrief.tileInfos.add(info, tile);
    }

    public void blockDestroyed(Unit unit, Tile tile) {
        if (unit == null || !unit.isPlayer()) return;

        var player = new SemiPlayer(unit.getPlayer().name(), unit.getPlayer().id, null);
        var info = new TileInfo(tile.block(), tile.x, tile.y, tile.build.rotation, null, InteractionType.destroyed, player);
        var lastInfo = antiGrief.tileInfos.getLast(tile);

        if (info.block instanceof ConstructBlock) {
            info.block = ((ConstructBuild)tile.build).cblock;
        }

        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction && lastInfo.player.id == info.player.id) return;
        antiGrief.tileInfos.add(info, tile);
    }

    public void blockConfig(Player p, Tile tile, Object config) {
        if (p == null || tile == null) return;
        var player = new SemiPlayer(p.name(), p.id, null);
        var info = new TileInfo(tile.block(), tile.x, tile.y, tile.build.rotation, config, InteractionType.configured, player);
        var lastInfo = antiGrief.tileInfos.getLast(tile);

        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction && lastInfo.player.id == info.player.id) {
            if (lastInfo.interaction == InteractionType.configured && (info.block instanceof PowerNode || info.block instanceof MassDriver)) return;
            if (lastInfo.config == info.config) return;
            if (lastInfo.interaction == InteractionType.configured && info.block instanceof Sorter) {
                lastInfo.config = info.config;
                return;
            }
        }
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

        Events.on(EventType.ConfigEvent.class, e -> {
            blockConfig(e.player, e.tile.tile, e.value);
        });
    }
}
