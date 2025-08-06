document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('cadastroForm');
    const errorAlert = document.getElementById('errorAlert');
    const successAlert = document.getElementById('successAlert');

    form.addEventListener('submit', function(event) {
        event.preventDefault();

        // Limpar alertas anteriores
        errorAlert.style.display = 'none';
        successAlert.style.display = 'none';

        // Validação de senha
        const senha = document.getElementById('senha').value;
        const repetirSenha = document.getElementById('repetirSenha').value;
        if (senha.length < 6) {
            errorAlert.textContent = 'A senha deve ter no mínimo 6 dígitos.';
            errorAlert.style.display = 'block';
            return;
        }
        if (senha !== repetirSenha) {
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
            password: senha,
            isActive: true
        };

        // Enviar para a API
        fetch('/api/clients', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(clientData)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Erro ao cadastrar cliente');
            }
            return response.json();
        })
        .then(data => {
            // Exibir mensagem de sucesso
            successAlert.textContent = 'Cliente cadastrado com sucesso! Conta criada com número: ' +
                                     (data.account ? data.account.accountNumber : 'Indisponível');
            successAlert.style.display = 'block';

            // Limpar formulário
            form.reset();

            // Redirecionar após 2 segundos
            setTimeout(() => {
                window.location.href = 'index.html';
            }, 2000);
        })
        .catch(error => {
            // Exibir mensagem de erro
            errorAlert.textContent = error.message || 'Ocorreu um erro ao processar sua solicitação';
            errorAlert.style.display = 'block';
        });
    });
});
