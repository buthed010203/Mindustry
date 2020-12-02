package mindustry.antigrief;

import arc.*;
import arc.util.*;
import mindustry.antigrief.TileInfos.*;
import mindustry.content.*;
import mindustry.game.*;
import mindustry.world.*;
import mindustry.gen.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.ConstructBlock.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.sandbox.*;

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
        var player = new SemiPlayer(unit.getPlayer().name(), unit.getPlayer().id);
        var lastInfo = antiGrief.tileInfos.getLast(tile);

        if (lastInfo == null && tile.block() == Blocks.air && tile.build == null) { // Sandbox blocks seem to have issues
//            Log.info("Tile deconstruct (" + tile.x +  ", " +  tile.y + ") couldnt be logged; sandbox=" + state.rules.infiniteResources);
            return;
        }

        var info = new TileInfo(tile.block() == Blocks.air ? lastInfo.block : tile.block(), tile.x, tile.y, tile.build == null ? lastInfo.rotation : tile.build.rotation, null, InteractionType.built, player);

        if (info.block instanceof ConstructBlock) {
            info.block = ((ConstructBuild)tile.build).cblock == null ? ((ConstructBuild)tile.build).previous : ((ConstructBuild)tile.build).cblock;
            if (info.block == null) {
                Log.info("Tile deconstruct (" + tile.x +  ", " +  tile.y + ") couldnt be logged because block is null; sandbox=" + state.rules.infiniteResources);
                return;
            }
        }

        if (lastInfo != null && lastInfo.block == info.block && info.block.quickRotate) {
            if (lastInfo.rotation != info.rotation) info.interaction = InteractionType.rotated;
        }

        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction && lastInfo.player.id == info.player.id && lastInfo.rotation == info.rotation) return;
        antiGrief.tileInfos.add(info, tile);
    }

    public void blockDestroyed(Unit unit, Tile tile) {
        if (unit == null || !unit.isPlayer() || tile.block() == null) return;

        var player = new SemiPlayer(unit.getPlayer().name(), unit.getPlayer().id);
        var lastInfo = antiGrief.tileInfos.getLast(tile);

        if (lastInfo == null && tile.block() == Blocks.air && tile.build == null) { // Sandbox blocks seem to have issues
//            Log.info("Tile deconstruct (" + tile.x +  ", " +  tile.y + ") couldnt be logged; sandbox=" + state.rules.infiniteResources);
            return;
        }

        var info = new TileInfo(tile.block() == Blocks.air ? lastInfo.block : tile.block(), tile.x, tile.y, tile.build == null ? lastInfo.rotation : tile.build.rotation, null, InteractionType.removed, player);

        if (info.block instanceof ConstructBlock) {
            info.block = ((ConstructBuild)tile.build).cblock == null ? ((ConstructBuild)tile.build).previous : ((ConstructBuild)tile.build).cblock;
            if (info.block == null) {
                Log.info("Tile deconstruct (" + tile.x +  ", " +  tile.y + ") couldnt be logged because block is null; sandbox=" + state.rules.infiniteResources);
                return;
            }
        }

        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction && lastInfo.player.id == info.player.id) return;
        antiGrief.tileInfos.add(info, tile);
    }

    public void blockConfig(Player p, Tile tile, Object config) {
        if (p == null || tile == null || tile.block() == null) return;
        var player = new SemiPlayer(p.name(), p.id);
        var info = new TileInfo(tile.block(), tile.x, tile.y, tile.build.rotation, config, InteractionType.configured, player);
        var lastInfo = antiGrief.tileInfos.getLast(tile);

        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction && lastInfo.player.id == info.player.id) {
            if (lastInfo.interaction == InteractionType.configured && (info.block instanceof PowerNode || info.block instanceof MassDriver)) return;
            if (lastInfo.config == info.config) return;
            if (lastInfo.interaction == InteractionType.configured && (info.block instanceof Sorter || info.block instanceof ItemSource || info.block instanceof LiquidSource)) {
                lastInfo.timestamp = info.timestamp;
                lastInfo.config = info.config;
                return;
            }
        }
        antiGrief.tileInfos.add(info, tile);
    }

    public void blockPickedUp(Unit unit, Building build) {
        if (unit == null || !unit.isPlayer() || build.block() == null) return;

        var player = new SemiPlayer(unit.getPlayer().name(), unit.getPlayer().id);
        var info = new TileInfo(build.block(), build.tile.x, build.tile.y, build.rotation, null, InteractionType.picked_up, player);
        var lastInfo = antiGrief.tileInfos.getLast(build.tile.x, build.tile.y);

        if (info.block instanceof ConstructBlock) {
            info.block = ((ConstructBuild)build).cblock == null ? ((ConstructBuild)build).previous : ((ConstructBuild)build).cblock;
            if (info.block == null) {
                Log.info("Tile deconstruct (" + build.tile.x +  ", " +  build.tile.y + ") couldnt be logged because block is null; sandbox=" + state.rules.infiniteResources);
                return;
            }
        }

        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction && lastInfo.player.id == info.player.id) return;
        antiGrief.tileInfos.add(info, build.tile.x, build.tile.y);
    }

    public void blockDropped(Unit unit, Tile tile) {
        if (unit == null || !unit.isPlayer() || tile.block() == null) return;

        var player = new SemiPlayer(unit.getPlayer().name(), unit.getPlayer().id);
        var info = new TileInfo(tile.block(), tile.x, tile.y, tile.build.rotation, null, InteractionType.dropped, player);
        var lastInfo = antiGrief.tileInfos.getLast(tile);

        if (info.block instanceof ConstructBlock) {
            info.block = ((ConstructBuild)tile.build).cblock == null ? ((ConstructBuild)tile.build).previous : ((ConstructBuild)tile.build).cblock;
            if (info.block == null) {
                Log.info("Tile deconstruct (" + tile.x +  ", " +  tile.y + ") couldnt be logged because block is null; sandbox=" + state.rules.infiniteResources);
                return;
            }
        }

        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction && lastInfo.player.id == info.player.id) return;
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

        Events.on(EventType.BlockBuildBeginEvent.class, e -> {
            if (!e.breaking) {
                blockBuilt(e.unit, e.tile);
            } else {
                blockDestroyed(e.unit, e.tile);
            }
        });

        Events.on(EventType.ConfigEvent.class, e -> {
            blockConfig(e.player, e.tile.tile, e.value);
        });

        Events.on(EventType.PickupEvent.class, e -> {
            if (e.build != null) {
                blockPickedUp(e.carrier, e.build);
            }
        });
    }
}
