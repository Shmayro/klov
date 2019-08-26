package com.aventstack.klov.domain;

public enum Feature {
	QuickSanity("Quick Sanity", "QuickSanity_JU5"),
	Aggregation("Aggregation","SanityCheck_01_Aggregation_JU5"),
	ClientRisk("Client Risk","SanityCheck_04_ClientRisk_JU5"),
	DealerIntervention("Dealer Intervention","SanityCheck_05_DealerIntervention_JU5"),
	DeskCollaboration("Desk Collaboration","SanityCheck_08_DeskCollaboration_JU5"),
	InstitutionalTrading("Institutional Trading","SanityCheck_02_InstitutionalTrading_JU5"),
	InstitutionalTradingCCY("Institutional Trading CCY","SanityCheck_02_InstitutionalTradingMarginCCY_JU5"),
	InstitutionalTradingCCYPAIR("Institutional Trading CCY PAIR","SanityCheck_02_InstitutionalTradingMarginCCyPair_JU5"),
	InternalRisk("Internal Risk","SanityCheck_06_InternalRisk_JU5"),
	OMS("OMS","SanityCheck_07_OMS_JU5"),
	Pricing("Pricing","SanityCheck_10_Pricing_JU5"),
	RetailTrading("Retail Trading","SanityCheck_09_RetailTrading_JU5"),
	SalesNegotiation("Sales Negotiation","SanityCheck_11_SalesNegotiation_JU5"),
	VoiceTrading("Voice Trading","SanityCheck_03_VoiceTrading_JU5"),
	MarginByCcyTestSuite("CCY Margin","MarginByCcyTestSuite"),
	MarginByCcyPairTestSuite("CCY Pair Margin","MarginByCcyPairTestSuite");

	private String title;
	private String queryName;

	Feature(String title, String queryName) {
		this.title=title;
		this.queryName=queryName;
	}

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @return the queryName
	 */
	public String getQueryName() {
		return queryName;
	}
}
