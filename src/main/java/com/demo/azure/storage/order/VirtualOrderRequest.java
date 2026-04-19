package com.demo.azure.storage.order;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Virtual order create request")
public class VirtualOrderRequest {

    @Schema(description = "Order number; optional—generated if omitted", example = "ORD-20260418-0001")
    @Size(max = 64)
    private String orderNo;

    @NotNull
    @DecimalMin(value = "0.01", message = "amount must be greater than zero")
    @Schema(description = "Amount", example = "199.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal amount;

    @NotBlank
    @Size(max = 64)
    @Schema(description = "Customer name", example = "Jane Doe", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @NotBlank
    @Pattern(regexp = "^[+0-9][0-9\\s\\-().]{6,24}$", message = "invalid phone number format")
    @Schema(description = "Phone number", example = "+1-555-0100", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @Schema(description = "Order date; defaults to today if omitted", example = "2026-04-18")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate orderDate;

    @NotBlank
    @Size(max = 32)
    @Schema(description = "Payment method", example = "CreditCard", requiredMode = Schema.RequiredMode.REQUIRED)
    private String paymentMethod;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDate orderDate) {
        this.orderDate = orderDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
