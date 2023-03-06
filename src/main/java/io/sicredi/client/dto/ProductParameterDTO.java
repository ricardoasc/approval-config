package io.sicredi.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductParameterDTO {
    private Number minRate;
    private Number maxRate;
    private Number minProductRate;
    private Number maxProductRate;
    private String productId;
    private String organization;
    private Number minInvestmentAmount;
    private Number minTransactionAmount;
}
