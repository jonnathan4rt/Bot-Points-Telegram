package newbie.command;

import newbie.util.MultiSessionTelegramBot;
import newbie.controller.BotController;

/**
 * Esta clase maneja el comando "/addpoints" para agregar puntos a un usuario.
 */
public class AddPointHandler {
    private MultiSessionTelegramBot multiSessionTelegramBot;
    private BotController botController;

    /**
     * Constructor para inicializar el manejador de comandos.
     * @param multiSessionTelegramBot Instancia del bot de Telegram
     * @param botController Instancia del controlador del bot
     */
    public AddPointHandler(MultiSessionTelegramBot multiSessionTelegramBot, BotController botController) {
        this.multiSessionTelegramBot = multiSessionTelegramBot;
        this.botController = botController;
    }

    /**
     * Maneja el comando "/addpoints" para agregar puntos a un usuario.
     * @param command El comando completo enviado por el usuario
     * @param chatId El ID del chat donde se envió el comando
     * @param messageId El ID del mensaje que contiene el comando
     */
    public void handleAddPointsCommand(String command, Long chatId, Integer messageId) {
        String[] parts = command.split("\\s+", 3);

        // Verifica si el comando tiene el formato correcto
        if (parts.length != 3) {
            botController.sendMessageToChat(chatId, "Formato de comando inválido. Use /addpoints <username> <puntos>.");
            return;
        }

        String username = parts[1];
        int points;

        try {
            points = Integer.parseInt(parts[2]);
            // Verifica si los puntos son positivos
            if (points <= 0) {
                botController.sendMessageToChat(chatId, "La cantidad de puntos debe ser un número positivo.");
                return;
            }
        } catch (NumberFormatException e) {
            botController.sendMessageToChat(chatId, "Formato de puntos inválido. Por favor, use un número entero positivo.");
            return;
        }

        // Agrega los puntos al usuario
        botController.addPoints(username, points);
        botController.sendMessageToChat(chatId, "Se añadieron " + points + " puntos a " + username);

        // Borra el mensaje del chat
        botController.deleteMessage(chatId, messageId);
    }
}
