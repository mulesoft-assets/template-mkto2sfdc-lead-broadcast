/**
 * Mule Anypoint Template
 * Copyright (c) MuleSoft, Inc.
 * All rights reserved.  http://www.mulesoft.com
 */

package org.mule.templates.integration;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mule.MessageExchangePattern;
import org.mule.api.MuleEvent;
import org.mule.api.MuleException;
import org.mule.context.notification.NotificationException;
import org.mule.modules.marketo.model.lead.CreateOrUpdateLead;
import org.mule.processor.chain.SubflowInterceptingChainLifecycleWrapper;
import org.mule.templates.test.utils.ListenerProbe;

import com.mulesoft.module.batch.BatchTestHelper;

/**
 * The objective of this class is to validate the correct behavior of the
 * Anypoint Template that make calls to external systems.
 * 
 */
public class BusinessLogicIT extends AbstractTemplateTestCase {

	private BatchTestHelper helper;
	private List<Map<String, Object>> createdLeadsInMarketo = new ArrayList<Map<String, Object>>();

	private SubflowInterceptingChainLifecycleWrapper createLeadInMarketoFlow;
	private SubflowInterceptingChainLifecycleWrapper retrieveLeadFromSalesforceBFlow;
	private SubflowInterceptingChainLifecycleWrapper deleteLeadFromMarketoFlow;
	private SubflowInterceptingChainLifecycleWrapper deleteLeadFromSalesforceBFlow;

	@BeforeClass
	public static void init() {
		// Set the frequency between polls to 10 seconds
		System.setProperty("polling.frequency", "10000");

		// Set the poll starting delay to 20 seconds
		System.setProperty("polling.startDelayMillis", "20000");
		System.setProperty(
				"watermark.default.expression",
				"#[groovy: new Date(System.currentTimeMillis() - 10000).format(\"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'\", TimeZone.getTimeZone('UTC'))]");
		System.setProperty("page.size", "200");
	}

	@Before
	public void setUp() throws Exception {
		stopFlowSchedulers(POLL_FLOW_NAME);
		registerListeners();

		helper = new BatchTestHelper(muleContext);

		// Flow to retrieve leads from target system after syncing
		createLeadInMarketoFlow = getSubFlow("createLeadInMarketoFlow");
		createLeadInMarketoFlow.initialise();
		
		retrieveLeadFromSalesforceBFlow = getSubFlow("retrieveLeadFromSalesforceBFlow");
		retrieveLeadFromSalesforceBFlow.initialise();

		// Delete the created leads in Marketo
		deleteLeadFromMarketoFlow = getSubFlow("deleteLeadFromMarketoFlow");
		deleteLeadFromMarketoFlow.initialise();

		// Delete the created leads in SFDC B
		deleteLeadFromSalesforceBFlow = getSubFlow("deleteLeadFromSalesforceBFlow");
		deleteLeadFromSalesforceBFlow.initialise();

		createEntities();
	}

	@After
	public void tearDown() throws Exception {
		stopFlowSchedulers(POLL_FLOW_NAME);
		deleteEntities(createdLeadsInMarketo);
	}

	@Test
	public void testMainFlow() throws Exception {
		// Run poll and wait for it to run
		runSchedulersOnce(POLL_FLOW_NAME);
		waitForPollToRun();

		// Wait for the batch job executed by the poll flow to finish
		helper.awaitJobTermination(TIMEOUT_SEC * 1000, 500);
		helper.assertJobWasSuccessful();

		
		Thread.sleep(20000);
		// Assert Lead was sync to target system
		Map<String, Object> payload = invokeRetrieveFlow(
				retrieveLeadFromSalesforceBFlow, createdLeadsInMarketo.get(0));
		assertEquals("The lead should have been sync", createdLeadsInMarketo.get(0)
				.get("email"), payload.get("Email"));
	}

	private void registerListeners() throws NotificationException {
		muleContext.registerListener(pipelineListener);
	}

	private void waitForPollToRun() {
		pollProber.check(new ListenerProbe(pipelineListener));
	}

	@SuppressWarnings("unchecked")
	private void createEntities() throws MuleException, Exception {

		Map<String, Object> testLead = new HashMap<>();
		testLead.put("email", buildUniqueName(TEMPLATE_NAME, "") + "@test.com");
		testLead.put("firstName", "FirstNameTest");
		testLead.put("lastName", "LastNameTest");

		createdLeadsInMarketo.add(testLead);

		final MuleEvent event = createLeadInMarketoFlow.process(getTestEvent(
				createdLeadsInMarketo, MessageExchangePattern.REQUEST_RESPONSE));
		final List<CreateOrUpdateLead> results = (List<CreateOrUpdateLead>) event
				.getMessage().getPayload();
		int i = 0;
		for (CreateOrUpdateLead result : results) {
			Map<String, Object> leadInA = createdLeadsInMarketo.get(i);
			leadInA.put("id", result.getId());
			i++;
		}
	}

	private void deleteEntities(List<Map<String, Object>> leads)
			throws MuleException, Exception {
		
		deleteLeadFromMarketoFlow.process(getTestEvent(leads,
				MessageExchangePattern.REQUEST_RESPONSE));

		final List<Object> idList = new ArrayList<>(leads.size());	
		for (final Map<String, Object> createdLead : leads) {
			final Map<String, Object> lead = invokeRetrieveFlow(
					retrieveLeadFromSalesforceBFlow, createdLead);
			if (lead != null) {
				idList.add(lead.get("Id"));
			}
		}
		deleteLeadFromSalesforceBFlow.process(getTestEvent(idList,
				MessageExchangePattern.REQUEST_RESPONSE));
	}

}
