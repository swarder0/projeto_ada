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

        // Atualizar saldos nos modais quando abertos
        updateModalBalances(user.account.balance);

        // Atualizar informações do extrato
        document.getElementById('accountHolderName').textContent = user.name;
        document.getElementById('accountNumberStatement').textContent = user.account.accountNumber;
    }
}

function updateModalBalances(balance) {
    // Atualizar saldo exibido nos modais de transferência e saque
    const balanceElements = document.querySelectorAll('#currentBalanceTransfer, #currentBalanceWithdraw');
    balanceElements.forEach(element => {
        if (element) {
            element.textContent = formatCurrency(balance);
        }
    });
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
    const depositType = document.getElementById('depositType').value;
    const description = document.getElementById('depositDescription').value;

    // Criar descrição completa baseada no tipo e descrição adicional
    let fullDescription = 'Depósito';
    if (depositType) {
        fullDescription = `Depósito via ${depositType}`;
        if (description) {
            fullDescription += ` - ${description}`;
        }
    } else if (description) {
        fullDescription = `Depósito - ${description}`;
    }

    const errorDiv = document.getElementById('depositError');
    const successDiv = document.getElementById('depositSuccess');

    // Reset alerts
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    // Validações com mensagens mais descritivas
    if (!amount || amount <= 0) {
        errorDiv.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>Por favor, informe um valor válido para o depósito (mínimo R$ 0,01).';
        errorDiv.style.display = 'block';
        return;
    }

    if (amount > 999999.99) {
        errorDiv.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>Valor muito alto. Para depósitos acima de R$ 999.999,99, entre em contato com nossa agência.';
        errorDiv.style.display = 'block';
        return;
    }

    const userData = JSON.parse(localStorage.getItem('userData'));
    const depositData = {
        accountId: userData.account.id,
        amount: amount,
        type: 'DEPOSIT',
        description: fullDescription
    };

    // Mostrar indicador de carregamento
    const confirmBtn = document.querySelector('#depositModal .btn-primary');
    const originalText = confirmBtn.innerHTML;
    confirmBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Processando...';
    confirmBtn.disabled = true;

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
        throw new Error('Erro ao processar depósito. Tente novamente.');
    })
    .then(data => {
        successDiv.innerHTML = `
            <i class="fas fa-check-circle me-2"></i>
            <strong>Depósito realizado com sucesso!</strong><br>
            Valor: R$ ${formatCurrency(amount)}<br>
            Novo saldo: R$ ${formatCurrency(userData.account.balance + amount)}
        `;
        successDiv.style.display = 'block';

        // Atualizar saldo
        updateBalance(amount, 'add');

        // Fechar modal após 3 segundos
        setTimeout(() => {
            bootstrap.Modal.getInstance(document.getElementById('depositModal')).hide();
            document.getElementById('depositForm').reset();
            loadTransactions();
        }, 3000);
    })
    .catch(error => {
        errorDiv.innerHTML = `<i class="fas fa-exclamation-triangle me-2"></i>${error.message}`;
        errorDiv.style.display = 'block';
    })
    .finally(() => {
        // Restaurar botão
        confirmBtn.innerHTML = originalText;
        confirmBtn.disabled = false;
    });
}

function processWithdraw() {
    const amount = parseFloat(document.getElementById('withdrawAmount').value);
    const withdrawMethod = document.getElementById('withdrawMethod').value;
    const description = document.getElementById('withdrawDescription').value;

    // Criar descrição completa baseada no método e descrição adicional
    let fullDescription = 'Saque';
    if (withdrawMethod) {
        const methodNames = {
            'ATM': 'Caixa Eletrônico',
            'AGENCIA': 'Agência Bancária',
            'CORRESPONDENTE': 'Correspondente Bancário',
            'PIX': 'PIX'
        };
        fullDescription = `Saque via ${methodNames[withdrawMethod] || withdrawMethod}`;
        if (description) {
            fullDescription += ` - ${description}`;
        }
    } else if (description) {
        fullDescription = `Saque - ${description}`;
    }

    const errorDiv = document.getElementById('withdrawError');
    const successDiv = document.getElementById('withdrawSuccess');

    // Reset alerts
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    // Validações com mensagens mais descritivas
    if (!amount || amount <= 0) {
        errorDiv.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>Por favor, informe um valor válido para o saque (mínimo R$ 0,01).';
        errorDiv.style.display = 'block';
        return;
    }

    const userData = JSON.parse(localStorage.getItem('userData'));
    const currentBalance = parseFloat(document.getElementById('balance').textContent.replace(/\./g, '').replace(',', '.'));

    if (amount > currentBalance) {
        errorDiv.innerHTML = `
            <i class="fas fa-exclamation-triangle me-2"></i>
            <strong>Saldo insuficiente!</strong><br>
            Valor solicitado: R$ ${formatCurrency(amount)}<br>
            Saldo disponível: R$ ${formatCurrency(currentBalance)}
        `;
        errorDiv.style.display = 'block';
        return;
    }

    const withdrawData = {
        accountId: userData.account.id,
        amount: amount,
        type: 'WITHDRAWAL',
        description: fullDescription
    };

    // Mostrar indicador de carregamento
    const confirmBtn = document.querySelector('#withdrawModal .btn-primary');
    const originalText = confirmBtn.innerHTML;
    confirmBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Processando...';
    confirmBtn.disabled = true;

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
        throw new Error('Erro ao processar saque. Tente novamente.');
    })
    .then(data => {
        successDiv.innerHTML = `
            <i class="fas fa-check-circle me-2"></i>
            <strong>Saque realizado com sucesso!</strong><br>
            Valor: R$ ${formatCurrency(amount)}<br>
            Novo saldo: R$ ${formatCurrency(currentBalance - amount)}
        `;
        successDiv.style.display = 'block';

        // Atualizar saldo
        updateBalance(amount, 'subtract');

        // Fechar modal após 3 segundos
        setTimeout(() => {
            bootstrap.Modal.getInstance(document.getElementById('withdrawModal')).hide();
            document.getElementById('withdrawForm').reset();
            loadTransactions();
        }, 3000);
    })
    .catch(error => {
        errorDiv.innerHTML = `<i class="fas fa-exclamation-triangle me-2"></i>${error.message}`;
        errorDiv.style.display = 'block';
    })
    .finally(() => {
        // Restaurar botão
        confirmBtn.innerHTML = originalText;
        confirmBtn.disabled = false;
    });
}

function processTransfer() {
    const destinationAccount = document.getElementById('destinationAccount').value.trim();
    const amount = parseFloat(document.getElementById('transferAmount').value);
    const description = document.getElementById('transferDescription').value || 'Transferência';

    const errorDiv = document.getElementById('transferError');
    const successDiv = document.getElementById('transferSuccess');

    // Reset alerts
    errorDiv.style.display = 'none';
    successDiv.style.display = 'none';

    // Validações com mensagens mais descritivas
    if (!destinationAccount) {
        errorDiv.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>Por favor, informe o número da conta de destino.';
        errorDiv.style.display = 'block';
        return;
    }

    if (!amount || amount <= 0) {
        errorDiv.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>Por favor, informe um valor válido para transferência (mínimo R$ 0,01).';
        errorDiv.style.display = 'block';
        return;
    }

    const userData = JSON.parse(localStorage.getItem('userData'));
    const currentBalance = parseFloat(document.getElementById('balance').textContent.replace(/\./g, '').replace(',', '.'));

    if (amount > currentBalance) {
        errorDiv.innerHTML = `
            <i class="fas fa-exclamation-triangle me-2"></i>
            <strong>Saldo insuficiente!</strong><br>
            Valor da transferência: R$ ${formatCurrency(amount)}<br>
            Saldo disponível: R$ ${formatCurrency(currentBalance)}
        `;
        errorDiv.style.display = 'block';
        return;
    }

    // Verificar se não está transferindo para a própria conta
    if (destinationAccount === userData.account.accountNumber) {
        errorDiv.innerHTML = '<i class="fas fa-exclamation-triangle me-2"></i>Você não pode transferir para sua própria conta.';
        errorDiv.style.display = 'block';
        return;
    }

    const transferData = {
        fromAccountId: userData.account.id,
        toAccountNumber: destinationAccount,
        amount: amount,
        description: description
    };

    // Mostrar indicador de carregamento
    const confirmBtn = document.querySelector('#transferModal .btn-primary');
    const originalText = confirmBtn.innerHTML;
    confirmBtn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Processando...';
    confirmBtn.disabled = true;

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
        successDiv.innerHTML = `
            <i class="fas fa-check-circle me-2"></i>
            <strong>Transferência realizada com sucesso!</strong><br>
            Para: ${destinationAccount}<br>
            Valor: R$ ${formatCurrency(amount)}<br>
            Novo saldo: R$ ${formatCurrency(currentBalance - amount)}
        `;
        successDiv.style.display = 'block';

        // Atualizar saldo
        updateBalance(amount, 'subtract');

        // Fechar modal após 3 segundos
        setTimeout(() => {
            bootstrap.Modal.getInstance(document.getElementById('transferModal')).hide();
            document.getElementById('transferForm').reset();
            loadTransactions();
        }, 3000);
    })
    .catch(error => {
        errorDiv.innerHTML = `<i class="fas fa-exclamation-triangle me-2"></i>${error.message}`;
        errorDiv.style.display = 'block';
    })
    .finally(() => {
        // Restaurar botão
        confirmBtn.innerHTML = originalText;
        confirmBtn.disabled = false;
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

    // Expandir transações simuladas com mais detalhes
    const mockTransactions = [
        {
            id: 1,
            type: 'DEPOSIT',
            amount: 1500.00,
            description: 'Depósito via PIX - Salário',
            date: new Date().toISOString(),
            status: 'COMPLETED',
            reference: 'DEP001'
        },
        {
            id: 2,
            type: 'WITHDRAWAL',
            amount: 200.00,
            description: 'Saque via Caixa Eletrônico - Compras',
            date: new Date(Date.now() - 86400000).toISOString(),
            status: 'COMPLETED',
            reference: 'SAQ001'
        },
        {
            id: 3,
            type: 'TRANSFER_OUT',
            amount: 150.00,
            description: 'Transferência - Pagamento de conta',
            date: new Date(Date.now() - 172800000).toISOString(),
            status: 'COMPLETED',
            reference: 'TRF001',
            destinationAccount: '98765-4'
        },
        {
            id: 4,
            type: 'DEPOSIT',
            amount: 300.00,
            description: 'Depósito via TED - Freelance',
            date: new Date(Date.now() - 259200000).toISOString(),
            status: 'COMPLETED',
            reference: 'DEP002'
        }
    ];

    if (mockTransactions.length === 0) {
        transactionsList.innerHTML = `
            <div class="text-center text-muted py-4">
                <i class="fas fa-inbox fa-2x mb-3"></i>
                <p>Nenhuma transação encontrada</p>
                <small>Suas movimentações aparecerão aqui</small>
            </div>
        `;
        return;
    }

    transactionsList.innerHTML = mockTransactions.slice(0, 5).map(transaction => {
        const isCredit = transaction.type === 'DEPOSIT' || transaction.type === 'TRANSFER_IN';
        const icon = getTransactionIcon(transaction.type);
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
                            <i class="fas fa-calendar me-1"></i>
                            ${new Date(transaction.date).toLocaleDateString('pt-BR', {
                                day: '2-digit',
                                month: '2-digit',
                                year: 'numeric',
                                hour: '2-digit',
                                minute: '2-digit'
                            })}
                        </div>
                        ${transaction.reference ? `<div class="text-muted small">
                            <i class="fas fa-hashtag me-1"></i>Ref: ${transaction.reference}
                        </div>` : ''}
                    </div>
                </div>
                <div class="text-end">
                    <strong class="${isCredit ? 'text-success' : 'text-danger'}">
                        ${amountPrefix} R$ ${formatCurrency(transaction.amount)}
                    </strong>
                    <div class="text-muted small">
                        <i class="fas fa-check-circle me-1"></i>${getStatusText(transaction.status)}
                    </div>
                </div>
            </div>
        `;
    }).join('');
}

function getTransactionIcon(type) {
    const icons = {
        'DEPOSIT': 'fas fa-arrow-down',
        'WITHDRAWAL': 'fas fa-arrow-up',
        'TRANSFER_OUT': 'fas fa-arrow-right',
        'TRANSFER_IN': 'fas fa-arrow-left'
    };
    return icons[type] || 'fas fa-exchange-alt';
}

function getStatusText(status) {
    const statusTexts = {
        'COMPLETED': 'Concluída',
        'PENDING': 'Pendente',
        'FAILED': 'Falhou',
        'CANCELLED': 'Cancelada'
    };
    return statusTexts[status] || status;
}

function showStatement() {
    // Abrir modal de extrato detalhado
    const statementModal = new bootstrap.Modal(document.getElementById('statementModal'));
    statementModal.show();

    // Definir datas padrão (últimos 30 dias)
    const today = new Date();
    const thirtyDaysAgo = new Date(today.getTime() - (30 * 24 * 60 * 60 * 1000));

    document.getElementById('endDate').valueAsDate = today;
    document.getElementById('startDate').valueAsDate = thirtyDaysAgo;

    // Carregar extrato
    loadStatementTransactions();
}

function loadStatementTransactions() {
    const statementDiv = document.getElementById('statementTransactions');

    // Simular carregamento
    statementDiv.innerHTML = `
        <div class="text-center py-4">
            <i class="fas fa-spinner fa-spin fa-2x text-primary"></i>
            <p class="mt-2">Carregando suas transações...</p>
        </div>
    `;

    setTimeout(() => {
        // Usar as mesmas transações mock mas com mais detalhes
        const allTransactions = [
            {
                id: 1,
                type: 'DEPOSIT',
                amount: 1500.00,
                description: 'Depósito via PIX - Salário',
                date: new Date().toISOString(),
                status: 'COMPLETED',
                reference: 'DEP001',
                balance: 2350.00
            },
            {
                id: 2,
                type: 'WITHDRAWAL',
                amount: 200.00,
                description: 'Saque via Caixa Eletrônico - Compras',
                date: new Date(Date.now() - 86400000).toISOString(),
                status: 'COMPLETED',
                reference: 'SAQ001',
                balance: 850.00
            },
            {
                id: 3,
                type: 'TRANSFER_OUT',
                amount: 150.00,
                description: 'Transferência - Pagamento de conta',
                date: new Date(Date.now() - 172800000).toISOString(),
                status: 'COMPLETED',
                reference: 'TRF001',
                destinationAccount: '98765-4',
                balance: 1050.00
            },
            {
                id: 4,
                type: 'DEPOSIT',
                amount: 300.00,
                description: 'Depósito via TED - Freelance',
                date: new Date(Date.now() - 259200000).toISOString(),
                status: 'COMPLETED',
                reference: 'DEP002',
                balance: 1200.00
            }
        ];

        if (allTransactions.length === 0) {
            statementDiv.innerHTML = `
                <div class="text-center text-muted py-5">
                    <i class="fas fa-file-alt fa-3x mb-3"></i>
                    <h5>Nenhuma transação encontrada</h5>
                    <p>Não há movimentações no período selecionado</p>
                </div>
            `;
            return;
        }

        statementDiv.innerHTML = `
            <div class="table-responsive">
                <table class="table table-hover">
                    <thead class="table-light">
                        <tr>
                            <th><i class="fas fa-calendar me-2"></i>Data/Hora</th>
                            <th><i class="fas fa-list me-2"></i>Descrição</th>
                            <th><i class="fas fa-tag me-2"></i>Referência</th>
                            <th><i class="fas fa-dollar-sign me-2"></i>Valor</th>
                            <th><i class="fas fa-wallet me-2"></i>Saldo</th>
                            <th><i class="fas fa-info-circle me-2"></i>Status</th>
                        </tr>
                    </thead>
                    <tbody>
                        ${allTransactions.map(transaction => {
                            const isCredit = transaction.type === 'DEPOSIT' || transaction.type === 'TRANSFER_IN';
                            const amountPrefix = isCredit ? '+' : '-';
                            const amountClass = isCredit ? 'text-success' : 'text-danger';

                            return `
                                <tr>
                                    <td>
                                        <small>
                                            ${new Date(transaction.date).toLocaleDateString('pt-BR', {
                                                day: '2-digit',
                                                month: '2-digit',
                                                year: 'numeric'
                                            })}<br>
                                            ${new Date(transaction.date).toLocaleTimeString('pt-BR', {
                                                hour: '2-digit',
                                                minute: '2-digit'
                                            })}
                                        </small>
                                    </td>
                                    <td>
                                        <strong>${transaction.description}</strong>
                                        ${transaction.destinationAccount ? `<br><small class="text-muted">Para: ${transaction.destinationAccount}</small>` : ''}
                                    </td>
                                    <td><small class="font-monospace">${transaction.reference}</small></td>
                                    <td>
                                        <strong class="${amountClass}">
                                            ${amountPrefix} R$ ${formatCurrency(transaction.amount)}
                                        </strong>
                                    </td>
                                    <td>R$ ${formatCurrency(transaction.balance)}</td>
                                    <td>
                                        <span class="badge bg-success">
                                            <i class="fas fa-check me-1"></i>${getStatusText(transaction.status)}
                                        </span>
                                    </td>
                                </tr>
                            `;
                        }).join('')}
                    </tbody>
                </table>
            </div>
        `;
    }, 1500);
}

function filterTransactions() {
    // Implementar filtros de transação
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;
    const transactionType = document.getElementById('transactionTypeFilter').value;

    if (!startDate || !endDate) {
        alert('Por favor, selecione as datas inicial e final para filtrar.');
        return;
    }

    if (new Date(startDate) > new Date(endDate)) {
        alert('A data inicial não pode ser maior que a data final.');
        return;
    }

    // Recarregar transações com filtros aplicados
    loadStatementTransactions();
}

function exportStatement() {
    // Simular exportação de PDF
    const startDate = document.getElementById('startDate').value;
    const endDate = document.getElementById('endDate').value;

    if (!startDate || !endDate) {
        alert('Por favor, selecione o período para exportar o extrato.');
        return;
    }

    // Mostrar indicador de download
    const btn = event.target;
    const originalText = btn.innerHTML;
    btn.innerHTML = '<i class="fas fa-spinner fa-spin me-2"></i>Gerando PDF...';
    btn.disabled = true;

    setTimeout(() => {
        alert(`Extrato do período de ${new Date(startDate).toLocaleDateString('pt-BR')} a ${new Date(endDate).toLocaleDateString('pt-BR')} foi gerado com sucesso!\n\nEm um sistema real, o download iniciaria automaticamente.`);

        // Restaurar botão
        btn.innerHTML = originalText;
        btn.disabled = false;
    }, 2000);
}

// Event listeners para atualizar saldos quando modais são abertos
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

    // Atualizar saldos quando modais são abertos
    document.getElementById('transferModal').addEventListener('show.bs.modal', function() {
        const userData = JSON.parse(localStorage.getItem('userData'));
        if (userData && userData.account) {
            updateModalBalances(userData.account.balance);
        }
    });

    document.getElementById('withdrawModal').addEventListener('show.bs.modal', function() {
        const userData = JSON.parse(localStorage.getItem('userData'));
        if (userData && userData.account) {
            updateModalBalances(userData.account.balance);
        }
    });
});
