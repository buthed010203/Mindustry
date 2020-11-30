package mindustry.antigrief;

import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.ui.*;

import static mindustry.Vars.*;

public class InfoHud extends Table{
    public InfoHud() {
        background(Tex.pane);
        update(() -> {
            clear();
            var tile = antiGrief.getCursorTile();
            if (tile == null) {
                add("Tile out of map");
                return;
            }
            var infos = antiGrief.tileInfos.get(tile);
            add(((char)Fonts.getUnicode(tile.block().name)) + " (" + tile.x + ", " + tile.y + ")").center();

            if (infos.size == 0) {
                add(" No info found.");
                return;
            }

            infos.forEach(info -> {
                row();
                add(info.player.name + " [white]" + info.interaction.name() + " " + ((char)Fonts.getUnicode(info.block.name))).center();
            });
        });
    }
}
