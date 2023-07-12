package oraksoft.codegen.modal;

import oraksoft.codegen.modules.ModHomeCodeGenerator;
import org.jdbi.v3.core.Jdbi;
import ozpasyazilim.utils.gui.fxcomponents.FxDialogShow;

public class MolcdgShared {
    public static boolean checkAndSetupActiveServerJdbi(ModHomeCodeGenerator modHome) {
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

    public static boolean checkActiveServerJdbi2(ModHomeCodeGenerator modHome) {
        Jdbi activeServerJdbi = modHome.getModalSqlInit().getJdbi1();

        if (activeServerJdbi == null) {
            FxDialogShow.showPopWarn("Server 1 Bağlantısını Kurunuz !!!");
            return false;
        }
        return true;
    }
}
