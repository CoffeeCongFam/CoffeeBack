package com.ucamp.coffee.domain.purchase.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortOneTempResponseDTO {

	Long purchaseId;
	String merchantUid;
	
}
