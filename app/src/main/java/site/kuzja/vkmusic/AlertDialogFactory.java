package site.kuzja.vkmusic;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Фабрика для создания диалогов
 */
class AlertDialogFactory {
    final static int BUTTONS_OK = 1;
    static AlertDialog create(String title, String message, int buttons, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setIcon(R.drawable.ic_launcher)
                .setCancelable(false);
        if (buttons == BUTTONS_OK) {
            builder.setNegativeButton("ОК",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
        }
        return builder.create();
    }
}
