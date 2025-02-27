package br.com.nlw.events.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public record AISearchJsonOut(
    List <Map<String, Object>> result,
    Integer imputCountTokens,
    Integer outputCountTokens,
    BigDecimal costEstimate
) { }
