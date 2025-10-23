<%@ page import="Models.Usuario" %>
<%@ page import="Models.Usuario" %>
<%@ page import="java.util.List" %>
<%@ page import="DAO.ClienteDAO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<div class="contenido-usuarios">
    <div class="header-reservas">
        <h2 class="titulo-seccion">Usuarios</h2>
        <button class="btn-crear-usuario" onclick="mostrarModalCrearUsuario()">
            <i class="fa-solid fa-plus"></i> Crear Usuario
        </button>
    </div>
    <%
        String mensajeExito = (String) session.getAttribute("exito");
        String mensajeError = (String) session.getAttribute("error");

        if (mensajeExito != null) {
            session.removeAttribute("exito");
    %>
    <div class="mensaje-exito">
        <i class="fa-solid fa-check-circle"></i>
        <%= mensajeExito %>
    </div>
    <%
        }

        if (mensajeError != null) {
            session.removeAttribute("error");
    %>
    <div class="mensaje-error">
        <i class="fa-solid fa-exclamation-circle"></i>
        <%= mensajeError %>
    </div>
    <%
        }
    %>

    <%
        List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
        Integer currentPage = (Integer) request.getAttribute("currentPage");
        Integer totalPages = (Integer) request.getAttribute("totalPages");
        Integer totalUsuarios = (Integer) request.getAttribute("totalUsuarios");
        Boolean hasNextPage = (Boolean) request.getAttribute("hasNextPage");
        Boolean hasPrevPage = (Boolean) request.getAttribute("hasPrevPage");

        if (usuarios != null && !usuarios.isEmpty()) {
    %>
    <div class="grid-usuarios">
        <%
            for (Usuario u : usuarios) {
                String fotoUsuario = u.getFotoUsuario();
                boolean tieneFoto = (fotoUsuario != null && !fotoUsuario.isEmpty());
        %>
        <div class="ficha-usuario">
            <div class="foto-usuario-container">
                <% if (tieneFoto) { %>
                    <img src="<%= fotoUsuario %>" alt="Foto de <%= u.getNombre() %>" class="foto-usuario">
                <% } else { %>
                    <i class="fa-solid fa-user-circle icono-usuario-placeholder"></i>
                <% } %>
            </div>
            <div class="info-usuario">
                <div class="nombre-usuario">
                    <i class="fa-solid fa-user"></i>
                    <span><%= u.getNombre() %></span>
                </div>
                <div class="email-usuario">
                    <i class="fa-solid fa-envelope"></i>
                    <span><%= u.getEmail() %></span>
                </div>
                <div class="cedula-usuario">
                    <i class="fa-solid fa-id-card"></i>
                    <span>CI: <%= u.getCedula() %></span>
                </div>
                <div class="rol-usuario">
                    <% if (u.getEsAdmin()) { %>
                        <span class="badge-admin"><i class="fa-solid fa-shield-halved"></i> Administrador</span>
                    <% } else { %>
                        <span class="badge-usuario"><i class="fa-solid fa-user"></i> Usuario</span>
                    <% } %>
                </div>
            </div>
            <div class="acciones-usuario">
                <button class="btn-editar" onclick="editarUsuario('<%= u.getCedula() %>')" title="Editar usuario">
                    <i class="fa-solid fa-edit"></i>
                </button>
                <% if (!u.getEsAdmin()) { %>
                <button class="btn-eliminar" onclick="eliminarUsuario('<%= u.getCedula() %>')" title="Eliminar usuario">
                    <i class="fa-solid fa-trash"></i>
                </button>
                <% } %>
            </div>
        </div>
        <%
            }
        %>
    </div>

    <!-- Paginación -->
    <%
        if (totalPages != null && totalPages > 1) {
    %>
    <div class="paginacion">
        <%
            if (hasPrevPage != null && hasPrevPage) {
        %>
        <a href="usuarios?page=<%= currentPage - 1 %>" class="btn-paginacion">
            <i class="fa-solid fa-chevron-left"></i> Anterior
        </a>
        <%
            }
        %>

        <span class="info-paginacion">
            Página <%= currentPage %> de <%= totalPages %> (<%= totalUsuarios %> usuarios)
        </span>

        <%
            if (hasNextPage != null && hasNextPage) {
        %>
        <a href="usuarios?page=<%= currentPage + 1 %>" class="btn-paginacion">
            Siguiente <i class="fa-solid fa-chevron-right"></i>
        </a>
        <%
            }
        %>
    </div>
    <%
        }
    %>
    <%
    } else {
    %>
    <div class="sin-usuarios">
        <h3>No hay usuarios disponibles</h3>
        <p>No se encontraron usuarios en el sistema.</p>
    </div>
    <%
        }
    %>
</div>

<!-- Modal para crear usuario -->
<div id="modalCrearUsuario" class="modal">
    <div class="modal-content modal-crear-usuario">
        <div class="modal-header">
            <h3><i class="fa-solid fa-user-plus"></i> Crear Nuevo Usuario</h3>
            <button class="btn-cerrar-modal" onclick="cerrarModalCrearUsuario()">
                <i class="fa-solid fa-times"></i>
            </button>
        </div>
        <form id="formCrearUsuario" action="crear-usuario" method="post">
            <div class="form-grid">
                <div class="form-group">
                    <label for="nombre">
                        <i class="fa-solid fa-user"></i> Nombre completo
                    </label>
                    <input type="text" id="nombre" name="nombre" 
                           placeholder="Nombre completo" required>
                </div>

                <div class="form-group">
                    <label for="emailUsuario">
                        <i class="fa-solid fa-envelope"></i> Email
                    </label>
                    <input type="email" id="emailUsuario" name="email" 
                           required placeholder="ejemplo@gmail.com">
                </div>

                <div class="form-group">
                    <label for="cedulaUsuario">
                        <i class="fa-solid fa-id-card"></i> Cédula
                    </label>
                    <input type="text" id="cedulaUsuario" name="cedula"
                           placeholder="Ej: 12345678" required pattern="[0-9]{7,8}">
                    <small>Solo números, 7 u 8 dígitos</small>
                </div>

                <div class="form-group">
                    <label for="password">
                        <i class="fa-solid fa-lock"></i> Contraseña
                    </label>
                    <input type="password" id="password" name="password" 
                           required minlength="6">
                    <small>Mínimo 6 caracteres</small>
                </div>

                <div class="form-group">
                    <label for="password2">
                        <i class="fa-solid fa-lock"></i> Repetir contraseña
                    </label>
                    <input type="password" id="password2" name="password2" 
                           required minlength="6">
                </div>

                <div class="form-group checkbox-admin">
                    <label class="checkbox-label">
                        <input type="checkbox" id="esAdmin" name="esAdmin" 
                               value="true" onchange="toggleClienteFields()">
                        <span><i class="fa-solid fa-shield-halved"></i> Es Administrador</span>
                    </label>
                    <small>Si es admin, no necesita datos de cliente</small>
                </div>
            </div>

            <!-- Campos de cliente (se ocultan si es admin) -->
            <div id="camposCliente" class="campos-cliente">
                <h4 class="subtitle-form"><i class="fa-solid fa-user-tag"></i> Datos de Cliente</h4>
                <div class="form-grid">
                    <div class="form-group">
                        <label for="tipoCliente">
                            <i class="fa-solid fa-users"></i> Tipo de cliente
                        </label>
                        <select id="tipoCliente" name="tipoCliente">
                            <option value="estudiante">Estudiante</option>
                            <option value="emprendedor">Emprendedor</option>
                            <option value="docente">Docente</option>
                            <option value="invitado">Invitado</option>
                        </select>
                    </div>

                    <div class="form-group" id="carreraWrapper">
                        <label for="carrera">
                            <i class="fa-solid fa-graduation-cap"></i> Carrera (solo estudiantes)
                        </label>
                        <select id="carrera" name="carrera">
                            <option value="">-- Seleccionar carrera --</option>
                            <%
                                // Cargar carreras para el select
                                DAO.ClienteDAO clienteDAO = new DAO.ClienteDAO();
                                try {
                                    java.util.List<String> carreras = clienteDAO.listarCarreras();
                                    if (carreras != null) {
                                        for (String c : carreras) {
                            %>
                            <option value="<%= c %>"><%= c %></option>
                            <%
                                        }
                                    }
                                } catch (Exception ignored) {}
                            %>
                        </select>
                    </div>
                </div>
            </div>

            <div class="modal-footer">
                <button type="button" class="btn-cancelar" onclick="cerrarModalCrearUsuario()">
                    <i class="fa-solid fa-times"></i> Cancelar
                </button>
                <button type="submit" class="btn-guardar">
                    <i class="fa-solid fa-save"></i> Crear Usuario
                </button>
            </div>
        </form>
    </div>
</div>

<script>
function mostrarModalCrearUsuario() {
    document.getElementById('modalCrearUsuario').style.display = 'flex';
    toggleClienteFields(); // Sincronizar estado inicial
}

function cerrarModalCrearUsuario() {
    document.getElementById('modalCrearUsuario').style.display = 'none';
    document.getElementById('formCrearUsuario').reset();
    toggleClienteFields(); // Resetear visibilidad de campos
}

function toggleClienteFields() {
    const esAdmin = document.getElementById('esAdmin').checked;
    const camposCliente = document.getElementById('camposCliente');
    const tipoCliente = document.getElementById('tipoCliente');
    const carrera = document.getElementById('carrera');
    
    if (esAdmin) {
        camposCliente.style.display = 'none';
        tipoCliente.removeAttribute('required');
        carrera.removeAttribute('required');
    } else {
        camposCliente.style.display = 'block';
        tipoCliente.setAttribute('required', 'required');
        syncCarreraField(); // Sincronizar campo de carrera
    }
}

function syncCarreraField() {
    const tipo = document.getElementById('tipoCliente');
    const carrera = document.getElementById('carrera');
    const wrapper = document.getElementById('carreraWrapper');
    
    const esEstudiante = tipo.value === 'estudiante';
    carrera.disabled = !esEstudiante;
    wrapper.style.display = esEstudiante ? 'block' : 'none';
    
    if (esEstudiante) {
        carrera.setAttribute('required', 'required');
    } else {
        carrera.removeAttribute('required');
    }
}

function editarUsuario(cedula) {
    // TODO: Implementar edición de usuario
    alert('Editar usuario con cédula: ' + cedula);
}

function eliminarUsuario(cedula) {
    if (confirm('¿Estás seguro de que quieres desactivar este usuario? El usuario no podrá acceder al sistema.')) {
        // Enviar solicitud AJAX para desactivar el usuario
        fetch('eliminar-usuario', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: 'cedula=' + encodeURIComponent(cedula)
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert('Usuario desactivado correctamente');
                // Recargar la página para mostrar los cambios
                window.location.reload();
            } else {
                alert('Error: ' + data.message);
            }
        })
        .catch(error => {
            console.error('Error:', error);
            alert('Error al desactivar el usuario. Por favor intenta de nuevo.');
        });
    }
}

// Inicializar eventos
document.addEventListener('DOMContentLoaded', function() {
    const tipoCliente = document.getElementById('tipoCliente');
    if (tipoCliente) {
        tipoCliente.addEventListener('change', syncCarreraField);
        syncCarreraField(); // Estado inicial
    }
});

// Cerrar modal al hacer clic fuera de él
window.onclick = function(event) {
    const modal = document.getElementById('modalCrearUsuario');
    if (event.target === modal) {
        cerrarModalCrearUsuario();
    }
}
</script>
