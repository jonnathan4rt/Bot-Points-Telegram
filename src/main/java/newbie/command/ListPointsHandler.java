package newbie.command;

import newbie.util.MultiSessionTelegramBot;
import newbie.controller.BotController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.stream.Collectors;

/**
 * Esta clase maneja el comando "/listpoints" para mostrar una lista de puntos de los usuarios.
 */
public class ListPointsHandler {

    private MultiSessionTelegramBot multiSessionTelegramBot;
    private BotController botController;

    /**
     * Constructor para inicializar el manejador de comandos.
     * @param multiSessionTelegramBot Instancia del bot de Telegram
     * @param botController Instancia del controlador del bot
     */
    public ListPointsHandler(MultiSessionTelegramBot multiSessionTelegramBot, BotController botController) {
        this.multiSessionTelegramBot = multiSessionTelegramBot;
        this.botController = botController;
    }

    /**
     * Maneja el comando "/listpoints" para mostrar una lista de puntos de los usuarios.
     * @param chatId El ID del chat donde se envió el comando
     * @param messageId El ID del mensaje que contiene el comando
     * @param pointsMap El mapa de puntos de los usuarios
     */
    public void handleListPointsCommand(Long chatId, Integer messageId, HashMap<String, Integer> pointsMap) {
        String message = generatePointsList(pointsMap);
        botController.sendMessageToChat(chatId, message);
        botController.deleteMessage(chatId, messageId);
    }

    /**
     * Genera una lista de puntos de los usuarios ordenada por puntos.
     * @param pointsMap El mapa de puntos de los usuarios
     * @return Una cadena que representa la lista de puntos
     */
    private synchronized String generatePointsList(HashMap<String, Integer> pointsMap) {
        // Crear una lista de pares (username, points) ordenada por puntos (de mayor a menor)
        List<Map.Entry<String, Integer>> sortedPoints = pointsMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toList());
        StringBuilder builder = new StringBuilder("(Ranking):\n");
        // Añadir medallas al usuario según su posición en el ranking
        for (int i = 0; i < sortedPoints.size(); i++) {
            String username = sortedPoints.get(i).getKey();
            int points = sortedPoints.get(i).getValue();
            String medal = "";
            // Asignar medallas
            if (i == 0) {
                medal = "🥇";
            } else if (i == 1) {
                medal = "🥈";
            } else if (i == 2) {
                medal = "🥉";
            } else if (i == 3) {
                medal = "🔥"; // Emoji para el puesto 4
            } else if (i == 4) {
                medal = "🌪"; // Emoji para el puesto 5
            } else {
                medal = "👤"; // Valor predeterminado
            }
            // Construir la entrada del ranking
            builder.append(medal).append(" ").append(username).append(": ").append(points).append(" puntos\n");
        }
        return builder.toString();
    }
}
