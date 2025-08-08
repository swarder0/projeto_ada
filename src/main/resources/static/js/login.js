document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('loginForm');
    const errorAlert = document.getElementById('errorAlert');
    const successAlert = document.getElementById('successAlert');
    const passwordField = document.getElementById('password');

    // Validação em tempo real da senha
    passwordField.addEventListener('input', function() {
        if (passwordField.value.length > 0 && passwordField.value.length < 6) {
            passwordField.classList.add('is-invalid');
        } else {
            passwordField.classList.remove('is-invalid');
        }
    });

    form.addEventListener('submit', function(event) {
        event.preventDefault();

        // Limpar alertas anteriores
        errorAlert.style.display = 'none';
        successAlert.style.display = 'none';

        // Coletar dados do formulário
        const email = document.getElementById('email').value;
        const password = passwordField.value;

        // Validar senha mínima de 6 dígitos
        if (password.length < 6) {
            errorAlert.textContent = 'A senha deve ter no mínimo 6 dígitos.';
            errorAlert.style.display = 'block';
            return;
        }

        // Fazer login via API
        const loginData = {
            email: email,
            password: password
        };

        fetch('/api/login', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(loginData)
        })
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.text().then(text => {
                    throw new Error(text || 'Email ou senha inválidos.');
                });
            }
        })
        .then(data => {
            successAlert.textContent = 'Login realizado com sucesso! Redirecionando...';
            successAlert.style.display = 'block';

            // Armazenar dados do usuário no localStorage
            localStorage.setItem('userData', JSON.stringify(data));
            localStorage.setItem('userEmail', email);

            // Redirecionar para o dashboard após 1.5 segundos
            setTimeout(() => {
                window.location.href = 'dashboard.html';
            }, 1500);
        })
        .catch(error => {
            errorAlert.textContent = error.message;
            errorAlert.style.display = 'block';
        });
    });
});
