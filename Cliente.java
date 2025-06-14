package cliente;

import java.io.*;
import java.net.*;
import org.json.JSONObject;
import org.json.JSONArray;

public class Cliente {
    public static void main(String[] args) {
        String host = "localhost";
        int porta = 5000;

        try (
            Socket socket = new Socket(host, porta);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
        ) {
            String busca = "";

            // Validação da entrada
            while (true) {
                System.out.print("Digite a substring de busca: ");
                busca = teclado.readLine();

                if (busca == null || busca.trim().isEmpty()) {
                    System.out.println("A busca não pode estar vazia. Tente novamente.");
                } else if (busca.trim().length() < 3) {
                    System.out.println("Digite pelo menos 3 caracteres para uma busca significativa.");
                } else {
                    break; // entrada válida
                }
            }

            JSONObject json = new JSONObject();
            json.put("query", busca);

            out.write(json.toString());
            out.newLine();
            out.flush();

            String resposta = in.readLine();
            System.out.println("\nResultados encontrados:\n");

            JSONArray resultadoJson = new JSONArray(resposta);
            if (resultadoJson.length() == 0) {
                System.out.println("Nenhum resultado encontrado para: \"" + busca + "\"\n");
            } else {
                for (int i = 0; i < resultadoJson.length(); i++) {
                    JSONObject obj = resultadoJson.getJSONObject(i);
                    String onde = obj.optString("onde", "DESCONHECIDO");
                    String texto = obj.optString("texto", "");

                    System.out.println("Local encontrado: " + onde);
                    System.out.println("Trecho: " + texto);
                    System.out.println("----------------------------------------------------\n");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
