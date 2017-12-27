package com.db.awmd.challenge;

import java.math.BigDecimal;
import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Payment;
import com.db.awmd.challenge.exception.InSufficientBalanceException;
import com.db.awmd.challenge.exception.SenderIsTheReceiverException;
import com.db.awmd.challenge.service.AccountsService;
import com.db.awmd.challenge.service.NotificationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountServiceAmountTransferTest {

	@Autowired
	private AccountsService accountsService;

	private Account senderAccount;
	
	private Account receiverAccount;
	
	@Before
	public void setup(){
		  senderAccount = new Account("Id-123");
		  senderAccount.setBalance(new BigDecimal(1000));
		  accountsService.createAccount(senderAccount);
		  
		  receiverAccount = new Account("Id-456");
		  receiverAccount.setBalance(new BigDecimal(1000));
		  accountsService.createAccount(receiverAccount);
	}

	@Test
	public void transferAmount() throws InSufficientBalanceException, SenderIsTheReceiverException, InterruptedException {
		Payment payment = new Payment("Id-123", "Id-456", BigDecimal.valueOf(1000));
		
		//Spying the notification service to avoid sending real time notification while JUnit testing
		
		NotificationService spyNotification = Mockito.spy(NotificationService.class);
		Mockito.doNothing().when(spyNotification).notifyAboutTransfer(Mockito.anyObject(), Mockito.anyString());
		
		Assert.assertTrue(accountsService.transferMoney(payment));
	}
	
	@Test(expected=SenderIsTheReceiverException.class)
	public void transferAmount_senderReceiverAccountSame() throws InSufficientBalanceException, SenderIsTheReceiverException, InterruptedException {
		Payment payment = new Payment("Id-123", "Id-123", BigDecimal.valueOf(1000));
		Assert.assertTrue(accountsService.transferMoney(payment));
	}
	
	@Test(expected=InSufficientBalanceException.class)
	public void transferAmount_senderIsHavingInsufficientBalance() throws InSufficientBalanceException, SenderIsTheReceiverException, InterruptedException {
		Payment payment = new Payment("Id-123", "Id-456", BigDecimal.valueOf(2000));
		Assert.assertTrue(accountsService.transferMoney(payment));
	}

	@After
	public void teardown(){
		accountsService.getAccountsRepository().clearAccounts();
	}
}
