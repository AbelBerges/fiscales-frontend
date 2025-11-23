package org.desarrollo.fiscalesfrontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.desarrollo.fiscalesfrontend.model.TipoPiso;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class TipoPisoServicio {

    private static final String URL_BASE = "http://localhost:8080/tipospisos";
    private HttpClient cliente;
    private ObjectMapper mapeador;

    public TipoPisoServicio() {
        this.cliente = HttpClient.newHttpClient();
        this.mapeador = new ObjectMapper();
    }


    public List<TipoPiso> listarTipoPiso() throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeador.readValue(respuesta.body(), new TypeReference<List<TipoPiso>>() {});
        } else {
            throw  new IOException("Error al acceder al obtener los tipos de pisos. Código HTTP " + respuesta.statusCode());
        }
    }

    public TipoPiso buscarPorId(Integer id) throws IOException, InterruptedException{
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/" + id))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return  mapeador.readValue(respuesta.body(), TipoPiso.class);
        } else {
            throw new IOException("Error al recuperar el Tipo de Piso");
        }
    }

}
