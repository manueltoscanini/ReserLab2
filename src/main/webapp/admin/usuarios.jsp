<%@ page import="Models.Usuario" %>
<%@ page import="Models.Usuario" %>
<%@ page import="java.util.List" %>
<%@ page import="DAO.ClienteDAO" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>


<div class="contenido-usuarios">
    <style>
    .acciones-usuarios-header { display:flex; gap:12px; align-items:center; }
    .acciones-usuarios-header .search-input {
        padding: 8px 12px;
        border: 1px solid #d0d5dd;
        border-radius: 8px;
        background: #fff;
        outline: none;
        transition: border-color .2s ease, box-shadow .2s ease;
    }
    .acciones-usuarios-header .search-input::placeholder { color: #98a2b3; }
    .acciones-usuarios-header .search-input:focus {
        border-color: #7aa7ff;
        box-shadow: 0 0 0 3px rgba(122,167,255,.2);
    }
    /* Espacios internos en los modales para que botones no queden pegados */
    #modalEditarUsuario .modal-content, #modalCrearUsuario .modal-content { padding: 24px; border-radius: 12px; }
    #modalEditarUsuario .modal-header, #modalCrearUsuario .modal-header { margin-bottom: 16px; }
    #modalEditarUsuario .modal-footer, #modalCrearUsuario .modal-footer {
        margin-top: 16px;
        padding-top: 16px;
        display: flex;
        gap: 12px;
        justify-content: flex-end;
    }
    </style>
    <div class="header-reservas">
        <h2 class="titulo-seccion">Usuarios</h2>
        <div class="acciones-usuarios-header">
            <form action="usuarios" method="get" style="display:flex; gap:8px; align-items:center;">
                <input class="search-input" type="text" name="q" placeholder="Buscar por nombre" value="<%= request.getAttribute("q") != null ? request.getAttribute("q") : "" %>" />
                <button type="submit" class="btn-crear-usuario" style="padding:8px 12px;">
                    <i class="fa-solid fa-magnifying-glass"></i> Buscar
                </button>
            </form>
            <button class="btn-crear-usuario" onclick="mostrarModalCrearUsuario()">
                <i class="fa-solid fa-plus"></i> Crear Usuario
            </button>
        </div>
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
                <% if (u.getEsAdmin()) { %>
                    <button class="btn-editar" disabled title="No se puede editar un administrador" style="opacity:.6; cursor:not-allowed;">
                        <i class="fa-solid fa-edit"></i>
                    </button>
                <% } else { %>
                    <button class="btn-editar" onclick="editarUsuario('<%= u.getCedula() %>')" title="Editar usuario">
                        <i class="fa-solid fa-edit"></i>
                    </button>
                    <button class="btn-eliminar" onclick="confirmarEliminarUsuario('<%= u.getCedula() %>')" title="Eliminar usuario">
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
        <a href="usuarios?page=<%= currentPage - 1 %><%= request.getAttribute("q") != null ? "&q=" + java.net.URLEncoder.encode((String)request.getAttribute("q"), java.nio.charset.StandardCharsets.UTF_8) : "" %>" class="btn-paginacion">
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
        <a href="usuarios?page=<%= currentPage + 1 %><%= request.getAttribute("q") != null ? "&q=" + java.net.URLEncoder.encode((String)request.getAttribute("q"), java.nio.charset.StandardCharsets.UTF_8) : "" %>" class="btn-paginacion">
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

<!-- Modal confirmación eliminar usuario -->
<div id="modalConfirmarEliminar" class="modal" style="display:none;">
    <div class="modal-content modal-crear-usuario">
        <div class="modal-header">
            <h3><i class="fa-solid fa-triangle-exclamation"></i> Confirmar eliminación</h3>
            <button class="btn-cerrar-modal" onclick="cerrarModalConfirmarEliminar()">
                <i class="fa-solid fa-times"></i>
            </button>
        </div>
        <div class="modal-body">
            <p>¿Estás seguro de desactivar este usuario? No podrá acceder al sistema.</p>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn-cancelar" onclick="cerrarModalConfirmarEliminar()">
                <i class="fa-solid fa-times"></i> Cancelar
            </button>
            <button type="button" class="btn-guardar" onclick="eliminarUsuarioConfirmado()">
                <i class="fa-solid fa-trash"></i> Desactivar
            </button>
        </div>
    </div>
</div>

<!-- Modal para editar usuario (nombre/email) -->
<div id="modalEditarUsuario" class="modal" style="display:none;">
    <div class="modal-content modal-crear-usuario">
        <div class="modal-header">
            <h3><i class="fa-solid fa-user-pen"></i> Editar usuario</h3>
            <button class="btn-cerrar-modal" onclick="cerrarModalEditarUsuario()">
                <i class="fa-solid fa-times"></i>
            </button>
        </div>
        <form id="formEditarUsuario">
            <input type="hidden" id="editCedula" name="cedula" />
            <div class="form-grid">
                <div class="form-group">
                    <label for="editNombre">
                        <i class="fa-solid fa-user"></i> Nombre
                    </label>
                    <input type="text" id="editNombre" name="nombre" required />
                </div>
                <div class="form-group">
                    <label for="editEmail">
                        <i class="fa-solid fa-envelope"></i> Email
                    </label>
                    <input type="email" id="editEmail" name="email" required />
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-cancelar" onclick="cerrarModalEditarUsuario()">
                    <i class="fa-solid fa-times"></i> Cancelar
                </button>
                <button type="submit" class="btn-guardar">
                    <i class="fa-solid fa-save"></i> Guardar cambios
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
    // Buscar la ficha del usuario por la cédula y extraer nombre/email actuales
    const fichas = document.querySelectorAll('.ficha-usuario');
    let nombre = '';
    let email = '';
    fichas.forEach(f => {
        const ci = f.querySelector('.cedula-usuario span');
        if (ci && ci.textContent.includes(cedula)) {
            const nombreEl = f.querySelector('.nombre-usuario span');
            const emailEl = f.querySelector('.email-usuario span');
            if (nombreEl) nombre = nombreEl.textContent.trim();
            if (emailEl) email = emailEl.textContent.trim();
        }
    });

    document.getElementById('editCedula').value = cedula;
    document.getElementById('editNombre').value = nombre;
    document.getElementById('editEmail').value = email;
    document.getElementById('modalEditarUsuario').style.display = 'flex';
}

function cerrarModalEditarUsuario() {
    document.getElementById('modalEditarUsuario').style.display = 'none';
    document.getElementById('formEditarUsuario').reset();
}

let cedulaPendienteEliminar = null;
function confirmarEliminarUsuario(cedula) {
    cedulaPendienteEliminar = cedula;
    document.getElementById('modalConfirmarEliminar').style.display = 'flex';
}

function cerrarModalConfirmarEliminar() {
    document.getElementById('modalConfirmarEliminar').style.display = 'none';
    cedulaPendienteEliminar = null;
}

function eliminarUsuarioConfirmado() {
    if (!cedulaPendienteEliminar) return;
    fetch('eliminar-usuario', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: 'cedula=' + encodeURIComponent(cedulaPendienteEliminar)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            if (typeof showToast === 'function') {
                showToast('success', 'Usuario desactivado correctamente');
            }
            window.location.reload();
        } else {
            if (typeof showToast === 'function') {
                showToast('error', 'Error: ' + (data.message || 'No se pudo desactivar'));
            }
        }
    })
    .catch(error => {
        console.error('Error:', error);
        if (typeof showToast === 'function') {
            showToast('error', 'Error al desactivar el usuario. Por favor intenta de nuevo.');
        }
    })
    .finally(() => cerrarModalConfirmarEliminar());
}

// Inicializar eventos
document.addEventListener('DOMContentLoaded', function() {
    const tipoCliente = document.getElementById('tipoCliente');
    if (tipoCliente) {
        tipoCliente.addEventListener('change', syncCarreraField);
        syncCarreraField(); // Estado inicial
    }

    const formEditar = document.getElementById('formEditarUsuario');
    if (formEditar) {
        formEditar.addEventListener('submit', async function(e) {
            e.preventDefault();
            const formData = new URLSearchParams(new FormData(formEditar));
            try {
                const resp = await fetch('editar-usuario', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: formData.toString()
                });
                const data = await resp.json();
                if (data.success) {
                    if (typeof showToast === 'function') {
                        showToast('success', 'Usuario actualizado correctamente');
                    }
                    window.location.reload();
                } else {
                    if (typeof showToast === 'function') {
                        showToast('error', 'Error: ' + (data.message || 'No se pudo actualizar'));
                    }
                }
            } catch (err) {
                console.error(err);
                if (typeof showToast === 'function') {
                    showToast('error', 'Error al actualizar el usuario');
                }
            }
        });
    }
});

// Cerrar modal al hacer clic fuera de él
window.onclick = function(event) {
    const modalCrear = document.getElementById('modalCrearUsuario');
    if (event.target === modalCrear) {
        cerrarModalCrearUsuario();
    }
    const modalEditar = document.getElementById('modalEditarUsuario');
    if (event.target === modalEditar) {
        cerrarModalEditarUsuario();
    }
    const modalEliminar = document.getElementById('modalConfirmarEliminar');
    if (event.target === modalEliminar) {
        cerrarModalConfirmarEliminar();
    }

}
</script>

