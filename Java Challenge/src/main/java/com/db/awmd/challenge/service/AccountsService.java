package com.db.awmd.challenge.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.Payment;
import com.db.awmd.challenge.exception.InSufficientBalanceException;
import com.db.awmd.challenge.exception.SenderIsTheReceiverException;
import com.db.awmd.challenge.repository.AccountsRepository;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;


/**
 * @author Lenovo
 *
 */
/**
 * @author Lenovo
 *
 */
@Service
@Slf4j
public class AccountsService {

	public static final String RECEIVER_NOTIFICATION_MESSAGE = "Amount %s is from Account %s to your account";

	public static final String SENDER_NOTIFICATION_MESSAGE = "Amount %s is transfered to Account %s from your account";

	public static final String INSUFFICIENT_BALANCE_EXCEPTION_MESSAGE = "Your account is having insufficient balance";

	public static final String SENDER_IS_THE_RECEIVER_EXCEPTION_MESSAGE = "Sender account number %s and Receiver account number %s are same";

	@Getter
	private final NotificationService notificationService;

	@Getter
	private final AccountsRepository accountsRepository;

	@Autowired
	public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
		this.accountsRepository = accountsRepository;
		this.notificationService = notificationService;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	/**
	 * @param payment
	 * @return
	 * @throws InSufficientBalanceException
	 * @throws SenderIsTheReceiverException
	 * @throws InterruptedException
	 * 
	 * This method is created to transfer the amount from one account to another. 
	 * This checks initially if the sender is the receiver or if the balance is not sufficient and throws exception accordingly.
	 */
	public boolean transferMoney(Payment payment) throws InSufficientBalanceException,SenderIsTheReceiverException,InterruptedException {
		log.debug("Start of transfer money method in AccountService");

		Account fromAccount = this.accountsRepository.getAccount(payment.getAccountFromId());
		Account toAccount = this.accountsRepository.getAccount(payment.getAccountToId());
		
		final Random number = new Random(123L);

		if (payment.getAccountFromId().equals(payment.getAccountToId())) {
			throw new SenderIsTheReceiverException(String.format(SENDER_IS_THE_RECEIVER_EXCEPTION_MESSAGE,
					payment.getAccountFromId(), payment.getAccountToId()));
		}

		while (true) {
			if (fromAccount.getLock().tryLock()) {
				try {
					if (toAccount.getLock().tryLock()) {
						try {
							if (fromAccount.getBalance().compareTo(payment.getTransferAmount()) < 0) {
								throw new InSufficientBalanceException(INSUFFICIENT_BALANCE_EXCEPTION_MESSAGE);
							} else {
								updateBalance(payment, fromAccount, toAccount);
								sendNotification(payment, fromAccount, toAccount);
								return true;
							}
						} finally {
							toAccount.getLock().tryLock();
						}
					}
				} finally {
					fromAccount.getLock().unlock();
				}

			}

			long sleeptime = 1 + Math.abs(number.nextLong()); // Thread is provided with some delay i.e. 1 milisecond + random delay to prevent livelock situation 
			Thread.sleep(sleeptime);
		}
	}

	/**
	 * @param payment
	 * @param fromAccount
	 * @param toAccount
	 * 
	 * This is a private method called by transferMoney method to send notifications via notification service.
	 */
	private void sendNotification(Payment payment, Account fromAccount, Account toAccount) {
		notificationService.notifyAboutTransfer(fromAccount, String.format(SENDER_NOTIFICATION_MESSAGE,
				payment.getTransferAmount().toPlainString(), toAccount.getAccountId()));
		notificationService.notifyAboutTransfer(toAccount, String.format(RECEIVER_NOTIFICATION_MESSAGE,
				payment.getTransferAmount().toPlainString(), fromAccount.getAccountId()));
	}

	/**
	 * @param payment
	 * @param fromAccount
	 * @param toAccount
	 * 
	 * This is a private method called by transferMoney method to update the balance in sender and receiver's account
	 */
	private void updateBalance(Payment payment, Account fromAccount, Account toAccount) {
		fromAccount.setBalance(fromAccount.getBalance().subtract(payment.getTransferAmount()));
		toAccount.setBalance(toAccount.getBalance().add(payment.getTransferAmount()));
	}
}
