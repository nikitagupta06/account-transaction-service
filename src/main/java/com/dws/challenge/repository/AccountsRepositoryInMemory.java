package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.Transaction;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.TransactionFailedException;
import com.dws.challenge.service.EmailNotificationService;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountsRepositoryInMemory implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    private final EmailNotificationService emailNotificationService;

    public AccountsRepositoryInMemory(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException("Account id " + account.getAccountId() + " already exists!");
        }
    }

    @Override
    public Account getAccount(String accountId) {
        return accounts.get(accountId);
    }

    @Override
    public void clearAccounts() {
        accounts.clear();
    }

    @Override
    public void transfer(Transaction transaction) throws TransactionFailedException {

        String fromAccountId = transaction.getFromAccountId();
        String toAccountId = transaction.getToAccountId();
        BigDecimal amount = transaction.getAmount();

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new TransactionFailedException("Amount can not be negative!");
        }

        if (amount.compareTo(accounts.get(fromAccountId).getBalance()) == 1) {
            throw new TransactionFailedException("Account id " + fromAccountId + " has low Balance");
        }

        new Thread(() -> {
            accounts.get(fromAccountId).setBalance(accounts.get(fromAccountId).getBalance().subtract(amount));
            accounts.get(toAccountId).setBalance(accounts.get(toAccountId).getBalance().add(amount));

            emailNotificationService.notifyAboutTransfer(accounts.get(fromAccountId), "Sent " + amount + " to account ID : " + toAccountId);
            emailNotificationService.notifyAboutTransfer(accounts.get(toAccountId), "Received " + amount + " from account ID : " + fromAccountId);
        }).start();
    }


}
