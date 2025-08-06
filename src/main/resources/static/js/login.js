document.addEventListener('DOMContentLoaded', function() {
    const form = document.getElementById('loginForm');
    const errorAlert = document.getElementById('errorAlert');
    const successAlert = document.getElementById('successAlert');

    form.addEventListener('submit', function(event) {
        event.preventDefault();

        // Limpar alertas anteriores
        errorAlert.style.display = 'none';
        successAlert.style.display = 'none';

        // Coletar dados do formulário
        const email = document.getElementById('email').value;
        const senha = document.getElementById('senha').value;

        // Buscar todos os clientes e verificar as credenciais
        fetch('/api/clients')
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erro ao buscar clientes');
                }
                return response.json();
            })
            .then(clients => {
                // Procurar por um cliente com o email e senha correspondentes
                const client = clients.find(c => c.email === email && c.password === senha);

                if (client) {
                    // Armazenar informações do cliente na sessão
                    sessionStorage.setItem('clientId', client.id);
                    sessionStorage.setItem('clientName', client.name);
                    sessionStorage.setItem('clientEmail', client.email);

                    // Exibir mensagem de sucesso
                    successAlert.textContent = 'Login realizado com sucesso! Redirecionando...';
                    successAlert.style.display = 'block';

                    // Redirecionar após 1 segundo (aqui você poderia redirecionar para uma página de área do cliente)
                    setTimeout(() => {
                        // Por enquanto, voltamos para a página inicial
                        window.location.href = 'index.html';
                    }, 1000);
                } else {
                    // Credenciais inválidas
                    errorAlert.textContent = 'Email ou senha inválidos. Por favor, tente novamente.';
                    errorAlert.style.display = 'block';
                }
            })
            .catch(error => {
                // Exibir mensagem de erro
                errorAlert.textContent = error.message || 'Ocorreu um erro ao processar sua solicitação';
                errorAlert.style.display = 'block';
            });
    });
});
