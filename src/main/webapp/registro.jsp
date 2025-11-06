<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registro - ReserLab</title>
    <link rel="stylesheet" href="estilos/registro.css?v=1.1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <!-- SweetAlert2 -->
    <script src="https://cdn.jsdelivr.net/npm/sweetalert2@11"></script>
</head>
<body>

<div id="contenedorRegistro">

    <h2>Crear cuenta</h2>

    <form action="${pageContext.request.contextPath}/RegistroServlet" method="post">
        <div>
            <label for="nombre">Nombre completo</label>
            <input type="text" id="nombre" name="nombre" placeholder="Tu nombre" value="<%= request.getParameter("nombre") != null ? request.getParameter("nombre") : "" %>" required>
        </div>

        <div class="fila">
            <div>
                <label for="email">Email</label>
                <input type="email" id="email" name="email" placeholder="tucorreo@ejemplo.com" value="<%= request.getParameter("email") != null ? request.getParameter("email") : "" %>" required>
            </div>
            <div>
                <label for="cedula">Cédula</label>
                <input type="text" id="cedula" name="cedula" placeholder="Ej: 4.123.456-7" value="<%= request.getParameter("cedula") != null ? request.getParameter("cedula") : "" %>" required>
            </div>
        </div>

        <div class="fila">
            <div>
                <label for="password">Contraseña</label>
                <input type="password" id="password" name="password" required>
            </div>
            <div>
                <label for="password2">Repetir contraseña</label>
                <input type="password" id="password2" name="password2" required>
            </div>
        </div>

        <div>
            <label for="tipoCliente">Tipo de cliente</label>
            <select id="tipoCliente" name="tipoCliente" required>
                <option value="estudiante" <%= "estudiante".equals(request.getParameter("tipoCliente")) ? "selected" : "" %>>Estudiante</option>
                <option value="emprendedor" <%= "emprendedor".equals(request.getParameter("tipoCliente")) ? "selected" : "" %>>Emprendedor</option>
                <option value="docente" <%= "docente".equals(request.getParameter("tipoCliente")) ? "selected" : "" %>>Docente</option>
                <option value="invitado" <%= "invitado".equals(request.getParameter("tipoCliente")) ? "selected" : "" %>>Invitado</option>
            </select>
            <div class="nota">Si elegís Estudiante, seleccioná también tu carrera.</div>
        </div>

        <div id="carreraWrapper">
            <label for="carrera">Carrera (solo estudiantes)</label>
            <select id="carrera" name="carrera">
                <option value="">-- Seleccioná carrera --</option>
                <%
                    // Obtener carreras de los parámetros de la URL o del atributo de solicitud
                    java.util.List<String> carreras = (java.util.List<String>) request.getAttribute("carreras");
                    String carrerasParam = request.getParameter("carreras");
                    
                    // Si no hay carreras en el atributo, intentar parsear desde el parámetro
                    if (carreras == null && carrerasParam != null && !carrerasParam.isEmpty()) {
                        String[] carrerasArray = carrerasParam.split(",");
                        carreras = new java.util.ArrayList<>();
                        for (String carrera : carrerasArray) {
                            try {
                                carreras.add(java.net.URLDecoder.decode(carrera, "UTF-8"));
                            } catch (Exception e) {
                                carreras.add(carrera);
                            }
                        }
                    }
                    
                    String carreraSeleccionada = request.getParameter("carrera");
                    if (carreras != null) {
                        for (String c : carreras) {
                            String selected = (carreraSeleccionada != null && carreraSeleccionada.equals(c)) ? "selected" : "";
                %>
                <option value="<%= c %>" <%= selected %>><%= c %></option>
                <%
                        }
                    }
                %>
            </select>
        </div>

        <div class="acciones">
            <input type="submit" value="Registrarme">
            <button type="button" onclick="window.location.href='login.jsp'">Ya tengo cuenta</button>
        </div>
    </form>

    <div class="btn-volver-container">
        <button type="button" class="btn-volver" onclick="window.location.href='index.jsp'">
            <i class="fa-solid fa-arrow-left"></i> Volver al inicio
        </button>
    </div>
</div>

<!-- Script para mostrar alertas con SweetAlert -->
<script>
    // Función para obtener parámetros de la URL
    function getParameterByName(name, url = window.location.href) {
        name = name.replace(/[\[\]]/g, '\\$&');
        var regex = new RegExp('[?&]' + name + '(=([^&#]*)|&|#|$)'),
            results = regex.exec(url);
        if (!results) return null;
        if (!results[2]) return '';
        return decodeURIComponent(results[2].replace(/\+/g, ' '));
    }

    // Mostrar alertas con SweetAlert según los parámetros de la URL
    window.addEventListener('DOMContentLoaded', function() {
        var error = getParameterByName('error');
        var exito = getParameterByName('exito');
        
        if (error) {
            Swal.fire({
                icon: 'error',
                title: 'Error',
                text: error,
                confirmButtonText: 'Aceptar'
            });
        }
        
        if (exito) {
            Swal.fire({
                icon: 'success',
                title: 'Éxito',
                text: exito,
                confirmButtonText: 'Aceptar'
            });
        }
    });
</script>

<script>
document.addEventListener('DOMContentLoaded', function(){
  const tipo = document.getElementById('tipoCliente');
  const carrera = document.getElementById('carrera');
  const wrapper = document.getElementById('carreraWrapper');
  function sync(){
    const esEstudiante = tipo.value === 'estudiante';
    carrera.disabled = !esEstudiante;
    wrapper.style.display = esEstudiante ? 'block' : 'none';
  }
  tipo.addEventListener('change', sync);
  sync();
});
</script>

</body>
</html>