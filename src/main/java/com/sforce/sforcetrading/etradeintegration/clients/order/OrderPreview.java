package com.sforce.sforcetrading.etradeintegration.clients.order;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sforce.sforcetrading.etradeintegration.service.OrderPreviewService;

@Component
public class OrderPreview {

	@Autowired
	private OrderPreviewService orderPreviewService;

	public void previewOrder(String accountIdKey, Map<String, String> inputs) {
		try {
			String response = orderPreviewService.previewOrder(accountIdKey, inputs);
			Map<String, Object> responseMap = orderPreviewService.parseResponse(response);
			// handle the responseMap as needed, e.g., logging or further processing
		} catch (Exception e) {
			// handle exceptions appropriately
		}
	}

	public Map<String, String> getOrderDataMap() {
		return orderPreviewService.getOrderDataMap();
	}

	public void fillOrderActionMenu(int choice, Map<String, String> input) {
		orderPreviewService.fillOrderActionMenu(choice, input);
	}

	public void fillOrderPriceMenu(int choice, Map<String, String> input) {
		orderPreviewService.fillOrderPriceMenu(choice, input);
	}

	public void fillDurationMenu(int choice, Map<String, String> input) {
		orderPreviewService.fillDurationMenu(choice, input);
	}

}
