package com.db.awmd.challenge;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import com.db.awmd.challenge.service.AccountsService;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerAmountTransferTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void prepareMockMvc() throws Exception {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();
    
  }

  @Test
  public void transferMoney() throws Exception {

	  this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
    	      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

	  this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
  	      .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());

	  this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-456\",\"transferAmount\":1000}")).andExpect(status().isOk());
    
  }

  @Test
  public void transferMoney_insufficientBalance() throws Exception {
	  this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
    	      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

	  this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
  	      .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());

	  this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-456\",\"transferAmount\":5000}")).andExpect(status().isPreconditionFailed());
  }
  
  @Test
  public void transferMoney_senderReceiverSame() throws Exception {
	  this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
    	      .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

	  this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
  	      .content("{\"accountId\":\"Id-456\",\"balance\":1000}")).andExpect(status().isCreated());

	  this.mockMvc.perform(post("/v1/accounts/transferMoney").contentType(MediaType.APPLICATION_JSON)
      .content("{\"accountFromId\":\"Id-123\",\"accountToId\":\"Id-123\",\"transferAmount\":1000}")).andExpect(status().isBadRequest());
  }

  @After
	public void teardown(){
			accountsService.getAccountsRepository().clearAccounts();
  }
}
