package io.ciphernance.identity.application.query.account.status;

import io.ciphernance.identity.application.exception.account.AccountNotFoundException;
import io.ciphernance.identity.application.mediator.QueryHandler;
import io.ciphernance.identity.domain.model.Account;
import io.ciphernance.identity.domain.port.AccountRepositoryPort;

public class GetAccountStatusHandler implements QueryHandler<GetAccountStatusQuery, GetAccountResponse> {

    private final AccountRepositoryPort accountRepository;

    public GetAccountStatusHandler(AccountRepositoryPort accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public GetAccountResponse handle(GetAccountStatusQuery query) {

        Account account = accountRepository.findById(query.accountId())
                .orElseThrow(() -> new AccountNotFoundException(query.accountId()));

        return GetAccountResponse.from(account);
    }
}
