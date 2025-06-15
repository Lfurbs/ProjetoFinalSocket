package servidorC;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import org.json.JSONArray;
import java.util.regex.Pattern;

public class ServidorC {
    public static void main(String[] args) throws IOException {
        int porta = 7000;
        // Cria um servidor socket escutando na porta 7000
        ServerSocket serverSocket = new ServerSocket(porta);
        System.out.println("Servidor C aguardando...");

        // Loop para aceitar múltiplas conexões simultâneas
        while (true) {
            Socket socket = serverSocket.accept(); // Aguarda conexão do Servidor A
            // Cria uma nova thread para tratar a requisição de forma paralela
            new Thread(() -> tratarBusca(socket)).start();
        }
    }

    // Método responsável por tratar a busca recebida do Servidor A
    public static void tratarBusca(Socket socket) {
        try (
            // Abre os fluxos de entrada e saída para comunicação com o Servidor A
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        ) {
            // Lê a entrada JSON contendo a consulta
            String entrada = in.readLine();
            JSONObject req = new JSONObject(entrada);
            String query = req.getString("query").toLowerCase(); // Converte a busca para minúsculas

            // Carrega o conteúdo do arquivo de dados local do servidor C
            JSONArray dados = new JSONArray(Files.readString(Paths.get("dados/dados_servidor_c.json")));
            JSONArray resultados = new JSONArray(); // Onde os resultados serão armazenados

            // Para cada artigo no arquivo de dados
            for (int i = 0; i < dados.length(); i++) {
                JSONObject artigo = dados.getJSONObject(i);
                String titulo = artigo.optString("title", "").toLowerCase();       // Campo título
                String introducao = artigo.optString("abstract", "").toLowerCase(); // Campo resumo

                // Verifica se a query aparece no título
                if (titulo.contains(query)) {
                    JSONObject resultado = new JSONObject();
                    resultado.put("onde", "TÍTULO");
                    resultado.put("texto", destaque(titulo, query)); // Realça a palavra
                    resultados.put(resultado);
                }

                // Verifica se a query aparece na introdução
                if (introducao.contains(query)) {
                    JSONObject resultado = new JSONObject();
                    resultado.put("onde", "INTRODUÇÃO");
                    resultado.put("texto", destaque(introducao, query)); // Realça a palavra
                    resultados.put(resultado);
                }
            }

            // Envia os resultados de volta para o Servidor A
            out.write(resultados.toString());
            out.newLine();
            out.flush();
        } catch (Exception e) {
            // Em caso de erro, exibe o stack trace
            e.printStackTrace();
        }
    }

    // Função responsável por destacar a palavra encontrada (em amarelo no terminal)
    public static String destaque(String texto, String query) {
        String ANSI_YELLOW = "\u001B[33m";
        String ANSI_RESET = "\u001B[0m";
        // Usa regex para destacar a query (case-insensitive)
        return texto.replaceAll("(?i)(" + Pattern.quote(query) + ")", ANSI_YELLOW + "$1" + ANSI_RESET);
    }

    /* Alternativa para destaque usando Markdown (ex: **palavra**)
    public static String destaque(String texto, String query) {
        return texto.replaceAll("(?i)(" + Pattern.quote(query) + ")", "**$1**");
    }
    */
}
