package newbie.command;

import newbie.controller.BotController;
import newbie.util.MultiSessionTelegramBot;

/**
 * Esta clase maneja el comando "/resetpoints" para restablecer los puntos de todos los usuarios.
 */
public class ResetPointsHandler {

    private MultiSessionTelegramBot multiSessionTelegramBot;
    private BotController botController;

    /**
     * Constructor para inicializar el manejador de comandos.
     * @param multiSessionTelegramBot Instancia del bot de Telegram
     * @param botController Instancia del controlador del bot
     */
    public ResetPointsHandler(MultiSessionTelegramBot multiSessionTelegramBot, BotController botController) {
        this.multiSessionTelegramBot = multiSessionTelegramBot;
        this.botController = botController;
    }

    /**
     * Maneja el comando "/resetpoints" para restablecer los puntos de todos los usuarios.
     * @param chatId El ID del chat donde se envió el comando
     * @param messageId El ID del mensaje que contiene el comando
     */
    public void handleResetPointsCommand(Long chatId, Integer messageId) {
        botController.resetPoints(); // Llama al método para restablecer los puntos
        botController.sendMessageToChat(chatId, "Se han restablecido los puntos de todos los usuarios.");
        botController.deleteMessage(chatId, messageId);
    }

}
