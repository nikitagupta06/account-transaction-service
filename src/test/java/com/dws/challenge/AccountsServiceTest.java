package com.dws.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

import java.math.BigDecimal;

import com.dws.challenge.domain.Account;
import com.dws.challenge.exception.DuplicateAccountIdException;
import com.dws.challenge.exception.TransactionFailedException;
import com.dws.challenge.service.AccountsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Test
  void addAccount() {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  void addAccount_failsOnDuplicateId() {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }
  }

  @Test
  void TransactionFailed_negativeAmount() {
    Account fromAccount = new Account("123");
    fromAccount.setBalance(new BigDecimal(1000));
    Account toAccount = new Account("124");
    fromAccount.setBalance(new BigDecimal(1500));
    BigDecimal amount = new BigDecimal("-100");

    try {
      this.accountsService.transfer(fromAccount.getAccountId(), toAccount.getAccountId(), amount);
    } catch (TransactionFailedException ex) {
      assertThat(ex.getMessage()).isEqualTo("Amount can not be negative!");
    }

  }
  @Test
  void transactionFailedException_insufficientBalance() {
    Account fromAccount = new Account("123");
    fromAccount.setBalance(new BigDecimal(1000));
    Account toAccount = new Account("124");
    fromAccount.setBalance(new BigDecimal(1500));
    BigDecimal amount = new BigDecimal("1001");

    try {
      this.accountsService.transfer(fromAccount.getAccountId(), toAccount.getAccountId(), amount);
    } catch (TransactionFailedException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + fromAccount.getAccountId() +
              " has low Balance");
    }

  }
}
