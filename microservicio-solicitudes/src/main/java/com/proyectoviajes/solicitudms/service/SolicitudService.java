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
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpStatusCodeException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class SolicitudService {

    private static final Logger logger = LoggerFactory.getLogger(SolicitudService.class);

    private final SolicitudRepository solicitudRepository;
    private final RestTemplate internalRestTemplate;

    // URLs inyectadas desde application.yml
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

    public Solicitud registrarNuevaSolicitud(SolicitudRequest request, String clienteKeycloakId, String token) {

        logger.debug("Iniciando registro de solicitud para clienteKeycloakId={}", clienteKeycloakId);

        // Nota: Las cabeceras (headers) de seguridad se adjuntan automáticamente
        // a través del TokenRelayInterceptor configurado en RestTemplate.

        // ---------------------------------------------------------
        // 1. LLAMADA A MS USUARIOS (Validar/Crear Cliente)
        // ---------------------------------------------------------
        ClienteDTO clienteDto = new ClienteDTO();
        clienteDto.setKeycloakId(clienteKeycloakId);
        clienteDto.setNombre(request.getClienteNombre());
        clienteDto.setEmail(request.getClienteEmail());
        clienteDto.setTelefono(request.getClienteTelefono());

        // Usamos exchange con HttpEntity para enviar el cuerpo y usar el Interceptor
        HttpEntity<ClienteDTO> entityUsuario = new HttpEntity<>(clienteDto);

        try {
            logger.debug("Llamando a MS Usuarios: {}", usuariosServiceUrl + "/validar-o-registrar");
            internalRestTemplate.exchange(
                    usuariosServiceUrl + "/validar-o-registrar",
                    HttpMethod.POST,
                    entityUsuario,
                    Object.class
            );
        } catch (HttpStatusCodeException e) {
            logger.error("Error HTTP al llamar MS Usuarios: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            logger.error("Fallo al llamar MS Usuarios: {}", e.getMessage(), e);
            throw new IllegalStateException("No se pudo validar/registrar el cliente", e);
        }

        // ---------------------------------------------------------
        // 2. LLAMADA A MS INVENTARIO (Crear Contenedor)
        // ---------------------------------------------------------
        ContenedorDTO contenedorRequest = new ContenedorDTO();
        contenedorRequest.setPesoKg(request.getPesoKg());
        contenedorRequest.setVolumenM3(request.getVolumenM3());
        contenedorRequest.setClienteKeycloakId(clienteKeycloakId);
        contenedorRequest.setEstado("BORRADOR");

        HttpEntity<ContenedorDTO> entityInventario = new HttpEntity<>(contenedorRequest);

        ContenedorDTO contenedorPersistido = null;
        try {
            logger.debug("Llamando a MS Inventario: {}", inventarioServiceUrl);
            contenedorPersistido = internalRestTemplate.exchange(
                    inventarioServiceUrl,
                    HttpMethod.POST,
                    entityInventario,
                    ContenedorDTO.class
            ).getBody();

            if (contenedorPersistido == null) {
                logger.error("MS Inventario devolvió body null al crear contenedor");
                throw new IllegalStateException("Inventario no creó el contenedor");
            }

        } catch (HttpStatusCodeException e) {
            logger.error("Error HTTP al llamar MS Inventario: status={}, body={}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        } catch (Exception e) {
            logger.error("Fallo al llamar MS Inventario: {}", e.getMessage(), e);
            throw new IllegalStateException("No se pudo crear el contenedor en Inventario", e);
        }

        // ---------------------------------------------------------
        // 3. LLAMADA A MS VIAJES (Mapeo de Datos y Cálculo)
        // ---------------------------------------------------------
        CalculoEstimadoResponse estimacion = null;

        if (viajesServiceUrl != null) {
            RutaCalculoDTO rutaRequest = new RutaCalculoDTO();

            // Mapeo de datos para el MS Viajes
            rutaRequest.setVolumenContenedorM3(request.getVolumenM3());
            rutaRequest.setPesoContenedorKg(request.getPesoKg());

            List<double[]> puntos = new ArrayList<>();
            puntos.add(new double[]{request.getOrigenLatitud(), request.getOrigenLongitud()});
            puntos.add(new double[]{request.getDestinoLatitud(), request.getDestinoLongitud()});
            rutaRequest.setPuntosRuta(puntos);

            HttpEntity<RutaCalculoDTO> entityViajes = new HttpEntity<>(rutaRequest);

            try {
                logger.debug("Llamando a MS Viajes: {}/rutas/estimacion", viajesServiceUrl);
                // El error 500 ocurre aquí si Viajes falla
                estimacion = internalRestTemplate.exchange(
                        viajesServiceUrl + "/rutas/estimacion",
                        HttpMethod.POST,
                        entityViajes,
                        CalculoEstimadoResponse.class
                ).getBody();

                if (estimacion == null) {
                    logger.warn("MS Viajes devolvió body null para la estimación, se usará 0.0");
                    estimacion = new CalculoEstimadoResponse();
                    estimacion.setCostoEstimado(0.0);
                    estimacion.setTiempoEstimadoHoras(0L);
                }

            } catch (HttpStatusCodeException e) {
                // Capturamos específicamente errores HTTP 4xx o 5xx del servicio hijo
                logger.warn("⚠️ Error de servicio MS Viajes (Cálculo): status={}", e.getStatusCode());
                logger.debug("Response body MS Viajes: {}", e.getResponseBodyAsString());
                estimacion = new CalculoEstimadoResponse();
                estimacion.setCostoEstimado(0.0);
                estimacion.setTiempoEstimadoHoras(0L);
            } catch (Exception e) {
                // Capturamos fallos de conexión (Connection Refused, etc.)
                logger.warn("⚠️ Fallo de conexión MS Viajes: {}", e.getMessage());
                estimacion = new CalculoEstimadoResponse();
                estimacion.setCostoEstimado(0.0);
                estimacion.setTiempoEstimadoHoras(0L);
            }
        }

        // ---------------------------------------------------------
        // 4. PERSISTENCIA FINAL DE LA SOLICITUD
        // ---------------------------------------------------------
        Solicitud solicitud = new Solicitud();
        solicitud.setContenedorId(contenedorPersistido.getId());
        solicitud.setClienteKeycloakId(clienteKeycloakId);
        solicitud.setEstado("PROGRAMADA");
        solicitud.setFechaCreacion(LocalDateTime.now());

        // Guardar estimación (o 0.0 si falló)
        if (estimacion != null) {
            solicitud.setCostoEstimado(estimacion.getCostoEstimado());
            solicitud.setTiempoEstimadoHoras(estimacion.getTiempoEstimadoHoras());
        }

        // Mapeo de Ubicaciones
        solicitud.setOrigenDireccion(request.getOrigenDireccion());
        solicitud.setOrigenLatitud(request.getOrigenLatitud());
        solicitud.setOrigenLongitud(request.getOrigenLongitud());

        solicitud.setDestinoDireccion(request.getDestinoDireccion());
        solicitud.setDestinoLatitud(request.getDestinoLatitud());
        solicitud.setDestinoLongitud(request.getDestinoLongitud());

        return solicitudRepository.save(solicitud);
    }
}