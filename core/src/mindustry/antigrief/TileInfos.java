package mindustry.antigrief;

import arc.*;
import arc.struct.*;
import arc.util.*;
import mindustry.game.EventType.*;
import mindustry.net.Administration.*;
import mindustry.world.*;

import static mindustry.Vars.*;

public class TileInfos{
    private final ObjectMap<Integer, Seq<TileInfo>> infos = new ObjectMap<>();
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
