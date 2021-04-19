package com.cy.tradingbot.services.wallet;


import com.cy.tradingbot.domain.Wallet;

import java.util.Hashtable;
import java.util.List;


public interface GetWalletService {
    List<Wallet> getWallets();

    Wallet getKrwWallet();

    Wallet getCoinWallet(String walletName);

    Wallet getWallet(String walletName);

    Hashtable<String, Wallet> getWalletHashTable();

}
