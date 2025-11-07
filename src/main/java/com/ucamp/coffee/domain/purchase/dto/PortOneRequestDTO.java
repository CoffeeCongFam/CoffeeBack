package com.ucamp.coffee.domain.purchase.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PortOneRequestDTO {

	private Long purchaseId;
	private String impUid;
	private String merchantUid;
}
