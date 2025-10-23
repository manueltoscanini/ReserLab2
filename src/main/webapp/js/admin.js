function mostrarModalCrearReserva() {
    document.getElementById('modalCrearReserva').style.display = 'flex';
    // Establecer fecha mínima como hoy
    const hoy = new Date().toISOString().split('T')[0];
    document.getElementById('fecha').setAttribute('min', hoy);
}



function cerrarModalCrearReserva() {
    document.getElementById('modalCrearReserva').style.display = 'none';
    document.getElementById('formCrearReserva').reset();
}

// Cerrar modal al hacer clic fuera de él
window.onclick = function(event) {
    const modal = document.getElementById('modalCrearReserva');
    if (event.target === modal) {
        cerrarModalCrearReserva();
    }
}

// Validar que hora fin sea mayor que hora inicio
document.getElementById('formCrearReserva').addEventListener('submit', function(e) {
    const horaInicio = document.getElementById('horaInicio').value;
    const horaFin = document.getElementById('horaFin').value;

    if (horaInicio && horaFin && horaInicio >= horaFin) {
        e.preventDefault();
        alert('La hora de fin debe ser posterior a la hora de inicio');
    }
});

// --- Subir foto de perfil ---
document.addEventListener('DOMContentLoaded', () => {
    const btnCambiarFoto = document.getElementById('btnCambiarFoto');
    const inputFoto = document.getElementById('inputFoto');
    const fotoPerfil = document.getElementById('fotoPerfil');
    const iconoPerfil = document.getElementById('iconoPerfil');

    if (btnCambiarFoto && inputFoto) {
        btnCambiarFoto.addEventListener('click', (e) => {
            e.preventDefault();
            inputFoto.click();
        });

        inputFoto.addEventListener('change', async (e) => {
            const archivo = e.target.files[0];
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

            // Mostrar indicador de carga
            btnCambiarFoto.innerHTML = '<i class="fa-solid fa-spinner fa-spin"></i>';
            btnCambiarFoto.disabled = true;

            try {
                const formData = new FormData();
                formData.append('foto', archivo);

                const response = await fetch('SubirFotoServlet', {
                    method: 'POST',
                    body: formData
                });

                const resultado = await response.json();

                if (resultado.success) {
                    // Actualizar la imagen en la interfaz
                    if (fotoPerfil) {
                        fotoPerfil.src = resultado.fotoUrl;
                    } else if (iconoPerfil) {
                        // Reemplazar el icono con la imagen
                        const container = document.querySelector('.foto-perfil-container');
                        iconoPerfil.style.display = 'none';
                        const nuevaImg = document.createElement('img');
                        nuevaImg.src = resultado.fotoUrl;
                        nuevaImg.alt = 'Foto de perfil';
                        nuevaImg.className = 'fotoPerfil';
                        nuevaImg.id = 'fotoPerfil';
                        container.insertBefore(nuevaImg, container.firstChild);
                    }
                    alert('Foto actualizada correctamente');
                } else {
                    alert('Error al subir la foto: ' + resultado.message);
                }
            } catch (error) {
                console.error('Error:', error);
                alert('Error al subir la foto. Por favor intenta de nuevo.');
            } finally {
                // Restaurar el botón
                btnCambiarFoto.innerHTML = '<i class="fa-solid fa-camera"></i>';
                btnCambiarFoto.disabled = false;
                // Limpiar el input para permitir subir la misma imagen nuevamente
                inputFoto.value = '';
            }
        });
    }
});