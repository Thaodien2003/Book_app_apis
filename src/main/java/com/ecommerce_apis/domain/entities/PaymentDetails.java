package com.ecommerce_apis.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class PaymentDetails {
	
	private String paymentMethod;

	private String status;

	private String paymentId;

	private String vnpayPaymentId;

	private String vnpayPaymentUrl;

	private String vnpayPaymentStatus;

}
