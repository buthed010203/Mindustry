package mindustry.antigrief;

import arc.*;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.*;

import mindustry.game.*;
import mindustry.world.*;
import mindustry.gen.*;
import mindustry.content.*;
import mindustry.antigrief.TileInfos.*;
import mindustry.world.blocks.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.power.*;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.ConstructBlock.*;
import mindustry.world.blocks.distribution.*;

import static mindustry.Vars.*;

public class BlockHandler{
    public BlockHandler() {
        register();
    }

    public void blockChange(Unit unit, Tile tile, boolean rotated, boolean removing) {
        if (tile.build == null) {
            Log.warn(Strings.format("Skipping tile (@, @) change log as tile.build is null | sandbox = @ | rotated = @ | block = @ | removing = @", tile.x, tile.y, state.rules.infiniteResources, rotated, tile.block().name, removing));
            return;
        }

        var info = new TileInfo(null, tile.x, tile.y, tile.build.rotation, tile.build.config(), removing ? InteractionType.removed : rotated ? InteractionType.rotated : InteractionType.built, new SemiPlayer(unit.getPlayer().name(), unit.getPlayer().id));
        var lastInfo = antiGrief.tileInfos.getLast(tile);

        // set the block
        if (tile.block() instanceof ConstructBlock) {
            info.block = ((ConstructBuild)tile.build).current == null ? ((ConstructBuild)tile.build).previous : ((ConstructBuild)tile.build).current;
        } else {
            if (tile.block() == Blocks.air && lastInfo != null) {
                info.block = lastInfo.block;
            }
        }

        if (info.block == null) info.block = tile.block();

        if (lastInfo != null && lastInfo.block == info.block && info.block.quickRotate) {
            if (lastInfo.rotation != info.rotation) info.interaction = InteractionType.rotated;
        }

        if (antiGrief.reactorWarn && info.interaction == InteractionType.built && info.block instanceof NuclearReactor) {
            var closetCore = unit.closestCore();
            if (Mathf.dst(info.x, info.y, closetCore.tile.x, closetCore.tile.y) < ((NuclearReactor)info.block).explosionRadius + info.block.size + closetCore.block.size) {
                AntiGrief.sendMessage(Strings.format("@ is building reactor at (@, @) @ blocks away from core", info.player.name, info.x, info.y, Mathf.round(Mathf.dst(info.x, info.y, closetCore.tile.x, closetCore.tile.y))), Color.red);
            }
        }

        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction && lastInfo.player.id == info.player.id && lastInfo.rotation == info.rotation && (lastInfo.timestamp - info.timestamp < 1000)) return;
        antiGrief.tileInfos.add(info, tile);
    }

    public void blockConfig(Player p, Tile tile, Object config) {
        if (tile.build == null) {
            Log.warn(Strings.format("Skipping tile (@, @) config change log tile.build is null | sandbox = @ | block = @", tile.x, tile.y, state.rules.infiniteResources, tile.block().name));
            return;
        }

        var info = new TileInfo(tile.block(), tile.x, tile.y, tile.build.rotation, config, InteractionType.configured, new SemiPlayer(p.name(), p.id));
        var lastInfo = antiGrief.tileInfos.getLast(tile);

        // don't add if it was reconfiguring of the same block by the same player
        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction && lastInfo.player.id == info.player.id) {
            // spam clicking power nodes / messed up mess driver / changing small things in logic blocks within 20 secs
            if (info.block instanceof PowerNode || info.block instanceof MassDriver || info.block instanceof LogicBlock || info.block instanceof Sorter || info.block instanceof ItemSource || info.block instanceof LiquidSource || info.block instanceof ItemBridge) {
                if (info.timestamp - lastInfo.timestamp < 20 * 1000) return;
                lastInfo.timestamp = info.timestamp;
                lastInfo.config = config;
                return;
            }

            if (lastInfo.config == info.config) {
                lastInfo.timestamp = info.timestamp;
                return;
            }
        }

        antiGrief.tileInfos.add(info, tile);
    }

    public void blockPickedUp(Unit unit, Building build) {
        var info = new TileInfo(build.block(), build.tile.x, build.tile.y, build.rotation, null, InteractionType.picked_up, new SemiPlayer(unit.getPlayer().name(), unit.getPlayer().id));
        var lastInfo = antiGrief.tileInfos.getLast(build.tile.x, build.tile.y);

        if (info.block instanceof ConstructBlock) {
            info.block = ((ConstructBuild)build).current == null ? ((ConstructBuild)build).previous : ((ConstructBuild)build).current;
            if (info.block == null) {
                Log.warn(Strings.format("Skipping tile (@, @) pickup log block is null | sandbox = @", build.x, build.y, state.rules.infiniteResources));
                return;
            }
        }

//        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction && lastInfo.player.id == info.player.id) return;
        antiGrief.tileInfos.add(info, build.tile.x, build.tile.y);
    }

    public void blockDropped(Unit unit, Tile tile) {
        if (tile.build == null) {
            Log.warn(Strings.format("Skipping tile (@, @) drop log as tile.build() is null | sandbox = @ | block = ", tile.x, tile.y, state.rules.infiniteResources, tile.block().name));
            return;
        }

        var info = new TileInfo(tile.block(), tile.x, tile.y, tile.build.rotation, null, InteractionType.dropped, new SemiPlayer(unit.getPlayer().name(), unit.getPlayer().id));
        var lastInfo = antiGrief.tileInfos.getLast(tile);

        if (info.block instanceof ConstructBlock) {
            info.block = ((ConstructBuild)tile.build).current == null ? ((ConstructBuild)tile.build).previous : ((ConstructBuild)tile.build).current;
            if (info.block == null) {
                Log.warn(Strings.format("Skipping tile (@, @) drop log block is null | sandbox = @", tile.x, tile.y, state.rules.infiniteResources));
                return;
            }
        }

//        if (lastInfo != null && lastInfo.block == info.block && lastInfo.interaction == info.interaction && lastInfo.player.id == info.player.id) return;
        antiGrief.tileInfos.add(info, tile);
    }

    private void register() {
        Events.on(EventType.BlockBuildBeginEvent.class, e -> {
            try {
                if (e.unit == null || !e.unit.isPlayer() || e.tile.block() == null || state.rules.infiniteResources) return;
                blockChange(e.unit, e.tile, false, e.breaking);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        });

        Events.on(EventType.BlockBuildEndEvent.class, e -> {
            try {
                if (e.unit == null || !e.unit.isPlayer() || e.tile.block() == null || !state.rules.infiniteResources) return;
                blockChange(e.unit, e.tile, false, e.breaking);
            } catch (NullPointerException ex) {
                ex.printStackTrace();
            }
        });

        Events.on(EventType.ConfigEvent.class, e -> {
            if (e.player == null) return;
            blockConfig(e.player, e.tile.tile, e.value);
        });

        Events.on(EventType.PickupEvent.class, e -> {
            if (!e.carrier.isPlayer()) return;
            if (e.build != null) {
                blockPickedUp(e.carrier, e.build);
            }
        });
    }
}
