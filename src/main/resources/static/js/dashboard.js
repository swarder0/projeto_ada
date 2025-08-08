document.addEventListener('DOMContentLoaded', function() {
    // Verificar se o usuário está logado
    const userData = localStorage.getItem('userData');
    if (!userData) {
        window.location.href = 'login.html';
        return;
    }

    const user = JSON.parse(userData);
    loadUserData(user);
    loadTransactions();
});

function loadUserData(user) {
    // Atualizar informações do usuário na tela
    document.getElementById('userName').innerHTML = `
        <i class="fas fa-user-circle me-2"></i>Olá, ${user.name}!
    `;

    if (user.account) {
        document.getElementById('accountNumber').textContent = user.account.accountNumber;
        document.getElementById('balance').textContent = formatCurrency(user.account.balance);
    }
}

function formatCurrency(value) {
    return new Intl.NumberFormat('pt-BR', {
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(value);
}

function logout() {
    localStorage.removeItem('userData');
    localStorage.removeItem('userEmail');
    window.location.href = 'login.html';
}

function processDeposit() {
    const amount = parseFloat(document.getElementById('depositAmount').value);
    const description = document.getElementById('depositDescription').value || 'Depósito';

    const errorDiv = document.getElementById('depositError');
    const successDiv = document.getElementById('depositSuccess');

    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    if (!amount || amount <= 0) {
        errorDiv.textContent = 'Valor inválido para depósito.';
        errorDiv.style.display = 'block';
        return;
    }

    const userData = JSON.parse(localStorage.getItem('userData'));
    const depositData = {
        accountId: userData.account.id,
        amount: amount,
        type: 'DEPOSIT',
        description: description
    };

    fetch('/api/transactions', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(depositData)
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error('Erro ao processar depósito');
    })
    .then(data => {
        successDiv.textContent = 'Depósito realizado com sucesso!';
        successDiv.style.display = 'block';

        // Atualizar saldo
        updateBalance(amount, 'add');

        // Fechar modal após 2 segundos
        setTimeout(() => {
            bootstrap.Modal.getInstance(document.getElementById('depositModal')).hide();
            document.getElementById('depositForm').reset();
            loadTransactions();
        }, 2000);
    })
    .catch(error => {
        errorDiv.textContent = error.message;
        errorDiv.style.display = 'block';
    });
}

function processWithdraw() {
    const amount = parseFloat(document.getElementById('withdrawAmount').value);
    const description = document.getElementById('withdrawDescription').value || 'Saque';

    const errorDiv = document.getElementById('withdrawError');
    const successDiv = document.getElementById('withdrawSuccess');

    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    if (!amount || amount <= 0) {
        errorDiv.textContent = 'Valor inválido para saque.';
        errorDiv.style.display = 'block';
        return;
    }

    const userData = JSON.parse(localStorage.getItem('userData'));
    const currentBalance = parseFloat(document.getElementById('balance').textContent.replace(/\./g, '').replace(',', '.'));

    if (amount > currentBalance) {
        errorDiv.textContent = 'Saldo insuficiente para esta operação.';
        errorDiv.style.display = 'block';
        return;
    }

    const withdrawData = {
        accountId: userData.account.id,
        amount: amount,
        type: 'WITHDRAWAL',
        description: description
    };

    fetch('/api/transactions', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(withdrawData)
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error('Erro ao processar saque');
    })
    .then(data => {
        successDiv.textContent = 'Saque realizado com sucesso!';
        successDiv.style.display = 'block';

        // Atualizar saldo
        updateBalance(amount, 'subtract');

        // Fechar modal após 2 segundos
        setTimeout(() => {
            bootstrap.Modal.getInstance(document.getElementById('withdrawModal')).hide();
            document.getElementById('withdrawForm').reset();
            loadTransactions();
        }, 2000);
    })
    .catch(error => {
        errorDiv.textContent = error.message;
        errorDiv.style.display = 'block';
    });
}

function processTransfer() {
    const destinationAccount = document.getElementById('destinationAccount').value;
    const amount = parseFloat(document.getElementById('transferAmount').value);
    const description = document.getElementById('transferDescription').value || 'Transferência';

    const errorDiv = document.getElementById('transferError');
    const successDiv = document.getElementById('transferSuccess');

    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    if (!destinationAccount) {
        errorDiv.textContent = 'Conta de destino é obrigatória.';
        errorDiv.style.display = 'block';
        return;
    }

    if (!amount || amount <= 0) {
        errorDiv.textContent = 'Valor inválido para transferência.';
        errorDiv.style.display = 'block';
        return;
    }

    const userData = JSON.parse(localStorage.getItem('userData'));
    const currentBalance = parseFloat(document.getElementById('balance').textContent.replace(/\./g, '').replace(',', '.'));

    if (amount > currentBalance) {
        errorDiv.textContent = 'Saldo insuficiente para esta operação.';
        errorDiv.style.display = 'block';
        return;
    }

    const transferData = {
        fromAccountId: userData.account.id,
        toAccountNumber: destinationAccount,
        amount: amount,
        description: description
    };

    fetch('/api/transfers', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(transferData)
    })
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        return response.text().then(text => {
            throw new Error(text || 'Erro ao processar transferência');
        });
    })
    .then(data => {
        successDiv.textContent = 'Transferência realizada com sucesso!';
        successDiv.style.display = 'block';

        // Atualizar saldo
        updateBalance(amount, 'subtract');

        // Fechar modal após 2 segundos
        setTimeout(() => {
            bootstrap.Modal.getInstance(document.getElementById('transferModal')).hide();
            document.getElementById('transferForm').reset();
            loadTransactions();
        }, 2000);
    })
    .catch(error => {
        errorDiv.textContent = error.message;
        errorDiv.style.display = 'block';
    });
}

function updateBalance(amount, operation) {
    const balanceElement = document.getElementById('balance');
    const currentBalance = parseFloat(balanceElement.textContent.replace(/\./g, '').replace(',', '.'));

    let newBalance;
    if (operation === 'add') {
        newBalance = currentBalance + amount;
    } else if (operation === 'subtract') {
        newBalance = currentBalance - amount;
    }

    balanceElement.textContent = formatCurrency(newBalance);

    // Atualizar também no localStorage
    const userData = JSON.parse(localStorage.getItem('userData'));
    userData.account.balance = newBalance;
    localStorage.setItem('userData', JSON.stringify(userData));
}

function loadTransactions() {
    const userData = JSON.parse(localStorage.getItem('userData'));
    const transactionsList = document.getElementById('transactionsList');

    // Simular transações (em um cenário real, isso viria do backend)
    const mockTransactions = [
        {
            id: 1,
            type: 'DEPOSIT',
            amount: 1000.00,
            description: 'Depósito inicial',
            date: new Date().toISOString(),
            status: 'COMPLETED'
        },
        {
            id: 2,
            type: 'WITHDRAWAL',
            amount: 200.00,
            description: 'Saque no ATM',
            date: new Date(Date.now() - 86400000).toISOString(),
            status: 'COMPLETED'
        }
    ];

    if (mockTransactions.length === 0) {
        transactionsList.innerHTML = '<p class="text-center text-muted">Nenhuma transação encontrada</p>';
        return;
    }

    transactionsList.innerHTML = mockTransactions.map(transaction => {
        const isCredit = transaction.type === 'DEPOSIT' || transaction.type === 'TRANSFER_IN';
        const icon = isCredit ? 'fas fa-arrow-up' : 'fas fa-arrow-down';
        const iconClass = isCredit ? 'transaction-in' : 'transaction-out';
        const amountPrefix = isCredit ? '+' : '-';

        return `
            <div class="transaction-item">
                <div class="d-flex align-items-center">
                    <div class="transaction-icon ${iconClass}">
                        <i class="${icon}"></i>
                    </div>
                    <div>
                        <strong>${transaction.description}</strong>
                        <div class="text-muted small">
                            ${new Date(transaction.date).toLocaleDateString('pt-BR')}
                        </div>
                    </div>
                </div>
                <div class="text-end">
                    <strong class="${isCredit ? 'text-success' : 'text-danger'}">
                        ${amountPrefix} R$ ${formatCurrency(transaction.amount)}
                    </strong>
                    <div class="text-muted small">${transaction.status}</div>
                </div>
            </div>
        `;
    }).join('');
}

function showStatement() {
    // Em um cenário real, isso abriria uma nova página ou modal com o extrato completo
    alert('Funcionalidade de extrato completo será implementada em breve!');
}

// Atualizar dados periodicamente (simulação)
setInterval(() => {
    const userData = localStorage.getItem('userData');
    if (userData) {
        // Em um cenário real, aqui faria uma chamada ao backend para buscar dados atualizados
        loadTransactions();
    }
}, 30000); // Atualiza a cada 30 segundos
