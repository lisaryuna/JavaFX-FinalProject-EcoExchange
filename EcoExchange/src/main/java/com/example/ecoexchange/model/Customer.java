package com.example.ecoexchange.model;

public class Customer extends User implements Levelable {
    private double balance;
    private double totalWeightHistory;
    private MembershipLevel membershipLevel;

    public Customer(int userID, String fullName, String username, String password, double balance, double totalWeightHistory, String levelStr) {
        super(userID, fullName, username, password, Role.CUSTOMER);
        this.balance = balance;
        this.totalWeightHistory = totalWeightHistory;

        try {
            this.membershipLevel = MembershipLevel.valueOf(levelStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.membershipLevel = MembershipLevel.BRONZE;
        }
    }

    @Override
    public void checkLevelStatus() {
        if (this.totalWeightHistory >= 50.0) {
            this.membershipLevel = MembershipLevel.GOLD;
        } else if (this.totalWeightHistory >= 20.0) {
            this.membershipLevel = MembershipLevel.SILVER;
        } else {
            this.membershipLevel = MembershipLevel.BRONZE;
        }
    }

    @Override
    public double getPriceBonus() {
        switch (this.membershipLevel) {
            case GOLD: return 500.0;
            case SILVER: return 200.0;
            default: return 0.0;
        }
    }

    public void addDeposit(double weight, double moneyEarned) {
        this.totalWeightHistory += weight;
        this.balance += moneyEarned;
        checkLevelStatus();
    }

    @Override
    public String getDashboardViewName() {
        return "customer_dashboard.fxml";
    }

    public double getBalance() {
        return balance;
    }
    public double getTotalWeightHistory() {
        return totalWeightHistory;
    }
    public MembershipLevel getMembershipLevel() {
        return membershipLevel;
    }

    public void setMembershipLevel(MembershipLevel membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    public void deductBalance(double amount) {
        if (amount > this.balance) {
            throw new IllegalArgumentException("Insufficient balance!");
        }
        this.balance -= amount;
    }
}
