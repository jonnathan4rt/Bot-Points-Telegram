package newbie.command;

import newbie.controller.BotController;
import newbie.util.MultiSessionTelegramBot;

/**
 * Esta clase maneja el comando "/delbyeveryone" para eliminar puntos de todos los usuarios.
 */
public class DelByEveryoneHandler {
    private MultiSessionTelegramBot multiSessionTelegramBot;
    private BotController botController;

    /**
     * Constructor para inicializar el manejador de comandos.
     * @param multiSessionTelegramBot Instancia del bot de Telegram
     * @param botController Instancia del controlador del bot
     */
    public DelByEveryoneHandler(MultiSessionTelegramBot multiSessionTelegramBot, BotController botController) {
        this.multiSessionTelegramBot = multiSessionTelegramBot;
        this.botController = botController;
    }

    /**
     * Maneja el comando "/delbyeveryone" para eliminar puntos de todos los usuarios.
     * @param command El comando completo enviado por el usuario
     * @param chatId El ID del chat donde se envió el comando
     * @param messageId El ID del mensaje que contiene el comando
     */
    public void handleDelByEveryoneCommand(String command, Long chatId, Integer messageId) {
        String[] parts = command.split("\\s+", 2);
        if (parts.length == 2) {
            int pointsToRemove;
            try {
                pointsToRemove = Integer.parseInt(parts[1]);
                botController.removeAllPoints(pointsToRemove); // Eliminar puntos de todos los usuarios
                botController.sendMessageToChat(chatId, "Se han eliminado " + pointsToRemove + " puntos de todos los usuarios.");
            } catch (NumberFormatException e) {
                multiSessionTelegramBot.sendTextMessageAsync("Formato de puntos inválido. Por favor, use un número entero.", null);
            }
            botController.deleteMessage(chatId, messageId);
        } else {
            multiSessionTelegramBot.sendTextMessageAsync("Formato de comando inválido. Use /delbyeveryone <puntos>.", null);
            // Borra el mensaje del chat
            botController.deleteMessage(chatId, messageId);
        }
    }
}
