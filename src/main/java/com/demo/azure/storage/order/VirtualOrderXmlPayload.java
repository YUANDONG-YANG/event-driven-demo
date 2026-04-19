package com.demo.azure.storage.order;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import java.math.BigDecimal;

/** XML shape written to disk (English element names). */
@JacksonXmlRootElement(localName = "VirtualOrder")
public class VirtualOrderXmlPayload {

    @JacksonXmlProperty(localName = "orderNo")
    private String orderNo;

    @JacksonXmlProperty(localName = "amount")
    private BigDecimal amount;

    @JacksonXmlProperty(localName = "userName")
    private String userName;

    @JacksonXmlProperty(localName = "phone")
    private String phone;

    @JacksonXmlProperty(localName = "orderDate")
    private String orderDate;

    @JacksonXmlProperty(localName = "paymentMethod")
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

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
