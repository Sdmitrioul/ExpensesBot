package com.dskroba.principals;

import com.dskroba.type.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrincipalServiceImpl implements PrincipalService {
    private final PrincipalRepository principalRepository;

    @Autowired
    public PrincipalServiceImpl(PrincipalRepository principalRepository) {
        this.principalRepository = principalRepository;
    }

    @Override
    public Principal authenticate(Principal principal) {
        Principal savedPrincipal = principalRepository.getByTelegramId(principal.key().telegramId());
        if (savedPrincipal == null) {
            return savePrincipal(Principal.Builder
                    .from(principal)
                    .currency(Currency.EUR)
                    .build());
        }
        Principal combinedPrincipal = Principal.Builder.from(savedPrincipal)
                .merge(principal)
                .build();
        if (combinedPrincipal.isDifferent(savedPrincipal)) {
            return savePrincipal(combinedPrincipal);
        }
        return combinedPrincipal;
    }

    @Override
    public Principal savePrincipal(Principal principal) {
        return principalRepository.savePrincipal(principal);
    }

    @Override
    public boolean isAuthorized(Principal principal) {
        return principal.isResolved();
    }

    @Override
    public List<Principal> getAllPrincipals() {
        return principalRepository.getPrincipals();
    }
}
