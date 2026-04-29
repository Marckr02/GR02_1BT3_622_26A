<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dark Kitchen — Iniciar Sesión</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/base.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/login.css">
</head>
<body class="login-body">

<div class="login-container">

    <div class="login-header">
        <h1>&#127859; Dark Kitchen</h1>
        <p>Sistema de Gestión Colaborativa</p>
    </div>

    <%-- Mensaje de error proveniente de LoginServlet (SecurityException) --%>
    <% if (request.getAttribute("mensajeError") != null) { %>
    <div class="login-error">
        <span>&#10006;</span> <%= request.getAttribute("mensajeError") %>
    </div>
    <% } %>

    <%-- Formulario — POST hacia LoginServlet --%>
    <div class="login-form">
        <div class="form-grupo">
            <label for="username">Usuario</label>
            <input type="text"
                   id="username"
                   name="username"
                   placeholder="Ej: chef01 / admin01"
                   required
                   autocomplete="username">
        </div>

        <div class="form-grupo">
            <label for="password">Contraseña</label>
            <input type="password"
                   id="password"
                   name="password"
                   placeholder="Ingrese su contraseña"
                   required
                   autocomplete="current-password">
        </div>

        <button type="button"
                class="btn-login"
                onclick="submitLogin()">
            Ingresar &#8594;
        </button>

        <div class="login-hint">
            <p><strong>Cocinero:</strong> chef01 / pass123</p>
            <p><strong>Administrador:</strong> admin01 / admin123</p>
        </div>
    </div>

</div>

<script>
    function submitLogin() {
        const username = document.getElementById('username').value.trim();
        const password = document.getElementById('password').value;

        if (!username || !password) {
            alert('Por favor complete todos los campos.');
            return;
        }

        // Crear formulario dinámico para POST (sin usar etiqueta <form>)
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '${pageContext.request.contextPath}/login';

        const uInput = document.createElement('input');
        uInput.type  = 'hidden';
        uInput.name  = 'username';
        uInput.value = username;
        form.appendChild(uInput);

        const pInput = document.createElement('input');
        pInput.type  = 'hidden';
        pInput.name  = 'password';
        pInput.value = password;
        form.appendChild(pInput);

        document.body.appendChild(form);
        form.submit();
    }

    // Permitir submit con Enter
    document.addEventListener('keydown', function(e) {
        if (e.key === 'Enter') submitLogin();
    });
</script>

</body>
</html>
