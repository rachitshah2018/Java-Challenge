package com.db.awmd.challenge.domain;

import java.math.BigDecimal;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

/**
 * @author Rachit Shah
 * 
 * Payment bean for holding information for transferMoney operation
 *
 */
@Data
public class Payment {
	
	@NotNull
	@NotEmpty
	private final String accountFromId;
	  
	@NotNull
	@NotEmpty
	private final String accountToId;
	
	@NotNull
	@Min(value = 1, message = "Amount to transfer must be positive.")
    private final BigDecimal transferAmount;

}
