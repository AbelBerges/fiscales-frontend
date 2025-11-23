package org.desarrollo.fiscalesfrontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.desarrollo.fiscalesfrontend.model.TipoEstablecimiento;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class TipoEstablecimientoServicio {
    private static final String URL_BASE = "http://localhost:8080/tipoestablecimiento";
    private HttpClient cliente;
    private ObjectMapper mapeo;

    public TipoEstablecimientoServicio() {
        this.cliente = HttpClient.newHttpClient();
        this.mapeo = new ObjectMapper();
    }

    public List<TipoEstablecimiento> listarTiposEstablecimiento() throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), new TypeReference<List<TipoEstablecimiento>>() {});
        } else
            throw new IOException("Error al listar los tipos de establecimientos" + respuesta.statusCode());
    }

    public TipoEstablecimiento buscarPorId(Integer id) throws IOException, InterruptedException{
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/" + id))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), TipoEstablecimiento.class);
        } else {
            throw new IOException("Error al recuperar el Tipo de Establecimiento");
        }
    }
}
