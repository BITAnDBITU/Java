import com.google.gson.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static Map<String,Integer> mapWithSizeOfRecept = new HashMap<>();
    public static void main(String[] args) throws Exception {
        System.out.println("Пожалуйста, введите ингредиенты через запятую");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        String ingr = bufferedReader.readLine();
        for (int i = 1; i < 11 ; i++) {

            String url = "http://www.recipepuppy.com/api/?i=" + ingr + "&p=" + i;

            URL obj = new URL(url);
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) obj.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }

            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            String responseString = response.toString();

            JsonParser jsonParser = new JsonParser();
            JsonObject jsonObject = jsonParser.parse(responseString).getAsJsonObject();
            JsonArray results = jsonObject.get("results").getAsJsonArray();

            for (JsonElement result : results) {
                JsonObject recipe = result.getAsJsonObject();
                // Это название рецепта
                String name = recipe.get("title").toString().trim();
                // Ссылка на него
                String href = recipe.get("href").toString().trim();
                // Ингредиенты
                String ingredients = recipe.get("ingredients").toString().trim();
                mapWithSizeOfRecept.put(name,ingredients.split(",").length);

            }
        }
        int min=Integer.MAX_VALUE;
        String resultRecept = "";
        for (Map.Entry<String, Integer> entry : mapWithSizeOfRecept.entrySet()) {
            if(min > entry.getValue()) {
                min = entry.getValue();
                resultRecept = entry.getKey();
            }
        }
        System.out.println("Simpliest recipe for requested ingredients is: " + resultRecept);
        System.in.read();
    }
}