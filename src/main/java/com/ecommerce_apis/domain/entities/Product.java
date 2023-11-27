package com.ecommerce_apis.domain.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Product {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	private String title;
	
	private String description;
	
	private int price;
	
	@Column(name = "discounted_price")
	private int discountedPrice;
	
	@Column(name = "discount_persent")
	private int discountPersent;
	
	@Column(name = "quantity")
	private int quantity;
	
	@Column(name = "brand")
	private String brand;
	
	@Column(name = "color")
	private String color;
	
	@Embedded
	@ElementCollection
	@Column(name = "sizes")
	private Set<Size> sizes = new HashSet<>();
	
	@Column(name = "image_url")
	private String imageUrl;

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Rating> ratings = new ArrayList<>();

	@OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	private List<Review> reviews = new ArrayList<>();
	
	@Column(name = "num_ratings")
	private int numRatings;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private Category category;

	@ManyToOne
	@JoinColumn(name = "seller_id", nullable = false)
	private User seller;

	private boolean deleted;
	
	private LocalDateTime createdAt;

	private LocalDateTime updateAt;
}
