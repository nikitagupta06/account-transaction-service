package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.TransactionFailedException;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  public void transfer(String fromId, String toId, BigDecimal amount) throws TransactionFailedException {
    this.accountsRepository.transfer(fromId, toId, amount);
  }

}
