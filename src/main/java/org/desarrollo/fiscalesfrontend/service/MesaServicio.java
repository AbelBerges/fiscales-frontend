package org.desarrollo.fiscalesfrontend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.desarrollo.fiscalesfrontend.dto.AsignacionMesasRequestDTO;
import org.desarrollo.fiscalesfrontend.dto.MesaRequestDTO;
import org.desarrollo.fiscalesfrontend.dto.MesaResponseDTO;
import org.desarrollo.fiscalesfrontend.mapper.MesaMapper;
import org.desarrollo.fiscalesfrontend.model.Mesa;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class MesaServicio {

    //private static final String URL_BASE = ("http://localhost:8080/mesas");
    private static final String URL_BASE = "https://fiscales-backend-production.up.railway.app/mesas";
    private HttpClient cliente;
    private ObjectMapper mapeo;

    public MesaServicio() {
        this.cliente = HttpClient.newHttpClient();
        this.mapeo = new ObjectMapper();
    }

    public HttpClient getCliente() {
        return this.cliente;
    }

    //====================== LISTAR TODAS LAS MESAS =============================
    public List<Mesa> listarMesas() throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE))
                .GET()
                .build();
        HttpResponse<String>  respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            List<MesaResponseDTO> listaDto = mapeo.readValue(respuesta.body(), new TypeReference<List<MesaResponseDTO>>() { });
            return listaDto.stream().map(MesaMapper::aEntidadCompleta).toList();
        } else {
            throw new IOException("Error al listar las mesas");
        }
    }

    //====================== GUARDAMOS UNA MESA ================================
    public MesaResponseDTO guardoMesa(MesaRequestDTO dto) throws IOException, InterruptedException {
        String json = mapeo.writeValueAsString(dto);

        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), MesaResponseDTO.class);
        } else {
            throw new IOException("Error al guardar la mesa ");
        }
    }
    //====================== BUSCAR UNA MESA POR ID =============================
    public MesaResponseDTO buscoMesaPorId(Integer id) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/" + id))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return mapeo.readValue(respuesta.body(), MesaResponseDTO.class);
        } else {
            throw new IOException("Error, no se pudo recuperar la mesa");
        }
    }

    //====================== BUSCO UNA MESA POR SU NÚMERO =========================
    public Mesa buscoPorNumeroMesa(Integer numeroMesa) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE +"/buscar/" + numeroMesa))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            MesaResponseDTO temp = mapeo.readValue(respuesta.body(), MesaResponseDTO.class);
            return MesaMapper.aEntidadCompleta(temp);
        } else {
            throw new IOException("Error al recuperar la mesa por su número");
        }
    }

    //====================== ACTUALIZAR UNA MESA =============================
    public boolean actualizarMesa(AsignacionMesasRequestDTO dto) throws IOException, InterruptedException {
        String json = mapeo.writeValueAsString(dto);
        HttpRequest requerimineto = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/actualizar-mesas"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimineto, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return Boolean.parseBoolean(respuesta.body());
        } else if(respuesta.statusCode() == 400) {
            return false;
        } else {
            throw new IOException("Error al actualizar la mesa ");
        }
    }
    //====================== LISTAMOS LAS MESAS SIN ESTABLECIMIENTO =============================
    public List<Mesa> mesasSinEstablecimiento() throws IOException, InterruptedException{
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/sin-establecimiento"))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        return mapeo.readValue(respuesta.body(), new TypeReference<List<Mesa>>() {});
    }
    //====================== GUARDAMOS LA ASIGNACIÓN DE LAS MESAS =============================
    public boolean asignarMesas(AsignacionMesasRequestDTO dto) throws IOException, InterruptedException{
        String json = mapeo.writeValueAsString(dto);
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/asignar"))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        if (respuesta.statusCode() == 200) {
            return Boolean.parseBoolean(respuesta.body());
        } else if (respuesta.statusCode() == 400){
            return false;
        } else {
            throw new IOException("Error al asignar las mesas al establecimiento");
        }
    }

    //====================== RECUPERAMOS EL LISTADO DE MESAS DE UN ESTABLECIMIENTO =============
    public AsignacionMesasRequestDTO mesasPorEstablecimiento(Integer id) throws IOException, InterruptedException {
        HttpRequest requerimiento = HttpRequest.newBuilder()
                .uri(URI.create(URL_BASE + "/mesas-establecimiento/" + id))
                .GET()
                .build();
        HttpResponse<String> respuesta = cliente.send(requerimiento, HttpResponse.BodyHandlers.ofString());
        return mapeo.readValue(respuesta.body(), new TypeReference<AsignacionMesasRequestDTO>() {});
    }

}
