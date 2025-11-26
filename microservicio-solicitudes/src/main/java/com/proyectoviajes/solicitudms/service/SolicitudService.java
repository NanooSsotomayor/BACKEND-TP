package com.proyectoviajes.solicitudms.service;

import com.proyectoviajes.solicitudms.dto.ContenedorDTO;
import com.proyectoviajes.solicitudms.dto.SolicitudRequest;
import com.proyectoviajes.solicitudms.model.Solicitud;
import com.proyectoviajes.solicitudms.repository.SolicitudRepository;
import com.proyectoviajes.solicitudms.dto.CalculoEstimadoResponse;
import com.proyectoviajes.solicitudms.dto.RutaCalculoDTO;
import com.proyectoviajes.solicitudms.dto.ClienteDTO;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpStatusCodeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class SolicitudService {

    private static final Logger logger = LoggerFactory.getLogger(SolicitudService.class);

    private final SolicitudRepository solicitudRepository;
    private final RestTemplate internalRestTemplate;

    @Value("${microservices.inventario.url}")
    private String inventarioServiceUrl;
    @Value("${microservices.usuarios.url}")
    private String usuariosServiceUrl;
    @Value("${microservices.viajes.url}")
    private String viajesServiceUrl;

    public SolicitudService(SolicitudRepository solicitudRepository, RestTemplate internalRestTemplate) {
        this.solicitudRepository = solicitudRepository;
        this.internalRestTemplate = internalRestTemplate;
    }

    /**
     * Paso 1: Registrar solicitud en estado BORRADOR.
     * Lo ejecuta el CLIENTE.
     */
    @Transactional
    public Solicitud registrarNuevaSolicitud(SolicitudRequest request, String clienteKeycloakId, String token) {

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 1. Validar o Registrar Cliente en MS-USUARIOS
        ClienteDTO clienteDto = new ClienteDTO();
        // Aquí deberías mapear los datos del request al DTO de cliente si es necesario
        clienteDto.setEmail(request.getClienteEmail()); // Ejemplo
        HttpEntity<ClienteDTO> entityUsuario = new HttpEntity<>(clienteDto, headers);

        try {
            internalRestTemplate.exchange(
                    usuariosServiceUrl + "/validar-o-registrar",
                    HttpMethod.POST,
                    entityUsuario,
                    Object.class
            );
        } catch (Exception e) {
            logger.error("Fallo al llamar MS Usuarios: {}", e.getMessage());
        }

        // 2. Crear Contenedor en MS-INVENTARIO (Estado: BORRADOR)
        ContenedorDTO contenedorRequest = new ContenedorDTO();
        contenedorRequest.setPesoKg(request.getPesoKg());
        contenedorRequest.setVolumenM3(request.getVolumenM3());
        contenedorRequest.setClienteKeycloakId(clienteKeycloakId);
        contenedorRequest.setEstado("BORRADOR"); // Nace como borrador

        HttpEntity<ContenedorDTO> entityInventario = new HttpEntity<>(contenedorRequest, headers);
        ContenedorDTO contenedorPersistido;

        try {
            contenedorPersistido = internalRestTemplate.exchange(
                    inventarioServiceUrl,
                    HttpMethod.POST,
                    entityInventario,
                    ContenedorDTO.class
            ).getBody();
        } catch (Exception e) {
            logger.error("Fallo al llamar MS Inventario: {}", e.getMessage());
            throw new IllegalStateException("No se pudo crear el contenedor", e);
        }

// 3. LLAMADA A MS VIAJES (Cálculo Estimado)
        CalculoEstimadoResponse estimacion = null;
        if (viajesServiceUrl != null) {
            RutaCalculoDTO rutaRequest = new RutaCalculoDTO();

            // ✅ AGREGA ESTAS DOS LÍNEAS:
            rutaRequest.setPesoContenedorKg(request.getPesoKg());
            rutaRequest.setVolumenContenedorM3(request.getVolumenM3());

            // Configuración de la ruta (esto ya lo tenías bien)
            List<double[]> puntos = new ArrayList<>();
            puntos.add(new double[]{request.getOrigenLatitud(), request.getOrigenLongitud()});
            puntos.add(new double[]{request.getDestinoLatitud(), request.getDestinoLongitud()});
            rutaRequest.setPuntosRuta(puntos);

            HttpEntity<RutaCalculoDTO> entityViajes = new HttpEntity<>(rutaRequest, headers);
            try {
                estimacion = internalRestTemplate.exchange(
                        viajesServiceUrl + "/estimacion",
                        HttpMethod.POST,
                        entityViajes,
                        CalculoEstimadoResponse.class
                ).getBody();
            } catch (Exception e) {
                logger.warn("⚠️ Fallo cálculo estimación: {}", e.getMessage());
            }
        }

        // 4. Guardar Solicitud Local (Estado: BORRADOR)
        Solicitud solicitud = new Solicitud();
        solicitud.setContenedorId(contenedorPersistido.getId());
        solicitud.setClienteKeycloakId(clienteKeycloakId);
        solicitud.setEstado("BORRADOR"); // Nace como borrador
        solicitud.setFechaCreacion(LocalDateTime.now());

        solicitud.setOrigenDireccion(request.getOrigenDireccion());
        solicitud.setOrigenLatitud(request.getOrigenLatitud());
        solicitud.setOrigenLongitud(request.getOrigenLongitud());
        solicitud.setDestinoDireccion(request.getDestinoDireccion());
        solicitud.setDestinoLatitud(request.getDestinoLatitud());
        solicitud.setDestinoLongitud(request.getDestinoLongitud());

        if (estimacion != null) {
            solicitud.setCostoEstimado(estimacion.getCostoEstimado());
            solicitud.setTiempoEstimadoHoras(estimacion.getTiempoEstimadoHoras());
        }

        return solicitudRepository.save(solicitud);
    }

    /**
     * Paso 2: Confirmar solicitud a estado PROGRAMADA.
     * Lo ejecuta el OPERADOR.
     */
    @Transactional
    public Solicitud confirmarSolicitud(Long solicitudId, String token) {
        // 1. Buscar solicitud
        Solicitud solicitud = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        // 2. Validar estado previo
        if (!"BORRADOR".equals(solicitud.getEstado())) {
            throw new IllegalStateException("Solo se pueden confirmar solicitudes en estado BORRADOR");
        }

        // 3. Actualizar estado Local
        solicitud.setEstado("PROGRAMADA");
        Solicitud guardada = solicitudRepository.save(solicitud);

        // 4. Actualizar estado Remoto (MS-INVENTARIO)
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Collections.singletonMap("estado", "PROGRAMADA");
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            internalRestTemplate.exchange(
                    inventarioServiceUrl + "/" + solicitud.getContenedorId() + "/estado",
                    HttpMethod.PATCH,
                    entity,
                    Void.class
            );
        } catch (Exception e) {
            logger.error("Error al sincronizar estado con inventario: {}", e.getMessage());
            // No hacemos rollback para no perder la confirmación local, pero queda registro del error
        }

        return guardada;
    }

    public List<Solicitud> obtenerSolicitudesCliente(String clienteKeycloakId) {
        return solicitudRepository.findByClienteKeycloakId(clienteKeycloakId);
    }
}