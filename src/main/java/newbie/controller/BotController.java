package newbie.controller;

import newbie.command.*;
import newbie.util.MessageUtil;
import newbie.util.MultiSessionTelegramBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatAdministrators;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;

import java.io.InputStream;
import java.util.HashMap;

/**
 * Esta clase representa el controlador principal del bot de Telegram.
 * Extiende MultiSessionTelegramBot para manejar múltiples sesiones del bot.
 */
public class BotController extends MultiSessionTelegramBot {

    /** Nombre del bot */
    public static final String NAME = "Points_MV_bot";
    /** Token de acceso del bot */
    public static final String TOKEN = "6965391890:AAHBGawdChmMsBxpqFuyrE9FWPMHYRn106A";

    /** HashMap para almacenar puntos de los usuarios */
    private HashMap<String, Integer> pointsMap = new HashMap<>();

    /** Controlador para manejar el comando /addpoints */
    private AddPointHandler addPointHandler;
    /** Controlador para manejar el comando /listpoints */
    private ListPointsHandler listPointsHandler;
    /** Controlador para manejar el comando /resetpoints */
    private ResetPointsHandler resetPointsHandler;
    /** Controlador para manejar el comando /delpoints */
    private DelPointsHandler delPointsHandler;
    /** Controlador para manejar el comando /delbyeveryone */
    private DelByEveryoneHandler delByEveryoneHandler;

    /** Utilidad para enviar mensajes */
    private MessageUtil messageUtil;
    /** Foto en caché para enviar junto con el comando /listpoints */
    private InputFile cachedPhoto;

    /**
     * Constructor para inicializar el controlador del bot.
     * Configura los manejadores de comandos y la utilidad de mensajes.
     */
    public BotController() {
        super(NAME, TOKEN);
        this.addPointHandler = new AddPointHandler(this, this);
        this.listPointsHandler = new ListPointsHandler(this, this);
        this.resetPointsHandler = new ResetPointsHandler(this, this);
        this.delPointsHandler = new DelPointsHandler(this, this);
        this.delByEveryoneHandler = new DelByEveryoneHandler(this, this);
        this.messageUtil = new MessageUtil(this); // Inicializa MessageUtil
    }

    /**
     * Método para manejar las actualizaciones recibidas del bot.
     * Se ejecuta cada vez que el bot recibe una actualización de Telegram.
     * @param updateEvent La actualización recibida
     */
    @Override
    public void onUpdateEventReceived(Update updateEvent) {
        if (updateEvent.hasMessage() && updateEvent.getMessage().isCommand()) {
            // Verificar si el remitente es un administrador o propietario del grupo
            if (esPropietarioGrupo(updateEvent)) {
                String command = updateEvent.getMessage().getText();
                Long chatId = updateEvent.getMessage().getChatId();
                Integer messageId = updateEvent.getMessage().getMessageId();

                // Llamar al manejador de comandos para /start
                StartCommandHandler.handleStartCommand(updateEvent.getMessage(), chatId, this);

                // Resto del código para manejar otros comandos
                if (command.startsWith("/addpoints")) {
                    addPointHandler.handleAddPointsCommand(command, chatId, messageId);
                } else if (command.equals("/listpoints")) {
                    if (cachedPhoto == null) {
                        // Cargar imagen desde el directorio resources solo si no está en caché
                        InputStream inputStream = getClass().getClassLoader().getResourceAsStream("images/TOP_MV.jpg");
                        cachedPhoto = new InputFile(inputStream, "photo.jpg");
                    }
                    sendPhotoToChat(chatId, "Mundo Virtual", cachedPhoto);
                    listPointsHandler.handleListPointsCommand(chatId, messageId, pointsMap);
                } else if (command.equals("/resetpoints")) {
                    resetPointsHandler.handleResetPointsCommand(chatId, messageId);
                } else if (command.startsWith("/delpoints")) {
                    delPointsHandler.handleDelPointsCommand(command, chatId, messageId);
                } else if (command.startsWith("/delbyeveryone")) {
                    delByEveryoneHandler.handleDelByEveryoneCommand(command, chatId, messageId);
                }
            } else {
                // El remitente no tiene permiso para usar comandos
                sendMessageToChat(updateEvent.getMessage().getChatId(), "Lo siento, no tienes permisos para usar comandos de este bot.");
            }
        }
    }
    /**
     * Verifica si el remitente de un mensaje es un administrador o propietario del grupo.
     * @param update La actualización recibida
     * @return true si el remitente es un administrador o propietario del grupo, false de lo contrario
     */
    private boolean esPropietarioGrupo(Update update) {
        if (!update.getMessage().getChat().isUserChat()) {
            String chatId = update.getMessage().getChatId().toString(); // Convertir a String
            Long userId = update.getMessage().getFrom().getId();

            GetChatAdministrators getChatAdministrators = new GetChatAdministrators();
            getChatAdministrators.setChatId(chatId);

            try {
                // Obtenemos la lista de administradores del grupo
                for (ChatMember chatMember : execute(getChatAdministrators)) {
                    User user = chatMember.getUser(); // Obtener el usuario asociado al ChatMember
                    if (user != null && user.getId().equals(userId)) {
                        return true; // El remitente es un administrador o propietario del grupo
                    }
                }
            } catch (TelegramApiException e) {
                e.printStackTrace();
                // Manejar luego el error adecuadamente
            }
        }
        return false; // El remitente no es un administrador o propietario del grupo
    }

    /**
     * Genera una lista de puntos para mostrar.
     * @return Una cadena que representa la lista de puntos
     */
    private String generatePointsList() {
        StringBuilder pointsList = new StringBuilder("Lista de puntos:\n");
        for (String username : pointsMap.keySet()) {
            pointsList.append(username).append(": ").append(pointsMap.get(username)).append("\n");
        }
        return pointsList.toString();
    }

    /**
     * Agrega puntos al usuario especificado.
     * @param username El nombre de usuario al que se le agregarán puntos
     * @param points La cantidad de puntos a agregar
     */
    public synchronized void addPoints(String username, int points) {
        int currentPoints = pointsMap.getOrDefault(username, 0);
        pointsMap.put(username, currentPoints + points);
    }

    /**
     * Reinicia todos los puntos, eliminando el mapa de puntos.
     */
    public synchronized void resetPoints() {
        pointsMap.clear();
    }

    /**
     * Elimina un mensaje después de un cierto tiempo.
     * @param chatId El ID del chat al que pertenece el mensaje
     * @param messageId El ID del mensaje que se eliminará
     */
    public void deleteMessage(Long chatId, Integer messageId) {
        if (messageUtil != null) { // Verifica si messageUtil no es nulo
            messageUtil.deleteMessage(chatId, messageId); // Llama al método en MessageUtil
        } else {
            System.err.println("Error: MessageUtil is not initialized.");
        }
    }

    /**
     * Envía un mensaje al chat de Telegram correspondiente.
     * @param chatId El ID del chat al que se enviará el mensaje
     * @param message El mensaje a enviar
     */
    public void sendMessageToChat(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            //Luego Hacer manejo de errores un poco mejor
        }
    }

    /**
     * Envía una foto al chat de Telegram correspondiente.
     * @param chatId El ID del chat al que se enviará la foto
     * @param caption La leyenda de la foto
     * @param photo La foto a enviar
     */
    public void sendPhotoToChat(Long chatId, String caption, InputFile photo) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setPhoto(photo);
        sendPhoto.setCaption(caption);
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            //Luego Hacer manejo de errores un poco mejor
        }
    }

    /**
     * Elimina la misma cantidad de puntos de todos los usuarios.
     * Si los puntos resultantes serían negativos, se establecen en cero.
     * @param pointsToRemove La cantidad de puntos a eliminar a todos los usuarios
     */
    public synchronized void removeAllPoints(int pointsToRemove) {
        for (String username : pointsMap.keySet()) {
            int currentPoints = pointsMap.get(username);
            int newPoints = Math.max(0, currentPoints - pointsToRemove); // Evita que los puntos sean negativos
            pointsMap.put(username, newPoints);
        }
    }
}
