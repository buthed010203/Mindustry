package mindustry.antigrief;

import arc.*;
import arc.graphics.*;
import arc.graphics.g2d.*;
import arc.math.*;
import arc.util.*;
import arc.struct.*;

import mindustry.gen.*;
import mindustry.entities.units.*;
import mindustry.game.EventType.*;
import mindustry.net.Administration.*;
import mindustry.type.*;
import mindustry.ui.*;
import mindustry.world.*;
import mindustry.world.blocks.distribution.*;
import mindustry.world.blocks.experimental.*;
import mindustry.world.blocks.logic.*;
import mindustry.world.blocks.sandbox.*;
import mindustry.world.blocks.units.*;

import static arc.Core.camera;
import static mindustry.Vars.*;

public class TileInfos{
    private final ObjectMap<Integer, Seq<TileInfo>> infos = new ObjectMap<>();
    private float brokenFade = 0f;
    private int width;

    private String lastMapName;

    public TileInfos() {
        Events.on(WorldLoadEvent.class, e -> {
            if (state.map.name().equals(lastMapName)) return;
            lastMapName = state.map.name();
            resize(world.width(), world.height());
        });
    }

    public void add(TileInfo info, int x, int y) {
        if (infos.get(y * width + x) == null) infos.put(y * width + x, new Seq<>());
        if (infos.get(y * width + x).size > antiGrief.maxInfosPerTile) infos.get(y * width + x).remove(0);
        infos.get(y * width + x).add(info);
    }

    public void add(TileInfo info, Tile tile) {
        add(info, tile.x, tile.y);
    }

    public Seq<TileInfo> get(int x, int y) {
        if (infos.get(y * width + x) == null) return new Seq<>();
        return infos.get(y * width + x);
    }

    public Seq<TileInfo> get(Tile tile) {
        return get(tile.x, tile.y);
    }

    public TileInfo getLast(Tile tile) {
        return getLast(tile.x, tile.y);
    }

    public void remove(TileInfo info, Tile tile) {
        remove(info, tile.x, tile.y);
    }

    public void remove(TileInfo info, int x, int y) {
        if (get(x, y).size == 0) return;
        infos.get(y * width + x).remove(info);
    }

    public TileInfo getLast(int x, int y) {
        var infos = get(x, y);
        if (infos.size == 0) return null;
        return infos.get(infos.size - 1);
    }

    public void resize(int width, int height) {
        this.width = width;
        infos.clear(width * height);
    }

    public void displayRemoved(){
        if (!antiGrief.commands.displayRemoved) return;
        brokenFade = Mathf.lerpDelta(brokenFade, 1f, 0.1f);

        if(brokenFade > 0.001f){
            Seq<TileInfo> lastBroken = new Seq<>();

            infos.each((loc, infos2) -> {
                for(int i = infos2.size - 1; i >= 0; i--){
                    if (infos2.get(i).interaction == InteractionType.removed || infos2.get(i).interaction == InteractionType.picked_up) {
                        lastBroken.add(infos2.get(i));
                        break;
                    }
                }
            });

            for(int i = 0; i < lastBroken.size; i++){
                if (lastBroken.get(i).block == null) continue;
                Block b = lastBroken.get(i).block;
                var info = lastBroken.get(i);
                if(!camera.bounds(Tmp.r1).grow(tilesize * 2f).overlaps(Tmp.r2.setSize(b.size * tilesize).setCenter(info.x * tilesize + b.offset, info.y * tilesize + b.offset))) continue;

                Draw.alpha(0.33f * brokenFade);
                Draw.mixcol(Color.white, 0.2f + Mathf.absin(Time.globalTime, 6f, 0.2f));
                if (antiGrief.displayFullSizeBlocks) {
                    Draw.rect(b.icon(Cicon.full), info.x * tilesize + b.offset, info.y * tilesize + b.offset, b.rotate ? info.rotation * 90 : 0f);
                } else {
                    Draw.rect(b.icon(Cicon.medium), info.x * tilesize, info.y * tilesize, b.rotate ? info.rotation * 90 : 0f);
                }
            }
            Draw.reset();
        }
    }

    static class TileInfo {
        public Block block;

        public int x;
        public int y;

        public int rotation;
        public Object config;

        public InteractionType interaction;
        public SemiPlayer player;

        public long timestamp;

        public TileInfo(Block block, int x, int y, int rotation, Object config, InteractionType interaction, SemiPlayer player){
            this.block = block;
            this.x = x;
            this.y = y;
            this.rotation = rotation;
            this.config = config;
            this.interaction = interaction;
            this.player = player;

            timestamp = Time.millis();
        }

        public String toString() {
            if (block == null) return "???";
            StringBuilder str = new StringBuilder(player.name + "[white] " + interaction.name().replace("_", " ") + " " + Fonts.getUnicodeStr(block.name));

            if (interaction == InteractionType.configured) {
                if(block instanceof MessageBlock){
                    if (config.equals("")) {
                        str.append(" to empty");
                    }
                }else if(block instanceof SwitchBlock){
                    if ((Boolean)config) {
                        str.append(" to T");
                    } else {
                        str.append(" to F");
                    }
                }else if(block instanceof Sorter || block instanceof ItemSource || block instanceof LiquidSource){
                    if (config != null){
                        str.append(" to ").append(Fonts.getUnicodeStr(block instanceof LiquidSource ? ((Liquid)config).name : ((Item)config).name));
                    }else{
                        str.append(" to ").append("none");
                    }
                }else if(block instanceof CommandCenter){
                    str.append(" to ");
                    var command = (UnitCommand)config;
                    if(command == UnitCommand.attack){
                        str.append(Iconc.commandAttack);
                    }else if(command == UnitCommand.rally){
                        str.append(Iconc.commandRally);
                    }else if(command == UnitCommand.idle) {
                        str.append(Iconc.cancel);
                    }
                }else if(block instanceof UnitFactory){
                    str.append(" to ");
                    if((Integer)config != -1){
                        str.append(Fonts.getUnicodeStr(((UnitFactory)block).plans.get((Integer)config).unit.name));
                    }else{
                        str.append("none");
                    }
                }else if(block instanceof BlockForge){
                    str.append(" to ");
                    if (config != null) {
                        str.append(Fonts.getUnicodeStr(((Block)config).name));
                    } else {
                        str.append("none");
                    }
                }
            }else if(interaction == InteractionType.rotated){
                str.append(" to ");
                if(rotation == 0){
                    str.append(Iconc.right);
                }else if(rotation == 1){
                    str.append(Iconc.up);
                }else if(rotation == 2){
                    str.append(Iconc.left);
                }else if(rotation == 3){
                    str.append(Iconc.down);
                }
            }
            return str.toString();
        }

        public String toString(boolean withTimestamp) {
            var str = this.toString();
            if (withTimestamp){
                str = str + " " + AntiGrief.prettyTime(Time.millis() - timestamp) + " ago";
            }
            return str;
        }
    }

    static class SemiPlayer {
        public String name;
        public int id;

        public SemiPlayer(String name, int id){
            this.name = name;
            this.id = id;
        }

        public TraceInfo getTrace() {
            return antiGrief.tracer.get(id);
        }
    }

    enum InteractionType {
        built, removed, configured, rotated, picked_up, dropped
    }
}
