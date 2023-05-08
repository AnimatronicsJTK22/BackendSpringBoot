package com.animatronics.spring.security.mongodb.payload.request;

public class MoneyDisciplineRequest {
    private double deposit;
    private double withdraw;
    private String lastChangeDesc;
    public double getDeposit() {
        return deposit;
    }
    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }
    public double getWithdraw() {
        return withdraw;
    }
    public void setWithdraw(double withdraw) {
        this.withdraw = withdraw;
    }
    public String getLastChangeDesc() {
        return lastChangeDesc;
    }
    public void setLastChangeDesc(String lastChangeDesc) {
        this.lastChangeDesc = lastChangeDesc;
    }

}
