package com.sforce.sforcetrading.etradeintegration.clients.order;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.sforce.sforcetrading.etradeintegration.clients.Client;
import com.sforce.sforcetrading.etradeintegration.exception.ApiException;
import com.sforce.sforcetrading.etradeintegration.oauth.AppController;
import com.sforce.sforcetrading.etradeintegration.oauth.model.ApiResource;
import com.sforce.sforcetrading.etradeintegration.oauth.model.ContentType;
import com.sforce.sforcetrading.etradeintegration.oauth.model.Message;
import com.sforce.sforcetrading.etradeintegration.oauth.model.OauthRequired;

@Component
public class OrderClient extends Client {

	@Autowired
	private AppController oauthManager;

	@Autowired
	private ApiResource apiResource;

	private Map<String, String> apiProperties;

	@Override
	public String getHttpMethod() {
		return "GET";
	}

	@Override
	public String getQueryParam() {
		return null;
	}

	@Override
	public String getURL(String accountIdKey) {
		return String.format("%s%s/orders", getURL(), accountIdKey);
	}

	@Override
	public String getURL() {
		return String.format("%s%s", apiResource.getApiBaseUrl(), apiResource.getOrderListUri());
	}

	public void setApiProperties(Map<String, String> apiProperties) {
		this.apiProperties = apiProperties;
	}

	public String getOrders(final String accountIdKey) throws ApiException {
		log.debug("Calling OrderList API " + getURL(accountIdKey));

		Message message = new Message();
		message.setOauthRequired(OauthRequired.YES);
		message.setHttpMethod(getHttpMethod());
		message.setUrl(getURL(accountIdKey));
		message.setContentType(ContentType.APPLICATION_JSON);

		return oauthManager.invoke(message);
	}

	public List<OrderDetail> parseResponse(final String response) throws Exception {
		List<OrderDetail> ordersList = new ArrayList<>();

		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(response);

		JSONObject orderResponse = (JSONObject) jsonObject.get("OrdersResponse");
		JSONArray orderData = null;

		if (jsonObject.get("OrdersResponse") != null) {
			orderData = (JSONArray) orderResponse.get("Order");
			for (Object orderObj : orderData) {
				JSONObject order = (JSONObject) orderObj;
				JSONObject orderDetail = (JSONObject) ((JSONArray) order.get("OrderDetail")).get(0);
				JSONArray orderInstArr = (JSONArray) orderDetail.get("Instrument");

				for (Object instObj : orderInstArr) {
					JSONObject instrument = (JSONObject) instObj;
					JSONObject product = (JSONObject) instrument.get("Product");

					OrderDetail orderDetailObj = new OrderDetail();
					orderDetailObj.setDate(OrderUtil.convertLongToDate((Long) orderDetail.get("placedTime")));
					orderDetailObj.setOrderId(order.get("orderId").toString());
					orderDetailObj.setType(order.get("orderType").toString());
					orderDetailObj.setAction(instrument.get("orderAction").toString());
					orderDetailObj.setQty(instrument.get("orderedQuantity").toString());
					orderDetailObj.setSymbol(product.get("symbol").toString());
					orderDetailObj.setPriceType(
							PriceType.getPriceType(String.valueOf(orderDetail.get("priceType"))).getValue());
					orderDetailObj.setTerm(
							OrderUtil.getTerm(OrderTerm.getOrderTerm(String.valueOf(orderDetail.get("orderTerm")))));
					orderDetailObj.setPrice(OrderUtil.getPrice(
							PriceType.getPriceType(String.valueOf(orderDetail.get("priceType"))), orderDetail));
					orderDetailObj.setExecuted(instrument.containsKey("averageExecutionPrice")
							? instrument.get("averageExecutionPrice").toString()
							: "-");
					orderDetailObj.setStatus(orderDetail.get("status").toString());

					ordersList.add(orderDetailObj);
				}
			}
		}

		return ordersList;
	}
}
