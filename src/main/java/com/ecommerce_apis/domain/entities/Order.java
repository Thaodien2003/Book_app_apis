package com.ecommerce_apis.domain.entities;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "order_table")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@Column(name = "order_id")
	private String orderId = new Date().getTime() + "-" + new SecureRandom().nextInt(1000);
	
	@ManyToOne
	private User user;

	@ManyToOne
	private User shipper;
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> orderItems = new ArrayList<>();
	
	private LocalDateTime orderDate;

	private LocalDateTime placedOrderDate;

	private LocalDateTime shippedOrderDate;

	private LocalDateTime successDeliveryDate;
	
	@OneToOne
	private Address shippingAddress;
	
	@Embedded
	private PaymentDetails paymentDetails = new PaymentDetails();
	
	private double totalPrice;
	
	private Integer totalDiscountedPrice;
	
	private Integer discounte;
	
	private String orderStatus;
	
	private int totalItem;

	private boolean deleted;
	
	private LocalDateTime ceatedAt;

}
