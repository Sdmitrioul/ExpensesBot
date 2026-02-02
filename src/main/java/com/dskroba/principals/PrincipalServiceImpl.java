package com.dskroba.principals;

import com.dskroba.telegram.TelegramId;
import com.dskroba.type.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.dskroba.principals.Principal.NONEXISTING_TABLE;

@Service
public class PrincipalServiceImpl implements PrincipalService {
    private final PrincipalRepository principalRepository;

    @Autowired
    public PrincipalServiceImpl(PrincipalRepository principalRepository) {
        this.principalRepository = principalRepository;
    }

    @Override
    public Principal authenticate(TelegramId issuer) {
        Principal savedPrincipal = principalRepository.getByTelegramId(issuer.telegramId());
        if (savedPrincipal == null) {
            Principal newPrincipal = Principal.builder(issuer)
                    .context(List.of(), Currency.parseValue("EUR"), NONEXISTING_TABLE)
                    .build();
            return savePrincipal(newPrincipal);
        }
        if (issuer.hasSameMetadata(savedPrincipal.key())) {
            Principal updatedPrincipal = savedPrincipal.toBuilder()
                    .telegramId(issuer)
                    .build();
            return savePrincipal(updatedPrincipal);
        }
        return savedPrincipal;
    }

    @Override
    public Principal savePrincipal(Principal principal) {
        return principalRepository.savePrincipal(principal);
    }

    @Override
    public List<Principal> getAllPrincipals() {
        return principalRepository.getPrincipals();
    }
}
