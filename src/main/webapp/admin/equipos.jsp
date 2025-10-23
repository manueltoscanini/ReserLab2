<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List" %>
<%@ page import="Models.Equipo" %>

<%
    List<Equipo> equipos = (List<Equipo>) request.getAttribute("equipos");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");
    Integer totalEquipos = (Integer) request.getAttribute("totalEquipos");
    Boolean hasNextPage = (Boolean) request.getAttribute("hasNextPage");
    Boolean hasPrevPage = (Boolean) request.getAttribute("hasPrevPage");
%>

<div class="contenido-equipos">
    <div class="header-seccion">
        <h2 class="titulo-seccion"><i class="fa-solid fa-laptop"></i> Gestión de Equipos</h2>
        <button class="btn-nuevo-equipo" onclick="mostrarModalNuevoEquipo()">
            <i class="fa-solid fa-plus"></i> Agregar Equipo
        </button>
    </div>

    <% if (equipos == null || equipos.isEmpty()) { %>
        <div class="sin-equipos">
            <i class="fa-solid fa-laptop"></i>
            <h3>No hay equipos registrados</h3>
            <p>Agrega el primer equipo para comenzar</p>
        </div>
    <% } else { %>
        <div class="grid-equipos">
            <% for (Equipo equipo : equipos) { %>
                <div class="tarjeta-equipo-admin">
                    <div class="equipo-imagen-container">
                        <% if (equipo.getFoto_Equipo() != null && !equipo.getFoto_Equipo().isEmpty()) { %>
                            <img src="<%= equipo.getFoto_Equipo() %>" alt="<%= equipo.getNombre() %>" class="equipo-foto" id="equipo-foto-<%= equipo.getId() %>">
                        <% } else { %>
                            <div class="equipo-sin-foto" id="equipo-foto-<%= equipo.getId() %>">
                                <i class="fa-solid fa-laptop"></i>
                                <span>Sin imagen</span>
                            </div>
                        <% } %>
                        <button class="btn-cambiar-foto-equipo" onclick="cambiarFotoEquipo(<%= equipo.getId() %>)" title="Cambiar foto">
                            <i class="fa-solid fa-camera"></i>
                        </button>
                        <input type="file" id="inputFotoEquipo-<%= equipo.getId() %>" accept="image/*" style="display: none;" onchange="subirFotoEquipo(<%= equipo.getId() %>, this)">
                    </div>
                    
                    <div class="equipo-info">
                        <h3 class="equipo-nombre"><%= equipo.getNombre() %></h3>
                        <div class="equipo-detalle">
                            <i class="fa-solid fa-tag"></i>
                            <span><strong>Tipo:</strong> <%= equipo.getTipo() %></span>
                        </div>
                        <div class="equipo-detalle">
                            <i class="fa-solid fa-exclamation-triangle"></i>
                            <span><strong>Precauciones:</strong> <%= equipo.getPrecauciones() == null || equipo.getPrecauciones().isEmpty() ? "Ninguna" : equipo.getPrecauciones() %></span>
                        </div>
                    </div>
                    
                    <div class="equipo-acciones">
                        <button class="btn-editar-equipo" onclick="editarEquipo(<%= equipo.getId() %>)">
                            <i class="fa-solid fa-edit"></i> Editar
                        </button>
                        <button class="btn-eliminar-equipo" onclick="eliminarEquipo(<%= equipo.getId() %>)">
                            <i class="fa-solid fa-trash"></i> Eliminar
                        </button>
                    </div>
                </div>
            <% } %>
        </div>
        
        <!-- Paginación -->
        <%
            if (totalPages != null && totalPages > 1) {
        %>
        <div class="paginacion">
            <%
                if (hasPrevPage != null && hasPrevPage) {
            %>
            <a href="equipos?page=<%= currentPage - 1 %>" class="btn-paginacion">
                <i class="fa-solid fa-chevron-left"></i> Anterior
            </a>
            <%
                }
            %>

            <span class="info-paginacion">
                Página <%= currentPage %> de <%= totalPages %> (<%= totalEquipos %> equipos)
            </span>

            <%
                if (hasNextPage != null && hasNextPage) {
            %>
            <a href="equipos?page=<%= currentPage + 1 %>" class="btn-paginacion">
                Siguiente <i class="fa-solid fa-chevron-right"></i>
            </a>
            <%
                }
            %>
        </div>
        <%
            }
        %>
    <% } %>
</div>

<script>
function cambiarFotoEquipo(idEquipo) {
    document.getElementById('inputFotoEquipo-' + idEquipo).click();
}

async function subirFotoEquipo(idEquipo, input) {
    const archivo = input.files[0];
    if (!archivo) return;

    // Validar que sea una imagen
    if (!archivo.type.startsWith('image/')) {
        alert('Por favor selecciona un archivo de imagen válido');
        return;
    }

    // Validar tamaño (máximo 10MB)
    if (archivo.size > 10 * 1024 * 1024) {
        alert('La imagen no debe superar los 10MB');
        return;
    }

    try {
        const formData = new FormData();
        formData.append('foto', archivo);
        formData.append('idEquipo', idEquipo);

        const response = await fetch('SubirFotoEquipoServlet', {
            method: 'POST',
            body: formData
        });

        const resultado = await response.json();

        if (resultado.success) {
            // Actualizar la imagen en la interfaz
            const contenedor = document.getElementById('equipo-foto-' + idEquipo);
            if (contenedor.tagName === 'IMG') {
                contenedor.src = resultado.fotoUrl;
            } else {
                // Reemplazar el div con una imagen
                const nuevaImg = document.createElement('img');
                nuevaImg.src = resultado.fotoUrl;
                nuevaImg.alt = 'Equipo';
                nuevaImg.className = 'equipo-foto';
                nuevaImg.id = 'equipo-foto-' + idEquipo;
                contenedor.parentNode.replaceChild(nuevaImg, contenedor);
            }
            alert('Foto actualizada correctamente');
        } else {
            alert('Error al subir la foto: ' + resultado.message);
        }
    } catch (error) {
        console.error('Error:', error);
        alert('Error al subir la foto. Por favor intenta de nuevo.');
    } finally {
        // Limpiar el input
        input.value = '';
    }
}

function mostrarModalNuevoEquipo() {
    alert('Funcionalidad de agregar equipo en desarrollo');
}

function editarEquipo(idEquipo) {
    alert('Funcionalidad de editar equipo en desarrollo');
}

function eliminarEquipo(idEquipo) {
    if (confirm('¿Estás seguro de que deseas eliminar este equipo?')) {
        alert('Funcionalidad de eliminar equipo en desarrollo');
    }
}
</script>
