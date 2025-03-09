package br.com.nlw.events.dto;

import java.math.BigDecimal;

public record AISearchMarkDownOut(
    String result, Integer imputCountTokens, Integer outputCountTokens, BigDecimal costEstimate) {}
