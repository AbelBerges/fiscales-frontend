package org.desarrollo.fiscalesfrontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.desarrollo.fiscalesfrontend.dto.JornadaResponseDTO;
import org.desarrollo.fiscalesfrontend.mapper.JornadaMapper;
import org.desarrollo.fiscalesfrontend.model.Jornada;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class JornadaServicio {

    private final String URL_BASE = "http://localhost:8080/jornadas";
    private HttpClient cliente;
    private ObjectMapper mapeo;

    public JornadaServicio() {
        this.cliente = HttpClient.newHttpClient();
        this.mapeo = new ObjectMapper();
    }

    //====================== LISTAMOS TODAS LAS JORNADAS ================================
    public List<Jornada> listarJornadas() throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<JornadaResponseDTO> resultado = mapeo.readValue(respuesta.body(), new TypeReference<List<JornadaResponseDTO>>() {});
            return resultado.stream().map(JornadaMapper::aEntidadCompleta).toList();
        } else {
            throw new IOException("Error al recuperar la lista de jornadas");
        }
    }

    //====================== BUSCAMOS UNA JORNADA POR ID ================================
    public JornadaResponseDTO buscarPorId(Integer id) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/" + id))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), JornadaResponseDTO.class);
        } else {
            throw new IOException("Error al recuperar la joranda");
        }
    }

    //====================== GUARDAMOS UNA JORNADA======================================
    public Jornada buscarJornadaPorTipo(String tipo) throws IOException, InterruptedException{
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/buscar/" + tipo))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            JornadaResponseDTO dto = mapeo.readValue(respuesta.body(), JornadaResponseDTO.class);
            return JornadaMapper.aEntidadCompleta(dto);
        } else {
            throw new IOException("Error la buscar la jornada por su tipo");
        }
    }
}
