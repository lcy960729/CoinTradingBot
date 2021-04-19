package com.cy.tradingbot.services.wallet;


import com.cy.tradingbot.dao.ExternalAPI.UpBitAPI;
import com.cy.tradingbot.domain.Wallet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Hashtable;
import java.util.List;

@Service
public class GetWalletToUpBitService implements GetWalletService {
    @Autowired
    private UpBitAPI upbitAPI;

    public List<Wallet> getWallets() {
        return upbitAPI.getAccounts().orElseThrow(RuntimeException::new);
    }

    public Wallet getKrwWallet() {
        return getWallet("KRW");
    }

    public Wallet getCoinWallet(String walletName) {
        return getWallet(walletName);
    }

    public Wallet getWallet(String walletName) {
        return getWallets().stream()
                .filter(wallet -> wallet.getCurrency().equals(walletName))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }

    public Hashtable<String, Wallet> getWalletHashTable() {
        Hashtable<String, Wallet> walletHashtable = new Hashtable<>();

        for (Wallet wallet : getWallets()) {
            walletHashtable.put(wallet.getCurrency(), wallet);
        }

        return walletHashtable;
    }

}
