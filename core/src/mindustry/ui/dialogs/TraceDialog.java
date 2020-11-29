package mindustry.ui.dialogs;

import arc.*;
import arc.scene.ui.Button;
import arc.scene.ui.layout.*;
import mindustry.gen.*;
import mindustry.net.Administration.*;

public class TraceDialog extends BaseDialog{

    public TraceDialog(){
        super("@trace");

        addCloseButton();
        setFillParent(false);
    }

    public void show(Player player, TraceInfo info){
        cont.clear();

        Table table = new Table(Tex.clear);
        table.margin(14);
        table.defaults().pad(1);

        table.defaults().left();
        table.add(Core.bundle.format("trace.playername", player.name));
        table.row();
        table.add(Core.bundle.format("trace.ip", info.ip));
        table.row();
        table.add(Core.bundle.format("trace.id", info.uuid));
        table.row();
        table.add(Core.bundle.format("trace.modclient", info.modded));
        table.row();
        table.add(Core.bundle.format("trace.mobile", info.mobile));
        table.row();

        table.add().pad(5);
        table.row();

        table.button("Copy to clipboard", Icon.copy, () -> {
            Core.app.setClipboardText("" +
                    Core.bundle.format("tracec.playername", player.name) + "\n" +
                    Core.bundle.format("tracec.ip", info.ip) + "\n" +
                    Core.bundle.format("tracec.id", info.uuid) + "\n" +
                    Core.bundle.format("tracec.modclient", info.modded) + "\n" +
                    Core.bundle.format("tracec.mobile", info.mobile) + "\n" +
                    "");
        }).right().growX();

        cont.add(table);

        show();
    }
}
