package newbie.command;

import newbie.controller.BotController;
import newbie.util.MultiSessionTelegramBot;

/**
 * Esta clase maneja el comando "/delpoints" para restar puntos a un usuario específico.
 */
public class DelPointsHandler {
    private MultiSessionTelegramBot multiSessionTelegramBot;
    private BotController botController;

    /**
     * Constructor para inicializar el manejador de comandos.
     * @param multiSessionTelegramBot Instancia del bot de Telegram
     * @param botController Instancia del controlador del bot
     */
    public DelPointsHandler(MultiSessionTelegramBot multiSessionTelegramBot, BotController botController) {
        this.multiSessionTelegramBot = multiSessionTelegramBot;
        this.botController = botController;
    }

    /**
     * Maneja el comando "/delpoints" para restar puntos a un usuario específico.
     * @param command El comando completo enviado por el usuario
     * @param chatId El ID del chat donde se envió el comando
     * @param messageId El ID del mensaje que contiene el comando
     */
    public void handleDelPointsCommand(String command, Long chatId, Integer messageId) {
        String[] parts = command.split("\\s+", 3);
        if (parts.length == 3) {
            String username = parts[1];
            int points;
            try {
                points = Integer.parseInt(parts[2]);
                if (points > 0) {
                    botController.addPoints(username, -points); // Restar puntos al usuario
                    botController.sendMessageToChat(chatId, "Se restaron " + points + " puntos a " + username);
                } else {
                    multiSessionTelegramBot.sendTextMessageAsync("El número de puntos a restar debe ser positivo.", null);
                }
            } catch (NumberFormatException e) {
                multiSessionTelegramBot.sendTextMessageAsync("Formato de puntos inválido. Por favor, use un número entero.", null);
            }
            botController.deleteMessage(chatId, messageId);
        } else {
            multiSessionTelegramBot.sendTextMessageAsync("Formato de comando inválido. Use /delpoints <username> <puntos>.", null);
        }
    }
}
