document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('cadastroForm');
    const errorAlert = document.getElementById('errorAlert');
    const successAlert = document.getElementById('successAlert');

    // Validação em tempo real da senha
    const senha = document.getElementById('senha');
    const repetirSenha = document.getElementById('repetirSenha');

    function validarSenhas() {
        if (senha.value.length > 0 && senha.value.length < 6) {
            senha.classList.add('is-invalid');
        } else {
            senha.classList.remove('is-invalid');
        }

        if (repetirSenha.value.length > 0 && senha.value !== repetirSenha.value) {
            repetirSenha.classList.add('is-invalid');
        } else {
            repetirSenha.classList.remove('is-invalid');
        }
    }

    senha.addEventListener('input', validarSenhas);
    repetirSenha.addEventListener('input', validarSenhas);

    form.addEventListener('submit', function(event) {
        event.preventDefault();

        // Limpar alertas anteriores
        errorAlert.style.display = 'none';
        successAlert.style.display = 'none';

        // Validação de senha
        const senhaValue = senha.value;
        const repetirSenhaValue = repetirSenha.value;

        if (senhaValue.length < 6) {
            errorAlert.textContent = 'A senha deve ter no mínimo 6 dígitos.';
            errorAlert.style.display = 'block';
            return;
        }

        if (senhaValue !== repetirSenhaValue) {
            errorAlert.textContent = 'As senhas não coincidem.';
            errorAlert.style.display = 'block';
            return;
        }

        // Coletar dados do formulário
        const clientData = {
            name: document.getElementById('nome').value,
            email: document.getElementById('email').value,
            cpf: document.getElementById('cpf').value,
            birthDate: document.getElementById('nascimento').value,
            address: {
                street: document.getElementById('rua').value,
                number: document.getElementById('numero').value,
                complement: document.getElementById('complemento').value,
                neighborhood: document.getElementById('bairro').value,
                city: document.getElementById('cidade').value,
                state: document.getElementById('estado').value,
                zipCode: document.getElementById('cep').value
            },
            phone: {
                countryCode: document.getElementById('codigoPais').value.replace('+', ''),
                areaCode: document.getElementById('ddd').value,
                numberCode: document.getElementById('telefone').value
            },
            password: senhaValue,
            isActive: true
        };

        // Enviar dados para o servidor
        fetch('/api/clients', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(clientData)
        })
        .then(response => {
            if (!response.ok) {
                return response.text().then(text => {
                    throw new Error(text);
                });
            }
            return response.json();
        })
        .then(data => {
            successAlert.innerHTML = `
                <strong>Cadastro realizado com sucesso!</strong><br>
                Conta criada: ${data.account.accountNumber}<br>
                <a href="login.html" class="btn btn-primary mt-2">Fazer Login</a>
            `;
            successAlert.style.display = 'block';
            form.reset();
        })
        .catch(error => {
            errorAlert.textContent = error.message;
            errorAlert.style.display = 'block';
        });
    });
});
