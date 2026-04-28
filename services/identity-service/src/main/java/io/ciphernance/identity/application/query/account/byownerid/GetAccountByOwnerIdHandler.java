package io.ciphernance.identity.application.query.account.byownerid;

import io.ciphernance.identity.application.exception.account.AccountNotFoundException;
import io.ciphernance.identity.application.mediator.QueryHandler;
import io.ciphernance.identity.application.query.account.GetAccountResponse;
import io.ciphernance.identity.domain.model.Account;
import io.ciphernance.identity.domain.port.AccountRepositoryPort;
import org.springframework.stereotype.Component;

@Component
public class GetAccountByOwnerIdHandler implements QueryHandler<GetAccountByOwnerIdQuery, GetAccountResponse> {

    private final AccountRepositoryPort accountRepository;

    public GetAccountByOwnerIdHandler(AccountRepositoryPort accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public GetAccountResponse handle(GetAccountByOwnerIdQuery query) {

        Account account = accountRepository.findByOwnerId(query.ownerId())
                .orElseThrow(() -> AccountNotFoundException.forOwner(query.ownerId()));

        return GetAccountResponse.from(account);
    }
}
