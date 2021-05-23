package ru.vkbot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.messages.*;
import com.vk.api.sdk.queries.messages.MessagesGetLongPollHistoryQuery;


public class Bot {

    public static void main(String[] args) throws ClientException, ApiException, InterruptedException{
        TransportClient transportClient = new HttpTransportClient();
        VkApiClient vk = new VkApiClient(transportClient);
        Random random = new Random();
        Keyboard keyboard = new Keyboard();
        //Что бы кнопки после 1 нажатия исчезали необходимо написать так:
//        Keyboard keyboard = new Keyboard().setOneTime(true);

//        List<List<KeyboardButton>> allKey = new ArrayList<>();
//        List<KeyboardButton> line1 = new ArrayList<>();
//        line1.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("Привет").setType(KeyboardButtonActionType.TEXT)).setColor(KeyboardButtonColor.POSITIVE));
//        line1.add(new KeyboardButton().setAction(new KeyboardButtonAction().setLabel("Кто я?").setType(KeyboardButtonActionType.TEXT)).setColor(KeyboardButtonColor.POSITIVE));
//        allKey.add(line1);
//        keyboard.setButtons(allKey);

        GroupActor actor = new GroupActor(91763023, "157ea4e71acc500d66d3d894671787957817779f9e340a5c083b01f9c8601c59772e855835b145db4f767");
        Integer ts = vk.messages().getLongPollServer(actor).execute().getTs();
        while (true){
            MessagesGetLongPollHistoryQuery historyQuery = vk.messages().getLongPollHistory(actor).ts(ts);
            List<Message> messages = historyQuery.execute().getMessages().getItems();
            if(!messages.isEmpty()){
                messages.forEach(message -> {
                    System.out.println(message.toString());
                    //Тут проходит все общение с ботом. Получаем строку от пользователя, сравниваем ее с заданым значением и отправляем ответ
                    try{
                        if(message.getText().equals("Привет")){
                            //Именно тут мы и отправляем ответ!
                            vk.messages().send(actor).message("Привет!").userId(message.getFromId()).randomId(random.nextInt(10000)).execute();
                        }
                        else if(message.getText().equals("Кто я?")){
                            vk.messages().send(actor).message("Ты хороший человек))").userId(message.getFromId()).randomId(random.nextInt(10000)).execute();
                        }
//                        else if(message.getText().equals("Кнопки")){
//                            vk.messages().send(actor).message("А вот и они!").userId(message.getFromId()).randomId(random.nextInt(10000)).keyboard(keyboard).execute();
//                        }
                        else {
                            vk.messages().send(actor).message("Я тебя не понял.").userId(message.getFromId()).randomId(random.nextInt(10000)).execute();
                        }
                    } catch (ApiException | ClientException e){
                        e.printStackTrace();
                    }
                });
            }
            ts = vk.messages().getLongPollServer(actor).execute().getTs();
            Thread.sleep(1000);
        }
    }
}
