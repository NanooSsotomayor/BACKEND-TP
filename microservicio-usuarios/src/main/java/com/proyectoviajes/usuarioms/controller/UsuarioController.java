package com.proyectoviajes.usuarioms.controller;

import com.proyectoviajes.usuarioms.model.Cliente;
import com.proyectoviajes.usuarioms.dto.ClienteDTO;
import com.proyectoviajes.usuarioms.service.ClienteService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final ClienteService clienteService;

    public UsuarioController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    /**
     * Endpoint para obtener los datos del usuario logueado o registrarlo por primera vez.
     * Este endpoint será llamado por el MS Solicitudes.
     */
    @PostMapping("/validar-o-registrar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Cliente> validarORegistrarCliente(
            @RequestBody ClienteDTO dto,
            @AuthenticationPrincipal Jwt jwt) {

        // Obtenemos el ID de Keycloak (la fuente de identidad)
        String keycloakId = jwt.getSubject();

        Cliente cliente = clienteService.registrarOObtenerCliente(keycloakId, dto);

        return ResponseEntity.ok(cliente);
    }

    /**
     * Endpoint de prueba para Operador (Ejemplo de uso de roles)
     */
    @GetMapping("/admin-test")
    @PreAuthorize("hasRole('OPERADOR')")
    public ResponseEntity<String> testAdmin() {
        return ResponseEntity.ok("Acceso de Operador OK");
    }
}