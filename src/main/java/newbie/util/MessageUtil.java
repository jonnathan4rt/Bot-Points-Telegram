package newbie.util;

import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Esta clase proporciona métodos de utilidad para el manejo de mensajes en Telegram.
 */
public class MessageUtil {
    private final MultiSessionTelegramBot bot;

    /**
     * Constructor para inicializar el objeto MessageUtil.
     * @param bot El bot de Telegram
     */
    public MessageUtil(MultiSessionTelegramBot bot) {
        this.bot = bot;
    }

    /**
     * Elimina un mensaje después de un cierto período de tiempo.
     * @param chatId El ID del chat donde se encuentra el mensaje
     * @param messageId El ID del mensaje a eliminar
     */
    public void deleteMessage(Long chatId, Integer messageId) {
        Thread deleteThread = new Thread(() -> {
            try {
                Thread.sleep(5000); // Espera 5 segundos antes de eliminar el mensaje
                DeleteMessage deleteMessage = new DeleteMessage();
                deleteMessage.setChatId(chatId);
                deleteMessage.setMessageId(messageId);

                bot.execute(deleteMessage); // Ejecuta la operación para eliminar el mensaje
            } catch (InterruptedException | TelegramApiException e) {
                e.printStackTrace();
            }
        });
        deleteThread.start(); // Inicia el hilo para eliminar el mensaje
    }
}
