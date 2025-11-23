package com.proyectoviajes.usuarioms.service;

import com.proyectoviajes.usuarioms.model.Cliente;
import com.proyectoviajes.usuarioms.dto.ClienteDTO;
import com.proyectoviajes.usuarioms.repository.ClienteRepository;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    /**
     * Busca un cliente existente por su ID de Keycloak o lo registra si no existe.
     * @param keycloakId ID del token JWT
     * @param dto Datos adicionales del cliente
     * @return Cliente persistido
     */
    public Cliente registrarOObtenerCliente(String keycloakId, ClienteDTO dto) {

        Cliente cliente = clienteRepository.findByKeycloakId(keycloakId);

        if (cliente == null) {
            // El cliente es nuevo, lo registramos.
            cliente = new Cliente();
            cliente.setKeycloakId(keycloakId);
            // Copiar datos del DTO (simplificado)
            cliente.setNombre(dto.getNombre());
            cliente.setTelefono(dto.getTelefono());
            cliente.setEmail(dto.getEmail());

            return clienteRepository.save(cliente);
        }

        return cliente;
    }
}