package oraksoft.codegen.modal;

import oraksoft.codegen.modules.ModHomeCodeGenerator;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;

public class ModalShared {
    public static boolean checkActiveServerJdbi(ModHomeCodeGenerator modHome) {

        Jdbi activeServerJdbi = modHome.getAndSetupActiveServerJdbi();

        if (activeServerJdbi != null) {
            FxDialogShow.showPopInfo("Veritabanı Bağlantı Başarılı...");
            //System.out.println("Connected");
            return true;
        } else {
            FxDialogShow.showPopError("Veritabanına Bağlanılamadı !!!");
            return false;
        }

    }
}
