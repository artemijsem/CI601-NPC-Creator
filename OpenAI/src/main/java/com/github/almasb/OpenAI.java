package com.github.almasb;

import javax.json.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class OpenAI {

    private static String SecretKey = "Bearer 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";

    private static String assistantID = "asst_kPJHExoqpt6WK7EqWWh3jzBk";

    private static String threadID = "thread_RMdmaXBG4jeTJ2RtSzdmbjhX";

    private static String runID = "";

    private static boolean runIsNotComplete = true;

    private static String GptModel = "gpt-3.5-turbo";

    private static double temperature = 0.7;

    public static HttpRequest createThread() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getURI("https://api.openai.com/v1/threads"))
                .headers("Content-Type", "application/json", "Authorization", SecretKey, "OpenAI-Beta", "assistants=v1")
                .POST(HttpRequest.BodyPublishers.ofString(""))
                .build();
        return request;
    }

    public static HttpRequest runThread(String threadID)
    {
        JsonObject MessageObj = Json.createObjectBuilder()
                .add("assistant_id", assistantID)
                .build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getURI("https://api.openai.com/v1/threads/" + threadID + "/runs"))
                .headers("Content-Type", "application/json", "Authorization", SecretKey, "OpenAI-Beta", "assistants=v1")
                .POST(HttpRequest.BodyPublishers.ofString(MessageObj.toString()))
                .build();
        return request;
    }

    public static HttpRequest getThread(String threadID)
    {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getURI("https://api.openai.com/v1/threads/" + threadID))
                .headers("Content-Type", "application/json", "Authorization", SecretKey, "OpenAI-Beta", "assistants=v1")
                .GET()
                .build();
        return request;
    }

    public static HttpRequest getRun(String threadID, String runID)
    {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getURI("https://api.openai.com/v1/threads/" + threadID + "/runs/" + runID))
                .headers("Authorization", SecretKey, "OpenAI-Beta", "assistants=v1")
                .GET()
                .build();
        return request;
    }


    public static HttpRequest postMessage(String threadID, String content)
    {
        JsonObject MessageObj = Json.createObjectBuilder()
                .add("role", "user")
                .add("content", content)
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(getURI("https://api.openai.com/v1/threads/" + threadID + "/messages"))
                .headers("Content-Type", "application/json", "Authorization", SecretKey, "OpenAI-Beta", "assistants=v1")
                .POST(HttpRequest.BodyPublishers.ofString(MessageObj.toString()))
                .build();
        return request;
    }

    public static HttpRequest listMessages(String threadID)
    {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getURI("https://api.openai.com/v1/threads/" + threadID + "/messages"))
                .headers("Content-Type", "application/json", "Authorization", SecretKey, "OpenAI-Beta", "assistants=v1")
                .GET()
                .build();
        return request;
    }

    public static HttpRequest getMessage(String threadID, String messageID)
    {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getURI("https://api.openai.com/v1/threads/" + threadID + "/messages/" + messageID))
                .headers("Content-Type", "application/json", "Authorization", SecretKey, "OpenAI-Beta", "assistants=v1")
                .GET()
                .build();
        return request;
    }



    // Serialize a HTTP request body for OpenAI API
    public static JsonObject OpenAIBody(String model, JsonArray messages, double temp)
    {
        JsonObjectBuilder objectBuilder = Json.createObjectBuilder()
                .add("model", model)
                .add("messages", messages)
                .add("temperature", temp);


        JsonObject JsonOutput = objectBuilder.build();

        return JsonOutput;
    }

    // Serialize a message for the request body
    public static JsonArray createMessage(String content)
    {
        JsonObject MessageObj = Json.createObjectBuilder()
                .add("role", "user")
                .add("content", content)
                .build();

        JsonArray output = Json.createArrayBuilder()
                .add(MessageObj)
                .build();

        return output;
    }

    public static HttpRequest getAssistant(String assisstantID)
    {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getURI("https://api.openai.com/v1/assistants/" + assisstantID))
                .headers("Content-Type", "application/json", "Authorization", SecretKey, "OpenAI-Beta", "assistants=v1")
                .GET()
                .build();
        return request;
    }


    public static HttpRequest testConnection(JsonObject body) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(getURI("https://api.openai.com/v1/chat/completions"))
                .headers("Content-Type", "application/json", "Authorization", SecretKey)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        return request;
    }

    // Error catching method for incorrect URIs
    public static URI getURI(String rawURI)
    {
        URI finalURI = null;
        try {
            URL url = new URL(rawURI);
            String nullFragment = null;
            finalURI = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), nullFragment);
            System.out.println("URI " + finalURI.toString() + " is OK");
        } catch (MalformedURLException e) {
            System.out.println("URL " + rawURI + " is a malformed URL");
        } catch (URISyntaxException e) {
            System.out.println("URI " + rawURI + " is a malformed URL");
        }

        return finalURI;
    }


    public static void run() throws IOException, InterruptedException {
        /*
        curl https://api.openai.com/v1/chat/completions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $OPENAI_API_KEY" \
  -d '{
     "model": "gpt-3.5-turbo",
     "messages": [{"role": "user", "content": "Say this is a test!"}],
     "temperature": 0.7
   }'
         */

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response;



        response = client.send(getAssistant(assistantID), HttpResponse.BodyHandlers.ofString());

        System.out.println(response.statusCode());
        System.out.println(response.body());
        response = client.send(createThread(), HttpResponse.BodyHandlers.ofString());
        String curThreadID = "";
        curThreadID = response.body().split(",")[0].split(":")[1].split("\"")[1];
        System.out.println(curThreadID);
        threadID = curThreadID;
        System.out.println("\nSTATUS CODE: " + response.statusCode());
        System.out.println("\n\nBODY RESPONSE:\n" + response.body());


        response = client.send(postMessage(threadID, "World: Welcome to the enchanting realm of Eldoria, a sprawling fantasy world brimming with magic, mythical creatures, and diverse landscapes.\n" +
                "Eldoria is a land where ancient forests, towering mountains, and shimmering lakes coexist harmoniously, creating a tapestry of natural wonders.\n" +
                "The inhabitants of Eldoria are a mix of various races, each with its own unique characteristics. Elves with their pointed ears and affinity for nature,\n" +
                "Dwarves known for their craftsmanship and underground cities, and Humans representing the adaptable and resourceful race, form the primary civilizations.\n" +
                "Additionally, there are mystical beings like the ethereal Faeries and enigmatic Centaurs, adding an air of magic to the land.\n" +
                "\n" +
                "Location: One notable town in Eldoria is Thundertop, nestled between the roots of the colossal Thunderpeak Mountains. Thundertop is a bustling town known for its\n" +
                " vibrant marketplace, where traders from distant lands gather to exchange goods and stories. The architecture of Thundertop seamlessly blends with the\n" +
                "  natural surroundings, with treehouses and stone buildings coexisting in harmony.\n" +
                "\n" +
                "Characters: Two residents of Thundertop, who have known each other since childhood, are Sylas and Elyra. Sylas, a tall and lean Elf with emerald-green eyes,\n" +
                "is a skilled archer and tracker. He has a deep connection with the ancient forests surrounding Thundertop and is often called upon to guide travelers\n" +
                "through the wilderness. Sylas is known for his calm demeanor and keen intuition.\n" +
                "Elyra, on the other hand, is a spirited Human with a fiery mane of red hair. She runs a small apothecary in the heart of Thundertop, \n" +
                "specializing in potions brewed from rare herbs and magical ingredients. Elyra's shop is adorned with colorful vials and dried herbs hanging from the ceiling,\n" +
                "creating an inviting atmosphere. She possesses a natural affinity for the healing arts and is well-respected in the community for her compassionate nature.\n" +
                "Sylas and Elyra share a deep bond forged through years of friendship. Sylas often visits Elyra's apothecary, bringing herbs and plants he gathers \n" +
                "during his expeditions. In return, Elyra provides him with potions and remedies to aid him in his adventures. \n" +
                "\n" +
                "Context: Sylas and Elyra are talking about how forest started to wither."), HttpResponse.BodyHandlers.ofString());

        System.out.println("\nSTATUS CODE: " + response.statusCode());
        System.out.println("\n\nBODY RESPONSE:\n" + response.body());

        response = client.send(runThread(threadID), HttpResponse.BodyHandlers.ofString());

        String curRunID = response.body().split(",")[0].split(":")[1].split("\"")[1];
        System.out.println(curRunID);
        runID = curRunID;
        System.out.println("\nSTATUS CODE: " + response.statusCode());
        System.out.println("\n\nBODY RESPONSE:\n" + response.body());


        response = client.send(getRun(threadID, runID), HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());
        //// IMPLEMENT POLLING TO RETRIEVE RUN COMPLETION STATUS
        while(runIsNotComplete)
        {
            response = client.send(getRun(threadID, runID), HttpResponse.BodyHandlers.ofString());
            String runStatus = response.body().split(",")[5].split(":")[1].split("\"")[1];
            if(runStatus.equals("completed"))
            {
                runIsNotComplete = false;
                break;
            }

            System.out.println(runStatus);

            Thread.sleep(8000);
        }

        System.out.println("RUN IS COMPLETE! RETRIEVING MESSAGES!");
        //////


        response = client.send(listMessages(threadID), HttpResponse.BodyHandlers.ofString());

        System.out.println(response.body());

        JsonReader jsonReader = Json.createReader(new StringReader(response.body()));
        JsonObject object = jsonReader.readObject();
        jsonReader.close();

        String message = object.getJsonArray("data").getJsonObject(0).getJsonArray("content").getJsonObject(0).getJsonObject("text").getString("value");

        System.out.println(message);

        PrintWriter out = new PrintWriter("grammy-editor/resources/GPT_Output.json");

        out.println(message);

        out.close();
    }
}

