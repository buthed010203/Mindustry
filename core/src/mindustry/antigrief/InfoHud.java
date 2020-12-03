package mindustry.antigrief;

import arc.scene.ui.layout.*;

import mindustry.ui.*;
import mindustry.gen.*;

import static mindustry.Vars.*;

public class InfoHud extends Table{
    public InfoHud() {
        background(Tex.pane);
        update(() -> {
            clear();
            var tile = AntiGrief.getCursorTile();
            if (tile == null) {
                add("Tile out of map");
                return;
            }

            var infos = antiGrief.tileInfos.get(tile);
            add(Fonts.getUnicodeStr(tile.block().name) + " (" + tile.x + ", " + tile.y + ")").center();

            if (infos.size == 0) {
                add(" No info found.");
                return;
            }

            int added = 0;
            for(int i = infos.size - 1; i >= 0 && added < antiGrief.maxInfoInHud; i--) {
                var info = infos.get(i);
                if (info.block == null) continue;
                row();
                add(info.toString(true));
                added++;
            }
        });
    }
}
