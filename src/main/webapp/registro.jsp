<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registro - ReserLab</title>
    <link rel="stylesheet" href="estilos/registro.css?v=1.1">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
</head>
<body>

<div id="contenedorRegistro">
    <h2>Crear cuenta</h2>

    <form action="${pageContext.request.contextPath}/RegistroServlet" method="post">
        <div>
            <label for="nombre">Nombre completo</label>
            <input type="text" id="nombre" name="nombre" placeholder="Tu nombre" required>
        </div>

        <div class="fila">
            <div>
                <label for="email">Email</label>
                <input type="email" id="email" name="email" placeholder="tucorreo@ejemplo.com" required>
            </div>
            <div>
                <label for="cedula">Cédula</label>
                <input type="text" id="cedula" name="cedula" placeholder="Ej: 4.123.456-7" required>
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
                <option value="estudiante">Estudiante</option>
                <option value="emprendedor">Emprendedor</option>
                <option value="docente">Docente</option>
                <option value="invitado">Invitado</option>
            </select>
            <div class="nota">Si elegís Estudiante, seleccioná también tu carrera.</div>
        </div>

        <div id="carreraWrapper">
            <label for="carrera">Carrera (solo estudiantes)</label>
            <select id="carrera" name="carrera">
                <option value="">-- Seleccioná carrera --</option>
                <%
                    java.util.List<String> carreras = (java.util.List<String>) request.getAttribute("carreras");
                    if (carreras != null) {
                        for (String c : carreras) {
                %>
                <option value="<%= c %>"><%= c %></option>
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

<%--
  Created by IntelliJ IDEA.
  User: enzot
  Date: 16/10/2025
  Time: 14:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
</head>
<body>

</body>
</html>
