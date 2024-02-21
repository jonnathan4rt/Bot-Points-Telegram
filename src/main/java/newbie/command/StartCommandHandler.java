package newbie.command;

import newbie.util.MultiSessionTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Esta clase maneja el comando "/start" para proporcionar información de bienvenida al usuario.
 */
public class StartCommandHandler {

    /**
     * Maneja el comando "/start" para proporcionar información de bienvenida al usuario.
     * @param message El mensaje que contiene el comando "/start"
     * @param chatId El ID del chat donde se envió el comando
     * @param bot El bot de Telegram
     */
    public static void handleStartCommand(Message message, Long chatId, MultiSessionTelegramBot bot) {
        String command = message.getText();
        if (command.equals("/start") && message.getChat().isUserChat()) {
            String welcomeMessage = "¡Saludos! 🎉 Agrega el bot a tu grupo para iniciar.\n\n"
                    + "Aquí están los comandos que puedes utilizar con el bot:\n\n"
                    + "📜 /listpoints: Obten la lista de todos los usuarios agregados.\n"
                    + "🔄 /resetpoints: Restablece todos los puntos del bot a cero.\n"
                    + "➕ /addpoints <Usuario> <Puntos>: Añade puntos a un usuario específico.\n"
                    + "➖ /delpoints <Usuario> <Puntos>: Resta puntos de un usuario específico.\n"
                    + "❌ /delbyeveryone <Puntos>: Elimina puntos específicos de todos los usuarios.\n\n"
                    + "¡Explora las funcionalidades del bot y diviértete en el grupo! 💬✨";
            SendMessage sendMessage = new SendMessage(chatId.toString(), welcomeMessage);
            try {
                bot.execute(sendMessage);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
