package org.desarrollo.fiscalesfrontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.desarrollo.fiscalesfrontend.model.Calle;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CalleServicio {

    //private static final String URL_BASE = "http://localhost:8080/calles";
    private static final String URL_BASE = "https://fiscales-backend-production.up.railway.app/calles";
    private HttpClient cliente;
    private ObjectMapper mapear;

    public CalleServicio() {
        this.cliente = HttpClient.newHttpClient();
        this.mapear = new ObjectMapper();
    }

    public List<Calle> listarCalles() throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapear.readValue(respuesta.body(), new TypeReference<List<Calle>>() {});
        } else {
            throw new IOException("Error  al obtener las calles. Código HTTP: " + respuesta.statusCode());
        }
    }

    public Calle buscarPorId(Integer id) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/" + id))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        return mapear.readValue(respuesta.body(), Calle.class);
    }
}
