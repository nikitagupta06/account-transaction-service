package com.dws.challenge.repository;

import com.dws.challenge.domain.Account;
import com.dws.challenge.domain.Transaction;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.TransactionFailedException;

public interface AccountsRepository {

    void createAccount(Account account) throws DuplicateAccountIdException;

    Account getAccount(String accountId);

    void clearAccounts();

    void transfer(Transaction transaction) throws TransactionFailedException;

}
