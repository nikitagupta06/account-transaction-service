package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.InsufficientBalanceException;
import com.dws.challenge.exception.NegativeAmountException;
import com.dws.challenge.service.EmailNotificationService;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class AccountsRepositoryInMemory extends Thread implements AccountsRepository {

    private final Map<String, Account> accounts = new ConcurrentHashMap<>();

    private EmailNotificationService emailNotificationService;

    public AccountsRepositoryInMemory(EmailNotificationService emailNotificationService) {
        this.emailNotificationService = emailNotificationService;
    }

    @Override
    public void createAccount(Account account) throws DuplicateAccountIdException {
        Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
        if (previousAccount != null) {
            throw new DuplicateAccountIdException(
                    "Account id " + account.getAccountId() + " already exists!");
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
    public void transfer(String accountIdFrom, String accountIdTo, BigDecimal amount)
            throws InsufficientBalanceException, NegativeAmountException {

        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new NegativeAmountException("Amount can not be negative!");
        }
        Account fromAccount = accounts.get(accountIdFrom);
        Account toAccount = accounts.get(accountIdTo);

        if (amount.compareTo(fromAccount.getBalance()) == 1) {
            throw new InsufficientBalanceException("Account id " + accountIdFrom +
                    " has low Balance");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        String fromAccountNotification =  "Sent " + amount + " to account ID : " + accountIdTo;
        String toAccountNotification = "Received " + amount + " from account ID : " + accountIdFrom;

        emailNotificationService.notifyAboutTransfer(fromAccount, fromAccountNotification);
        emailNotificationService.notifyAboutTransfer(toAccount, toAccountNotification);

    }



}
