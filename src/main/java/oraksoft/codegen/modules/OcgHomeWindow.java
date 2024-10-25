package oraksoft.codegen.modules;

import javafx.scene.layout.Pane;
import ozpasyazilim.utils.gui.fxcomponents.*;
import ozpasyazilim.utils.mvc.IFiModView;

public class OcgHomeWindow implements IFiModView {

    // Layout Components
    private FxMigPane migRoot;
    private FxMigPane migHeader;
    private FxMigPane migMenu;
    private FxMigPane migContent;

    @Override
    public Pane getRootPane() {
        return getMigRoot();
    }

    public void initGui() {
        // FxMpConfig.debugMode = true;
        // Root Container
        migRoot = new FxMigPane(FxMigHp.bui().lcgInset0Gap55().lcgNoGrid().getLcg());
        // Layout Containers
        migHeader = new FxMigPane(FxMigHp.bui().lcgInset0Gap55().getLcg());
        migMenu = new FxMigPane(FxMigHp.bui().lcgInset3Gap33().lcgNoGrid().getLcg());
        migContent = new FxMigPane(FxMigHp.bui().lcgInset3Gap33().getLcg());

        //rootMigPane.add(migHeader, "growx,pushx,wrap");
        migRoot.add(migMenu, "growx,pushx,wrap");
        migRoot.add(migContent, "grow,push,span");

    }

    public FxMigPane getMigRoot() {
        return migRoot;
    }

    public FxMigPane getMigHeader() {
        return migHeader;
    }

    public FxMigPane getMigMenu() {
        return migMenu;
    }

    public FxMigPane getMigContent() {
        return migContent;
    }

}
