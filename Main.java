import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import com.sun.speech.freetts.*;



public class Main {

	 private static final String VOICE_NAME = "kevin16";
	
    public static void main(String[] args) {
      Scanner sc = new Scanner(System.in);
      String input = new String();
      while (!input.equals("End")) {
    	input = sc.nextLine();
    	if(input.equals("End")) {
    		Elios("Goodbye");
    		break;
    	}
    	Elios(input);
      }
      sc.close();
    }

    public static void Elios(String message) {
        String url = "https://api.openai.com/v1/chat/completions";
        String apiKey = " "; // <--- API key goes here
        String model = "gpt-4o-mini"; // current model of chatgpt api

        try {
            // Create the HTTP POST request
            @SuppressWarnings("deprecation")
			URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);
            con.setRequestProperty("Content-Type", "application/json");

            // Build the request body
            String body = "{\"model\": \"" + model + "\", \"messages\": [{\"role\": \"user\", \"content\": \"" + message + "\"}]}";
            con.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(con.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Get the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
           
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
               
               
            }
            in.close();
            //text to speech
            String output = extractContentFromResponse(response.toString());
            System.out.println(output);
            System.setProperty("freetts.voices",
                    "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");

            speak(output);
            
         

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // This method extracts the response expected from chatgpt and returns it.
    public static String extractContentFromResponse(String response) {
        int startMarker = response.indexOf("content")+11; // Marker for where the content starts.
        int endMarker = response.indexOf("\"", startMarker); // Marker for where the content ends.
        String extractedText =  response.substring(startMarker, endMarker); // Returns the substring containing only the response.
        return extractedText.replace("\\n", "\n");
    }
    private static void speak(String text) {
        Voice voice = VoiceManager.getInstance().getVoice(VOICE_NAME);
        
        if (voice == null) {
            System.err.println("Voice not found: " + VOICE_NAME);
            return;
        }

        voice.allocate(); // Load voice
        
        voice.setRate(200); // Increase speech rate (higher value = faster)
        voice.setPitch(110); // Adjust pitch (optional, 100 is default)
        voice.setVolume(1.0f); // Full volume (range: 0.0 to 1.0)
      
        String[] lines = text.split("\n");
        for (String line : lines) {
            voice.speak(line);
            try {
                Thread.sleep(500); // pause between lines
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        voice.deallocate(); // Clean up
    }
}
